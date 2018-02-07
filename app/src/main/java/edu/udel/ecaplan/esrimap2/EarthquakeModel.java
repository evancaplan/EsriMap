package edu.udel.ecaplan.esrimap2;

import com.esri.core.geometry.Point;
import com.esri.core.map.Graphic;

import java.util.LinkedList;

/**
 * Created by evancaplan on 5/11/17.
 */

public class EarthquakeModel {
    //linked lists are quicker for iterators
    private LinkedList<Earthquake> earthQuakeList;
    private String url="https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/2.5_week.geojson";
    private Graphic selected_graphic;
    private Graphic selectedEarthquale;

    public EarthquakeModel(LinkedList<Earthquake> earthquakes) {earthQuakeList =earthquakes;}
    public LinkedList<Earthquake> getEarthQuakeList() {
        return earthQuakeList;
    }
    public void setEarthQuakeList(LinkedList<Earthquake> earthQuakeList) {this.earthQuakeList = earthQuakeList;}
    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public Graphic getSelected_graphic() {
        return selected_graphic;
    }
    public void setSelected_graphic(Graphic selected_graphic) {this.selected_graphic = selected_graphic;
}

