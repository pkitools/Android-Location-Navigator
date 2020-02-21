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

import java.util.Map;

import tools.pki.aln.GeoCoder;
import tools.pki.aln.ILogger;
import tools.pki.aln.NavigationApplications;
import tools.pki.aln.NavigationException;
import tools.pki.aln.NavigationParameter;
import tools.pki.aln.NavigatorApp;
import tools.pki.aln.Position;

import static tools.pki.aln.NavigationApplications.YANDEX;

public class YandexApp extends CommonFunctions implements NavigatorApp {

    private final GeoCoder geoCoder;

    public YandexApp(GeoCoder geoCoder, ILogger logger) {
        super(logger);
        this.geoCoder = geoCoder;
    }

    @Override
    public Intent go(NavigationParameter params) throws NavigationException {
        String destAddress = null;
        String destLatLon = null;
        String startAddress = null;
        String startLatLon = null;


        if (params.getDestination().getType() == Position.Type.NAME) {
            destAddress = getLocationFromName(params.getDestination());
            try {
                destLatLon = geoCoder.geocodeAddressToLatLon(params.getDestination().getAddress());
            } catch (Exception e) {
                throw new NavigationException("Unable to geocode getDestination() address to coordinates ", e);
            }
        } else {
            destLatLon = getLocationFromPos(params.getDestination());
        }

        if (params.getStart().getType() == Position.Type.NAME) {
            startAddress = getLocationFromName(params.getStart());
            try {
                startLatLon = geoCoder.geocodeAddressToLatLon(params.getStart().getAddress());
            } catch (Exception e) {
                throw new NavigationException("Unable to geocode start address to coordinates ", e);
            }
        } else if (params.getStart().getType() == Position.Type.POSITION) {
            startLatLon = getLocationFromPos(params.getStart());
        }

        String pack = NavigationApplications.getPackage(YANDEX);
        Intent intent = new Intent(pack + ".action.BUILD_ROUTE_ON_MAP");
        intent.setPackage(pack);
        String logMsg = "Using Yandex to navigate to";

        String[] parts = splitLatLon(destLatLon);
        intent.putExtra("lat_to", parts[0]);
        intent.putExtra("lon_to", parts[1]);
        logMsg += " [" + destLatLon + "]";

        if (!StringUtil.isEmpty(destAddress)) {
            logMsg += " ('" + destAddress + "')";
        }


        logMsg += " from";
        if (params.getStart().getType() != Position.Type.NONE && startLatLon!=null) {
            parts = splitLatLon(startLatLon);
            intent.putExtra("lat_from", parts[0]);
            intent.putExtra("lon_from", parts[1]);
            logMsg += " [" + startLatLon + "]";
            if (!StringUtil.isEmpty(startAddress)) {
                logMsg += " ('" + startAddress + "')";
            }
        } else {
            logMsg += " current location";
        }



        for (Map.Entry<String, String> entry : params.getExtras().entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            intent.putExtra(key, value);

        }
        debug(logMsg);
        return intent;

    }
}
