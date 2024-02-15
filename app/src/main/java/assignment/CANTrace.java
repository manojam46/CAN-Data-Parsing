package assignment;

import java.util.Map;
import java.util.HashMap;
import java.util.*;

public class CANTrace {
    private Map<Float, Object> CAN_FRAME_DATA;

    public CANTrace(){
        CAN_FRAME_DATA = new HashMap<Float, Object>();
        
    }

    public void appendCanData(Object canData){
        if(SingleCANFrameData.class.isInstance(canData)){
            SingleCANFrameData singleCANFrameData = (SingleCANFrameData) canData;
            CAN_FRAME_DATA.put(singleCANFrameData.getTimeOffset(), canData);
        }

        if(MultipleCANFrameData.class.isInstance(canData)){
            MultipleCANFrameData multipleCANFrameData = (MultipleCANFrameData) canData;
            CAN_FRAME_DATA.put(multipleCANFrameData.getTimeOffset(), canData);
        }
    }


}
