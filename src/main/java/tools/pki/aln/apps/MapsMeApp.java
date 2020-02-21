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

import android.content.Intent;

import tools.pki.aln.GeoCoder;
import tools.pki.aln.ILogger;
import tools.pki.aln.NavigationApplications;
import tools.pki.aln.NavigationException;
import tools.pki.aln.NavigationParameter;
import tools.pki.aln.NavigatorApp;
import tools.pki.aln.Position;

import static tools.pki.aln.NavigationApplications.MAPS_ME;

public class MapsMeApp extends CommonFunctions implements NavigatorApp {

    private final GeoCoder geoCoder;

    public MapsMeApp(ILogger logger, GeoCoder geoCoder) {
        super(logger);
        this.geoCoder = geoCoder;
    }

    @Override
    public Intent go(NavigationParameter params) throws NavigationException {
        String destAddress;
        String destLatLon = null;
        String startAddress;
        String startLatLon = null;

        String packageName = NavigationApplications.getPackage(MAPS_ME);

        Intent intent = new Intent(packageName.concat(".action.BUILD_ROUTE"));
        intent.setPackage(packageName);

        String logMsg = "Using MAPs.ME to navigate";

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

        String[] destPos = splitLatLon(destLatLon);
        intent.putExtra("lat_to", Double.parseDouble(destPos[0]));
        intent.putExtra("lon_to", Double.parseDouble(destPos[1]));

        logMsg += " from";
        if (params.getStart().getType() == Position.Type.NONE) {
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
            intent.putExtra("lat_from", params.getStart().getLatitude());
            intent.putExtra("lon_from", params.getStart().getLongitude());
            logMsg += " [" + startLatLon + "]";
        }

        String transportMode = null;
        switch (params.getTransportMode()) {
            case WALKING:
                transportMode = "pedestrian";
                break;
            case BICYCLE:
                transportMode = "bicycle";
                break;
            case TRANSIT:
                transportMode = "taxi";
                break;
            default:
                transportMode = "vehicle";
                break;
        }

        if (!StringUtil.isEmpty(transportMode)) {
            intent.putExtra("router", transportMode);
            logMsg += " by transportMode=" + transportMode;
        }
        debug(logMsg);
        return (intent);

    }
}
