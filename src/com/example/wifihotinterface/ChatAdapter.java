package com.example.wifihotinterface;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ChatAdapter extends BaseAdapter {

	private Context mContext;

	private List<ChatMessage> messsages;

	public ChatAdapter(Context context, List<ChatMessage> messsages) {
		this.mContext = context;
		this.messsages = messsages;
	}

	@Override
	public int getCount() {
		return messsages.size();
	}

	@Override
	public Object getItem(int position) {
		return messsages.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = View.inflate(mContext, R.layout.chat_layout, null);
		}
		TextView deviceName = (TextView) convertView.findViewById(R.id.deviceName);
		TextView deviceAddress = (TextView) convertView.findViewById(R.id.deviceAddress);
		TextView msgTime = (TextView) convertView.findViewById(R.id.msgTime);
		TextView chatText = (TextView) convertView.findViewById(R.id.chatText);
		deviceName.setText(messsages.get(position).getDeviceName());
		deviceAddress.setText(messsages.get(position).getNetAddress());
		msgTime.setText(messsages.get(position).getMsgTime());
		chatText.setText(messsages.get(position).getMsg());
		return convertView;
	}

	public void refreshDeviceList(List<ChatMessage> messsages) {
		this.messsages = messsages;
		this.notifyDataSetChanged();
	}
}
