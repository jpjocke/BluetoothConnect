package com.btconnect.view;

import com.btconnect.R;
import com.btconnect.variables.SVar;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class DiscoverBtActivity extends Activity{
	private static final String TAG = "BTconnect_Discover";
	private BluetoothAdapter mBtAdapter;
    private ArrayAdapter<String> mDevicesArrayAdapter;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.discover_bt);

		// Set result CANCELED in case the user backs out
		setResult(Activity.RESULT_CANCELED);

		mBtAdapter = BluetoothAdapter.getDefaultAdapter();
		mDevicesArrayAdapter = new ArrayAdapter<String>(this, R.layout.devicename);

        ListView newDevicesListView = (ListView) findViewById(R.id.discDevices);
        newDevicesListView.setAdapter(mDevicesArrayAdapter);
        newDevicesListView.setOnItemClickListener(mDeviceClickListener);
		
		// Register for broadcasts when a device is discovered
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(mReceiver, filter);

        // Register for broadcasts when discovery has finished
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(mReceiver, filter);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		// Make sure we're not doing discovery anymore
		if (mBtAdapter != null) 
			mBtAdapter.cancelDiscovery();
		
		// Unregister broadcast listeners
        this.unregisterReceiver(mReceiver);
	}

	public void onClick(View v){
		if(v == findViewById(R.id.discScanBtn)){
			v.setVisibility(View.GONE);
			findViewById(R.id.discProgress).setVisibility(View.VISIBLE);
			doDiscovery();
		}
	}

	private void doDiscovery() {
		Log.d(TAG, "discovery");

		// Indicate scanning in the title
		setProgressBarIndeterminateVisibility(true);

		// If we're already discovering, stop it
		if (mBtAdapter.isDiscovering()) {
			mBtAdapter.cancelDiscovery();
		}

		// Request discover from BluetoothAdapter
		mBtAdapter.startDiscovery();
	}
	
	// The on-click listener for all devices in the ListViews
    private OnItemClickListener mDeviceClickListener = new OnItemClickListener() {
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {
            mBtAdapter.cancelDiscovery();
            // MAC = last 17 chars
            String info = ((TextView) v).getText().toString();
            String address = info.substring(info.length() - 17);

            //return the clicked device
            Intent intent = new Intent();
            intent.putExtra(SVar.EXTRA_DEVICE_ADDRESS, address);
            setResult(Activity.RESULT_OK, intent);
            finish();
        }
    };
	
	// The BroadcastReceiver that listens for discovered devices and when discovery is finished
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                mDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
    			findViewById(R.id.discProgress).setVisibility(View.GONE);
                if(mDevicesArrayAdapter.getCount() == 0)
                	findViewById(R.id.discNoFoundTxt).setVisibility(View.VISIBLE);
            }
        }
    };

}
