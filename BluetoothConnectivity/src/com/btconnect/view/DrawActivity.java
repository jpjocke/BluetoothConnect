package com.btconnect.view;

import org.json.JSONException;
import org.json.JSONObject;

import com.btconnect.R;
import com.btconnect.model.DrawList;
import com.btconnect.variables.SVar;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.view.ViewGroup.LayoutParams; 

public class DrawActivity extends Activity{
	private static final String TAG = "DrawActivity";
	private BluetoothAdapter mBluetoothAdapter;
	private BtConnectService mBtConnect;
	private TextView status;
	private DrawList drawList;
	//private DrawPnl drawPnl;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE); 
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,  
				WindowManager.LayoutParams.FLAG_FULLSCREEN);  

		setContentView(R.layout.draw);

		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		mBtConnect = new BtConnectService(mHandler);

		status = (TextView)findViewById(R.id.drawStatus);
		Bundle extras = getIntent().getExtras(); 
		if(extras.getString(SVar.EXTRA_TYPE).compareTo(SVar.SERVER) == 0){
			status.setText("Awaiting client");
			mBtConnect.startListening();
		}
		else{
			status.setText("Connecting to server");
			Log.d(TAG, extras.getString(SVar.MAC));
			connectDevice(extras.getString(SVar.MAC));
		}
		drawList = new DrawList();
		((DrawPnl)findViewById(R.id.drawDrawPnl)).setNetworkInterface(mBtConnect);
		((DrawPnl)findViewById(R.id.drawDrawPnl)).setDrawList(drawList);
		//drawPnl = new DrawPnl(this); 
		//findViewById(R.id.drawFrame).addContentView(drawPnl, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)); 
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mBtConnect != null) 
			mBtConnect.stop();
	}

	private void connectDevice(String macAdress) {
		BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(macAdress);
		mBtConnect.connectTo(device);
	}

	private final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch(msg.what){
			case SVar.DEVICE_CONNECTED:
				status.setText("Device connected");
				break;
			case SVar.DEVICE_DISCONNECTED:
				status.setText("Lost connection");
				break;
			case SVar.BT_READ:
				try {
					drawList.addOneFingerData(new JSONObject((String)msg.obj));
				} catch (JSONException e) {
					Log.e(TAG, "JSON error", e);
				}
				/*
				byte[] read = (byte[]) msg.obj;
				String s= new String(read, 0, msg.arg1);
				try {
					drawList.addOneFingerData(new JSONObject(s));
				} catch (JSONException e) {
					Log.e(TAG, "JSON error", e);
					e.printStackTrace();
				}*/
				//Log.d(TAG, s);
				break;
			}
		}
	};

}
