package assignment;

import java.util.*;

public class SegmentData {
    /** Common Segment Data */
    // Type of data Stright | Curve
    private String DATA_TYPE;

    // Start GPS Coordinates
    private double GPS_START_LAT;
    private double GPS_START_LONG;

    // End GPS Coordinates
    private double GPS_END_LAT;
    private double GPS_END_LONG;

    // Vehicle Speed
    private double MAX_VEHICLE_SPEED;
    private double MIN_VEHICLE_SPEED;
    private double AVG_VEHICLE_SPEED;
    private List<Double> VEHICLE_SPEEDS;

    // Vehicle Accelaration
    private double MAX_ACCEL; // Longitudinal -> Straight | Lateral -> Curve
    private double MIN_ACCEL; // Longitudinal -> Straight | Lateral -> Curve

    /* Straight Specify Segment Data */
    private double STRIGHT_LEN;

    /* Curve Specify Segment Data */
    private String CURVE_TYPE;
    private double DEG_CURVE;
    private double MAX_STEER_ANGLE;

    public void calculate() {
        this.AVG_VEHICLE_SPEED = average(VEHICLE_SPEEDS);
    }

    public void setStartGPSCoordinates(double lat, double longi) {
        this.GPS_START_LAT = lat;
        this.GPS_START_LONG = longi;
    }

    public void setEndGPSCoordinates(double lat, double longi) {
        this.GPS_END_LAT = lat;
        this.GPS_END_LONG = longi;
    }

    public void setVehicleSpeed(double vehicleSpeed) {
        this.VEHICLE_SPEEDS.add(vehicleSpeed);

        if (vehicleSpeed > this.MAX_VEHICLE_SPEED) {
            this.MAX_VEHICLE_SPEED = vehicleSpeed;
        }

        if (vehicleSpeed < this.MIN_VEHICLE_SPEED) {
            this.MIN_VEHICLE_SPEED = vehicleSpeed;
        }
    }

    public double getStartGPSCoordinates

    double average(List<Double> listToCalculate) {
        int sum = 0;

        if(listToCalculate.size() <= 0){
            return 0;
        }

        for (double i : listToCalculate) {
            sum += i;
        }

        return (double) sum / listToCalculate.size();
    }
}
