package com.example.administrator.huashixingkong.fragment;


import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;


import com.example.administrator.huashixingkong.R;
import com.example.administrator.huashixingkong.activity.MainActivity;
import com.example.administrator.huashixingkong.model.MapObjectContainer;
import com.example.administrator.huashixingkong.model.MapObjectModel;
import com.example.administrator.huashixingkong.popup.TextPopup;
import com.ls.widgets.map.MapWidget;
import com.ls.widgets.map.config.GPSConfig;
import com.ls.widgets.map.config.MapGraphicsConfig;
import com.ls.widgets.map.config.OfflineMapConfig;
import com.ls.widgets.map.events.MapScrolledEvent;
import com.ls.widgets.map.events.MapTouchedEvent;
import com.ls.widgets.map.events.ObjectTouchEvent;
import com.ls.widgets.map.interfaces.Layer;
import com.ls.widgets.map.interfaces.MapEventsListener;
import com.ls.widgets.map.interfaces.OnMapScrollListener;
import com.ls.widgets.map.interfaces.OnMapTouchListener;
import com.ls.widgets.map.model.MapObject;
import com.ls.widgets.map.utils.PivotFactory;
import com.ls.widgets.map.utils.PivotFactory.PivotPosition;
import android.view.View.OnTouchListener;
import android.widget.RelativeLayout;

import java.util.ArrayList;


public class FragmentPage extends Fragment implements OnMapTouchListener, MapEventsListener {

    private View view;

    public static final int Map_ID=1;
    private static final int MIN_TIME_INTERVAL = 0;
    private static final int MIN_DISTANCE_IN_METERS = 0;
    private static final String TAG = "offlineMap";

    public final long LAYER1_ID=5;
    public final long LAYER2_ID=10;
    public final int position_id=112;

    private int nextObjectId;
    private int pinHeight;

    private MapWidget map=null;
    private MapObject my_position=null;

    private TextPopup mapObjectInfoPopup;
    private MapObjectContainer model;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_fragment_page,container,false);

        nextObjectId = 0;

        model = new MapObjectContainer();

        initMap(savedInstanceState);
        initModel();
        initMapObjects();
        initMapListeners();
//        initLocationManager();

        // Will show the position of the user on a map.
        // Do not forget to enable ACCESS_FINE_LOCATION and ACCESS_COARSE_LOCATION permission int the manifest.

        // Uncomment this if you are at Filitheyo island :)
        map.setShowMyPosition(true);

        map.centerMap();

        return view;
    }

//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        nextObjectId = 0;
//
//        model = new MapObjectContainer();
//
//        initMap(savedInstanceState);
//        initModel();
//        initMapObjects();
//        initMapListeners();
////        initLocationManager();
//
//        // Will show the position of the user on a map.
//        // Do not forget to enable ACCESS_FINE_LOCATION and ACCESS_COARSE_LOCATION permission int the manifest.
//
//        // Uncomment this if you are at Filitheyo island :)
//        map.setShowMyPosition(true);
//
//        map.centerMap();
//
//
//    }

    private void initMap(Bundle savedInstanceState){
        // In order to display the map on the screen you will need
        // to initialize widget and place it into layout.
        map = new MapWidget(savedInstanceState, this.getActivity(),
                "map", // root name of the map under assets folder.
                12); // initial zoom level

        map.setId(Map_ID);

        OfflineMapConfig config = map.getConfig();
        config.setPinchZoomEnabled(true); // Sets pinch gesture to zoom
        config.setFlingEnabled(true);    // Sets inertial scrolling of the map
        config.setMaxZoomLevelLimit(13);
        config.setZoomBtnsVisible(true); // Sets embedded zoom buttons visible

        // Configuration of GPS receiver
        GPSConfig gpsConfig = config.getGpsConfig();
        gpsConfig.setPassiveMode(false);
        gpsConfig.setGPSUpdateInterval(500, 5);

        // Configuration of position marker
        MapGraphicsConfig graphicsConfig = config.getGraphicsConfig();
        graphicsConfig.setAccuracyAreaColor(0x550000FF); // Blue with transparency
        graphicsConfig.setAccuracyAreaBorderColor(Color.BLUE); // Blue without transparency

         RelativeLayout layout=(RelativeLayout) view.findViewById(R.id.rootLayout);
        // Adding the map to the layout
        layout.addView(map, 0);
        layout.setBackgroundColor(Color.parseColor("#EEF7F5"));

        //show user's position
        map.setShowMyPosition(true);

        // Adding layers in order to put there some map objects
        map.createLayer(LAYER1_ID); // you will need layer id's in order to access particular layer
        map.createLayer(LAYER2_ID);
    }

    private void initModel(){
        MapObjectModel objectModel=new MapObjectModel(0, 1230,1240, R.drawable.map_icon_building, "第一课室");
        model.addObject(objectModel);
        objectModel=new MapObjectModel(1, 1990, 1020,R.drawable.map_icon_kids, "陶园");
        model.addObject(objectModel);

    }

    private void initMapObjects(){
        mapObjectInfoPopup =new TextPopup(this.getActivity(), (RelativeLayout)view.findViewById(R.id.rootLayout));

        Layer layer1=map.getLayerById(LAYER1_ID);
        Layer layer2=map.getLayerById(LAYER2_ID);

        for (int i=0; i<model.size(); ++i) {
            addNotScalableMapObject(model.getObject(i), layer1);
        }
        addMapPositionObject(position_id, layer2);
        layer2.setVisible(false);

    }


    private void addMapPositionObject(int positionObject_ID,Layer layer){
        // Getting the drawable of the map object
        Drawable drawable = getResources().getDrawable(R.drawable.maps_blue_dot);
        // Creating the map object
        my_position = new MapObject(positionObject_ID, // id, will be passed to the listener when user clicks on it
                drawable,
                new Point(0, 0), // coordinates in original map coordinate system.
                // Pivot point of center of the drawable in the drawable's coordinate system.
                PivotFactory.createPivotPoint(drawable, PivotPosition.PIVOT_CENTER),
                true, // This object will be passed to the listener
                true); // is not scalable. It will have the same size on each zoom level
        layer.addMapObject(my_position);

    }

    private void addNotScalableMapObject(MapObjectModel objectModel,  Layer layer)
    {
        if (objectModel.getLocation() != null) {
            addNotScalableMapObject(objectModel.getLocation(),objectModel.getIcon_id(), layer);
        } else {
            addNotScalableMapObject(objectModel.getX(), objectModel.getY(), objectModel.getIcon_id(), layer);
        }
    }

    private void addNotScalableMapObject(Location location,int icon_id , Layer layer) {
        if (location == null)
            return;

        // Getting the drawable of the map object
        Drawable drawable = getResources().getDrawable(icon_id);
        // Creating the map object
        MapObject object1 = new MapObject(Integer.valueOf(nextObjectId), // id, will be passed to the listener when user clicks on it
                drawable,
                new Point(0, 0), // coordinates in original map coordinate system.
                // Pivot point of center of the drawable in the drawable's coordinate system.
                PivotFactory.createPivotPoint(drawable, PivotPosition.PIVOT_CENTER),
                true, // This object will be passed to the listener
                true); // is not scalable. It will have the same size on each zoom level
        layer.addMapObject(object1);

        // Will crash if you try to move before adding to the layer.
        object1.moveTo(location);
        nextObjectId += 1;
    }

    private void addNotScalableMapObject(int x, int y, int icon_id, Layer layer)
    {
        // Getting the drawable of the map object
        Drawable drawable = getResources().getDrawable(icon_id);
        pinHeight = drawable.getIntrinsicHeight();
        // Creating the map object
        MapObject object1 = new MapObject(Integer.valueOf(nextObjectId), // id, will be passed to the listener when user clicks on it
                drawable,
                new Point(x, y), // coordinates in original map coordinate system.
                // Pivot point of center of the drawable in the drawable's coordinate system.
                PivotFactory.createPivotPoint(drawable, PivotPosition.PIVOT_CENTER),
                true, // This object will be passed to the listener
                true); // is not scalable. It will have the same size on each zoom level

        // Adding object to layer
        layer.addMapObject(object1);
        nextObjectId += 1;
    }


    private void initMapListeners(){
        // In order to receive MapObject touch events we need to set listener
        map.setOnMapTouchListener(this);

        // In order to receive pre and post zoom events we need to set MapEventsListener
        map.addMapEventsListener(this);

        // In order to receive map scroll events we set OnMapScrollListener
        map.setOnMapScrolledListener(new OnMapScrollListener()
        {
            public void onScrolledEvent(MapWidget v, MapScrolledEvent event)
            {
                handleOnMapScroll(v, event);
            }
        });

    }

    private void handleOnMapScroll(MapWidget v, MapScrolledEvent event)
    {
        // When user scrolls the map we receive scroll events
        // This is useful when need to move some object together with the map

        int dx = event.getDX(); // Number of pixels that user has scrolled horizontally
        int dy = event.getDY(); // Number of pixels that user has scrolled vertically

        if (mapObjectInfoPopup.isVisible()) {
            mapObjectInfoPopup.moveBy(dx, dy);
        }
    }


    @Override
    public void onTouch(MapWidget v, MapTouchedEvent event) {
        // Get touched object events from the MapTouchEvent
        ArrayList<ObjectTouchEvent> touchedObjs = event.getTouchedObjectIds();
        if (touchedObjs.size()>0) {
            int xInMapCoords = event.getMapX();
            int yInMapCoords = event.getMapY();
            int xInScreenCoords = event.getScreenX();
            int yInScreenCoords = event.getScreenY();

            ObjectTouchEvent objectTouchEvent = event.getTouchedObjectIds().get(0);

            // Due to a bug this is not actually the layer id, but index of the layer in layers array.
            // Will be fixed in the next release.
            long layerId = objectTouchEvent.getLayerId();
            Integer objectId = (Integer)objectTouchEvent.getObjectId();

            // User has touched one or more map object
            // We will take the first one to show in the toast message.
            String message = "You touched the object with id: " + objectId + " on layer: " + layerId +
                    " mapX: " + xInMapCoords + " mapY: " + yInMapCoords + " screenX: " + xInScreenCoords + " screenY: " +
                    yInScreenCoords;

            Log.d(TAG, message);
            MapObjectModel objectModel = model.getObjectById(objectId.intValue());

            if (objectModel != null) {
                // This is a case when we want to show popup info exactly above the pin image

                float density = getResources().getDisplayMetrics().density;
                int imgHeight = (int) (pinHeight / density / 2);

                // Calculating position of popup on the screen
                int x = xToScreenCoords(objectModel.getX());
                int y = yToScreenCoords(objectModel.getY()) - imgHeight;

                // Show it
                showLocationsPopup(x, y, objectModel.getCaption());
            } else {
                // This is a case when we want to show popup where the user has touched.
                showLocationsPopup(xInScreenCoords, yInScreenCoords, "Shows where user touched");
            }

            // Hint: If user touched more than one object you can show the dialog in which ask
            // the user to select concrete object

        } else {
            if (mapObjectInfoPopup != null) {
                mapObjectInfoPopup.hide();
            }
        }
    }

    private void showLocationsPopup(int x, int y, String text)
    {
        RelativeLayout mapLayout = (RelativeLayout) view.findViewById(R.id.rootLayout);

        if (mapObjectInfoPopup != null)
        {
            mapObjectInfoPopup.hide();
        }

        ((TextPopup) mapObjectInfoPopup).setIcon((BitmapDrawable) getResources().getDrawable(R.drawable.map_popup_arrow));
        ((TextPopup) mapObjectInfoPopup).setText(text);

        mapObjectInfoPopup.setOnClickListener(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (mapObjectInfoPopup != null) {
                        mapObjectInfoPopup.hide();
                    }
                }

                return false;
            }
        });

        ((TextPopup) mapObjectInfoPopup).show(mapLayout, x, y);
    }



    /***
     * Transforms coordinate in map coordinate system to screen coordinate system
     * @param mapCoord - X in map coordinate in pixels.
     * @return X coordinate in screen coordinates. You can use this value to display any object on the screen.
     */
    private int xToScreenCoords(int mapCoord)
    {
        return (int)(mapCoord *  map.getScale() - map.getScrollX());
    }

    private int yToScreenCoords(int mapCoord)
    {
        return (int)(mapCoord *  map.getScale() - map.getScrollY());
    }


    @Override
    public void onPreZoomIn() {
        Log.i(TAG, "onPreZoomIn()");

        if (mapObjectInfoPopup != null) {
            mapObjectInfoPopup.hide();
        }
    }

    @Override
    public void onPostZoomIn() {
        Log.i(TAG, "onPostZoomIn()");
    }

    @Override
    public void onPreZoomOut() {
        Log.i(TAG, "onPreZoomOut()");

        if (mapObjectInfoPopup != null) {
            mapObjectInfoPopup.hide();
        }
    }

    @Override
    public void onPostZoomOut() {
        Log.i(TAG, "onPostZoomOut()");
    }
}
