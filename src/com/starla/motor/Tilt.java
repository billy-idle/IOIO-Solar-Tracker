package com.starla.motor;

import ioio.lib.api.PwmOutput;
import ioio.lib.api.exception.ConnectionLostException;

/**
  * @version 1, 7/03/14
  * @author Guillermo Guzm&aacute;n S&aacute;nchez
 */
public final class Tilt extends Servomotor {

    private static final int MIN_PULSE_WIDTH = 970; // 0°
    private static final int MAX_PULSE_WIDTH = 1850; // 90°
    private static final int MIN_ANGLE = 0;
    private static final int MAX_ANGLE = 90;

    public Tilt(PwmOutput pwmOutput) {
        super(pwmOutput);
    }

    public void write(int angle) throws ConnectionLostException {
        assert angle >= 0 && angle <= 90 : "Tilt angle is out of range";
        pwmOutput.setPulseWidth(map(angle, MIN_ANGLE, MAX_ANGLE, MIN_PULSE_WIDTH, MAX_PULSE_WIDTH));
    }

}
