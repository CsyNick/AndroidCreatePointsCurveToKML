
//class:OrientationInfo

  var toRad = Math.PI / 180;
  var toDeg = 180 / Math.PI;

  function OrientationInfo(heading, tilt, roll) 
  {
	  var me = this;
	  me.heading = heading;
	  me.tilt = tilt;
	  me.roll = roll;
  }
  
  OrientationInfo.prototype.getHeading = function() {
	var me = this;
	  return me.heading;
  }
  
  OrientationInfo.prototype.getTilt = function() {
	var me = this;
	  return me.tilt;
  }
  
  OrientationInfo.prototype.getRoll = function() {
	var me = this;
	  return me.roll;
  }
  
//class:JpyuBezierPoint

  function JpyuBezierPoint(lon, lat, alt, controlPointGlobalTimeId) 
  {
	  var me = this;
	  me.prevJpyuBPoint = null;
	  me.nextJpyuBPoint = null;
	  me.id = "0";
	  me.title = "";
	  
	  me.SET = 0;
	  me.MOVING = 1;
	  me.CLOSEST = 2;
	  me.state = 0;
	  me.placemark = null;
	  
	  me.MYWIDTH = 10;
	  me.MYHEIGHT = 10;

	  me.AltitudeMode = ge.ALTITUDE_RELATIVE_TO_GROUND;
	  me.x = lon;
	  me.y = lat;

	  if (typeof(alt) == 'undefined'){
		  me.z = defaultHeight_global;
	  }else{
	      me.z = alt;
	  }
	  me.bearing = 0;

	  //me.OrientationInfoObjSpecified = null
	  me.OrientationInfoObjSpecified = new OrientationInfo(0, 0, 0);
	  me.OrientationInfoObj = new OrientationInfo(0, 0, 0);
	  me.belongToTourPath = [];
	  me.belongToBPoint = [];
	  me.belongToBPointId = [];

	  me.controlPointGlobalIndexId = 0;//此control point在全部Bezier curve上之index(相對於全部Bezier curve)
	  me.controlPointGlobalDistanceId = 0;//此control point在全部Bezier curve上之Distance index(相對於全部Bezier curve)

	  if( typeof controlPointGlobalTimeId == "undefined"){
		  me.controlPointGlobalTimeId = 0;//此control point在全部Bezier curve上之Time index(相對於全部Bezier curve)
	  }else{
		  me.controlPointGlobalTimeId = controlPointGlobalTimeId;//此control point在全部Bezier curve上之Time index(相對於全部Bezier curve)
	  }
	  me.deltaDistance = -1;//  -1表示採用前一個control point的設定  //0.0005;  //可控制導覽速度(公尺)
	  me.outputDelta = 5;//"1sec"; //trajectory points之輸出模式
	  //"1sec"表示每1秒印出一筆資料
	  //"1km"表示每1km印出一筆資料
	  //"5"表示每段分成5等分來印出一筆資料
  }

  JpyuBezierPoint.prototype.setSpecifyBPOrientation = function() {
	var me = this;
	  if(me.OrientationInfoObjSpecified != null){
		  me.OrientationInfoObj.heading = me.OrientationInfoObjSpecified.heading;
		  me.OrientationInfoObj.tilt = me.OrientationInfoObjSpecified.tilt;
		  me.OrientationInfoObj.roll = me.OrientationInfoObjSpecified.roll;
	  }
  }

  JpyuBezierPoint.prototype.getLon = function() {
	var me = this;
	  return me.x;
  }

  JpyuBezierPoint.prototype.getLat = function() {
	var me = this;
	  return me.y;
  }

  JpyuBezierPoint.prototype.getAlt = function() {
	var me = this;
	  return me.z;
  }

  JpyuBezierPoint.prototype.getOrientation = function() {
	var me = this;
	  return me.OrientationInfoObj;
  }

  JpyuBezierPoint.prototype.getOrientationInfoObj = function() {
	var me = this;
	return me.OrientationInfoObj;
  }

  JpyuBezierPoint.prototype.getOrientationInfoObjSpecified = function() {
	var me = this;
	return me.OrientationInfoObjSpecified;
  }
  
  JpyuBezierPoint.prototype.saveSpecifyBPOrientation = function(heading, tilt, roll) {
	var me = this;
	  if(me.OrientationInfoObjSpecified == null){
		  me.OrientationInfoObjSpecified = new OrientationInfo(0, 0, 0);
	  }
	  if (typeof(heading) == 'undefined'){
		  me.OrientationInfoObjSpecified.heading = me.OrientationInfoObj.heading;
		  me.OrientationInfoObjSpecified.tilt = me.OrientationInfoObj.tilt;
		  me.OrientationInfoObjSpecified.roll = me.OrientationInfoObj.roll;
	  }else{
		  me.OrientationInfoObjSpecified.heading = heading;
		  me.OrientationInfoObjSpecified.tilt = tilt;
		  me.OrientationInfoObjSpecified.roll = roll;
	  }
	  
  }
  

  JpyuBezierPoint.prototype.getBearing = function(bearing) {
	var me = this;
	return me.bearing;
  }

  JpyuBezierPoint.prototype.setBearing = function(bearing) {
	var me = this;
	me.bearing = bearing;
  }
  
  JpyuBezierPoint.prototype.bearingTo = function(JpyuBPoint) {
	var me = this;
    var lat1 = me.y*toRad, lat2 = JpyuBPoint.y*toRad;
    var dLon = (JpyuBPoint.x-me.x)*toRad;

    var y = Math.sin(dLon) * Math.cos(lat2);
    var x = Math.cos(lat1)*Math.sin(lat2) -
            Math.sin(lat1)*Math.cos(lat2)*Math.cos(dLon);
    var brng = Math.atan2(y, x);

	me.bearing = (brng*toDeg+360) % 360;
    return me.bearing;
  }

  JpyuBezierPoint.prototype.makePlacemark = function(lat, lon, alt, altMode, iconStr, title)
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

  JpyuBezierPoint.prototype.makePlacemarkForScene = function(lat, lon, alt, altMode, iconStr, title)
  {
	  var me = this;
		// Apply stylemap to a placemark
		  var styleMap = ge.createStyleMap('');

		  // Create normal style for style map
		  var normalStyle = ge.createStyle('');
		  var normalIcon = ge.createIcon('');
		  normalIcon.setHref('http://maps.google.com/mapfiles/ms/micons/' + iconStr + '.png');
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


  JpyuBezierPoint.prototype.updatePlacemark = function() 
  {
	  var me = this;
	  var point = me.placemark.getGeometry();
	  //doLog("me.x="+me.x+"  me.y="+me.y+"   me.z="+me.z);
	  point.setLongitude(me.x);
	  point.setLatitude(me.y);
	  point.setAltitude(me.z);
	  
  }
  
  JpyuBezierPoint.prototype.showPlacemark = function(bvalue) 
  {
	  var me = this;
	    if(me.placemark) me.placemark.setVisibility(bvalue);
  }

  JpyuBezierPoint.prototype.removePlacemark = function() 
  {
	  var me = this;
	    if(me.placemark) ge.getFeatures().removeChild(me.placemark);
  }
  
  JpyuBezierPoint.prototype.moveto = function(x, y, z) 
  {
	  var me = this;
      me.x = x;
      me.y = y;
	  if (typeof(z) == 'undefined'){
		  me.z = defaultHeight_global;
	  }else{
	      me.z = z;
	  }
  }  // moveto
  

  JpyuBezierPoint.prototype.setstate = function(new_state) 
  {
	  var me = this;
      me.state = new_state;
  }  // setstate
  
  

  JpyuBezierPoint.prototype.get_x = function() 
  {
	  var me = this;
      return me.x;
  }
  
  
  JpyuBezierPoint.prototype.get_y = function() 
  {
	  var me = this;
      return me.y;
  }
  
  JpyuBezierPoint.prototype.get_z = function() 
  {
	  var me = this;
      return me.z;
  }
  
  JpyuBezierPoint.prototype.getLon = function() 
  {
	  var me = this;
      return me.x;
  }
  
  
  JpyuBezierPoint.prototype.getLat = function() 
  {
	  var me = this;
      return me.y;
  }
  
  JpyuBezierPoint.prototype.getAlt = function() 
  {
	  var me = this;
      return me.z;
  }

  JpyuBezierPoint.prototype.draw = function() 
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
        //System.out.println("JpyuBezierPoint draw: illegal state");
    }
  }  // draw
  
