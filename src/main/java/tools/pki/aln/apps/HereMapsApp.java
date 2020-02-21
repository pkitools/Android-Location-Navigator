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

public class HereMapsApp extends CommonFunctions implements NavigatorApp {

    private final GeoCoder geoCoder;

    public HereMapsApp(ILogger logger, GeoCoder geoCoder) {
        super(logger);
        this.geoCoder = geoCoder;
    }

    @Override
    public Intent go(NavigationParameter params) throws NavigationException {
        String destAddress;
        String destLatLon;
        String startAddress;
        String startLatLon = null;
        String destNickname = params.getDestination().getNickName();
        String startNickname = params.getStart().getNickName();


        String url = "https://share.here.com/r/";
        String logMsg = "Using HERE Maps to navigate";

        logMsg += " from";
        if (params.getStart().getType() == Position.Type.NONE) {
            url += "mylocation";
            logMsg += " Current Location";

        } else {
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

        debug(logMsg);
        debug("URI: " + url);
        return new Intent(Intent.ACTION_VIEW, Uri.parse(url));
    }
}
