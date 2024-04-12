package assignment;

public class CANFrame {
    private float TIME_OFFSET;
    private String MSG_ID;
    private int DATA_LENGTH;
    private String DATA_BYTES;

    // Constructor to hold pass values
    public CANFrame(String msgId, float timeOffset, int dataLength, String dataBytes){
        this.TIME_OFFSET = timeOffset;
        this.MSG_ID = msgId;
        this.DATA_LENGTH = dataLength;
        this.DATA_BYTES = dataBytes;
    }

    // Return timeoffset
    public float getTimeOffset(){
        return this.TIME_OFFSET;
    }

    // Return msg id
    public String getMsgId(){
        return this.MSG_ID;
    }

    // Return data length
    public int getDataLength(){
        return this.DATA_LENGTH;
    }

    // Return data bytes
    public String getDataBytes(){
        return this.DATA_BYTES;
    }
}
