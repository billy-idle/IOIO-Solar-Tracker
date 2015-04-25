package com.starla.motor;

import ioio.lib.api.PwmOutput;
import ioio.lib.api.exception.ConnectionLostException;

/**
 * This class allows an IOIO board to control an RC servo motor.
 * Describes the azimuth Γ (Gamma) angle within the range -90°(East) and 90°(West), for solar energy applications.
 * <p>
 * This angle is constrained because of the limitations of the servo motor (from 0°(min) to 180°(max)).
 * Valid during the winter solstice and the equinox.
 * <p>
 * <a href="http://www.dfrobot.com/index.php?route=product/product&filter_name=fit0045&product_id=146#.UdyLhth2EUW">
 * DF05BB Tilt/Pan Kit (5kg)</a>
 *
 * @author Guillermo Guzm&aacute;n S&aacute;nchez
 * @version 1, 18/04/2015
 */
public final class Pan extends Servomotor {

    private static final int MIN_PULSE_WIDTH = 2400; // 180° --> -90°
    private static final int MAX_PULSE_WIDTH = 544; // 0° --> 90°
    private static final int MIN_ANGLE = -90;
    private static final int MAX_ANGLE = 90;

    /**
     * @param pwmOutput Contains the information about the frequency, operating mode and the pin number.
     * @see PwmOutput
     */
    public Pan(PwmOutput pwmOutput) {
        super(pwmOutput);
    }

    /**
     * Writes a value to the servo, controlling the shaft accordingly. On a standard servo, this will set the angle of
     * the shaft (in degrees), moving the shaft to that orientation.
     *
     * @param angle The angle in degrees, between -90° and 90°.
     * @throws ConnectionLostException
     */
    public void write(int angle) throws ConnectionLostException {
        assert angle >= -90 && angle <= 90 : "Pan angle is out of range";
        pwmOutput.setPulseWidth(map(constraint(angle, MIN_ANGLE, MAX_ANGLE), MIN_ANGLE, MAX_ANGLE, MIN_PULSE_WIDTH, MAX_PULSE_WIDTH));
    }

}