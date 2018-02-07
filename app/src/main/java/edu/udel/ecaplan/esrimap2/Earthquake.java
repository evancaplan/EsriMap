package edu.udel.ecaplan.esrimap2;

import com.esri.core.geometry.Point;

/**
 * Created by evancaplan on 5/9/17.
 */

public class Earthquake {
    private String title;
    private double mag;
    java.util.Date time;
    private double lat;
    private double lon;
    private Point point;
    public Earthquake(String title, double mag, java.util.Date time, double lat, double lon,Point point){
        this.title=title;
        this.mag=mag;
        this.time=time;
        this.lat=lat;
        this.lon=lon;
        this.point=point;
}
    public String getTitle() {
        return title;
    }
    public double getMag() {
        return mag;
    }
    public java.util.Date getTime() {
        return time;
    }
    public double getLon() {
        return lon;
    }
    public double getLat() {return lat;}
    public Point getPoint() {
        return point;
    }

}
