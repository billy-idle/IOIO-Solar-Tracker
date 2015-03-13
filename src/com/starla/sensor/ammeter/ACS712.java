package com.starla.sensor.ammeter;

import com.starla.sensor.voltmeter.IOIO_VMeter;
import ioio.lib.api.AnalogInput;

/**
 * @version 1, 7/03/14
 * @author Guillermo Guzm&aacute;n S&aacute;nchez
 */
public final class ACS712 extends IOIO_VMeter {

    /**
     * Class constructor.
     *
     * @param method
     * @param sample
     * @param analogInput
     * @param fromLow
     * @param fromHigh
     * @param toLow
     * @param toHigh
     */
    public ACS712(Method method, Sample sample, AnalogInput analogInput, double fromLow, double fromHigh, double toLow, double toHigh) {
        super(method, sample, analogInput, fromLow, fromHigh, toLow, toHigh);
    }
}
