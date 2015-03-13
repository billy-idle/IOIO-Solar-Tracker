package com.starla;

/**
 * This class computes the Sun's position using the algorithm #5 developed by Roberto Grena.
 * Is based on "Sun_position_algorithms.h" header file.
 *
 * @see <a href="http://www.solaritaly.enea.it/StrSunPosition/SunPositionEn.php">Enea site</a>
 * @version 1, 7/03/14
 * @author Guillermo Guzm&aacute;n S&aacute;nchez
 */
public final class SunPosition{

    private final ObservationPoint observationPoint;
    private double zenith;
    private double azimuth;
    private double elevationAngle;

    private final double W1 = 1.49e-3;
    private final double W2 = 4.31e-3;
    private final double W3 = 1.076e-2;
    private final double W4 = 1.575e-2;
    private final double W5 = 2.152e-2;
    private final double W6 = 3.152e-2;
    private final double W7 = 2.1277e-1;
    private final double [] W = {W1, W2, W3, W4, W5, W6, W7};

    /*	Fourier Coefficients	*/
    private final double A1 = 3.33024e-2;
    private final double A2 = 3.512e-4;
    private final double A3 = 5.2e-6;
    private final double [] A = {A1, A2, A3};

    private final double B1 = -2.0582e-3;
    private final double B2 = -4.07e-5;
    private final double B3 = -9e-7;
    private final double [] B = {B1, B2, B3};

    private final double D1 = 1.27e-5;
    private final double D2 = 1.21e-5;
    private final double D3 = 2.33e-5;
    private final double D4 = 3.49e-5;
    private final double D5 = 2.67e-5;
    private final double D6 = 1.28e-5;
    private final double D7 = 3.14e-5;
    private final double [] D = {D1, D2, D3, D4, D5, D6, D7};

    private final double F1 = -2.337;
    private final double F2 = 3.065;
    private final double F3 = -1.533;
    private final double F4 = -2.358;
    private final double F5 = 0.074;
    private final double F6 = 1.547;
    private final double F7 = -0.488;
    private final double [] F = {F1, F2, F3, F4, F5, F6, F7};

    /**
     * Class constructor, used to compute a single Sun's position.
     *
     * @param observationPoint
     */
    public SunPosition(ObservationPoint observationPoint) {
        this.observationPoint = observationPoint;
        computePosition();
    }

    /**
     * This method computes the Sun's position.
     */
    private void computePosition(){
        double longitude = observationPoint.getLocation().getLongitude();
        double latitude = observationPoint.getLocation().getLatitude();
        double pressure = observationPoint.getWeather().getPressure();
        double temperature = observationPoint.getWeather().getTemperature();
        double t = observationPoint.getTime().getT();
        double te = observationPoint.getTime().getTe();

        double WA = 0.0172019715;
        double wate = WA * te;

        double kSummation = 0.0;
        double iSummation = 0.0;

        for(int k=0; k<A.length; k++){
            kSummation += A[k]*Math.sin((k+1)*wate) + B[k]*Math.cos((k+1)*wate);
        }

        for(int i=0; i<D.length; i++){
            iSummation += D[i]*Math.sin(W[i]*te + F[i]);
        }

        double l0 = 1.7527901;
        double l1 = 1.7202792159e-2;
        double BETA = 2.92e-5;
        double DBETA = -8.23e-5;
        double l = l0 + l1 *te + kSummation + DBETA *Math.sin(wate)*Math.sin(BETA *te) + iSummation;

        double WN = 9.282e-4;
        double v = WN *te - 0.8;
        double deltaLambda = 8.34e-5*Math.sin(v);
        double PI = 3.14159265358979;
        double lambda = l + PI + deltaLambda;
        double epsilon = 4.089567e-1 - 6.19e-9*te + 4.46e-5*Math.cos(v);

        double rightAscension = Math.atan2(Math.sin(lambda)*Math.cos(epsilon), Math.cos(lambda));

        if (rightAscension < 0.0){
            rightAscension += (2.0 * PI);
        }

        double declination = Math.asin(Math.sin(lambda)*Math.sin(epsilon));

        double hourAngle = 1.7528311 + 6.300388099*t + longitude - rightAscension + 0.92*deltaLambda;
        hourAngle = ((hourAngle + PI) % (2.0 * PI)) - PI;

        elevationAngle = Math.asin((Math.sin(latitude)*Math.sin(declination)) + (Math.cos(latitude)*Math.cos(declination)*Math.cos(hourAngle)));
        double parallaxCorrection = -4.26e-5*Math.cos(elevationAngle);
        elevationAngle = elevationAngle + parallaxCorrection;
        azimuth = Math.atan2(Math.sin(hourAngle), Math.cos(hourAngle) * Math.sin(latitude) - Math.tan(declination) * Math.cos(latitude));

        double refractionCorrection = 0.0;

        if (elevationAngle > 0.0){
            refractionCorrection = (0.08422 * pressure) / ((273.0 + temperature) * Math.tan(elevationAngle + 0.003138/(elevationAngle + 0.08919)));
        }

        zenith = PI/2 - elevationAngle - refractionCorrection;
    }

    /**
     *
     * @return  The value of the zenith Ζ (Zíta) in radians.
     */
    public double getZenith() {
        return zenith;
    }

    /**
     *
     * @return  The value of the azimuth Γ (Gamma) in radians.
     */
    public double getAzimuth() {
        return azimuth;
    }

    /**
     *
     * @return  The value of the elevation angle in radians.
     */
    public double getElevationAngle() {
        return elevationAngle;
    }

    /**
     *
     * @return  The ObservationPoint object.
     */
    public ObservationPoint getObservationPoint(){
        return observationPoint;
    }

}
