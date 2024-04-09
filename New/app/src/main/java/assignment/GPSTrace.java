package assignment;

// import java.io.*;
// import java.text.*;
import java.util.*;

public class GPSTrace {
    private LinkedList<GPSCoordinate> GPS_COORDIANTES;

    GPSTrace(){
        this.GPS_COORDIANTES = new LinkedList<GPSCoordinate>();
    }

    public void addNew(GPSCoordinate gpsCoordinate){
        this.GPS_COORDIANTES.add(gpsCoordinate);
    }

    public LinkedList<GPSCoordinate> getGpsCoordinates() {
        return this.GPS_COORDIANTES;
    }
}
