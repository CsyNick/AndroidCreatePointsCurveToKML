package com.chsy.bezier3d;

import com.chsy.bezier3d.JpyuBezierPoint.OrientationInfo;

public class BezierPoint {
	double toRad = Math.PI / 180;
	double toDeg = 180 / Math.PI;
	JpyuBezierPoint JpyuBPoint;
	double bearing;
	CurveInfo curveInfoObj;
	double controlPointGlobalIndexId;
	double controlPointGlobalDistanceId;
	double controlPointGlobalTimeId;
	double deltaDistance;
	String outputDelta;
	public BezierPoint(JpyuBezierPoint jpyuBPoint) {
		// TODO Auto-generated constructor stub
	}

	//class:BezierPoint
	   BezierPoint(JpyuBezierPoint JpyuBPoint,double controlPointGlobalTimeId) 
	  {
		 
		   BezierPoint prevBPoint = null;
		   BezierPoint nextBPoint = null;
		  String id = "0";
		  int SET = 0;
		  int MOVING = 1;
		  int CLOSEST = 2;
		  int  intstate = 0;
//		  this.placemark = null;

		  int MYWIDTH = 10;
		  int MYHEIGHT = 10;

		  this.JpyuBPoint = JpyuBPoint;
		 // this.AltitudeMode = ge.ALTITUDE_RELATIVE_TO_GROUND;
		  //this.x = this.JpyuBPoint.get_x();
		  //this.y = this.JpyuBPoint.get_y();
		  //this.z = this.JpyuBPoint.get_z()

		 this.bearing = 0;

		  //this.OrientationInfoObjSpecified = null
		  //this.OrientationInfoObj = new OrientationInfo(0, 0, 0);
		  this.curveInfoObj = null; //xxxxxx
		  this.belongToTourPath = [];

		  this.controlPointGlobalIndexId = 0;//此control point在全部Bezier curve上之index(相對於全部Bezier curve)
		  this.controlPointGlobalDistanceId = 0;//此control point在全部Bezier curve上之Distance index(相對於全部Bezier curve)

		  if( typeof controlPointGlobalTimeId == "undefined"){
			  this.controlPointGlobalTimeId = 0;//此control point在全部Bezier curve上之Time index(相對於全部Bezier curve)
		  }else{
			  this.controlPointGlobalTimeId = controlPointGlobalTimeId;//此control point在全部Bezier curve上之Time index(相對於全部Bezier curve)
		  }
		  this.deltaDistance = -1;//  -1表示採用前一個control point的設定  //0.0005;  //可控制導覽速度(公尺)
		  this.outputDelta = "1sec";//"1sec"; //trajectory points之輸出模式
		  this.outputDelta = "0.008km";//trajectory points之輸出模式
		  this.outputDelta = "0.032km";//trajectory points之輸出模式
		  //this.outputDelta = "5";//trajectory points之輸出模式
		  //"1sec"表示每1秒印出一筆資料
		  //"1km"表示每1km印出一筆資料
		  //"5"表示每段分成5等分來印出一筆資料
		
	  }

	  public void setSpecifyBPOrientation() {
	
		  if(this.JpyuBPoint.OrientationInfoObjSpecified != null){
			  this.JpyuBPoint.OrientationInfoObj.heading = this.JpyuBPoint.OrientationInfoObjSpecified.heading;
			  this.JpyuBPoint.OrientationInfoObj.tilt = this.JpyuBPoint.OrientationInfoObjSpecified.tilt;
			  this.JpyuBPoint.OrientationInfoObj.roll = this.JpyuBPoint.OrientationInfoObjSpecified.roll;
		  }
	  }

	  public double getLon() {

		  return this.JpyuBPoint.get_x();
	  }

	  public double getLat() {
		
		  return this.JpyuBPoint.get_y();
	  }

	  public double getAlt() {
	
		  return this.JpyuBPoint.get_z();
	  }

	  public OrientationInfo getOrientation() {
		
		  return this.JpyuBPoint.OrientationInfoObj;
	  }

	 public void saveSpecifyBPOrientation(double heading,double tilt, double roll) {
	
		  if(this.JpyuBPoint.OrientationInfoObjSpecified == null){
			  this.JpyuBPointOrientationInfoObjSpecified = new OrientationInfo(0, 0, 0);
		  }
		  if (typeof(heading) == 'undefined'){
			  this.JpyuBPoint.OrientationInfoObjSpecified.heading = this.JpyuBPoint.OrientationInfoObj.heading;
			  this.JpyuBPoint.OrientationInfoObjSpecified.tilt = this.JpyuBPoint.OrientationInfoObj.tilt;
			  this.JpyuBPoint.OrientationInfoObjSpecified.roll = this.JpyuBPoint.OrientationInfoObj.roll;
		  }else{
			  this.JpyuBPoint.OrientationInfoObjSpecified.heading = heading;
			  this.JpyuBPoint.OrientationInfoObjSpecified.tilt = tilt;
			  this.JpyuBPoint.OrientationInfoObjSpecified.roll = roll;
		  }
		  
	  }
	  
	  
	  public OrientationInfo getOrientationInfoObj() {
	
		return this.JpyuBPoint.OrientationInfoObj;
	  }

	  public OrientationInfo getOrientationInfoObjSpecified() {
	
		return this.JpyuBPoint.OrientationInfoObjSpecified;
	  }

	  public OrientationInfo interpolateOrientationByDistance(JpyuBezierPoint prevBPoint,double d_interpValue) {
	
		OrientationInfo OrientationInfoObj = new OrientationInfo(0, 0, 0);
	    double t = d_interpValue/this.curveInfoObj.totalArcLength;
	    //OrientationInfoObj.heading = prevBPoint.getOrientationInfoObj().heading 
	    //                             + t*(this.getOrientationInfoObj().heading - prevBPoint.getOrientationInfoObj().heading)
	    OrientationInfoObj.heading = HeadingInterpolation.getHeading(
			    prevBPoint.getOrientationInfoObj().heading,
	    		this.getOrientationInfoObj().heading, t);
	    OrientationInfoObj.tilt = prevBPoint.getOrientationInfoObj().tilt 
	                                 + t*(this.getOrientationInfoObj().tilt - prevBPoint.getOrientationInfoObj().tilt);
	    OrientationInfoObj.roll = prevBPoint.getOrientationInfoObj().roll 
	                                 + t*(this.getOrientationInfoObj().roll - prevBPoint.getOrientationInfoObj().roll);
		return OrientationInfoObj;
	  }
	  

	 public double getBearing() {
		
		return this.bearing;
	  }

	 public void setBearing(double bearing) {
		
		this.bearing = bearing;
	  }
	  
	 public double bearingTo(JpyuBezierPoint BPoint) {
		
	    double lat1 = this.JpyuBPoint.get_y()*toRad;
	    double lat2 = BPoint.get_y()*toRad;
	    double dLon = (BPoint.get_x()-this.JpyuBPoint.get_x())*toRad;

	    double y = Math.sin(dLon) * Math.cos(lat2);
	    double x = Math.cos(lat1)*Math.sin(lat2) -
	            Math.sin(lat1)*Math.cos(lat2)*Math.cos(dLon);
	    double brng = Math.atan2(y, x);

		this.bearing = (brng*toDeg+360) % 360;
	    return this.bearing;
	  }


	  public void set_x(double x) 
	  {
		  
	      this.JpyuBPoint.x = x;
	  }
	  
	  
	  public void set_y(double y) 
	  {
		  
	      this.JpyuBPoint.y = y;
	  }
	  
	  public void set_z(double z) 
	  {
		
	      this.JpyuBPoint.z = z;
	  }

	  public double get_x() 
	  {
	      return this.JpyuBPoint.get_x();
	  }
	  
	  
	  public double get_y() 
	  {
		
		  //doLog("=============== this.JpyuBPoint="+this.JpyuBPoint);
	      return this.JpyuBPoint.get_y();
	  }
	  
	  public double get_z () 
	  {
	
	      return this.JpyuBPoint.get_z();
	  }
	  
	  
	  
	
	
}
