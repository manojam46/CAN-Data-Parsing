package assignment;

import java.util.*;

class SensorDataReceiver {  
    private static List<String> DATA_CACHE = new ArrayList<String>();
    private static boolean HEADER_PRINTED = false;

    public static void receiveSensorValues(double sensorData, float timeOffset, String identifier){
        if(!HEADER_PRINTED){
            System.out.printf("| %-12s | %-13s | %-10s | %-7s | %-8s | %-9s | %-19s | %n", "Current Time", "Vehicle Speed", "SteerAngle", "YawRate", "LatAccel", "LongAccel", "GPS Lat/Long");
            HEADER_PRINTED = true;
        }

        if(DATA_CACHE.size() == 8){
            System.out.printf(
                "| %-12s | %-13s | %-10s | %-7s | %-8s | %-9s | %-12s | %n", 
                DATA_CACHE.get(0), 
                DATA_CACHE.get(1), 
                DATA_CACHE.get(2), 
                DATA_CACHE.get(3), 
                DATA_CACHE.get(4), 
                DATA_CACHE.get(5), 
                DATA_CACHE.get(6) + " / " + DATA_CACHE.get(7)
            );

            DATA_CACHE.clear();
        }
        
        if(DATA_CACHE.size() <= 0){
            DATA_CACHE.add(String.valueOf(timeOffset));
        }

        switch (identifier) {
            case "0018":
                DATA_CACHE.add("...");
                DATA_CACHE.add(String.valueOf(sensorData));
                DATA_CACHE.add("...");
                DATA_CACHE.add("...");
                DATA_CACHE.add("...");
            break;

            case "0F7A":
                DATA_CACHE.add(String.valueOf(sensorData));
                DATA_CACHE.add("...");
                DATA_CACHE.add("...");
                DATA_CACHE.add("...");
                DATA_CACHE.add("...");
            break;

            case "0B41":
                if(DATA_CACHE.size() <= 3){
                    DATA_CACHE.add("...");
                    DATA_CACHE.add("...");
                }
                DATA_CACHE.add(String.valueOf(sensorData));
            break;
        }

        if(identifier.startsWith("GPS")){
            DATA_CACHE.add(String.valueOf(sensorData));
        }
    }
}
