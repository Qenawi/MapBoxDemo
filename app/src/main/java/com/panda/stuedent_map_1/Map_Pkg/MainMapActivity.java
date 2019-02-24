package com.panda.stuedent_map_1.Map_Pkg;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.Toast;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.*;
import com.panda.stuedent_map_1.Map_Pkg.MapUtils.PolyUtil;
import com.panda.stuedent_map_1.Map_Pkg.Models.directions;
import com.panda.stuedent_map_1.R;
import qenawi.panda.a_predator.network_Handeler.A_Predator_NWM;
import qenawi.panda.a_predator.network_Handeler.A_Predator_NetWorkManger;
import qenawi.panda.a_predator.network_Handeler.CService_DBase;

import java.util.ArrayList;
import java.util.HashMap;

public class MainMapActivity extends AppCompatActivity implements OnMapReadyCallback
{
    Marker start, end, animate;
    public A_Predator_NetWorkManger a_predator_netWorkManger;
    static final LatLng Destination = new LatLng(30.091887, 31.359715);//Almaza Airport
    static final LatLng PickUpPoint1 = new LatLng(-73.989, 40.733);//sarya elkoba  gesr elswis (+)
    static final LatLng PickUpPoint2 = new LatLng(30.069481, 31.280790);// Al3aBasya

    ArrayList<String> routs;
    int Selected_route = 0;
    private Polyline line;
    private GoogleMap mMap;


    /**
     * sRC
     * https://www.oodlestechnologies.com/blogs/How-to-smoothly-move-and-rotate-a-marker-in-google-map
     *
     * @param
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.maps1);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
        a_predator_netWorkManger = new A_Predator_NetWorkManger(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng startpos = new LatLng(-35.016, 143.321);
        LatLng endpos = new LatLng(-32.491, 147.309);
        googleMap.setMinZoomPreference(10f);
        mMap = googleMap;
        mMap.animateCamera(CameraUpdateFactory.newLatLng(PickUpPoint1));

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
             //   FetchRoute();
                ArrayList<LatLng> data=new ArrayList<>();
                data.addAll(PolyUtil.decode("_urwFt}qbMuLp_@jWzPoHhRMK")) ;
                PolylineOptions sd = new PolylineOptions();
                sd.add(new LatLng(PickUpPoint1.latitude, PickUpPoint1.longitude));
                sd.width(5);
                sd.color(Color.CYAN);
                sd.addAll(data);
                mMap.addPolyline(sd);
                Log.v("101",data.size()+" ");
                mMap.animateCamera(CameraUpdateFactory.newLatLng(PickUpPoint1));

            }
        });
    }

    public void moveVechile(final Marker myMarker, final LatLng finalPosition) {
        final LatLng startPosition = myMarker.getPosition();
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        final Interpolator interpolator = new AccelerateDecelerateInterpolator();
        final float durationInMs = 8000;
        final boolean hideMarker = false;
        handler.post(new Runnable() {
            long elapsed;
            float t;
            float v;

            @Override
            public void run() {
                // Calculate progress using interpolator
                elapsed = SystemClock.uptimeMillis() - start;
                t = elapsed / durationInMs;
                v = interpolator.getInterpolation(t);

                LatLng currentPosition = new LatLng(
                        startPosition.latitude * (1 - t) + (finalPosition.latitude) * t,
                        startPosition.longitude * (1 - t) + (finalPosition.longitude) * t);
                myMarker.setPosition(currentPosition);
                // myMarker.setRotation(finalPosition.getBearing());


                // Repeat till progress is completeelse
                if (t < 1) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                    // handler.postDelayed(this, 100);
                } else {
                    if (hideMarker) {
                        myMarker.setVisible(false);
                    } else {
                        myMarker.setVisible(true);
                    }
                }
            }
        });


    }

    public void FetchRoute() {
        Toast.makeText(this, "Loading", Toast.LENGTH_SHORT).show();
        String URL = "https://maps.googleapis.com/maps/api/directions/json";
        HashMap<String, String> Headers = new HashMap<>();
        HashMap<String, Object> ReqBody = new HashMap<String, Object>() {
            {
                put("origin", String.valueOf(PickUpPoint1.latitude) + "," + String.valueOf(PickUpPoint1.longitude));
                put("destination", String.valueOf(Destination.latitude) + "," + String.valueOf(Destination.longitude));
                put("optimize", "true");
                put("key", "AIzaSyANLskwqWyFkfNcfLZtao6eFCi--z3Kwz8");
            }
        };
        a_predator_netWorkManger.FetchData(new directions(), Headers, URL, ReqBody, new A_Predator_NWM.RequistResuiltCallBack() {
            @Override
            public <T extends CService_DBase> void Sucess(T Resposne) {

                directions directions = (com.panda.stuedent_map_1.Map_Pkg.Models.directions) Resposne;
                ShowRoad_(directions);
            }

            @Override
            public void Faild(Throwable error) {
                error.printStackTrace();
            }
        });


    }

    public void ShowRoad_(directions road_data) {
        Toast.makeText(this, "Loading Road", Toast.LENGTH_SHORT).show();

        final ArrayList<LatLng> road = new ArrayList<>();
        routs = new ArrayList<>();
        if (road_data.getRoutes().size() > 1) {
            for (int i = 0; i < road_data.getRoutes().size(); i++) {
                routs.add(road_data.getRoutes().get(i).getSummary());
            }

        }
        PolylineOptions sd = new PolylineOptions();
        sd.add(new LatLng(PickUpPoint1.latitude, PickUpPoint1.longitude));
        Log.v("exo", "boxi");
        for (int i = 0; i < road_data.getRoutes().get(Selected_route).getLegs().get(0).getSteps().size(); i++) {
            sd.addAll(PolyUtil.decode(road_data.getRoutes().get(Selected_route).getLegs().get(0).getSteps().get(i).getPolyline().getPoints()));
        }
        sd.add(Destination);
        sd.width(5);
        sd.color(Color.CYAN);
        if (line != null) {
            line.setVisible(false);
            line.remove();
            line = null;
        }
        line = mMap.addPolyline(sd);
        mMap.animateCamera(CameraUpdateFactory.newLatLng(PickUpPoint1));

    }
}
