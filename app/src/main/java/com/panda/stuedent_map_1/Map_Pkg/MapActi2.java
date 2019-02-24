package com.panda.stuedent_map_1.Map_Pkg;

import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Toast;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.api.directions.v5.MapboxDirections;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.core.constants.Constants;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.light.Position;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;
import com.panda.stuedent_map_1.Map_Pkg.MapUtils.PolyUtil;
import com.panda.stuedent_map_1.R;

import android.support.annotation.NonNull;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.List;

import static com.mapbox.core.constants.Constants.PRECISION_6;

public class MapActi2 extends AppCompatActivity implements
        OnMapReadyCallback, PermissionsListener {

    private static final String TAG ="CHAIOS OoO" ;
    private PermissionsManager permissionsManager;
    private MapboxMap mapboxMap;
    private MapView mapView;
    NavigationMapRoute navigationMapRoute = null;
    private DirectionsRoute currentRoute=null;
    //
    List<LatLng>Route_Points=new ArrayList<>();
    com.mapbox.mapboxsdk.annotations.Marker SelectPoint=null;
    com.mapbox.mapboxsdk.annotations.Marker Pus=null;
    // Marker Animation
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

// Mapbox access token is configured here. This needs to be called either in your application
// object or in the same activity which contains the mapview.
        Mapbox.getInstance(this, getString(R.string.access_token));

// This contains the MapView in XML and needs to be called after the access token is configured.
        setContentView(R.layout.mmap2);

        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
    }

    @Override
    public void onMapReady(@NonNull final MapboxMap mapboxMap2)
    {
        MapActi2.this.mapboxMap = mapboxMap2;
        mapboxMap.setStyle(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {
            @SuppressLint("MissingPermission")
            @Override
            public void onStyleLoaded(@NonNull Style style) {
                // Map is set up and the style has loaded. Now you can add data or make other map adjustments
                enableLocationComponent(style);

                if (mapboxMap.getLocationComponent().getLastKnownLocation()!=null)
                {

Pus =  mapboxMap.addMarker(new MarkerOptions().setTitle("track").setPosition(new LatLng(mapboxMap.getLocationComponent().getLastKnownLocation().getLatitude(),mapboxMap.getLocationComponent().getLastKnownLocation().getLongitude())));

                    Point dest=Point.fromLngLat(31.282532890628318,30.104366776961925);
                    mapboxMap.addMarker(new MarkerOptions().setTitle("MrQenawi marker 1").setPosition(new LatLng(30.104366776961925,31.282532890628318)));
                    GetRoute(dest);
                }
            }
        });

        mapboxMap.addOnMapClickListener(point ->
        {
            Log.d(TAG, "onMapReady: ."+ point.toString());
            // Check if point is on the route
            if (SelectPoint!=null)
            {
                SelectPoint.remove();


            }
            SelectPoint=mapboxMap.addMarker(new MarkerOptions().setTitle("MrQenawi marker 1").setPosition(new LatLng(point.getLatitude(),point.getLongitude())));
            if (net.mastrgamr.mbmapboxutils.PolyUtil.isLocationOnPath(new LatLng(point.getLatitude(),point.getLongitude()),Route_Points,false,10.0))
            {
                Toast.makeText(this, "Good", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(this, "BAD", Toast.LENGTH_SHORT).show();

            }

            return false;
        });
        }

    void GetRoute(Point Destination)
    {
        @SuppressLint("MissingPermission") Point origin = Point.fromLngLat(mapboxMap.getLocationComponent().getLastKnownLocation().getLongitude(), mapboxMap.getLocationComponent().getLastKnownLocation().getLatitude());
      //  @SuppressLint("MissingPermission") Point destination = Point.fromLngLat(Destination.getLongitude(), Destination.getLatitude());
        getRoute(origin, Destination);
    }

    private void getRoute(Point origin, Point destination) {
        NavigationRoute.builder(this)
                .accessToken(Mapbox.getAccessToken())
                .origin(origin)
                .destination(destination)
                .build()
                .getRoute(new Callback<DirectionsResponse>() {
                    @Override
                    public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
// You can get the generic HTTP info about the response
                        Log.d(TAG, "Response code: " + response.code());
                        if (response.body() == null) {
                            Log.e(TAG, "No routes found, make sure you set the right user and access token.");
                            return;
                        } else if (response.body().routes().size() < 1) {
                            Log.e(TAG, "No routes found");
                            return;
                        }

                         currentRoute = response.body().routes().get(0);
                        LineString lineString=LineString.fromPolyline(currentRoute.geometry(),Constants.PRECISION_6);
                        List<Point> coordinates = lineString.coordinates();


                        for (int i = 0; i < coordinates.size(); i++)
                        {
                            Route_Points.add( new LatLng(
                                    coordinates.get(i).latitude(),
                                    coordinates.get(i).longitude()));
                        }

// Draw the route on the map
                        if (navigationMapRoute != null) {
                            navigationMapRoute.removeRoute();
                        } else {
                            navigationMapRoute = new NavigationMapRoute(null, mapView, mapboxMap, R.style.NavigationMapRoute);
                        }
                        navigationMapRoute.addRoute(currentRoute);

                 moveVechile(Pus,new LatLng(destination.latitude(),destination.longitude()));
                    }

                    @Override
                    public void onFailure(Call<DirectionsResponse> call, Throwable throwable) {
                        Log.e(TAG, "Error: " + throwable.getMessage());
                    }
                });
    }


 // Marker Anmiation


 public void moveVechile(final com.mapbox.mapboxsdk.annotations.Marker myMarker, final LatLng finalPosition) {
        final LatLng startPosition = myMarker.getPosition();
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        final Interpolator interpolator = new AccelerateDecelerateInterpolator();
        final float durationInMs = 10000;
        final boolean hideMarker = false;
        handler.post(new Runnable() {
            long elapsed;
            float t;
            float v;

            @Override
            public void run()
            {
                // Calculate progress using interpolator
                elapsed = SystemClock.uptimeMillis() - start;
                t = elapsed / durationInMs;
                v = interpolator.getInterpolation(t);
             LatLng currentPosition = new LatLng
                     (
                        startPosition.getLatitude() * (1 - t) + (finalPosition.getLatitude()) * t,
                        startPosition.getLongitude() * (1 - t) + (finalPosition.getLongitude()) * t);
                myMarker.setPosition(currentPosition);
                // myMarker.setRotation(finalPosition.getBearing());


                // Repeat till progress is completeelse
                if (t < 1)
                {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                    // handler.postDelayed(this, 100);
                } else
                    {
                   //
                }
            }
        });


    }

  // Marker Anmiation

    @SuppressWarnings({"MissingPermission"})
    private void enableLocationComponent(@NonNull Style loadedMapStyle) {
// Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(this)) {

// Get an instance of the component
            LocationComponent locationComponent = mapboxMap.getLocationComponent();

// Activate with options
            locationComponent.activateLocationComponent(this, loadedMapStyle);

// Enable to make component visible
            locationComponent.setLocationComponentEnabled(true);

// Set the component's camera mode
            locationComponent.setCameraMode(CameraMode.TRACKING);

// Set the component's render mode
            locationComponent.setRenderMode(RenderMode.COMPASS);
        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        Toast.makeText(this, "R.string.user_location_permission_explanation", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            mapboxMap.getStyle(new Style.OnStyleLoaded() {
                @Override
                public void onStyleLoaded(@NonNull Style style) {
                    enableLocationComponent(style);
                }
            });
        } else {
            Toast.makeText(this, "R.string.user_location_permission_not_granted", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    @SuppressWarnings({"MissingPermission"})
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}
