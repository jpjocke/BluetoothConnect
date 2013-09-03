package com.btconnect.model;

import java.util.ArrayList;

import android.graphics.Point;
import android.util.Log;

public class PointFactory {
	private ArrayList<PointFloat> points;
	
	public PointFactory(){
		points = new ArrayList<PointFloat>();
	}
	
	public synchronized PointFloat takePoint(){
		if(points.size() != 0){
			PointFloat p = points.get(0);
			points.remove(0);
			p.set(0, 0);
			return p;
		}
		//Log.d("multi","created new point");
		return new PointFloat();
	}
	
	public synchronized void returnPoint(PointFloat p){
		points.add(p);
	}

}
