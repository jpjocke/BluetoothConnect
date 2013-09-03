package com.btconnect.model;

import java.util.ArrayList;
import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

import jocke.helper.JSONhelper;
import android.graphics.Point;

public class OneFingerData {
	private PointFactory pf;
	private ArrayList<PointFloat> pointData;
	private int mId;
	private int color;
	private long createTime;
	
	public OneFingerData(PointFactory pf){
		this.pf = pf;
		pointData = new ArrayList<PointFloat>();
		setmId(-1);
		color = 0;
		createTime = System.currentTimeMillis();
	}
	
	public String toJSON(){
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append(JSONhelper.pairToJson("mId", mId) + ",");
		sb.append(JSONhelper.pairToJson("color", color) + ",");
		sb.append(JSONhelper.pairToJson("createTime", createTime) + ",");
		sb.append(JSONhelper.stringToName("points"));
		sb.append("{");
		for(int i = 0; i < pointData.size(); i++){
			sb.append(JSONhelper.stringToName(String.valueOf(i)));
			sb.append(pointData.get(i).toJSON());
			if(i != pointData.size() - 1)
				sb.append(",");
		}
		sb.append("}}");
		
		return sb.toString();
	}
	
	public void setByJSON(JSONObject jo) throws JSONException{
		mId = jo.getInt("mId");
		color = jo.getInt("color");
		createTime = jo.getLong("createTime");
		JSONObject points = jo.getJSONObject("points"); 
        Iterator<?> keys = points.keys(); 
        while(keys.hasNext()){ 
            String key = (String)keys.next(); 
            addPoint(points.getJSONObject(key).getInt("x"), points.getJSONObject(key).getInt("y"));
        } 
	}

	public int getmId() {
		return mId;
	}

	public void setmId(int mId) {
		this.mId = mId;
	}

	public void addPoint(float x, float y){
		PointFloat p = pf.takePoint();
		p.set(x, y);
		pointData.add(p);
	}
	
	public ArrayList<PointFloat> getPointData() {
		return pointData;
	}
	
	public void reset(){
		returnAllPoints();
		setmId(-1);
		color = 0;
	}
	
	public int getColor(){
		return color;
	}
	
	public void setColor(int c){
		color = c;
	}

	private void returnAllPoints(){
		for(int i = 0; i < pointData.size(); i++){
			pf.returnPoint(pointData.get(i));
		}
		pointData.clear();
	}

	public long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}

}
