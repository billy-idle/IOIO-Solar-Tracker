package com.starla;

/**
 * Pressure and temperature are required for the computation of the refraction correction. This is a separate issue not
 * strictly related to the Sun position algorithm, but since it can have a significant effect when the sun is low.
 *
 * @version 1, 7/03/14
 * @author Guillermo Guzm&aacute;n S&aacute;nchez
 */
public final class Weather {

    private double temperature;
    private double pressure;

    /**
     * Class constructor.
     *
     * @param temperature   The temperature value must be given in Celsius degrees (°C).
     * @param pressure      The pressure value must be given in atmospheres (atm).
     */
    public Weather(double temperature, double pressure) {
        assert temperature >= -93.0 && temperature <= 70.7 : "Temperature out of range"; //http://www.rpp.com.pe/2013-12-11-determinan-que-la-antartida-es-el-lugar-mas-frio-de-la-tierra-noticia_654227.html
        assert pressure >= 0.0 && pressure <= 1.07 : "Pressure out of range"; //http://en.wikipedia.org/wiki/Atmospheric_pressure#Records
        this.temperature = temperature;
        this.pressure = pressure;
    }

    /**
     *
     * @return  The temperature value in °C.
     */
    public double getTemperature() {
        return temperature;
    }

    /**
     *
     * @return  The pressure value in atm.
     */
    public double getPressure() {
        return pressure;
    }

}

