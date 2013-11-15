package controller.Activity;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import controller.BOActivityAbstract;
import controller.Activity.R;

//游戏入口
public class BOGameEntranceActivity extends BOActivityAbstract {

	//不需要登录
	public int authorize = this.NOT_LOGIN;
	//不需要用户资料
	public int need_user_info = this.NEED_NOT_USERINFO;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game_entrance);
	}


}
