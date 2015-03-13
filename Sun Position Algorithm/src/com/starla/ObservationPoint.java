package com.starla;

/**
 * This class is composed by three different class instances: Time, Location and Weather.
 *
 * @version 1, 7/03/14
 * @author Guillermo Guzm&aacute;n S&aacute;nchez
 */
public final class ObservationPoint {

    private final Time time;
    private final Location location;
    private final Weather weather;

    /**
     * Class constructor, used to compute a single Sun's location.
     *
     * @param time
     * @param location
     * @param weather
     */
    public ObservationPoint(Time time, Location location, Weather weather) {
        this.time = time;
        this.location = location;
        this.weather = weather;
    }

    /**
     * @return Time object.
     */
    public Time getTime() {
        return time;
    }

    /**
     * @return Location object.
     */
    public Location getLocation() {
        return location;
    }

    /**
     * @return Weather object.
     */
    public Weather getWeather() {
        return weather;
    }

}

