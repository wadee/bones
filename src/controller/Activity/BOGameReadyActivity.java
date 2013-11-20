package controller.Activity;

import tools.Socket.SocketServer.ServerMsgListener;

import tools.Socket.SocketClient.ClientMsgListener;
import com.google.gson.Gson;

import tools.Wifi.WifiApplication;
import controller.BOActivityAbstract;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;

//准备游戏
public class BOGameReadyActivity extends BOActivityAbstract {

	private static final String TAG = "BOGameReadyActivity";

	private WifiApplication app;
	
	private Handler serverHandler;
	
	private Handler clientHandler;
	private Gson gson;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game_ready);
		
		app = (WifiApplication) this.getApplication();
		
		TextView youselfView = (TextView) findViewById(R.id.ready_host);
		youselfView.setText(app.tag);
		TextView otherView = (TextView) findViewById(R.id.ready_guest);
		otherView.setText("玩家2");
		
		initServerHandler();
		initClientHandler();
		initServerListener();
		initClientListener();
	}
	
	private void initServerHandler() {
		if (app.server == null) {
			return;
		}
		serverHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				/**
				 * 连接成功后，显示对方
				 */
				Log.i(TAG, "into initServerHandler() handleMessage(Message msg)");
				Log.i("serverhandler", msg.toString());
				String text = (String) msg.obj;
//				sendChatMsg(text);
				gson = new Gson();
//				ChatMessage chatMsg = gson.fromJson(text, ChatMessage.class);
//				chatMessages.add(chatMsg);
//				adapter.refreshDeviceList(chatMessages);
//				Log.i(TAG, "into initServerHandler() handleMessage(Message msg) chatMessage = " + chatMsg);
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
				/**
				 * 连接成功后，显示对方
				 */
				Log.i(TAG, "into initClientHandler() handleMessage(Message msg)");
				Log.i("clienthandler", msg.toString());
				String text = (String) msg.obj;
				gson = new Gson();
//				ChatMessage chatMsg = gson.fromJson(text, ChatMessage.class);
//				chatMessages.add(chatMsg);
//				adapter.refreshDeviceList(chatMessages);
//				Log.i(TAG, "into initClientHandler() handleMessage(Message msg) chatMessage =" + chatMsg);
			}
		};
	}
	private void initServerListener() {
		if (app.server == null) {
			return;
		}
		app.server.setMsgListener(new ServerMsgListener() {
			Message msg = null;

			@Override
			public void handlerHotMsg(String hotMsg) {
				msg = serverHandler.obtainMessage();
				msg.obj = hotMsg;
				serverHandler.sendMessage(msg);
			}

			@Override
			public void handlerErorMsg(String errorMsg) {
				// TODO Auto-generated method stub

			}
		});
	}
	private void initClientListener() {
		if (app.client == null) {
			return;
		}
		app.client.setMsgListener(new ClientMsgListener() {

			Message msg = null;

			@Override
			public void handlerHotMsg(String hotMsg) {
				msg = clientHandler.obtainMessage();
				msg.obj = hotMsg;
				clientHandler.sendMessage(msg);
			}

			@Override
			public void handlerErorMsg(String errorMsg) {
				// TODO Auto-generated method stub
			}
		});
	}
}