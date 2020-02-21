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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

import tools.pki.aln.GeoCoder;
import tools.pki.aln.ILogger;
import tools.pki.aln.NavigationException;
import tools.pki.aln.NavigationParameter;
import tools.pki.aln.NavigatorApp;
import tools.pki.aln.Position;

public class CabifyApp extends CommonFunctions implements NavigatorApp {
    public static final String STOPS = "stops";
    private final GeoCoder geoCoder;

    public CabifyApp(ILogger logger, GeoCoder geoCoder) {
        super(logger);
        this.geoCoder = geoCoder;
    }

    @Override
    public Intent go(NavigationParameter params) throws NavigationException {
        try {
            String destAddress;
            String destLatLon ;
            String startAddress;
            String startLatLon = null;
            String destNickname = params.getDestination().getNickName();
            String startNickname = params.getStart().getNickName();

            String url = "cabify://cabify/journey?json=";
            String logMsg = "Using Cabify to navigate";
            JSONObject oJson = new JSONObject();

            // Parse destination
            JSONObject oDest = new JSONObject();
            logMsg += " to";

            if (params.getDestination().getType() == Position.Type.NAME) {
                destAddress = getLocationFromName(params.getDestination());
                logMsg += " '" + destAddress + "'";
                destLatLon = getLatLanFromAddress(params.getDestination(), "Unable to geocode destination address to coordinates");
            } else {
                destLatLon = getLocationFromPos(params.getDestination());
            }
            logMsg += " [" + destLatLon + "]";

            JSONObject oDestLoc = new JSONObject();

            oDestLoc.put("latitude", params.getDestination().getLatitude());

            oDestLoc.put("longitude", params.getDestination().getLongitude());
            oDest.put("loc", oDestLoc);

            if (!StringUtil.isEmpty(destNickname)) {
                oDest.put("name", destNickname);
                logMsg += " (" + destNickname + ")";
            }

            // Parse start
            JSONObject oStart = new JSONObject();
            logMsg += " from";

            if (params.getStart().getType() == Position.Type.NONE) {
                logMsg += " Current Location";
                oStart.put("loc", "current");
            } else {
                if (params.getStart().getType() == Position.Type.NAME) {
                    startAddress = getLocationFromName(params.getStart());
                    logMsg += " '" + startAddress + "'";
                    startLatLon = getLatLanFromAddress(params.getStart(), "Unable to geocode start address to coordinates");
                } else if (params.getStart().getType() == Position.Type.POSITION) {
                    startLatLon = getLocationFromPos(params.getStart());
                }
                logMsg += " [" + startLatLon + "]";
                JSONObject oStartLoc = new JSONObject();
                oStartLoc.put("latitude", params.getStart().getLatitude());
                oStartLoc.put("longitude", params.getStart().getLongitude());
                oStart.put("loc", oStartLoc);
            }

            if (!StringUtil.isEmpty(startNickname)) {
                oStart.put("name", startNickname);
                logMsg += " (" + startNickname + ")";
            }

            // Assemble JSON
            JSONArray aStops = new JSONArray();
            aStops.put(oStart);

            Map extras = params.getExtras();
            if (extras != null) {
                logMsg += " - extras=" + extras;
            }

            // Assemble JSON

            aStops.put(oStart);
            aStops.put(oDest);

            if (extras != null && extras.get(STOPS) != null) {
                addStopsFromExtra(aStops, extras);
            }
            oJson.put(STOPS, aStops);

            url += oJson.toString();

            debug(logMsg);
            debug("URI: " + url);
            return new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        } catch (JSONException e) {
            throw new NavigationException("Error in initiating application ", e);
        }
    }

    private void addStopsFromExtra(JSONArray aStops, Map extras) {
        try {
            List<String> stops = (List) extras.get(STOPS);
            for (String obj : stops) {
                aStops.put(obj);
            }
        } catch (Exception err) {
            logger.error("Invalid format of Extras, stop");
        }
    }

    private String getLatLanFromAddress(Position start, String s) throws NavigationException {
        String startLatLon;
        try {
            startLatLon = geoCoder.geocodeAddressToLatLon(start.getAddress());
        } catch (Exception e) {
            throw new NavigationException(s, e);
        }
        return startLatLon;
    }
}
