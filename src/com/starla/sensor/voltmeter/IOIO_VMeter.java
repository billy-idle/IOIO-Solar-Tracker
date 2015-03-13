package com.starla.sensor.voltmeter;

import ioio.lib.api.AnalogInput;
import ioio.lib.api.exception.ConnectionLostException;

/**
 * @version 1, 7/03/14
 * @author Guillermo Guzm&aacute;n S&aacute;nchez
 */
public class IOIO_VMeter {
    private final AnalogInput analogInput;
    private final double fromLow;
    private final double fromHigh;
    private final double toLow;
    private final double toHigh;
    private Method method;
    private Sample sample;

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
    public IOIO_VMeter(Method method, Sample sample, AnalogInput analogInput, double fromLow, double fromHigh, double toLow, double toHigh) {
        this.method = method;
        this.sample = sample;
        this.analogInput = analogInput;
        this.fromLow = fromLow;
        this.fromHigh = fromHigh;
        this.toLow = toLow;
        this.toHigh = toHigh;
    }

    /**
     *
     * @return
     * @throws ConnectionLostException
     * @throws InterruptedException
     */
    public double getVolts() throws ConnectionLostException, InterruptedException {
        switch (method) {
            case AVERAGE:
                return averageMethod(sample.getSample());
            case CHAUVENET:
                return chauvenetMethod(sample.getSample(), sample.getCoefficient());
            default:
                return 8888.0; // It never happens!
        }
    }

    /**
     *
     * @param sample
     * @return
     * @throws ConnectionLostException
     * @throws InterruptedException
     */
    private double averageMethod(int sample) throws ConnectionLostException, InterruptedException {
        double total = 0;
        double volts;
        double value;

        for (int i = 0; i < sample; i++) {
            total += analogInput.getVoltageBuffered(); // Read analog input _pin
        }

        if (total == 0) {
            return 0;
        }

        volts = total / sample; // Compute the average sample
        value = map(volts, fromLow, fromHigh, toLow, toHigh); // For example: Scales volts from 2400mV-5000mV to amperes 0mA-500mA

        return value;
    }

    /**
     *
     * @param sample
     * @param coefficient
     * @return
     * @throws ConnectionLostException
     * @throws InterruptedException
     */
    private double chauvenetMethod(int sample, double coefficient) throws ConnectionLostException, InterruptedException {
        double total = 0.0;
        double value;
        double[] storage = new double[sample];
        double reading;
        double average;
        double stdDeviation;
        double ks;
        double Exi2 = 0.0;

        for (int i = 0; i < sample; i++) {
            reading = analogInput.getVoltageBuffered(); // Read analog input pin.
            total += reading; // Add the reading to the total.
            Exi2 += Math.pow(reading, 2); // Compute Exi2 (E -> Summation)
            storage[i] = reading; // Stores the reading.
        }

        if (total == 0.0) {
            return 0.0;
        }

        average = total / sample; // Compute the average value.
        stdDeviation = Math.sqrt(Exi2 / sample - Math.pow(average, 2)); // Compute the standard deviation.
        ks = coefficient * stdDeviation; // compute ks
        total = 0.0;
        int counter = 0;

        for (int i = 0; i < sample; i++) {
            if ((storage[i] - average) < ks) {
                total += storage[i];
                counter++;
            }
        }

        if (total == 0) {
            return 0;
        }

        average = total / counter;
        value = map(average, fromLow, fromHigh, toLow, toHigh); // For example: Scales volts from 2400mV-5000mV to amperes 0mA-500mA

        return value;
    }

    /**
     *
     * @param value
     * @param fromLow
     * @param fromHigh
     * @param toLow
     * @param toHigh
     * @return
     */
    private double map(double value, double fromLow, double fromHigh, double toLow, double toHigh) {
        return (value - fromLow) * (toHigh - toLow) / (fromHigh - fromLow) + toLow;
    }

    /**
     *
     */
    public enum Method {AVERAGE, CHAUVENET}

    /**
     *
     */
    public enum Sample {
        TWO(2, 1.15),
        THREE(3, 1.35),
        FOUR(4, 1.54),
        FIVE(5, 1.65),
        SIX(6, 1.73),
        SEVEN(7, 1.80),
        EIGHT(8, 1.86),
        NINE(9, 1.92),
        TEN(10, 1.96),
        FIFTEEN(15, 2.13),
        TWENTY(20, 2.24),
        TWENTYFIVE(25, 2.33),
        THIRTY(30, 2.40),
        FORTY(40, 2.48),
        FIFTY(50, 2.57),
        ONEHUNDRED(100, 2.81),
        THREEHUNDRED(300, 3.14),
        FIVEHUNDRED(500, 3.29),
        ONETHOUSAND(1000, 3.48);

        private final int sample;
        private final double coefficient;

        /**
         *
         * @param value
         * @param coefficient
         */
        private Sample(int value, double coefficient) {
            this.sample = value;
            this.coefficient = coefficient;
        }

        /**
         *
         * @return
         */
        public int getSample() {
            return sample;
        }

        /**
         *
         * @return
         */
        public double getCoefficient() {
            return coefficient;
        }
    }
}
