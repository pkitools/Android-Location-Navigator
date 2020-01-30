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