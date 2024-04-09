package assignment;

import java.io.*;
import java.text.*;
import java.util.*;

public class CANTraceParser {
    public CANTrace ParseCANTraceFile(String fileLocation) {
        try { 
            // Fetching the file from the user location
            File fileToParse = new File(fileLocation);               

            // Accessing the data from the file selected by the user
            Scanner fileScanner = new Scanner(fileToParse);   

            // Can trace is used to store all the parsed data
            CANTrace canTrace = new CANTrace();               

            while(fileScanner.hasNextLine()) {
                // Obtaining the subsequent line from the.trc file
                String CANData = fileScanner.nextLine();

                // Do not parse If the first character in the CAN data is a ";"
                if(CANData.startsWith(";")){
                    continue;
                } 

                // Contains SingleCANFrameData|MultipleCANFrameData
                Object parsedCANData = parseCANData(CANData);

                // Skiping the loop if null is returned
                if(parsedCANData == null) continue;

                // Appending parsed data to CANTrace
                canTrace.addNew(parsedCANData);
            } 

            fileScanner.close();
            return canTrace;

        // Error Handling
        } catch (FileNotFoundException e) {
            System.err.println("File not found!");
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } 
        return null;
    }

    // After analysing the CAN Data, this function produces an Object that might include SingleCANFrameData or MultipleCANFrameData.
    private Object parseCANData(String CANData){
        List<String> ids = Arrays.asList("0018", "0F7A", "0B41");

        // extracting the necessary info from the data frame by using its unique components.
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

        if(!ids.contains(msgId)){
            return null;
        }

        if(msgId.equals("0018")){
            final int byteHigh   = 7;
            final int byteLow    = 6;
            final int bitHigh    = 5;
            final int bitLow     = 0;
            final double offset     = 2048;
            final double stepSize   = 0.5;
            final String attributeType = "Steering wheel angle";

            // Calcuulating data from hex based on presets
            double finalValue = extractDataAndComputeResult(byteHigh, byteLow, bitHigh, bitLow, offset, stepSize, dataBytes);
            
            // Storing the calculted values
            SingleCANFrameData singleCANFrameData = new SingleCANFrameData(msgId, timeOffset, dataLength, dataBytes, finalValue, attributeType);
            
            return singleCANFrameData;
        }

        // For Vehicle Speed
        if(msgId.equals("0F7A")){
            final int byteHigh   = 7;
            final int byteLow    = 6;
            final int bitHigh    = 3;
            final int bitLow     = 0;
            final double offset     = 0; 
            final double stepSize   = 0.1;
            final String attributeType   = "Vehicle Speed";

            // Calcuulating data from hex based on presets
            double finalValue = extractDataAndComputeResult(byteHigh, byteLow, bitHigh, bitLow, offset, stepSize, dataBytes);

            // Storing the calculted values
            SingleCANFrameData singleCANFrameData = new SingleCANFrameData(msgId, timeOffset, dataLength, dataBytes, finalValue, attributeType);

            return singleCANFrameData;
        }

        if(msgId.equals("0B41")){
            // Vehicle yaw rate
            final int yawRate_byteHigh   = 7;
            final int yawRate_byteLow    = 6;
            final int yawRate_bitHigh    = 7;
            final int yawRate_bitLow     = 0;
            final double yawRate_offset     = 327.68; 
            final double yawRate_stepSize   = 0.01;
            final String yawRate_type       = "Vehicle Yaw Rate";

            // Vehicle longitudinal acceleration
            final int longitudinalAccelaration_byteHigh   = 3;
            final int longitudinalAccelaration_byteLow    = 3;
            final int longitudinalAccelaration_bitHigh    = 7;
            final int longitudinalAccelaration_bitLow     = 0;
            final double longitudinalAccelaration_offset     = 10.24;
            final double longitudinalAccelaration_stepSize   = 0.08;
            final String longitudinalAccelaration_type       = "Vehicle Longitudinal Acceleration";

            // Vehicle lateral acceleration
            final int lateralAccelearation_byteHigh   = 2;
            final int lateralAccelearation_byteLow    = 2;
            final int lateralAccelearation_bitHigh    = 7;
            final int lateralAccelearation_bitLow     = 0;
            final double lateralAccelearation_offset     = 10.24;
            final double lateralAccelearation_stepSize   = 0.08;
            final String lateralAccelearation_type       = "Vehicle Lateral Acceleration";

            // Calculating data from hex based on pre-sets
            double yawRate_result = extractDataAndComputeResult(yawRate_byteHigh, yawRate_byteLow, yawRate_bitHigh, yawRate_bitLow, yawRate_offset, yawRate_stepSize, dataBytes);

            double longitudinalAccelaration_result = extractDataAndComputeResult(longitudinalAccelaration_byteHigh, longitudinalAccelaration_byteLow, longitudinalAccelaration_bitHigh, longitudinalAccelaration_bitLow, longitudinalAccelaration_offset, longitudinalAccelaration_stepSize, dataBytes);

            double lateralAccelearation_result = extractDataAndComputeResult(lateralAccelearation_byteHigh, lateralAccelearation_byteLow, lateralAccelearation_bitHigh, lateralAccelearation_bitLow, lateralAccelearation_offset, lateralAccelearation_stepSize, dataBytes);

            // Storing the calculted values
            MultipleCANFrameData multipleCANFrameData = new MultipleCANFrameData(msgId, timeOffset, dataLength, dataBytes, yawRate_result, longitudinalAccelaration_result, lateralAccelearation_result, yawRate_type, longitudinalAccelaration_type, lateralAccelearation_type);

            return multipleCANFrameData;
        }

        return null;
    }

    // To exctract/calculate the CAN data by its presets
    private double extractDataAndComputeResult(int byteHigh, int byteLow, int bitHigh, int bitLow, double offset, double stepSize, String dataBytes){
        String []dataBytesArr = dataBytes.split(" "); // Spliting Hex data by "white spsace"

        int highestHexDataByteArrayIndex = dataBytesArr.length - byteHigh - 1;
        int lowestHexDataByteArrayIndex = dataBytesArr.length - byteLow - 1;

        if(highestHexDataByteArrayIndex == lowestHexDataByteArrayIndex){
            String hexDataByte = dataBytesArr[highestHexDataByteArrayIndex];
            String decimalData   = hexadecimalToBinary(hexDataByte);

            String decimalArray[]  = decimalData.split("");

            String relaventDecimal   = String.join("", Arrays.copyOfRange(decimalArray, decimalArray.length - bitHigh - 1, decimalArray.length));

            int parsedDecimal = getDecimalNumber(relaventDecimal);

            double valueWithStepSize = parsedDecimal * stepSize;

            return Double.parseDouble((new DecimalFormat("#.00")).format(valueWithStepSize - offset));
        }

        // Extracting Hex values from databytes
        String highestHexDataByte   = dataBytesArr[highestHexDataByteArrayIndex];
        String lowestHexDataByte    = dataBytesArr[lowestHexDataByteArrayIndex];

        // Converting extracted Hex to binary
        String highestDecimalData   = hexadecimalToBinary(highestHexDataByte);
        String lowestDecimalData    = hexadecimalToBinary(lowestHexDataByte);

        // Splting converted binary values
        String highestDecimalArray[]  = highestDecimalData.split("");
        String lowestdecimalArray[]   = lowestDecimalData.split("");

        // Extracting required binary data based on presets
        String relaventHighestDecimal   = String.join("", Arrays.copyOfRange(highestDecimalArray, highestDecimalArray.length - bitHigh - 1, highestDecimalArray.length));
        String relaventLowestDecimal    = String.join("", Arrays.copyOfRange(lowestdecimalArray, 0, lowestdecimalArray.length - bitLow));

        // Converting extraced binary data into decimal values
        int parsedDecimal = getDecimalNumber(relaventHighestDecimal + relaventLowestDecimal);

        // Multiplying step size
        double valueWithStepSize = parsedDecimal * stepSize;

        // Subtracting the offset and returning the value
        return Double.parseDouble((new DecimalFormat("#.00")).format(valueWithStepSize - offset));
    }

    // Function to convert hex to binary (Source: "https://www.geeksforgeeks.org/java-program-to-convert-hexadecimal-to-binary/")
    private String hexadecimalToBinary(String hexadecimalNumber){
        String binaryNumber = "";
        hexadecimalNumber = hexadecimalNumber.toUpperCase();

        // Hashmap with all the hex values and its corresponding decimal values
        HashMap<Character, String> hexToCorrespondingDecimal = new HashMap<Character, String>(){{           
            put('0', "0000");
            put('1', "0001");
            put('2', "0010");
            put('3', "0011");
            put('4', "0100");
            put('5', "0101");
            put('6', "0110");
            put('7', "0111");
            put('8', "1000");
            put('9', "1001");
            put('A', "1010");
            put('B', "1011");
            put('C', "1100");
            put('D', "1101");
            put('E', "1110");
            put('F', "1111");
        }};

        // Looping through the Hex data to convert it to its corresponding decimal value
        for (int i = 0; i < hexadecimalNumber.length(); i++) {
            char hex = hexadecimalNumber.charAt(i);

            if(!hexToCorrespondingDecimal.containsKey(hex)){
                throw new Error("You have entered an invalid Hexadecimal Number.");
            }

            binaryNumber += hexToCorrespondingDecimal.get(hex);
        }

        return binaryNumber;
    }
    

    // function for converting binary to decimal number (Source: 'https://www.geeksforgeeks.org/program-binary-decimal-conversion/')
    private int getDecimalNumber(String binaryNumbers){
        int decimalNumber = 0;
        int power = 0;
        long binaryNumber = Long.parseLong(binaryNumbers);
        while(binaryNumber > 0){
            //taking the rightmost digit from binaryNumber
            long temp = binaryNumber%10;
            //now multiplying the digit and adding it to decimalNumber variable
            decimalNumber += temp*Math.pow(2, power);
            //removing the rightmost digit from binaryNumber variable
            binaryNumber = binaryNumber/10;
            //incrementing the power variable by 1 to be used as power for 2
            power++;
        }
        return decimalNumber;
    }
}
