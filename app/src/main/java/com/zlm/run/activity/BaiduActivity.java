package com.zlm.run.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.trace.model.SortType;
import com.zlm.run.R;
import com.zlm.run.tool.BitmapUtil;
import com.zlm.run.tool.LogUtil;
import com.zlm.run.tool.MapUtil;

import java.util.ArrayList;
import java.util.List;

public class BaiduActivity extends BaseActivity {
    private MapUtil mapUtil;
    private MapView mMapView;
    private LocationClient locationClient;

    private List<LatLng> latLngs;

    private boolean isFirst = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baidu);
        mMapView = findViewById(R.id.map_view);
        mapUtil = MapUtil.getINSTANCE();
        mapUtil.init(mMapView);
        latLngs = new ArrayList<>();
        BitmapUtil.init();

        locationClient = new LocationClient(getApplicationContext());
        locationClient.registerLocationListener(new MyLocationClintListener());
        initLocation();

    }

    private void initLocation() {
        LocationClientOption option = new LocationClientOption();
        option.setScanSpan(2000);
        option.setOpenGps(true);
        option.setCoorType("bd09ll");
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        locationClient.setLocOption(option);
        locationClient.start();
    }

    private class MyLocationClintListener extends BDAbstractLocationListener {

        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            LatLng latLng = new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude());
            if (isFirst) {
                mapUtil.updateStatus(latLng, true);
                isFirst = false;
            } else {
                latLngs.add(latLng);
                mapUtil.updateStatus(latLng, true);
                mapUtil.drawHistoryTrack(latLngs, SortType.asc);
            }
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        mapUtil.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapUtil.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapUtil.clear();
        latLngs.clear();
        BitmapUtil.clear();
        locationClient.stop();

    }
}
