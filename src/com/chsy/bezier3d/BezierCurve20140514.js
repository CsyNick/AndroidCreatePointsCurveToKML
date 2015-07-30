
//class:BezierCurve
//jpyu0502 BezierCurve應改成BezierCurveBasedPath(由多段Bezier curve組成)
function BezierCurve(cp0, cp1, cp2, cp3) {//jpyu0520 BezierCurveu應改成TourPath
	var me = this;
	me.isUsingLinearInterPolationForZ = false;//z方向採用線性內插
	me.LineStringKmlObj_doc = null;
	me.LineStringKmlObj = null;
	me.KmlLineString = null;
	me.initializeLineStringForBezierCurve();
	
	//jpyu0502 me.controlPoints應改成me.passedPoints
	//此段Bezier curve上之Control points的集合(共四點)
	me.controlPoints = [];//此段Bezier curve上之Control points的集合(共四點)
	me.controlPointsLocalIndexId = [];//此段Bezier curve上之Control points的index(相對於此段Bezier curve)
	me.controlPointsGlobalIndexId = [];//此段Bezier curve上之Control points的index(相對於全部Bezier curve)
	me.controlPointsLocalDistanceId = [];//此段Bezier curve上之Control points的弧長index(相對於此段Bezier curve)
	me.controlPointsGlobalDistanceId = [];//此段Bezier curve上之Control points的弧長index(相對於全部Bezier curve)
	me.controlPointsLocalTimeId = [];//此段Bezier curve上之Control points的Time index(相對於此段Bezier curve)
	me.controlPointsGlobalTimeId = [];//此段Bezier curve上之Control points的Time index(相對於全部Bezier curve)

	me.BezierCurvePoints = [];//此段Bezier curve上藉由內插所產生之點(共61點, for delta= 0.05)
	me.BezierCurvePointsLocalIndexId = [];//各點上的index(相對於此段Bezier curve)
	me.BezierCurvePointsGlobalIndexId = [];//各點上的index(相對於全部Bezier curve)
	me.BezierCurvePointsLocalDistanceId = [];//各點上的弧長index(相對於此段Bezier curve)
	me.BezierCurvePointsGlobalDistanceId = [];//各點上的弧長index(相對於全部Bezier curve)
	me.BezierCurvePointsLocalTimeId = [];//各點上的Time index(相對於此段Bezier curve)
	me.BezierCurvePointsGlobalTimeId = [];//各點上的Time index(相對於全部Bezier curve)

	me.totalDistanceInThisBezierCurve = 1; //此段Bezier curve之總弧長，稍後會自動更新

	me.globalIndexId_min =0.0;
	me.globalIndexId_max = 0.0;
	me.globalTimeId_min = 0.0;
	me.globalTimeId_max = 0.0;
	me.globalDistanceId_min = 0.0;
	me.globalDistanceId_max = 0.0;
	//x代表lon
	//y代表lat
	//z代表alt
	me.x = []; //control point之x座標
	me.y = []; //control point之y座標
	me.z = []; //control point之y座標
	me.p1x = [];
	me.p1y = [];
	me.p1z = [];

	me.p2x = [];
	me.p2y = [];
	me.p2z = [];
	//me.setupControlPoints(cp0, cp1, cp2, cp3);
	// BezierCurvePointCounter += me.BezierCurvePoints.length;

	//	  me.globalIndexId_min = BezierCurvePointCounter;//此段Bezier curve之第一點的index(相對於全部Bezier curves)
	//	  for(var i = 0; i< me.BezierCurvePoints.length; i++){
	//		  me.BezierCurvePointsGlobalIndexId[BezierCurvePointCounter] = BezierCurvePointCounter;
	//		  BezierCurvePointCounter += 1;
	//	  }
	//	  me.globalIndexId_max = BezierCurvePointCounter -1;//此段Bezier curve之最後一點的index(相對於全部Bezier curves)
	//	  
	me.previousBezierCurvePointId = -1;
}

BezierCurve.prototype.updateGlobalProperties = function(startIndexId,
		startDistanceId, startTimeId) {
	var me = this;
    //startTimeId為此段bezierCurve起點所對應之Time
    //startIndexId為此段bezierCurve起點所對應之Index
    //startDistanceId為此段bezierCurve起點所對應之弧長(distance)
	//doLog("startIndexId="+startIndexId+"   startTimeId="+startTimeId+"   startDistanceId="+startDistanceId);

	var len = me.BezierCurvePointsLocalIndexId.length;

	if (typeof (startIndexId) == 'undefined') {
		startIndexId = 0;
	}
	if (typeof (startDistanceId) == 'undefined') {
		startDistanceId = 0.0;
	}
	if (typeof (startTimeId) == 'undefined') {
		startTimeId = 0.0;  //修正TimeId之參考點
	}
	//doLog("startIndexId="+startIndexId+"   startDistanceId="+startDistanceId);
	me.globalIndexId_min = startIndexId + me.BezierCurvePointsLocalIndexId[0];
	me.globalIndexId_max = startIndexId + me.BezierCurvePointsLocalIndexId[len - 1];

	for ( var i = 0; i < len; i++) {
		me.BezierCurvePointsGlobalIndexId[i] = startIndexId
				+ me.BezierCurvePointsLocalIndexId[i];
		me.BezierCurvePointsGlobalDistanceId[i] = startDistanceId
				+ me.BezierCurvePointsLocalDistanceId[i];
		me.BezierCurvePointsGlobalTimeId[i] = startTimeId
		+ me.BezierCurvePointsLocalTimeId[i];
		//doLog("me.BezierCurvePointsGlobalDistanceId[i]="+me.BezierCurvePointsGlobalDistanceId[i]);
	}

	len = me.controlPointsLocalDistanceId.length;
	me.globalDistanceId_min = startDistanceId + me.controlPointsLocalDistanceId[0];
	me.globalDistanceId_max = startDistanceId + me.controlPointsLocalDistanceId[len - 1];

	len = me.controlPointsLocalTimeId.length;
	//doLog("=== startTimeId="+startTimeId+"  me.controlPointsLocalTimeId[0]="+me.controlPointsLocalTimeId[0]
	//+"  me.controlPointsLocalTimeId[len-1]="+me.controlPointsLocalTimeId[len - 1]);
	me.globalTimeId_min = startTimeId + me.controlPointsLocalTimeId[0];
	me.globalTimeId_max = startTimeId + me.controlPointsLocalTimeId[len - 1];
	//doLog("me.globalTimeId_min="+me.globalTimeId_min+"   me.globalTimeId_max="+me.globalTimeId_max);
	
	var totalArcLength = 0;
 	var totalTimeLength = 0;
	for ( var i = 0; i < len; i++) {
		me.controlPointsGlobalIndexId[i] = startIndexId + me.controlPointsLocalIndexId[i];
		me.controlPointsGlobalDistanceId[i] = startDistanceId + me.controlPointsLocalDistanceId[i];
		me.controlPointsGlobalTimeId[i] = startTimeId + me.controlPointsLocalTimeId[i];
		me.controlPoints[i].controlPointGlobalIndexId = me.controlPointsGlobalIndexId[i];
		me.controlPoints[i].controlPointGlobalDistanceId = me.controlPointsGlobalDistanceId[i];
		me.controlPoints[i].controlPointGlobalTimeId = me.controlPointsGlobalTimeId[i];
		//doLog("id="+i+" controlPointGlobalTimeId="+me.controlPoints[i].controlPointGlobalTimeId);
		if (i >= 1) {
			totalTimeLength = me.controlPoints[i].controlPointGlobalTimeId
			        - me.controlPoints[i - 1].controlPointGlobalTimeId;
			totalArcLength = me.controlPoints[i].controlPointGlobalDistanceId
					- me.controlPoints[i - 1].controlPointGlobalDistanceId;
			//建立每個control point上所對應的Bezier curve
			me.controlPoints[i].curveInfoObj = new CurveInfo(me.x[i - 1],
					me.y[i - 1], me.z[i - 1], me.p1x[i - 1], me.p1y[i - 1],
					me.p1z[i - 1], me.p2x[i - 1], me.p2y[i - 1], me.p2z[i - 1],
					me.x[i], me.y[i], me.z[i], totalArcLength, totalTimeLength);
			me.controlPoints[i].curveInfoObj.isUsingLinearInterPolationForZ = me.isUsingLinearInterPolationForZ;

			var firstBPointOrinetationInfoObj = me.controlPoints[i-1].JpyuBPoint.getOrientationInfoObjSpecified();
			var lastBPointOrinetationInfoObj = me.controlPoints[i].JpyuBPoint.getOrientationInfoObjSpecified();
			me.controlPoints[i].curveInfoObj.setOrientation(firstBPointOrinetationInfoObj, lastBPointOrinetationInfoObj);

			var firstBPointBearing = me.controlPoints[i-1].bearing;
			var lastBPointBearing = me.controlPoints[i].bearing;
			me.controlPoints[i].curveInfoObj.setBearing(firstBPointBearing, lastBPointBearing);

		}
	}

//	doLog("---Call BezierCurve.prototype.updateGlobalProperties()");
//	doLog("globalIndexId_min=" + me.globalIndexId_min);
//	doLog("globalIndexId_max=" + me.globalIndexId_max);
//	doLog("globalDistanceId_min=" + me.globalDistanceId_min);
//	doLog("globalDistanceId_max=" + me.globalDistanceId_max);
//	doLog("globalTimeId_min=" + me.globalTimeId_min);
//	doLog("globalTimeId_max=" + me.globalTimeId_max);
//	
}

BezierCurve.prototype.addControlPointsToPath = function(cp) {
	var me = this;
//	var len = me.controlPoints.length;
//	me.controlPoints[len] = cp;
	me.controlPoints.push(cp);
	me.updateBezierCurve();
	//劃出curve必經過控制點

	//if(me.controlPoints.length >= 6)
	//  me.addPointsToLineString();
	//doLog("controlPoints in curve="+me.controlPoints.length);
}

BezierCurve.prototype.setupControlPoints = function(cp0, cp1, cp2, cp3) {
	var me = this;
	//共4個control points
	me.controlPoints[0] = cp0;
	me.controlPoints[1] = cp1;
	me.controlPoints[2] = cp2;
	me.controlPoints[3] = cp3;

	me.updateBezierCurve();
	me.addPointsToLineString();
}

BezierCurve.prototype.updateControlPoints = function(index, cp) {
	var me = this;

	me.controlPoints[index] = cp;

	me.updateBezierCurve();
	//me.addPointsToLineString();
	me.updateBezierCurvePointsInKmlLineString();
}

BezierCurve.prototype.updateBezierCurve = function() {
	var me = this;
	//將此path上的所有點，以二連續點一組的方式來分段。然後求得每一段上的二個控制點 P1及P2
	var noOfPassedPoints = me.controlPoints.length;
	//將此path上的所有點的x座標存到me.x =[]中
	//將此path上的所有點的y座標存到me.y =[]中
	//將此path上的所有點的z座標存到me.z =[]中
	for ( var i = 0; i < noOfPassedPoints; i++) {
		me.x[i] = me.controlPoints[i].get_x();
		me.y[i] = me.controlPoints[i].get_y();
		me.z[i] = me.controlPoints[i].get_z();
	}
	me.compute_P1P2(me.x, me.y, me.z);
	me.getBezierCurvePoints();//取得me.BezierCurvePoints

}

BezierCurve.prototype.getBezierCurvePoints = function() {
	var me = this;
	var BPoints = [];
	var px = [];
	var py = [];
	var pz = [];
	//此段bezier curve中會被等分並產生61個點，而其中有4個點為實際的control point，
	//其index分別被儲存於
	//me.controlPointsLocalIndexId[0],me.controlPointsLocalIndexId[1]
	//me.controlPointsLocalIndexId[2],me.controlPointsLocalIndexId[3]
	me.tmpCounter = 0;
	me.totalDistanceInThisBezierCurve = 0;
	me.totalTimeInThisBezierCurve = 0;
	me.totalTimeInThisSegment = 0;
	
	me.controlPointsLocalIndexId[0] = me.tmpCounter;
	me.controlPointsGlobalIndexId[0] = me.tmpCounter; //將來會再被更新
	me.controlPointsLocalDistanceId = [];
	me.controlPointsGlobalDistanceId = [];
	me.controlPointsLocalTimeId = [];
	me.controlPointsGlobalTimeId = [];
	
	me.BezierCurvePointsLocalIndexId = [];
	me.BezierCurvePointsGlobalIndexId = [];
	me.BezierCurvePointsLocalDistanceId = [];
	me.BezierCurvePointsGlobalDistanceId = [];
	me.BezierCurvePointsLocalTimeId = [];
	me.BezierCurvePointsGlobalTimeId = [];

	//me.x[i]為此path上的第i點的x座標
	//建立此path中，各連續二點間之Bezier curve
	
	for ( var i = 0; i < me.x.length - 1; i++) {  //me.x.length = 4
		//建立代表第i段之Bezier curve
		px[0] = me.x[i];
		py[0] = me.y[i];
		pz[0] = me.z[i];

		px[1] = me.p1x[i];
		py[1] = me.p1y[i];
		pz[1] = me.p1z[i];

		px[2] = me.p2x[i];
		py[2] = me.p2y[i];
		pz[2] = me.p2z[i];

		px[3] = me.x[i + 1];
		py[3] = me.y[i + 1];
		pz[3] = me.z[i + 1];

		if (i == 0) {
			me.controlPointsLocalTimeId  //i=0的點
	                 .push(me.totalTimeInThisBezierCurve);
	        me.controlPointsGlobalTimeId
			         .push(me.controlPoints[i].controlPointGlobalTimeId);
	        //---------------------------------------------------------------                  	        
			me.controlPointsLocalDistanceId//i=0的點
			        .push(me.totalDistanceInThisBezierCurve);
			me.controlPointsGlobalDistanceId
					.push(me.totalDistanceInThisBezierCurve);

			me.totalTimeInThisSegment = me.controlPoints[i+1].controlPointGlobalTimeId
	                                    - me.controlPoints[i].controlPointGlobalTimeId

			me.v1 = [ me.y[i], me.x[i], me.z[i] ];
            //doLog("i="+i +" controlPoints[i+1].controlPointGlobalTimeId="+me.controlPoints[i+1].controlPointGlobalTimeId);
            //doLog("i="+i +" controlPoints[i].controlPointGlobalTimeId="+me.controlPoints[i].controlPointGlobalTimeId);
	//		doLog("i="+i+"  call generateBezierCurvePointsJpyu me.totalTimeInThisSegment="+me.totalTimeInThisSegment+"--------");
			me.generateBezierCurvePointsJpyu(BPoints, px, py, pz, false);
			//上一行中計算得新的me.totalDistanceInThisBezierCurve及me.totalTimeInThisBezierCurve
			//me.tmpCounter會被累加
            
	        me.controlPointsLocalTimeId //i+1的點
	                 .push(me.totalTimeInThisBezierCurve);
	        me.controlPointsGlobalTimeId
	                 .push(me.controlPoints[i+1].controlPointGlobalTimeId);

			me.controlPointsLocalDistanceId//i+1的點
			        .push(me.totalDistanceInThisBezierCurve);
			me.controlPointsGlobalDistanceId
					.push(me.totalDistanceInThisBezierCurve);
		} else {      	        
			me.totalTimeInThisSegment = me.controlPoints[i+1].controlPointGlobalTimeId
            - me.controlPoints[i].controlPointGlobalTimeId
            //doLog("i="+i +" controlPoints[i+1].controlPointGlobalTimeId="+me.controlPoints[i+1].controlPointGlobalTimeId);
           // doLog("i="+i +" controlPoints[i].controlPointGlobalTimeId="+me.controlPoints[i].controlPointGlobalTimeId);
			//doLog("i="+i+"  call generateBezierCurvePointsJpyu me.totalTimeInThisSegment="+me.totalTimeInThisSegment+"--------");
			me.generateBezierCurvePointsJpyu(BPoints, px, py, pz, true);
			//上一行中計算得新的me.totalDistanceInThisBezierCurve及me.totalTimeInThisBezierCurve
			//me.tmpCounter會被累加
	        me.controlPointsLocalTimeId  //i+1的點
          	         .push(me.totalTimeInThisBezierCurve);
            me.controlPointsGlobalTimeId
          	         .push(me.controlPoints[i+1].controlPointGlobalTimeId);

			me.controlPointsLocalDistanceId  //i+1的點
			        .push(me.totalDistanceInThisBezierCurve);
			me.controlPointsGlobalDistanceId
					.push(me.totalDistanceInThisBezierCurve);
		}
		me.controlPointsLocalIndexId[i + 1] = me.tmpCounter - 1;
		me.controlPointsGlobalIndexId[i + 1] = me.tmpCounter - 1;
		//me.controlPointsGlobalIndexId將來會再被更新

	}
//	doLog("---BezierCurve.prototype.getBezierCurvePoints()");
//	for(var i=0; i<me.controlPointsLocalTimeId.length; i++){
//        doLog("i,controlPointLocalTimeId="+i+",  "+me.controlPointsLocalTimeId[i]);
//	}
//	for(var i=0; i<me.controlPointsGlobalTimeId.length; i++){
//        doLog("i,controlPointGlobalTimeId="+i+",  "+me.controlPointsGlobalTimeId[i]);
//	}
	//--------------------------------------
	//共61點(for delta= 0.05)
	me.BezierCurvePoints = BPoints;
	return BPoints;
}

BezierCurve.prototype.generateBezierCurvePointsJpyu = function(BPoints, x, y,
		z, omitFirstPoint) {
	var me = this;
	var dist = 0;
	var time = 0;
	var t = 0;
	var old_x = x[0];
	var old_y = y[0];
	var old_z = z[0];

	//delta=0.05時，共21點(包含第一點及最後一點)
	//doLog("---for ( var k = 0; k <= 20; k++)");
    //1/0.05 ---> 20
	for ( var k = 0; k <= 20; k++)
	{
		t = k*0.05;
		if (omitFirstPoint && k == 0) continue;
		var f0 = (1 - t) * (1 - t) * (1 - t);
		var f1 = 3 * t * (1 - t) * (1 - t);
		var f2 = 3 * t * t * (1 - t);
		var f3 = t * t * t;
		var new_x = f0 * x[0] + f1 * x[1] + f2 * x[2] + f3 * x[3];
		var new_y = f0 * y[0] + f1 * y[1] + f2 * y[2] + f3 * y[3];
		var new_z;// = f0*z[0] + f1*z[1] + f2*z[2] + f3*z[3];
		if (me.isUsingLinearInterPolationForZ) {
			new_z = z[0] + (z[3] - z[0]) * t;
		} else {
			new_z = f0 * z[0] + f1 * z[1] + f2 * z[2] + f3 * z[3];
		}
		old_x = new_x;
		old_y = new_y;
		old_z = new_z;
		//--------------------------------------------------
		me.v2 = [ old_y, old_x, old_z ];		
                                dist = V3.absoluteDistance(me.v1, me.v2) / 1000.0;  //km

		me.totalDistanceInThisBezierCurve = me.totalDistanceInThisBezierCurve
				+ parseFloat(dist);
		me.BezierCurvePointsLocalDistanceId.push(me.totalDistanceInThisBezierCurve);
		me.BezierCurvePointsGlobalDistanceId.push(me.totalDistanceInThisBezierCurve); 
		//me.BezierCurvePointsGlobalDistanceId將來會再被更新
		time = t*me.totalTimeInThisSegment;
		me.BezierCurvePointsLocalTimeId.push(me.totalTimeInThisBezierCurve+time);
		me.BezierCurvePointsGlobalTimeId.push(me.totalTimeInThisBezierCurve+time); 

//		doLog("-----me.tmpCounter="+me.tmpCounter+"  --t="+t
//				+"     me.totalTimeInThisBezierCurve,time="+me.totalTimeInThisBezierCurve+", "+time); 

		me.v1 = [ old_y, old_x, old_z ];
		//--------------------------------------------------
		var JpyuBPoint = new JpyuBezierPoint(old_x, old_y, old_z);
		BPoints.push(new BezierPoint(JpyuBPoint));
		me.BezierCurvePointsLocalIndexId[me.tmpCounter] = me.tmpCounter;
		me.BezierCurvePointsGlobalIndexId[me.tmpCounter] = me.tmpCounter; 
		//me.BezierCurvePointsGlobalIndexId將來會再被更新
		me.tmpCounter += 1; //此時之me.tmpCounter值為目前之總點數
	}

    me.totalTimeInThisBezierCurve = me.totalTimeInThisBezierCurve+me.totalTimeInThisSegment;
	
//	doLog("---BezierCurve.prototype.generateBezierCurvePointsJpyu()");
//	for(var i=0; i<me.BezierCurvePointsLocalDistanceId.length; i++){
//        doLog("i="+i+" BezierCurvePointsLocalDistanceId="+me.BezierCurvePointsLocalDistanceId[i]);
//	}
//	doLog("---------------------------------------------");
//	for(var i=0; i<me.BezierCurvePointsGlobalDistanceId.length; i++){
//        doLog("i="+i+" BezierCurvePointsGlobalDistanceId="+me.BezierCurvePointsGlobalDistanceId[i]);
//	}
    
//	//doLog("---BezierCurve.prototype.generateBezierCurvePointsJpyu()");
//	for(var i=0; i<me.BezierCurvePointsLocalTimeId.length; i++){
//        doLog("i="+i+" BezierCurvePointsLocalTimeId="+me.BezierCurvePointsLocalTimeId[i]);
//	}
//	doLog("---------------------------------------------");
//	for(var i=0; i<me.BezierCurvePointsGlobalTimeId.length; i++){
//        doLog("i="+i+" BezierCurvePointsGlobalTimeId="+me.BezierCurvePointsGlobalTimeId[i]);
//	}
}

BezierCurve.prototype.shiftBezierCurvePointsHeightInKmlLineString = function(deltaHeight) {
	var me = this;
	var lineString_coords = me.KmlLineString.getCoordinates();
	var KmlCoord_jpyu;
	var lat_jpyu;
	var lng_jpyu;
	var alt_jpyu;
	var KmlCoord_jpyu;
	//更新已存在LineString內的所有點之(lon,lat,alt)
	var KmlCoordArray_jpyu = lineString_coords;
	for ( var i = 0; i < KmlCoordArray_jpyu.getLength(); i++) {
		KmlCoord_jpyu = KmlCoordArray_jpyu.get(i);
		lat_jpyu = KmlCoord_jpyu.getLatitude();
		lng_jpyu = KmlCoord_jpyu.getLongitude();
		alt_jpyu = KmlCoord_jpyu.getAltitude() + deltaHeight;
		KmlCoord_jpyu = KmlCoordArray_jpyu.setLatLngAlt(i, lat_jpyu,
				lng_jpyu, alt_jpyu);
	}

}

BezierCurve.prototype.updateBezierCurvePointsInKmlLineString = function() {//jpyu05xx
	var me = this;
	//取得此段之BezierCurvePoints
	var BPoints = me.BezierCurvePoints;
	var lineString_coords = me.KmlLineString.getCoordinates();
	//var alt = defaultHeight_global;
	var KmlCoord_jpyu;
	var lat_jpyu;
	var lng_jpyu;
	var alt_jpyu = defaultHeight_global;
	var KmlCoord_jpyu;
	//更新已存在LineString內的所有點之(lon,lat,alt)
	for ( var i = 0; i < BPoints.length; i++) {
		var KmlCoordArray_jpyu = lineString_coords;
		KmlCoord_jpyu = KmlCoordArray_jpyu.get(i);
		lat_jpyu = KmlCoord_jpyu.getLatitude();
		lng_jpyu = KmlCoord_jpyu.getLongitude();
		alt_jpyu = KmlCoord_jpyu.getAltitude();
		KmlCoord_jpyu = KmlCoordArray_jpyu.setLatLngAlt(i, BPoints[i].get_y(),
				BPoints[i].get_x(), BPoints[i].get_z());
	}

}

BezierCurve.prototype.addPointsToLineString = function() {//jpyu05xx
	var me = this;

	//取得此段之BezierCurvePoints
	var BPoints = me.BezierCurvePoints; //TourPath上用Bezier curve內插求得的點
	var lineString_coords = me.KmlLineString.getCoordinates();
	for ( var i = 0; i < BPoints.length; i++) {
		lineString_coords.pushLatLngAlt(BPoints[i].get_y(), BPoints[i].get_x(),
				BPoints[i].get_z());
	}
}

BezierCurve.prototype.addPointsToLineStringForMouseClicked = function() {//jpyu05xx
	var me = this;

	//取得此段之BezierCurvePoints
	var BPoints = me.BezierCurvePoints; //TourPath上用Bezier curve內插求得的點
	var lineString_coords = me.KmlLineString.getCoordinates();
	//doLog("previousBezierCurvePointId="+(me.previousBezierCurvePointId+1)+" BPoints.length="+BPoints.length);//21  41  61 81
	for ( var i = me.previousBezierCurvePointId+1; i < BPoints.length; i++) {
		lineString_coords.pushLatLngAlt(BPoints[i].get_y(), BPoints[i].get_x(),
				BPoints[i].get_z());
	}
	me.previousBezierCurvePointId = me.BezierCurvePoints.length-1;
	me.updateBezierCurvePointsInKmlLineString();
}

BezierCurve.prototype.compute_P1P2 = function(x, y, z) {
	var me = this;
	var n = x.length - 1;
	//var p1x = [];
	//var p2x = [];
	me.computeControlPoints(x, me.p1x, me.p2x);
	//--------------------------------
	n = y.length - 1;
	//var p1y = [];
	//var p2y = [];
	me.computeControlPoints(y, me.p1y, me.p2y);
	//--------------------------------
	n = z.length - 1;
	//var p1z = [];
	//var p2z = [];
	me.computeControlPoints(z, me.p1z, me.p2z);
	//--------------------------------
}

BezierCurve.prototype.computeControlPoints = function(K, p1, p2) {
	var me = this;
	if(K.length == 2){
		p1[0] = K[0] + (K[1]-K[0])*0.3;
		p2[0] = K[0] + (K[1]-K[0])*0.6;
		return;
	}
	var n = K.length - 1; //K.length為TourPath上通過的point的數目

	/*rhs vector*/
	var a = [];
	var b = [];
	var c = [];
	var r = [];

	/*left most segment*/
	a[0] = 0;
	b[0] = 2;
	c[0] = 1;
	r[0] = K[0] + 2 * K[1];

	/*internal segments*/
	for ( var i = 1; i < n - 1; i++) {
		a[i] = 1;
		b[i] = 4;
		c[i] = 1;
		r[i] = 4 * K[i] + 2 * K[i + 1];
	}

	/*right segment*/
	a[n - 1] = 2;
	b[n - 1] = 7;
	c[n - 1] = 0;
	r[n - 1] = 8 * K[n - 1] + K[n];

	/*solves Ax=b with the Thomas algorithm (from Wikipedia)*/
	for ( var i = 1; i < n; i++) {
		var m = a[i] / b[i - 1];
		b[i] = b[i] - m * c[i - 1];
		r[i] = r[i] - m * r[i - 1];
	}

	p1[n - 1] = r[n - 1] / b[n - 1];
	for ( var i = n - 2; i >= 0; --i)
		p1[i] = (r[i] - c[i] * p1[i + 1]) / b[i];

	/*we have p1, now compute p2*/
	for ( var i = 0; i < n - 1; i++)
		p2[i] = 2 * K[i + 1] - p1[i + 1];

	p2[n - 1] = 0.5 * (K[n] + p1[n - 1]);

	//return {p1:p1, p2:p2
}

BezierCurve.prototype.outputBezierCurvePoints = function(outfileHandle, startIndexId, startTimeId, startDistanceId) {
	var me = this;

	if (typeof (startIndexId) == 'undefined') {
		startIndexId = 0;
		startDistanceId = 0.0;
		startTimeId = 0.0;  //修正TimeId之參考點
	}
	
	outfileHandle.WriteLine("-----------------------------------------");
	for ( var i = 0; i < me.BezierCurvePointsLocalIndexId.length; i++) {
		outfileHandle.Write("<point id='" + i + "'>");
//		outfileHandle.Write(me.BezierCurvePoints[i].get_x());
//		outfileHandle.Write(",");
//		outfileHandle.Write(me.BezierCurvePoints[i].get_y());
//		outfileHandle.Write(",");
//		outfileHandle.Write(me.BezierCurvePoints[i].get_z());
//		outfileHandle.Write("\t\t,");
//		outfileHandle.Write(me.BezierCurvePointsLocalIndexId[i]);
		outfileHandle.Write("\t\t,");
		outfileHandle.Write(startIndexId+me.BezierCurvePointsGlobalIndexId[i]);
//		outfileHandle.Write("\t\t,");
//		outfileHandle.Write(me.BezierCurvePointsLocalTimeId[i]);
		outfileHandle.Write("\t\t,");
		outfileHandle.Write(startTimeId+me.BezierCurvePointsGlobalTimeId[i]);
//		outfileHandle.Write("\t\t,");
//		outfileHandle.Write(me.BezierCurvePointsLocalDistanceId[i]);
		outfileHandle.Write("\t\t\t\t,");
		outfileHandle.Write((startDistanceId+me.BezierCurvePointsGlobalDistanceId[i]).toFixed(5));
		outfileHandle.WriteLine("</point>");
	}
}
BezierCurve.prototype.outputBezierCurveControlPoints = function(outfileHandle, startIndexId, startTimeId, startDistanceId) {
	var me = this;

	if (typeof (startIndexId) == 'undefined') {
		startIndexId = 0;
		startDistanceId = 0.0;
		startTimeId = 0.0;  //修正TimeId之參考點
	}
	
	outfileHandle.WriteLine("-----------------------------------------");
	outfileHandle.WriteLine("globalIndexId_min=" + (startIndexId+me.globalIndexId_min));
	outfileHandle.WriteLine("globalIndexId_max=" + (startIndexId+me.globalIndexId_max));
	outfileHandle.WriteLine("globalTimeId_min=" + (startTimeId+me.globalTimeId_min));
	outfileHandle.WriteLine("globalTimeId_max=" + (startTimeId+me.globalTimeId_max));
	outfileHandle.WriteLine("globalDistanceId_min=" + (startDistanceId+me.globalDistanceId_min));
	outfileHandle.WriteLine("globalDistanceId_max=" + (startDistanceId+me.globalDistanceId_max));
	for ( var i = 0; i < me.controlPointsLocalIndexId.length; i++) {
		outfileHandle.Write("<controlpoint id='" + i + "'>");
//		outfileHandle.Write(me.controlPoints[i].get_x());
//		outfileHandle.Write(",");
//		outfileHandle.Write(me.controlPoints[i].get_y());
//		outfileHandle.Write(",");
//		outfileHandle.Write(me.controlPoints[i].get_z());
//		outfileHandle.Write("\t\t,");
//		outfileHandle.Write(me.controlPointsLocalIndexId[i]);
		outfileHandle.Write("\t\t,");
		outfileHandle.Write(startIndexId+me.controlPointsGlobalIndexId[i]);
//		outfileHandle.Write("\t\t,");
//		outfileHandle.Write(me.controlPointsLocalTimeId[i]);
		outfileHandle.Write("\t\t,");
		outfileHandle.Write(startTimeId+me.controlPointsGlobalTimeId[i]);
//		outfileHandle.Write("\t\t,");
//		outfileHandle.Write(me.controlPointsLocalDistanceId[i]);
		outfileHandle.Write("\t\t,");
		outfileHandle.Write((startDistanceId+me.controlPointsGlobalDistanceId[i]).toFixed(5));
		outfileHandle.WriteLine("</controlpoint>");
	}

}

BezierCurve.prototype.initializeLineStringForBezierCurve = function() {
	var me = this;
	if (me.LineStringKmlObj_doc == null) {
		me.LineStringKmlObj_doc = ge.createDocument('');
		ge.getFeatures().appendChild(me.LineStringKmlObj_doc);
	}
	if (me.LineStringKmlObj)
		me.LineStringKmlObj_doc.getFeatures().removeChild(me.LineStringKmlObj);

	//只產生LineStringKmlObj，稍後可將點加入LineStringKmlObj_coords集合中
	me.KmlLineString = ge.createLineString('');
	me.KmlLineString.setTessellate(true);
	//**********************************************************
	me.KmlLineString.setAltitudeMode(ge.ALTITUDE_RELATIVE_TO_GROUND);
	//me.KmlLineString.setAltitudeMode(ge.ALTITUDE_ABSOLUTE);
	//me.KmlLineString.setAltitudeMode(ge.ALTITUDE_CLAMP_TO_GROUND);

	//**********************************************************
	me.LineStringKmlObj = ge.createPlacemark('');
	me.LineStringKmlObj.setGeometry(me.KmlLineString);
	me.LineStringKmlObj.setVisibility(true);

	me.LineStringKmlObj_doc.getFeatures().appendChild(me.LineStringKmlObj);

	me.LineStringKmlObj.setStyleSelector(ge.createStyle(''));
	var LineStringKmlObj_lineStyle = me.LineStringKmlObj.getStyleSelector()
			.getLineStyle();
	LineStringKmlObj_lineStyle.setWidth(4);
	LineStringKmlObj_lineStyle.getColor().set('ddffffff'); // aabbggrr formatx
	LineStringKmlObj_lineStyle.setColorMode(ge.COLOR_RANDOM);

	var LineStringKmlObj_polyStyle = me.LineStringKmlObj.getStyleSelector()
			.getPolyStyle();
	LineStringKmlObj_polyStyle.getColor().set('ddffffff'); // aabbggrr format
	LineStringKmlObj_polyStyle.setColorMode(ge.COLOR_RANDOM);

	var LineStringKmlObj_coords = me.KmlLineString.getCoordinates();

};

BezierCurve.prototype.showLineStringForBezierCurve = function(bvalue) {
	var me = this;
	me.LineStringKmlObj.setVisibility(bvalue);
}

//BezierCurve.prototype.removeLineStringForBezierCurve = function() {
//	var me = this;
//
//    if(me.placemark) ge.getFeatures().removeChild(me.placemark);
//	me.LineStringKmlObj.setVisibility(bvalue);
//}
		  
		