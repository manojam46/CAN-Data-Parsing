package assignment;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Field;
import java.util.Scanner;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class CANTraceParser {
    public void ParseCANTraceFile(String trcFileLocToParse) {
        try { 
            File fileToParse        = new File(trcFileLocToParse);  // Accessing the file from location submitted by the user         
            Scanner trcFileScanner  = new Scanner(fileToParse);     // Fetching files from the file seelcted by the user
            CANTrace canTrace       = new CANTrace();               // To store all the parsed data

            while (trcFileScanner.hasNextLine()) {
                // Fetching the nextline from the .trc file
                String CANData = trcFileScanner.nextLine();

                // Skip parsing If the CAN Data Starts with a ; (comment)'
                if(CANData.startsWith(";")) continue;

                // Contains SingleCANFrameData|MultipleCANFrameData
                Object parsedCANData = parseCANData(CANData);

                // Skiping the loop if null is returned
                if(parsedCANData == null) continue;

                System.out.println( ( (SingleCANFrameData) parsedCANData ).getMsgId() );
                
                break;
            } 

            trcFileScanner.close();
        } catch (FileNotFoundException e) {
            System.err.println("File not found!");
            e.printStackTrace();
        } catch (SecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } 
    }

    // This function parses the CAN Data and returns an Object which may contain SingleCANFrameData | MultipleCANFrameData
    private Object parseCANData(String CANData){
        HashSet<String> idsToParse = new HashSet<String>(Arrays.asList("0018", "0F7A", "0B41")); // Set of Id's to parse

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

        if(!idsToParse.contains(msgId)) return null;

        if(msgId.equals("0018")){
            final int highestByte   = 7;
            final int lowestByte    = 6;
            final int highestBit    = 5;
            final int lowestBit     = 0;
            final int offset        = 2048; // Todo: Check offset with prof
            final double stepSize   = 0.5;

            double finalValue = calculate(highestByte, lowestByte, highestBit, lowestBit, offset, stepSize, dataBytes);
            
            SingleCANFrameData singleCANFrameData = new SingleCANFrameData(msgId, timeOffset, dataLength, dataBytes, finalValue);
            return singleCANFrameData;
        }

        if(msgId.equals("0F7A")){
            final int highestByte   = 7;
            final int lowestByte    = 6;
            final int highestBit    = 3;
            final int lowestBit     = 0;
            final int offset        = 0; // Todo: Check offset with prof
            final double stepSize   = 0.1;

            double finalValue = calculate(highestByte, lowestByte, highestBit, lowestBit, offset, stepSize, dataBytes);

            SingleCANFrameData singleCANFrameData = new SingleCANFrameData(msgId, timeOffset, dataLength, dataBytes, finalValue);
            return singleCANFrameData;
        }

        if(msgId.equals("0B41")){
            // Vehicle yaw rate
            final int yawRate_highestByte   = 7;
            final int yawRate_lowestByte    = 6;
            final int yawRate_highestBit    = 3;
            final int yawRate_lowestBit     = 0;
            final int yawRate_offset        = 0; // Todo: Check offset with prof
            final double yawRate_stepSize   = 0.1;

            // Vehicle longitudinal acceleration
            final int longitudinalAccelaration_highestByte   = 7;
            final int longitudinalAccelaration_lowestByte    = 6;
            final int longitudinalAccelaration_highestBit    = 3;
            final int longitudinalAccelaration_lowestBit     = 0;
            final int longitudinalAccelaration_offset        = 0; // Todo: Check offset with prof
            final double longitudinalAccelaration_stepSize   = 0.1;

            // Vehicle lateral acceleration
            final int lateralAccelearation_highestByte   = 7;
            final int lateralAccelearation_lowestByte    = 6;
            final int lateralAccelearation_highestBit    = 3;
            final int lateralAccelearation_lowestBit     = 0;
            final int lateralAccelearation_offset        = 0; // Todo: Check offset with prof
            final double lateralAccelearation_stepSize   = 0.1;

            double yawRate_finalValue = calculate(
                yawRate_highestByte, yawRate_lowestByte, yawRate_highestBit, yawRate_lowestBit, yawRate_offset, yawRate_stepSize, dataBytes
            );

            double longitudinalAccelaration_finalValue = calculate(
                longitudinalAccelaration_highestByte, longitudinalAccelaration_lowestByte, longitudinalAccelaration_highestBit, 
                longitudinalAccelaration_lowestBit, longitudinalAccelaration_offset, longitudinalAccelaration_stepSize, dataBytes
            );

            double lateralAccelearation_finalValue = calculate(
                lateralAccelearation_highestByte, lateralAccelearation_lowestByte, lateralAccelearation_highestBit, 
                lateralAccelearation_lowestBit, lateralAccelearation_offset, lateralAccelearation_stepSize, dataBytes
            );

            MultipleCANFrameData multipleCANFrameData = new MultipleCANFrameData(
                msgId, timeOffset, dataLength, dataBytes, yawRate_finalValue, longitudinalAccelaration_finalValue, lateralAccelearation_finalValue
            );
            return multipleCANFrameData;
        }

        return null;
    }

    // To exctract/calculate the CAN data by its presets
    private double calculate(int highestByte, int lowestByte, int highestBit, int lowestBit, int offset, double stepSize, String dataBytes){
        String []dataBytesArr = dataBytes.split(" ");

        String highestHexDataByte = dataBytesArr[dataBytesArr.length - highestByte - 1];
        String lowestHexDataByte = dataBytesArr[dataBytesArr.length - lowestByte - 1];

        String highestDecimalData = hexadecimalToBinary(highestHexDataByte);
        String lowestDecimalData = hexadecimalToBinary(lowestHexDataByte);

        String highestDeicmalArr[] = highestDecimalData.split("");
        String lowestdeicmalArr[] = lowestDecimalData.split("");

        String relaventHighestDecimal = String.join("", Arrays.copyOfRange(highestDeicmalArr, highestDeicmalArr.length - highestBit - 1, highestDeicmalArr.length));
        String relaventLowestDecimal = String.join("", Arrays.copyOfRange(lowestdeicmalArr, 0, lowestdeicmalArr.length - lowestBit));

        int parsedDecimal = getDecimalNumber(relaventHighestDecimal + relaventLowestDecimal);

        double valueWithStepSize = parsedDecimal * stepSize;

        return valueWithStepSize - offset;
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
