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

    public Position build() {
        return new Position(type, address, name, latitude, longitude, nickName);
    }
}