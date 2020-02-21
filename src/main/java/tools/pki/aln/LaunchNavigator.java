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


import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import tools.pki.aln.apps.BaidoApp;
import tools.pki.aln.apps.CabifyApp;
import tools.pki.aln.apps.CityMapperApp;
import tools.pki.aln.apps.GaodeApp;
import tools.pki.aln.apps.GenericAppLauncher;
import tools.pki.aln.apps.GoogleMapsApp;
import tools.pki.aln.apps.HereMapsApp;
import tools.pki.aln.apps.L99TaxisApp;
import tools.pki.aln.apps.LyftApp;
import tools.pki.aln.apps.MapsMeApp;
import tools.pki.aln.apps.MoovitApp;
import tools.pki.aln.apps.StringUtil;
import tools.pki.aln.apps.SygicApp;
import tools.pki.aln.apps.UberApp;
import tools.pki.aln.apps.WazeApp;
import tools.pki.aln.apps.YandexApp;


public class LaunchNavigator extends NavigationApplications {


    /**********************
     * private static properties
     **********************/
    private static final String LOG_TAG = "LaunchNavigator";

    /**********************
     * Internal properties
     **********************/

    private boolean geocodingEnabled = false;

    GeoCoder geoCoder;

    private ILogger logger;

    private NavigationParameter parameter;

    private String appName;

    /*******************
     * Constructors
     *******************/

    /**
     * To lunch app with geocoding enabled
     *
     * @param context this is required for checking installed apps and other context related stuffs
     * @param logger used for logging info
     * @param apiKey Google API key
     * @throws NavigationException if invalid parameter is provided
     */
    public LaunchNavigator(@NonNull Context context,@Nullable ILogger logger,@Nullable String apiKey) throws NavigationException {
        super(context,logger);
        validateObject(context, "context");

        if (apiKey != null) {
            this.geoCoder = new GeoCoder(apiKey);
            geocodingEnabled = true;
        }
        if (logger == null)
            this.logger = new AndroidLogger();
        else {
            this.logger = (logger);
        }
    }

    /**
     * Lunch application with context and loger
     *
     * @param context it is compulsory
     * @param logger  if null we will use default android logger otherwise you can implement your own way of loging
     * @throws NavigationException error in parameters
     */
    public LaunchNavigator(@NonNull Context context, @Nullable ILogger logger) throws NavigationException {
        this(context, logger, null);
    }


    /**
     * Call if you are not using Geocoding
     * @param context this is required for checking installed apps and other context related stuffs
     * @throws NavigationException if invalid parameter is provided
     */
    private LaunchNavigator(@NonNull Context context) throws NavigationException {
        this(context, new AndroidLogger(), null);
    }


    /**
     *
     * @param context get an instance for your context
     * @return an instance of {@link LaunchNavigator}
     * @throws NavigationException if invalid parameter is provided
     */
    public static LaunchNavigator withContext(@NonNull Context context) throws NavigationException {
        return new LaunchNavigator(context);
    }



    /**
     * Call this if you don't want API calls for geocoding, Set google API Key if you want to enable Geocoding
     *
     * @return an instance of {@link LaunchNavigator}
     */
    public LaunchNavigator withoutGeoCoding() {
        this.geocodingEnabled = false;
        return this;
    }

    /**
     * Set GoogleApi Key if you are using places API
     *
     * @param googleApiKey key for places API
     * @return an instance of {@link LaunchNavigator}
     */
    public LaunchNavigator apiKey(String googleApiKey) {
        this.geoCoder = new GeoCoder(googleApiKey);
        this.geocodingEnabled = true;
        return this;
    }



    /*******************
     * Public API
     ******************
     **/

    /**
     * Navigate to a point using class parameter
     * @throws NavigationException no parameter of error in navigation
     */
    public void navigate() throws NavigationException {
        validateObject(parameter, "Navigation Parameter");
        navigate(parameter);
    }

    /**
     * Run a navigation App to go from one point to another
     *
     * @param parameter
     * @
     */
    public void navigate(NavigationParameter parameter) throws NavigationException {
        String navigateArgs = "Called navigate() with parameter";
        debug(navigateArgs);
        appName = parameter.app;
        NavigationParameter.LaunchMode launchMode = parameter.launchMode;
        NavigatorApp navigatorApp = getNavigatorApp(launchMode);
        Intent intent = navigatorApp.go(parameter);
        invokeIntent(intent);
    }

    private NavigatorApp getNavigatorApp(NavigationParameter.LaunchMode launchMode) throws NavigationException {
        if (!isNetworkAvailable()) {
            logger.error("Network is not available");
        }
        if (shallUse(GOOGLE_MAPS) &&  (launchMode!=null && !launchMode.equals(NavigationParameter.LaunchMode.GEO))) {
            return new GoogleMapsApp(logger);
        } else if (shallUse(CITYMAPPER)) {
            validateGeoCoding();
            return new CityMapperApp(geoCoder, logger);
        } else if (shallUse(UBER)) {
            return new UberApp(logger);
        } else if (shallUse(WAZE)) {
            return new WazeApp(logger);
        } else if (shallUse(YANDEX)) {
            validateGeoCoding();
            return new YandexApp(geoCoder, logger);
        } else if (shallUse(SYGIC)) {
            validateGeoCoding();
            return new SygicApp(geoCoder, logger);
        } else if (shallUse(HERE_MAPS)) {
            validateGeoCoding();
            return new HereMapsApp(logger, geoCoder);
        } else if (shallUse(MOOVIT)) {
            validateGeoCoding();
            return new MoovitApp(logger, geoCoder);
        } else if (shallUse(LYFT)) {
            validateGeoCoding();
            return new LyftApp(logger, geoCoder);
        } else if (shallUse(MAPS_ME)) {
            validateGeoCoding();
            return new MapsMeApp(logger, geoCoder);
        } else if (shallUse(CABIFY)) {
            validateGeoCoding();
            return new CabifyApp(logger, geoCoder);
        } else if (shallUse(BAIDU)) {
            return new BaidoApp(logger);
        } else if (shallUse(GAODE)) {
            validateGeoCoding();
            return new GaodeApp(logger, geoCoder, context);
        } else if (shallUse(TAXIS_99)) {
            validateGeoCoding();
            return new L99TaxisApp(logger, geoCoder);
        } else {
            validateGeoCoding();
            return new GenericAppLauncher(logger, geoCoder, context, launchMode);
        }
    }

    private boolean shallUse( @NonNull String appname){
        if (StringUtil.isEmpty(this.appName)){
            logger.error("Application name is empty");
            return false;
        }else {
            logger.debug("Checking " +appname + " if supports " + appname);
            return appName.equalsIgnoreCase(appname);
        }
    }

    private void validateGeoCoding() throws NavigationException {
        if (geocodingEnabled)
            validateObject(geoCoder, "Google API Key");
    }


    private void validateObject(Object object, String name) throws NavigationException {
        if (object == null) {
            logger.error(name + "Not Provided While Required");
            throw new NavigationException(LOG_TAG + ": null " + name + " is not acceptable to use with this APP");
        }
    }

    /*
     * Utilities
     */
    private void invokeIntent(Intent intent) throws NavigationException {
        try {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }catch (ActivityNotFoundException exception){
            logger.error(intent.getPackage() + " not found");
            throw new NavigationException("Application is not found", exception);
        }

    }

    /**
     * Check if network is available and phone is connected to  a network
     * @return true if network is available and phone is connected to  a network
     */
    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public LaunchNavigator setParameter(NavigationParameter parameter) {
        logger.debug( "Provided parameter: "+ parameter);
        this.parameter = parameter;
        return this;
    }
}