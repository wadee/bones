package controller.Activity;

import java.util.List;

import global.BOGlobalConst;

import tools.Wifi.WifiHotAdapter;

import tools.Wifi.WifiHotManager.OpretionsType;
import tools.Wifi.WifiHotManager.WifiBroadCastOperations;

import tools.Wifi.WifiHotManager;

import tools.Socket.SocketClient;
import controller.BOActivityAbstract;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Button;
import android.widget.Toast;

//创建热点，搜索wifi页面
public class BOGameConnectActivity extends BOActivityAbstract implements WifiBroadCastOperations {
	private static String TAG = "connect";
	private List<ScanResult> wifiList;
	private WifiHotManager WifiHotM;
	private ListView listView;
	private Button scanHotsBtn;
	private WifiHotAdapter adapter;
	private String mSSID;
	private Handler clientHandler;
	private SocketClient client;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game_connect);
		
		initClientHandler();
		/**
		 * 搜索热点开始
		 */
		
		listView = (ListView) findViewById(R.id.listHots);
		listView.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				// TODO Auto-generated method stub
				ScanResult result = wifiList.get(position);
				mSSID = result.SSID;
				Toast.makeText(BOGameConnectActivity.this, result.SSID, Toast.LENGTH_SHORT).show();
				WifiHotM.connectToHotpot(mSSID, wifiList, BOGlobalConst.PASSWORD);
			}
			
		});
		
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
		WifiHotM.setConnectStatu(false);
		WifiHotM.unRegisterWifiStateBroadCast();
		WifiHotM.unRegisterWifiConnectBroadCast();
		Toast.makeText(BOGameConnectActivity.this, wifiInfo.getSSID(), Toast.LENGTH_SHORT).show();
//		initClient(ip);
		return false;
	}

	@Override
	public void operationByType(OpretionsType type, String SSID) {
		// TODO Auto-generated method stub
		if (type == OpretionsType.CONNECT) {
			WifiHotM.connectToHotpot(SSID, wifiList, BOGlobalConst.PASSWORD);
		} else if (type == OpretionsType.SCAN) {
			WifiHotM.scanWifiHot();
		}
		
	}
	
	private void refreshWifiList(List<ScanResult> results) {
		if (null == adapter) {
			adapter = new WifiHotAdapter(results, this);
			listView.setAdapter(adapter);
		} else {
			adapter.refreshData(results);
		}
	}
	
	private void initClientHandler() {
		clientHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				Log.i(TAG, "into initClientHandler() handleMessage(Message msg)");
				if (msg.what == 0) {
					Toast.makeText(BOGameConnectActivity.this, "连接失败", Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(BOGameConnectActivity.this, "连接成功", Toast.LENGTH_SHORT).show();
					String text = (String) msg.obj;
					Log.i(TAG, "into initClientHandler() handleMessage(Message msg) text =" + text);
				}
			}
		};
	}
	
}