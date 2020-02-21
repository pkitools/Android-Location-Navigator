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
import tools.pki.aln.NavigationException;
import tools.pki.aln.NavigationParameter;
import tools.pki.aln.NavigatorApp;
import tools.pki.aln.Position;

public class L99TaxisApp extends CommonFunctions implements NavigatorApp {

    private final GeoCoder geoCoder;

    public L99TaxisApp(ILogger logger, GeoCoder geoCoder) {
        super(logger);
        this.geoCoder = geoCoder;
    }

    @Override
    public Intent go(NavigationParameter params) throws NavigationException {
        String destAddress = null;
        String destLatLon;
        String startAddress = null;
        String startLatLon;
        String destNickname = params.getDestination().getNickName();
        String startNickname = params.getStart().getNickName();

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
        if (params.getStart().getType() == Position.Type.NAME) {
            startAddress = getLocationFromName(params.getStart());
            logMsg += " '" + startAddress + "'";
            try {
                startLatLon = geoCoder.geocodeAddressToLatLon(params.getStart().getAddress());
            } catch (Exception e) {
                throw new NavigationException("Unable to geocode start address to coordinates", e);
            }
        } else if (params.getStart().getType() == Position.Type.POSITION) {
            startLatLon = getLocationFromPos(params.getStart());
        } else {
            throw new NavigationException("start location is a required parameter for 99 Taxi and must be specified");
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

        debug(logMsg);
        debug("URI: " + url);

        Intent intent = new Intent();
        intent.setData(Uri.parse(url));
        return (intent);

    }
}
