package com.starla;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoField;

/**
 * In this algorithm, the first step is the time computation. The time t used here is the number of days starting from
 * beginning of the year 2060 (roughly the midpoint of the interval 2010-2110), according to UT.
 *
 * @version 1, 24/03/14
 * @author Guillermo Guzm&aacute;n S&aacute;nchez
 */
public final class Time{

    private ZonedDateTime zonedDateTime;
    private double ut;
    private double t;
    private double te;

    /**
     * Class constructor.
     *
     * @param zonedDateTime
     */
    public Time(ZonedDateTime zonedDateTime){
        this.zonedDateTime = zonedDateTime.withZoneSameInstant(ZoneId.of("UTC"));
        assert zonedDateTime.isAfter(ZonedDateTime.of(2010, 1, 1, 0, 0, 0, 0, ZoneId.of("UTC"))) && zonedDateTime.isBefore(ZonedDateTime.of(2110, 1, 1, 0, 0, 0, 0, ZoneId.of("UTC"))) : "zoneDateTime is out of the validity range";
        computeTime();
    }


    /**
     * This method calculates t and te.
     *
     * t is the number of days starting from the beginning of the year 2060 (roughly the
     * midpoint of the interval 2010-2110), according to UT and dependent on the earth rotation.
     *
     * te is based on TT (terrestrial time) and independent on the earth rotation.
     */
    private void computeTime(){
        int year = zonedDateTime.getYear();
        int month = zonedDateTime.getMonthValue();
        int day = zonedDateTime.getDayOfMonth();
        // Xms*(1s/1000ms/)*(1min/60s)*(1h/60min) = Xh/3_600_000.0
        ut = zonedDateTime.getLong(ChronoField.MILLI_OF_DAY) / 3_600_000.0;

        double deltaT = 96.4 + 0.567 * (year - 2061);

        if (month <= 2){
            month = month + 12;
            year = year - 1;
        }

        t = (int)(365.25*(double)(year-2000)) + (int)(30.6001*(double)(month+1)) - (int)(0.01*(double)year) + day
                + 0.0416667*ut - 21958.0;

        te = t + 1.1574e-5 * deltaT;
    }

    /**
     *
     * @return  The value of UT in decimal format, between 0 and 24.
     */
    public double getUt() {
        return ut;
    }

    /**
     *
     * @return  The number of days starting from the beginning of the year 2060.
     */
    public double getT() {
        return t;
    }

    /**
     *
     * @return  The value of te.
     */
    public double getTe() {
        return te;
    }

    /**
     *
     * @return  zonedDateTime.
     */
    public ZonedDateTime getZonedDateTime() {
        return zonedDateTime;
    }

}

