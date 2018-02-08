package edu.udel.ecaplan.esrimap2;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.widget.TextView;
import android.widget.Toast;

import com.esri.android.map.Callout;
import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.MapOnTouchListener;
import com.esri.android.map.MapView;
import com.esri.android.map.event.OnSingleTapListener;
import com.esri.core.geometry.GeometryEngine;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.SpatialReference;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.SimpleMarkerSymbol;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.ListIterator;
import java.util.Map;
/**
 * Created by evancaplan on 5/11/17.
 */

public class EarthquakeController {
    private MapActivity activity;
    private EarthquakeModel model;

    private EarthquakeView view;
    private JSONObject mapJSONObjectData;

    public EarthquakeController(MapActivity activity, EarthquakeModel model, EarthquakeView view) {
        this.activity = activity;
        this.model = model;
        this.view = view;


        //create a new remote fetch and display the data
        remoteFetch earthQuakeEvents = new remoteFetch();
        earthQuakeEvents.execute();

        //create an instance of the earthquake tap listener
        earthQuakeTapListener listener = new earthQuakeTapListener(activity);
        view.getMapView().setOnSingleTapListener(listener);

        //allows for easier user movements of map
        view.getMapView().setOnTouchListener(new MyTouchListener(activity, view.getMapView()));
    }

    public void updateData() {
        remoteFetch lastHour = new remoteFetch();
        lastHour.execute();
    }

    //allows user to have easy interactions with map like pinch zoom
    private class MyTouchListener extends MapOnTouchListener {
        MapView mapView;

        public MyTouchListener(Context context, MapView view) {
            super(context, view);
            mapView = view;
        }
    }

    private class earthQuakeTapListener implements OnSingleTapListener {

        Context context;
        private static final long serialVersionUID = 1L;

        public earthQuakeTapListener(Context ctx) {
            context = ctx;
        }

        public void onSingleTap(float x, float y) {
            MapView mMapView = view.getMapView();
            GraphicsLayer graphicsLayer = view.getGraphicsLayer();

            Callout callout = mMapView.getCallout();
            callout.hide();

            //point of tap
            Point point = mMapView.toMapPoint(x, y);

            //create an array of the earthquake graphics closest to the tap
            int[] graphics = graphicsLayer.getGraphicIDs(x,y,20);

            //if there are graphics close to tap it will get the closest graphic to the tap
            if (graphics.length > 0) {
                // Gets closest graphic to touch
                model.setSelected_graphic(graphicsLayer.getGraphic(graphics[0]));
                model.
                // calls the selected graphic and gets its attributes which
                //were put in in the putEarthquakeonMap method
                Map<String, Object> graphicInfo = model.getSelected_graphic().getAttributes();

                String info = "";
                //loop through the attributes and add the attributes to empty string
                for (int i = 0; i < graphicInfo.size(); i++) {
                    info = info + graphicInfo.keySet().toArray()[i] + ": " + graphicInfo.values().toArray()[i] + "\n";
                }

                TextView text = new TextView(context);
                //set the textview to an empty string and set the
                //font size
                text.setText(info);
                text.setTextSize(12);

                //set attributes for attribute box
                callout.setOffset(0, 0);
                callout.setCoordinates(point);
                callout.setMaxWidth(2000);
                callout.setMaxHeight(1500);

                //add the textview with attributes to the map callout
                callout.setContent(text);
                callout.show();

            }
        }

    }

    //method to do the fetching of the data and
    //to create the new earthquakes
    private void getEarthquakes(String url) {

        try {
            //in order for this to work I needed to use Apache http library
            //although their is a remoteFetch class
            //the remote fetch occurs here in this method, but the remote fetch is the class
            //that calls on this method
            HttpClient client = new DefaultHttpClient();
            HttpGet request = new HttpGet(url);
            HttpResponse response = client.execute(request);
            HttpEntity jsonentity = response.getEntity();
            InputStream input = jsonentity.getContent();
            String jsonStr = convertStreamToString(input);
            mapJSONObjectData = new JSONObject(jsonStr);

            //the data is under the key "features", so this call will get its data
            JSONArray earthquakeFeatures = mapJSONObjectData.getJSONArray("features");

            //set the properties for each earthquake in the data set
            if (mapJSONObjectData != null) {

                try {

                    for (int i = 0; i < earthquakeFeatures.length(); i++) {

                        //the information needed for location is under the key geometry
                        JSONObject earthquakeGeometry = earthquakeFeatures.getJSONObject(i)
                                .getJSONObject("geometry");


                        double lon = Double.parseDouble(earthquakeGeometry.getJSONArray("coordinates")
                                .get(0).toString());

                        double lat = Double.parseDouble(earthquakeGeometry.getJSONArray("coordinates")
                                .get(1).toString());

                        Point point = (Point) GeometryEngine.project(new Point(lon, lat), SpatialReference.create(4326),
                                view.getMapView().getSpatialReference());

                        //the information like magnitude and time are under the key properties
                        JSONObject earthquakeProperties = earthquakeFeatures.getJSONObject(i)
                                .getJSONObject("properties");

                        String title = earthquakeProperties.getString("place").toString();

                        double mag = Double.valueOf(earthquakeProperties.getString("mag")
                                .toString());

                        //get the time of the earthquake and convert it from unix to a string
                        long unixtime = Long.valueOf(earthquakeProperties.getString("time")
                                .toString());
                        java.util.Date time = new java.util.Date(unixtime);

                        //create a new earthquake and add it to the earthquake list
                        model.getEarthQuakeList().add(new Earthquake(title, mag, time, lat, lon, point));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    //method gets called on in the getEarthquakes method in order to turn
    //the geojson feed that was converted to an InputStream
    //into a string
    public String convertStreamToString(InputStream input) {

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(input));
        StringBuilder jsonString = new StringBuilder();
        String line;
        try {
            //go through the json file line by line and append the line strings together
            while ((line = bufferedReader.readLine()) != null) {
                String t = line + "\n";
                jsonString.append(t);
            }
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return jsonString.toString();
    }

    private void putEarthquakesOnMap() {

        //create an iterator for the earthquake list
        ListIterator<Earthquake> earthQuakeIterator = model.getEarthQuakeList().listIterator(0);
        //create a blank symbol to change for the while loop
        SimpleMarkerSymbol symbol = new SimpleMarkerSymbol(Color.rgb(0,
                0, 0), 15, SimpleMarkerSymbol.STYLE.CIRCLE);

        //create an empty map of the titles and their corresponding info
        Map<String, Object> attr = new HashMap<>();

        while (earthQuakeIterator != null && earthQuakeIterator.hasNext()) {

            Earthquake earthquake = earthQuakeIterator.next();

            //set the marker on the map based on the magnitude
            if (earthquake.getMag() < 5) {
                symbol = new SimpleMarkerSymbol(Color.rgb(250, 99, 99), 7, SimpleMarkerSymbol.STYLE.CIRCLE);
            }

            if (earthquake.getMag() >= 5 && earthquake.getMag() <= 6.5) {
                symbol = new SimpleMarkerSymbol(Color.rgb(20, 128, 180), 15, SimpleMarkerSymbol.STYLE.CIRCLE);
            }

            if (earthquake.getMag() > 6.5) {
                symbol = new SimpleMarkerSymbol(Color.rgb(90, 255, 99), 20, SimpleMarkerSymbol.STYLE.CIRCLE);
            }

            //put the needed information into attr
            attr.put("Area", earthquake.getTitle());
            attr.put("Magnitude", earthquake.getMag());
            attr.put("Time", earthquake.getTime().toString());
            attr.put("Latitude", earthquake.getLat());
            attr.put("Longitude", earthquake.getLon());

            //add the earthquake to the map
            view.getGraphicsLayer().addGraphic(new Graphic(earthquake.getPoint(), symbol, attr));
        }

    }

    protected class remoteFetch extends AsyncTask<Void, Void, Void> {
        //create a progress loading circle
        private ProgressDialog mProgDialog;
        @Override
        protected void onPreExecute() {
            //clear the map of any callouts that may be showing for graphics layer
            if (view.getMapView().getCallout().isShowing()) {
                view.getMapView().getCallout().hide();
            }
            //show progress dialog while searching for events
            mProgDialog = ProgressDialog.show(activity, "", "Building Map, Please Wait...", true);
        }

        @Override
        protected Void doInBackground(Void... params) {
            view.getGraphicsLayer().removeAll();
            //call get earthquakes and put the earthquakes on map
            getEarthquakes(model.getUrl());
            putEarthquakesOnMap();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            //after the remote fetch is finished remove the progress loading circle
            if (mProgDialog.isShowing()) {
                mProgDialog.dismiss();
            }
            //if the map is displayed and there are no earthquakes display this message
            if (view.getGraphicsLayer() != null && view.getGraphicsLayer().getNumberOfGraphics() == 0) {
                // update the user that there was no data
                Toast.makeText(activity,
                        "There are no earthquakes for the time requested.",
                        Toast.LENGTH_SHORT).show();
            }

        }
    }

}





