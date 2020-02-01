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

import java.security.InvalidParameterException;
import java.util.Map;

import tools.pki.aln.ILogger;
import tools.pki.aln.NavigationParameter;
import tools.pki.aln.Position;

public abstract class CommonFunctions {

    ILogger logger;

    String getLocationFromPos(Position position) {
        String location;
        if (StringUtil.isEmpty(position.getLatitude()) || StringUtil.isEmpty(position.getLongitude())) {
            throw new InvalidParameterException("Expected arguments for lat/lon.");
        }
        location = position.getLatitude() + "," + position.getLongitude();
        return location;
    }

    String getLocationFromName(Position position) {

        String name = position.getName();
        if (StringUtil.isEmpty(name) || name.length() == 0) {
            throw new InvalidParameterException("Expected non-empty string argument for place name.");
        }
        return name;
    }

     String[] splitLatLon(String latlon) {
        return latlon.split(",");
    }


     String parseExtrasToUrl(NavigationParameter params) {
        String extras = null;
        if (params.getExtras() != null) {
            for (Map.Entry<String, String> entry : params.getExtras().entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                extras += "&" + key + "=" + value;
            }
            return extras;
        }
        return "";
    }

}
