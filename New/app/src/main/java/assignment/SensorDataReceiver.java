package assignment;

import java.util.*;

class SensorDataReceiver {  
    private static List<String> DATA_CACHE = new ArrayList<String>();
    private static boolean HEADER_PRINTED = false;

    // To recieve sensor data from startSimulation
    public static void receiveSensorValues(double sensorData, float timeOffset, String identifier){
        String format = "| %-12s | %-13s | %-10s | %-7s | %-8s | %-9s | %-19s |";

        // Checks if the header is already printed
        if(!HEADER_PRINTED){
            String fotmatedString = String.format(format, "Current Time", "Vehicle Speed", "SteerAngle", "YawRate", "LatAccel", "LongAccel", "GPS Lat/Long");
            System.out.print(fotmatedString + "\n");
            HEADER_PRINTED = true;
        }

        // Dumps all the data cached till the size is 8 and clears the cache
        if(DATA_CACHE.size() == 8){
            // Creating approx size to backspace the cursor
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

            // Send data to Socket to broadcast the data
            SimulationGUI.broadCastData(DATA_CACHE);
            DATA_CACHE.clear();
        }
        
        // String timeoffset in datacache
        if(DATA_CACHE.size() <= 0){
            DATA_CACHE.add(String.valueOf(timeOffset));
        }

        // To store data into cache based on idetifier
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

        // String GPS coordinates data
        if(identifier.startsWith("GPS")){
            DATA_CACHE.add(String.valueOf(sensorData));
        }
    }
}
