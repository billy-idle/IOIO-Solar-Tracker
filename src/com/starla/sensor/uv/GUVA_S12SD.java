package com.starla.sensor.uv;

import com.starla.sensor.voltmeter.IOIO_VMeter;
import ioio.lib.api.AnalogInput;
import ioio.lib.api.exception.ConnectionLostException;

/**
 * @version 1, 7/03/14
 * @author Guillermo Guzm&aacute;n S&aacute;nchez
 */
public final class GUVA_S12SD extends IOIO_VMeter {

    /**
     *
     * @param method
     * @param sample
     * @param analogInput
     * @param fromLow
     * @param fromHigh
     * @param toLow
     * @param toHigh
     */
    public GUVA_S12SD(Method method, Sample sample, AnalogInput analogInput, double fromLow, double fromHigh, double toLow, double toHigh) {
        super(method, sample, analogInput, fromLow, fromHigh, toLow, toHigh);
    }

    /**
     *
     * @return
     * @throws ConnectionLostException
     * @throws InterruptedException
     */
    public double getUVIndex() throws ConnectionLostException, InterruptedException {
        return getVolts() / 0.1;
    }

    /**
     *
     * @param uvIndex
     * @return
     */
    public double getUVAPower(double uvIndex) {
        return ((0.1 * uvIndex) / 4.3) * 9.0;
    }
}
