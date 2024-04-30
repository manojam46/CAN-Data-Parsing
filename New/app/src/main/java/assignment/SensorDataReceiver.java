package assignment;

import java.text.DecimalFormat;
import java.util.*;

import com.fasterxml.jackson.databind.ObjectMapper;


class SensorDataReceiver {  
    private final static double YAW_RATE_THRESHOLD = 2;
    private final static double STEERING_WHEEL_ANGLE_THRESHOLD = 8;

    private static List<String> DATA_CACHE = new ArrayList<String>();
    private static boolean HEADER_PRINTED = false;

    private static SegmentData CURRENT_PROCESSING_SEGMENT_DATA = new SegmentData("STRAIGHT");
    private static List<SegmentData> SEGMENT_DATAS = new ArrayList<SegmentData>();

    // private static ArrayList<ArrayList<String>> PREV_GPS_COORD = new ArrayList<ArrayList<String>>();
    private static double GPS_LAT = -1;
    private static double GPS_LONG = -1;

    // private static Iterator<SegmentData> SEGMENT_DATA_ITR;
    private static int DATA_0B41_COUNT = 0;

    // Curve Detection
    private static int COUNT = 0;
    private static List<Double> CURRENT_GPS_COORDINATES = new ArrayList<Double>();
    private static Set<String> TRAINED_GPS_COORDINATES = new HashSet<String>();
    private static Set<String> PROCESSED_GPS_START_COORDINATES = new HashSet<String>();
    private static Set<String> PROCESSED_GPS_END_COORDINATES = new HashSet<String>();
    private static Set<String> CHECKED_GPS_COORDIANTES = new HashSet<String>();
    private static String JSON_TO_SEND = "";
    private static String JSON_TO_SEND2 = "";
    private static boolean SEG_PRINT = false;

    private static String mapToJson(Map<String, Object> map) {
        StringBuilder jsonBuilder = new StringBuilder();
        jsonBuilder.append("{");
        boolean isFirst = true;

        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (!isFirst) {
                jsonBuilder.append(",");
            }
            jsonBuilder.append("\"").append(entry.getKey()).append("\":");

            Object value = entry.getValue();
            if (value instanceof String) {
                jsonBuilder.append("\"").append(value).append("\"");
            } else if(value instanceof Map<?, ?>) {
                jsonBuilder.append(mapToJson((Map<String, Object>) value));
            } else {
                jsonBuilder.append(value);
            }

            isFirst = false;
        }

        jsonBuilder.append("}");
        return jsonBuilder.toString();
    }

    // Method is used to detect curve and  straight roads
    private static void ADASDetection(double sensorData, float timeOffset, String identifier){
        if(timeOffset <= 4){
            CHECKED_GPS_COORDIANTES.clear();
            CURRENT_GPS_COORDINATES.clear();
        }

        if(identifier.startsWith("GPS")){
            if(CURRENT_GPS_COORDINATES.size() >= 2 && !CURRENT_GPS_COORDINATES.contains(sensorData)){
                CURRENT_GPS_COORDINATES.clear();
            }

            if(CURRENT_GPS_COORDINATES.size() < 2){
                CURRENT_GPS_COORDINATES.add(sensorData);
            }
        }

        if(CURRENT_GPS_COORDINATES.size() != 2){
            return;
        }

        String currentGPSHash = CURRENT_GPS_COORDINATES.get(0) + ":" + CURRENT_GPS_COORDINATES.get(1);

        // String prevGPSHash = (PREV_GPS_COORDINATES.size() > 0)? PREV_GPS_COORDINATES.get(0) + ":" + PREV_GPS_COORDINATES.get(1) : "";
        if(!TRAINED_GPS_COORDINATES.contains(currentGPSHash) || CHECKED_GPS_COORDIANTES.contains(currentGPSHash)){
            return;
        }

        // PREV_GPS_COORDINATES = CURRENT_GPS_COORDINATES;
        CHECKED_GPS_COORDIANTES.add(currentGPSHash);

        for(int i = 0, j = 1; i <  SEGMENT_DATAS.size(); i++, j++){
            SegmentData itr = SEGMENT_DATAS.get(i);

            if(!itr.conatinsGPSCoordinates(CURRENT_GPS_COORDINATES)){
                continue;
            }

            if(j >= SEGMENT_DATAS.size()){
                // System.out.println("No further segements detected");
                return;
            }

            SegmentData nextSegment = SEGMENT_DATAS.get(j);

            // System.out.println("Next " + nextSegment.getTypeOfData() + " in : " + itr.calculateDistance(CURRENT_GPS_COORDINATES));
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                Map<String, Object> data = itr.print(false);
                data.put("UPCOMMING_SEGMENT", nextSegment.getTypeOfData());
                data.put("UPCOMMING_SEGMENT_CURVE_TYPE", nextSegment.getCurveType());
                data.put("DISTANCE_TO_NEXT", itr.calculateDistance(CURRENT_GPS_COORDINATES));

                // JSON_TO_SEND = objectMapper.writeValueAsString(data);
                JSON_TO_SEND2 = mapToJson(data);
            } catch (Exception e) {
                e.printStackTrace();
            }
            break;
        }
    }

    // Used to train ADAS based on sensor data
    private static void ADASTraining(double sensorData, float timeOffset, String identifier){
        // To store data into cache based on idetifier
        switch (identifier) {
            // Steering wheel angle
            case "0018": 
                CURRENT_PROCESSING_SEGMENT_DATA.setSteeringWheelAngle(sensorData);
            break;

            // Vehicle Speed 
            case "0F7A": 
                CURRENT_PROCESSING_SEGMENT_DATA.setVehicleSpeed(sensorData);
            break;

            // Vehicle Yaw Rate
            // Vehicle Longitudinal Acceleration
            // Vehicle Lateral Acceleration
            case "0B41":
                if(DATA_0B41_COUNT == 0){
                    CURRENT_PROCESSING_SEGMENT_DATA.setYawRate(sensorData);
                    
                    double avgSteerAngleData = CURRENT_PROCESSING_SEGMENT_DATA.getAvgSteeringWheelAngle();
                    double tempSensorData = CURRENT_PROCESSING_SEGMENT_DATA.getAvgYawRate();

                    avgSteerAngleData = (avgSteerAngleData < 0)? avgSteerAngleData * -1 : avgSteerAngleData;
                    tempSensorData = (tempSensorData < 0)? tempSensorData * -1 : tempSensorData;

                    boolean curveDetected = tempSensorData > YAW_RATE_THRESHOLD && avgSteerAngleData > STEERING_WHEEL_ANGLE_THRESHOLD;

                    if ( curveDetected && CURRENT_PROCESSING_SEGMENT_DATA.getTypeOfData().equals("STRAIGHT") ){
                        // String gpsCoordinates = GPS_LAT + ":" + GPS_LONG;
                        
                        // if(TRAINED_GPS_COORDINATES.contains(gpsCoordinates)){
                        //     System.out.println(gpsCoordinates);
                        //     // for
                        //     break;
                        // }

                        // CURRENT_PROCESSING_SEGMENT_DATA.setEndGPSCoordinates(PREV_LAT, PREV_LONG);
                        // System.out.println("Curve detected");
                        CURRENT_PROCESSING_SEGMENT_DATA.calculate();
                        
                        List<Double> coordinates = CURRENT_PROCESSING_SEGMENT_DATA.getStartGPSCoordinates();
                        PROCESSED_GPS_START_COORDINATES.add(coordinates.get(0) + ":" + coordinates.get(1));

                        coordinates = CURRENT_PROCESSING_SEGMENT_DATA.getEndGPSCoordinates();
                        PROCESSED_GPS_END_COORDINATES.add(coordinates.get(0) + ":" + coordinates.get(1));

                        SEGMENT_DATAS.add(CURRENT_PROCESSING_SEGMENT_DATA);

                        SegmentData tempSegmentData = CURRENT_PROCESSING_SEGMENT_DATA;
                        CURRENT_PROCESSING_SEGMENT_DATA = new SegmentData("CURVE");
                        CURRENT_PROCESSING_SEGMENT_DATA.setYawRateListAndAvg(tempSegmentData.getYawRateList(), tempSensorData);
                        CURRENT_PROCESSING_SEGMENT_DATA.setSteeringWheelAngleListAndAvg(
                            tempSegmentData.getToAvgSteeringAngleList(), 
                            tempSegmentData.getAvgSteeringWheelAngle()
                        );
                    }
                    
                    if ( !curveDetected && CURRENT_PROCESSING_SEGMENT_DATA.getTypeOfData().equals("CURVE") ) {
                        // String gpsCoordinates = GPS_LAT + ":" + GPS_LONG;
                        
                        // if(TRAINED_GPS_COORDINATES.contains(gpsCoordinates)){
                        //     System.out.println(gpsCoordinates);

                        //     break;
                        // }

                        // CURRENT_PROCESSING_SEGMENT_DATA.setEndGPSCoordinates(PREV_LAT, PREV_LONG);
                        // System.out.println("Straight detected");
                        CURRENT_PROCESSING_SEGMENT_DATA.calculate();

                        List<Double> coordinates = CURRENT_PROCESSING_SEGMENT_DATA.getStartGPSCoordinates();
                        PROCESSED_GPS_START_COORDINATES.add(coordinates.get(0) + ":" + coordinates.get(1));

                        coordinates = CURRENT_PROCESSING_SEGMENT_DATA.getEndGPSCoordinates();
                        PROCESSED_GPS_END_COORDINATES.add(coordinates.get(0) + ":" + coordinates.get(1));

                        SEGMENT_DATAS.add(CURRENT_PROCESSING_SEGMENT_DATA);
                        
                        SegmentData tempSegmentData = CURRENT_PROCESSING_SEGMENT_DATA;
                        CURRENT_PROCESSING_SEGMENT_DATA = new SegmentData("STRAIGHT");
                        CURRENT_PROCESSING_SEGMENT_DATA.setYawRateListAndAvg(tempSegmentData.getYawRateList(), tempSensorData);
                        CURRENT_PROCESSING_SEGMENT_DATA.setSteeringWheelAngleListAndAvg(
                            tempSegmentData.getToAvgSteeringAngleList(), 
                            tempSegmentData.getAvgSteeringWheelAngle()
                        );
                    }
                }

                DATA_0B41_COUNT++;

                
                
                if(CURRENT_PROCESSING_SEGMENT_DATA.getTypeOfData().equals("STRAIGHT") && DATA_0B41_COUNT == 1){
                    CURRENT_PROCESSING_SEGMENT_DATA.setVehicleAcceleration(sensorData);
                }

                if(CURRENT_PROCESSING_SEGMENT_DATA.getTypeOfData().equals("CURVE") && DATA_0B41_COUNT == 2){
                    CURRENT_PROCESSING_SEGMENT_DATA.setVehicleAcceleration(sensorData);
                }

                if(DATA_0B41_COUNT == 2){
                    DATA_0B41_COUNT = 0;
                }
            break;
        }

        // String GPS coordinates data
        if(identifier.startsWith("GPS")){
            if(identifier.equals("GPS_Latitude")){
                GPS_LAT = sensorData;
                return;
            } else {
                GPS_LONG = sensorData;
            }

            TRAINED_GPS_COORDINATES.add(GPS_LAT + ":" + GPS_LONG);
            CURRENT_PROCESSING_SEGMENT_DATA.setGPSCoordinates(GPS_LAT, GPS_LONG);
        }
    }

    // To recieve sensor data from startSimulation
    public static void receiveSensorValues(double sensorData, float timeOffset, String identifier){
        String format = "| %-12s | %-13s | %-10s | %-7s | %-8s | %-9s | %-19s |";

        // Checks if the header is already printed
        if(!HEADER_PRINTED){
            String fotmatedString = String.format(format, "Current Time", "Vehicle Speed", "SteerAngle", "YawRate", "LatAccel", "LongAccel", "GPS Lat/Long");
            System.out.print(fotmatedString + "\n");
            HEADER_PRINTED = true;
        }

        if(timeOffset > 43285){
            COUNT++;
            if(!SEG_PRINT){
                SEG_PRINT = true;

                String segFormat = "| %-19s | %-19s | %-9s | %-12s | %-11s | %-9s | %-8s | %-8s | %-10s | %-9s | %-12s |%n";
                System.out.println("\n\n");
                System.out.printf(segFormat, "SegStart", "SegEnd", "AvgSpeed", "MaxVehSpeed", "MinVehSpeed", "CurveDir", "MaxAccel", "MinAccel", "StraightLen", "DegCurve", "MaxSteerAngle");
                for(SegmentData itr : SEGMENT_DATAS){
                    String start = itr.getStartGPSCoordinates().get(0) + "/" + itr.getStartGPSCoordinates().get(1);
                    String end = itr.getEndGPSCoordinates().get(0) + "/" + itr.getEndGPSCoordinates().get(1);
                    // Double.parseDouble((new DecimalFormat("#.00")).format(valueWithStepSize - offset))
                    double AvgSpeed = Double.parseDouble((new DecimalFormat("#.00")).format(itr.getAvgVehicleSpeed()));
                    double maxVehSpeed = itr.getMaxVehicleSpeed();
                    double MinVehSpeed = itr.getMinVehicleSpeed();
                    String curveDir = itr.getCurveType();
                    double maxAccel = Double.parseDouble((new DecimalFormat("#.00")).format(itr.getVehicleMaxAcceleration()));
                    double minAccel = Double.parseDouble((new DecimalFormat("#.00")).format(itr.getVehicleMinAcceleration()));
                    double straightLen = Double.parseDouble((new DecimalFormat("#.00")).format(itr.getStraightLength()));
                    double degCurve = Double.parseDouble((new DecimalFormat("#.00")).format(itr.getCurveAngle()));
                    double maxSteerAngle = itr.getMaxSteeringWheelAngle();

                    System.out.printf(segFormat, start, end, AvgSpeed, maxVehSpeed, MinVehSpeed, curveDir, maxAccel, minAccel, straightLen, degCurve, maxSteerAngle);
                }
            }
        }

        if(COUNT > 0){
            ADASDetection(sensorData, timeOffset, identifier);
        } else {
            ADASTraining(sensorData, timeOffset, identifier);
        }

        // Dumps all the data cached till the size is 8 and clears the cache
        if(DATA_CACHE.size() == 8){
            // Creating approx size to backspace the cursor
            if(COUNT <= 0){
                String toDelete = DATA_CACHE.get(0) + DATA_CACHE.get(1) + DATA_CACHE.get(2) + DATA_CACHE.get(3) + DATA_CACHE.get(4) + DATA_CACHE.get(5) + DATA_CACHE.get(6) + " / " + DATA_CACHE.get(7);
    
                for(int i = 0; i < toDelete.length() ; i++){
                    System.out.print("\b");
                }
    
                System.out.printf(
                    "\r" + format, 
                    DATA_CACHE.get(0), 
                    DATA_CACHE.get(1), 
                    DATA_CACHE.get(2), 
                    DATA_CACHE.get(3), 
                    DATA_CACHE.get(4), 
                    DATA_CACHE.get(5), 
                    DATA_CACHE.get(6) + " / " + DATA_CACHE.get(7)
                    
                );
            }

            // Send data to Socket to broadcast the data
            // DATA_CACHE.add(JSON_TO_SEND);
            DATA_CACHE.add(JSON_TO_SEND2);
            SimulationGUI.broadCastData(DATA_CACHE);
            DATA_CACHE.clear();
        }
        
        // String timeoffset in datacache
        if(DATA_CACHE.size() <= 0){
            DATA_CACHE.add(String.valueOf(timeOffset));
        }

        // To store data into cache based on idetifier
        switch (identifier) {
            // Steering wheel angle
            case "0018": 
                DATA_CACHE.add("...");
                DATA_CACHE.add(String.valueOf(sensorData));
                DATA_CACHE.add("...");
                DATA_CACHE.add("...");
                DATA_CACHE.add("...");
            break;

            // Vehicle Speed 
            case "0F7A": 
                DATA_CACHE.add(String.valueOf(sensorData));
                DATA_CACHE.add("...");
                DATA_CACHE.add("...");
                DATA_CACHE.add("...");
                DATA_CACHE.add("...");
            break;

            // Vehicle Yaw Rate
            // Vehicle Longitudinal Acceleration
            // Vehicle Lateral Acceleration
            case "0B41":
                if(DATA_CACHE.size() <= 3){
                    DATA_CACHE.add("...");
                    DATA_CACHE.add("...");
                }
                DATA_CACHE.add(String.valueOf(sensorData));
            break;
        }

        // String GPS coordinates data
        if(identifier.startsWith("GPS")){
            DATA_CACHE.add(String.valueOf(sensorData));
        }
    }
}





// class SensorDataReceiver {  
//     private static List<String> OLD_DATA_CACHE = new ArrayList<String>();
//     private static Map<String, Double> DATA_CACHE = new HashMap<String, Double>();
//     private static boolean HEADER_PRINTED = false;

//     // To recieve sensor data from startSimulation
//     public static void receiveSensorValues(double sensorData, float timeOffset, String identifier){
//         String format = "| %-12s | %-13s | %-10s | %-7s | %-8s | %-9s | %-19s |";

//         // Checks if the header is already printed
//         if(!HEADER_PRINTED){
//             String fotmatedString = String.format(format, "Current Time", "Vehicle Speed", "SteerAngle", "YawRate", "LatAccel", "LongAccel", "GPS Lat/Long");
//             System.out.print(fotmatedString + "\n");
//             HEADER_PRINTED = true;
//         }

//         // Dumps all the data cached till the size is 8 and clears the cache
//         if(OLD_DATA_CACHE.size() == 8){
//             // Creating approx size to backspace the cursor
//             String toDelete = OLD_DATA_CACHE.get(0) + OLD_DATA_CACHE.get(1) + OLD_DATA_CACHE.get(2) + OLD_DATA_CACHE.get(3) + OLD_DATA_CACHE.get(4) + OLD_DATA_CACHE.get(5) + OLD_DATA_CACHE.get(6) + " / " + OLD_DATA_CACHE.get(7);
//             String data = 

//             for(int i = 0; i < toDelete.length() ; i++){
//                 System.out.print("\b");
//             }

//             System.out.printf(
//                 "\r" + format, 
//                 OLD_DATA_CACHE.get(0), 
//                 OLD_DATA_CACHE.get(1), 
//                 OLD_DATA_CACHE.get(2), 
//                 OLD_DATA_CACHE.get(3), 
//                 OLD_DATA_CACHE.get(4), 
//                 OLD_DATA_CACHE.get(5), 
//                 OLD_DATA_CACHE.get(6) + " / " + OLD_DATA_CACHE.get(7)
                
//             );

//             // Send data to Socket to broadcast the data
//             SimulationGUI.broadCastData(OLD_DATA_CACHE);
//             OLD_DATA_CACHE.clear();
//         }
        
//         // String timeoffset in datacache
//         if(OLD_DATA_CACHE.size() <= 0){
//             OLD_DATA_CACHE.add(String.valueOf(timeOffset));
//         }

//         if(!DATA_CACHE.containsKey("currentTime")){
//             DATA_CACHE.put("currentTime", Double.parseDouble(String.valueOf(timeOffset)));
//         }

//         // To store data into cache based on idetifier
//         switch (identifier) {
//             // Steering wheel angle
//             case "0018": 
//                 OLD_DATA_CACHE.add("...");
//                 OLD_DATA_CACHE.add(String.valueOf(sensorData));
//                 OLD_DATA_CACHE.add("...");
//                 OLD_DATA_CACHE.add("...");
//                 OLD_DATA_CACHE.add("...");
//                 DATA_CACHE.put("steeringWheelAngle", sensorData);
//             break;

//             // Vehicle Speed 
//             case "0F7A": 
//                 OLD_DATA_CACHE.add(String.valueOf(sensorData));
//                 OLD_DATA_CACHE.add("...");
//                 OLD_DATA_CACHE.add("...");
//                 OLD_DATA_CACHE.add("...");
//                 OLD_DATA_CACHE.add("...");
//                 DATA_CACHE.put("speed", sensorData);
//             break;

//             // Vehicle Yaw Rate
//             // Vehicle Longitudinal Acceleration
//             // Vehicle Lateral Acceleration
//             case "0B41":
//                 if(OLD_DATA_CACHE.size() <= 3){
//                     OLD_DATA_CACHE.add("...");
//                     OLD_DATA_CACHE.add("...");
//                 }
//                 OLD_DATA_CACHE.add(String.valueOf(sensorData));

//                 if(!DATA_CACHE.containsKey("yawRate")){
//                     DATA_CACHE.put("yawRate", sensorData);
//                 } else if (!DATA_CACHE.containsKey("longitudinalAcceleration")) {
//                     DATA_CACHE.put("longitudinalAcceleration", sensorData);
//                 } else {
//                     DATA_CACHE.put("lateralAcceleration", sensorData);
//                 }
//             break;
//         }

//         // String GPS coordinates data
//         if(identifier.startsWith("GPS")){
//             OLD_DATA_CACHE.add(String.valueOf(sensorData));
//         }

//         if()
//     }
// }
