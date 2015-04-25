package com.starla.sensor.uv;

        import com.starla.sensor.voltmeter.IOIOVoltmeter;
        import ioio.lib.api.AnalogInput;
        import ioio.lib.api.exception.ConnectionLostException;

/**
 * This class allows a IOIO board to read the voltage from an GUVA_S12SD, process it and get the uv-index and the
 * uv-a power.
 * <p>
 * <a href="http://www.adafruit.com/products/1918">ANALOG UV LIGHT SENSOR BREAKOUT - GUVA-S12SD</a>
 *
 * @author Guillermo Guzm&aacute;n S&aacute;nchez
 * @version 1, 18/04/2015
 */
public final class GUVA_S12SD extends IOIOVoltmeter {

    /**
     * Class constructor.
     *
     * @param method      Enum type, average method or Chauvenet method.
     * @param sample      Enum type. Allow values are TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE, TEN, FIFTEEN, TWENTY,
     *                    TWENTYFIVE, THIRTY, FORTY, FIFTY, ONEHUNDRED, THREEHUNDRED, FIVEHUNDRED, ONETHOUSAND.
     * @param analogInput A pin use for analog input.
     * @param fromLow     The lower bound of the value's current range.
     * @param fromHigh    The upper bound of the value's current range.
     * @param toLow       The lower bound of the value's target range.
     * @param toHigh      The upper bound of the value's target range.
     * @see IOIOVoltmeter.Sample
     * @see AnalogInput
     */
    public GUVA_S12SD(Method method, Sample sample, AnalogInput analogInput, double fromLow, double fromHigh, double toLow, double toHigh) {
        super(method, sample, analogInput, fromLow, fromHigh, toLow, toHigh);
    }

    /**
     * @return The UV Index
     * @throws ConnectionLostException
     * @throws InterruptedException
     * @see <a href="http://www2.epa.gov/sunwise/uv-index-scale">UV Index Scale</a>
     */
    public double getUVIndex() throws ConnectionLostException, InterruptedException {
        return getVolts() / 0.1;
    }

    /**
     * @param uvIndex UV Index
     * @return UV-A Power (mW/m2)
     */
    public double getUVAPower(double uvIndex) {
        return ((0.1 * uvIndex) / 4.3) * 9.0;
    }
}