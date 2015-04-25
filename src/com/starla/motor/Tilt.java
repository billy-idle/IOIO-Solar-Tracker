package com.starla.motor;

import ioio.lib.api.PwmOutput;
import ioio.lib.api.exception.ConnectionLostException;

/**
 * This class allows an IOIO board to control an RC servo motor.
 * Describes the zenith Ζ (Zíta) angle within the range 90°(Horizon) and 0°(around midday) for solar energy applications.
 * <p>
 * <a href="http://www.dfrobot.com/index.php?route=product/product&filter_name=fit0045&product_id=146#.UdyLhth2EUW">
 * DF05BB Tilt/Pan Kit (5kg)</a>
 *
 * @author Guillermo Guzm&aacute;n S&aacute;nchez
 * @version 1, 18/04/2015
 */
public final class Tilt extends Servomotor {

    private static final int MIN_PULSE_WIDTH = 970; // 0°
    private static final int MAX_PULSE_WIDTH = 1850; // 90°
    private static final int MIN_ANGLE = 0;
    private static final int MAX_ANGLE = 90;

    /**
     * @param pwmOutput Contains the information about the frequency, operating mode and the number of the pin.
     * @see PwmOutput
     */
    public Tilt(PwmOutput pwmOutput) {
        super(pwmOutput);
    }

    /**
     * @param angle The angle in degrees, between 0° and 90°.
     * @throws ConnectionLostException
     */
    public void write(int angle) throws ConnectionLostException {
        assert angle >= 0 && angle <= 90 : "Tilt angle is out of range";
        pwmOutput.setPulseWidth(map(constraint(angle, MIN_ANGLE, MAX_ANGLE), MIN_ANGLE, MAX_ANGLE, MIN_PULSE_WIDTH, MAX_PULSE_WIDTH));
    }

}