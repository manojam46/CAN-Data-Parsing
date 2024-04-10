package assignment;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;
// import java.util.Map;
// import java.util.HashMap;

public class CANTrace {
    private LinkedList<Object> CAN_FRAME_DATA;
    private Iterator<Object> CAN_DATA_ITR;

    public CANTrace(){
        this.CAN_FRAME_DATA = new LinkedList<Object>();
    }

    // Adds the new element to the linked list
    public void addNew(Object canData){
        // Checks the instance of the object with SingleCANFrameData
        if(SingleCANFrameData.class.isInstance(canData)){
            SingleCANFrameData singleCANFrameData = (SingleCANFrameData) canData;
            this.CAN_FRAME_DATA.add(singleCANFrameData);
        }

        // Checks the instance of the object with MultipleCANFrameData
        if(MultipleCANFrameData.class.isInstance(canData)){
            MultipleCANFrameData multipleCANFrameData = (MultipleCANFrameData) canData;
            this.CAN_FRAME_DATA.add(multipleCANFrameData);
        }
    }

    // Fetches the new message from the List of CAN Data
    public Object getNextMessage(){
        try{
            if(this.CAN_DATA_ITR == null){
                this.CAN_DATA_ITR = this.CAN_FRAME_DATA.iterator();
            }

            if(!this.CAN_DATA_ITR.hasNext()){
                this.CAN_DATA_ITR = this.CAN_FRAME_DATA.iterator();
                return null;
            }
    
            Object dataFrame = this.CAN_DATA_ITR.next();
    
            String id = new String();
            String timeOffset = new String();
            String calculations = new String();
            
            if(SingleCANFrameData.class.isInstance(dataFrame)){
                SingleCANFrameData singleCANFrameData = (SingleCANFrameData) dataFrame;
                id              = singleCANFrameData.getMsgId();
                timeOffset      = String.valueOf(singleCANFrameData.getTimeOffset());
                calculations    = singleCANFrameData.getProcessedData().get("value") + 
                                    " (" + singleCANFrameData.getProcessedData().get("type") + ") ";
            }
    
            if(MultipleCANFrameData.class.isInstance(dataFrame)){
                MultipleCANFrameData multipleCANFrameData = (MultipleCANFrameData) dataFrame;
                id              = multipleCANFrameData.getMsgId();
                timeOffset      = String.valueOf(multipleCANFrameData.getTimeOffset());
                calculations    = multipleCANFrameData.getProcessedData1().get("value") + 
                                    " (" + multipleCANFrameData.getProcessedData1().get("type") + ") : ";
                                    
                calculations    += multipleCANFrameData.getProcessedData2().get("value") + 
                                    " (" + multipleCANFrameData.getProcessedData2().get("type") + ") : ";
                                    
                calculations    += multipleCANFrameData.getProcessedData3().get("value") + 
                                    " (" + multipleCANFrameData.getProcessedData3().get("type") + ")";
            }
    
            System.out.printf("| %-5s | %-10s | %-10s %n", "ID", "TIME OFFSET", "CALCULATION(S)");
            System.out.println("-----------------------------------------------------------------");
            System.out.printf("| %-5s | %-11s | %-15s %n\n\n", id, timeOffset, calculations); 

            return dataFrame;
        } catch(NoSuchElementException e){
            return null;
        }
    }
    

    public void resetNextMessage(){
        this.CAN_DATA_ITR = this.CAN_FRAME_DATA.iterator();
    }

    public void printTrace(){
        Iterator<Object> tracer = this.CAN_FRAME_DATA.iterator();

        System.out.printf("| %-5s | %-10s | %-10s %n", "ID", "TIME OFFSET", "CALCULATION(S)");

        while(tracer.hasNext()){
            String id = new String();
            String timeOffset = new String();
            String calculations = new String();

            Object canData = tracer.next();

            if(SingleCANFrameData.class.isInstance(canData)){
                SingleCANFrameData singleCANFrameData = (SingleCANFrameData) canData;
                id              = singleCANFrameData.getMsgId();
                timeOffset      = String.valueOf(singleCANFrameData.getTimeOffset());
                calculations    = singleCANFrameData.getProcessedData().get("value") + 
                                    " (" + singleCANFrameData.getProcessedData().get("type") + ") ";
            }
    
            if(MultipleCANFrameData.class.isInstance(canData)){
                MultipleCANFrameData multipleCANFrameData = (MultipleCANFrameData) canData;
                id              = multipleCANFrameData.getMsgId();
                timeOffset      = String.valueOf(multipleCANFrameData.getTimeOffset());
                calculations    = multipleCANFrameData.getProcessedData1().get("value") + 
                                    " (" + multipleCANFrameData.getProcessedData1().get("type") + ") : ";
                                    
                calculations    += multipleCANFrameData.getProcessedData2().get("value") + 
                                    " (" + multipleCANFrameData.getProcessedData2().get("type") + ") : ";
                                    
                calculations    += multipleCANFrameData.getProcessedData3().get("value") + 
                                    " (" + multipleCANFrameData.getProcessedData3().get("type") + ")";
            }

            System.out.printf("| %-5s | %-11s | %-15s %n", id, timeOffset, calculations); 
        }
    }
}
