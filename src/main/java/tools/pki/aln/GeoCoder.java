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

package tools.pki.aln;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.InvalidParameterException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class GeoCoder {


    private OkHttpClient httpClient = new OkHttpClient();
    private String googleApiKey;


    public GeoCoder(String googleApiKey) {
        this.googleApiKey = googleApiKey;
    }

    private JSONObject doGeocode(String query) throws IOException, JSONException {
        if (this.googleApiKey == null) {
            throw new InvalidParameterException("Google API key has not been specified");
        }
        String url = "https://maps.google.com/maps/api/geocode/json?" + query + "&sensor=false&key=" + this.googleApiKey;
        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = httpClient.newCall(request).execute();
        String responseBody = response.body().string();
        JSONObject oResponse = new JSONObject(responseBody);
        if (oResponse.has("error_message")) {
            throw new InvalidParameterException(oResponse.getString("error_message"));
        }
        return ((JSONArray) oResponse.get("results")).getJSONObject(0);
    }

    public String geocodeAddressToLatLon(String address) throws JSONException, IOException {
        String result;
        address = address.replaceAll(" ", "%20");

        JSONObject oResponse = doGeocode("address=" + address);

        double longitude = oResponse
                .getJSONObject("geometry").getJSONObject("location")
                .getDouble("lng");

        double latitude = oResponse
                .getJSONObject("geometry").getJSONObject("location")
                .getDouble("lat");

        result = latitude + "," + longitude;
        return result;
    }

    public String reverseGeocodeLatLonToAddress(String latLon) throws JSONException, IOException {
        String result;
        JSONObject oResponse = doGeocode("latlng=" + latLon);
        result = oResponse.getString("formatted_address");
        return result;
    }


}
