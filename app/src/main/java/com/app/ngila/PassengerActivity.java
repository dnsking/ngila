package com.app.ngila;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.FloatEvaluator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.TextView;

import com.app.ngila.data.DriverLocation;
import com.app.ngila.data.DriverOrder;
import com.app.ngila.data.DriverTravel;
import com.app.ngila.data.NgilaUser;
import com.app.ngila.locationhandler.SingleShotLocationProvider;
import com.app.ngila.network.NetworkContentHelper;
import com.app.ngila.network.actions.AddPassengerPickUp;
import com.app.ngila.network.actions.SignInNetworkAction;
import com.app.ngila.utils.AnimationUtils;
import com.app.ngila.utils.Utils;
import com.app.ngila.views.TimelineViewHelper;
import com.github.marlonlom.utilities.timeago.TimeAgo;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.google.maps.android.SphericalUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import okhttp3.Response;
import pl.tajchert.nammu.Nammu;
import pl.tajchert.nammu.PermissionCallback;

import static android.Manifest.permission.ACCESS_BACKGROUND_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static com.app.ngila.App.Cancel;

public class PassengerActivity extends AppCompatActivity implements
        GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener,
        OnMapReadyCallback {

    private NgilaUser ngilaUser;
    private String city;

    private SupportMapFragment mapFragment ;
    private GoogleMap mMap;
    private LatLng currentLocation;
    private LatLng destinationLocation;
    private Marker marker;
    private ArrayList<Marker> driverMarkers;
    private TextView locationTextView;
    private int requestCode = 100;
    private GroundOverlay ripple;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ngilaUser = Utils.GetNgilaUser(this);
        setContentView(R.layout.activity_driver);
        locationTextView = findViewById(R.id.locationTextView);


        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);


        Nammu.askForPermission(this, Build.VERSION.SDK_INT>28? new String[]{ACCESS_BACKGROUND_LOCATION,
                        ACCESS_FINE_LOCATION}:new String[]{
                        ACCESS_FINE_LOCATION}
                , new PermissionCallback() {
                    @Override
                    public void permissionGranted() {

                        findViewById(R.id.actionButton).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                Intent intent = new Intent(PassengerActivity.this,NewPassengerBookingMainActivity.class);
                                intent.putExtra(App.Content,Utils.LatLonOBjToString(currentLocation));
                                startActivityForResult(intent,requestCode);

                            }
                        });
                        mapFragment.getMapAsync(PassengerActivity.this);
                        init();
                    }

                    @Override
                    public void permissionRefused() {

                        //   Snackbar.make(placeBtn,"Location Permission Required®",Snackbar.LENGTH_SHORT).show();
                    }
                });

        registerReceiver(new OrderReceiver(),
                new IntentFilter(App.Order));

    }

    private class OrderReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            String json = intent.getStringExtra(App.Content);
            DriverOrder oder =new Gson().fromJson(json,DriverOrder.class);

            if(oder.getArriveTime().equals(App.Cancel)){

                Utils.SaveString(App.Order,context,null);
            }
            else{
                Utils.SaveString(App.Order,context,json);
                showJourney();
            }
        }
    }
    private void showJourney(){


        findViewById(R.id.locationTextView).setVisibility(View.GONE);
        findViewById(R.id.actionButton).setVisibility(View.GONE);

        findViewById(R.id.card).setVisibility(View.VISIBLE);
      String json =   Utils.GetString(App.Order,this);
        DriverOrder oder =new Gson().fromJson(json,DriverOrder.class);


        ((TextView)findViewById(R.id.timeTextView)).setText( TimeAgo.using(
                Long.parseLong(oder.getOrderTime())
        ));
        LatLng mOrigin= Utils.LatLonOBjFromString(oder.getPassengerLocation());
        LatLng mDestination= Utils.LatLonOBjFromString(oder.getDestination());


        Marker[] markers = new Marker[]{
                mMap.addMarker(new MarkerOptions().position(mOrigin).title("Pick Up").icon(Utils.CarIconSmall(this)))   ,
                mMap.addMarker(new MarkerOptions().position(mDestination).title("Destination").icon(Utils.CarIconSmall(this)))
        };


        LatLngBounds.Builder b = new LatLngBounds.Builder();
        for (Marker m : markers) {
            b.include(m.getPosition());
        }
        LatLngBounds bounds = b.build();
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 15);
        mMap.animateCamera(cu);
    }
    private void showWaiting(){

        findViewById(R.id.actionButton).setVisibility(View.GONE);

        if(marker!=null)
        marker.remove();
        /*marker=  mMap.addMarker(new MarkerOptions().position(new LatLng(currentLocation.latitude, currentLocation.longitude))
                .title("Pick Up")
                .icon(Utils.Wave(PassengerActivity.this)).anchor(0.5f, 0.5f)
        );*/
      //  AnimationUtils.getObjectAniRepeat(marker,"rotation",8000,new AccelerateDecelerateInterpolator(),0f,3360f).start();

        locationTextView.setText("Waiting For Driver");
        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                // if overlay already exists - remove it
                if (ripple != null) {
                    ripple.remove();
                }
                ripple = showRipples(new LatLng(currentLocation.latitude, currentLocation.longitude), Color.parseColor("#79CCC7"));
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data!=null&&data.getStringExtra(App.Content)!=null){

            destinationLocation = Utils.LatLonOBjFromString(data.getStringExtra(App.Content));
            showWaiting();
            new Thread(new Runnable() {
                @Override
                public void run() {

                    try {

                        AddPassengerPickUp addPassengerPickUp = new AddPassengerPickUp(city,
                                ngilaUser.getPhoneNumber(),
                                Utils.LatLonOBjToString(currentLocation)
                                ,Long.toString(Calendar.getInstance().getTimeInMillis())
                                ,Utils.AdressName(PassengerActivity.this,
                                destinationLocation.latitude,destinationLocation.longitude)
                                , Utils.LatLonOBjToString(destinationLocation)
                        );
                        NetworkContentHelper.AddContent(PassengerActivity.this,addPassengerPickUp);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }).start();
        }
    }

    private void loadCars(){

        if(driverMarkers==null)
        driverMarkers = new ArrayList<>();
        else{
            for (Marker driverMaker:
                    driverMarkers ) {
                driverMaker.remove();
            }
        }

        driverMarkers = new ArrayList<>();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    SignInNetworkAction getPassengerBookedNetworkAction=  new SignInNetworkAction(city
                            ,"GetDriverLocation");
                    Response response = NetworkContentHelper.ApiGatewayCaller(getPassengerBookedNetworkAction);

                    String  result = response.body().string();

                    App. Log("NetworkContentHelper "+result);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            /*DriverLocation[] driverLocations = new Gson().fromJson(result,DriverLocation[].class);
                            for(DriverLocation driverLocation:driverLocations){

                                double[] location = Utils.LatLonFromString(driverLocation.getLocation());
                                Marker driverMaker=  marker= mMap.addMarker(new MarkerOptions().position(new LatLng(
                                        location[0],location[1]

                                ))
                                        .icon(Utils.CarIcon(PassengerActivity.this)));
                            }*/
                        }
                    });




                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

        new android.os.CountDownTimer(1000*3,50){
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
              //  loadCars();

            }
        }.start();
    }
    public void init(){
        SingleShotLocationProvider.requestSingleUpdate(PassengerActivity.this,
                new SingleShotLocationProvider.LocationCallback() {
                    @Override
                    public void onNewLocationAvailable(SingleShotLocationProvider.GPSCoordinates location) {
        new Thread(new Runnable() {
            @Override
            public void run() {

                                try {
                                    city = Utils.LocationCity(PassengerActivity.this,location.latitude,location.longitude);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                try {

                    SignInNetworkAction getPassengerBookedNetworkAction=  new SignInNetworkAction(ngilaUser.getPhoneNumber()
                    ,"GetPassengerBooked");


                    Response response = NetworkContentHelper.ApiGatewayCaller(getPassengerBookedNetworkAction);

                    String result = response.body().string();
                    App. Log("DriverTravel "+result);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            DriverTravel driverTravel = new Gson().fromJson(result,DriverTravel.class);
                            if(driverTravel.getRideId()==null){
                                loadCars();

                            }
                            else{

                            }
                        }
                    });


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
                    }
                });
    }

    @Override
    public boolean onMyLocationButtonClick() {
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {

    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        mMap.setMyLocationEnabled(true);

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener()
        {
            @Override
            public void onMapClick(LatLng arg0)
            {

                currentLocation =arg0;
                if(marker!=null)
                    marker.remove();
                marker= mMap.addMarker(new MarkerOptions().position(arg0)
                        .title("Pick Up"));
                try {
                    locationTextView.setText(Utils.AdressName(PassengerActivity.this,arg0.latitude,arg0.longitude));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(arg0, 13));

                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(arg0)      // Sets the center of the map to location user
                        .zoom(17)                   // Sets the zoom
                        .bearing(30)                // Sets the orientation of the camera to east
                        .tilt(40)                   // Sets the tilt of the camera to 30 degrees
                        .build();                   // Creates a CameraPosition from the builder
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));


            }
        });

        SingleShotLocationProvider.requestSingleUpdate(this, new SingleShotLocationProvider.LocationCallback() {
            @Override
            public void onNewLocationAvailable(SingleShotLocationProvider.GPSCoordinates location) {

                currentLocation =new LatLng(location.latitude, location.longitude);
                marker=  mMap.addMarker(new MarkerOptions().position(new LatLng(location.latitude, location.longitude))
                        .title("Pick Up")
                   //     .icon(Utils.Wave(PassengerActivity.this)).anchor(0.5f, 0.5f)
                );
              //  AnimationUtils.getObjectAniRepeat(marker,"rotation",8000,new AccelerateDecelerateInterpolator(),0f,3360f).start();
                try {
                    locationTextView.setText(Utils.AdressName(PassengerActivity.this,location.latitude,location.longitude));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.latitude, location.longitude), 13));

                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(new LatLng(location.latitude, location.longitude))      // Sets the center of the map to location user
                        .zoom(17)                   // Sets the zoom
                        .bearing(30)                // Sets the orientation of the camera to east
                        .tilt(40)                   // Sets the tilt of the camera to 30 degrees
                        .build();                   // Creates a CameraPosition from the builder
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        Nammu.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }



    /**
     * Create requested url for Direction API to get routes from origin to destination
     *
     * @param origin
     * @param destination
     * @return
     */
    private String buildRequestUrl(LatLng origin, LatLng destination) {
        String strOrigin = "origin=" + origin.latitude + "," + origin.longitude;
        String strDestination = "destination=" + destination.latitude + "," + destination.longitude;
        String sensor = "sensor=false";
        String mode = "mode=driving";

        String param = strOrigin + "&" + strDestination + "&" + sensor + "&" + mode;
        String output = "json";
        String APIKEY = getResources().getString(R.string.google_maps_key);

        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + param + "&key="+APIKEY;
        Log.d("TAG", url);
        return url;
    }

    /**
     * Create requested url for Direction API to get routes from origin to destination
     *
     * @param origin
     * @param destination
     * @return
     */
    private String getRequestedUrl(LatLng origin, LatLng destination) {
        String strOrigin = "origin=" + origin.latitude + "," + origin.longitude;
        String strDestination = "destination=" + destination.latitude + "," + destination.longitude;
        String sensor = "sensor=false";
        String mode = "mode=driving";

        String param = strOrigin + "&" + strDestination + "&" + sensor + "&" + mode;
        String output = "json";
        String APIKEY = getResources().getString(R.string.google_maps_key);

        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + param + APIKEY;
        return url;
    }

    /**
     * Request direction from Google Direction API
     *
     * @param requestedUrl see
     * @return JSON data routes/direction
     */
    private String requestDirection(String requestedUrl) {
        String responseString = "";
        InputStream inputStream = null;
        HttpURLConnection httpURLConnection = null;
        try {
            URL url = new URL(requestedUrl);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.connect();

            inputStream = httpURLConnection.getInputStream();
            InputStreamReader reader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(reader);

            StringBuffer stringBuffer = new StringBuffer();
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line);
            }
            responseString = stringBuffer.toString();
            bufferedReader.close();
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        httpURLConnection.disconnect();
        return responseString;
    }

    public class TaskDirectionRequest extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            String responseString = "";
            try {
                responseString = requestDirection(strings[0]);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return responseString;
        }

        @Override
        protected void onPostExecute(String responseString) {
            super.onPostExecute(responseString);

            App.Log("TaskDirectionRequest responseString "+responseString);
            //Json object parsing
            TaskParseDirection parseResult = new TaskParseDirection();
            parseResult.execute(responseString);
        }
    }

    public class TaskParseDirection extends AsyncTask<String, Void, List<List<HashMap<String, String>>>> {
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonString) {
            List<List<HashMap<String, String>>> routes = null;
            JSONObject jsonObject = null;

            try {
                jsonObject = new JSONObject(jsonString[0]);
                DirectionParser parser = new DirectionParser();
                routes = parser.parse(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> lists) {
            super.onPostExecute(lists);
            ArrayList<LatLng> points = null;
            PolylineOptions polylineOptions = null;

            for (List<HashMap<String, String>> path : lists) {
                points = new ArrayList<LatLng>();
                polylineOptions = new PolylineOptions();

                for (HashMap<String, String> point : path) {
                    double lat = Double.parseDouble(point.get("lat"));
                    double lon = Double.parseDouble(point.get("lng"));

                    points.add(new LatLng(lat, lon));

                }
                for(int i=0;i<points.size();i++){
                    if(i<(points.size()-1)){

                        double distance = SphericalUtil.computeDistanceBetween((LatLng) points.get(i), (LatLng)points.get(i+1));
                    }
                }


                polylineOptions.addAll(points);
                polylineOptions.width(15f);
                polylineOptions.color(Color.BLACK);
                polylineOptions.geodesic(true);

            }
            if (polylineOptions != null) {
                mMap.addPolyline(polylineOptions);
            } else {
                //  Toast.makeText(getApplicationContext(), "Direction not found", Toast.LENGTH_LONG).show();
            }
        }
    }
    public class DirectionParser {
        /**
         * Receives a JSONObject and returns a list of lists containing latitude and longitude
         */
        public List<List<HashMap<String, String>>> parse(JSONObject jObject) {

            List<List<HashMap<String, String>>> routes = new ArrayList<List<HashMap<String, String>>>();
            JSONArray jRoutes = null;
            JSONArray jLegs = null;
            JSONArray jSteps = null;

            try {

                jRoutes = jObject.getJSONArray("routes");

                /** Traversing all routes */
                for (int i = 0; i < jRoutes.length(); i++) {
                    jLegs = ((JSONObject) jRoutes.get(i)).getJSONArray("legs");
                    List path = new ArrayList<HashMap<String, String>>();

                    /** Traversing all legs */
                    for (int j = 0; j < jLegs.length(); j++) {
                        jSteps = ((JSONObject) jLegs.get(j)).getJSONArray("steps");

                        /** Traversing all steps */
                        for (int k = 0; k < jSteps.length(); k++) {
                            String polyline = "";
                            polyline = (String) ((JSONObject) ((JSONObject) jSteps.get(k)).get("polyline")).get("points");
                            List<LatLng> list = decodePoly(polyline);

                            /** Traversing all points */
                            for (int l = 0; l < list.size(); l++) {
                                HashMap<String, String> hm = new HashMap<String, String>();
                                hm.put("lat", Double.toString(((LatLng) list.get(l)).latitude));
                                hm.put("lng", Double.toString(((LatLng) list.get(l)).longitude));
                                path.add(hm);
                            }
                        }
                        routes.add(path);
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
            }
            return routes;
        }


        private List<LatLng> decodePoly(String encoded) {

            List<LatLng> poly = new ArrayList<LatLng>();
            int index = 0, len = encoded.length();
            int lat = 0, lng = 0;

            while (index < len) {
                int b, shift = 0, result = 0;
                do {
                    b = encoded.charAt(index++) - 63;
                    result |= (b & 0x1f) << shift;
                    shift += 5;
                } while (b >= 0x20);
                int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
                lat += dlat;

                shift = 0;
                result = 0;
                do {
                    b = encoded.charAt(index++) - 63;
                    result |= (b & 0x1f) << shift;
                    shift += 5;
                } while (b >= 0x20);
                int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
                lng += dlng;

                LatLng p = new LatLng((((double) lat / 1E5)),
                        (((double) lng / 1E5)));
                poly.add(p);
            }
            return poly;
        }


    }
    private GroundOverlay showRipples(LatLng latLng, int color) {
        GradientDrawable d = new GradientDrawable();
        d.setShape(GradientDrawable.OVAL);
        d.setSize(500, 500);
        d.setColor(color);
        d.setStroke(0, Color.TRANSPARENT);

        final Bitmap bitmap = Bitmap.createBitmap(d.getIntrinsicWidth()
                , d.getIntrinsicHeight()
                , Bitmap.Config.ARGB_8888);

        // Convert the drawable to bitmap
        final Canvas canvas = new Canvas(bitmap);
        d.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        d.draw(canvas);

        // Radius of the circle for current zoom level and latitude (because Earth is sphere at first approach)
        double meters_to_pixels = (Math.cos(mMap.getCameraPosition().target.latitude * Math.PI /180) * 2 * Math.PI * 6378137) / (256 * Math.pow(2, mMap.getCameraPosition().zoom));
        final int radius = (int)(meters_to_pixels * Utils.convertDpToPixel(98,this));

        // Add the circle to the map
        final GroundOverlay circle = mMap.addGroundOverlay(new GroundOverlayOptions()
                .position(latLng, 2 * radius).image(BitmapDescriptorFactory.fromBitmap(bitmap)));

        // Prep the animator
        PropertyValuesHolder radiusHolder = PropertyValuesHolder.ofFloat("radius", 1, radius);
        PropertyValuesHolder transparencyHolder = PropertyValuesHolder.ofFloat("transparency", 0, 1);

        ValueAnimator valueAnimator = new ValueAnimator();
        valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        valueAnimator.setRepeatMode(ValueAnimator.RESTART);
        valueAnimator.setValues(radiusHolder, transparencyHolder);
        valueAnimator.setDuration(1500);
        valueAnimator.setEvaluator(new FloatEvaluator());
        valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float animatedRadius = (float) valueAnimator.getAnimatedValue("radius");
                float animatedAlpha = (float) valueAnimator.getAnimatedValue("transparency");
                circle.setDimensions(animatedRadius * 2);
                circle.setTransparency(animatedAlpha);

            }
        });

        // start the animation
        valueAnimator.start();

        return circle;
    }
}