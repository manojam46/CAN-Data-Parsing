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

    // GPS Coordinates
    public LinkedHashSet<List<Double>> GPS_COORDINATES;

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
    private List<Double> STEERING_WHEEL_ANGLES;
    private double AVG_STEERING_WHEEL_ANGLE;
    private List<Double> TO_AVG_STEERING_WHEEL_ANGLES;
    private final int STEERING_ANGLE_AVG_CALC_THRESHOLD = 8;

    /* Yaw rate */
    private final int YAW_RATE_AVG_CALC_THRESHOLD = 11;
    private List<Double> YAW_RATE;
    private double AVG_YAW_RATE;

    public void calculate(){
        this.AVG_VEHICLE_SPEED = average(VEHICLE_SPEEDS);

        // Set Start GPS Coordinates
        if(this.GPS_COORDINATES.size() > 0){
            List<List<Double>> coordinates = List.copyOf(this.GPS_COORDINATES);
            double gpsStartlat = coordinates.get(0).get(0);
            double gpsStartLong = coordinates.get(0).get(1);
            double gpsEndLat = coordinates.get(this.GPS_COORDINATES.size() - 1).get(0);
            double gpsEndLong = coordinates.get(this.GPS_COORDINATES.size() - 1).get(1);
    
            this.setStartGPSCoordinates(gpsStartlat, gpsStartLong);
            this.setEndGPSCoordinates(gpsEndLat, gpsEndLong);
            // System.out.println("****** START *******");
            // System.out.println(gpsStartlat + " " + gpsStartLong);
            // System.out.println("****** END *******");
            // System.out.println(gpsEndLat + " " + gpsEndLong + "\n");
        }

        // Claculating maxmum steering wheel angle
        List<Double> maxSteerAngle = new ArrayList<Double>();
        List<Double> minSteerAngle = new ArrayList<Double>();
        for (Double steerAngle : this.STEERING_WHEEL_ANGLES) {
            if(steerAngle > 0){
                maxSteerAngle.add(steerAngle);
                continue;
            }

            minSteerAngle.add(steerAngle);
        }
        if(maxSteerAngle.size() > minSteerAngle.size()){
            this.MAX_STEER_ANGLE = Collections.max(maxSteerAngle);
        } else {
            this.MAX_STEER_ANGLE = Collections.min(minSteerAngle);
        }

        // Processing curve type
        if(this.DATA_TYPE.equals("CURVE")){
            this.CURVE_TYPE = (this.MAX_STEER_ANGLE > 0)? "RIGHT" : "LEFT";
            this.DEG_CURVE = detectCurveAngle(this.GPS_COORDINATES, this.YAW_RATE);
        }

        // Calculate straight lenght based on gps start, gps end and by avg speed and only if the type of data is straight
        if(this.DATA_TYPE.equals("STRAIGHT")){
            this.STRIGHT_LEN = calculateDistance(this.GPS_COORDINATES, this.AVG_VEHICLE_SPEED);
        }
    }

    SegmentData(String dataType){
        this.GPS_START_LAT      = -1;
        this.GPS_START_LONG     = -1;

        this.GPS_END_LAT        = -1;
        this.GPS_END_LONG       = -1;

        this.MAX_VEHICLE_SPEED  = 0;
        this.MIN_VEHICLE_SPEED  = 0;

        this.VEHICLE_SPEEDS     = new ArrayList<Double>();

        this.MAX_ACCEL          = 0;
        this.MIN_ACCEL          = 0;
        
        this.MAX_STEER_ANGLE    = 0;

        this.GPS_COORDINATES    = new LinkedHashSet<List<Double>>();
        // this.GPS_COORDINATES    = new ArrayList<List<Double>>();
        // this.GPS_COORDINATES    = new Double[10][];

        this.DATA_TYPE          = dataType;

        this.YAW_RATE           = new ArrayList<Double>();

        TO_AVG_STEERING_WHEEL_ANGLES = new ArrayList<Double>();
        STEERING_WHEEL_ANGLES        = new ArrayList<Double>();   
    }

    /* Setter Methods Starts Here */
    // Set Data type
    public void setDataType(String dataType){
        this.DATA_TYPE = dataType;
    }

    // Sets GPS start coordiantes
    private void setStartGPSCoordinates(double lat, double longi) {
        this.GPS_START_LAT = lat;
        this.GPS_START_LONG = longi;
    }

    // Sets GPS end coordiantes
    private void setEndGPSCoordinates(double lat, double longi) {
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

    // Sets steering wheel angle
    public void setSteeringWheelAngle(double steeringWheelAngle){
        this.STEERING_WHEEL_ANGLES.add(steeringWheelAngle);
        this.TO_AVG_STEERING_WHEEL_ANGLES.add(steeringWheelAngle);

        if(this.TO_AVG_STEERING_WHEEL_ANGLES.size() > STEERING_ANGLE_AVG_CALC_THRESHOLD){
            this.AVG_STEERING_WHEEL_ANGLE = average(this.TO_AVG_STEERING_WHEEL_ANGLES);
            this.TO_AVG_STEERING_WHEEL_ANGLES.clear();
        }
    }

    public void setSteeringWheelAngleListAndAvg(List<Double> steeringWheelAngles, double avgSteeeringWheelAngle){
        this.TO_AVG_STEERING_WHEEL_ANGLES = steeringWheelAngles;
        this.AVG_STEERING_WHEEL_ANGLE = avgSteeeringWheelAngle;
    }

    // Set yaw rate
    public void setYawRate(double yawRate){
        this.YAW_RATE.add(yawRate);

        if(this.YAW_RATE.size() > YAW_RATE_AVG_CALC_THRESHOLD){
            this.AVG_YAW_RATE = average(this.YAW_RATE);
            this.YAW_RATE.clear();
        }
    }

    // Set yaw rate list for averaging
    public void setYawRateListAndAvg(List<Double> yawRate, double avgYawRate){
        this.YAW_RATE = yawRate;
        this.AVG_YAW_RATE = avgYawRate;
    }

    // Sets new GPS Coordinates
    public void setGPSCoordinates(double lat, double longi){
        if(lat == 0.0 || longi == 0.0){
            return;
        }

        this.GPS_COORDINATES.add(new ArrayList<Double>(){{
            add(lat);
            add(longi);
        }});

        // System.out.println("\n\n***************** START **********************");
        // for(List<Double> itr: this.GPS_COORDINATES){
        //     System.out.print(itr.get(0));
        //     System.out.println(itr.get(1));
        // }
        // System.out.println("***************** END **********************\n");
    }

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

    // Get the straight length
    public double getStraightLength(){
        return this.STRIGHT_LEN;
    }

    // Get Max Sterring WHeel Angle
    public double getMaxSteeringWheelAngle(){
        return this.MAX_STEER_ANGLE;
    }

    // Get Min Sterring WHeel Angle
    public double getAvgSteeringWheelAngle(){
        return this.AVG_STEERING_WHEEL_ANGLE;
    }

    // Get type of curve
    public String getCurveType(){
        return this.CURVE_TYPE;
    }

    // Get avg yaw rate
    public double getAvgYawRate(){
        return this.AVG_YAW_RATE;
    }

    // Get Yaw rate list
    public List<Double> getYawRateList(){
        return this.YAW_RATE;
    }

    // Get Steering wheel list
    public List<Double> getToAvgSteeringAngleList(){
        return this.TO_AVG_STEERING_WHEEL_ANGLES;
    }

    public double getCurveAngle(){
        return this.DEG_CURVE;
    }

    public Map<String, Object> print(boolean print){
        if(print){
            System.out.println("\n\n********* " + this.DATA_TYPE + " SEGMENT **********");
            System.out.println("GPS SEGMENT START   : " + this.GPS_START_LAT + " : " + this.GPS_START_LONG);
            System.out.println("GPS SEGEMTN END     : " + this.GPS_END_LAT + " : " + this.GPS_END_LONG);
            System.out.println("AVG VEHICLE SPEED   : " + this.AVG_VEHICLE_SPEED);
            
            if(this.DATA_TYPE.equals("STRAIGHT")){
                System.out.println("MAX VEHICLE SPEED   : " + this.MAX_VEHICLE_SPEED);
                System.out.println("MIN VEHICLE SPEED   : " + this.MIN_VEHICLE_SPEED);
            } else {
                System.out.println("CUREVE DIRECTION    : " + this.CURVE_TYPE);
            }
            
            System.out.println("MAX ACCELERATION    : " + this.MAX_ACCEL);
            System.out.println("MIN ACCELERATION    : " + this.MIN_ACCEL);
            
            if(this.DATA_TYPE.equals("STRAIGHT")){
                System.out.println("STRAIGHT LENGTH     : " + this.STRIGHT_LEN);
            } else {
                System.out.println("DEGRE CURVE         : " + this.DEG_CURVE);
                System.out.println("MAX STEERING ANGLE  : " + this.MAX_STEER_ANGLE);
            }
    
            System.out.println("**************************************************");
        }

        return new HashMap<String, Object>(){{
            put("DATA_TYPE", DATA_TYPE);

            put("GPS_SEGMENT_START", Map.of(
                "LAT", GPS_START_LAT,
                "LONG", GPS_START_LONG
            ));

            put("GPS_SEGMENT_END", Map.of(
                "LAT", GPS_END_LAT,
                "LONG", GPS_END_LONG
            ));

            put("AVG_VEHICLE_SPEED", AVG_VEHICLE_SPEED);
            put("MAX_VEHICLE_SPEED", MAX_VEHICLE_SPEED);
            put("MIN_VEHICLE_SPEED", MIN_VEHICLE_SPEED);

            put("CURVE_TYPE", CURVE_TYPE);
            put("MAX_ACCEL", MAX_ACCEL);
            put("MIN_ACCEL", MIN_ACCEL);
            put("STRIGHT_LEN", STRIGHT_LEN);
            put("DEG_CURVE", DEG_CURVE);
            put("MAX_STEER_ANGLE", MAX_STEER_ANGLE);
        }};
    }

    public boolean matchesGPSStartSegment(double lat, double longi){
        return this.GPS_START_LAT == lat && this.GPS_START_LONG == longi;
    }

    public boolean conatinsGPSCoordinates(List<Double> coordinates){
        return this.GPS_COORDINATES.contains(coordinates);
    }

    public double calculateDistance(List<Double> coordinates){
        if(!this.GPS_COORDINATES.contains(coordinates)){
            return -1;
        }

        List<List<Double>> coordinatesList = new ArrayList<>(this.GPS_COORDINATES); //List.copyOf(this.GPS_COORDINATES);
        // List<List<Double>> coordinatesList = new ArrayList<>();
        // for (List<Double> list : this.GPS_COORDINATES) {
        //     coordinatesList.add(new ArrayList<>(list));
        // }

        int index = coordinatesList.indexOf(coordinates);

        try{
            return calculateDistance(new LinkedHashSet<List<Double>>(coordinatesList.subList(index, coordinatesList.size())), getAvgVehicleSpeed());
        } catch( Exception e ){
            e.printStackTrace();
        }

        return -1;
    }

    // https://community.fabric.microsoft.com/t5/Desktop/How-to-calculate-lat-long-distance/td-p/1488227#:~:text=You%20need%20Latitude%20and%20Longitude,is%20Earth%20radius%20in%20km.)
    private static double calculateDistance(LinkedHashSet<List<Double>> coordinatesSet, double avgSpeed) {
        double totalDistance = 0.0;
        for (int i = 0; i < coordinatesSet.size() - 1; i++) {
            List<List<Double>> coordinates = List.copyOf(coordinatesSet);
            GpsCoordinate start = new GpsCoordinate(coordinates.get(i).get(0), coordinates.get(i).get(1));
            GpsCoordinate end = new GpsCoordinate(coordinates.get(i + 1).get(0), coordinates.get(i + 1).get(1));
            double distanceBetweenPoints = calculateDistanceBetweenPoints(start, end);
            totalDistance += distanceBetweenPoints;
        }
        
        return totalDistance * 1000;
    }

    private static double calculateDistanceBetweenPoints(GpsCoordinate start, GpsCoordinate end) {
        double earthRadius = 6371; // Radius of the Earth in kilometers
        double lat1 = Math.toRadians(start.latitude);
        double lon1 = Math.toRadians(start.longitude);
        double lat2 = Math.toRadians(end.latitude);
        double lon2 = Math.toRadians(end.longitude);

        double dLat = lat2 - lat1;
        double dLon = lon2 - lon1;

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(lat1) * Math.cos(lat2)
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return earthRadius * c; // Distance in kilometers
    }

    // Calculates the average from list and returns the average 
    private double average(List<Double> listToCalculate) {
        int sum = 0;

        if(listToCalculate.size() <= 0){
            return 0;
        }

        for (double i : listToCalculate) {
            sum += i;
        }

        return (double) sum / listToCalculate.size();
    }

    public double detectCurveAngle(LinkedHashSet<List<Double>> gpsCoordinates, List<Double> yawRates) {
        int dataSize = Math.min(gpsCoordinates.size(), yawRates.size());
        double totalChangeInDirection = 0.0;
        List<Double> prevCoord = null;
        for (List<Double> coord : gpsCoordinates) {
            if (prevCoord != null) {
                double dx = coord.get(0) - prevCoord.get(0);
                double dy = coord.get(1) - prevCoord.get(1);
                double directionAngle = Math.toDegrees(Math.atan2(dy, dx));
                double normalizedYawRate = this.AVG_YAW_RATE % 360; //yawRates.get(0) % 360;
                if (normalizedYawRate > 180) {
                    normalizedYawRate -= 360;
                }
                totalChangeInDirection += Math.abs(directionAngle - normalizedYawRate);
            }
            prevCoord = coord;
        }
        double curveAngle = totalChangeInDirection / (dataSize - 1);
        return curveAngle;
    }

    private static class GpsCoordinate {
        double latitude;
        double longitude;

        public GpsCoordinate(double latitude, double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }
    }
}
