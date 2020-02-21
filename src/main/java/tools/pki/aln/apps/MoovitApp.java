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

public class MoovitApp extends CommonFunctions implements NavigatorApp {

    private final GeoCoder geoCoder;

    public MoovitApp(ILogger logger, GeoCoder geoCoder) {
        super(logger);
        this.geoCoder = geoCoder;
    }

    @Override
    public Intent go(NavigationParameter params) throws NavigationException {

        String logMsg = "Using Moovit to navigate";
        logMsg += " to";
        String url = parsDestination(params.getDestination());
        logMsg += url;
        logMsg += " from";
        url += parseStart(params.getStart());
        String extras = parseExtrasToUrl(params);
        if (!StringUtil.isEmpty(extras)) {
            url += extras;
            logMsg += " - extras=" + extras;
        }

        debug(logMsg);
        debug("URI: " + url);
        return new Intent(Intent.ACTION_VIEW, Uri.parse(url));
    }

    private String parseStart(Position start) throws NavigationException {
        String startLatLon = "";
        String url = "";
        if (start.getType() == Position.Type.NONE) {

        } else {
            if (start.getType() == Position.Type.NAME) {
                String startAddress = getLocationFromName(start);
                try {
                    startLatLon = geoCoder.geocodeAddressToLatLon(start.getAddress());
                } catch (Exception e) {
                    throw new NavigationException("Unable to geocode start address to coordinates", e);
                }
            } else if (start.getType() == Position.Type.POSITION) {
                startLatLon = getLocationFromPos(start);
            }

            String[] startPos = splitLatLon(startLatLon);
            url += "&orig_lat=" + startPos[0] + "&orig_lon=" + startPos[1];

            if (!StringUtil.isEmpty(start.getNickName())) {
                url += "&orig_name=" + start.getNickName();
            }
        }
        return url;

    }

    private String parsDestination(Position position) throws NavigationException {
        String url = "moovit://directions";
      String dest;
        String destAddress = null;
        String destLatLon = null;
        if (position.getType() == Position.Type.NAME) {
             destAddress = getLocationFromName(position);
            try {
                destLatLon = geoCoder.geocodeAddressToLatLon(position.getAddress());
            } catch (Exception e) {
                throw new NavigationException("Unable to geocode destination address to coordinates ", e);
            }
        } else {
            destLatLon = getLocationFromPos(position);
        }

        if (!StringUtil.isEmpty(destLatLon)) {
            dest = destLatLon;
        } else {
            dest = destAddress;
        }

        String[] destPos = splitLatLon(dest);
        url += "?dest_lat=" + destPos[0] + "&dest_lon=" + destPos[1];

        if (!StringUtil.isEmpty(position.getNickName())) {
            url += "&dest_name=" + position.getNickName();

        }
        return url;
    }
}
