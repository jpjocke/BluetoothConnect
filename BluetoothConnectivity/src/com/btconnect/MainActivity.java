package com.btconnect;

import com.variables.SVar;

import android.os.Bundle;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;

public class MainActivity extends Activity {
	private static final String TAG = "BTconnect_MAIN";

	private BluetoothAdapter mBluetoothAdapter = null;
	private BtConnectService mBtConnect;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		mBtConnect = new BtConnectService();
	}
	@Override
	protected void onStart(){
		super.onStart();
		//Enable BT directly
		if (!mBluetoothAdapter.isEnabled()) {
			Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableIntent, SVar.REQUEST_ENABLE_BT);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// Stop the Bluetooth chat services
		if (mBtConnect != null) 
			mBtConnect.stop();
	}


	public void onClick(View v){
		if(v == findViewById(R.id.mainServerBtn)){
			Log.d(TAG, "server");
			mBtConnect.startListening();
		}
		else if(v == findViewById(R.id.mainConnectBtn)){
			Log.d(TAG, "connect");
			Intent connect = new Intent(this, DiscoverBT.class);
			startActivityForResult(connect, SVar.REQUEST_DEVICE);
		}
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == SVar.REQUEST_DEVICE){
			if (resultCode == Activity.RESULT_OK) {
				Log.d(TAG, "device found, should connect now " + data.getExtras().getString(SVar.EXTRA_DEVICE_ADDRESS));
				connectDevice(data);
			}
		}
		else if(requestCode == SVar.REQUEST_ENABLE_BT){
			if (resultCode == Activity.RESULT_OK) {
				Log.d(TAG, "BT enabled");
			} else {
				Log.d(TAG, "BT not enabled");
				finish();
			}
		}

	}
	
	private void connectDevice(Intent data) {
        // Get the device MAC address
        String address = data.getExtras()
            .getString(SVar.EXTRA_DEVICE_ADDRESS);
        // Get the BluetoothDevice object
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        // Attempt to connect to the device
        mBtConnect.connectTo(device);
    }

}
