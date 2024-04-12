package assignment;

// import java.io.*;
// import java.text.*;
import java.util.*;

public class GPSTrace {
    private LinkedList<GPSCoordinate> GPS_COORDIANTES;

    GPSTrace(){
        this.GPS_COORDIANTES = new LinkedList<GPSCoordinate>();
    }

    // Addes new coordinates to the list
    public void addNew(GPSCoordinate gpsCoordinate){
        this.GPS_COORDIANTES.add(gpsCoordinate);
    }

    // Return all the captured GPS coordinates
    public LinkedList<GPSCoordinate> getGPSCoordinates() {
        return this.GPS_COORDIANTES;
    }

    // Return gps coordinated based on time offset
    public GPSCoordinate getGPSCoordinateByTimeOffset(float timeOffset){
        int index = (int) (timeOffset  / 1000);
        if(index >= GPS_COORDIANTES.size()){
            return new GPSCoordinate();
        }
        return GPS_COORDIANTES.get(index);
    }
}
