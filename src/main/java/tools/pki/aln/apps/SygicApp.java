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

import android.content.Intent;
import android.net.Uri;

import tools.pki.aln.GeoCoder;
import tools.pki.aln.ILogger;
import tools.pki.aln.NavigationApplications;
import tools.pki.aln.NavigationException;
import tools.pki.aln.NavigationParameter;
import tools.pki.aln.NavigatorApp;
import tools.pki.aln.Position;

import static tools.pki.aln.NavigationApplications.SYGIC;

public class SygicApp extends CommonFunctions implements NavigatorApp {

    private final GeoCoder geoCoder;

    public SygicApp(GeoCoder geoCoder, ILogger logger) {
        super(logger);
        this.geoCoder = geoCoder;
    }

    @Override
    public Intent go(NavigationParameter params) throws NavigationException {
        String destAddress = null;
        String destLatLon = null;

        String url = NavigationApplications.getPackage(SYGIC) + "://coordinate|";
        String logMsg = "Using Sygic to navigate to";
        String transportMode = null;
        if (params.getTransportMode() == NavigationParameter.TransportMode.WALKING) {
            transportMode = "walk";
        } else {
            transportMode = "drive";
        }

        if (params.getDestination().getType() == Position.Type.NAME) {
            destAddress = getLocationFromName(params.getDestination());
            logMsg += " '" + destAddress + "'";
            try {
                destLatLon = geoCoder.geocodeAddressToLatLon(params.getDestination().getAddress());
            } catch (Exception e) {
                throw new NavigationException("Unable to geocode getDestination() address to coordinates ", e);
            }
        } else {
            destLatLon = getLocationFromPos(params.getDestination());
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

        debug(logMsg);
        debug("URI: " + url);
        return new Intent(Intent.ACTION_VIEW, Uri.parse(url));
    }
}
