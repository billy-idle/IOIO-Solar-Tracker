package com.starla;

/**
 * Geographical longitude θ (Thíta) and laitude φ (Fi) must be given. The most widely use reference system is the
 * standard geodetic system WGS84, used for example in the GPS positioning system. If this reference system is adopted,
 * the computed local coordinates zenith Ζ (Zíta) and azimuth Γ (Gamma) will be expressed with respect  to the standard
 * vertical south direction given by WGS84.
 *
 * @version 1, 7/03/14
 * @author Guillermo Guzm&aacute;n S&aacute;nchez
 */
public final class Location {

    private final double longitude;
    private final double latitude;

    /**
     * Class constructor.
     *
     * @param longitude Longitude in radians. The value must be between 0 and 2*PI.
     * @param latitude  Latitude in radians. The value must be between -PI/2 and PI/2.
     */
    public Location(double longitude, double latitude) {
        assert longitude >= 0 && longitude <= 2*Math.PI: "Longitude out of range";
        assert latitude >= -Math.PI/2 && latitude <= Math.PI/2 : "Latitude out of range";
        this.longitude = longitude;
        this.latitude = latitude;
    }

    /**
     *
     * @return The latitude value in radians.
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     *
     * @return The longitude value in radians.
     */
    public double getLongitude() {
        return longitude;
    }

}

