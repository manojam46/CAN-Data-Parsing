package assignment;

public class SingleCANFrameData extends CANFrame {
    private double PROCESSED_DATA;

    public SingleCANFrameData(String msgId, float timeOffset, int dataLength, String dataBytes, double processedData){
        super(msgId, timeOffset, dataLength, dataBytes);
        this.PROCESSED_DATA = processedData;
    }

    public double getProcessedData(){
        return this.PROCESSED_DATA;
    }
}
