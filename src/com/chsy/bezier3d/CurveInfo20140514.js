
//class:CurveInfo
//jpyu0520 CurveInfo 應改成 BezierCurve
function CurveInfo(p0x, p0y, p0z, p1x, p1y, p1z, p2x, p2y, p2z, p3x, p3y, p3z
		, arcLength, timeLength)
  {
	  var me = this;
	  me.isUsingLinearInterPolationForZ = true;

	  me.firstBPointBearing = 0.0;
	  me.lastBPointBearing = 0.0;
	  me.firstBPointOrinetationInfoObj = null;
	  me.lastBPointOrinetationInfoObj = null;

	  //x代表lon
	  //y代表lat
	  //z代表alt
	  //4個Control points
	  me.px = [];
	  me.py = [];
	  me.pz = [];
	  
	  me.px[0] = p0x;
	  me.py[0] = p0y;
	  me.pz[0] = p0z;
	  
	  me.px[1] = p1x;
	  me.py[1] = p1y;
	  me.pz[1] = p1z;
	  
	  me.px[2] = p2x;
	  me.py[2] = p2y;
	  me.pz[2] = p2z;
	  
	  me.px[3] = p3x;
	  me.py[3] = p3y;
	  me.pz[3] = p3z;
	  
	  if( typeof arcLength == "undefined"){
		  me.totalArcLength = 1.0
	  }else{
		  me.totalArcLength = arcLength;  //一般而言，arcLength代表弧長
	  }
	  
	  if( typeof timeLength == "undefined"){
		  me.totalTimeLength = 1.0
	  }else{
		  me.totalTimeLength = timeLength;  //timeLength代表此段Curve的時間長度
	  }
	  
  }

CurveInfo.prototype.setBearing = function(firstBPointBearing, lastBPointBearing)
{
	var me = this;

	me.firstBPointBearing = firstBPointBearing;
	me.lastBPointBearing = lastBPointBearing;	
}

CurveInfo.prototype.setOrientation = function(firstBPointOrinetationInfoObj, lastBPointOrinetationInfoObj)
{
	var me = this;

	me.firstBPointOrinetationInfoObj = firstBPointOrinetationInfoObj;
	me.lastBPointOrinetationInfoObj = lastBPointOrinetationInfoObj;	
}

CurveInfo.prototype.getCurvePoints = function()
{
	var me = this;

	var BPoints = [];

    //delta=0.05時，共21點(包含第一點及最後一點)
    for(var t=0.0; t <= 1.0; t += 0.05)  // should do this recursively...
    {
    	BPoint = me.getCurvePointByT(t);
        BPoints.push(BPoint);
    }
    return BPoints;
	
};

CurveInfo.prototype.getCurvePointByT = function(t_interpValue)
{
	  var me = this;

	  // 0 <= t_interpValue <= 1
      var t = 0;
      var old_x = me.px[0];
      var old_y = me.py[0];
      var old_z = me.pz[0];

      t = t_interpValue;
      var f0 = (1-t)*(1-t)*(1-t);
      var f1 = 3*t*(1-t)*(1-t);
      var f2 = 3*t*t*(1-t);
      var f3 = t*t*t;
      var new_x = f0*me.px[0] + f1*me.px[1] + f2*me.px[2] + f3*me.px[3];
      var new_y = f0*me.py[0] + f1*me.py[1] + f2*me.py[2] + f3*me.py[3];
      var new_z;
      if(me.isUsingLinearInterPolationForZ){
          new_z = me.pz[0] + (me.pz[3] - me.pz[0])*t;
      }else{
          new_z = f0*me.pz[0] + f1*me.pz[1] + f2*me.pz[2] + f3*me.pz[3];
      }

      old_x = new_x;
      old_y = new_y;
      old_z = new_z;
	  var JpyuBPoint = new JpyuBezierPoint(old_x, old_y, old_z);
      bezierPoint = new BezierPoint(JpyuBPoint);
      var OrientationInfoObj = me.interpolateOrientationByT(t_interpValue);

      JpyuBPoint.saveSpecifyBPOrientation(OrientationInfoObj.heading, 
    		  OrientationInfoObj.tilt, 
    		  OrientationInfoObj.roll);
      bezierPoint.saveSpecifyBPOrientation(OrientationInfoObj.heading, 
    		  OrientationInfoObj.tilt, 
    		  OrientationInfoObj.roll);
      bezierPoint.bearing = me.interpolateBearingByT(t_interpValue);
    return bezierPoint;
};

CurveInfo.prototype.getCurvePointByDistance = function(d_interpValue)
{
	//d_interpValue是從此段curve的起點算起的弧長
	  var me = this;
      var t = d_interpValue/me.totalArcLength;
      return me.getCurvePointByT(t);
};

CurveInfo.prototype.getCurvePointByTime = function(time_interpValue)
{
	//time_interpValue是從此段curve的起點算起的時間
	  var me = this;
      var t = time_interpValue/me.totalTimeLength;
      return me.getCurvePointByT(t);
};


CurveInfo.prototype.interpolateOrientationByT = function(t_interpValue) {
	var me = this;
	var OrientationInfoObj = new OrientationInfo(0, 0, 0);
  var t = t_interpValue;		
//	doLog("t="+t
//			+"  heading1="+me.firstBPointOrinetationInfoObj.heading
//			+"  heading2="+me.lastBPointOrinetationInfoObj.heading);
  //OrientationInfoObj.heading = me.firstBPointOrinetationInfoObj.heading 
  //                             + t*(me.lastBPointOrinetationInfoObj.heading - me.firstBPointOrinetationInfoObj.heading)
  OrientationInfoObj.heading = HeadingInterpolation.getHeading(
		  me.firstBPointOrinetationInfoObj.heading,
		  me.lastBPointOrinetationInfoObj.heading, t);	
//	doLog(""
//			+"  headingVal="+OrientationInfoObj.heading);
  OrientationInfoObj.tilt = me.firstBPointOrinetationInfoObj.tilt 
                               + t*(me.lastBPointOrinetationInfoObj.tilt - me.firstBPointOrinetationInfoObj.tilt);
  OrientationInfoObj.roll = me.firstBPointOrinetationInfoObj.roll 
                               + t*(me.lastBPointOrinetationInfoObj.roll - me.firstBPointOrinetationInfoObj.roll);
	return OrientationInfoObj;
}


//Keep an angle in [-180,180]
CurveInfo.prototype.fixAngle = function(a) {
	var me = this;
	while (a < -180) {
	 a += 360;
	}
	while (a > 180) {
	 a -= 360;
	}
	return a;
};

CurveInfo.prototype.interpolateBearingByT = function(t_interpValue) {
	var me = this;
	var OrientationInfoObj = new OrientationInfo(0, 0, 0);
    var t = t_interpValue;	
    var bearing = me.firstBPointBearing 
                   + t*(me.lastBPointBearing - me.firstBPointBearing);
    bearing = me.fixAngle(bearing);
	return bearing;
}