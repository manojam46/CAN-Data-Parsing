package assignment;

import java.util.*;

public class CANSimulation {
    protected static CANTrace CAN_TRACE;
    protected static GPSTrace GPS_TRACE;

    public static void main(String args[]) {
        try{
            
            // String[] args = {
            //     "/Users/keer/Documents/Assignments/SER 540/CAN-Data-Parsing/18 CANmessages.trc", 
            //     "/Users/keer/Documents/Assignments/SER 540/CAN-Data-Parsing/GPStrace.txt"
            // };
            // String[] args = {
            //     "C:\\Users\\Manoj A M\\Desktop\\Assignment\\SEM 1\\Internet Embeded System\\Assignment 1\\18 CANmessages.trc", 
            //     "C:\\Users\\Manoj A M\\Desktop\\Assignment\\SEM 1\\Internet Embeded System\\Assignment 1\\GPStrace.txt"
            // };

            // Checking if the .trc file location has been submitted
            if(args.length < 2){
                throw new Error("File location(s) cannot be empty");
            }
    
            String fileLoc = args[0];
            String gpsLoc = args[1];
            
            CAN_TRACE = (new CANTraceParser()).parseCANTraceFile(fileLoc);
            GPS_TRACE = (new GPSParser()).parseGPSTraceFile(gpsLoc);
            
            SimulationGUI.startHttpServer();
            SimulationGUI.startSocket();
        } catch( Exception e ){
            e.printStackTrace();
        }
    }

    public static void startSimulation(){
        long simulationStartTime = System.currentTimeMillis();
        Object canData = CAN_TRACE.getNextMessage(false);

        while (canData != null) {
            long currentSimulationTime = System.currentTimeMillis();
            long timeOffset = currentSimulationTime - simulationStartTime;

            LinkedList<Double> data = new LinkedList<Double>();
            String msgId;
            float dataTimeOffset;

            if(MultipleCANFrameData.class.isInstance(canData)){
                MultipleCANFrameData multipleCANFrameData = (MultipleCANFrameData) canData;

                data.add(Double.parseDouble(multipleCANFrameData.getProcessedData1().get("value")));
                data.add(Double.parseDouble(multipleCANFrameData.getProcessedData2().get("value")));
                data.add(Double.parseDouble(multipleCANFrameData.getProcessedData3().get("value")));

                msgId = multipleCANFrameData.getMsgId();
                dataTimeOffset = multipleCANFrameData.getTimeOffset();
            } else {
                SingleCANFrameData singleCANFrameData = (SingleCANFrameData) canData;

                data.add(Double.parseDouble(singleCANFrameData.getProcessedData().get("value")));
                msgId = singleCANFrameData.getMsgId();
                dataTimeOffset = singleCANFrameData.getTimeOffset();
            }

            if(timeOffset < dataTimeOffset) {
                continue;
            }
            
            // Send data
            for(int i = 0; i < data.size(); i++){
                double sensorData = data.get(i);
                SensorDataReceiver.receiveSensorValues(sensorData, dataTimeOffset, msgId);
            }

            GPSCoordinate coordinates = GPS_TRACE.getGPSCoordinateByTimeOffset(dataTimeOffset);
            for(int i = 0; i < 2; i++){
                double sensorData;
                if(i == 0){
                    sensorData = coordinates.getLatitude();
                    msgId = "GPS_Latitude";
                } else {
                    sensorData = coordinates.getLogitude();
                    msgId = "GPS_Logitude";
                }

                SensorDataReceiver.receiveSensorValues(sensorData, dataTimeOffset, msgId);
            }

            canData = CAN_TRACE.getNextMessage(false);
        }
    }

}
