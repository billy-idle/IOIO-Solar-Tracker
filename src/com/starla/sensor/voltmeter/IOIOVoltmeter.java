package com.starla.sensor.voltmeter;

import ioio.lib.api.AnalogInput;
import ioio.lib.api.exception.ConnectionLostException;

/**
 * This class allows a IOIO board behaves like a voltmeter.
 *
 * @author Guillermo Guzm&aacute;n S&aacute;nchez
 * @version 1, 18/04/2015
 */
public class IOIOVoltmeter {
    private final AnalogInput analogInput;
    private final double fromLow;
    private final double fromHigh;
    private final double toLow;
    private final double toHigh;
    private final Method method;
    private final Sample sample;

    /**
     * Class constructor.
     *
     * @param method      Enum type, average method or Chauvenet method.
     * @param sample      Enum type. Allow values are TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE, TEN, FIFTEEN, TWENTY,
     *                    TWENTYFIVE, THIRTY, FORTY, FIFTY, ONEHUNDRED, THREEHUNDRED, FIVEHUNDRED, ONETHOUSAND.
     * @param analogInput A pin use for analog input.
     * @param fromLow     The lower bound of the value's current range.
     * @param fromHigh    The upper bound of the value's current range.
     * @param toLow       The lower bound of the value's target range.
     * @param toHigh      The upper bound of the value's target range.
     * @see IOIOVoltmeter.Sample
     * @see AnalogInput
     */
    public IOIOVoltmeter(Method method, Sample sample, AnalogInput analogInput, double fromLow, double fromHigh, double toLow, double toHigh) {
        this.method = method;
        this.sample = sample;
        this.analogInput = analogInput;
        this.fromLow = fromLow;
        this.fromHigh = fromHigh;
        this.toLow = toLow;
        this.toHigh = toHigh;
    }

    /**
     * @return The re-mapped value of the voltage.
     * @throws ConnectionLostException
     * @throws InterruptedException
     */
    public double getVolts() throws ConnectionLostException, InterruptedException {
        switch (method) {
            case AVERAGE:
                return averageMethod(sample);
            case CHAUVENET:
                return chauvenetMethod(sample);
            default:
                return 8888.0; // It never happens!
        }
    }

    /**
     * Gets the mapped value of the voltage, using the average method..
     *
     * @param sample The amount of samples.
     * @return The re-mapped value of the voltage.
     * @throws ConnectionLostException
     * @throws InterruptedException
     */
    private double averageMethod(Sample sample) throws ConnectionLostException, InterruptedException {
        double total = 0;
        double volts;
        double value;

        for (int i = 0; i < sample.getSample(); i++) {
            total += analogInput.getVoltageBuffered(); // Read analog input _pin
        }

        if (total == 0) {
            return 0;
        }

        volts = total / sample.getSample(); // Compute the average sample
        value = map(volts, fromLow, fromHigh, toLow, toHigh); // For example: Scales volts from 2400mV-5000mV to amperes 0mA-500mA

        return value;
    }

    /**
     * Gets the mapped value of the voltage, using the Chauvenet's rejection criteria.
     *
     * @param sample The amount of samples.
     * @return The re-mapped value of the voltage.
     * @throws ConnectionLostException
     * @throws InterruptedException
     * @see IOIOVoltmeter.Sample
     */
    private double chauvenetMethod(Sample sample) throws ConnectionLostException, InterruptedException {
        double total = 0.0;
        double value;
        double[] storage = new double[sample.getSample()];
        double reading;
        double average;
        double stdDeviation;
        double ks;
        double Exi2 = 0.0;

        for (int i = 0; i < sample.getSample(); i++) {
            reading = analogInput.getVoltageBuffered(); // Read analog input pin.
            total += reading; // Add the reading to the total.
            Exi2 += Math.pow(reading, 2); // Compute Exi2 (E -> Summation)
            storage[i] = reading; // Stores the reading.
        }

        if (total == 0.0) {
            return 0.0;
        }

        average = total / sample.getSample(); // Compute the average value.
        stdDeviation = Math.sqrt(Exi2 / sample.getSample() - Math.pow(average, 2)); // Compute the standard deviation.
        ks = sample.getCoefficient() * stdDeviation; // compute ks
        total = 0.0;
        int counter = 0;

        for (int i = 0; i < sample.getSample(); i++) {
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
     * Re-maps a number from one range to another. Does not constrain values to within the range.
     *
     * @param value    The number to map.
     * @param fromLow  The lower bound of the value's current range.
     * @param fromHigh The upper bound of the value's current range.
     * @param toLow    The lower bound of the value's target range.
     * @param toHigh   The upper bound of the value's target range.
     * @return The mapped value.
     */
    private double map(double value, double fromLow, double fromHigh, double toLow, double toHigh) {
        return (value - fromLow) * (toHigh - toLow) / (fromHigh - fromLow) + toLow;
    }

    /**
     * Average method or Chauvenet's method.
     */
    public enum Method {
        AVERAGE, CHAUVENET
    }

    /**
     * This enum type represents the concepts illustrated in the document "Metrología estadística".
     *
     * @see <a href="http://www.metrologiaindust.com.ar/Servicios/Capacitacion/Curso2/Material/Diapositivas/1-%20Metrologia%20Estadistica.pdf">Metrología estadística</a>
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
         * @param value The amount of samples.
         * @param coefficient The Chauvenet's coefficient.
         */
        Sample(int value, double coefficient) {
            this.sample = value;
            this.coefficient = coefficient;
        }

        /**
         * @return The amount of samples.
         */
        public int getSample() {
            return sample;
        }

        /**
         * @return The Chauvenet's coefficient.
         */
        public double getCoefficient() {
            return coefficient;
        }
    }
}
