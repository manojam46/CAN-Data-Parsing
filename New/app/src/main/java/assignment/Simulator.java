package assignment;

import java.util.*;

public class Simulator extends CANSimulation {
    public void startSimulation(){
        long simulationStartTime = System.currentTimeMillis();
        Object canData = CAN_TRACE.getNextMessage();

        while (canData != null) {
            long currentSimulationTime = System.currentTimeMillis();
            long timeOffset = currentSimulationTime - simulationStartTime;

            LinkedList<String> data = new LinkedList<String>();
            LinkedList<String> dataType = new LinkedList<String>();
            float dataTimeOffset;

            if(MultipleCANFrameData.class.isInstance(canData)){
                MultipleCANFrameData multipleCANFrameData = (MultipleCANFrameData) canData;

                data.add(multipleCANFrameData.getProcessedData1().get("value"));
                data.add(multipleCANFrameData.getProcessedData2().get("value"));
                data.add(multipleCANFrameData.getProcessedData3().get("value"));

                dataType.add(multipleCANFrameData.getProcessedData1().get("type"));
                dataType.add(multipleCANFrameData.getProcessedData2().get("type"));
                dataType.add(multipleCANFrameData.getProcessedData3().get("type"));
                
                dataTimeOffset = multipleCANFrameData.getTimeOffset();
            } else {
                SingleCANFrameData singleCANFrameData = (SingleCANFrameData) canData;

                data.add(singleCANFrameData.getProcessedData().get("value"));
                dataType.add(singleCANFrameData.getProcessedData().get("type"));
                dataTimeOffset = singleCANFrameData.getTimeOffset();
            }

            if(timeOffset < dataTimeOffset) {
                continue;
            }
            
            // Send data
            System.out.println("Sending: " + String.join(" - ", data));
            canData = CAN_TRACE.getNextMessage();
        }
    }
}
