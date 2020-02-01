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
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.jetbrains.annotations.NotNull;

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
import tools.pki.aln.apps.SygicApp;
import tools.pki.aln.apps.UberApp;
import tools.pki.aln.apps.WazeApp;
import tools.pki.aln.apps.YandexApp;

import static tools.pki.aln.NavigationApplications.BAIDU;
import static tools.pki.aln.NavigationApplications.CABIFY;
import static tools.pki.aln.NavigationApplications.CITYMAPPER;
import static tools.pki.aln.NavigationApplications.GAODE;
import static tools.pki.aln.NavigationApplications.GOOGLE_MAPS;
import static tools.pki.aln.NavigationApplications.HERE_MAPS;
import static tools.pki.aln.NavigationApplications.LYFT;
import static tools.pki.aln.NavigationApplications.MAPS_ME;
import static tools.pki.aln.NavigationApplications.MOOVIT;
import static tools.pki.aln.NavigationApplications.SYGIC;
import static tools.pki.aln.NavigationApplications.TAXIS_99;
import static tools.pki.aln.NavigationApplications.UBER;
import static tools.pki.aln.NavigationApplications.WAZE;
import static tools.pki.aln.NavigationApplications.YANDEX;


public class LaunchNavigator {

    /*******************
     * Constructors
     *******************/

    private final Context context;

    /**
     * Lunch application with context and loger
     * @param context it is complsary
     * @param logger if null we will use default android logger otherwise you can implement your own way of loging
     * @throws NavigationException error in parameters
     */
    public LaunchNavigator(Context context, ILogger logger) throws NavigationException {
        this(context, logger, null);
    }


    /**
     * To lunch app with geocoding enabled
     * @param context
     * @param logger
     * @param apiKey
     * @throws NavigationException
     */
    public LaunchNavigator(Context context, ILogger logger, String apiKey) throws NavigationException {
        validateObject(context, "context");
        this.context = context;

        if (apiKey != null) {
            this.geoCoder = new GeoCoder(apiKey);
            geocodingEnabled = true;
        }
        this.logger = (logger);
    }

    private LaunchNavigator(Context context) throws NavigationException {
        this(context, new AndroidLogger(), null);
    }


    public static LaunchNavigator with(Context context) throws NavigationException {
        return new LaunchNavigator(context);
    }


    /**
     * Call this if you don't want API calls for geocoding, Set API Key if you want to enable Geocoding
     * @return
     */
    public LaunchNavigator withoutGeoCoding() {
        this.geocodingEnabled = false;
        return this;
    }

    /**
     * Set GoogleApi Key if you are using places API
     *
     * @param googleApiKey
     * @return
     */

    public LaunchNavigator apiKey(String googleApiKey) {
        this.geoCoder = new GeoCoder(googleApiKey);
        this.geocodingEnabled = true;
        return this;
    }


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


    /*******************
     * Public API
     ******************
     **/

    /**
     * Run a navigation App to go from one point to another
     * @param parameter
     * @
     */
    public void navigate(NavigationParameter parameter) throws NavigationException {
        String navigateArgs = "Called navigate() with parameter";
        logger.debug(navigateArgs);
        String appName = parameter.app;
        NavigationParameter.LaunchMode launchMode = parameter.launchMode;
        NavigatorApp navigatorApp= getNavigatorApp(appName, launchMode);
        Intent intent = navigatorApp.go(parameter);
        invokeIntent(intent);
    }

    @NotNull
    private NavigatorApp getNavigatorApp(String appName, NavigationParameter.LaunchMode launchMode) throws NavigationException {
        NavigatorApp navigatorApp;
        if (!isNetworkAvailable()){
            logger.error("Network is not available");
        }
        if (appName.equals(GOOGLE_MAPS) && !launchMode.equals(NavigationParameter.LaunchMode.GEO)) {
            navigatorApp = new GoogleMapsApp(logger);
        } else if (appName.equals(CITYMAPPER)) {
            validateGeoCoding();
            navigatorApp = new CityMapperApp(geoCoder, logger);
        } else if (appName.equals(UBER)) {
            navigatorApp = new UberApp(logger);
        } else if (appName.equals(WAZE)) {
            navigatorApp = new WazeApp(logger);
        } else if (appName.equals(YANDEX)) {
            validateGeoCoding();
            navigatorApp = new YandexApp(geoCoder, logger);
        } else if (appName.equals(SYGIC)) {
            validateGeoCoding();
            navigatorApp = new SygicApp(geoCoder, logger);
        } else if (appName.equals(HERE_MAPS)) {
            validateGeoCoding();
            navigatorApp = new HereMapsApp(logger, geoCoder);
        } else if (appName.equals(MOOVIT)) {
            validateGeoCoding();
            navigatorApp = new MoovitApp(logger, geoCoder);
        } else if (appName.equals(LYFT)) {
            validateGeoCoding();
            navigatorApp = new LyftApp(logger, geoCoder);
        } else if (appName.equals(MAPS_ME)) {
            validateGeoCoding();
            navigatorApp = new MapsMeApp(logger, geoCoder);
        } else if (appName.equals(CABIFY)) {
            validateGeoCoding();
            navigatorApp = new CabifyApp(logger, geoCoder);
        } else if (appName.equals(BAIDU)) {
            navigatorApp = new BaidoApp(logger);
        } else if (appName.equals(GAODE)) {
            validateGeoCoding();
            navigatorApp = new GaodeApp(logger, geoCoder, context);
        } else if (appName.equals(TAXIS_99)) {
            validateGeoCoding();
            navigatorApp = new L99TaxisApp(logger, geoCoder);
        } else {
            validateGeoCoding();
            navigatorApp = new GenericAppLauncher(logger, geoCoder, context);
        }
        return navigatorApp;
    }

    private void validateGeoCoding() throws NavigationException {
        if (geocodingEnabled)
            validateObject(geoCoder, "Google API Key");
    }


    private void validateObject(Object object, String name) throws NavigationException {
        if (object == null) {
            throw new NavigationException(LOG_TAG + ": null " + name + " is not acceptable to use with this APP");
        }
    }


    /*
     * Utilities
     */
    private void invokeIntent(Intent intent) {
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }


    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}