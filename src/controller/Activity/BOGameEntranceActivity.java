package controller.Activity;

import javax.security.auth.PrivateCredentialPermission;

import global.BOGlobalUserinfo;
import android.R.anim;
import android.R.string;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import controller.BOActivityAbstract;
import controller.Activity.R;
import data.read.BODrUser;
import data.write.BODwUser;

//游戏入口
public class BOGameEntranceActivity extends BOActivityAbstract {

	//不需要登录
	public int authorize = this.NOT_LOGIN;
	//不需要用户资料
	public int need_user_info = this.NEED_NOT_USERINFO;
	
	public EditText signname;
	
	public BODwUser dwuser;
	
	public String name;
	
	public BODrUser druser;
	
	private Button btn_start;
	
	private Button btn_quit;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game_entrance);
		
		druser = new BODrUser(BOGameEntranceActivity.this);
		name = druser.getUserName();
		//判断用户是否已注册，如未注册弹出注册层
		if(name==null){
			signdialog();
			druser = new BODrUser(BOGameEntranceActivity.this);
			name = druser.getUserName();
		}
		
		//将名字放到全局变量中
		BOGlobalUserinfo.setUserName(name);
		
		//获取开始按钮
		btn_start = (Button) findViewById(R.id.app_start);
		btn_start.setOnClickListener(new StartButtonOnClickListener());
		
		//获取退出按钮
		btn_quit = (Button) findViewById(R.id.app_quit);
		btn_quit.setOnClickListener(new QuitButtonOnClickListener());
		
	}
	
	//用户注册弹窗
	protected void signdialog(){
		 AlertDialog.Builder builder = new Builder(this);
		 signname = new EditText(this);
		 
		 builder.setTitle("请输入姓名");
		 builder.setView(signname);
		 builder.setPositiveButton("确定", new android.content.DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				String sign_name = signname.getText().toString();
				//将用户输入数据存入本地
				dwuser = new BODwUser(BOGameEntranceActivity.this);
				dwuser.insertUserName(sign_name);
				
				BODrUser druser = new BODrUser(BOGameEntranceActivity.this);
				String name = druser.getUserName();
			}
		});
		 builder.setNegativeButton("取消", null);
		 builder.show();
		 /*new AlertDialog.Builder(this).setTitle("请输入姓名").setIcon(android.R.drawable.ic_dialog_info).setView( new EditText(this))
		 .setPositiveButton("确定", null)
		 .setNegativeButton("取消", null).show();*/
	} 


	//点击开始按钮时的事件
	class StartButtonOnClickListener implements android.view.View.OnClickListener{
		@Override
		public void onClick(View v) {
			//打开wifi
			
			//跳转到connect页
			Intent intent = new Intent();
			intent.setClass(BOGameEntranceActivity.this,BOGameConnectActivity.class);
			BOGameEntranceActivity.this.startActivity(intent);
			
		}
	}
	
	//点击退出按钮事件
	class QuitButtonOnClickListener implements android.view.View.OnClickListener{

		@Override
		public void onClick(View arg0) {
			//让用户回到首页
			Intent intent = new Intent(Intent.ACTION_MAIN);  
            intent.addCategory(Intent.CATEGORY_HOME);  
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);  
            startActivity(intent);
            //杀死游戏进程
            android.os.Process.killProcess(android.os.Process.myPid());
		}
		
	}

	
}
