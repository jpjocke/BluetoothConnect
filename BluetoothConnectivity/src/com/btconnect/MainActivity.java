package com.btconnect;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;

public class MainActivity extends Activity {
	private static final String TAG = "BTconnect_MAIN";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	
	public void onClick(View v){
		if(v == findViewById(R.id.mainServerBtn)){
			Log.d(TAG, "server");
		}
		else if(v == findViewById(R.id.mainConnectBtn)){
			Log.d(TAG, "connect");
		}
	}

}