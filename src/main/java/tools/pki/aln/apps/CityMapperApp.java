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


/**
 * Navigate to a a location by google app
 */
public class CityMapperApp extends CommonFunctions implements NavigatorApp {

    private final GeoCoder geoCoder;

    public CityMapperApp(GeoCoder geoCoder, ILogger logger) {
        super(logger);
        this.geoCoder = geoCoder;
    }

    @Override
    public Intent go(NavigationParameter params) throws NavigationException {
        String destAddress = null;
        String destLatLon = null;
        String destNickname = params.getDestination().getNickName();
        if (params.getDestination().getType().equals(Position.Type.NAME)) {
            destAddress = getLocationFromName(params.getDestination());
        } else {
            destLatLon = getLocationFromPos(params.getDestination());
        }
        String url = "https://citymapper.com/directions?";
        String logMsg = "Using Citymapper to navigate to";
        if (!StringUtil.isEmpty(destAddress)) {
            url += "&endaddress=" + Uri.encode(destAddress);
            logMsg += " '" + destAddress + "'";
        }
        if (StringUtil.isEmpty(destLatLon)) {
            destLatLon = getLatLanFromAddress(params);
        }
        url += "&endcoord=" + destLatLon;
        logMsg += " [" + destLatLon + "]";
        if (!StringUtil.isEmpty(destNickname)) {
            url += "&endname=" + Uri.encode(destNickname);
            logMsg += " (" + destNickname + ")";
        }

        logMsg += " from";
        url += parsStart(params);
        logMsg += url;
        String extras = parseExtrasToUrl(params);
        if (!StringUtil.isEmpty(extras)) {
            url += extras;
            logMsg += " - extras=" + extras;
        }

        logger.debug(logMsg);
        logger.debug("URI: " + url);
        return new Intent(Intent.ACTION_VIEW, Uri.parse(url));
    }

    private String parsStart(NavigationParameter params) throws NavigationException {
        String url = "";
        String startAddress = null;
        String startLatLon = null;
        String startNickname = params.getStart().getNickName();
        if (params.getStart().getType().equals(Position.Type.NAME)) {
            startAddress = getLocationFromName(params.getStart());
        } else if (params.getStart().getType().equals(Position.Type.POSITION)) {
            startLatLon = getLocationFromPos(params.getStart());
        }
        if (!params.getStart().getType().equals(Position.Type.NONE)) {
            if (!StringUtil.isEmpty(startAddress)) {
                url += "&startaddress=" + Uri.encode(startAddress);
            }
            if (StringUtil.isEmpty(startLatLon)) {
                if (geoCoder == null) {
                    throw new NavigationException("If you do not want to use latitude and longitude, provide API Key for google!");
                }
                startLatLon = getLatLan(params, "Unable to geocode start address to coordinates");
            }
            url += "&startcoord=" + startLatLon;
            if (!StringUtil.isEmpty(startNickname)) {
                url += "&startname=" + Uri.encode(startNickname);
            }
        }
        return url;
    }

    private String getLatLan(NavigationParameter params, String msg) throws NavigationException {
        try {
            return geoCoder.geocodeAddressToLatLon(params.getStart().getAddress());
        } catch (Exception e) {
            throw new NavigationException(msg, e);
        }
    }

    private String getLatLanFromAddress(NavigationParameter params) throws NavigationException {
        if (geoCoder == null) {
            throw new NavigationException("If you do not want to use latitude and longitude, provide API Key for google!");
        }
        return getLatLan(params, "Unable to geocode getDestination() address to coordinates");
    }


}
