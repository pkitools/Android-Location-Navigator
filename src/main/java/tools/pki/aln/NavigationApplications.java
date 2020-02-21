/*
 * Copyright (c) 2020. PKI.Tools
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package tools.pki.aln;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NavigationApplications {
    public static final String GEO_URI = "GEO:";
    private final Map<String, String> supportedAppNames = new HashMap<>();
    /**
     * Name/Package map
     */
    private static final Map<String, String> supportedAppPackages = new HashMap<>();
    Context context;
    ILogger logger;
    PackageManager packageManager;

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
    // Explicitly supported apps
    protected static final String GEO = "GEO"; // Use native app choose for GEO: intent


    private NavigationApplications() {
        supportedAppNames.put(GOOGLE_MAPS, "Google Maps");
        supportedAppNames.put(CITYMAPPER, "Citymapper");
        supportedAppNames.put(UBER, "Uber");
        supportedAppNames.put(WAZE, "Waze");
        supportedAppNames.put(YANDEX, "Yandex Navigator");
        supportedAppNames.put(SYGIC, "Sygic");
        supportedAppNames.put(HERE_MAPS, "HERE Maps");
        supportedAppNames.put(MOOVIT, "Moovit");
        supportedAppNames.put(LYFT, "Lyft");
        supportedAppNames.put(MAPS_ME, "MAPS.ME");
        supportedAppNames.put(CABIFY, "Cabify");
        supportedAppNames.put(BAIDU, "Baidu Maps");
        supportedAppNames.put(TAXIS_99, "99 Taxi");
        supportedAppNames.put(GAODE, "Gaode Maps (Amap)");
        supportedAppPackages.put(GOOGLE_MAPS, "com.google.android.apps.maps");
        supportedAppPackages.put(CITYMAPPER, "com.citymapper.app.release");
        supportedAppPackages.put(UBER, "com.ubercab");
        supportedAppPackages.put(WAZE, "com.waze");
        supportedAppPackages.put(YANDEX, "ru.yandex.yandexnavi");
        supportedAppPackages.put(SYGIC, "com.sygic.aura");
        supportedAppPackages.put(HERE_MAPS, "com.here.app.maps");
        supportedAppPackages.put(MOOVIT, "com.tranzmate");
        supportedAppPackages.put(LYFT, "me.lyft.android");
        supportedAppPackages.put(MAPS_ME, "com.mapswithme.maps.pro");
        supportedAppPackages.put(CABIFY, "com.cabify.rider");
        supportedAppPackages.put(BAIDU, "com.baidu.BaiduMap");
        supportedAppPackages.put(TAXIS_99, "com.taxis99");
        supportedAppPackages.put(GAODE, "com.autonavi.minimap");

    }


    public NavigationApplications(Context context, ILogger logger) {
        this();
        setContext(context);
        discoverAllGeoApps();
        this.logger = logger;
    }

    static NavigationApplications with(Context context) {
        return new NavigationApplications(context, null);
    }

    void setContext(Context context) {
        this.context = context;
        packageManager = context.getApplicationContext().getPackageManager();
    }


    public static String getPackage(String applicationName) {
        return supportedAppPackages.get(applicationName);

    }


    // Map of app name to package name
    protected Map<String, NavigationAppInfo> installedGeoApps;


    /**
     * All applications with indicator that they are installed and supported or not
     *
     * @return map of app name and application status
     */
    public Map<String, NavigationAppInfo> all() {
        return installedGeoApps;
    }

    /**
     * Get All GEO apps that are installed in phone it firstly adds the apps that are not in supported list and then checks if supported apps are present
     */
    protected void discoverAllGeoApps() {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(GEO_URI));
        List<ResolveInfo> resolveInfoList = packageManager.queryIntentActivities(intent, 0);
        installedGeoApps = new HashMap<>();
        for (ResolveInfo resolveInfo : resolveInfoList) {
            String packageName = resolveInfo.activityInfo.packageName;
            String appName = getAppName(packageName);
            NavigationAppInfo navigationAppInfo = new NavigationAppBuilder().name(appName).friendlyName(appName).installed(true)
                    .supported(supportedAppPackages.containsValue(packageName)).packageName(packageName).build();
            installedGeoApps.put(appName, navigationAppInfo);
        }

        // Check if explicitly supported apps are installed
        for (Map.Entry<String, String> entry : supportedAppPackages.entrySet()) {
            String selectedAppName = entry.getKey();
            String selectedFriendlyName = supportedAppNames.get(selectedAppName);
            String selectedPackageName = entry.getValue();

            if (!installedGeoApps.containsKey(selectedAppName)) {
                NavigationAppInfo navigationAppInfo = new NavigationAppBuilder().name(selectedAppName)
                        .friendlyName(selectedFriendlyName).packageName(selectedPackageName)
                        .supported(true).installed(isPackageInstalled(selectedPackageName, packageManager)).build();
                installedGeoApps.put(selectedAppName, navigationAppInfo);
            }
        }
    }

    private String getAppName(String packageName) {
        ApplicationInfo ai;
        try {
            ai = packageManager.getApplicationInfo(packageName, 0);
        } catch (final PackageManager.NameNotFoundException e) {
            ai = null;
        }
        return (String) (ai != null ? packageManager.getApplicationLabel(ai) : null);
    }

    public String getThisAppName() {
        return context.getApplicationInfo().loadLabel(packageManager).toString();
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


    /**
     * Provide list of apps supported or existing in this device
     *
     * @return map of friendly name and normal name
     */
    public List<String> installedAndSupported() {
        List apps = new ArrayList();
        for (Map.Entry<String, NavigationAppInfo> value : installedGeoApps.entrySet()) {
            if (value.getValue().installed && value.getValue().supported)
                apps.add(value.getValue().friendlyName);
        }
        return apps;
    }

    /**
     * Provide list of apps supported or existing in this device
     *
     * @return map of friendly name and normal name
     */
    public List<String> installed() {
        List apps = new ArrayList();
        for (Map.Entry<String, NavigationAppInfo> value : installedGeoApps.entrySet()) {
            if (value.getValue().installed)
                apps.add(value.getValue().friendlyName);
        }
        return apps;
    }

    public boolean isAppAvailable(String appName) {
        if (supportedAppPackages.containsKey(appName)) {
            appName = supportedAppPackages.get(appName);
        }
        NavigationAppInfo appInfo = installedGeoApps.get(appName);
        if (appInfo == null)
            return false;
        return appInfo.installed;
    }

   public boolean installedAndSupported(String appName) {
        if (supportedAppPackages.containsKey(appName)) {
            appName = supportedAppPackages.get(appName);
        }
        NavigationAppInfo appInfo = installedGeoApps.get(appName);
        if (appInfo == null)
            return false;
        return appInfo.installed && appInfo.supported;
    }



    public String getAppDisplayName(String packageName) {

        String name = "[Not found]";
        if (packageName == null || packageName.equals(GEO)) {
            return "[Native chooser]";
        }
        for (Map.Entry<String, NavigationAppInfo> entry : installedGeoApps.entrySet()) {
            NavigationAppInfo thisPackage = entry.getValue();
            if (packageName.equals(thisPackage.packageName)) {
                return thisPackage.friendlyName;
            }
        }
        return name;
    }

    void debug(String text) {
        if (logger != null)
            logger.debug(text);
    }
}
