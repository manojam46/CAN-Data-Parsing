package assignment;

public class CANSimulation {
    protected static CANTrace CAN_TRACE;
    protected static GPSTrace GPS_TRACE;

    public static void main(String argss[]) {
        // Checking if the .trc file location has been submitted
        // if(args.length < 2){
        //     throw new Error("File location(s) cannot be empty");
        // }

        String[] args = {"/Users/keer/Documents/Assignments/SER 540/CAN-Data-Parsing/18 CANmessages.trc", "/Users/keer/Documents/Assignments/SER 540/CAN-Data-Parsing/GPStrace.txt"};

        String fileLoc = args[0];
        String gpsLoc = args[1];
        
        CAN_TRACE = (new CANTraceParser()).parseCANTraceFile(fileLoc);
        GPS_TRACE = (new GPSParser()).parseGPSTraceFile(gpsLoc);
    }
}
