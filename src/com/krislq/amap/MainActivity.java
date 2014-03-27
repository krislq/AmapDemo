package com.krislq.amap;

import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMap.OnCameraChangeListener;
import com.amap.api.maps.AMap.OnInfoWindowClickListener;
import com.amap.api.maps.AMap.OnMapClickListener;
import com.amap.api.maps.AMap.OnMapLoadedListener;
import com.amap.api.maps.AMap.OnMarkerClickListener;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.SupportMapFragment;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.Circle;
import com.amap.api.maps.model.CircleOptions;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;

public class MainActivity extends FragmentActivity implements 
OnMarkerClickListener, 
OnInfoWindowClickListener, 
OnMapClickListener, 
OnCameraChangeListener, 
OnMapLoadedListener, 
LocationSource, 
AMapLocationListener {
    private SupportMapFragment mMapFragment =null;
    private AMap mAmap = null;
    private AmapInfoWindowAdapter mCarInfoAdapter = null;
    private AMapLocation mMyLocation;
    private Marker mMyMarker = null;
    private Marker mLocationMarker = null;
    private Marker mSelectedMarker = null;
    private Circle mCircle = null;
    private int mLastCarResource = 0;
    private LocationManagerProxy mAMapLocationManager;

    //用于marker info有没有显示出来
    private boolean mMakerInfoDisplay = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.car_map);
        mAmap = mMapFragment.getMap();
    }


    @Override
    public void onResume() {
        super.onResume();
        if(mAmap!=null) {
            initAmap();
        }
        requestLocationUpdates();
    }
    

    @Override
    public void onPause() {
        deactivate();
        super.onPause();
    }


    private void initAmap() {
        if(mAmap!=null) {
            // 设置自定义InfoWindow样式
                mCarInfoAdapter = new AmapInfoWindowAdapter(this);
               mAmap.setInfoWindowAdapter(mCarInfoAdapter);
               // 为marker事件设置监听器
               mAmap.setOnMarkerClickListener(this);
               mAmap.setOnInfoWindowClickListener(this);
               mAmap.setOnMapClickListener(this);
               mAmap.setOnCameraChangeListener(this);
               mAmap.setOnMapLoadedListener(this);
               
               mAmap.setLocationSource(this);
               mAmap.getUiSettings().setCompassEnabled(true);
               mAmap.getUiSettings().setRotateGesturesEnabled(true);
               mAmap.getUiSettings().setZoomControlsEnabled(false);
//               mAmap.setMyLocationEnabled(true);
           }
    }

    
    public CircleOptions getCircleOptions(AMapLocation myLocation) {
        if(myLocation==null) {
            return null;
        }
        int fillColo = Color.argb(100, 200, 200, 200);
        return new CircleOptions().center(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()))
                .radius(myLocation.getAccuracy())
                .fillColor(fillColo)
                .strokeWidth(0);
    }

    private void requestLocationUpdates() {
        if(mAMapLocationManager==null) {
            mAMapLocationManager = LocationManagerProxy.getInstance(this);
            mAMapLocationManager.requestLocationUpdates(
                    LocationProviderProxy.AMapNetwork, 30000, 30,this);
        }
    }

    private void addMyLocationMarker() {
        if(mMyLocation==null) {
            return;
        }

        if(mMyMarker!=null) {
            mMyMarker.setVisible(false);
        }
        if(mCircle!=null) {
            mCircle.setVisible(false);
        }

        mMyMarker = addMarker(new LatLng(mMyLocation.getLatitude(), mMyLocation.getLongitude()),"MyLocation","",R.drawable.ic_location_my);
        mCircle = mAmap.addCircle(getCircleOptions(mMyLocation));
    }
    private Marker addMarker(LatLng latlng,String title,String spippet,int icon) {
        return mAmap.addMarker(new MarkerOptions()
                .position(latlng)
                .title(title)
                .snippet(spippet)
                .icon(icon ==0 ?
                        BitmapDescriptorFactory.defaultMarker()
                        :BitmapDescriptorFactory.fromResource(icon)));
    }
    
    @Override
    public void activate(OnLocationChangedListener l) {
        requestLocationUpdates();
    }

    @Override
    public void deactivate() {
        if (mAMapLocationManager != null) {
            mAMapLocationManager.removeUpdates(this);
            mAMapLocationManager.destory();
        }
        mAMapLocationManager = null;
    }

    @Override
    public void onMapLoaded() {
        //如果 觉得默认的初始位置不是我们想要的，可以在这里去设置个
        CameraPosition position = mAmap.getCameraPosition();
        mLocationMarker = addMarker(position.target, "center", "kris demo", R.drawable.icar_available_a);
        mAmap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                position.target, 14));
    }

    @Override
    public void onCameraChange(CameraPosition position) {
        
    }

    @Override
    public void onCameraChangeFinish(CameraPosition position) {
        
    }

    @Override
    public void onMapClick(LatLng latlng) {
        if(mSelectedMarker!=null) {
            mSelectedMarker.hideInfoWindow();
            //告诉camera change没有Info window显示了
            mMakerInfoDisplay = false;
            mSelectedMarker.setIcon(BitmapDescriptorFactory.fromResource(mLastCarResource));
        }
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Toast.makeText(this, marker.getTitle(), Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {

        //用户点击了新的Marker，先需要把当前选 中的Marker还原成上次的的图标
        if(mSelectedMarker!=null) {
            mSelectedMarker.setIcon(BitmapDescriptorFactory.fromResource(mLastCarResource));
        }
        mSelectedMarker = marker;
        
        mLastCarResource = R.drawable.icar_available_a;
        if("MyLocation".equals(marker.getTitle())) {
            mLastCarResource = R.drawable.ic_location_my;
        }
        int currentCarResource = R.drawable.icar_available_pressed_a;
        if("MyLocation".equals(marker.getTitle())) {
            currentCarResource = R.drawable.ic_location_select;
        }
        mSelectedMarker.setIcon(BitmapDescriptorFactory.fromResource(currentCarResource));
        mMakerInfoDisplay = true;
        return false;
    }


    @Override
    public void onLocationChanged(Location location) {
        // TODO Auto-generated method stub
        
    }


    @Override
    public void onProviderDisabled(String provider) {
        
    }


    @Override
    public void onProviderEnabled(String provider) {
        
    }


    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        
    }


    @Override
    public void onLocationChanged(AMapLocation aLocation) {

        if(aLocation==null) {
            return;
        }
        boolean needGet = false;
        if(mMyLocation ==null) {
            needGet = true;
        }
        mMyLocation = aLocation;
        if(needGet) {
            //随时都在刷新自己的位置，但是并不去获取数据
            addMyLocationMarker();
        }
    }


    private void move2MyLocation() {
        if(mMyLocation==null || mAmap ==null) {
            return;
        }
        CameraPosition position = mAmap.getCameraPosition();
        mAmap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(mMyLocation.getLatitude(), mMyLocation.getLongitude()), position.zoom>14? position.zoom:14));
    }
}
