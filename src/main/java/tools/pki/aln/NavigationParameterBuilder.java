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

public class NavigationParameterBuilder {
    private String app;
    private Position destination;
    private Position start;
    private NavigationParameter.TransportMode transportMode;
    private NavigationParameter.LaunchMode launchMode;
    private Map<String, String> extras;

    public NavigationParameterBuilder setApp(String app) {
        this.app = app;
        return this;
    }

    public NavigationParameterBuilder setDestination(Position destination) {
        this.destination = destination;
        return this;
    }

    public NavigationParameterBuilder setStart(Position start) {
        this.start = start;
        return this;
    }

    public NavigationParameterBuilder setTransportMode(NavigationParameter.TransportMode transportMode) {
        this.transportMode = transportMode;
        return this;
    }

    public NavigationParameterBuilder setLaunchMode(NavigationParameter.LaunchMode launchMode) {
        this.launchMode = launchMode;
        return this;
    }

    public NavigationParameterBuilder setExtras(Map<String, String> extras) {
        this.extras = extras;
        return this;
    }

    public NavigationParameter createNavigationParameter() {
        return new NavigationParameter(app, destination, start, transportMode, launchMode, extras);
    }
}