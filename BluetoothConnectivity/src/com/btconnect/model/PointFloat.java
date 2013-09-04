package com.btconnect.model;

import jocke.helper.JSONhelper;

public class PointFloat {
	public float x, y;

	public PointFloat(float x, float y){
		this.x = x;
		this.y = y;
	}

	public PointFloat(){
		x = 0;
		y = 0;
	}

	public void set(float x, float y){
		this.x = x;
		this.y = y;
	}

	public String toJSON(){
		return "{" + 
				JSONhelper.pairToJson("x", x) + 
				"," + JSONhelper.pairToJson("y", y) + 
				"}";
	}
	
	/**
	 * Turns this object to a JSONObject string
	 * normalizes it to this screensize
	 * @param width
	 * @param height
	 * @return
	 */
	public String toJSONNormalized(int width, int height){
		return "{" + 
				JSONhelper.pairToJson("x", (x / (float)width)) + 
				"," + JSONhelper.pairToJson("y", (y / (float)height)) + 
				"}";
	}

}
