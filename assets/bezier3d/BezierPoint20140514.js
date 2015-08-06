
//class:BezierPoint
  function BezierPoint(JpyuBPoint, controlPointGlobalTimeId) 
  {
	  var me = this;
	  me.prevBPoint = null;
	  me.nextBPoint = null;
	  me.id = "0";
	  me.SET = 0;
	  me.MOVING = 1;
	  me.CLOSEST = 2;
	  me.state = 0;
	  me.placemark = null;

	  me.MYWIDTH = 10;
	  me.MYHEIGHT = 10;

	  me.JpyuBPoint = JpyuBPoint;
	  me.AltitudeMode = ge.ALTITUDE_RELATIVE_TO_GROUND;
	  //me.x = me.JpyuBPoint.get_x();
	  //me.y = me.JpyuBPoint.get_y();
	  //me.z = me.JpyuBPoint.get_z()

	  me.bearing = 0;

	  //me.OrientationInfoObjSpecified = null
	  //me.OrientationInfoObj = new OrientationInfo(0, 0, 0);
	  me.curveInfoObj = null; //xxxxxx
	  me.belongToTourPath = [];

	  me.controlPointGlobalIndexId = 0;//此control point在全部Bezier curve上之index(相對於全部Bezier curve)
	  me.controlPointGlobalDistanceId = 0;//此control point在全部Bezier curve上之Distance index(相對於全部Bezier curve)

	  if( typeof controlPointGlobalTimeId == "undefined"){
		  me.controlPointGlobalTimeId = 0;//此control point在全部Bezier curve上之Time index(相對於全部Bezier curve)
	  }else{
		  me.controlPointGlobalTimeId = controlPointGlobalTimeId;//此control point在全部Bezier curve上之Time index(相對於全部Bezier curve)
	  }
	  me.deltaDistance = -1;//  -1表示採用前一個control point的設定  //0.0005;  //可控制導覽速度(公尺)
	  me.outputDelta = "1sec";//"1sec"; //trajectory points之輸出模式
	  me.outputDelta = "0.008km";//trajectory points之輸出模式
	  me.outputDelta = "0.032km";//trajectory points之輸出模式
	  //me.outputDelta = "5";//trajectory points之輸出模式
	  //"1sec"表示每1秒印出一筆資料
	  //"1km"表示每1km印出一筆資料
	  //"5"表示每段分成5等分來印出一筆資料
	
  }

  BezierPoint.prototype.setSpecifyBPOrientation = function() {
	var me = this;
	  if(me.JpyuBPoint.OrientationInfoObjSpecified != null){
		  me.JpyuBPoint.OrientationInfoObj.heading = me.JpyuBPoint.OrientationInfoObjSpecified.heading;
		  me.JpyuBPoint.OrientationInfoObj.tilt = me.JpyuBPoint.OrientationInfoObjSpecified.tilt;
		  me.JpyuBPoint.OrientationInfoObj.roll = me.JpyuBPoint.OrientationInfoObjSpecified.roll;
	  }
  }

  BezierPoint.prototype.getLon = function() {
	var me = this;
	  return me.JpyuBPoint.get_x();
  }

  BezierPoint.prototype.getLat = function() {
	var me = this;
	  return me.JpyuBPoint.get_y();
  }

  BezierPoint.prototype.getAlt = function() {
	var me = this;
	  return me.JpyuBPoint.get_z();
  }

  BezierPoint.prototype.getOrientation = function() {
	var me = this;
	  return me.JpyuBPoint.OrientationInfoObj;
  }

  BezierPoint.prototype.saveSpecifyBPOrientation = function(heading, tilt, roll) {
	var me = this;
	  if(me.JpyuBPoint.OrientationInfoObjSpecified == null){
		  me.JpyuBPointOrientationInfoObjSpecified = new OrientationInfo(0, 0, 0);
	  }
	  if (typeof(heading) == 'undefined'){
		  me.JpyuBPoint.OrientationInfoObjSpecified.heading = me.JpyuBPoint.OrientationInfoObj.heading;
		  me.JpyuBPoint.OrientationInfoObjSpecified.tilt = me.JpyuBPoint.OrientationInfoObj.tilt;
		  me.JpyuBPoint.OrientationInfoObjSpecified.roll = me.JpyuBPoint.OrientationInfoObj.roll;
	  }else{
		  me.JpyuBPoint.OrientationInfoObjSpecified.heading = heading;
		  me.JpyuBPoint.OrientationInfoObjSpecified.tilt = tilt;
		  me.JpyuBPoint.OrientationInfoObjSpecified.roll = roll;
	  }
	  
  }
  
  
  BezierPoint.prototype.getOrientationInfoObj = function() {
	var me = this;
	return me.JpyuBPoint.OrientationInfoObj;
  }

  BezierPoint.prototype.getOrientationInfoObjSpecified = function() {
	var me = this;
	return me.JpyuBPoint.OrientationInfoObjSpecified;
  }

  BezierPoint.prototype.interpolateOrientationByDistance = function(prevBPoint, d_interpValue) {
	var me = this;
	var OrientationInfoObj = new OrientationInfo(0, 0, 0);
    var t = d_interpValue/me.curveInfoObj.totalArcLength;
    //OrientationInfoObj.heading = prevBPoint.getOrientationInfoObj().heading 
    //                             + t*(me.getOrientationInfoObj().heading - prevBPoint.getOrientationInfoObj().heading)
    OrientationInfoObj.heading = HeadingInterpolation.getHeading(
		    prevBPoint.getOrientationInfoObj().heading,
    		me.getOrientationInfoObj().heading, t);
    OrientationInfoObj.tilt = prevBPoint.getOrientationInfoObj().tilt 
                                 + t*(me.getOrientationInfoObj().tilt - prevBPoint.getOrientationInfoObj().tilt);
    OrientationInfoObj.roll = prevBPoint.getOrientationInfoObj().roll 
                                 + t*(me.getOrientationInfoObj().roll - prevBPoint.getOrientationInfoObj().roll);
	return OrientationInfoObj;
  }
  

  BezierPoint.prototype.getBearing = function(bearing) {
	var me = this;
	return me.bearing;
  }

  BezierPoint.prototype.setBearing = function(bearing) {
	var me = this;
	me.bearing = bearing;
  }
  
  BezierPoint.prototype.bearingTo = function(BPoint) {
	var me = this;
    var lat1 = me.JpyuBPoint.get_y()*toRad;
    var lat2 = BPoint.get_y()*toRad;
    var dLon = (BPoint.get_x()-me.JpyuBPoint.get_x())*toRad;

    var y = Math.sin(dLon) * Math.cos(lat2);
    var x = Math.cos(lat1)*Math.sin(lat2) -
            Math.sin(lat1)*Math.cos(lat2)*Math.cos(dLon);
    var brng = Math.atan2(y, x);

	me.bearing = (brng*toDeg+360) % 360;
    return me.bearing;
  }

  BezierPoint.prototype.makePlacemark = function(lat, lon, alt, altMode, iconStr, title)
  {
	  var me = this;
		// Apply stylemap to a placemark
		  var styleMap = ge.createStyleMap('');

		  // Create normal style for style map
		  var normalStyle = ge.createStyle('');
		  var normalIcon = ge.createIcon('');
		  normalIcon.setHref('http://maps.google.com/mapfiles/kml/paddle/' + iconStr + '.png');
		  normalStyle.getIconStyle().setIcon(normalIcon);
		  //normalStyle.getIconStyle().getHotSpot().set(0.5, ge.UNITS_FRACTION, 0, ge.UNITS_FRACTION);
		    

		  // Create highlight style for style map
		  var highlightStyle = ge.createStyle('');
		  var highlightIcon = ge.createIcon('');
		  //grn-blank,  ylw-blank,   wht-blank,   pink-blank,   ltblu-blank,   blu-blank
		  highlightIcon.setHref('http://maps.google.com/mapfiles/kml/paddle/' + 'grn-blank' + '.png');
		  highlightStyle.getIconStyle().setIcon(highlightIcon);
		  //highlightStyle.getIconStyle().getHotSpot().set(0.5, ge.UNITS_FRACTION, 0, ge.UNITS_FRACTION);

		  styleMap.setNormalStyle(normalStyle);
		  styleMap.setHighlightStyle(highlightStyle);
	//---------------------------------

//	    var icon = ge.createIcon('');
//	    icon.setHref('http://maps.google.com/mapfiles/kml/paddle/' + iconStr + '.png');
	//    
//	    var style = ge.createStyle('');
//	    style.getIconStyle().setIcon(icon);
//	    style.getIconStyle().getHotSpot().set(0.5, ge.UNITS_FRACTION, 0, ge.UNITS_FRACTION);
	    //---------------------------------------------------------
	    var pt = ge.createPoint('');
	    pt.set(lat, lon, alt, altMode, true, false);

	    me.placemark = ge.createPlacemark('');
	    me.placemark.setGeometry(pt);
	    //me.placemark.setStyleSelector(style); 
	    me.placemark.setStyleSelector(styleMap); // Apply stylemap to a placemark

	    me.placemark.setName(title);
	    ge.getFeatures().appendChild(me.placemark);

    // listen to the click event
    google.earth.addEventListener(me.placemark, 'click', function(event) {

    	//doLog("===placemark clicked---");
        // Prevent default balloon from popping up for marker placemarks
        event.preventDefault(); 
        //setCurrentEditingControlPoint(me);

      
    });

  }


  BezierPoint.prototype.updatePlacemark = function() 
  {
	  var me = this;
	  if(me.placemark == null) return;
	  var point = me.placemark.getGeometry();
	  point.setLongitude(me.JpyuBPoint.get_x());
	  point.setLatitude(me.JpyuBPoint.get_y());
	  point.setAltitude(me.JpyuBPoint.get_z());
	  
  }
  
  BezierPoint.prototype.showPlacemark = function(bvalue) 
  {
	  var me = this;
	    if(me.placemark) me.placemark.setVisibility(bvalue);
  }

  BezierPoint.prototype.removePlacemark = function() 
  {
	  var me = this;
	    if(me.placemark) ge.getFeatures().removeChild(me.placemark);
  }
  
  BezierPoint.prototype.moveto = function(x, y, z) 
  {
	  var me = this;
      me.JpyuBPoint.x = x;
      me.JpyuBPoint.y = y;
	  if (typeof(z) == 'undefined'){
		  me.JpyuBPoint.z = defaultHeight_global;
	  }else{
	      me.JpyuBPoint.z = z;
	  }
  }  // moveto
  

  BezierPoint.prototype.setstate = function(new_state) 
  {
	  var me = this;
      me.state = new_state;
  }  // setstate
  


  BezierPoint.prototype.set_x = function(x) 
  {
	  var me = this;
      me.JpyuBPoint.x = x;
  }
  
  
  BezierPoint.prototype.set_y = function(y) 
  {
	  var me = this;
      me.JpyuBPoint.y = y;
  }
  
  BezierPoint.prototype.set_z = function(z) 
  {
	  var me = this;
      me.JpyuBPoint.z = z;
  }

  BezierPoint.prototype.get_x = function() 
  {
	  var me = this;
      return me.JpyuBPoint.get_x();
  }
  
  
  BezierPoint.prototype.get_y = function() 
  {
	  var me = this;
	  //doLog("=============== me.JpyuBPoint="+me.JpyuBPoint);
      return me.JpyuBPoint.get_y();
  }
  
  BezierPoint.prototype.get_z = function() 
  {
	  var me = this;
      return me.JpyuBPoint.get_z();
  }
  
  BezierPoint.prototype.getLon = function() 
  {
	  var me = this;
      return me.JpyuBPoint.get_x();
  }
  
  
  BezierPoint.prototype.getLat = function() 
  {
	  var me = this;
      return me.JpyuBPoint.get_y();
  }
  
  BezierPoint.prototype.getAlt = function() 
  {
	  var me = this;
      return me.JpyuBPoint.get_z();
  }

  BezierPoint.prototype.draw = function() 
  {
	  var me = this;
    //int cx = (int)(x - MYWIDTH/2);
    //int cy = (int)(y - MYWIDTH/2);
    switch (me.state)
    {
      case me.SET:
        //g.setColor(Color.blue);
        //g.fillOval(cx, cy, MYWIDTH, MYHEIGHT);
      break;
      case me.MOVING:
        //g.setColor(Color.white);        
        //g.drawOval(cx + MYWIDTH/4, cy+MYHEIGHT/4, 
        //       MYWIDTH/2, MYHEIGHT/2);
        break;
      case me.CLOSEST:
        //g.setColor(Color.white);
        //g.drawLine(cx, cy, cx+MYWIDTH, cy+MYHEIGHT);
        //g.drawLine(cx, cy+MYHEIGHT, cx+MYWIDTH, cy);
        break;
      default:
        //System.out.println("BezierPoint draw: illegal state");
    }
  }  // draw
  
