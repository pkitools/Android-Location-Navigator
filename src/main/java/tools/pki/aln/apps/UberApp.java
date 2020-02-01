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

import tools.pki.aln.ILogger;
import tools.pki.aln.NavigationParameter;
import tools.pki.aln.NavigatorApp;
import tools.pki.aln.Position;

public class UberApp extends CommonFunctions implements NavigatorApp {

    public UberApp(ILogger logger) {
        super(logger);
    }

    @Override
    public Intent go(NavigationParameter params) {
        String url = getDestUrl(params.getDestination());
        url += getStartUrl(params.getStart());
        String extras = parseExtrasToUrl(params);
        if (!StringUtil.isEmpty(extras)) {
            url += extras;
            logger.debug(" - extras=" + extras);
        }
        logger.debug("URI: " + url);
        return new Intent(Intent.ACTION_VIEW, Uri.parse(url));
    }

    private String getStartUrl(Position position) {
        String url = "";
        String startAddress = null;
        String startLatLon = null;
        if (position.getType() == (Position.Type.NAME)) {
            startAddress = getLocationFromName(position);
        } else if (position.getType() == Position.Type.POSITION) {
            startLatLon = getLocationFromPos(position);
        }
        if (position.getType() != Position.Type.NONE) {
            if (!StringUtil.isEmpty(startAddress)) {
                url += "&pickup[formatted_address]==" + startAddress;
            }
            if (!StringUtil.isEmpty(startLatLon)) {
                url += "&pickup[latitude]=" + position.getLatitude() + "&pickup[longitude]=" + position.getLongitude();
            }
            if (!StringUtil.isEmpty(position.getNickName())) {
                url += "&pickup[nickname]=" + position.getNickName();
            }
        } else {
            url += "&pickup=my_location";
        }
        return url;
    }

    private String getDestUrl(Position destPosition) {
        String url = "uber://?action=setPickup";
        if (destPosition.getType() == (Position.Type.NAME)) {
            String destAddress = getLocationFromName(destPosition);
            if (!StringUtil.isEmpty(destAddress)) {
                url += "&dropoff[formatted_address]=" + destAddress;
            }
        } else {
            String destLatLon = getLocationFromPos(destPosition);
            if (!StringUtil.isEmpty(destLatLon)) {
                url += "&dropoff[latitude]=" + destPosition.getLatitude() + "&dropoff[longitude]=" + destPosition.getLongitude();
            }
        }


        if (!StringUtil.isEmpty(destPosition.getNickName())) {
            url += "&dropoff[nickname]=" + destPosition.getNickName();
        }
        return url;
    }
}
