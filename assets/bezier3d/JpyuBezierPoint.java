package com.chsy.bezier3d;

import java.util.ArrayList;

public class JpyuBezierPoint {

	double toRad = Math.PI / 180;
	double toDeg = 180 / Math.PI;
	OrientationInfo OrientationInfoObjSpecified;
	OrientationInfo OrientationInfoObj;
	double bearing;
	double x, y, z;

	public class OrientationInfo {
		double heading;
		double tilt;
		double roll;

		OrientationInfo(double heading, double tilt, double roll) {
			this.heading = heading;
			this.tilt = tilt;
			this.roll = roll;
		}

		public double getHeading() {
			return heading;
		}

		public double getTilt() {
			return tilt;
		}

		public double getRoll() {
			return roll;
		}
	}

	public JpyuBezierPoint(double lon,double lat, double alt, double controlPointGlobalTimeId){
		  
		  JpyuBezierPoint prevJpyuBPoint = null;
		  JpyuBezierPoint nextJpyuBPoint = null;
		  String id = "0";
		  String title = "";
		  
		 int SET = 0;
		 int MOVING = 1;
		 int CLOSEST = 2;
		 int state = 0;
		// int placemark = null;
		  
		 int MYWIDTH = 10;
		 int MYHEIGHT = 10;

		
		double x = lon;
		double y = lat;
		double z = 0;
//		  if (typeof(alt) == "undefined"){
//			  me.z = defaultHeight_global;
//		  }else{
//		     z = alt;
//		  }
		  double bearing = 0;

		  OrientationInfoObjSpecified = new OrientationInfo(0, 0, 0);
		  OrientationInfoObj = new OrientationInfo(0, 0, 0);
		  me.belongToTourPath = [];
		  me.belongToBPoint = [];
		  me.belongToBPointId = [];

		  int controlPointGlobalIndexId = 0;//此control point在全部Bezier curve上之index(相對於全部Bezier curve)
		  int controlPointGlobalDistanceId = 0;//此control point在全部Bezier curve上之Distance index(相對於全部Bezier curve)

		  if( typeof controlPointGlobalTimeId == "undefined"){
			  controlPointGlobalTimeId = 0;//此control point在全部Bezier curve上之Time index(相對於全部Bezier curve)
		  }else{
			 controlPointGlobalTimeId = controlPointGlobalTimeId;//此control point在全部Bezier curve上之Time index(相對於全部Bezier curve)
		  }
		  double deltaDistance = -1;//  -1表示採用前一個control point的設定  //0.0005;  //可控制導覽速度(公尺)
		  double outputDelta = 5;//"1sec"; //trajectory points之輸出模式
		  //"1sec"表示每1秒印出一筆資料
		  //"1km"表示每1km印出一筆資料
		  //"5"表示每段分成5等分來印出一筆資料
		  
	  }

	public JpyuBezierPoint(double old_x, double old_y, double old_z) {
		// TODO Auto-generated constructor stub
	}

	public void setSpecifyBPOrientation() {

		if (OrientationInfoObjSpecified != null) {
			OrientationInfoObj.heading = OrientationInfoObjSpecified.heading;
			OrientationInfoObj.tilt = OrientationInfoObjSpecified.tilt;
			OrientationInfoObj.roll = OrientationInfoObjSpecified.roll;
		}
	}

	public double getLon() {

		return x;
	}

	public double getLat() {

		return y;
	}

	public double getAlt() {

		return z;
	}

	public OrientationInfo getOrientation() {

		return OrientationInfoObj;
	}

	public OrientationInfo getOrientationInfoObj() {

		return OrientationInfoObj;
	}

	public OrientationInfo getOrientationInfoObjSpecified() {

		return OrientationInfoObjSpecified;
	}

	public void saveSpecifyBPOrientation(double heading, double tilt,double roll) {
		
			  if(OrientationInfoObjSpecified == null){
				  me.OrientationInfoObjSpecified = new OrientationInfo(0, 0, 0);
			  }
			  if (typeof(heading) == 'undefined'){
				OrientationInfoObjSpecified.heading = me.OrientationInfoObj.heading;
				 OrientationInfoObjSpecified.tilt = me.OrientationInfoObj.tilt;
				OrientationInfoObjSpecified.roll = me.OrientationInfoObj.roll;
			  }else{
			OrientationInfoObjSpecified.heading = heading;
			OrientationInfoObjSpecified.tilt = tilt;
		    OrientationInfoObjSpecified.roll = roll;
			  }
			  
		  }

	public double getBearing(double bearing) {

		return bearing;
	}

	public void setBearing(double bearing) {

		this.bearing = bearing;
	}

	public double bearingT(JpyuBezierPoint JpyuBPoint) {

		double lat1 = y * toRad, lat2 = JpyuBPoint.y * toRad;
		double dLon = (JpyuBPoint.x - x) * toRad;

		double y = Math.sin(dLon) * Math.cos(lat2);
		double x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1)
				* Math.cos(lat2) * Math.cos(dLon);
		double brng = Math.atan2(y, x);

		bearing = (brng * toDeg + 360) % 360;
		return bearing;
	}

	public double get_x() {

		return x;
	}

	public double get_y() {

		return y;
	}

	public double get_z() {

		return z;
	}

}
