package com.btconnect.variables;

import java.util.UUID;

public class SVar {
    public static final String EXTRA_DEVICE_ADDRESS = "device_address";
    public static final String NAME_APP = "BluetoothCom";
    public static final UUID APP_UUID = UUID.fromString("ba26a7e9-056c-11d2-97cf-00c5646eea45");
    
	public static final int REQUEST_DEVICE = 1;
	public static final int REQUEST_ENABLE_BT = 10;
	public static final int DEVICE_CONNECTED = 20;
	public static final int DEVICE_DISCONNECTED = 21;
	public static final int BT_READ = 22;

	public static final String EXTRA_TYPE = "type";
	public static final String SERVER = "server";
	public static final String CONNECT = "connect";
	public static final String MAC = "mac";
	
	public static int screenWidth = 200;
	public static int screenHeight = 600;
}
