package assignment;

public class MultipleCANData extends CANFrame {
    private static float PROCESSED_DATA_1;
    private static float PROCESSED_DATA_2;
    private static float PROCESSED_DATA_3;

    public MultipleCANData(String msgId, float timeOffset, int dataLength, 
        String dataBytes, float processedData1, float processedData2, float processedData3
    ){
        super(msgId, timeOffset, dataLength, dataBytes);
        PROCESSED_DATA_1 = processedData1;
        PROCESSED_DATA_2 = processedData2;
        PROCESSED_DATA_3 = processedData3;
    }

    public static float getProcessedData1() {
        return PROCESSED_DATA_1;
    }

    public static float getProcessedData2(){
        return PROCESSED_DATA_2;
    }

    public static float getProcessedData3(){
        return PROCESSED_DATA_3;
    }
}
