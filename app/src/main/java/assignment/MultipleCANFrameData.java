package assignment;

public class MultipleCANFrameData extends CANFrame {
    private static double PROCESSED_DATA_1;
    private static double PROCESSED_DATA_2;
    private static double PROCESSED_DATA_3;

    public MultipleCANFrameData(String msgId, float timeOffset, int dataLength, 
        String dataBytes, double processedData1, double processedData2, double processedData3
    ){
        super(msgId, timeOffset, dataLength, dataBytes);
        PROCESSED_DATA_1 = processedData1;
        PROCESSED_DATA_2 = processedData2;
        PROCESSED_DATA_3 = processedData3;
    }

    public static double getProcessedData1() {
        return PROCESSED_DATA_1;
    }

    public static double getProcessedData2(){
        return PROCESSED_DATA_2;
    }

    public static double getProcessedData3(){
        return PROCESSED_DATA_3;
    }
}
