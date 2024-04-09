package assignment;

import java.util.HashMap;

public class MultipleCANFrameData extends CANFrame {
    private double PROCESSED_DATA_1;
    private String PROCESSED_DATA_1_TYPE;
    private double PROCESSED_DATA_2;
    private String PROCESSED_DATA_2_TYPE;
    private double PROCESSED_DATA_3;
    private String PROCESSED_DATA_3_TYPE;

    public MultipleCANFrameData(String msgId, float timeOffset, int dataLength, 
        String dataBytes, double processedData1, double processedData2, double processedData3,
        String processedData1Type, String processedData2Type, String processedData3Type
    ){
        super(msgId, timeOffset, dataLength, dataBytes);
        this.PROCESSED_DATA_1       = processedData1;
        this.PROCESSED_DATA_1_TYPE  = processedData1Type;
        this.PROCESSED_DATA_2       = processedData2;
        this.PROCESSED_DATA_2_TYPE  = processedData2Type;
        this.PROCESSED_DATA_3       = processedData3;
        this.PROCESSED_DATA_3_TYPE  = processedData3Type;
    }

    public HashMap<String, String> getProcessedData1() {
        String data = String.valueOf(this.PROCESSED_DATA_1);
        String type = this.PROCESSED_DATA_1_TYPE;
        return new HashMap<String, String>(){{
            put("value", data);
            put("type", type);
        }}; 
    }

    public HashMap<String, String> getProcessedData2(){
        String data = String.valueOf(this.PROCESSED_DATA_2);
        String type = this.PROCESSED_DATA_2_TYPE;
        return new HashMap<String, String>(){{
            put("value", data);
            put("type", type);
        }}; 
    }

    public HashMap<String, String> getProcessedData3(){
        String data = String.valueOf(this.PROCESSED_DATA_3);
        String type = this.PROCESSED_DATA_3_TYPE;
        return new HashMap<String, String>(){{
            put("value", data);
            put("type", type);
        }}; 
    }
}
