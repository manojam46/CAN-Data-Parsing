package assignment;

public class GPSCoordinate {
    private double latitude;
    private double logitude;
    private double timeOffset;

    // Constructor to pass value
    GPSCoordinate(double latitude, double logitude, double timeOffset){
        this.latitude = latitude;
        this.logitude = logitude;
        this.timeOffset = timeOffset;
    }   

    // Default Constructor to pass no value
    GPSCoordinate(){}

    // Get latitude data
    public double getLatitude() {
        return this.latitude;
    }

    // Get longitude data
    public double getLogitude() {
        return this.logitude;
    }

    // Get timeoffset data
    public double getTimeOffset() {
        return this.timeOffset;
    }
}
