package tools.Wifi;


import tools.Socket.SocketClient;
import tools.Socket.SocketServer;
import android.app.Application;

public class WifiApplication extends Application {

	public SocketServer server;

	public SocketClient client;

	public WifiHotManager wifiHotM;

	public String tag;
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
	}

}
