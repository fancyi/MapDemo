package com.synwing.demo.map;


import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.AMap.OnMarkerClickListener;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.maps2d.model.MyLocationStyle;
import com.amap.api.maps2d.model.PolylineOptions;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.GeocodeSearch.OnGeocodeSearchListener;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;

import android.app.Activity;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements 
	LocationSource,AMapLocationListener,OnGeocodeSearchListener,OnMarkerClickListener {
	private MapView mapView;
	private AMap aMap;
	private OnLocationChangedListener mListener;
	private LocationManagerProxy mAMapLocationManager;
	private TextView tv;
	private double lat;//纬度
	private double lng;//经度
	private GeocodeSearch geocoderSearch;
	private String addressName;
	private List<LatLonPoint> lats = new ArrayList<LatLonPoint>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		tv = (TextView)findViewById(R.id.zuobiao);
		mapView = (MapView) findViewById(R.id.map);
		mapView.onCreate(savedInstanceState);
		init();
		//定义一些经纬度
		LatLonPoint lat1 = new LatLonPoint(30.565808,104.254761);//龙泉
		LatLonPoint lat2 = new LatLonPoint(30.718227,104.135284);//成华
		LatLonPoint lat3 = new LatLonPoint(30.859795,104.419556);//金堂
		LatLonPoint lat4 = new LatLonPoint(30.991737,103.617554);//都江堰
		LatLonPoint lat5 = new LatLonPoint(30.413817,103.820543);//新津
		LatLonPoint lat6 = new LatLonPoint(30.657997,103.664246);//崇州
		LatLonPoint lat7 = new LatLonPoint(30.416259,103.462114);//邛崃
		lats.add(lat1);
		lats.add(lat2);
		lats.add(lat3);
		lats.add(lat4);
		lats.add(lat5);
		lats.add(lat6);
		lats.add(lat7);
	}
	/**
     * 初始化AMap对象
     */
    private void init() {
        if (aMap == null) {
            aMap = mapView.getMap();
        }
        setUpMap();
        geocoderSearch = new GeocodeSearch(this);
		geocoderSearch.setOnGeocodeSearchListener(this);
    }
    /**
     * 设置一些amap的属性
     */
    private void setUpMap() {
		// 自定义系统定位小蓝点
		MyLocationStyle myLocationStyle = new MyLocationStyle();
		myLocationStyle.myLocationIcon(BitmapDescriptorFactory
				.fromResource(R.drawable.location_marker));// 设置小蓝点的图标
		myLocationStyle.strokeColor(Color.WHITE);// 设置圆形的边框颜色
		//myLocationStyle.radiusFillColor(Color.argb(100, 0, 0, 180));// 设置圆形的填充颜色
		//myLocationStyle.anchor(int,int)//设置小蓝点的锚点
		//myLocationStyle.strokeWidth(1.0f);// 设置圆形的边框粗细
		aMap.setMyLocationStyle(myLocationStyle);
		aMap.setLocationSource(this);// 设置定位监听
		aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
		aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
	   // aMap.setMyLocationType()
		aMap.setOnMarkerClickListener(this);
	}
 
    /**
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }
 
    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }
     
    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }
 
    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }
    /**
     * 激活定位
     */
	@SuppressWarnings("deprecation")
	@Override
	public void activate(OnLocationChangedListener listener) {
		mListener = listener;
		if (mAMapLocationManager == null) {
			mAMapLocationManager = LocationManagerProxy.getInstance(this);
			/*
			 * mAMapLocManager.setGpsEnable(false);
			 * 1.0.2版本新增方法，设置true表示混合定位中包含gps定位，false表示纯网络定位，默认是true Location
			 * API定位采用GPS和网络混合定位方式
			 * ，第一个参数是定位provider，第二个参数时间最短是2000毫秒，第三个参数距离间隔单位是米，第四个参数是定位监听者
			 */
			mAMapLocationManager.requestLocationUpdates(
					LocationProviderProxy.AMapNetwork, 6*1000, 10, this);
		}
	}
	/**
	 * 响应逆地理编码
	 */
	public void getAddress(final LatLonPoint latLonPoint) {
			RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 200,
					GeocodeSearch.AMAP);// 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
			geocoderSearch.getFromLocationAsyn(query);// 设置同步逆地理编码请求
	}
	/**
	 * 停止定位
	 */
	@Override
	public void deactivate() {
		mListener = null;
		if (mAMapLocationManager != null) {
			mAMapLocationManager.removeUpdates(this);
			mAMapLocationManager.destory();
		}
		mAMapLocationManager = null;
	}
	@Override
	public void onLocationChanged(Location location) {
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
	/**
	 * 逆地理编码回调
	 */
	@Override
	public void onRegeocodeSearched(RegeocodeResult result, int rCode) {
		if (rCode == 0) {
				if (result != null && result.getRegeocodeAddress() != null
						&& result.getRegeocodeAddress().getFormatAddress() != null) {
					addressName = result.getRegeocodeAddress().getFormatAddress()
							+ "附近";
					tv.setText(addressName);
					Toast.makeText(MainActivity.this, addressName, Toast.LENGTH_SHORT).show();
				} 
			}
	}
	/**
	 * 定位成功后回调函数
	 */
	@Override
	public void onLocationChanged(AMapLocation aLocation) {
		if (mListener != null && aLocation != null) {
			mListener.onLocationChanged(aLocation);// 显示系统小蓝点
		}
		//获取当前位置信息
		String desc = "";
		Bundle locBundle = aLocation.getExtras();
		if (locBundle != null) {
		    desc = locBundle.getString("desc");
		}
		if(aLocation != null && aLocation.getAMapException().getErrorCode() == 0){
            //获取位置信息
            Double geoLat = aLocation.getLatitude();//纬度
            Double geoLng = aLocation.getLongitude();//经度   
            //tv.setText("纬度："+geoLat+",经度："+geoLng+",当前所在地址："+desc+"~"+addressName);
            //折线
    		PolylineOptions options = new PolylineOptions();
    		options.add(new LatLng(geoLat,geoLng));
    		//模拟位置标记
    		for (int i = 0; i < lats.size(); i++) {
    			aMap.addMarker(new MarkerOptions().anchor(0.5f, 1f)
        				.position(new LatLng(lats.get(i).getLatitude(),lats.get(i).getLongitude()))
        				.draggable(true).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
    			//循环添加经纬度
        		options.add(new LatLng(lats.get(i).getLatitude(),lats.get(i).getLongitude()));
        		//设置颜色
        		options.color(Color.RED);
        		//设置宽度
        		options.width(10);
        		//设置成虚线
        		options.setDottedLine(true);
        		aMap.addPolyline(options);
			}
        }
	}
	@Override
	public void onGeocodeSearched(GeocodeResult arg0, int arg1) {
	}
	/**
	 * Marker点击事件
	 */
	@Override
	public boolean onMarkerClick(Marker mar) {
		LatLonPoint la = new LatLonPoint(mar.getPosition().latitude, mar.getPosition().longitude);
		RegeocodeQuery query = new RegeocodeQuery(la, 200,
				GeocodeSearch.AMAP);// 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
		geocoderSearch.getFromLocationAsyn(query);// 设置同步逆地理编码请求
		//mar.setTitle(addressName);
		return false;
	}
	/**
	 * 双击退出程序
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK) {
			exitBy2Click();      //调用双击退出函数
		}
		return false;
	}
	/*
	 * 双击退出函数
	 */
	private static Boolean isExit = false; 
	
	private void exitBy2Click(){
		Timer tExit = null;
		if(isExit==false){
			isExit = true;//准备退出
			Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show(); 
			tExit = new Timer();
			tExit.schedule(new TimerTask() {
				@Override 
				public void run() {
					isExit = false; // 取消退出
				} 
			}, 2000); // 如果2秒钟内没有按下返回键，则启动定时器取消掉刚才执行的任务
				}else{
					finish(); 
					System.exit(0);
				}
			}
}
