package assignment;

import java.util.Iterator;
import java.util.LinkedList;

public class CANTrace {
    private LinkedList<Object> CAN_FRAME_DATA;
    private Iterator<Object> CAN_DATA_ITR;

    public CANTrace(){
        this.CAN_FRAME_DATA = new LinkedList<Object>();
    }

    public void appendCanData(Object canData){
        if(SingleCANFrameData.class.isInstance(canData)){
            SingleCANFrameData singleCANFrameData = (SingleCANFrameData) canData;
            this.CAN_FRAME_DATA.add(singleCANFrameData);
        }

        if(MultipleCANFrameData.class.isInstance(canData)){
            MultipleCANFrameData multipleCANFrameData = (MultipleCANFrameData) canData;
            this.CAN_FRAME_DATA.add(multipleCANFrameData);
        }
    }

    public Object getNextMessage(){
        if(this.CAN_DATA_ITR == null || !this.CAN_DATA_ITR.hasNext()){
            this.CAN_DATA_ITR = this.CAN_FRAME_DATA.iterator();
        }

        Object dataFrame = this.CAN_DATA_ITR.next();

        prettyPrint(dataFrame);

        return dataFrame;
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

    public void prettyPrint(Object canFrame){
        String id = new String();
        String timeOffset = new String();
        String calculations = new String();
        
        if(SingleCANFrameData.class.isInstance(canFrame)){
            SingleCANFrameData singleCANFrameData = (SingleCANFrameData) canFrame;
            id              = singleCANFrameData.getMsgId();
            timeOffset      = String.valueOf(singleCANFrameData.getTimeOffset());
            calculations    = singleCANFrameData.getProcessedData().get("value") + 
                                " (" + singleCANFrameData.getProcessedData().get("type") + ") ";
        }

        if(MultipleCANFrameData.class.isInstance(canFrame)){
            MultipleCANFrameData multipleCANFrameData = (MultipleCANFrameData) canFrame;
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
        System.out.printf("| %-5s | %-11s | %-15s %n\n\n", id, timeOffset, calculations); 
    }
}
