package assignment;

import java.util.Map;

public class CANTrace {
    private Map<Float, Object> CAN_FRAME_DATA;

    public void appendCanData(Object canData){
        System.out.println(canData.getClass());
        // this.CAN_FRAME_DATA.put(null, canData)
    }


}
