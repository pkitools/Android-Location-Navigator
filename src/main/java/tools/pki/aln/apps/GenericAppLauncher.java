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

package tools.pki.aln.apps;
//
// Created by  on 2020-01-31.
//

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import tools.pki.aln.GeoCoder;
import tools.pki.aln.ILogger;
import tools.pki.aln.NavigationApplications;
import tools.pki.aln.NavigationException;
import tools.pki.aln.NavigationParameter;
import tools.pki.aln.NavigatorApp;
import tools.pki.aln.Position;

import static tools.pki.aln.NavigationApplications.GOOGLE_MAPS;
import static tools.pki.aln.NavigationParameter.LaunchMode.GEO;

/**
 * Navigate to a a location by google app
 */
public class GenericAppLauncher extends CommonFunctions implements NavigatorApp {

    private final GeoCoder geoCoder;
    private NavigationApplications navigationApplications;

    public GenericAppLauncher(ILogger logger, GeoCoder geoCoder, Context context) {
        super(logger);
        this.geoCoder = geoCoder;
        navigationApplications = NavigationApplications.with(context);
    }

    @Override
    public Intent go(NavigationParameter params) throws NavigationException {
        String appName = params.getApp();

        String logMsg = "Using " + navigationApplications.getAppDisplayName(appName) + " to navigate to ";
        String destLatLon = null;
        String destName = null;
        String dest;

        if (params.getDestination().getType() == Position.Type.NAME) {
            destName = getLocationFromName(params.getDestination());
            try {
                destLatLon = geoCoder.geocodeAddressToLatLon(params.getDestination().getAddress());
            } catch (Exception e) {
                throw new NavigationException( "Unable to geocode destination address to coordinates",e);
            }
            logMsg += destName;
            if (!StringUtil.isEmpty(destLatLon)) {
                logMsg += "[" + destLatLon + "]";
            }

        } else {
            destLatLon = getLocationFromPos(params.getDestination());
            logMsg += "[" + destLatLon + "]";
        }

        if (!StringUtil.isEmpty(destLatLon)) {
            dest = destLatLon;
        } else {
            dest = destName;
        }

        String uri = navigationApplications.GEO_URI + destLatLon + "?q=" + dest;
        if (!StringUtil.isEmpty(params.getDestination().getNickName())) {
            uri += "(" + params.getDestination().getNickName() + ")";
            logMsg += "(" + params.getDestination().getNickName() + ")";
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
                appName =  NavigationApplications.getPackage(GOOGLE_MAPS);
            }
            intent.setPackage(appName);
        }
        return (intent);

    }


}
