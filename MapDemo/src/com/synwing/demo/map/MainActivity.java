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
	private double lat;//γ��
	private double lng;//����
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
		//����һЩ��γ��
		LatLonPoint lat1 = new LatLonPoint(30.565808,104.254761);//��Ȫ
		LatLonPoint lat2 = new LatLonPoint(30.718227,104.135284);//�ɻ�
		LatLonPoint lat3 = new LatLonPoint(30.859795,104.419556);//����
		LatLonPoint lat4 = new LatLonPoint(30.991737,103.617554);//������
		LatLonPoint lat5 = new LatLonPoint(30.413817,103.820543);//�½�
		LatLonPoint lat6 = new LatLonPoint(30.657997,103.664246);//����
		LatLonPoint lat7 = new LatLonPoint(30.416259,103.462114);//����
		lats.add(lat1);
		lats.add(lat2);
		lats.add(lat3);
		lats.add(lat4);
		lats.add(lat5);
		lats.add(lat6);
		lats.add(lat7);
	}
	/**
     * ��ʼ��AMap����
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
     * ����һЩamap������
     */
    private void setUpMap() {
		// �Զ���ϵͳ��λС����
		MyLocationStyle myLocationStyle = new MyLocationStyle();
		myLocationStyle.myLocationIcon(BitmapDescriptorFactory
				.fromResource(R.drawable.location_marker));// ����С�����ͼ��
		myLocationStyle.strokeColor(Color.WHITE);// ����Բ�εı߿���ɫ
		//myLocationStyle.radiusFillColor(Color.argb(100, 0, 0, 180));// ����Բ�ε������ɫ
		//myLocationStyle.anchor(int,int)//����С�����ê��
		//myLocationStyle.strokeWidth(1.0f);// ����Բ�εı߿��ϸ
		aMap.setMyLocationStyle(myLocationStyle);
		aMap.setLocationSource(this);// ���ö�λ����
		aMap.getUiSettings().setMyLocationButtonEnabled(true);// ����Ĭ�϶�λ��ť�Ƿ���ʾ
		aMap.setMyLocationEnabled(true);// ����Ϊtrue��ʾ��ʾ��λ�㲢�ɴ�����λ��false��ʾ���ض�λ�㲢���ɴ�����λ��Ĭ����false
	   // aMap.setMyLocationType()
		aMap.setOnMarkerClickListener(this);
	}
 
    /**
     * ����������д
     */
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }
 
    /**
     * ����������д
     */
    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }
     
    /**
     * ����������д
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }
 
    /**
     * ����������д
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }
    /**
     * ���λ
     */
	@SuppressWarnings("deprecation")
	@Override
	public void activate(OnLocationChangedListener listener) {
		mListener = listener;
		if (mAMapLocationManager == null) {
			mAMapLocationManager = LocationManagerProxy.getInstance(this);
			/*
			 * mAMapLocManager.setGpsEnable(false);
			 * 1.0.2�汾��������������true��ʾ��϶�λ�а���gps��λ��false��ʾ�����綨λ��Ĭ����true Location
			 * API��λ����GPS�������϶�λ��ʽ
			 * ����һ�������Ƕ�λprovider���ڶ�������ʱ�������2000���룬������������������λ���ף����ĸ������Ƕ�λ������
			 */
			mAMapLocationManager.requestLocationUpdates(
					LocationProviderProxy.AMapNetwork, 6*1000, 10, this);
		}
	}
	/**
	 * ��Ӧ��������
	 */
	public void getAddress(final LatLonPoint latLonPoint) {
			RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 200,
					GeocodeSearch.AMAP);// ��һ��������ʾһ��Latlng���ڶ�������ʾ��Χ�����ף�������������ʾ�ǻ�ϵ����ϵ����GPSԭ������ϵ
			geocoderSearch.getFromLocationAsyn(query);// ����ͬ��������������
	}
	/**
	 * ֹͣ��λ
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
	 * ��������ص�
	 */
	@Override
	public void onRegeocodeSearched(RegeocodeResult result, int rCode) {
		if (rCode == 0) {
				if (result != null && result.getRegeocodeAddress() != null
						&& result.getRegeocodeAddress().getFormatAddress() != null) {
					addressName = result.getRegeocodeAddress().getFormatAddress()
							+ "����";
					tv.setText(addressName);
					Toast.makeText(MainActivity.this, addressName, Toast.LENGTH_SHORT).show();
				} 
			}
	}
	/**
	 * ��λ�ɹ���ص�����
	 */
	@Override
	public void onLocationChanged(AMapLocation aLocation) {
		if (mListener != null && aLocation != null) {
			mListener.onLocationChanged(aLocation);// ��ʾϵͳС����
		}
		//��ȡ��ǰλ����Ϣ
		String desc = "";
		Bundle locBundle = aLocation.getExtras();
		if (locBundle != null) {
		    desc = locBundle.getString("desc");
		}
		if(aLocation != null && aLocation.getAMapException().getErrorCode() == 0){
            //��ȡλ����Ϣ
            Double geoLat = aLocation.getLatitude();//γ��
            Double geoLng = aLocation.getLongitude();//����   
            //tv.setText("γ�ȣ�"+geoLat+",���ȣ�"+geoLng+",��ǰ���ڵ�ַ��"+desc+"~"+addressName);
            //����
    		PolylineOptions options = new PolylineOptions();
    		options.add(new LatLng(geoLat,geoLng));
    		//ģ��λ�ñ��
    		for (int i = 0; i < lats.size(); i++) {
    			aMap.addMarker(new MarkerOptions().anchor(0.5f, 1f)
        				.position(new LatLng(lats.get(i).getLatitude(),lats.get(i).getLongitude()))
        				.draggable(true).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
    			//ѭ����Ӿ�γ��
        		options.add(new LatLng(lats.get(i).getLatitude(),lats.get(i).getLongitude()));
        		//������ɫ
        		options.color(Color.RED);
        		//���ÿ��
        		options.width(10);
        		//���ó�����
        		options.setDottedLine(true);
        		aMap.addPolyline(options);
			}
        }
	}
	@Override
	public void onGeocodeSearched(GeocodeResult arg0, int arg1) {
	}
	/**
	 * Marker����¼�
	 */
	@Override
	public boolean onMarkerClick(Marker mar) {
		LatLonPoint la = new LatLonPoint(mar.getPosition().latitude, mar.getPosition().longitude);
		RegeocodeQuery query = new RegeocodeQuery(la, 200,
				GeocodeSearch.AMAP);// ��һ��������ʾһ��Latlng���ڶ�������ʾ��Χ�����ף�������������ʾ�ǻ�ϵ����ϵ����GPSԭ������ϵ
		geocoderSearch.getFromLocationAsyn(query);// ����ͬ��������������
		//mar.setTitle(addressName);
		return false;
	}
	/**
	 * ˫���˳�����
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK) {
			exitBy2Click();      //����˫���˳�����
		}
		return false;
	}
	/*
	 * ˫���˳�����
	 */
	private static Boolean isExit = false; 
	
	private void exitBy2Click(){
		Timer tExit = null;
		if(isExit==false){
			isExit = true;//׼���˳�
			Toast.makeText(this, "�ٰ�һ���˳�����", Toast.LENGTH_SHORT).show(); 
			tExit = new Timer();
			tExit.schedule(new TimerTask() {
				@Override 
				public void run() {
					isExit = false; // ȡ���˳�
				} 
			}, 2000); // ���2������û�а��·��ؼ�����������ʱ��ȡ�����ղ�ִ�е�����
				}else{
					finish(); 
					System.exit(0);
				}
			}
}
