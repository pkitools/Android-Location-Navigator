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

    enum LaunchMode {
        GEO, TURN_BY_TURN;
    }

    enum TransportMode{
        DRIVING("d"),WALKING("w"),BICYCLE("b"), TRANSIT("t");
        String abbreviation;

        TransportMode(String d) {
            abbreviation = d;
        }
    }
}