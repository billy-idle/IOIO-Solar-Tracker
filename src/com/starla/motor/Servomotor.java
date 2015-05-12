package com.starla.motor;

import ioio.lib.api.PwmOutput;
import ioio.lib.api.exception.ConnectionLostException;

/**
 * This class allows an IOIO board to control RC servo motors.
 *
 * @author Guillermo Guzm&aacute;n S&aacute;nchez
 * @version 1, 18/04/2015
 */
class Servomotor {

    final PwmOutput pwmOutput;

    /**
     * @param pwmOutput Contains the information about the frequency, operating mode and the number of the pin.
     * @see PwmOutput
     */
    Servomotor(PwmOutput pwmOutput) {
        this.pwmOutput = pwmOutput;
    }

    /**
     * Writes a value to the servo, controlling the shaft accordingly. On a standard servo, this will set the angle of
     * the shaft (in degrees), moving the shaft to that orientation.
     *
     * @param angle The angle in degrees, between 0° and 180°.
     * @throws ConnectionLostException
     */
    public void write(int angle) throws ConnectionLostException {
        int minPulseWidth = 544;
        int maxPulseWidth = 2400;
        int minAngle = 0;
        int maxAngle = 180;
        pwmOutput.setPulseWidth(map(constraint(angle, minAngle, maxAngle), minAngle, maxAngle, minPulseWidth,
                maxPulseWidth));
    }

    /**
     * Re-maps a number from one range to another. Does not constrain values to within the range.
     *
     * @param value    The number to map.
     * @param fromLow  The lower bound of the value's current range.
     * @param fromHigh The upper bound of the value's current range.
     * @param toLow    The lower bound of the value's target range.
     * @param toHigh   The upper bound of the value's target range.
     * @return The mapped value.
     */
    int map(int value, int fromLow, int fromHigh, int toLow, int toHigh) {
        return (value - fromLow) * (toHigh - toLow) / (fromHigh - fromLow) + toLow;
    }

    /**
     * Constrains a number to be within the range.
     *
     * @param value The number to constrain.
     * @param low   The lower end of the range.
     * @param high  The upper end of the range.
     * @return value, if value is between low and high; low, if value is less than low; high, if value is greater
     * than high.
     */
    int constraint(int value, int low, int high) {
        return (value < low) ? low : ((value > high) ? high : value);
    }

}