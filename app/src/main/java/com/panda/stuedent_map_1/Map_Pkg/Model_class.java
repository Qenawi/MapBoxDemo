package com.panda.stuedent_map_1.Map_Pkg;

import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute;

import java.util.ArrayList;
import java.util.List;

/**
 *  models are modular classes representing the data,
 *  state and business logic of our 'Tic-Tac-Toe' application.
 *  They describe the elements which are the base of our whole app '(players, cells)',
 *  they check whether the 'game is still ongoing or has ended' and know what actions
 *  to take when a game is finished,
 *  all of the logic is written, now all we need is
 *  a component that will orchestrate the whole game.
 *  . That’s the View Model, which we’ll build
 */
public class Model_class
{
    public MapboxMap mapboxMap; // MapBox Main Object
    public NavigationMapRoute navigationMapRoute = null; // Get Route And Draw IT
    public DirectionsRoute currentRoute = null; // Save Acquired Route And Save It
    public List<LatLng> Route_Points = null;
    public com.mapbox.mapboxsdk.annotations.Marker SelectPoint = null;// PickUp Point
    public com.mapbox.mapboxsdk.annotations.Marker Pus = null;// Pus Pin
    public Model_class()
    {
        Route_Points=new ArrayList<>();
    }


}