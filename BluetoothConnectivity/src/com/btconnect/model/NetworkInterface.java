package com.btconnect.model;

import org.json.JSONObject;

public interface NetworkInterface {
	//write to the network, use a valid JSONrepresentation
	public void write(String JsonRepresentation);

}
