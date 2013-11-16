package controller.Activity;

import java.util.List;

import tools.Wifi.WifiHotAdapter;

import tools.Wifi.WifiHotManager.OpretionsType;
import tools.Wifi.WifiHotManager.WifiBroadCastOperations;

import tools.Wifi.WifiHotManager;

import controller.BOActivityAbstract;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.Button;

//创建热点，搜索wifi页面
public class BOGameConnectActivity extends BOActivityAbstract implements WifiBroadCastOperations {
	private static String TAG = "connect";
	private List<ScanResult> wifiList;
	private WifiHotManager WifiHotM;
	private ListView listView;
	private Button scanHotsBtn;
	private WifiHotAdapter adapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game_connect);
		
		/**
		 * 搜索热点开始
		 */
		listView = (ListView) findViewById(R.id.listHots);
		
		scanHotsBtn = (Button) findViewById(R.id.flashHot);
		scanHotsBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				WifiHotM.scanWifiHot();
			}
		});
	}
	
	protected void onResume(){
		WifiHotM = WifiHotManager.getInstance(BOGameConnectActivity.this,BOGameConnectActivity.this);
		WifiHotM.scanWifiHot();
		super.onResume();
	}

	@Override
	public void disPlayWifiScanResult(List<ScanResult> wifiList) {
		// TODO Auto-generated method stub
		this.wifiList = wifiList;
		WifiHotM.unRegisterWifiScanBroadCast();
		refreshWifiList(wifiList);
	}

	@Override
	public boolean disPlayWifiConResult(boolean result, WifiInfo wifiInfo) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void operationByType(OpretionsType type, String SSID) {
		// TODO Auto-generated method stub
		
	}
	
	private void refreshWifiList(List<ScanResult> results) {
		if (null == adapter) {
			adapter = new WifiHotAdapter(results, this);
			listView.setAdapter(adapter);
		} else {
			adapter.refreshData(results);
		}
	}
	
}