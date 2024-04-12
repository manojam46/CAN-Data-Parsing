package assignment;

import java.util.HashMap;

public class SingleCANFrameData extends CANFrame {
    private double PROCESSED_DATA;
    private String DATA_TYPE;

    // Default constructor to store processed CAN data
    public SingleCANFrameData(String msgId, float timeOffset, int dataLength, String dataBytes, double processedData, String dataType){
        super(msgId, timeOffset, dataLength, dataBytes);
        this.PROCESSED_DATA = processedData;
        this.DATA_TYPE = dataType;
    }

    // Returns processed CAN data with value and type
    public HashMap<String, String> getProcessedData(){
        String data = String.valueOf(this.PROCESSED_DATA);
        String type = this.DATA_TYPE;
        return new HashMap<String, String>(){{
            put("value", data);
            put("type", type);
        }}; 
    }
}
