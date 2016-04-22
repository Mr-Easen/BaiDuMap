package com.example.mapviewtest;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

public class MainActivity extends Activity
{

	public LocationClient mLocationClient = null;
	public BDLocationListener myListener = new MyLocationListener();
	BitmapDescriptor mCurrentMarker = null;
	boolean isFirstLoc = true;// 是否首次定位

	private MapView mapView = null;
	private BaiduMap baiduMap;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		SDKInitializer.initialize(getApplicationContext());
		setContentView(R.layout.activity_main);
		mapView = (MapView) findViewById(R.id.bmapView);
		mLocationClient = new LocationClient(getApplicationContext()); // 声明LocationClient类
		baiduMap = mapView.getMap();  
		 baiduMap.setMyLocationEnabled(true);  
		mLocationClient.registerLocationListener(myListener); // 注册监听函数
		initLocation();
		
	}

	@Override
	protected void onDestroy()
	{
		// TODO Auto-generated method stub
		mLocationClient.stop();
		super.onDestroy();
		mapView.onDestroy();
		
	}

	@Override
	protected void onResume()
	{
		// TODO Auto-generated method stub
		super.onResume();
		mapView.onResume();
	}

	@Override
	protected void onPause()
	{
		// TODO Auto-generated method stub
		super.onPause();
		mapView.onPause();
	}

	private void initLocation()
	{
		// TODO Auto-generated method stub
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(LocationMode.Hight_Accuracy);// 可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
		option.setCoorType("bd09ll");// 可选，默认gcj02，设置返回的定位结果坐标系
		int span = 1000;
		option.setScanSpan(span);// 可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
		option.setIsNeedAddress(true);// 可选，设置是否需要地址信息，默认不需要
		option.setOpenGps(true);// 可选，默认false,设置是否使用gps
		option.setLocationNotify(true);// 可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
		option.setIsNeedLocationDescribe(true);// 可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
		option.setIsNeedLocationPoiList(true);// 可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
		option.setIgnoreKillProcess(false);// 可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
		option.SetIgnoreCacheException(false);// 可选，默认false，设置是否收集CRASH信息，默认收集
		option.setEnableSimulateGps(false);// 可选，默认false，设置是否需要过滤gps仿真结果，默认需要
		mLocationClient.setLocOption(option);
		mLocationClient.start();
	}

	class MyLocationListener implements BDLocationListener
	{

		@Override
		public void onReceiveLocation(BDLocation location)
		{
			if (location == null || mapView == null)
				return;

			
			MyLocationData locData = new MyLocationData.Builder()
					.accuracy(location.getRadius())
					// 此处设置开发者获取到的方向信息，顺时针0-360
					.direction(100).latitude(location.getLatitude())
					.longitude(location.getLongitude()).build();
			baiduMap.setMyLocationData(locData); // 设置定位数据

			if (isFirstLoc)
			{
				isFirstLoc = false;
				Toast.makeText(getApplicationContext(), "您现在正在"+location.getDistrict()+location.getStreet(), Toast.LENGTH_LONG).show();
				LatLng ll = new LatLng(location.getLatitude(),
						location.getLongitude());
				MapStatusUpdate u = MapStatusUpdateFactory
						.newLatLngZoom(ll, 16); // 设置地图中心点以及缩放级别
				// MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
				baiduMap.animateMapStatus(u);

			}

		}

	}
}
