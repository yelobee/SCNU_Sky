package com.example.administrator.huashixingkong.fragment;


import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;


import com.example.administrator.huashixingkong.R;
import com.example.administrator.huashixingkong.activity.PositionDetailActivity;
import com.example.administrator.huashixingkong.activity.AnnounceActivity;
import com.example.administrator.huashixingkong.model.ActiveActivity;
import com.example.administrator.huashixingkong.model.ActivityData;
import com.example.administrator.huashixingkong.model.BuildingPositions;
import com.example.administrator.huashixingkong.model.MapObjectContainer;
import com.example.administrator.huashixingkong.model.MapObjectModel;
import com.example.administrator.huashixingkong.model.PositionData;
import com.example.administrator.huashixingkong.model.SkyStar;
import com.example.administrator.huashixingkong.popup.TextPopup;
import com.example.administrator.huashixingkong.tools.HttpHelp;
import com.example.administrator.huashixingkong.tools.TxtDataController;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ls.widgets.map.MapWidget;
import com.ls.widgets.map.config.GPSConfig;
import com.ls.widgets.map.config.MapGraphicsConfig;
import com.ls.widgets.map.config.OfflineMapConfig;
import com.ls.widgets.map.events.MapScrolledEvent;
import com.ls.widgets.map.events.MapTouchedEvent;
import com.ls.widgets.map.events.ObjectTouchEvent;
import com.ls.widgets.map.interfaces.Layer;
import com.ls.widgets.map.interfaces.MapEventsListener;
import com.ls.widgets.map.interfaces.OnMapDoubleTapListener;
import com.ls.widgets.map.interfaces.OnMapScrollListener;
import com.ls.widgets.map.interfaces.OnMapTouchListener;
import com.ls.widgets.map.location.PositionMarker;
import com.ls.widgets.map.model.MapLayer;
import com.ls.widgets.map.model.MapObject;
import com.ls.widgets.map.utils.PivotFactory;
import com.ls.widgets.map.utils.PivotFactory.PivotPosition;
import android.view.View.OnTouchListener;
import android.widget.RelativeLayout;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import android.os.Handler;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;

import org.apache.http.util.EncodingUtils;


public class FragmentPage extends Fragment implements OnMapTouchListener, MapEventsListener, BDLocationListener {

    private View view;

    private static final int Map_ID=1;
    private static final int Sky_Map_ID=2;
    private static final int BackGround_Map_ID=3;
    private static final int MIN_TIME_INTERVAL = 0;
    private static final int MIN_DISTANCE_IN_METERS = 0;
    private static final String TAG = "offlineMap";

    public final long LAYER1_ID=5;
    public final long LAYER2_ID=10;
    public final int position_id=112;

    public final long SKY_LAYER1_ID=7;

    private int nextObjectId;
    private int pinHeight;

    private MapWidget map=null;
    private MapWidget skymap=null;
    private MapWidget backgroundmap=null;
    private MapObject my_position=null;
    private PositionMarker myPosition=null;

    private TextPopup mapObjectInfoPopup;
    private MapObjectContainer model;

    //baiduSDK
    private static final String POSITION_LAT="position_lat";
    private static final String POSITION_LONG="position_long";
    private static final String POSITION_RADIUS="position_radius";
    private LocationClient mLocationClient=null;
    private static final int SCAN_SPAN=5000;
    private Location user_position=null;

    private Handler mHandler=null;

    //position json loading
    private  String path="/scnu/map_positions_config.txt";//文件路径
    private TxtDataController rd;
    private Gson gson=new Gson();//gson对象
    private String gsonData=null;//从文件或网络传来的json数据
    private PositionData pData=null;//gson所转化的数据对象
    private List<BuildingPositions> positionDatas=null;
    private Thread mThread=null;// 用于下载在网络中positionList数据

    //activity
    private ActivityData aData=null;
    private List<ActiveActivity> activityDatas=null;

    //sky
    private TxtDataController readSkyData;
    private String skypath="/scnu/scnu_sky.txt";//文件路径
    private String skyData=null;
    private List<SkyStar> skyStarList=null;
    private MapObjectContainer skymodel;



    private RelativeLayout layout;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_fragment_page,container,false);

        //baiduSDK
        mLocationClient=new LocationClient(getActivity().getApplicationContext());
        mLocationClient.registerLocationListener(this);

        //mAppWidget
        nextObjectId = 0;

        model = new MapObjectContainer();
        skymodel=new MapObjectContainer();

        mHandler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 1:
                        myPosition.setAccuracy(msg.getData().getFloat(POSITION_RADIUS));

                        Layer layer = map.getLayerById(LAYER2_ID);
                        layer.setVisible(true);

                        //Log.i(TAG,""+msg.getData().getDouble(POSITION_LAT)+" "+msg.getData().getDouble(POSITION_LONG)+" "+msg.getData().getFloat(POSITION_RADIUS));
                        Location temp = new Location("test");
                        temp.setLatitude(msg.getData().getDouble(POSITION_LAT));
                        temp.setLongitude(msg.getData().getDouble(POSITION_LONG));
                        user_position=temp;

                        myPosition.moveTo(temp);
                        break;

                    case 2:
                        if (gsonData != null) {
                            pData = gson.fromJson(gsonData, PositionData.class);
                            positionDatas = pData.getPositions();
                            MapObjectModel objectModel = null;
                            //将json所得数据存入model中
                            for (BuildingPositions bp : positionDatas) {
                                objectModel = new MapObjectModel(bp);
                                model.addObject(objectModel);
                                addNotScalableMapObject(objectModel, map.getLayerById(LAYER2_ID));
                            }
                        }
                        break;

                    case 3:
                        //zoomout,隐藏map，及其layer，关闭用户定位，显示skymap & bringtofront
                        map.getLayerById(LAYER1_ID).setVisible(false);
                        map.getLayerById(LAYER2_ID).setVisible(false);
                        mLocationClient.stop();
                        //在隐藏map前，需clearAnimation，否则会隐藏无效
                        map.clearAnimation();
                        map.setVisibility(View.GONE);
                        skymap.setVisibility(View.VISIBLE);
                        layout.bringChildToFront(skymap);
                        skymap.zoomOut();
                        break;
                    case 4:
                        //zoomin,隐藏skymap，并显示map，及其layer，并启动定位，最后bringtofront
                        map.getLayerById(LAYER1_ID).setVisible(true);
                        map.getLayerById(LAYER2_ID).setVisible(true);
                        mLocationClient.start();
                        //在隐藏map前，需clearAnimation，否则会隐藏无效
                        skymap.setVisibility(View.GONE);
                        map.setVisibility(View.VISIBLE);
                        layout.bringChildToFront(backgroundmap);
                        layout.bringChildToFront(map);
                        map.zoomIn();
                        break;
                    case 5:
                        String activeActivityJson=(String)msg.obj;
                        if (activeActivityJson!=null){
                            //aData=gson.fromJson(activeActivityJson, new TypeToken<List<ActiveActivity>>(){}.getType());
                            //activityDatas=aData.getActivitys();
                            activityDatas=gson.fromJson(activeActivityJson, new TypeToken<List<ActiveActivity>>(){}.getType());
                            MapObjectModel objectModel=null;
                            for (ActiveActivity aa:activityDatas){
                                objectModel=new MapObjectModel(aa);
                                model.addObject(objectModel);
                                //addNotScalableMapObject(objectModel,map.getLayerById(LAYER2_ID));
                                addActiveActivityMapObject(objectModel,map.getLayerById(LAYER2_ID));
                            }

                        }
                        break;
                    case 6:
                        if (skyData!=null){
                            skyStarList=gson.fromJson(skyData,new TypeToken<List<SkyStar>>(){}.getType());
                            MapObjectModel objectModel=null;
                            for (SkyStar ss:skyStarList){
                                objectModel=new MapObjectModel(ss);
                                skymodel.addObject(objectModel);
                                addScnuSkyMapObject(objectModel,skymap.getLayerById(SKY_LAYER1_ID));
                            }
                        }
                        break;
                }
            }
        };
        initMap(savedInstanceState);
        initModel();
        initMapObjects();
        initMapActivity();
        initSky();
        initMapListeners();


        //init baiduSDK
        initLocation();

        //start LocationClient
        mLocationClient.start();



        // Will show the position of the user on a map.
        // Do not forget to enable ACCESS_FINE_LOCATION and ACCESS_COARSE_LOCATION permission int the manifest.

        // Uncomment this if you are at Filitheyo island :)

        map.centerMap();
        skymap.centerMap();

        return view;
    }


    @Override
    public void onPause() {
        super.onPause();
        mLocationClient.stop();
    }

    @Override
    public void onResume() {
        super.onResume();
        mLocationClient.start();
    }

    @Override
    public void onStop() {
        super.onStop();
        mLocationClient.stop();
    }

    //baiduSDK
    private void initLocation(){
        LocationClientOption option=new LocationClientOption();
        option.setLocationMode(LocationMode.Hight_Accuracy);
        option.setCoorType("bd09ll");
        option.setOpenGps(true);
        option.setScanSpan(SCAN_SPAN);

        mLocationClient.setLocOption(option);
    }


    private void initMap(Bundle savedInstanceState){
        // In order to display the map on the screen you will need
        // to initialize widget and place it into layout.
        map = new MapWidget(savedInstanceState, this.getActivity(),
                "map", // root name of the map under assets folder.
                12); // initial zoom level
        skymap = new MapWidget(savedInstanceState, this.getActivity(),
                "scnusky", // root name of the map under assets folder.
                12); // initial zoom level
        backgroundmap = new MapWidget(savedInstanceState, this.getActivity(),
                "background", // root name of the map under assets folder.
                12); // initial zoom level

        map.setId(Map_ID);
        skymap.setId(Sky_Map_ID);
        backgroundmap.setId(BackGround_Map_ID);

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
        graphicsConfig.setAccuracyAreaColor(0x55FF0000); //Transparent Red
        graphicsConfig.setAccuracyAreaBorderColor(Color.RED);
        graphicsConfig.setDotPointerDrawableId(R.drawable.round_pointer);
        graphicsConfig.setArrowPointerDrawableId(R.drawable.arrow_pointer);

        //skymap
        OfflineMapConfig config2=skymap.getConfig();
        config2.setFlingEnabled(true);
        config2.setPinchZoomEnabled(true);
        config2.setMinZoomLevelLimit(12);
        config2.setMinZoomLevelLimit(12);
        config2.setZoomBtnsVisible(false);

        //backgroundmap
        OfflineMapConfig config3=backgroundmap.getConfig();
        config3.setFlingEnabled(false);
        config3.setPinchZoomEnabled(false);
        config3.setMinZoomLevelLimit(12);
        config3.setMinZoomLevelLimit(12);
        config3.setZoomBtnsVisible(false);

        // Adding the map to the layout
//        RelativeLayout layout=(RelativeLayout) findViewById(R.id.rootLayout);
        layout=(RelativeLayout) view.findViewById(R.id.rootLayout);
        layout.addView(map, 0);
        layout.addView(skymap, 1);
        layout.addView(backgroundmap, 2);
        layout.setBackgroundColor(Color.parseColor("#EEF7F5"));
        skymap.setVisibility(View.GONE);
        layout.bringChildToFront(map);

        // Adding layers in order to put there some map objects
        map.createLayer(LAYER1_ID); // you will need layer id's in order to access particular layer
        map.createLayer(LAYER2_ID);

        skymap.createLayer(SKY_LAYER1_ID);
    }

    private void initModel(){
        //先监测本地是否有缓存数据，有即读取并显示，无则开启线程下载显示，并将下载所得的json数据缓存起来
        File file = new File(Environment.getExternalStorageDirectory()
                .getPath() + path);
        rd = new TxtDataController(path);
        // 若数据文件在本地，直接读取
        if (file.exists()) {
            try {
                gsonData = rd.getData();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            Message msg = new Message();
            msg.what = 2;
            mHandler.sendMessage(msg);
        }
        // 若文件不在本地，开线程下载json文件
        else {
            new Thread(runnable).start();
            if (mThread == null) {
                mThread = new Thread(runnable);
                mThread.start();
            } else {
                Toast.makeText(getActivity().getApplicationContext(), "线程已启动",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    private void initMapObjects(){
        mapObjectInfoPopup =new TextPopup(this.getActivity(), (RelativeLayout)view.findViewById(R.id.rootLayout));

        Layer layer1=map.getLayerById(LAYER1_ID);
        MapLayer layer2=(MapLayer)map.getLayerById(LAYER2_ID);

        for (int i=0; i<model.size(); ++i) {
            addNotScalableMapObject(model.getObject(i), layer1);
        }
        addMapPositionObject(layer2);
        layer2.setVisible(false);

    }

    private void initMapActivity(){
//        Layer layer=map.getLayerById(LAYER1_ID);
//        addNotScalableMapObject(1705,650,R.drawable.maps_blue_dot,layer);
        new Thread(new activityMessage()).start();
    }

    private void initSky(){
        //skymap的layer
        readSkyData=new TxtDataController(skypath);
        Layer skylayer1=skymap.getLayerById(SKY_LAYER1_ID);
        try {
            skyData=readSkyData.getData();
        }catch (IOException e){
            e.printStackTrace();
        }
        Message msg=new Message();
        msg.what=6;
        mHandler.sendMessage(msg);
    }


    private void addMapPositionObject(MapLayer layer){
        // Getting the drawable of the map object
        Drawable drawable = getResources().getDrawable(R.drawable.round_pointer);
        // Creating the map object

        //设置范围
        MapGraphicsConfig graphics=map.getConfig().getGraphicsConfig();

        Drawable dot=this.getResources().getDrawable(graphics.getDotPointerDrawableId());
        Drawable arrow=this.getResources().getDrawable(graphics.getArrowPointerDrawableId());

        myPosition=new PositionMarker(map, position_id, dot, arrow);
        myPosition.setColor(graphics.getAccuracyAreaColor(), graphics.getAccuracyAreaBorderColor());

        layer.addMapObject(myPosition);

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

    //校园活动加载
    private void addActiveActivityMapObject(MapObjectModel objectModel,Layer layer){
        Drawable drawable = getResources().getDrawable(objectModel.getIcon_id());
        pinHeight = drawable.getIntrinsicHeight();
        MapObject object1 = new MapObject(Integer.valueOf(objectModel.getId()), // id, will be passed to the listener when user clicks on it
                drawable,
                new Point(objectModel.getX(), objectModel.getY()), // coordinates in original map coordinate system.
                // Pivot point of center of the drawable in the drawable's coordinate system.
                PivotFactory.createPivotPoint(drawable, PivotPosition.PIVOT_CENTER),
                true, // This object will be passed to the listener
                true); // is not scalable. It will have the same size on each zoom level
        layer.addMapObject(object1);
    }

    private void addScnuSkyMapObject(MapObjectModel objectModel,Layer layer){
        Drawable drawable = getResources().getDrawable(objectModel.getIcon_id());
        pinHeight = drawable.getIntrinsicHeight();
        MapObject object1 = new MapObject(Integer.valueOf(objectModel.getId()), // id, will be passed to the listener when user clicks on it
                drawable,
                new Point(objectModel.getX(), objectModel.getY()), // coordinates in original map coordinate system.
                // Pivot point of center of the drawable in the drawable's coordinate system.
                PivotFactory.createPivotPoint(drawable, PivotPosition.PIVOT_CENTER),
                true, // This object will be passed to the listener
                true); // is not scalable. It will have the same size on each zoom level
        layer.addMapObject(object1);
    }


    private void initMapListeners(){
        //校园活动地图点击事件
        // 为了接收MapObject触摸事件,需要设置地图点击监听器
        map.setOnMapTouchListener(this);

        //为了接收预先/准确zoom事件,需要设置地图事件监听器
        map.addMapEventsListener(this);

        // In order to receive map scroll events we set OnMapScrollListener
        //为了接收地图滚动事件,需要设置地图滚动监听器
        map.setOnMapScrolledListener(new OnMapScrollListener() {
            public void onScrolledEvent(MapWidget v, MapScrolledEvent event) {
                handleOnMapScroll(v, event);
            }
        });
        //
        map.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (user_position != null) {
                    map.scrollMapTo(user_position);
                    return true;
                }
                return false;
            }
        });


        //华师星空点击事件
        skymap.addMapEventsListener(this);

        skymap.setOnMapScrolledListener(new OnMapScrollListener() {

            @Override
            public void onScrolledEvent(MapWidget v, MapScrolledEvent event) {
                handleOnMapScroll(v, event);
            }
        });

        skymap.setOnDoubleTapListener(new OnMapDoubleTapListener() {
            @Override
            public boolean onDoubleTap(MapWidget v, MapTouchedEvent event) {
                Intent intent=new Intent();
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                intent.setClass(getActivity(), AnnounceActivity.class);
                startActivity(intent);
                return true;
            }
        });

        skymap.setOnMapTouchListener(new OnMapTouchListener() {
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
                    MapObjectModel objectModel = skymodel.getObjectById(objectId.intValue());

                    if (objectModel != null) {
                        // This is a case when we want to show popup info exactly above the pin image

                        float density = getResources().getDisplayMetrics().density;
                        int imgHeight = (int) (pinHeight / density / 2);

                        // Calculating position of popup on the screen
                        int x = xToScreenCoords_sky(objectModel.getX());
                        int y = yToScreenCoords_sky(objectModel.getY()) - imgHeight;

                        // Show it
                        showLocationPopUpWithoutArrow(x, y, objectModel.getCaption());

                    } else {
                        // This is a case when we want to show popup where the user has touched.
                        showLocationPopUpWithoutArrow(xInScreenCoords, yInScreenCoords, "Shows where user touched");
                    }

                    // Hint: If user touched more than one object you can show the dialog in which ask
                    // the user to select concrete object

                } else {
                    if (mapObjectInfoPopup != null) {
                        mapObjectInfoPopup.hide();
                    }
                }
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
                //detail/active
                if (objectModel.getDetailed()==1) {
                    showLocationsPopup(x, y, objectModel.getCaption(),objectModel.getId());
                }else {
                    showLocationPopUpWithoutArrow(x, y, objectModel.getCaption());
                }
            } else {
                // This is a case when we want to show popup where the user has touched.
                showLocationPopUpWithoutArrow(xInScreenCoords, yInScreenCoords, "Shows where user touched");
            }

            // Hint: If user touched more than one object you can show the dialog in which ask
            // the user to select concrete object

        } else {
            if (mapObjectInfoPopup != null) {
                mapObjectInfoPopup.hide();
            }
        }
    }

    private void showLocationsPopup(int x, int y, String text,final int position_id)
    {
        RelativeLayout mapLayout = (RelativeLayout) view.findViewById(R.id.rootLayout);

        if (mapObjectInfoPopup != null)
        {
            mapObjectInfoPopup.hide();
        }

        ((TextPopup) mapObjectInfoPopup).setIcon((BitmapDrawable) getResources().getDrawable(R.drawable.map_popup_arrow));
        ((TextPopup) mapObjectInfoPopup).setText(text);

        mapObjectInfoPopup.setOnClickListener(new OnTouchListener()
        {
            public boolean onTouch(View v, MotionEvent event)
            {

                Intent intent=new Intent();
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                intent.setClass(getActivity(),PositionDetailActivity.class);
                intent.putExtra("position_id",position_id);
                startActivity(intent);

                if (event.getAction() == MotionEvent.ACTION_UP)
                {
                    if (mapObjectInfoPopup != null)
                    {
                        ((TextPopup) mapObjectInfoPopup).removeIcon();
                        mapObjectInfoPopup.hide();
                    }
                }

                return false;
            }
        });

        ((TextPopup) mapObjectInfoPopup).show(mapLayout, x, y);
    }

    private void showLocationPopUpWithoutArrow(int x,int y, String text)
    {
        RelativeLayout mapLayout = (RelativeLayout) view.findViewById(R.id.rootLayout);

        if (mapObjectInfoPopup != null)
        {
            mapObjectInfoPopup.hide();
        }

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
        ((TextPopup) mapObjectInfoPopup).removeIcon();
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

    private int xToScreenCoords_sky(int mapCoord)
    {
        return (int)(mapCoord *  skymap.getScale() - skymap.getScrollX());
    }

    private int yToScreenCoords_sky(int mapCoord)
    {
        return (int)(mapCoord *  skymap.getScale() - skymap.getScrollY());
    }


    @Override
    public void onPreZoomIn() {
        Log.i(TAG, "onPreZoomIn()");

        if (mapObjectInfoPopup != null) {
            mapObjectInfoPopup.hide();
        }
        if (skymap.getZoomLevel()==12) {
            Message message=new Message();
            message.what=4;
            mHandler.sendMessage(message);
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
        if (map.getZoomLevel()==10 && (skymap.getZoomLevel()==12||skymap.getZoomLevel()==13)) {
            //layout.removeView(map);
            Message message=new Message();
            message.what=3;
            mHandler.sendMessage(message);
        }
    }

    @Override
    public void onReceiveLocation(BDLocation location) {
        //Receive Location

        if (location.getLocType() == BDLocation.TypeServerError||location.getLocType() ==BDLocation.TypeNetWorkException||location.getLocType() == BDLocation.TypeCriteriaException) {
            Log.i("BaiduLocationApiDem", "定位失败");
        }else {
            Message msg=new Message();
            msg.what=1;

            Bundle bundle=new Bundle();
            bundle.putDouble(POSITION_LAT, location.getLatitude());
            bundle.putDouble(POSITION_LONG, location.getLongitude());
            bundle.putFloat(POSITION_RADIUS, location.getRadius());
            msg.setData(bundle);

            //更新用户位置
            mHandler.sendMessage(msg);
        }
    }


    // 负责下载网络中的json数据
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            // TODO Auto-generated method stub
            URL url;
            BufferedReader br;
            try {
                Log.i("DownloadPositionData", "begin to download");
                url = new URL(
                        "http://128.199.107.78:8080/scnu_sky_material/scnu_sky/map_positions_config.txt");
                URLConnection ucon = url.openConnection();
                InputStream is = ucon.getInputStream();
                BufferedInputStream bis = new BufferedInputStream(is);
                ByteArrayOutputStream bos=new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int current = 0;
                while ((current = bis.read(buffer)) != -1) {
                    bos.write(buffer, 0, current);
                }
                gsonData = EncodingUtils.getString(bos.toByteArray(), "UTF-8");
                //将下载所得的gsonData缓存起来。
                rd.writeData(gsonData);
                // Log.i("test", "" + gsonData);
            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    Log.i("DownloadPositionData", "download compelete");
                    Message msg = new Message();
                    msg.what = 2;
                    mHandler.sendMessage(msg);
                }
            });
        }

    };

    class activityMessage implements Runnable{

        @Override
        public void run() {
            String result = null;
            try {
                result = HttpHelp.SaveActiveActivity();//请求服务器返回json字符
                //可以用JsonAnalysis转换MAP
//                ArrayList<HashMap<String,Object>> position_detail_list= JsonAnalysis.ActiveActivityAnalysis(result);
//                position_name= (String) position_detail_list.get(0).get("position_name");
//                position_detail= (String) position_detail_list.get(0).get("position_detail");
            } catch (Exception e) {
                e.printStackTrace();
            }
            Message msg = mHandler.obtainMessage();
            if(result!=null){
                //Toast.makeText(LoginActivity.this, "ok", Toast.LENGTH_SHORT).show();
                Log.d("ActiveActivityMessage", "ok");
                msg.what = 5;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        }
    }

}
