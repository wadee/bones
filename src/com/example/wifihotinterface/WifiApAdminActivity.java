package com.example.wifihotinterface;

import java.util.List;
import android.app.Activity;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.wifihotinterface.pack.Global;
import com.example.wifihotinterface.pack.SocketClient;
import com.example.wifihotinterface.pack.SocketClient.ClientMsgListener;
import com.example.wifihotinterface.pack.SocketServer;
import com.example.wifihotinterface.pack.SocketServer.ServerMsgListener;
import com.example.wifihotinterface.pack.WifiHotManager;
import com.example.wifihotinterface.pack.WifiHotManager.OpretionsType;
import com.example.wifihotinterface.pack.WifiHotManager.WifiBroadCastOperations;
import com.google.gson.Gson;

public class WifiApAdminActivity extends Activity implements WifiBroadCastOperations {

	private static String TAG = "WifiApAdminActivity";

	private Button wifiHotBtn;

	private Button chatBtn;

	private Button scanHotsBtn;

	private TextView statu;

	private WifiHotManager wifiHotM;

	private List<ScanResult> wifiList;

	private SocketClient client;

	private SocketServer server;

	private ListView listView;

	private WifiHotAdapter adapter;

	private boolean connected;

	private String mSSID;

	private Handler serverHandler;

	private Handler clientHandler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "into onCreate()");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.actiivty_wifihotlist);
		initClientHandler();
		initServerHandler();
		// 聊天室
		chatBtn = (Button) findViewById(R.id.chatHom);
		chatBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				saveData();
				gotoChatActivity();
			}
		});

		// 热点创建
		wifiHotBtn = (Button) findViewById(R.id.createHot);
		wifiHotBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				wifiHotM.startAWifiHot("WIFI-TEST");
				initServer();
			}
		});

		// 热点扫描
		scanHotsBtn = (Button) findViewById(R.id.scanHots);
		scanHotsBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				wifiHotM.scanWifiHot();
			}
		});

		// 热点列表
		listView = (ListView) findViewById(R.id.listHots);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				ScanResult result = wifiList.get(position);
				mSSID = result.SSID;
				statu.setText("连接中...");
				Log.i(TAG, "into  onItemClick() SSID= " + result.SSID);
				wifiHotM.connectToHotpot(result.SSID, wifiList, Global.PASSWORD);
				Log.i(TAG, "out  onItemClick() SSID= " + result.SSID);
			}
		});
		statu = (TextView) findViewById(R.id.hotTitleName);
		Log.i(TAG, "out onCreate()");
	}

	private String intToIp(int i) {
		return (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF) + "." + ((i >> 24) & 0xFF);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	// 扫描热点广播初始化
	@Override
	protected void onResume() {
		wifiHotM = WifiHotManager.getInstance(WifiApAdminActivity.this, WifiApAdminActivity.this);
		wifiHotM.scanWifiHot();
		super.onResume();
	}

	// 监听返回键，退出程序
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			Log.i(TAG, "into onBackPressed()");
			wifiHotM.unRegisterWifiScanBroadCast();
			wifiHotM.unRegisterWifiStateBroadCast();
			wifiHotM.disableWifiHot();
			this.finish();
			Log.i(TAG, "out onBackPressed()");
			return true;
		}
		return true;
	}

	@Override
	protected void onDestroy() {
		Log.i(TAG, "into onDestroy() ");
		if (adapter != null) {
			adapter.clearData();
			adapter = null;
		}
		if (server != null) {
			server.clearServer();
			server.stopListner();
			server = null;
			wifiHotM.disableWifiHot();

		}
		if (client != null) {
			client.clearClient();
			client.stopAcceptMessage();
			client = null;
			wifiHotM.deleteMoreCon(mSSID);
		}
		// ？
		System.exit(0);
		Log.i(TAG, "out onDestroy() ");
		super.onDestroy();
	}

	// wifi 热点扫描回调
	@Override
	public void disPlayWifiScanResult(List<ScanResult> wifiList) {

		Log.i(TAG, "into 扫描结果回调函数");
		this.wifiList = wifiList;
		wifiHotM.unRegisterWifiScanBroadCast();
		refreshWifiList(wifiList);
		Log.i(TAG, "out 热点扫描结果 ： = " + wifiList);

	}

	// wifi 连接回调
	@Override
	public boolean disPlayWifiConResult(boolean result, WifiInfo wifiInfo) {

		Log.i(TAG, "into 热点连接回调函数");
		String ip = "";
		wifiHotM.setConnectStatu(false);
		wifiHotM.unRegisterWifiStateBroadCast();
		wifiHotM.unRegisterWifiConnectBroadCast();
		initClient(ip);
		Log.i(TAG, "out 热点链接回调函数");
		return false;
	}

	// wifi 热点连接、扫描在Wifi关闭的情况下，回调
	@Override
	public void operationByType(OpretionsType type, String SSID) {
		Log.i(TAG, "into operationByType！type = " + type);
		if (type == OpretionsType.CONNECT) {
			wifiHotM.connectToHotpot(SSID, wifiList, Global.PASSWORD);
		} else if (type == OpretionsType.SCAN) {
			wifiHotM.scanWifiHot();
		}
		Log.i(TAG, "out operationByType！");

	}

	// server 初始化
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

	// client 初始化
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

	private void refreshWifiList(List<ScanResult> results) {
		Log.i(TAG, "into 刷新wifi热点列表");
		if (null == adapter) {
			Log.i(TAG, "into 刷新wifi热点列表 adapter is null！");
			adapter = new WifiHotAdapter(results, this);
			listView.setAdapter(adapter);
		} else {
			Log.i(TAG, "into 刷新wifi热点列表 adapter is not null！");
			adapter.refreshData(results);
		}
		Log.i(TAG, "out 刷新wifi热点列表");
	}

	private void saveData() {
		Log.i(TAG, "into saveData()");
		WifiApplication app = (WifiApplication) this.getApplication();
		app.client = this.client;
		app.server = this.server;
		app.wifiHotM = this.wifiHotM;
		Log.i(TAG, "out saveData() app client ="+app.client);
	}

	private void gotoChatActivity() {
		Intent intent = new Intent();
		intent.setClass(this, GroupChatActivity.class);
		this.startActivity(intent);
	}

	private void initServerHandler() {
		serverHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				Log.i(TAG, "into initServerHandler() handleMessage(Message msg)");
				if (msg.what == 0) {
					statu.setText("连接创建失败");
				} else {
					String text = (String) msg.obj;
					statu.setText("连接创建成功");
					Log.i(TAG, "into initServerHandler() handleMessage(Message msg) text = " + text);
				}
			}
		};
	}

	private void initClientHandler() {
		clientHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				Log.i(TAG, "into initClientHandler() handleMessage(Message msg)");
				if (msg.what == 0) {
					statu.setText("连接失败！");
				} else {
					statu.setText("连接成功！");
					String text = (String) msg.obj;
					Log.i(TAG, "into initClientHandler() handleMessage(Message msg) text =" + text);
				}
			}
		};
	}
}
