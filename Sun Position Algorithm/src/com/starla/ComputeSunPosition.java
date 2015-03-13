package com.starla;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoField;

/**
 * Created by Mordecai on 7/03/14.
 */
class ComputeSunPosition {

    public static void main(String[] args) {
        double longitude = 0.21787;
        double latitude = 0.73117;

        double pressure = 1.0;
        double temperature = 25.0;

        int year = 2020;
        int month = 1;
        int day = 25;
        int hour = 4;
        int minute = 0;
        int second = 0;
        int nanoSecond = 0;
        String zone = "Europe/Rome";

        double decimals = 100_000.0;

        ZonedDateTime RomeDateTime = ZonedDateTime.of(year, month, day, hour, minute, second, nanoSecond, ZoneId.of(zone));

        Time time = new Time(RomeDateTime);
        Location location =  new Location(longitude, latitude);
        Weather weather = new Weather(temperature, pressure);
        ObservationPoint observationPoint = new ObservationPoint(time, location, weather);
        SunPosition sunPosition = new SunPosition(observationPoint);

        System.out.println("DateTime\tut\tzenith\tazimuth");
        double ut, zenith, azimuth;

        year = RomeDateTime.getYear();
        month = RomeDateTime.getMonthValue();
        day = RomeDateTime.getDayOfMonth();
        ut = RomeDateTime.withZoneSameInstant(ZoneId.of("UTC")).getLong(ChronoField.MILLI_OF_DAY) / 3_600_000.0;
        zenith = Math.round(sunPosition.getZenith()*decimals)/decimals;
        azimuth = Math.round(sunPosition.getAzimuth()*decimals)/decimals;

        System.out.println(year+"-"+month+"-"+day+"\t"+ut+"\t"+zenith+"\t"+azimuth);
    }
}

