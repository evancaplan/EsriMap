package edu.udel.ecaplan.esrimap2;

import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.MapView;
import com.esri.core.geometry.Point;

/**
 * Created by evancaplan on 5/11/17.
 */

public class EarthquakeView {
    MapView mMapView = null;
    GraphicsLayer graphicsLayer = null;
    Point locationPoint;
    String locationPointAddress;

    public EarthquakeView(MapActivity activity) {
        //Retrieve initial map
        mMapView = (MapView) activity.findViewById(R.id.map);
        //Create graphics layer and add to initial map
        graphicsLayer = new GraphicsLayer();
        mMapView.addLayer(graphicsLayer);
        // you need to do this when using Esri SDK
        mMapView.setEsriLogoVisible(true);
        // enable map to cross dateline
        mMapView.enableWrapAround(true);
    }
    public MapView getMapView() {
        return mMapView;
    }
    public GraphicsLayer getGraphicsLayer() {
        return graphicsLayer;
    }
    public Point  getLocationPoint(){return locationPoint;}
    public void setLocationPointAddress(String s){this.locationPointAddress=s;}
}
