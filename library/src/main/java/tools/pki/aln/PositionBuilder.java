package tools.pki.aln;

public class PositionBuilder {
    private Position.Type type;
    private String address;
    private String name;
    private String latitude;
    private String longitude;
    private String nickName;

    public PositionBuilder setType(Position.Type type) {
        this.type = type;
        return this;
    }

    public PositionBuilder setAddress(String address) {
        this.address = address;
        return this;
    }

    public PositionBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public PositionBuilder setLatitude(String latitude) {
        this.latitude = latitude;
        return this;
    }

    public PositionBuilder setLongitude(String longitude) {
        this.longitude = longitude;
        return this;
    }

    public PositionBuilder setNickName(String nickName) {
        this.nickName = nickName;
        return this;
    }

    public Position createPosition() {
        return new Position(type, address, name, latitude, longitude, nickName);
    }
}