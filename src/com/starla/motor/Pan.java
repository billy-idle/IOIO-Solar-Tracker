package com.starla.motor;

import ioio.lib.api.PwmOutput;
import ioio.lib.api.exception.ConnectionLostException;

/**
 * Created by asus on 12/2/2015.
 */
public final class Pan extends Servomotor {

    private static final int MIN_PULSE_WIDTH = 2400; // 180° --> -90°
    private static final int MAX_PULSE_WIDTH = 544; // 0° --> 90°
    private static final int MIN_ANGLE = -90;
    private static final int MAX_ANGLE = 90;

    public Pan(PwmOutput pwmOutput) {
        super(pwmOutput);
    }

    public void write(int angle) throws ConnectionLostException {
        assert angle >= -90 && angle <= 90 : "Pan angle is out of range";
        pwmOutput.setPulseWidth(map(angle, MIN_ANGLE, MAX_ANGLE, MIN_PULSE_WIDTH, MAX_PULSE_WIDTH));
    }
}
