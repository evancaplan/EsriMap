package edu.udel.ecaplan.esrimap2;
/**
 * Created by evancaplan on 5/9/17.
 */

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.MapOptions;
import com.esri.android.map.MapView;
import com.esri.core.geometry.Point;

import java.util.LinkedList;


public class MapActivity extends AppCompatActivity {

//create menu options
   final String last_hour = "https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/2.5_hour.geojson";
    final String last_24 = "https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/2.5_day.geojson";
    final String last_week = "https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/2.5_week.geojson";
    final String last_month = "https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/2.5_month.geojson";
    // create basemap option for each menu item
    final MapOptions mStreetsMap= new MapOptions (MapOptions.MapType.STREETS);
    final MapOptions mTopoMap= new MapOptions(MapOptions.MapType.TOPO);
    final MapOptions mOceansMap= new MapOptions (MapOptions.MapType.OCEANS);
    final MapOptions mGrayMap= new MapOptions(MapOptions.MapType.GRAY);
    final MapOptions mSatelliteMap= new MapOptions (MapOptions.MapType.SATELLITE);
    //create menu options for different data
    MenuItem lastHourMenuItem = null;
    MenuItem last24HoursMenuItem = null;
    MenuItem lastWeekMenuItem=null;
    MenuItem lastMonthMenuItem=null;
    //create menu ooptions for base map
    MenuItem mStreetsMenuItem = null;
    MenuItem mTopoMenuItem = null;
    MenuItem mOceansMenuItem=null;
    MenuItem mGrayMenuItem=null;
    MenuItem mSatelliteMenuItem=null;
    //create the reset zoom menu option
    MenuItem resetZoomMenuItem=null;
    private EarthquakeModel model;
    private EarthquakeController controller;
    private EarthquakeView view;
    EditText searchText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        model = new EarthquakeModel(new LinkedList<Earthquake>());
        view = new EarthquakeView(this);
        controller = new EarthquakeController(this, model, view);
    }

    public boolean onCreateOptionsMenu(Menu menu) {

        //add corresponding taps to the menu items
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);

        mOceansMenuItem = menu.getItem(0);
        mStreetsMenuItem = menu.getItem(1);
        mTopoMenuItem = menu.getItem(2);
        mGrayMenuItem = menu.getItem(3);
        mSatelliteMenuItem = menu.getItem(4);
        lastHourMenuItem = menu.getItem(5);
        last24HoursMenuItem = menu.getItem(6);
        lastWeekMenuItem = menu.getItem(7);
        lastMonthMenuItem = menu.getItem(8);
        resetZoomMenuItem=menu.getItem(9);

        //set the default map to be clicked when app starts up
        mTopoMenuItem.setChecked(true);
        return true;

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        // what happens when the option gets tapped
        MapView mMapView = view.getMapView();
        GraphicsLayer graphicsLayer = view.getGraphicsLayer();
        Point point= mMapView.getCenter();
        switch (item.getItemId()) {

            //handles cases when user wants to switch the basemap
            case R.id.World_Street_Map:

                mMapView.setMapOptions(mStreetsMap);
                mMapView.zoomToResolution(point,mMapView.getResolution());
                mStreetsMenuItem.setChecked(true);
                return true;
            case R.id.World_Topo:
               // Point x= mMapView.getCenter();
                mMapView.setMapOptions(mTopoMap);
                mMapView.zoomToResolution(point,mMapView.getResolution());
                mTopoMenuItem.setChecked(true);
                return true;
            case R.id.Gray:
               // Point center= mMapView.getCenter();
                mMapView.setMapOptions(mGrayMap);
                mMapView.zoomToResolution(point,mMapView.getResolution());
                mGrayMenuItem.setChecked(true);
                return true;
            case R.id.Ocean_Basemap:
               // Point xcenter= mMapView.getCenter();
                mMapView.setMapOptions(mOceansMap);
                mMapView.zoomToResolution(point,mMapView.getResolution());
                mOceansMenuItem.setChecked(true);
                return true;
            case R.id.Satellite_Map:
               // Point xcenter2= mMapView.getCenter();
                mMapView.setMapOptions(mSatelliteMap);
                mSatelliteMenuItem.setChecked(true);
                mMapView.zoomToResolution(point,mMapView.getResolution());
                return true;

            //handles cases when user wants to switch the data time
            case R.id.lastHour:
                graphicsLayer.removeAll();
                model.getEarthQuakeList().clear();
                model.setUrl(last_hour);
                controller.updateData();
                lastHourMenuItem.setChecked(true);
                return true;
            case R.id.last24Hours:
                graphicsLayer.removeAll();
                model.getEarthQuakeList().clear();
                model.setUrl(last_24);
                controller.updateData();

                last24HoursMenuItem.setChecked(true);
                return true;
            case R.id.lastWeek:
                graphicsLayer.removeAll();
                model.getEarthQuakeList().clear();
                model.setUrl(last_week);
                controller.updateData();

                lastWeekMenuItem.setChecked(true);
                return true;
            case R.id.lastMonth:
                graphicsLayer.removeAll();
                model.getEarthQuakeList().clear();
                model.setUrl(last_month);
                controller.updateData();

                lastMonthMenuItem.setChecked(true);
                return true;

            //this is mainly for the emulator because it
            // isn't good at zooming out with a pinching motion
            //also it is useful if a user has zoomed in too far
            case R.id.resetZoom:
                mMapView.setResolution(40000);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        view.getMapView().pause();
    }
    @Override
    protected void onResume() {
        super.onResume();
        view.getMapView().unpause();
    }
}