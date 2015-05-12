package com.starla.sensor.voltmeter;

import ioio.lib.api.AnalogInput;
import ioio.lib.api.exception.ConnectionLostException;

import java.util.ArrayList;
import java.util.List;

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
     * @param sample      Enum type. Allow values are TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE, TEN, FIFTEEN,
     *                    TWENTY,
     *                    TWENTYFIVE, THIRTY, FORTY, FIFTY, ONEHUNDRED, THREEHUNDRED, FIVEHUNDRED, ONETHOUSAND.
     * @param analogInput A pin use for analog input.
     * @param fromLow     The lower bound of the value's current range.
     * @param fromHigh    The upper bound of the value's current range.
     * @param toLow       The lower bound of the value's target range.
     * @param toHigh      The upper bound of the value's target range.
     * @see IOIOVoltmeter.Sample
     * @see AnalogInput
     */
    public IOIOVoltmeter(Method method, Sample sample, AnalogInput analogInput, double fromLow, double fromHigh,
                         double toLow, double toHigh) {
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
        int size = sample.getSample();
        double volts;
        double value;

        for (int i = 0; i < size; i++) {
            total += analogInput.getVoltageBuffered(); // Read analog input _pin
        }
        // Compute the average sample
        volts = total / size;
        // For example: Scales volts from 2400mV-5000mV to amperes 0mA-500mA
        value = map(volts, fromLow, fromHigh, toLow, toHigh);

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
        int size = sample.getSample();
        double coefficient = sample.getCoefficient();
        List<Double> storage = new ArrayList<>(size);

        for (int i = 0; i < size; i++) {
            storage.add((double) analogInput.getVoltageBuffered()); // Reads analog input pin and stores the reading.
        }

        double total = storage.
                parallelStream().
                reduce(0.0, Double::sum); // Adds all the elements.

        double Exi2 = storage.
                parallelStream().
                reduce(0.0, (x, y) -> (x + (y * y))); // Computes Exi2 (E -> Summation)

        final double average = total / size; // Computes the average value.
        double stdDeviation = Math.sqrt(Exi2 / size - Math.pow(average, 2)); // Computes the standard deviation.
        double ks = coefficient * stdDeviation; // computes ks

        long count = storage.
                parallelStream().
                filter(r -> ((r - average) < ks)).
                count(); // Returns the count of elements.

        total = storage.
                parallelStream().
                filter(r -> ((r - average) < ks)).
                reduce(0.0, Double::sum); // Adds all the elements.

        double newAverage = total / count;
        // For example: Scales volts from 2400mV-5000mV to amperes 0mA-500mA
        return map(constraint(newAverage, fromLow, fromHigh), fromLow, fromHigh, toLow, toHigh);
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
     * Constrains a number to be within the range.
     *
     * @param value The number to constrain.
     * @param low   The lower end of the range.
     * @param high  The upper end of the range.
     * @return value, if value is between low and high; low, if value is less than low; high, if value is greater
     * than high.
     */
    private double constraint(double value, double low, double high) {
        return (value < low) ? low : ((value > high) ? high : value);
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
     * @see <a href="http://www.metrologiaindust.com.ar/Servicios/Capacitacion/Curso2/Material/Diapositivas/
     * 1-%20Metrologia%20Estadistica.pdf">Metrología estadística</a>
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
         * @param value       The amount of samples.
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