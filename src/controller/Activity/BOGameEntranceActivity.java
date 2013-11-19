package controller.Activity;

import android.R.string;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.util.Log;
import android.view.Menu;
import android.widget.EditText;
import controller.BOActivityAbstract;
import controller.Activity.R;

//游戏入口
public class BOGameEntranceActivity extends BOActivityAbstract {

	//不需要登录
	public int authorize = this.NOT_LOGIN;
	//不需要用户资料
	public int need_user_info = this.NEED_NOT_USERINFO;
	
	public EditText signname;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game_entrance);
		
		//判断用户是否已注册，如未注册弹出注册层
		signdialog();
		
	}
	
	//用户注册弹窗
	protected void signdialog(){
		 AlertDialog.Builder builder = new Builder(this);
		 signname = new EditText(this);
		 
		 builder.setTitle("请输入姓名");
		 builder.setView(signname);
		 builder.setPositiveButton("确定", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				String sign_name = signname.getText().toString();
				//将用户输入数据存入本地
			}
		});
		 builder.setNegativeButton("取消", null);
		 builder.show();
		 /*new AlertDialog.Builder(this).setTitle("请输入姓名").setIcon(android.R.drawable.ic_dialog_info).setView( new EditText(this))
		 .setPositiveButton("确定", null)
		 .setNegativeButton("取消", null).show();*/
	} 


}
