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

    SegmentData(){
        this.MAX_VEHICLE_SPEED = 0;
        this.MIN_VEHICLE_SPEED = 0;
        this.VEHICLE_SPEEDS = new ArrayList<Double>();
        this.MAX_ACCEL = 0;
        this.MIN_ACCEL = 0;
    }

    /* Setter Methods Starts Here */
    // Set Data type
    public void setDataType(String dataType){
        this.DATA_TYPE = dataType;
    }

    // Sets GPS start coordiantes
    public void setStartGPSCoordinates(double lat, double longi) {
        this.GPS_START_LAT = lat;
        this.GPS_START_LONG = longi;
    }

    // Sets GPS end coordiantes
    public void setEndGPSCoordinates(double lat, double longi) {
        this.GPS_END_LAT = lat;
        this.GPS_END_LONG = longi;
    }

    // Sets vehicle speed and calculates min, max and average speed
    public void setVehicleSpeed(double vehicleSpeed) {
        this.VEHICLE_SPEEDS.add(vehicleSpeed);

        if (vehicleSpeed > this.MAX_VEHICLE_SPEED) {
            this.MAX_VEHICLE_SPEED = vehicleSpeed;
        }

        if (vehicleSpeed < this.MIN_VEHICLE_SPEED) {
            this.MIN_VEHICLE_SPEED = vehicleSpeed;
        }
    }

    // Sets vehicle acceleration
    public void setVehicleAcceleration(double acceleration){
        if(acceleration > this.MAX_ACCEL){
            this.MAX_ACCEL = acceleration;
        }

        if(acceleration < this.MIN_ACCEL){
            this.MIN_ACCEL = acceleration;
        }
    }

    // TODO: Start working on Straight length

    /* Getter Methods Starts Here */
    // Returns String / Curve
    public String getTypeOfData(){
        return this.DATA_TYPE;
    }

    // Returns GPS Start Coordinates
    public List<Double> getStartGPSCoordinates(){
        return Arrays.asList(this.GPS_START_LAT, this.GPS_START_LONG);
    }

    // Returns GPS End Coordinates
    public List<Double> getEndGPSCoordinates(){
        return Arrays.asList(this.GPS_END_LAT, this.GPS_END_LONG);
    }

    // Returns Max Vehicle Speed
    public double getMaxVehicleSpeed(){
        return this.MAX_VEHICLE_SPEED;
    }

    // Returns Min Vehicle Speed
    public double getMinVehicleSpeed(){
        return this.MIN_VEHICLE_SPEED;
    }

    // Returns Avg Vehicle Speed
    public double getAvgVehicleSpeed(){
        return this.AVG_VEHICLE_SPEED;
    }

    // Get Max vehicle acceleration
    public double getVehicleMaxAcceleration(){
        return this.MAX_ACCEL;
    }

    // Get Min vehicle acceleration
    public double getVehicleMinAcceleration(){
        return this.MIN_ACCEL;
    }


    // Calculates the average from list and returns the average 
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
