package com.panda.stuedent_map_1.Map_Pkg;

import android.arch.lifecycle.ViewModel;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.maps.MapboxMap;

/*
Cant Know Any Thing About View
 */

/***
 * Responsible for wrapping the model and preparing observable data needed by the view.
 * It also provides hooks for the view to pass events to the model.
 * An important thing to keep in mind is that the View Model is not tied to the view.
     ...................................
 * View Model will contain a LiveData instance which will be observed by our View. More concretely,
 * the View Model will notify the View about when the Game ends,
 * this will allow the View to update the UI
 */
public class ViewModel_class  extends ViewModel
{
  //Contain DataModel
  // Contain CallBack To The View  and Other NetworkMangers And Data
   private Model_class MapModel;
   public void init(MapboxMap MapBoxObject)
   {
       MapModel=new Model_class();
       MapModel.mapboxMap=MapBoxObject;
   }
}
