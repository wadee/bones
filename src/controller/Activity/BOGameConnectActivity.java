package controller.Activity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import tools.Socket.SocketClient.ClientMsgListener;

import tools.Socket.SocketServer;
import tools.Socket.SocketServer.ServerMsgListener;

import global.BOGlobalConst;

import tools.Wifi.WifiHotAdapter;

import tools.Wifi.WifiHotManager.OpretionsType;
import tools.Wifi.WifiHotManager.WifiBroadCastOperations;

import tools.Wifi.WifiHotManager;

import tools.Socket.SocketClient;
import controller.BOActivityAbstract;
import android.app.ProgressDialog;
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
	private Button createHotsBtn;
	private WifiHotAdapter adapter;
	private String mSSID;
	private Handler clientHandler;
	private Handler serverHandler;
	private SocketClient client;
	private SocketServer server;
	private boolean connected;
	private ProgressDialog progressDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game_connect);
		
		initClientHandler();
		initServerHandler();
		/**
		 * 热点列表
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
		
		/**
		 * 重新搜索按钮
		 */
		scanHotsBtn = (Button) findViewById(R.id.flashHot);
		scanHotsBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				progressDialog.show(); 
				WifiHotM.scanWifiHot();
			}
		});
		
		/**
		 * 建立热点按钮createHot
		 */
		createHotsBtn = (Button) findViewById(R.id.createHot);
		createHotsBtn.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				WifiHotM.startAWifiHot("WIFI-TEST");
				initServer();
			}
			
		});
		
	}
	
	protected void onResume(){
		WifiHotM = WifiHotManager.getInstance(BOGameConnectActivity.this,BOGameConnectActivity.this);
		progressDialog = ProgressDialog.show(BOGameConnectActivity.this, "正在搜索附近热点", "请稍后...", true, true);
		WifiHotM.scanWifiHot();
		super.onResume();
	}

	@Override
	public void disPlayWifiScanResult(List<ScanResult> wifiList) {
		// TODO Auto-generated method stub
		progressDialog.dismiss();
		Iterator<ScanResult> it = wifiList.iterator();
		ArrayList<String> ssid_list = new ArrayList<String>();
		ArrayList<ScanResult> new_wifilist = new ArrayList<ScanResult>();
		while(it.hasNext()){
			ScanResult cur = it.next();
			if(!ssid_list.contains(cur.SSID)){
				ssid_list.add(cur.SSID);
				new_wifilist.add(cur);
			}else{
				continue;
			}
		}
		
		this.wifiList = (List<ScanResult>)new_wifilist;
		WifiHotM.unRegisterWifiScanBroadCast();
		refreshWifiList(this.wifiList);
	}

	@Override
	public boolean disPlayWifiConResult(boolean result, WifiInfo wifiInfo) {
		// TODO Auto-generated method stub
		WifiHotM.setConnectStatu(false);
		WifiHotM.unRegisterWifiStateBroadCast();
		WifiHotM.unRegisterWifiConnectBroadCast();
		Toast.makeText(BOGameConnectActivity.this, wifiInfo.getSSID(), Toast.LENGTH_SHORT).show();
		initClient("");
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
					Log.i("client", "fail");
					Toast.makeText(BOGameConnectActivity.this, "连接失败", Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(BOGameConnectActivity.this, "连接成功", Toast.LENGTH_SHORT).show();
					String text = (String) msg.obj;
					Log.i(TAG, "into initClientHandler() handleMessage(Message msg) text =" + text);
				}
			}
		};
	}
	
	private void initServerHandler() {
		serverHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				Log.i(TAG, "into initServerHandler() handleMessage(Message msg)");
				if (msg.what == 0) {
					Toast.makeText(BOGameConnectActivity.this, "创建失败", Toast.LENGTH_SHORT).show();
				} else {
					String text = (String) msg.obj;
					Toast.makeText(BOGameConnectActivity.this, "创建成功", Toast.LENGTH_SHORT).show();
					Log.i(TAG, "into initServerHandler() handleMessage(Message msg) text = " + text);
				}
			}
		};
	}
	
	private void initServer() {
		server = SocketServer.newInstance(12345, new ServerMsgListener() {
			Message msg = null;

			@Override
			public void handlerHotMsg(String hotMsg) {
				connected = true;
				Log.i(TAG, "server 初始化成功！");
				msg = clientHandler.obtainMessage();
				msg.obj = hotMsg;
				msg.what = 1;
				serverHandler.sendMessage(msg);
			}

			@Override
			public void handlerErorMsg(String errorMsg) {
				connected = false;
				Log.d(TAG, "server 初始化失败！");
				msg = clientHandler.obtainMessage();
				msg.obj = errorMsg;
				msg.what = 0;
				serverHandler.sendMessage(msg);
			}
		});
		server.beginListen();
	}
	private void initClient(String IP) {
		client = SocketClient.newInstance("192.168.43.1", 12345, new ClientMsgListener() {

			Message msg = null;

			@Override
			public void handlerErorMsg(String errorMsg) {
				connected = false;
				Log.d(TAG, "client 初始化失败！");
				msg = clientHandler.obtainMessage();
				msg.obj = errorMsg;
				msg.what = 0;
				clientHandler.sendMessage(msg);

			}

			@Override
			public void handlerHotMsg(String hotMsg) {
				connected = true;
				Log.i(TAG, "client 初始化成功！");
				msg = clientHandler.obtainMessage();
				msg.obj = hotMsg;
				msg.what = 1;
				clientHandler.sendMessage(msg);

			}
		});
		client.connectServer();
	}
	
}