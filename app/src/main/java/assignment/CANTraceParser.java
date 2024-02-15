package assignment;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class CANTraceParser {
    public void ParseCANTraceFile(String trcFileLocToParse) {
        try { 
            // List<CANFrame> CANFrameList = new ArrayList<CANFrame>();    // Used to store all the parsed CAN Frame data
            Map<Float, CANFrame> CANFrameList   = new HashMap<Float, CANFrame>();    // Used to store all the parsed CAN Frame data
            File fileToParse                    = new File(trcFileLocToParse);  // Accessing the file from location submitted by the user         
            Scanner trcFileScanner              = new Scanner(fileToParse);     // 

            while (trcFileScanner.hasNextLine()) {
                String CANData = trcFileScanner.nextLine();

                // Skip parsing If the CAN Data Starts with a ; (comment)'
                if(CANData.startsWith(";")) continue;

                parseCANData(CANData);
            } 

            trcFileScanner.close();
        } catch (FileNotFoundException e) {
            System.err.println("File not found!");
            e.printStackTrace();
        }  
    }

    private static  parseCANData(String CANData){
        HashSet<String> idsToParse = new HashSet<String>(Arrays.asList("0018", "0F7A", "0B41")); // Set of Id's to parse
        // Map<String, Object> idsToParse = new HashMap<String, Object>(){{
        //     put("0018", new HashMap<String, Integer>(){{
        //         put("HigherByte", 7);
        //         put("LowerByte", 6);
        //         put("HigherBit", 5);
        //         put("LowerBit", 0);
        //     }});
        //     put("0F7A", new HashMap<String, Integer>(){{
        //         put("HigherByte", 7);
        //         put("LowerByte", 6);
        //         put("HigherBit", 3);
        //         put("LowerBit", 0);
        //     }});
        //     put("0B41", new HashMap<String, Integer>(){{
        //         put("HigherByte", 7);
        //         put("LowerByte", 6);
        //         put("HigherBit", 3);
        //         put("LowerBit", 0);
        //     }});
        // }};

        // Using unique parts of the data frame to extract the requried data
        // Used to extract the Time Offset (ms)
        int msgNumEndIndex = CANData.indexOf(')') + 1;
        int typeStartIndex = CANData.indexOf('R');

        // Used to extract the ID (hex)
        int idHexStartIndex = typeStartIndex + 2;
        int idHexEndIndex = idHexStartIndex + 13;

        // Used to extract the Data Length Code
        int dataLengthCodeStartIndex = idHexEndIndex + 1;
        int dataLengthCodeEndIndex  = dataLengthCodeStartIndex + 3; 

        // Used to extract the Data Bytes (hex)
        int hexDataStartIndex = dataLengthCodeEndIndex + 1;

        float timeOffset    = Float.parseFloat(CANData.substring(msgNumEndIndex, typeStartIndex).trim());
        String msgId        = CANData.substring(idHexStartIndex, idHexEndIndex).trim();
        int dataLength      = Integer.parseInt(CANData.substring(dataLengthCodeStartIndex, dataLengthCodeEndIndex).trim());
        String dataBytes    = CANData.substring(hexDataStartIndex).trim();

        if(!idsToParse.contains(msgId)) return;

        if(msgId == "0018"){
            final int highestByte   = 7;
            final int lowestByte    = 6;
            final int highestBit    = 5;
            final int lowestBit     = 0;
            final int offset        = 2048;
            final double stepSize   = 0.5;

            
        }

        if(msgId == "0F7A")

        System.out.println("******************");
        System.out.println("Time: " + timeOffset);
        System.out.println("Id: " + msgId);    
        System.out.println("Data Length: " + dataLength);    
        System.out.println("HEX: " + dataBytes); 
        System.out.println("******************\n\n");


    }
}
