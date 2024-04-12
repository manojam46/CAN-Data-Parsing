package assignment;

import java.io.*;
import java.util.*;

public class GPSParser {
    public GPSTrace parseGPSTraceFile(String fileLocation){
        try { 
            // Fetching the file from the user location
            File fileToParse = new File(fileLocation);               

            // Accessing the data from the file selected by the user
            Scanner fileScanner = new Scanner(fileToParse);   

            // Can trace is used to store all the parsed data
            GPSTrace gpsTrace = new GPSTrace();               

            for(int timeOffset = 0; fileScanner.hasNextLine(); timeOffset += 1000) {
                // Obtaining the subsequent line from the.trc file
                String GPSData = fileScanner.nextLine();

                // Contains SingleCANFrameData|MultipleCANFrameData
                GPSCoordinate parsedGPSData = extractGPSCordinates(GPSData, timeOffset);

                // Appending parsed data to CANTrace
                gpsTrace.addNew(parsedGPSData);
            } 

            fileScanner.close();
            return gpsTrace;

        // Error Handling
        } catch (FileNotFoundException e) {
            System.err.println("File not found!");
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } 

        return null;
    } 

    // Extracting GPS coordinates and return GPSCoordinate instance
    private GPSCoordinate extractGPSCordinates(String GPSData, double timeOffset){
        String[] parsedGPSData = GPSData.split(", ");

        double latitude = Double.parseDouble(parsedGPSData[0]);
        double logitude = Double.parseDouble(parsedGPSData[1].split(";")[0]);

        return new GPSCoordinate(latitude, logitude, timeOffset);
    }
}
