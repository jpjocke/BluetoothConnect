package com.btconnect.model;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

public class DrawList {
	private ArrayList<OneFingerData> drawList;
	private OneFingerDataFactory ofdFactory;
	
	public DrawList(){
		drawList = new ArrayList<OneFingerData>();
		ofdFactory = new OneFingerDataFactory();
	}
	
	public ArrayList<OneFingerData> getDrawList(){
		return drawList;
	}
	
	public synchronized void addOneFingerData(OneFingerData ofd){
		drawList.add(ofd);
	}
	
	public void addOneFingerData(JSONObject jo) throws JSONException{
		OneFingerData ofd = ofdFactory.takeOneFingerData(0);
		ofd.setByJSON(jo);
		addOneFingerData(ofd);
	}
	
	public OneFingerDataFactory getOneFingerDataFactory(){
		return ofdFactory;
	}

}
