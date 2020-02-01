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

public class GaodeApp extends CommonFunctions implements NavigatorApp {

    private final GeoCoder geoCoder;
    private final Context context;

    public GaodeApp(ILogger logger, GeoCoder geoCoder, Context context) {
        super(logger);
        this.geoCoder = geoCoder;
        this.context = context;
    }

    @Override
    public Intent go(NavigationParameter params) throws NavigationException {
        String destAddress = null;
        String destLatLon = null;
        String startAddress = null;
        String startLatLon = null;
        String destNickname = params.getDestination().getNickName();
        String startNickname = params.getStart().getNickName();

        String transportMode;


        String url = "amapuri://route/plan/?";
        String logMsg = "Using Gaode Maps to navigate";

        String extras = parseExtrasToUrl(params);
        if (StringUtil.isEmpty(extras)) {
            extras = "";
        }

        if (!extras.contains("sourceApplication=")) {
            extras += "&sourceApplication=" + Uri.encode(NavigationApplications.with(context).getThisAppName());
        }

        // Destination
        logMsg += " to";
        if (params.getDestination().getType() == Position.Type.NAME) {
            destAddress = getLocationFromName(params.getDestination());
            logMsg += " '" + destAddress + "'";
            try {
                destLatLon = geoCoder.geocodeAddressToLatLon(params.getDestination().getAddress());
            } catch (Exception e) {
                throw new NavigationException("Unable to geocode destination address to coordinates", e);
            }
        } else {
            destLatLon = getLocationFromPos(params.getDestination());
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
        if (params.getStart().getType() == Position.Type.NONE) {
            logMsg += " Current Location";
        } else {
            if (params.getStart().getType() == Position.Type.NAME) {
                startAddress = getLocationFromName(params.getStart());
                logMsg += " '" + startAddress + "'";
                try {
                    startLatLon =geoCoder.geocodeAddressToLatLon(params.getStart().getAddress());
                } catch (Exception e) {
                    startLatLon = null;
                }
            } else {
                startLatLon = getLocationFromPos(params.getStart());
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
        switch (params.getTransportMode()) {
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
        return (intent);

    }
}
