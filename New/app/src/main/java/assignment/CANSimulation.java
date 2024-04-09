package assignment;

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
        

        // for(GPSCoordinate gp: gpsTrace.getGpsCoordinates()){
        //     System.out.println(gp.getLatitude() + " | " + gp.getLogitude() + " | " + gp.getTimeOffset());
        // }

        int totalRetrivedData = 0;
        long simulationStartTime = System.currentTimeMillis();

        while (totalRetrivedData < canTrace.getCANDataTotalLength()) {
            long currentSimulationTime = System.currentTimeMillis();
            long timeOffset = currentSimulationTime - simulationStartTime;

            Object data = canTrace.getNextMessageByTimeOffset(timeOffset);

            System.out.println(currentSimulationTime + "-" +simulationStartTime);
            if(data == null){
                System.out.println("No Data: " + timeOffset);
                continue;
            }

            System.out.println("Data: " + timeOffset);
            totalRetrivedData++;
        }
    }
}
