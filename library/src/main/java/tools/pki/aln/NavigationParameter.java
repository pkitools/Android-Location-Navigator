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

import java.util.Map;


/**
 * Parameter for navigation
 */
public class NavigationParameter {
    protected NavigationParameter(String app, Position destination, Position start, TransportMode transportMode, LaunchMode launchMode, Map<String, String> extras) {
        this.app = app;
        this.destination = destination;
        this.start = start;
        this.transportMode = transportMode;
        this.launchMode = launchMode;
        this.extras = extras;
    }

    public String getApp() {
        return app;
    }

    public void setApp(String app) {
        this.app = app;
    }

    public Position getDestination() {
        return destination;
    }

    public void setDestination(Position destination) {
        this.destination = destination;
    }

    public Position getStart() {
        return start;
    }

    public void setStart(Position start) {
        this.start = start;
    }

    public TransportMode getTransportMode() {
        return transportMode;
    }

    public void setTransportMode(TransportMode transportMode) {
        this.transportMode = transportMode;
    }

    public LaunchMode getLaunchMode() {
        return launchMode;
    }

    public void setLaunchMode(LaunchMode launchMode) {
        this.launchMode = launchMode;
    }

    public Map<String, String> getExtras() {
        return extras;
    }

    public void setExtras(Map<String, String> extras) {
        this.extras = extras;
    }

    String app;
    Position destination;
    Position start;
    TransportMode transportMode;
    LaunchMode launchMode;
    Map<String, String> extras;

    public enum LaunchMode {
        GEO,
        TURN_BY_TURN;
    }

    public enum TransportMode{
        DRIVING("d"),WALKING("w"),BICYCLE("b"), TRANSIT("t");
        String abbreviation;
        public String getAbbreviation() {
            return abbreviation;
        }

        TransportMode(String d) {
            abbreviation = d;
        }
    }
}