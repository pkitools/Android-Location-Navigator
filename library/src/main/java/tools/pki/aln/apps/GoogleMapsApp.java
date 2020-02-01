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
import tools.pki.aln.NavigationApplications;
import tools.pki.aln.NavigationParameter;
import tools.pki.aln.NavigatorApp;
import tools.pki.aln.Position;

import static tools.pki.aln.NavigationApplications.GOOGLE_MAPS;

/**
 * Navigate to a a location by google app
 */
public class GoogleMapsApp extends CommonFunctions implements NavigatorApp {
    private  static final String MAPS_PROTOCOL = "http://maps.google.com/maps?";
    private  static final String TURN_BY_TURN_PROTOCOL = "google.navigation:";

    public GoogleMapsApp(ILogger logger) {
        super(logger);
    }

    @Override
    public Intent go(NavigationParameter params) {
        String destination;
        String start;

        destination = parsePosition(params.getDestination());

        start = parsePosition(params.getStart());
        String transportMode = params.getTransportMode().getAbbreviation();

        String logMsg = "Using Google Maps to navigate to " + destination;
        String url;

        if (params.getLaunchMode() == NavigationParameter.LaunchMode.TURN_BY_TURN) {
            url = TURN_BY_TURN_PROTOCOL + "q=" + destination;
            if (params.getTransportMode() != null) {
                logMsg += " by transportMode=" + params.getTransportMode().getAbbreviation();
                url += "&mode=" + transportMode;
            }
            logMsg += " in turn-by-turn mode";
        } else {
            url = MAPS_PROTOCOL + "daddr=" + destination;
            if (!StringUtil.isEmpty(start)) {
                logMsg += " from " + start;
                url += "&saddr=" + start;
            } else {
                logMsg += " from current location";
            }
            logMsg += " in maps mode";
        }

        String extras = parseExtrasToUrl(params);
        if (!StringUtil.isEmpty(extras)) {
            url += extras;
            logMsg += " - extras=" + extras;
        }
        logger.debug(logMsg);
        logger.debug("URI: " + url);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        intent.setClassName(NavigationApplications.getPackage(GOOGLE_MAPS), "com.google.android.maps.MapsActivity");
        return intent;

    }

    private String parsePosition(Position position) {
        String destination;
        if (position.getType().equals(Position.Type.POSITION)) {
            destination = getLocationFromPos(position);
        } else {
            destination = getLocationFromName(position);
        }
        return destination;
    }

}
