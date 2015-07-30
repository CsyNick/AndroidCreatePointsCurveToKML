package com.chsy.bezier3d;

import java.util.ArrayList;

import com.chsy.bezier3d.JpyuBezierPoint.OrientationInfo;

public class CurveInfo {

	
	  boolean isUsingLinearInterPolationForZ = true;

	  double firstBPointBearing = 0.0;
	  double lastBPointBearing = 0.0;
	  OrientationInfo firstBPointOrinetationInfoObj = null;
	  OrientationInfo lastBPointOrinetationInfoObj = null;
	  OrientationInfo OrientationInfoObj= null;

	  //x代表lon
	  //y代表lat
	  //z代表alt
	  //4個Control points
	  double px[] = new double[4];
	  double py[] = new double[4];
	  double pz[] = new double[4];
	  double totalArcLength,totalTimeLength;

	  CurveInfo(double p0x,double p0y,double p0z,double p1x,double p1y,double p1z,double p2x,double p2y,double p2z,double p3x,double p3y,double p3z
				,double arcLength,double timeLength){
		  px[0] = p0x;
		  py[0] = p0y;
		  pz[0] = p0z;
		  
		  px[1] = p1x;
		  py[1] = p1y;
		   pz[1] = p1z;
		  
		  px[2] = p2x;
		  py[2] = p2y;
		  pz[2] = p2z;
		  
		  px[3] = p3x;
		  py[3] = p3y;
		  pz[3] = p3z;
		  if( typeof arcLength == "undefined"){
			  totalArcLength = 1.0;
		  }else{
			   totalArcLength = arcLength;  //一般而言，arcLength代表弧長
		  }
		  
		  if( typeof timeLength == "undefined"){
			totalTimeLength = 1.0;
		  }else{
			  totalTimeLength = timeLength;  //timeLength代表此段Curve的時間長度
		  }
		  
}
	 

public void setBearing(double firstBPointBearing,double lastBPointBearing){
	
	this.firstBPointBearing = firstBPointBearing;
	this.lastBPointBearing = lastBPointBearing;	
}

public void setOrientation(OrientationInfo firstBPointOrinetationInfoObj, OrientationInfo lastBPointOrinetationInfoObj)
{


	this.firstBPointOrinetationInfoObj = firstBPointOrinetationInfoObj;
	this.lastBPointOrinetationInfoObj = lastBPointOrinetationInfoObj;	
}

public ArrayList<JpyuBezierPoint> getCurvePoints()
{
	

	ArrayList<JpyuBezierPoint> BPoints =new ArrayList<JpyuBezierPoint>();
	JpyuBezierPoint BPoint;
    //delta=0.05時，共21點(包含第一點及最後一點)
    for(double t=0.0; t <= 1.0; t += 0.05)  // should do this recursively...
    {
    	BPoint = getCurvePointByT(t);
        BPoints.add(BPoint);
    }
    return BPoints;
	
};

public JpyuBezierPoint getCurvePointByT(double t_interpValue)
{

	  // 0 <= t_interpValue <= 1
      double t = 0;
      double old_x = this.px[0];
      double old_y = this.py[0];
      double old_z = this.pz[0];

      t = t_interpValue;
      double f0 = (1-t)*(1-t)*(1-t);
      double f1 = 3*t*(1-t)*(1-t);
      double f2 = 3*t*t*(1-t);
      double f3 = t*t*t;
      double new_x = f0*this.px[0] + f1*this.px[1] + f2*this.px[2] + f3*this.px[3];
      double new_y = f0*this.py[0] + f1*this.py[1] + f2*this.py[2] + f3*this.py[3];
      double new_z;
      if(this.isUsingLinearInterPolationForZ){
          new_z = this.pz[0] + (this.pz[3] - this.pz[0])*t;
      }else{
          new_z = f0*this.pz[0] + f1*this.pz[1] + f2*this.pz[2] + f3*this.pz[3];
      }

      old_x = new_x;
      old_y = new_y;
      old_z = new_z;
      JpyuBezierPoint JpyuBPoint = new JpyuBezierPoint(old_x, old_y, old_z);
      BezierPoint   bezierPoint = new BezierPoint(JpyuBPoint);
      OrientationInfo OrientationInfoObj = this.interpolateOrientationByT(t_interpValue);

      JpyuBPoint.saveSpecifyBPOrientation(OrientationInfoObj.heading, 
    		  OrientationInfoObj.tilt, 
    		  OrientationInfoObj.roll);
      bezierPoint.saveSpecifyBPOrientation(OrientationInfoObj.heading, 
    		  OrientationInfoObj.tilt, 
    		  OrientationInfoObj.roll);
      bezierPoint.bearing = this.interpolateBearingByT(t_interpValue);
    return bezierPoint;
};

public JpyuBezierPoint getCurvePointByDistance(double d_interpValue)
{
	//d_interpValue是從此段curve的起點算起的弧長
	 
      double t = d_interpValue/this.totalArcLength;
      return this.getCurvePointByT(t);
};

public JpyuBezierPoint getCurvePointByTime(double time_interpValue)
{
	//time_interpValue是從此段curve的起點算起的時間

      double t = time_interpValue/this.totalTimeLength;
      return this.getCurvePointByT(t);
};


 public OrientationInfo interpolateOrientationByT(double t_interpValue) {
	
	 OrientationInfo OrientationInfoObj = new OrientationInfo(0, 0, 0);
	 double t = t_interpValue;		
//	doLog("t="+t
//			+"  heading1="+this.firstBPointOrinetationInfoObj.heading
//			+"  heading2="+this.lastBPointOrinetationInfoObj.heading);
  //OrientationInfoObj.heading = this.firstBPointOrinetationInfoObj.heading 
  //                             + t*(this.lastBPointOrinetationInfoObj.heading - this.firstBPointOrinetationInfoObj.heading)
  OrientationInfoObj.heading = HeadingInterpolation.getHeading(
		  this.firstBPointOrinetationInfoObj.heading,
		  this.lastBPointOrinetationInfoObj.heading, t);	
//	doLog(""
//			+"  headingVal="+OrientationInfoObj.heading);
  OrientationInfoObj.tilt = this.firstBPointOrinetationInfoObj.tilt 
                               + t*(this.lastBPointOrinetationInfoObj.tilt - this.firstBPointOrinetationInfoObj.tilt);
  OrientationInfoObj.roll = this.firstBPointOrinetationInfoObj.roll 
                               + t*(this.lastBPointOrinetationInfoObj.roll - this.firstBPointOrinetationInfoObj.roll);
	return OrientationInfoObj;
}


//Keep an angle in [-180,180]
public double fixAngle(double angle) {
	
	while (angle< -180) {
		angle += 360;
	}
	while (angle > 180) {
		angle -= 360;
	}
	return angle;
};

public double interpolateBearingByT(double t_interpValue) {
	
	 OrientationInfoObj = new OrientationInfo(0, 0, 0); 
	 double t = t_interpValue;	
    double bearing = firstBPointBearing 
                   + t*(this.lastBPointBearing - this.firstBPointBearing);
    bearing = fixAngle(bearing);
	return bearing;
}
	
}
