package com.example.wifihotinterface;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.wifihotinterface.pack.SocketClient.ClientMsgListener;
import com.example.wifihotinterface.pack.SocketServer.ServerMsgListener;
import com.google.gson.Gson;

public class GroupChatActivity extends Activity {

	private static final String TAG = "GroupChatActivity";

	private ListView chatList;

	private Button refreshBtn;

	private EditText chatMsg;

	private Button sendMsg;

	private ChatAdapter adapter;

	private String deviceName;

	private String deviceIp;

	private Handler serverHandler;

	private Handler clientHandler;

	public List<ChatMessage> chatMessages = new ArrayList<ChatMessage>();

	private Gson gson;

	private WifiApplication app;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.actiivty_groupchat);
		initUI();
		initListener();
		deviceName = new Build().MODEL;
		deviceIp = "192.168.43.1";
		app = (WifiApplication) this.getApplication();
		initServerHandler();
		initClientHandler();
		initServerListener();
		initClientListener();
	}

	private void initUI() {

		adapter = new ChatAdapter(this, chatMessages);
		chatList = (ListView) this.findViewById(R.id.chatList);
		sendMsg = (Button) this.findViewById(R.id.sendMsg);
		chatMsg = (EditText) this.findViewById(R.id.chatMsg);
		chatList.setAdapter(adapter);

	}

	private void initListener() {

		sendMsg.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String text = chatMsg.getText().toString();
				if (null == text || text.equals("")) {
					Toast.makeText(GroupChatActivity.this, "请输入您需要发送的文本信息", Toast.LENGTH_SHORT).show();
				} else {
					sendChatMsg(structChatMessage(text));
				}
			}
		});
	}

	private String structChatMessage(String text) {

		ChatMessage msg = new ChatMessage();
		msg.setDeviceName(deviceName);
		msg.setNetAddress(deviceIp);
		msg.setMsg(text);
		gson = new Gson();
		return gson.toJson(msg);

	}

	// 發送消息入口
	private void sendChatMsg(String msg) {
		Log.i(TAG, "into sendChatMsg(ChatMessage msg) msg =" + msg);
		if (app.server != null) {
			app.server.sendMsgToAllCLients(msg);
		} else if (app.client != null) {
			app.client.sendMsg(msg);
		}
		Log.i(TAG, "out sendChatMsg(ChatMessage msg) msg =" + msg);
	}

	private void initServerListener() {
		if (app.server == null) {
			return;
		}
		Log.i(TAG, "into initServerListener() app server =" + app.server);
		app.server.setMsgListener(new ServerMsgListener() {
			Message msg = null;

			@Override
			public void handlerHotMsg(String hotMsg) {
				Log.i(TAG, "into initServerListener() handlerHotMsg(String hotMsg) hotMsg = " + hotMsg);
				msg = serverHandler.obtainMessage();
				msg.obj = hotMsg;
				serverHandler.sendMessage(msg);
			}

			@Override
			public void handlerErorMsg(String errorMsg) {
				// TODO Auto-generated method stub

			}
		});
		Log.i(TAG, "out initServerListener() ");
	}

	private void initClientListener() {
		if (app.client == null) {
			return;
		}
		Log.i(TAG, "into initClientListener() app client =" + app.client);
		app.client.setMsgListener(new ClientMsgListener() {

			Message msg = null;

			@Override
			public void handlerHotMsg(String hotMsg) {
				Log.i(TAG, "into initClientListener() handlerHotMsg(String hotMsg) hotMsg = " + hotMsg);
				msg = clientHandler.obtainMessage();
				msg.obj = hotMsg;
				clientHandler.sendMessage(msg);
			}

			@Override
			public void handlerErorMsg(String errorMsg) {
				// TODO Auto-generated method stub
			}
		});
		Log.i(TAG, "out initClientListener()");
	}

	private void initServerHandler() {
		if (app.server == null) {
			return;
		}
		serverHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				Log.i(TAG, "into initServerHandler() handleMessage(Message msg)");
				String text = (String) msg.obj;
				sendChatMsg(text);
				gson = new Gson();
				ChatMessage chatMsg = gson.fromJson(text, ChatMessage.class);
				chatMessages.add(chatMsg);
				adapter.refreshDeviceList(chatMessages);
				Log.i(TAG, "into initServerHandler() handleMessage(Message msg) chatMessage = " + chatMsg);
			}
		};
	}

	private void initClientHandler() {
		if (app.client == null) {
			return;
		}
		clientHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				Log.i(TAG, "into initClientHandler() handleMessage(Message msg)");
				String text = (String) msg.obj;
				gson = new Gson();
				ChatMessage chatMsg = gson.fromJson(text, ChatMessage.class);
				chatMessages.add(chatMsg);
				adapter.refreshDeviceList(chatMessages);
				Log.i(TAG, "into initClientHandler() handleMessage(Message msg) chatMessage =" + chatMsg);
			}
		};
	}

	@Override
	public void onBackPressed() {
		this.finish();
		super.onBackPressed();
	}
}
