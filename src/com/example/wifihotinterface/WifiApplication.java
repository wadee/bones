package com.example.wifihotinterface;

import com.example.wifihotinterface.pack.SocketClient;
import com.example.wifihotinterface.pack.SocketServer;
import com.example.wifihotinterface.pack.WifiHotManager;

import android.app.Application;

public class WifiApplication extends Application {

	public SocketServer server;

	public SocketClient client;

	public WifiHotManager wifiHotM;

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
	}

}
