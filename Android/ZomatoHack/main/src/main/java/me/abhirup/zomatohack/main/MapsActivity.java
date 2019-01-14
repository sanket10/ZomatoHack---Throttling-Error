package me.abhirup.zomatohack.main;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.SyncStateContract;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileProvider;
import com.google.maps.android.heatmaps.Gradient;
import com.google.maps.android.heatmaps.HeatmapTileProvider;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.maps.android.heatmaps.WeightedLatLng;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private TileOverlay mOverlay;
    private Marker mMarker;
    private Gradient gradient;
    private ArrayList<Double> listOfLocations;

    final String TAG = "GPS";
    private final static int ALL_PERMISSIONS_RESULT = 101;
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Acquire a reference to the system Location Manager
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

//        ArrayList<String> permissions = new ArrayList<>();
//        ArrayList<String> permissionsToRequest;
//        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
//        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
//        permissionsToRequest = findUnAskedPermissions(permissions);
//        requestPermissions(permissionsToRequest.toArray(new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);

        // Define a listener that responds to location updates
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                makeUseOfNewLocation(location);
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}
        };

        // Register the listener with the Location Manager to receive location updates
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);

        new GetHeatMapTask().execute();

        double x = 12.9279;
        double y = 77.6271;

        int[] colors = {
                Color.rgb(251,211,233),
                Color.rgb(187,55,125)
        };

        float[] startPoints = {
                0.7f, 1f
        };

        gradient = new Gradient(colors, startPoints);
    }

    public void makeUseOfNewLocation(Location location) {
        int randomX = (int)(Math.random() * 3);
        double x;
        double y;
        if (listOfLocations != null) {
            x = listOfLocations.get(randomX*2); // location.getLatitude() + (Math.random() * 0.02 - 0.01) * 10;
            y = listOfLocations.get(randomX*2 + 1); //location.getLongitude() + (Math.random() * 0.02 - 0.01) * 10;
        } else {
            x = 12.9279;ew
            y = 77.6271;
        }

        Double[] arrayUsage = {x,y};
         new UpdateLocationTask().execute(arrayUsage);
        LatLng sydney = new LatLng(x, y);

        mMarker.remove();
        mMarker = mMap.addMarker(new MarkerOptions().position(sydney).title("Current location"));
        new GetHeatMapTask().execute();
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(sydney, 17));

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(x, y))      // Sets the center of the map to location user
                .zoom(12)                   // Sets the zoom                 // Sets the tilt of the camera to 30 degrees
                .build();

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));


//        if (mOverlay != null) {
//            mOverlay.remove();
//        }
//
//        List<LatLng> list = new ArrayList<>();
//        for (int  i = 0; i < 1000; ++i) {
//            double temp_x = x + Math.random() * 0.02 - 0.01;
//            double temp_y = y + Math.random() * 0.02 - 0.01;
//            list.add(new LatLng(temp_x, temp_y));
//        }
//
//        // Create a heat map tile provider, passing it the latlngs of the police stations.
//        HeatmapTileProvider mProvider = new HeatmapTileProvider.Builder()
//                .data(list)
//                .build();
//        // Add a tile overlay to the map, using the heat map tile provider.
//        mOverlay = mMap.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));
    }

    public void showHeatMap(ArrayList<Double> arr) {
        listOfLocations = arr;

        if (mOverlay != null) {
            mOverlay.remove();
        }

        int max_1 = 0;
        int max_2 = 0;
        int max_3 = 0;

        List<WeightedLatLng> list = new ArrayList<>();
        // for (int j = 0; j < 150; ++ j) {
        for (int  i = 0; i < 10; ++i) {
            double temp_x = arr.get(i*2); // + (Math.random() * 0.02 - 0.01) * 1.5;
            double temp_y = arr.get(i*2 + 1); // + (Math.random() * 0.02 - 0.01) * 1.5;
//            if (arr.get(i+20) > arr.get(max_1+20)) {
//                max_3 = max_2;
//                max_2 = max_1;
//                max_1 = i;
//            } else if (arr.get(i+20) > arr.get(max_2+20)) {
//                max_3 = max_2;
//                max_2 = i;
//            } else if (arr.get(i+20) > arr.get(max_2+20)) {
//                max_3 = i;
//            }
            list.add(new WeightedLatLng(new LatLng(temp_x, temp_y ), arr.get(i+20)));
        }
        // }

//        listOfLocations = new ArrayList<>();
//        listOfLocations.add(arr.get(max_1*2));
//        listOfLocations.add(arr.get(max_1*2+1));
//        listOfLocations.add(arr.get(max_2*2));
//        listOfLocations.add(arr.get(max_2*2+1));
//        listOfLocations.add(arr.get(max_3*2));
//        listOfLocations.add(arr.get(max_3*2+1));

        // Create a heat map tile provider, passing it the latlngs of the police stations.
        HeatmapTileProvider mProvider = new HeatmapTileProvider.Builder()
                .weightedData(list)
                //.gradient(gradient)
                .build();

        mProvider.setOpacity(0.75);
        mProvider.setRadius(100);
        // Add a tile overlay to the map, using the heat map tile provider.
        mOverlay = mMap.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng sydney = new LatLng(12.9279, 77.6271);
        mMarker = mMap.addMarker(new MarkerOptions().position(sydney).title("Current Location"));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(sydney, 17));

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(12.9279, 77.6271))      // Sets the center of the map to location user
                .zoom(13)                   // Sets the zoom                 // Sets the tilt of the camera to 30 degrees
                .build();

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

//        if (mOverlay != null) {
//            mOverlay.remove();
//        }

//        List<LatLng> list = new ArrayList<>();
//        for (int  i = 0; i < 1000; ++i) {
//            double temp_x = 12.9279 + Math.random() * 0.02 - 0.01;
//            double temp_y = 77.6271 + Math.random() * 0.02 - 0.01;
//            list.add(new LatLng(temp_x, temp_y));
//        }
//
//        // Create a heat map tile provider, passing it the latlngs of the police stations.
//        HeatmapTileProvider mProvider = new HeatmapTileProvider.Builder()
//                .data(list)
//                .build();
//        // Add a tile overlay to the map, using the heat map tile provider.
//        mOverlay = mMap.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));
    }

    private static final int TWO_MINUTES = 1000 * 60 * 2;

    /** Determines whether one Location reading is better than the current Location fix
     * @param location  The new Location that you want to evaluate
     * @param currentBestLocation  The current Location fix, to which you want to compare the new one
     */
    protected boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }

    /** Checks whether two providers are the same */
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }

    private ArrayList findUnAskedPermissions(ArrayList<String> wanted) {
        ArrayList result = new ArrayList();

        for (String perm : wanted) {
            if (!hasPermission(perm)) {
                result.add(perm);
            }
        }

        return result;
    }

    private boolean hasPermission(String permission) {
        if (canAskPermission()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
            }
        }
        return true;
    }

    private boolean canAskPermission() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }

    private String readStream(InputStream in) {
        try {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = in.read(buffer)) != -1) {
            result.write(buffer, 0, length);
        }
// StandardCharsets.UTF_8.name() > JDK 7

            return result.toString("UTF-8");
        } catch (Exception e) {
            return "";
        }

    }


    class GetHeatMapTask extends AsyncTask<String, Void, String> {

        private Exception exception;

        protected String doInBackground(String... urls) {
            URL url = null;
            HttpURLConnection urlConnection = null;
            Log.d("Hello", "1");
            try {
                Log.d("Hi", "1");
                url = new URL("http://192.168.43.140:8080/heatmap/get_heatmap");
                Log.d("Hello", "2");
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setConnectTimeout(1000);
                urlConnection.setReadTimeout(1000);
                Log.d("Hello", "2.5");
                // urlConnection.connect();
                Log.d("Hi", "3");
                Log.d("hello", String.valueOf(urlConnection.getResponseCode()));
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                Log.d("Hello", "4");
                String s = readStream(in);
                Log.d("Response", s);
                return s;
            } catch (Exception e) {
                Log.d("Hello", "Exception");
                e.printStackTrace();
            } finally {
                Log.d("Hello", "Finally");
                if (urlConnection != null) urlConnection.disconnect();
            }
            Log.d("Hello", "Return Null");
            return "";
        }

        protected void onPostExecute(String s) {
            try {
                String blockValues = s.substring(s.indexOf("\"response\"") + 12, s.length() - 3);
                Log.d("Blocks", blockValues);
//                String values = s.substring(s.indexOf("\"$$$\":[["));
//                values = values.substring(12, values.length() - 3);
                String[] temp = blockValues.split(",");
                ArrayList<Double> valuesNumbers = new ArrayList<>();
                for (int i = 0; i < temp.length; ++i) {
                    valuesNumbers.add(Double.parseDouble(temp[i]));
                }
                showHeatMap(valuesNumbers);
                Log.d("Response", blockValues);
            } catch(Exception e) {

            }
        }
    }

    class UpdateLocationTask extends AsyncTask<Double[] , Void, String> {
        private Exception exception;
        protected String doInBackground(Double[]... arr) {
            URL url = null;
            HttpURLConnection urlConnection = null;
            Log.d("Hello", "1");
            try {
                Log.d("Hi", arr[0][0].toString());
                url = new URL("http://192.168.43.140:8080/heatmap/update_location");
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setConnectTimeout(1000);
                urlConnection.setReadTimeout(1000);
                urlConnection.setDoOutput(true);
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestProperty("charset", "utf-8");


                String json = "{\"id\":2,\"latitude\":" + arr[0][0] + ",\"longitude\":" + arr[0][1] + "}";
                try (DataOutputStream wr = new DataOutputStream(urlConnection.getOutputStream())) {
                    wr.write(json.getBytes("UTF-8"));
                };

                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                Log.d("Hello", "4");
                String s = readStream(in);
                Log.d("Response", s);
                return s;
            } catch (Exception e) {
                Log.d("Hello", "Exception");
                e.printStackTrace();
            } finally {
                Log.d("Hello", "Finally");
                if (urlConnection != null) urlConnection.disconnect();
            }
            Log.d("Hello", "Return Null");
            return "";
        }
    }
}
