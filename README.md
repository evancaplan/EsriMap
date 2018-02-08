# EsriMap
# This project uses ESRI's ArcGIS Android SDK and the USGS's GeoJSON data to display earthquakes as points in real time.
This app was built with the intent to use data made available by the USGS at https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/2.5_week.geojson, 
and display the earthquake information. The app fetches the GeoJSON data online, converts the input stream into a string, parses the
string, creates a symbol for each earthquake in the data set, and then puts each earthquake onto the map as well as its attributes in an
on click listener. The user is able to change the base map as well as change the time frame in which the data is pulled, from within the
last hour to within the last month. One bug I found was with the base maps and changing them. When the user switches the base map it 
creates a new map entirely without going back to where the user was looking, but this bug was fixed. One I was unable to fix is associated
with the earthquake coordinates. Sometimes the data loads and the coordinates for each earthquake are (0,0), but when the user selects a
time frame for the data, the earthquakes appear where they should.
