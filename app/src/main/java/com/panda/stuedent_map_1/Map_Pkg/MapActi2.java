package com.panda.stuedent_map_1.Map_Pkg;

import android.animation.*;
import android.annotation.SuppressLint;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.os.SystemClock;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Property;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import carbon.widget.ConstraintLayout;
import carbon.widget.Snackbar;
import carbon.widget.TextView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.*;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.core.constants.Constants;
import com.mapbox.core.utils.MapboxUtils;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute;
import com.mapbox.services.android.navigation.ui.v5.utils.MapUtils;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;
import com.panda.stuedent_map_1.Main.Models.PickupRequst;
import com.panda.stuedent_map_1.Main.Models.Route_Module;
import com.panda.stuedent_map_1.R;
import android.support.annotation.NonNull;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Observable;
import java.util.concurrent.Callable;
/**
 * Main Targets - >
 * Draw Route Between 2 Locations (Done)
 * Choose Pick Up Point and Akc Server (50%)
 * - Pick Point And Max Tolerance is 5 meter's
 * -
 * Move Pus According To Server Update
 * -
 * -
 * -
 */

/**
 * Design pattern Used
 * Mv-vM
 */
public class MapActi2 extends AppCompatActivity implements
        OnMapReadyCallback, PermissionsListener {
    private static final String TAG = MapActi2.class.getSimpleName();
    private PermissionsManager permissionsManager;// To Aquire Permission
    private MapboxMap mapboxMap; // MapBox Main Object
    private MapView mapView;// MapView Xml
    NavigationMapRoute navigationMapRoute = null; // Get Route And Draw IT
    private DirectionsRoute currentRoute = null; // Save Acquired Route And Save It
    private List<LatLng> Route_Points = new ArrayList<>(); // PolyLine
    GeoJsonSource SelectPoint = null;// PickUp Point
    CompositeDisposable disposable;
    // Routs And Destination
    LatLng SelectedRoute = null;
    // animation 2
    private int index;
    private int next;
    private LatLng startPosition;
    private LatLng endPosition;
    private LatLng CarPosition = null;
    private LatLng Pick_up_Pos = null;
    public Float Current_Bearing = 0.0f;
    private Float NextFloat_Bearing = 0.0f;
    private ValueAnimator valueAnimator;
    private static final int MyID = 101;
    private float v;
    private double lat, lng;
    GeoJsonSource geoJsonSource;
    private Handler handler;
    private Route_Module Selected_route_module = null;
    @BindView(R.id.Messge_box)
    TextView Messege;
    @BindView(R.id.Confirm)
    TextView ConfirmBtn;
    @BindView(R.id.Cancel)
    TextView CancelBtn;
    @BindView(R.id.picktxt)
    TextView Pick_Apoint;

    boolean CanClick = true;
    boolean Enapled = true;
    private Snackbar snackbar = null;
    @BindView(R.id.containeer)
    ConstraintLayout RootView;
    // Fire Base
    ValueEventListener Confermation_Value_Event_Listner;
    DatabaseReference databaseReference;
    FirebaseDatabase database;
    private Runnable runnable;

    // Marker Animation
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Mapbox access token is configured here.
        // This needs to be called either in your application
        // object or in the same activity which contains the mapview.
        Mapbox.getInstance(this, getString(R.string.access_token));
        // This contains the MapView in XML and needs to be called after
        // the access token is configured.
        setContentView(R.layout.mmap2);
        ButterKnife.bind(this);
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference();
        if (getIntent().getExtras() != null && getIntent().getExtras().containsKey("RouteModule")) {
            Selected_route_module = getIntent().getExtras().getParcelable("RouteModule");
            String[] points = Selected_route_module.getStartPoint().split(",");
            SelectedRoute = new LatLng(Double.valueOf(points[0]), Double.valueOf(points[1]));
        }
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        disposable = new CompositeDisposable();
        Messege.setTextColor(getResources().getColor(R.color.carbon_yellow_a400));
        Messege.setText("Getting Things Ready ....");
        Check_If_Currnt_Route_Is_Usable();
    }

    @Override
    public void onMapReady(@NonNull final MapboxMap mapboxMap2) {
        // Map Is Ready
        MapActi2.this.mapboxMap = mapboxMap2;
        mapboxMap.setStyle(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {
            @SuppressLint("MissingPermission")
            @Override
            public void onStyleLoaded(@NonNull Style style) {
                // Map is set up and the style has loaded. Now you can add data or make other map adjustments
                enableLocationComponent(style);
                InitMapLocation();
            }
        });

        mapboxMap.addOnMapClickListener(point ->
        {
            if (snackbar != null || !Enapled || !CanClick) {
                return false;
            }

            SelectPoint.setGeoJson(Point.fromLngLat(point.getLongitude(), point.getLatitude()));
            Pick_up_Pos = point;
            mapboxMap.getStyle().getLayer("layer-id2").setProperties(PropertyFactory.visibility(com.mapbox.mapboxsdk.style.layers.Property.VISIBLE));

            if (net.mastrgamr.mbmapboxutils.PolyUtil.isLocationOnPath(new LatLng(point.getLatitude(), point.getLongitude()), Route_Points, false, 10.0)) {

                ShowSnack("Pleas Click Confirm To Notify Driver", R.color.carbon_green_400);
            } else {
                ShowSnack("The Point Is To Far From The Bus Route", R.color.carbon_red_a100);

            }
            return false;
        });
    }


    void Disaple_View() {
        Messege.setTextColor(getResources().getColor(R.color.carbon_red_500));
        Messege.setText("Selected Route Is Not Active yet Pleas Contact Driver....");
        Enapled = false;
    }

    @OnClick(R.id.Confirm)
    void Confirm() {
        if (Pick_up_Pos==null||!Enapled)
        {
            return;
        }
        CanClick = false;
        Messege.setTextColor(getResources().getColor(R.color.carbon_lightBlue_500));
        Messege.setText("pleas W8 While Sending Your Coordinates  ....");
        SendPickUpRequst();

    }

    @OnClick(R.id.Cancel)
    void Cancel() {
        if (!Enapled) {
            return;
        }
        CanClick = true;
        Messege.setTextColor(getResources().getColor(R.color.carbon_green_200));
        Messege.setText("Waiting For Location Picking ...");

    }

    void SendPickUpRequst() {
        databaseReference.child("Navigation").child(String.valueOf(Selected_route_module.
                getLineId())).child("PickupRequst").child(String.valueOf(MyID)).
                setValue(new PickupRequst("pending", String.valueOf(Pick_up_Pos.getLatitude()) + "," + String.valueOf(Pick_up_Pos.getLongitude()))).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                ShowSnack("TaskResult" + String.valueOf(task.isSuccessful()), R.color.carbon_orange_200);
                if (task.isSuccessful() && task.isComplete())
                {
                    HockUpConfirmationListner();
                }
            }
        });
    }

    void HockUpConfirmationListner() {


        Query query = databaseReference.child("Navigation").child(String.valueOf(Selected_route_module.
                getLineId())).child("PickupRequst").child(String.valueOf(MyID));
        Confermation_Value_Event_Listner = query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                PickupRequst pickupRequst = dataSnapshot.getValue(PickupRequst.class);
                if (pickupRequst != null && pickupRequst.getState() != null) {
                    if (pickupRequst.getState().equals("accepted"))
                    {
                        Messege.setTextColor(getResources().getColor(R.color.carbon_green_600));
                        Messege.setText("Request Confirmed ....");
                        Toast.makeText(MapActi2.this, "Confirmed", Toast.LENGTH_SHORT).show();
                        StartAnimation();

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void StartAnimation()
    {
        databaseReference.removeEventListener(Confermation_Value_Event_Listner); // Remove
        Animateee();
    }
    void Check_If_Currnt_Route_Is_Usable()
    {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Navigation");
        Query query = myRef.child(String.valueOf(Selected_route_module.getLineId()));
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    mapView.getMapAsync(MapActi2.this); // Wait Until Map is Ready
                } else {
                    Disaple_View();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    @SuppressLint("MissingPermission")
    void InitMapLocation() {
        AddBusMarker();
        Point dest = Point.fromLngLat(31.2827528, 29.9828841);
        GetRoute(dest);
    }

    @SuppressLint("MissingPermission")
    void AddBusMarker() {
        // Hidden
        SelectPoint = new GeoJsonSource("pick_id",
                Feature.fromGeometry(Point.fromLngLat(SelectedRoute.getLongitude(),
                        SelectedRoute.getLongitude())));


        mapboxMap.getStyle().addImage(("pick_icon_name"), BitmapFactory.decodeResource(getResources(), R.drawable.map_marker_light));
        mapboxMap.getStyle().addSource(SelectPoint);

        mapboxMap.getStyle().addLayer(new SymbolLayer("layer-id2", "pick_id")
                .withProperties(
                        PropertyFactory.iconImage("pick_icon_name"),
                        PropertyFactory.iconIgnorePlacement(true),
                        PropertyFactory.iconAllowOverlap(true),
                        PropertyFactory.visibility(com.mapbox.mapboxsdk.style.layers.Property.NONE)
                ));


        geoJsonSource = new GeoJsonSource("car_id",
                Feature.fromGeometry(Point.fromLngLat(SelectedRoute.getLongitude(),
                        SelectedRoute.getLongitude())));


        mapboxMap.getStyle().addImage(("car_icon_name"), BitmapFactory.decodeResource(getResources(), R.drawable.ic_car));
        mapboxMap.getStyle().addSource(geoJsonSource);
        mapboxMap.getStyle().addLayer(new SymbolLayer("layer-id", "car_id")
                .withProperties(
                        PropertyFactory.iconImage("car_icon_name"),
                        PropertyFactory.iconRotate((float) 180.0),
                        PropertyFactory.iconIgnorePlacement(true),
                        PropertyFactory.iconAllowOverlap(true),
                        PropertyFactory.visibility(com.mapbox.mapboxsdk.style.layers.Property.NONE)
                ));
    }

    void Add_End_Route_Point() {
        mapboxMap.addMarker(new MarkerOptions()
                .setTitle("Cs Academy..").setSnippet("Snip Academy........" +
                        "....").
                        setPosition(new LatLng(29.9828841,
                                31.2827528)));
    }

    void GetRoute(Point Destination) {
        @SuppressLint("MissingPermission") Point origin = Point.fromLngLat(SelectedRoute.getLongitude(), SelectedRoute.getLatitude());
        getRoute(origin, Destination);
    }

    private void getRoute(Point origin, Point destination) {
        Messege.setTextColor(getResources().getColor(R.color.carbon_yellow_a700));
        Messege.setText("Getting Your Travel Route ...");
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
                        Rout_Sucess(response.body());
                    }

                    @Override
                    public void onFailure(Call<DirectionsResponse> call, Throwable throwable) {
                        Toast.makeText(MapActi2.this, "Faild To Get Route", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void Rout_Sucess(DirectionsResponse directionsResponse) {
        currentRoute = directionsResponse.routes().get(0);
        // back Thread
        disposable.add
                (
                        io.reactivex.Observable.fromCallable(new Callable<ArrayList<LatLng>>() {

                            @Override
                            public ArrayList<LatLng> call() throws Exception {
                                ArrayList<LatLng> Ret = new ArrayList<LatLng>();
                                LineString lineString = LineString.fromPolyline(currentRoute.geometry(), Constants.PRECISION_6);
                                List<Point> coordinates = lineString.coordinates();
                                Timber.tag("Current Thread").v(Thread.currentThread().getName());
                                for (int i = 0; i < coordinates.size(); i++) {
                                    Ret.add(new LatLng(
                                            coordinates.get(i).latitude(),
                                            coordinates.get(i).longitude()));
                                }
                                return Ret;
                            }

                        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(s ->
                        {
                            Route_Points = s;
                            DrawRoute();
                        }, e -> {

                        })
                );
    }

    void DrawRoute() {
        if (navigationMapRoute != null) {
            navigationMapRoute.removeRoute();
        } else {
            navigationMapRoute = new NavigationMapRoute(null, mapView, mapboxMap, R.style.NavigationMapRoute);
        }
        navigationMapRoute.addRoute(currentRoute);
        Add_End_Route_Point();

        Messege.setTextColor(getResources().getColor(R.color.carbon_green_200));
        Messege.setText("Waiting For Location Picking ...");
    }

    // Marker Anmiation
    void Animateee() {

        handler = new Handler();
        index = -1;
        next = 1;
         runnable= () -> {
             if (index < Route_Points.size() - 1) {
                 index++;
                 next = index + 1;
             }
             if (index < Route_Points.size() - 1) {
                 startPosition = Route_Points.get(index);
                 endPosition = Route_Points.get(next);
             }
             if (index == 0) {

             }
             if (index == Route_Points.size() - 1) {

             }
             valueAnimator = ValueAnimator.ofFloat(0, 1);
             valueAnimator.setDuration(3000);
             valueAnimator.setInterpolator(new LinearInterpolator());
             valueAnimator.addUpdateListener(valueAnimator ->
                     {
                 //animate Point To to point  Start -> end 0 -> 1
                 v = valueAnimator.getAnimatedFraction();
                 lng = v * endPosition.getLongitude() + (1 - v)
                         * startPosition.getLongitude();
                 lat = v * endPosition.getLatitude() + (1 - v)
                         * startPosition.getLatitude();
                 LatLng newPos = new LatLng(lat, lng);
                 CarPosition = newPos;
                 Current_Bearing = NextFloat_Bearing;
                 NextFloat_Bearing = getBearing(startPosition, newPos);

                  if (mapboxMap!=null&&mapboxMap.getStyle()!=null)
                  {
                mapboxMap.getStyle().getLayer("layer-id").
                setProperties(PropertyFactory.iconRotate(NextFloat_Bearing),
                PropertyFactory.iconAnchor(com.mapbox.mapboxsdk.style.layers.Property.ICON_ANCHOR_CENTER),
                PropertyFactory.visibility(com.mapbox.mapboxsdk.style.layers.Property.VISIBLE));// Rotation And Anchor
                   }
                 geoJsonSource.setGeoJson(Point.fromLngLat(newPos.getLongitude(), newPos.getLatitude()));
                   }
             );
             valueAnimator.start();
             if (index != Route_Points.size() - 1)
             {
                 // if Route List Still Has Points   Re Do Operation With Delay OF 500 Ms
                 if (net.mastrgamr.mbmapboxutils.SphericalUtil.computeDistanceBetween(Route_Points.get(index), Pick_up_Pos) <= 20)
                 {
                     ShowSnack("Pleas Get In The Pus", R.color.carbon_orange_a400);
                 }
                 handler.postDelayed(runnable, 2500);
             }
         };
        handler.postDelayed(runnable, 1000);// First Delay Lunch ---
        }

    public void rotateMarker(final float toRotation)
    {
        valueAnimator.pause();
        ValueAnimator animator = ValueAnimator.ofFloat(Current_Bearing, toRotation);
        animator.setDuration(500);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float rotate = Float.parseFloat(animation.getAnimatedValue().toString());
                mapboxMap.getStyle().getLayer("layer-id").
                        setProperties(PropertyFactory.iconRotate(rotate),
                                PropertyFactory.iconAnchor(com.mapbox.mapboxsdk.style.layers.Property.ICON_ANCHOR_CENTER));// Rotation And Anchor
            }
        });
        animator.start();
    }

    // Get Marker Rotation
    private float getBearing(LatLng begin, LatLng end) {
        double lat = Math.abs(begin.getLatitude() - end.getLatitude());
        double lng = Math.abs(begin.getLongitude() - end.getLongitude());

        if (begin.getLatitude() < end.getLatitude() && begin.getLongitude() < end.getLongitude())
            return (float) (Math.toDegrees(Math.atan(lng / lat)));
        else if (begin.getLatitude() >= end.getLatitude() && begin.getLongitude() < end.getLongitude())
            return (float) ((90 - Math.toDegrees(Math.atan(lng / lat))) + 90);
        else if (begin.getLatitude() >= end.getLatitude() && begin.getLongitude() >= end.getLongitude())
            return (float) (Math.toDegrees(Math.atan(lng / lat)) + 180);
        else if (begin.getLatitude() < end.getLatitude() && begin.getLongitude() >= end.getLongitude())
            return (float) ((90 - Math.toDegrees(Math.atan(lng / lat))) + 270);
        return -1;
    }

    @OnClick(R.id.TrackBus_btn)
    public void TrackPusLocation() {
        if (!Enapled || CarPosition == null) {
            return;
        }
        mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition
                (new CameraPosition.Builder().target(CarPosition)
                        .zoom(15.5f).build()));
    }

    @SuppressLint("MissingPermission")
    @OnClick(R.id.Location_btn)
    public void TrackMy_position() {
        if (!Enapled || mapboxMap.getLocationComponent().getLastKnownLocation() == null) {
            return;
        }
        mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition
                (new CameraPosition.Builder().target(new LatLng(mapboxMap.getLocationComponent().getLastKnownLocation().getLatitude(), mapboxMap.getLocationComponent().getLastKnownLocation().getLongitude()))
                        .zoom(15.5f).build()));
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
        Snackbar.clearQueue();
        mapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (valueAnimator!=null)
        valueAnimator.removeAllUpdateListeners();
        if (handler!=null&&runnable!=null)
        handler.removeCallbacksAndMessages(runnable);
        Snackbar.clearQueue();
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

    private void ShowSnack(String Msg, int Color) {
        snackbar = new Snackbar(MapActi2.this, Msg, 1500);
        snackbar.getView().setBackgroundColor(getResources().getColor(Color));
        snackbar.setStyle(Snackbar.Style.Floating);
        snackbar.setTapOutsideToDismissEnabled(true);
        snackbar.setSwipeToDismissEnabled(true);
        snackbar.setGravity(Gravity.START | Gravity.BOTTOM);
        if (RootView != null)
            snackbar.show(RootView);
        snackbar.setOnDismissedListener(new Snackbar.OnDismissedListener() {
            @Override
            public void onDismiss() {
                snackbar = null;
            }
        });
    }
}
