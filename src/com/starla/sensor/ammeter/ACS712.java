package com.starla.sensor.ammeter;

import com.starla.sensor.voltmeter.IOIOVoltmeter;
import ioio.lib.api.AnalogInput;

/**
 * This class allows a IOIO board to read the voltage from an ACS712, process it and convert it to amperes.
 * <a href="https://www.sparkfun.com/products/8883">AC712-Current sensor</a>
 *
 * @author Guillermo Guzm&aacute;n S&aacute;nchez
 * @version 1, 18/04/2015
 */

public final class ACS712 extends IOIOVoltmeter {

    /**
     * Class constructor.
     *
     * @param method      Enum type, average method or Chauvenet method.
     * @param sample      Enum type. Allowed values are TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE, TEN, FIFTEEN,
     *                    TWENTY,
     *                    TWENTYFIVE, THIRTY, FORTY, FIFTY, ONEHUNDRED, THREEHUNDRED, FIVEHUNDRED, ONETHOUSAND.
     * @param analogInput A pin use for analog input.
     * @param fromLow     The lower bound of the value's current range.
     * @param fromHigh    The upper bound of the value's current range.
     * @param toLow       The lower bound of the value's target range.
     * @param toHigh      The upper bound of the value's target range.
     * @see IOIOVoltmeter.Sample
     * @see AnalogInput
     */
    public ACS712(Method method, Sample sample, AnalogInput analogInput, double fromLow, double fromHigh, double toLow,
                  double toHigh) {
        super(method, sample, analogInput, fromLow, fromHigh, toLow, toHigh);
    }
}