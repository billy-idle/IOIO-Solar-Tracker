package com.starla.tracker;

import com.starla.motor.Pan;
import com.starla.motor.Tilt;
import com.starla.sensor.ammeter.ACS712;
import com.starla.sensor.uv.GUVA_S12SD;
import com.starla.sensor.voltmeter.IOIOVoltmeter;
import com.starla.sensor.weather.BMP180;
import com.starla.position.*;
import ioio.lib.api.*;
import ioio.lib.api.exception.ConnectionLostException;
import ioio.lib.util.BaseIOIOLooper;
import ioio.lib.util.IOIOLooper;
import ioio.lib.util.pc.IOIOConsoleApp;

import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * @author Guillermo Guzm&aacute;n S&aacute;nchez
 * @version 1, 17/04/2015
 */
public final class Tracker extends IOIOConsoleApp {
    // parameters related with the bmp_180 (pressure and temperature) sensor
    private final int TWI_MODULE = 2;
    // parameters related with the acs712 sensor (ammeter)
    private final int AMMETER_SP_PIN = 31;
    private final int AMMETER_BA_PIN = 42;
    // parameters related with the IOIO-based voltmeter
    private final int VOLTMETER_SP_PIN = 33;
    private final int VOLTMETER_BA_PIN = 38;
    private final int VOLTMETER_UV_PIN = 34; // GUVA_S12SD Sensor
    // parameters related with the servomotors
    private final int SERVO_PWM_FREQUENCY = 100;
    private final int SERVO_PAN_PIN = 10;
    private final int SERVO_TILT_PIN = 11;
    // parameters related with the location
    private final double LONGITUDE = Math.toRadians(284.78); // 75.22°O = -75.22° = 284.78°
    private final double LATITUDE = Math.toRadians(4.44);
    private final double ALTITUDE = 1111.0;

    private DigitalOutput led_;
    private boolean ledOn_ = true;
    private TwiMaster twi;

    private final int BUFFER_SIZE = 1000;
    private AnalogInput analogInputAmmeterSP;
    private AnalogInput analogInputAmmeterBA;

    private AnalogInput analogInputVoltmeterSP;
    private AnalogInput analogInputVoltmeterBA;
    private AnalogInput analogInputVoltmeterUV;

    private ACS712 ammeterSolarPanel;
    private ACS712 ammeterBattery;
    private GUVA_S12SD uvaSensor;
    private IOIOVoltmeter voltmeterSolarPanel;
    private IOIOVoltmeter voltmeterBattery;
    private BMP180 pressureTemperatureSensor;
    private PwmOutput panPwmOutput;
    private PwmOutput tiltPwmOutput;
    private Pan servoPan;
    private Tilt servoTilt;

    private double temperature;
    private double relativePressure;

    private double currentSolarPanel;
    private double voltageSolarPanel;
    private double powerSolarPanel;

    private double currentBattery;
    private double voltageBattery;
    private double powerBattery;

    private double uvIndex;
    private double uvaPower;

    private SunPosition sunPosition;
    private Time time;
    private Location location;
    private double zenith;
    private double azimuth;

    // Boilerplate tracker(). Copy-paste this code into any IOIO application.
    public static void main(String[] args) throws Exception {
        new Tracker().go(args);
    }

    @Override
    protected void run(String[] args) throws IOException {
        while (true) {
        }
    }

    @Override
    public IOIOLooper createIOIOLooper(String connectionType, Object extra) {
        return new BaseIOIOLooper() {
            @Override
            protected void setup() throws ConnectionLostException, InterruptedException {
                led_ = ioio_.openDigitalOutput(IOIO.LED_PIN, true);

                twi = ioio_.openTwiMaster(TWI_MODULE, TwiMaster.Rate.RATE_400KHz, false);
                pressureTemperatureSensor = new BMP180(twi);

                analogInputAmmeterSP = ioio_.openAnalogInput(AMMETER_SP_PIN);
                analogInputAmmeterSP.setBuffer(BUFFER_SIZE);
                ammeterSolarPanel = new ACS712(ACS712.Method.CHAUVENET, ACS712.Sample.ONEHUNDRED,
                        analogInputAmmeterSP, 2.5, 3.3, 0, 0.540);

                analogInputVoltmeterSP = ioio_.openAnalogInput(VOLTMETER_SP_PIN);
                analogInputVoltmeterSP.setBuffer(BUFFER_SIZE);
                voltmeterSolarPanel = new IOIOVoltmeter(IOIOVoltmeter.Method.CHAUVENET,
                        IOIOVoltmeter.Sample.ONEHUNDRED, analogInputVoltmeterSP, 0, 3.29, 0, 10.0);

                analogInputAmmeterBA = ioio_.openAnalogInput(AMMETER_BA_PIN);
                analogInputAmmeterBA.setBuffer(BUFFER_SIZE);
                ammeterBattery = new ACS712(ACS712.Method.CHAUVENET, ACS712.Sample.ONEHUNDRED,
                        analogInputAmmeterBA, 2.5, 3.3, 0, 0.5);

                analogInputVoltmeterBA = ioio_.openAnalogInput(VOLTMETER_BA_PIN);
                analogInputVoltmeterBA.setBuffer(BUFFER_SIZE);
                voltmeterBattery = new IOIOVoltmeter(IOIOVoltmeter.Method.CHAUVENET,
                        IOIOVoltmeter.Sample.ONEHUNDRED, analogInputVoltmeterBA, 0, 3.27, 0, 5.0);

                analogInputVoltmeterUV = ioio_.openAnalogInput(VOLTMETER_UV_PIN);
                analogInputVoltmeterUV.setBuffer(BUFFER_SIZE);
                uvaSensor = new GUVA_S12SD(GUVA_S12SD.Method.CHAUVENET, GUVA_S12SD.Sample.ONEHUNDRED,
                        analogInputVoltmeterUV, 0, 3.23, 0, 6.8);

                panPwmOutput = ioio_.openPwmOutput(new DigitalOutput.Spec(SERVO_PAN_PIN,
                        DigitalOutput.Spec.Mode.OPEN_DRAIN), SERVO_PWM_FREQUENCY);
                servoPan = new Pan(panPwmOutput);
                servoPan.write(0);

                tiltPwmOutput = ioio_.openPwmOutput(new DigitalOutput.Spec(SERVO_TILT_PIN,
                        DigitalOutput.Spec.Mode.OPEN_DRAIN), SERVO_PWM_FREQUENCY);
                servoTilt = new Tilt(tiltPwmOutput);
                servoTilt.write(0);

                location = new Location(LONGITUDE, LATITUDE);

                Thread.sleep(30_000); //Used to orient it towards the geographic south
                System.out.println("ZoneDateTime\t\t\t\t\t\t\t\t\tT.(°C)\tP.(atm)\t\tZenith°\tAzimuth°" +
                        "\tInput(W)\tOutput(W)\tUVIndex\t\tUVAPower (W/m2)");
            }

            @Override
            public void loop() throws ConnectionLostException, InterruptedException {
                led_.write(ledOn_);

                temperature = pressureTemperatureSensor.getTemperature();
                relativePressure = pressureTemperatureSensor.mbToAtm(pressureTemperatureSensor.
                        seaLevel(pressureTemperatureSensor.getPressure(temperature, BMP180.Oversampling.HIGH_RES),
                                ALTITUDE));

                currentSolarPanel = ammeterSolarPanel.getVolts(); // -0.05 ajuste práctico
                voltageSolarPanel = voltmeterSolarPanel.getVolts(); // +0.5 ajuste práctico;
                powerSolarPanel = currentSolarPanel * voltageSolarPanel;

                currentBattery = ammeterBattery.getVolts(); // -0.05 ajuste práctico;
                voltageBattery = voltmeterBattery.getVolts();
                powerBattery = currentBattery * voltageBattery;

                uvIndex = uvaSensor.getUVIndex();
                uvaPower = uvaSensor.getUVAPower(uvIndex);

                time = new Time(ZonedDateTime.now());
                time.computeTime();

                sunPosition = new SunPosition(new ObservationPoint(time, location,
                        new Weather(temperature, relativePressure)));
                sunPosition.computePosition();

                zenith = toDegrees(sunPosition.getZenith());
                azimuth = toDegrees(sunPosition.getAzimuth());

                servoTilt.write((int) zenith);
                servoPan.write((int) azimuth);

                ledOn_ = false;

                print();

                Thread.sleep(1000);
            }
        };
    }

    private void print() {
        System.out.print(time.getZonedDateTime().withZoneSameInstant(ZoneId.systemDefault()) + "\t");
        System.out.print(round(temperature) + "\t");
        System.out.print(round(relativePressure) + "\t\t\t");
        System.out.print(zenith + "\t");
        System.out.print(azimuth + "\t\t");
        System.out.print(round(powerSolarPanel) + "\t\t");
        System.out.print(round(powerBattery) + "\t\t");
        System.out.print(round(uvIndex) + "\t\t\t");
        System.out.print(round(uvaPower * 10)); //(mW/cm2)--(*10)-->(W/m2)
        System.out.println();
    }

    private static double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    private static double toDegrees(double angle) {
        return round(Math.toDegrees(angle));
    }
}