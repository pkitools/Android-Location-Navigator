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

public class BaidoApp extends CommonFunctions implements NavigatorApp {

    public BaidoApp(ILogger logger) {
        super(logger);
    }

    @Override
    public Intent go(NavigationParameter params) {
        String start;
        String dest;
        String destNickname = params.getDestination().getNickName();
        String startNickname = params.getStart().getNickName();


        String transportMode;


        String url = "baidumap://map/direction";
        String logMsg = "Using Baidu Maps to navigate";

        String extras = parseExtrasToUrl(params);
        if (StringUtil.isEmpty(extras)) {
            extras = "";
        }

        if (!extras.contains("coord_type=")) {
            extras += "&coord_type=wgs84";
        }

        // Destination
        logMsg += " to";
        if (params.getDestination().getType() == Position.Type.NAME) {
            dest = getLocationFromName(params.getDestination());
            logMsg += dest;
        } else {
            dest = getLocationFromPos(params.getDestination());
            logMsg += " [" + dest + "]";
            if (!StringUtil.isEmpty(destNickname)) {
                dest = "latlng:" + dest + "|name:" + destNickname;
                logMsg += " (" + destNickname + ")";
            }
        }
        url += "?destination=" + dest;

        // Start
        logMsg += " from";
        if (params.getStart().getType() == Position.Type.NONE) {
            logMsg += " Current Location";
        } else {
            if (params.getStart().getType() == Position.Type.NAME) {
                start = getLocationFromName(params.getStart());
                logMsg += start;
            } else {
                start = getLocationFromPos(params.getStart());
                logMsg += " [" + start + "]";
                if (!StringUtil.isEmpty(startNickname)) {
                    start = "latlng:" + start + "|name:" + startNickname;
                    logMsg += " (" + startNickname + ")";
                }
            }
            url += "&origin=" + start;
        }

        // Transport mode
        switch (params.getTransportMode()) {
            case WALKING:
                transportMode = "walking";
                break;
            case BICYCLE:
                transportMode = "riding";
                break;
            case TRANSIT:
                transportMode = "transit";
                break;
            default:
                transportMode = "driving";
                break;
        }

        url += "&mode=" + transportMode;
        logMsg += " by transportMode=" + transportMode;

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
