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

public class WazeApp extends CommonFunctions implements NavigatorApp {

    public WazeApp(ILogger logger) {
        super(logger);
    }

    @Override
    public Intent go(NavigationParameter params) {
        String destAddress = null;
        String destLatLon = null;
        boolean navigate = false;
        String url = "waze://?";
        String logMsg = "Using Waze to navigate to";
        if (params.getDestination()!=null) {
            navigate = true;
            if (params.getDestination().getType() == Position.Type.NAME) {
                destAddress = getLocationFromName(params.getDestination());
            } else {
                destLatLon = getLocationFromPos(params.getDestination());
            }

            if (!StringUtil.isEmpty(destLatLon)) {
                url += "ll=" + destLatLon;
                logMsg += " [" + destLatLon + "]";
            } else {
                url += "q=" + destAddress;
                logMsg += " '" + destAddress + "'";
            }
        }
        if(navigate)
           url += "&navigate=yes";

        logMsg += " from current location";

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
