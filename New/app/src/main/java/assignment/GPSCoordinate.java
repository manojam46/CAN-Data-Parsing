package assignment;

public class GPSCoordinate {
    private double latitude;
    private double logitude;
    private double timeOffset;

    GPSCoordinate(double latitude, double logitude, double timeOffset){
        this.latitude = latitude;
        this.logitude = logitude;
        this.timeOffset = timeOffset;
    }   

    public double getLatitude() {
        return this.latitude;
    }

    public double getLogitude() {
        return this.logitude;
    }

    public double getTimeOffset() {
        return this.timeOffset;
    }
}
