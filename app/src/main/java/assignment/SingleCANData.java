package assignment;

public class SingleCANData extends CANFrame {
    private float PROCESSED_DATA;

    public SingleCANData(String msgId, float timeOffset, int dataLength, String dataBytes, float processedData){
        super(msgId, timeOffset, dataLength, dataBytes);
        this.PROCESSED_DATA = processedData;
    }

    public float getProcessedData(){
        return this.PROCESSED_DATA;
    }
}
