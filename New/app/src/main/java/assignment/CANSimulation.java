package assignment;

import java.util.LinkedList;
import java.util.Scanner;

public class CANSimulation {
    @SuppressWarnings("resource")
    public static void main(String args[]) {
        // Checking if the .trc file location has been submitted
        if(args.length < 2){
            throw new Error("File location(s) cannot be empty");
        }

        String fileLoc = args[0];
        
        CANTraceParser trcFileParser = new CANTraceParser();
        CANTrace canTrace = trcFileParser.ParseCANTraceFile(fileLoc);

        String gpsLoc = args[1];

        GPSParser gpsParser = new GPSParser();
        GPSTrace gpsTrace = gpsParser.parseGPSTraceFile(gpsLoc);
        

        
    }
}
