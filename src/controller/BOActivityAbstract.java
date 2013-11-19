package controller;
import android.R.integer;
import android.R.string;
import android.app.Activity;
import android.os.Bundle;
import android.provider.SyncStateContract.Constants;
import android.util.Log;

//主要负责验证用户登录相关和一些共有方法的实现与定义
abstract public class BOActivityAbstract extends Activity{
	
	public static int MUST_LOGIN = 1;
	public static int NOT_LOGIN = 2;
	public static int MAYBE_LOGIN = 3;
	
	public static int NEED_USERINFO = 1;
	public static int NEED_NOT_USERINFO = 0;
	
	//是否需要用户登录，默认需要登录
	public int authorize = this.MUST_LOGIN;
	//是否需要用户资料，默认不需要资料
	public int need_user_info = this.NEED_NOT_USERINFO;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i("123", "123");
		//判断是否验证登录
		if(this.authorize == this.MUST_LOGIN  ){
			//TODO:验证登录
			//判断是否需要用户资料
			if(this.need_user_info == this.NEED_NOT_USERINFO){
				//TODO:获取用户信息
			}
		}else{
			
		}
		
		super.onCreate(savedInstanceState);
	}
	
} 