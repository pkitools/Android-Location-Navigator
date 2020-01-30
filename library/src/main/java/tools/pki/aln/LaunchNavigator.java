package tools.pki.aln;


import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class LaunchNavigator {
    public LaunchNavigator() {
    }

    /**********************
     * private static properties
     **********************/
    private static final String LOG_TAG = "LaunchNavigator";
    private static final String NO_APP_FOUND = "No Activity found to handle Intent";
    private static final String TAG = LaunchNavigator.class.getSimpleName();
    private final String MAPS_PROTOCOL = "http://maps.google.com/maps?";

    // Explicitly supported apps
    private static final String GEO = "GEO"; // Use native app choose for GEO: intent
    public static final String GOOGLE_MAPS = "google_maps";
    public static final String CITYMAPPER = "citymapper";
    public static final String UBER = "uber";
    public static final String WAZE = "waze";
    public static final String YANDEX = "yandex";
    public static final String SYGIC = "sygic";
    public static final String HERE_MAPS = "here_maps";
    public static final String MOOVIT = "moovit";
    public static final String LYFT = "lyft";
    public static final String MAPS_ME = "maps_me";
    public static final String CABIFY = "cabify";
    public static final String BAIDU = "baidu";
    public static final String TAXIS_99 = "taxis_99";
    public static final String GAODE = "gaode";

    private final Map<String, String> supportedAppPackages = Collections.unmodifiableMap(new HashMap<String, String>() {
        {
            put(GOOGLE_MAPS, "com.google.android.apps.maps");
            put(CITYMAPPER, "com.citymapper.app.release");
            put(UBER, "com.ubercab");
            put(WAZE, "com.waze");
            put(YANDEX, "ru.yandex.yandexnavi");
            put(SYGIC, "com.sygic.aura");
            put(HERE_MAPS, "com.here.app.maps");
            put(MOOVIT, "com.tranzmate");
            put(LYFT, "me.lyft.android");
            put(MAPS_ME, "com.mapswithme.maps.pro");
            put(CABIFY, "com.cabify.rider");
            put(BAIDU, "com.baidu.BaiduMap");
            put(TAXIS_99, "com.taxis99");
            put(GAODE, "com.autonavi.minimap");
        }
    });


    private final Map<String, String> supportedAppNames = Collections.unmodifiableMap(new HashMap<String, String>() {
        {
            put(GOOGLE_MAPS, "Google Maps");
            put(CITYMAPPER, "Citymapper");
            put(UBER, "Uber");
            put(WAZE, "Waze");
            put(YANDEX, "Yandex Navigator");
            put(SYGIC, "Sygic");
            put(HERE_MAPS, "HERE Maps");
            put(MOOVIT, "Moovit");
            put(LYFT, "Lyft");
            put(MAPS_ME, "MAPS.ME");
            put(CABIFY, "Cabify");
            put(BAIDU, "Baidu Maps");
            put(TAXIS_99, "99 Taxi");
            put(GAODE, "Gaode Maps (Amap)");
        }
    });

    private final String GEO_URI = "GEO:";

    /**********************
     * Internal properties
     **********************/

    public LaunchNavigator withGeoCoding(){
        this.geocodingEnabled = true;
        return this;
    }

    public LaunchNavigator withoutGeoCoding(){
        this.geocodingEnabled = false;
        return this;
    }
    private boolean geocodingEnabled = false;

    public LaunchNavigator withPackageManager(PackageManager packageManager){
        this.packageManager = packageManager;
        return this;
    }

    private PackageManager packageManager;

    Context context;

    private OkHttpClient httpClient = new OkHttpClient();



    private ILogger logger;


    // Map of app name to package name
    private Map<String, String> availableApps;



    String googleApiKey = null;

    /**
     * Set GoogleApi Key if you are using places API
     * @param googleApiKey
     * @return
     */

    public LaunchNavigator apiKey(String googleApiKey){
        this.googleApiKey = googleApiKey;
        return this;
    }


    /*******************
     * Constructors
     *******************/

    public LaunchNavigator(Context context, ILogger logger) {
        this(context, logger, false);
    }


    private LaunchNavigator(Context context) {
        this(context, new AndroidLogger(), false);
    }

    public LaunchNavigator(Context context, ILogger logger, boolean geocodingEnabled) {
        this.geocodingEnabled = geocodingEnabled;
        this.logger =(logger);
        initialize(context);
    }


    /*******************
     * Public API
     ******************
     **/
    public Map<String, String> getGeoApps() {
        HashMap<String, String> apps = new HashMap<>();

        // Dynamically populate from discovered available apps that support GEO: protocol
        for (Map.Entry<String, String> entry : availableApps.entrySet()) {
            String appName = entry.getKey();
            String packageName = entry.getValue();
            // If it's not already an explicitly supported app
            if (!supportedAppPackages.containsValue(packageName)) {
                apps.put(appName, packageName);
            }
        }
        return apps;
    }

    List<String> supportedApps;

    public Map<String, Boolean> getAvailableApps() {
        Map<String, Boolean> apps = new HashMap<>();

        // Add explicitly supported apps first
        for (Map.Entry<String, String> entry : supportedAppPackages.entrySet()) {
            String appName = entry.getKey();
            String packageName = entry.getValue();
            apps.put(appName, availableApps.containsValue(packageName));
        }

        // Iterate over available apps and add any dynamically discovered ones
        for (Map.Entry<String, String> entry : availableApps.entrySet()) {
            String packageName = entry.getValue();
            // If it's not already present
            if (!apps.containsKey(packageName) && !supportedAppPackages.containsValue(packageName)) {
                apps.put(packageName, true);
            }
        }
        return apps;
    }

    public List<String> getSupportedApps() {
        Map<String, Boolean> apps = new HashMap<>();
        ArrayList list = new ArrayList();
        // Add explicitly supported apps first
        for (Map.Entry<String, String> entry : supportedAppPackages.entrySet()) {
            String appName = entry.getKey();
            String packageName = entry.getValue();
            apps.put(appName, availableApps.containsValue(packageName));
        }

        // Iterate over available apps and add any dynamically discovered ones
        for (Map.Entry<String, String> entry : availableApps.entrySet()) {
            String packageName = entry.getValue();
            // If it's not already present
            if (!apps.containsKey(packageName) && !supportedAppPackages.containsValue(packageName)) {
                list.add(packageName);
            }
        }
        return list;
    }

    public boolean isAppAvailable(String appName) {
        if (supportedAppPackages.containsKey(appName)) {
            appName = supportedAppPackages.get(appName);
        }
        return availableApps.containsValue(appName);
    }

    /**
     * @param params
     * @return errors
     * @
     */
    public String navigate(NavigationParameter params) {
        String navigateArgs = "Called navigate() with params";
        logger.debug(navigateArgs);
        String appName = params.app;
        NavigationParameter.LaunchMode launchMode = params.launchMode;

        String error;

        if (appName.equals(GOOGLE_MAPS) && !launchMode.equals(NavigationParameter.LaunchMode.GEO)) {
            error = launchGoogleMaps(params);
        } else if (appName.equals(CITYMAPPER)) {
            error = launchCitymapper(params);
        } else if (appName.equals(UBER)) {
            error = launchUber(params);
        } else if (appName.equals(WAZE)) {
            error = launchWaze(params);
        } else if (appName.equals(YANDEX)) {
            error = launchYandex(params);
        } else if (appName.equals(SYGIC)) {
            error = launchSygic(params);
        } else if (appName.equals(HERE_MAPS)) {
            error = launchHereMaps(params);
        } else if (appName.equals(MOOVIT)) {
            error = launchMoovit(params);
        } else if (appName.equals(LYFT)) {
            error = launchLyft(params);
        } else if (appName.equals(MAPS_ME)) {
            error = launchMapsMe(params);
        } else if (appName.equals(CABIFY)) {
            error = launchCabify(params);
        } else if (appName.equals(BAIDU)) {
            error = launchBaidu(params);
        } else if (appName.equals(GAODE)) {
            error = launchGaode(params);
        } else if (appName.equals(TAXIS_99)) {
            error = launch99Taxis(params);
        } else {
            error = launchApp(params);
        }
        return error;
    }


    /*******************
     * Internal methods
     *******************/

    public static LaunchNavigator with(Context context) throws InvalidParameterException {
        return new LaunchNavigator(context);
    }

    private void initialize(Context context) throws InvalidParameterException {
        if (context == null) {
            throw new InvalidParameterException(LOG_TAG + ": null context passed to initialize()");
        }
        this.context = context;
        this.packageManager = context.getPackageManager();
        discoverAvailableApps();
    }

    private void discoverAvailableApps() {

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(GEO_URI));
        List<ResolveInfo> resolveInfoList = packageManager.queryIntentActivities(intent, 0);
        availableApps = new HashMap<String, String>();
        for (ResolveInfo resolveInfo : resolveInfoList) {
            String packageName = resolveInfo.activityInfo.packageName;
            String appName = getAppName(packageName);
            if (!supportedAppPackages.containsValue(packageName)) { // if it's not already an explicitly supported app
                logger.debug("Found available app supporting GEO protocol: " + appName + " (" + packageName + ")");
                availableApps.put(appName, packageName);
            }
        }

        // Check if explicitly supported apps are installed
        for (Map.Entry<String, String> entry : supportedAppPackages.entrySet()) {
            String _appName = entry.getKey();
            String _packageName = entry.getValue();
            if (isPackageInstalled(_packageName, packageManager)) {
                availableApps.put(supportedAppNames.get(_appName), _packageName);
                logger.debug(_appName + " is available");
            } else {
                logger.debug(_appName + " is not available");
            }
        }
    }

    private String launchApp(NavigationParameter params) {
        String appName = params.app;

        String logMsg = "Using " + getAppDisplayName(appName) + " to navigate to ";
        String destLatLon = null;
        String destName = null;
        String dest;

        if (params.destination.type == Position.Type.NAME) {
            destName = getLocationFromName(params.destination);
            try {
                destLatLon = geocodeAddressToLatLon(params.destination.address);
            } catch (Exception e) {
                return "Unable to geocode destination address to coordinates: " + e.getMessage();
            }
            logMsg += destName;
            if (!StringUtil.isEmpty(destLatLon)) {
                logMsg += "[" + destLatLon + "]";
            }

        } else {
            destLatLon = getLocationFromPos(params.destination);
            logMsg += "[" + destLatLon + "]";
        }

        if (!StringUtil.isEmpty(destLatLon)) {
            dest = destLatLon;
        } else {
            dest = destName;
        }

        String uri = GEO_URI + destLatLon + "?q=" + dest;
        if (!StringUtil.isEmpty(params.destination.nickName)) {
            uri += "(" + params.destination.nickName + ")";
            logMsg += "(" + params.destination.nickName + ")";
        }

        String extras = parseExtrasToUrl(params);
        if (!StringUtil.isEmpty(extras)) {
            uri += extras;
            logMsg += " - extras=" + extras;
        }

        logger.debug(logMsg);
        logger.debug("URI: " + uri);

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        if (!appName.equals(GEO)) {
            if (appName.equals(GOOGLE_MAPS)) {
                appName = supportedAppPackages.get(GOOGLE_MAPS);
            }
            intent.setPackage(appName);
        }
        invokeIntent(intent);
        return null;
    }

    String parsePosition(Position position) {
        String destination;
        if (position.type.equals(Position.Type.POSITION)) {
            destination = getLocationFromPos(position);
        } else {
            destination = getLocationFromName(position);
        }
        return destination;
    }

    private String launchGoogleMaps(NavigationParameter params) {
        String destination;
        String start = null;

        destination = parsePosition(params.destination);

        start = parsePosition(params.start);
        String transportMode = params.transportMode.abbreviation;

        String logMsg = "Using Google Maps to navigate to " + destination;
        String url;

        if (params.launchMode == NavigationParameter.LaunchMode.TURN_BY_TURN) {
            String TURN_BY_TURN_PROTOCOL = "google.navigation:";
            url = TURN_BY_TURN_PROTOCOL + "q=" + destination;
            if (params.transportMode != null) {
                logMsg += " by transportMode=" + params.transportMode.abbreviation;
                url += "&mode=" + transportMode;
            }
            logMsg += " in turn-by-turn mode";
        } else {
            url = MAPS_PROTOCOL + "daddr=" + destination;
            if (!StringUtil.isEmpty(start)) {
                logMsg += " from " + start;
                url += "&saddr=" + start;
            } else {
                logMsg += " from current location";
            }
            logMsg += " in maps mode";
        }

        String extras = parseExtrasToUrl(params);
        if (!StringUtil.isEmpty(extras)) {
            url += extras;
            logMsg += " - extras=" + extras;
        }
        logger.debug(logMsg);
        logger.debug("URI: " + url);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        intent.setClassName(supportedAppPackages.get(GOOGLE_MAPS), "com.google.android.maps.MapsActivity");
        invokeIntent(intent);
        return null;
    }

    private String launchCitymapper(NavigationParameter params) {
        /* try {*/
        String destAddress = null;
        String destLatLon = null;
        String startAddress = null;
        String startLatLon = null;
        String destNickname = params.destination.nickName;
        String startNickname = params.start.nickName;


        if (params.destination.type.equals(Position.Type.NAME)) {
            destAddress = getLocationFromName(params.destination);
        } else {
            destLatLon = getLocationFromPos(params.destination);
        }

        if (params.start.type.equals(Position.Type.NAME)) {
            startAddress = getLocationFromName(params.start);
        } else if (params.start.type.equals(Position.Type.POSITION)) {
            startLatLon = getLocationFromPos(params.start);
        }

        String url = "https://citymapper.com/directions?";
        String logMsg = "Using Citymapper to navigate to";
        if (!StringUtil.isEmpty(destAddress)) {
            url += "&endaddress=" + Uri.encode(destAddress);
            logMsg += " '" + destAddress + "'";
        }
        if (StringUtil.isEmpty(destLatLon)) {
            try {
                destLatLon = geocodeAddressToLatLon(params.destination.address);
            } catch (Exception e) {
                return "Unable to geocode destination address to coordinates: " + e.getMessage();
            }
        }
        url += "&endcoord=" + destLatLon;
        logMsg += " [" + destLatLon + "]";
        if (!StringUtil.isEmpty(destNickname)) {
            url += "&endname=" + Uri.encode(destNickname);
            logMsg += " (" + destNickname + ")";
        }

        if (!params.start.equals(Position.Type.NONE)) {
            logMsg += " from";
            if (!StringUtil.isEmpty(startAddress)) {
                url += "&startaddress=" + Uri.encode(startAddress);
                logMsg += " '" + startAddress + "'";
            }
            if (StringUtil.isEmpty(startLatLon)) {
                try {
                    startLatLon = geocodeAddressToLatLon(params.start.address);
                } catch (Exception e) {
                    return "Unable to geocode start address to coordinates: " + e.getMessage();
                }
            }
            url += "&startcoord=" + startLatLon;
            logMsg += " [" + startLatLon + "]";
            if (!StringUtil.isEmpty(startNickname)) {
                url += "&startname=" + Uri.encode(startNickname);
                logMsg += " (" + startNickname + ")";
            }
        }

        String extras = parseExtrasToUrl(params);
        if (!StringUtil.isEmpty(extras)) {
            url += extras;
            logMsg += " - extras=" + extras;
        }

        logger.debug(logMsg);
        logger.debug("URI: " + url);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        invokeIntent(intent);
        return null;
        /*} catch (JSONException e) {
            String msg = e.getMessage();
            if (msg.contains(NO_APP_FOUND)) {
                msg = "Citymapper app is not installed on this device";
            }
            return msg;
        }*/
    }

    private String launchUber(NavigationParameter params) {

        String destAddress = null;
        String destLatLon = null;
        String startAddress = null;
        String startLatLon = null;
        String destNickname = params.destination.nickName;
        String startNickname = params.start.nickName;


        if (params.destination.type == (Position.Type.NAME)) {
            destAddress = getLocationFromName(params.destination);
        } else {
            destLatLon = getLocationFromPos(params.destination);
        }

        if (params.start.type == (Position.Type.NAME)) {
            startAddress = getLocationFromName(params.start);
        } else if (params.start.type == Position.Type.POSITION) {
            startLatLon = getLocationFromPos(params.start);
        }

        String url = "uber://?action=setPickup";
        String logMsg = "Using Uber to navigate to";
        if (!StringUtil.isEmpty(destAddress)) {
            url += "&dropoff[formatted_address]=" + destAddress;
            logMsg += " '" + destAddress + "'";
        }
        if (!StringUtil.isEmpty(destLatLon)) {
            url += "&dropoff[latitude]=" + params.destination.latitude + "&dropoff[longitude]=" + params.destination.longitude;
            logMsg += " [" + destLatLon + "]";
        }
        if (!StringUtil.isEmpty(destNickname)) {
            url += "&dropoff[nickname]=" + destNickname;
            logMsg += " (" + destNickname + ")";
        }

        logMsg += " from";
        if (params.start.type != Position.Type.NONE) {
            if (!StringUtil.isEmpty(startAddress)) {
                url += "&pickup[formatted_address]==" + startAddress;
                logMsg += " '" + startAddress + "'";
            }
            if (!StringUtil.isEmpty(startLatLon)) {
                url += "&pickup[latitude]=" + params.start.latitude + "&pickup[longitude]=" + params.start.longitude;
                logMsg += " [" + startLatLon + "]";
            }
            if (!StringUtil.isEmpty(startNickname)) {
                url += "&pickup[nickname]=" + startNickname;
                logMsg += " (" + startNickname + ")";
            }
        } else {
            url += "&pickup=my_location";
            logMsg += " current location";
        }

        String extras = parseExtrasToUrl(params);
        if (!StringUtil.isEmpty(extras)) {
            url += extras;
            logMsg += " - extras=" + extras;
        }

        logger.debug(logMsg);
        logger.debug("URI: " + url);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        invokeIntent(intent);
        return null;

    }

    private String launchWaze(NavigationParameter params) {

        String destAddress = null;
        String destLatLon = null;
        if (params.destination.type == Position.Type.NAME) {
            destAddress = getLocationFromName(params.destination);
        } else {
            destLatLon = getLocationFromPos(params.destination);
        }
        String url = "waze://?";
        String logMsg = "Using Waze to navigate to";
        if (!StringUtil.isEmpty(destLatLon)) {
            url += "ll=" + destLatLon;
            logMsg += " [" + destLatLon + "]";
        } else {
            url += "q=" + destAddress;
            logMsg += " '" + destAddress + "'";
        }
        url += "&navigate=yes";

        logMsg += " from current location";

        String extras = parseExtrasToUrl(params);
        if (!StringUtil.isEmpty(extras)) {
            url += extras;
            logMsg += " - extras=" + extras;
        }

        logger.debug(logMsg);
        logger.debug("URI: " + url);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        invokeIntent(intent);
        return null;
    }

    private String launchYandex(NavigationParameter params) {

        String destAddress = null;
        String destLatLon = null;
        String startAddress = null;
        String startLatLon = null;


        if (params.destination.type == Position.Type.NAME) {
            destAddress = getLocationFromName(params.destination);
            try {
                destLatLon = geocodeAddressToLatLon(params.destination.address);
            } catch (Exception e) {
                return "Unable to geocode destination address to coordinates: " + e.getMessage();
            }
        } else {
            destLatLon = getLocationFromPos(params.destination);
        }

        if (params.start.type == Position.Type.NAME) {
            startAddress = getLocationFromName(params.start);
            try {
                startLatLon = geocodeAddressToLatLon(params.start.address);
            } catch (Exception e) {
                return "Unable to geocode start address to coordinates: " + e.getMessage();
            }
        } else if (params.start.type == Position.Type.POSITION) {
            startLatLon = getLocationFromPos(params.start);
        }

        Intent intent = new Intent(supportedAppPackages.get(YANDEX) + ".action.BUILD_ROUTE_ON_MAP");
        intent.setPackage(supportedAppPackages.get(YANDEX));
        String logMsg = "Using Yandex to navigate to";

        String[] parts = splitLatLon(destLatLon);
        intent.putExtra("lat_to", parts[0]);
        intent.putExtra("lon_to", parts[1]);
        logMsg += " [" + destLatLon + "]";

        if (!StringUtil.isEmpty(destAddress)) {
            logMsg += " ('" + destAddress + "')";
        }


        logMsg += " from";
        if (params.start.type != Position.Type.NONE) {
            parts = splitLatLon(startLatLon);
            intent.putExtra("lat_from", parts[0]);
            intent.putExtra("lon_from", parts[1]);
            logMsg += " [" + startLatLon + "]";
            if (!StringUtil.isEmpty(startAddress)) {
                logMsg += " ('" + startAddress + "')";
            }
        } else {
            logMsg += " current location";
        }


        for (Map.Entry<String, String> entry : params.extras.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            intent.putExtra(key, value);

        }
        logger.debug(logMsg);
        invokeIntent(intent);
        return null;
    }

    private String launchSygic(NavigationParameter params) {

        String destAddress = null;
        String destLatLon = null;

        String url = supportedAppPackages.get(SYGIC) + "://coordinate|";
        String logMsg = "Using Sygic to navigate to";
        String transportMode = null;
        if (params.transportMode == NavigationParameter.TransportMode.WALKING) {
            transportMode = "walk";
        } else {
            transportMode = "drive";
        }

        if (params.destination.type == Position.Type.NAME) {
            destAddress = getLocationFromName(params.destination);
            logMsg += " '" + destAddress + "'";
            try {
                destLatLon = geocodeAddressToLatLon(params.destination.address);
            } catch (Exception e) {
                return "Unable to geocode destination address to coordinates: " + e.getMessage();
            }
        } else {
            destLatLon = getLocationFromPos(params.destination);
        }

        logMsg += " [" + destLatLon + "]";

        String[] pos = splitLatLon(destLatLon);
        url += pos[1] + "|" + pos[0] + "|" + transportMode;

        logMsg += " by " + transportMode;

        String extras = parseExtrasToUrl(params);
        if (!StringUtil.isEmpty(extras)) {
            url += extras;
            logMsg += " - extras=" + extras;
        }

        logger.debug(logMsg);
        logger.debug("URI: " + url);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        invokeIntent(intent);
        return null;
    }

    private String launchHereMaps(NavigationParameter params) {

        String destAddress;
        String destLatLon = null;
        String startAddress;
        String startLatLon = null;
        String destNickname = params.destination.nickName;
        String startNickname = params.start.nickName;


        String url = "https://share.here.com/r/";
        String logMsg = "Using HERE Maps to navigate";

        logMsg += " from";
        if (params.start.type == Position.Type.NONE) {
            url += "mylocation";
            logMsg += " Current Location";

        } else {
            if (params.start.type == Position.Type.NAME) {
                startAddress = getLocationFromName(params.start);
                logMsg += " '" + startAddress + "'";
                try {
                    startLatLon = geocodeAddressToLatLon(params.start.address);
                } catch (Exception e) {
                    return "Unable to geocode start address to coordinates: " + e.getMessage();
                }
            } else if (params.start.type == Position.Type.POSITION) {
                startLatLon = getLocationFromPos(params.start);
            }

            url += startLatLon;
            logMsg += " [" + startLatLon + "]";

            if (!StringUtil.isEmpty(startNickname)) {
                url += "," + startNickname;
                logMsg += " (" + startNickname + ")";
            }
        }

        url += "/";
        logMsg += " to";
        if (params.destination.type == Position.Type.NAME) {
            destAddress = getLocationFromName(params.destination);
            logMsg += " '" + destAddress + "'";
            try {
                destLatLon = geocodeAddressToLatLon(params.destination.address);
            } catch (Exception e) {
                return "Unable to geocode destination address to coordinates: " + e.getMessage();
            }
        } else {
            destLatLon = getLocationFromPos(params.destination);
        }
        logMsg += " [" + destLatLon + "]";
        url += destLatLon;

        if (!StringUtil.isEmpty(destNickname)) {
            url += "," + destNickname;
            logMsg += " (" + destNickname + ")";
        }

        String extras = parseExtrasToUrl(params);
        if (!StringUtil.isEmpty(extras)) {
            url += "?" + extras;
            logMsg += " - extras=" + extras;
        }

        logger.debug(logMsg);
        logger.debug("URI: " + url);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        invokeIntent(intent);
        return null;

    }

    private String launchMoovit(NavigationParameter params) {

        String destAddress;
        String destLatLon = null;
        String startAddress;
        String startLatLon = null;
        String destNickname = params.destination.nickName;
        String startNickname = params.start.nickName;

        String url = "moovit://directions";
        String logMsg = "Using Moovit to navigate";


        logMsg += " to";
        if (params.destination.type == Position.Type.NAME) {
            destAddress = getLocationFromName(params.destination);
            logMsg += " '" + destAddress + "'";
            try {
                destLatLon = geocodeAddressToLatLon(params.destination.address);
            } catch (Exception e) {
                return "Unable to geocode destination address to coordinates: " + e.getMessage();
            }
        } else {
            destLatLon = getLocationFromPos(params.destination);
        }
        logMsg += " [" + destLatLon + "]";

        String[] destPos = splitLatLon(destLatLon);
        url += "?dest_lat=" + destPos[0] + "&dest_lon=" + destPos[1];

        if (!StringUtil.isEmpty(destNickname)) {
            url += "&dest_name=" + destNickname;
            logMsg += " (" + destNickname + ")";
        }

        logMsg += " from";
        if (params.start.type == Position.Type.NONE) {
            logMsg += " Current Location";
        } else {
            if (params.start.type == Position.Type.NAME) {
                startAddress = getLocationFromName(params.start);
                logMsg += " '" + startAddress + "'";
                try {
                    startLatLon = geocodeAddressToLatLon(params.start.address);
                } catch (Exception e) {
                    return "Unable to geocode start address to coordinates: " + e.getMessage();
                }
            } else if (params.start.type == Position.Type.POSITION) {
                startLatLon = getLocationFromPos(params.start);
            }

            String[] startPos = splitLatLon(startLatLon);
            url += "&orig_lat=" + startPos[0] + "&orig_lon=" + startPos[1];
            logMsg += " [" + startLatLon + "]";

            if (!StringUtil.isEmpty(startNickname)) {
                url += "&orig_name=" + startNickname;
                logMsg += " (" + startNickname + ")";
            }
        }

        String extras = parseExtrasToUrl(params);
        if (!StringUtil.isEmpty(extras)) {
            url += extras;
            logMsg += " - extras=" + extras;
        }

        logger.debug(logMsg);
        logger.debug("URI: " + url);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        invokeIntent(intent);
        return null;

    }

    private String launchLyft(NavigationParameter params) {

        String destAddress;
        String destLatLon = null;
        String startAddress;
        String startLatLon = null;

        String url = "lyft://ridetype?";
        String logMsg = "Using Lyft to navigate";

        String extras = parseExtrasToUrl(params);
        if (!StringUtil.isEmpty(extras)) {
            url += extras;
            logMsg += " - extras=" + extras;
        }

        if (StringUtil.isEmpty(extras) || !extras.contains("id=")) {
            url += "id=lyft";
        }

        logMsg += " to";
        if (params.destination.type == Position.Type.NAME) {
            destAddress = getLocationFromName(params.destination);
            logMsg += " '" + destAddress + "'";
            try {
                destLatLon = geocodeAddressToLatLon(params.destination.address);
            } catch (Exception e) {
                return "Unable to geocode destination address to coordinates: " + e.getMessage();
            }
        } else {
            destLatLon = getLocationFromPos(params.destination);
        }
        logMsg += " [" + destLatLon + "]";

        String[] destPos = splitLatLon(destLatLon);
        url += "&destination[latitude]=" + destPos[0] + "&destination[longitude]=" + destPos[1];

        logMsg += " from";
        if (params.start.type == Position.Type.NONE) {
            logMsg += " Current Location";
        } else {
            if (params.start.type == Position.Type.NAME) {
                startAddress = getLocationFromName(params.start);
                logMsg += " '" + startAddress + "'";
                try {
                    startLatLon = geocodeAddressToLatLon(params.start.address);
                } catch (Exception e) {
                    return "Unable to geocode start address to coordinates: " + e.getMessage();
                }
            } else if (params.start.type == Position.Type.POSITION) {
                startLatLon = getLocationFromPos(params.start);
            }

            String[] startPos = splitLatLon(startLatLon);
            url += "&pickup[latitude]=" + startPos[0] + "&pickup[longitude]=" + startPos[1];
            logMsg += " [" + startLatLon + "]";

        }

        logger.debug(logMsg);
        logger.debug("URI: " + url);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        invokeIntent(intent);
        return null;
    }

    private String launchMapsMe(NavigationParameter params) {

        String destAddress;
        String destLatLon = null;
        String startAddress;
        String startLatLon = null;


        Intent intent = new Intent(supportedAppPackages.get(MAPS_ME).concat(".action.BUILD_ROUTE"));
        intent.setPackage(supportedAppPackages.get(MAPS_ME));

        String logMsg = "Using MAPs.ME to navigate";

        logMsg += " to";
        if (params.destination.type == Position.Type.NAME) {
            destAddress = getLocationFromName(params.destination);
            logMsg += " '" + destAddress + "'";
            try {
                destLatLon = geocodeAddressToLatLon(params.destination.address);
            } catch (Exception e) {
                return "Unable to geocode destination address to coordinates: " + e.getMessage();
            }
        } else {
            destLatLon = getLocationFromPos(params.destination);
        }
        logMsg += " [" + destLatLon + "]";

        String[] destPos = splitLatLon(destLatLon);
        intent.putExtra("lat_to", Double.parseDouble(destPos[0]));
        intent.putExtra("lon_to", Double.parseDouble(destPos[1]));

        logMsg += " from";
        if (params.start.type == Position.Type.NONE) {
            logMsg += " Current Location";
        } else {
            if (params.start.type == Position.Type.NAME) {
                startAddress = getLocationFromName(params.start);
                logMsg += " '" + startAddress + "'";
                try {
                    startLatLon = geocodeAddressToLatLon(params.start.address);
                } catch (Exception e) {
                    return "Unable to geocode start address to coordinates: " + e.getMessage();
                }
            } else if (params.start.type == Position.Type.POSITION) {
                startLatLon = getLocationFromPos(params.start);
            }
            intent.putExtra("lat_from", params.start.latitude);
            intent.putExtra("lon_from", params.start.longitude);
            logMsg += " [" + startLatLon + "]";
        }

        String transportMode = null;
        switch (params.transportMode) {
            case WALKING:
                transportMode = "pedestrian";
                break;
            case BICYCLE:
                transportMode = "bicycle";
                break;
            case TRANSIT:
                transportMode = "taxi";
                break;
            default:
                transportMode = "vehicle";
                break;
        }

        if (!StringUtil.isEmpty(transportMode)) {
            intent.putExtra("router", transportMode);
            logMsg += " by transportMode=" + transportMode;
        }
        logger.debug(logMsg);
        invokeIntent(intent);
        return null;
    }

    private String launchCabify(NavigationParameter params) {
        try {
            String destAddress;
            String destLatLon = null;
            String startAddress;
            String startLatLon = null;
            String destNickname = params.destination.nickName;
            String startNickname = params.start.nickName;

            String url = "cabify://cabify/journey?json=";
            String logMsg = "Using Cabify to navigate";
            JSONObject oJson = new JSONObject();

            // Parse destination
            JSONObject oDest = new JSONObject();
            logMsg += " to";

            if (params.destination.type == Position.Type.NAME) {
                destAddress = getLocationFromName(params.destination);
                logMsg += " '" + destAddress + "'";
                try {
                    destLatLon = geocodeAddressToLatLon(params.destination.address);
                } catch (Exception e) {
                    return "Unable to geocode destination address to coordinates: " + e.getMessage();
                }
            } else {
                destLatLon = getLocationFromPos(params.destination);
            }
            logMsg += " [" + destLatLon + "]";

            JSONObject oDestLoc = new JSONObject();
            try {
                oDestLoc.put("latitude", params.destination.latitude);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            oDestLoc.put("longitude", params.destination.longitude);
            oDest.put("loc", oDestLoc);

            if (!StringUtil.isEmpty(destNickname)) {
                oDest.put("name", destNickname);
                logMsg += " (" + destNickname + ")";
            }

            // Parse start
            JSONObject oStart = new JSONObject();
            logMsg += " from";

            if (params.start.type == Position.Type.NONE) {
                logMsg += " Current Location";
                oStart.put("loc", "current");
            } else {
                if (params.start.type == Position.Type.NAME) {
                    startAddress = getLocationFromName(params.start);
                    logMsg += " '" + startAddress + "'";
                    try {
                        startLatLon = geocodeAddressToLatLon(params.start.address);
                    } catch (Exception e) {
                        return "Unable to geocode start address to coordinates: " + e.getMessage();
                    }
                } else if (params.start.type == Position.Type.POSITION) {
                    startLatLon = getLocationFromPos(params.start);
                }
                logMsg += " [" + startLatLon + "]";
                String[] startPos = splitLatLon(startLatLon);

                JSONObject oStartLoc = new JSONObject();
                oStartLoc.put("latitude", params.start.latitude);
                oStartLoc.put("longitude", params.start.longitude);
                oStart.put("loc", oStartLoc);
            }

            if (!StringUtil.isEmpty(startNickname)) {
                oStart.put("name", startNickname);
                logMsg += " (" + startNickname + ")";
            }

            // Assemble JSON
            JSONArray aStops = new JSONArray();
            aStops.put(oStart);

            Map extras = params.getExtras();
            if (!(extras == null)) {
                logMsg += " - extras=" + extras;
            }

            // Assemble JSON

            aStops.put(oStart);
            aStops.put(oDest);
            if (extras.get("stops") != null) {
                try {
                    List<String> stops = (List) extras.get("stops");
                    for (String obj : stops) {
                        aStops.put(obj);
                    }

                } catch (Exception err) {

                }

            }
            oJson.put("stops", aStops);

            url += oJson.toString();

            logger.debug(logMsg);
            logger.debug("URI: " + url);
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            invokeIntent(intent);
            return null;
        } catch (JSONException e) {
            String msg = e.getMessage();
            if (msg.contains(NO_APP_FOUND)) {
                msg = "Gaode Maps app is not installed on this device";
            }
            return msg;
        }
    }

    private String launchBaidu(NavigationParameter params) {

        String start;
        String dest;
        String destNickname = params.destination.nickName;
        String startNickname = params.start.nickName;


        String transportMode;


        String url = "baidumap://map/direction";
        String logMsg = "Using Baidu Maps to navigate";

        String extras = parseExtrasToUrl(params);
        if (StringUtil.isEmpty(extras)) {
            extras = "";
        }

        if (!extras.contains("coord_type=")) {
            extras += "&coord_type=wgs84";
        }

        // Destination
        logMsg += " to";
        if (params.destination.type == Position.Type.NAME) {
            dest = getLocationFromName(params.destination);
            logMsg += dest;
        } else {
            dest = getLocationFromPos(params.destination);
            logMsg += " [" + dest + "]";
            if (!StringUtil.isEmpty(destNickname)) {
                dest = "latlng:" + dest + "|name:" + destNickname;
                logMsg += " (" + destNickname + ")";
            }
        }
        url += "?destination=" + dest;

        // Start
        logMsg += " from";
        if (params.start.type == Position.Type.NONE) {
            logMsg += " Current Location";
        } else {
            if (params.start.type == Position.Type.NAME) {
                start = getLocationFromName(params.start);
                logMsg += start;
            } else {
                start = getLocationFromPos(params.start);
                logMsg += " [" + start + "]";
                if (!StringUtil.isEmpty(startNickname)) {
                    start = "latlng:" + start + "|name:" + startNickname;
                    logMsg += " (" + startNickname + ")";
                }
            }
            url += "&origin=" + start;
        }

        // Transport mode
        switch (params.transportMode) {
            case WALKING:
                transportMode = "walking";
                break;
            case BICYCLE:
                transportMode = "riding";
                break;
            case TRANSIT:
                transportMode = "transit";
                break;
            default:
                transportMode = "driving";
                break;
        }

        url += "&mode=" + transportMode;
        logMsg += " by transportMode=" + transportMode;

        // Extras
        url += extras;
        logMsg += " - extras=" + extras;


        logger.debug(logMsg);
        logger.debug("URI: " + url);

        Intent intent = new Intent();
        intent.setData(Uri.parse(url));
        invokeIntent(intent);
        return null;

    }

    private String launchGaode(NavigationParameter params) {

        String destAddress = null;
        String destLatLon = null;
        String startAddress = null;
        String startLatLon = null;
        String destNickname = params.destination.nickName;
        String startNickname = params.start.nickName;

        String transportMode;


        String url = "amapuri://route/plan/?";
        String logMsg = "Using Gaode Maps to navigate";

        String extras = parseExtrasToUrl(params);
        if (StringUtil.isEmpty(extras)) {
            extras = "";
        }

        if (!extras.contains("sourceApplication=")) {
            extras += "&sourceApplication=" + Uri.encode(getThisAppName());
        }

        // Destination
        logMsg += " to";
        if (params.destination.type == Position.Type.NAME) {
            destAddress = getLocationFromName(params.destination);
            logMsg += " '" + destAddress + "'";
            try {
                destLatLon = geocodeAddressToLatLon(params.destination.address);
            } catch (Exception e) {
                return "Unable to geocode destination address to coordinates: " + e.getMessage();
            }
        } else {
            destLatLon = getLocationFromPos(params.destination);
        }
        logMsg += " [" + destLatLon + "]";
        String[] pos = splitLatLon(destLatLon);
        url += "dlat=" + pos[0] + "&dlon=" + pos[1];

        // Dest name
        if (!StringUtil.isEmpty(destNickname)) {
            logMsg += " (" + destNickname + ")";
            url += "&dname=" + destNickname;
        }

        // Start
        logMsg += " from";
        if (params.start.type == Position.Type.NONE) {
            logMsg += " Current Location";
        } else {
            if (params.start.type == Position.Type.NAME) {
                startAddress = getLocationFromName(params.start);
                logMsg += " '" + startAddress + "'";
                try {
                    startLatLon = geocodeAddressToLatLon(params.start.address);
                } catch (Exception e) {
                    startLatLon = null;
                }
            } else {
                startLatLon = getLocationFromPos(params.start);
            }
            if (!StringUtil.isEmpty(startLatLon)) {
                logMsg += " [" + startLatLon + "]";
                pos = splitLatLon(startLatLon);
                url += "&slat=" + pos[0] + "&slon=" + pos[1];

                // Start name
                if (!StringUtil.isEmpty(startNickname)) {
                    logMsg += " (" + startNickname + ")";
                    url += "&sname=" + startNickname;
                }
            }
        }


        // Transport mode
        String transportModeName;
        switch (params.transportMode) {
            case WALKING:
                transportModeName = "walking";
                transportMode = "2";
                break;
            case BICYCLE:
                transportModeName = "bicycle";
                transportMode = "3";
                break;
            case TRANSIT:
                transportModeName = "transit";
                transportMode = "1";
                break;
            default:
                transportModeName = "driving";
                transportMode = "0";
                break;
        }
        url += "&t=" + transportMode;
        logMsg += " by transportMode=" + transportModeName;

        // Extras
        url += extras;
        logMsg += " - extras=" + extras;


        logger.debug(logMsg);
        logger.debug("URI: " + url);

        Intent intent = new Intent();
        intent.setData(Uri.parse(url));
        invokeIntent(intent);
        return null;
    }

    private String launch99Taxis(NavigationParameter params) {

        String destAddress = null;
        String destLatLon = null;
        String startAddress = null;
        String startLatLon = null;
        String destNickname = params.destination.nickName;
        String startNickname = params.start.nickName;

        String url = "taxis99://call?";
        String logMsg = "Using 99 Taxi to navigate";

        String extras = parseExtrasToUrl(params);
        if (StringUtil.isEmpty(extras)) {
            extras = "";
        }

        if (!extras.contains("deep_link_product_id")) {
            extras += "&deep_link_product_id=316";
        }

        if (!extras.contains("client_id")) {
            extras += "&client_id=MAP_123";
        }

        // Destination
        logMsg += " to";
        if (params.destination.type == Position.Type.NAME) {
            destAddress = getLocationFromName(params.destination);
            logMsg += " '" + destAddress + "'";
            try {
                destLatLon = geocodeAddressToLatLon(params.destination.address);
            } catch (Exception e) {
                return "Unable to geocode destination address to coordinates: " + e.getMessage();
            }
        } else {
            destLatLon = getLocationFromPos(params.destination);
        }
        logMsg += " [" + destLatLon + "]";
        String[] pos = splitLatLon(destLatLon);
        url += "dropoff_latitude=" + pos[0] + "&dropoff_longitude=" + pos[1];

        // Dest name
        if (StringUtil.isEmpty(destNickname)) {
            if (!StringUtil.isEmpty(destAddress)) {
                destNickname = destAddress;
            } else {
                destNickname = "Dropoff";
            }
        }
        logMsg += " (" + destNickname + ")";
        url += "&dropoff_title=" + destNickname;

        // Start
        logMsg += " from";
        if (params.start.type == Position.Type.NAME) {
            startAddress = getLocationFromName(params.start);
            logMsg += " '" + startAddress + "'";
            try {
                startLatLon = geocodeAddressToLatLon(params.start.address);
            } catch (Exception e) {
                return "Unable to geocode start address to coordinates: " + e.getMessage();
            }
        } else if (params.start.type == Position.Type.POSITION) {
            startLatLon = getLocationFromPos(params.start);
        } else {
            return "start location is a required parameter for 99 Taxi and must be specified";
        }
        logMsg += " [" + startLatLon + "]";
        pos = splitLatLon(startLatLon);
        url += "&pickup_latitude=" + pos[0] + "&pickup_longitude=" + pos[1];

        // Start name
        if (StringUtil.isEmpty(startNickname)) {
            if (!StringUtil.isEmpty(startAddress)) {
                startNickname = startAddress;
            } else {
                startNickname = "Pickup";
            }
        }
        logMsg += " (" + startNickname + ")";
        url += "&pickup_title=" + startNickname;


        // Extras
        url += extras;
        logMsg += " - extras=" + extras;

        logger.debug(logMsg);
        logger.debug("URI: " + url);

        Intent intent = new Intent();
        intent.setData(Uri.parse(url));
        invokeIntent(intent);
        return null;
    }

    /*
     * Utilities
     */
    private void invokeIntent(Intent intent) {
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    private String parseExtrasToUrl(NavigationParameter params) {
        String extras = null;
        if (params.extras != null) {
            for (Map.Entry<String, String> entry : params.extras.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                extras += "&" + key + "=" + value;
            }
            return extras;
        }
        return "";
    }

    private String getLocationFromPos(Position position) {
        String location;
        if (StringUtil.isEmpty(position.latitude) || StringUtil.isEmpty(position.longitude)) {
            throw new InvalidParameterException("Expected arguments for lat/lon.");
        }
        location = position.latitude + "," + position.longitude;
        return location;
    }

    private String getLocationFromName(Position position) {

        String name = position.name;
        if (StringUtil.isEmpty(name) || name.length() == 0) {
            throw new InvalidParameterException("Expected non-empty string argument for place name.");
        }
        return name;
    }

    private String[] splitLatLon(String latlon) {
        return latlon.split(",");
    }

    private String getAppName(String packageName) {
        ApplicationInfo ai;
        try {
            ai = packageManager.getApplicationInfo(packageName, 0);
        } catch (final PackageManager.NameNotFoundException e) {
            ai = null;
        }
        final String applicationName = (String) (ai != null ? packageManager.getApplicationLabel(ai) : null);
        return applicationName;
    }


    private boolean isPackageInstalled(String packagename, PackageManager packageManager) {
        try {
            packageManager.getPackageInfo(packagename, PackageManager.GET_ACTIVITIES);
            ApplicationInfo ai = packageManager.getApplicationInfo(packagename, 0);
            return ai.enabled;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    private String geocodeAddressToLatLon(String address) throws JSONException, IOException {
        String result;
        String errMsg = "Unable to geocode coords from address '" + address;

        if (!geocodingEnabled) {
            throw new InvalidParameterException("Geocoding disabled: " + errMsg);
        }

        if (!isNetworkAvailable()) {
            throw new InvalidParameterException("No internet connection: " + errMsg);
        }

        address = address.replaceAll(" ", "%20");

        JSONObject oResponse = doGeocode("address=" + address);

        double longitude = oResponse
                .getJSONObject("geometry").getJSONObject("location")
                .getDouble("lng");

        double latitude = oResponse
                .getJSONObject("geometry").getJSONObject("location")
                .getDouble("lat");

        result = latitude + "," + longitude;
        logger.debug("Geocoded '" + address + "' to '" + result + "'");
        return result;
    }

    private String reverseGeocodeLatLonToAddress(String latLon) throws JSONException, IOException {
        String result;
        String errMsg = "Unable to reverse geocode address from coords '" + latLon;
        if (!geocodingEnabled) {
            throw new InvalidParameterException("Geocoding is disabled: " + errMsg);
        }

        if (!isNetworkAvailable()) {
            throw new InvalidParameterException("No internet connection: " + errMsg);
        }

        JSONObject oResponse = doGeocode("latlng=" + latLon);
        result = oResponse.getString("formatted_address");
        logger.debug("Reverse geocoded '" + latLon + "' to '" + result + "'");
        return result;
    }

    private JSONObject doGeocode(String query) throws IOException, JSONException {
        if (this.googleApiKey == null) {
            throw new InvalidParameterException("Google API key has not been specified");
        }
        String url = "https://maps.google.com/maps/api/geocode/json?" + query + "&sensor=false&key=" + this.googleApiKey;
        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = httpClient.newCall(request).execute();
        String responseBody = response.body().string();
        JSONObject oResponse = new JSONObject(responseBody);
        if (oResponse.has("error_message")) {
            throw new InvalidParameterException(oResponse.getString("error_message"));
        }
        return ((JSONArray) oResponse.get("results")).getJSONObject(0);
    }

    private String getAppDisplayName(String packageName) {
        String name = "[Not found]";
        if (packageName.equals(GEO)) {
            return "[Native chooser]";
        }
        for (Map.Entry<String, String> entry : availableApps.entrySet()) {
            String appName = entry.getKey();
            String _packageName = entry.getValue();
            if (packageName.equals(_packageName)) {
                name = appName;
                break;
            }
        }
        return name;
    }

    private String getThisAppName() {
        return context.getApplicationInfo().loadLabel(packageManager).toString();
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}