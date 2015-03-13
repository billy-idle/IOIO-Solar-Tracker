package com.starla.motor;

import ioio.lib.api.PwmOutput;
import ioio.lib.api.exception.ConnectionLostException;

/**
 * Created by asus on 12/2/2015.
 */
public abstract class Servomotor {

    final PwmOutput pwmOutput;

    Servomotor(PwmOutput pwmOutput) {
        this.pwmOutput = pwmOutput;
    }

    public void write(int angle) throws ConnectionLostException {
        int minPulseWidth = 544;
        int maxPulseWidth = 2400;
        int minAngle = 0;
        int maxAngle = 180;
        pwmOutput.setPulseWidth(map(angle, minAngle, maxAngle, minPulseWidth, maxPulseWidth));
    }

    public int map(int value, int fromLow, int fromHigh, int toLow, int toHigh) {
        return (value - fromLow) * (toHigh - toLow) / (fromHigh - fromLow) + toLow;
    }

    public void test(int minPulseWidth, int maxPulseWidth) throws ConnectionLostException {
        int min, max;

        if (minPulseWidth < maxPulseWidth) {
            min = minPulseWidth;
            max = maxPulseWidth;
        } else {
            min = maxPulseWidth;
            max = minPulseWidth;
        }

        for (int i = min; i <= max; i++) {
            pwmOutput.setPulseWidth(i);
        }

        for (int i = max; i >= min; i--) {
            pwmOutput.setPulseWidth(i);
        }
    }
}
