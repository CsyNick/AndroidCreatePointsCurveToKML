package com.chsy.bezier3d;

public class BezierCurveMaker {

double defaultHeight_global = 100.0;
double BezierCurvePointCounter = 0;
BezierCurveMaker() {
	  /*jpyudoc
	  �i���ݩʦp�U:
	 NoOfControlPoints: int
	 controlPoints : []
	 centerOfAllControlPoints_lon : double
	 centerOfAllControlPoints_lat : double

	 BezierCurveSet : []
	 currentControlPoint : BezierPoint
	 currentControlPointId : int
	 myCameraInfo : CameraInfo
	  jpyudoc*/

	boolean isScenePlacemarkUsed = false;
	this.myCameraInfo = null;
	  //------------------------------------------------------------
//	  this.AltitudeModeArr = [];
//	  this.AltitudeModeArr['absolute']= ge.ALTITUDE_ABSOLUTE;
//	  this.AltitudeModeArr['relativeToGround']= ge.ALTITUDE_RELATIVE_TO_GROUND;
//	  this.AltitudeModeArr['clampToGround']= ge.ALTITUDE_CLAMP_TO_GROUND;

	  //this.AltitudeMode = ge.ALTITUDE_ABSOLUTE;
//	  this.AltitudeMode_default = ge.ALTITUDE_ABSOLUTE;//this.AltitudeModeArr['absolute'];
//	  this.AltitudeModeStr = [];
//	  this.AltitudeModeStr[ge.ALTITUDE_ABSOLUTE]= 'absolute';
//	  this.AltitudeModeStr[ge.ALTITUDE_RELATIVE_TO_GROUND]= 'relativeToGround';
//	  this.AltitudeModeStr[ge.ALTITUDE_CLAMP_TO_GROUND]= 'clampToGround';
//	  //------------------------------------------------------------
	  
//	jpyuFileInfo.setDataPathMode();
//	var TourDbaseDir = jpyuFileInfo.absolutePath_of_webPage + "/"
//			+ "../TourDbaseV2";
//	TourDbaseDir = jpyuFileInfo.modyfyFileUrl(TourDbaseDir);
//	this.TourDbaseDir = TourDbaseDir;
//	this.saveToFileUrl = this.TourDbaseDir + "/newTour.xml";//this.fileUrl;
//	this.StraightLineStringKmlObj_doc = null;
//	this.StraightLineStringKmlObj = null;
//	this.isDrawStraightLine = false;

	this.SceneNameMap = [];
	this.SceneIdMap = [];
	this.JpyuBPoints_MapforScenes = [];  //��ڦs�b����������Ҧ����I�����X
	this.JpyuBPoints_forScenes = [];  //��ڦs�b����������Ҧ����I�����X
	this.initialize();

	this.display_hull = false;
	this.display_hint = false;
	this.display_points = true;
}
//
//BezierCurveMaker.prototype.setTourDbaseDir = function(TourDbaseDir){
//	var me = this;
//	this.TourDbaseDir = TourDbaseDir;
//	this.saveToFileUrl = this.TourDbaseDir + "/newTour.xml";//this.fileUrl;
//}

//BezierCurveMaker.prototype.setCameraInfo = function(cameraInfoObj) {
//	var me = this;
//	this.myCameraInfo = cameraInfoObj;
//}

//jpyu0502 ���令BezierCurveBasedPathMaker
public void initialize() {
	this.initializeLineStringForBezierCurve();
	this.desiredNoOfPointsInBezierCurve = 4;
	this.endIndexOfNextBezierCurve = this.desiredNoOfPointsInBezierCurve - 1;
	this.NoOfControlPoints = 0;
	this.JpyuBPoints_Map = [];  //��ڦs�b����������Ҧ��I�����X
	this.JpyuBPoints = [];  //��ڦs�b����������Ҧ��I�����X
	this.controlPoints = [];  //TourPath�����Ҧ��I�����X
	this.centerOfAllControlPoints_lon = 0;
	this.centerOfAllControlPoints_lat = 0;

	this.BezierCurveSet = [];//jpyu0502 ���令bpaths
	this.currentControlPoint = null;
	this.currentControlPointId = null;

	this.needToUpdateGlobalProperties = false;
	
    //�Ψ��x�s�Y�@�ؤ����覡�Ҳ��ͪ������ƾ�
	  this.interpolation_Time = [];
	  this.interpolation_BPoints = [];
}

public void initializeLineStringForBezierCurve() {
	var me = this;
	if (!this.isDrawStraightLine)
		return;
	//�u�e�G�I�s�u������
	if (this.StraightLineStringKmlObj_doc == null) {
		this.StraightLineStringKmlObj_doc = ge.createDocument('');
		ge.getFeatures().appendChild(this.StraightLineStringKmlObj_doc);
	}
	if (this.StraightLineStringKmlObj)
		this.StraightLineStringKmlObj_doc.getFeatures().removeChild(
				this.StraightLineStringKmlObj);

	this.StraightLineStringKmlObj = ge.createPlacemark('');
	this.StraightLineStringKmlObj_lineString = ge.createLineString('');
	this.StraightLineStringKmlObj
			.setGeometry(this.StraightLineStringKmlObj_lineString);
	this.StraightLineStringKmlObj_lineString.setTessellate(true);
	//**********************************************************
	this.StraightLineStringKmlObj_lineString
			.setAltitudeMode(ge.ALTITUDE_RELATIVE_TO_GROUND);
	//this.StraightLineStringKmlObj_lineString.setAltitudeMode(ge.ALTITUDE_ABSOLUTE);
	//this.StraightLineStringKmlObj_lineString.setAltitudeMode(ge.ALTITUDE_CLAMP_TO_GROUND);

	//**********************************************************
	this.StraightLineStringKmlObj.setStyleSelector(ge.createStyle(''));
	this.StraightLineStringKmlObj_lineStyle = this.StraightLineStringKmlObj
			.getStyleSelector().getLineStyle();
	this.StraightLineStringKmlObj_lineStyle.setWidth(4);
	this.StraightLineStringKmlObj_lineStyle.getColor().set('ddffffff'); // aabbggrr formatx
	this.StraightLineStringKmlObj_lineStyle.setColorMode(ge.COLOR_RANDOM);
	this.StraightLineStringKmlObj_polyStyle = this.StraightLineStringKmlObj
			.getStyleSelector().getPolyStyle();
	this.StraightLineStringKmlObj_polyStyle.getColor().set('ddffffff'); // aabbggrr format
	this.StraightLineStringKmlObj_polyStyle.setColorMode(ge.COLOR_RANDOM);

	this.StraightLineStringKmlObj_coords = this.StraightLineStringKmlObj_lineString
			.getCoordinates();
	this.StraightLineStringKmlObj_doc.getFeatures().appendChild(
			this.StraightLineStringKmlObj);

	this.StraightLineStringKmlObj.setVisibility(true);

}

public void showBezierCures(int bvalue) {
	
	for ( int i = 0; i < this.controlPoints.length; i++) {
		this.controlPoints[i].showPlacemark(bvalue);
	}
	for ( int i = 0; i < this.BezierCurveSet.length; i++) {
		this.BezierCurveSet[i].showLineStringForBezierCurve(bvalue);
	}
}

public void clearBezierCurves() {

	// removes all points
	for ( int i = 0; i < this.controlPoints.length; i++) {
		this.controlPoints[i].removePlacemark();
		String idStr = ""+this.JpyuBPoints[i].id;
		if(idStr.indexOf("S") != -1) continue;
		this.JpyuBPoints[i].removePlacemark();
	}
	for ( int i = 0; i < this.BezierCurveSet.length; i++) {
		this.BezierCurveSet[i].initializeLineStringForBezierCurve();
	}
	this.initialize();
};

public void insertBezierCurveAt(int index) {
	//�b���ϥ�TimeId�ɡA���|�����D //todo
	var startPointIndexOfBezierCurve;
	var endPointIndexOfBezierCurve;
	int BezierCurveId = -1;
	if (index == 0) {
		indexH = 0;
		startPointIndexOfBezierCurve = 0;
		endPointIndexOfBezierCurve = startPointIndexOfBezierCurve + 3;
		BezierCurveId = endPointIndexOfBezierCurve / 3 - 1;
	} else if (index % 3 == 0) {
		indexH = 3;
		startPointIndexOfBezierCurve = index - 3;
		endPointIndexOfBezierCurve = startPointIndexOfBezierCurve + 3;
		BezierCurveId = endPointIndexOfBezierCurve / 3 - 1;
	} else {
		//�p�Gindex�ҥN���I�O����Y�qBezier curve��������
		indexH = index % 3;
		startPointIndexOfBezierCurve = index - index % 3;
		endPointIndexOfBezierCurve = startPointIndexOfBezierCurve + 3;
		BezierCurveId = endPointIndexOfBezierCurve / 3 - 1;
	}
	//�bstartPointIndexOfBezierCurve�MendPointIndexOfBezierCurve������������5�ӷscontrol points
	var newControlPoints = [];
	newControlPoints[0] = this.controlPoints[startPointIndexOfBezierCurve];
	newControlPoints[1] = this.controlPoints[startPointIndexOfBezierCurve+1].curveInfoObj.getCurvePointByT(0.5);
	newControlPoints[1].makePlacemark(newControlPoints[1].get_y(), newControlPoints[1].get_x(),
			newControlPoints[1].get_z(), ge.ALTITUDE_RELATIVE_TO_GROUND, 'T', 'new');
	newControlPoints[2] = this.controlPoints[startPointIndexOfBezierCurve+1];
	newControlPoints[3] = this.controlPoints[startPointIndexOfBezierCurve+2].curveInfoObj.getCurvePointByT(0.5);;
	newControlPoints[3].makePlacemark(newControlPoints[3].get_y(), newControlPoints[3].get_x(),
			newControlPoints[3].get_z(), ge.ALTITUDE_RELATIVE_TO_GROUND, 'T', 'new');
	newControlPoints[4] = this.controlPoints[startPointIndexOfBezierCurve+2];
	newControlPoints[5] = this.controlPoints[startPointIndexOfBezierCurve+3].curveInfoObj.getCurvePointByT(0.5);;
	newControlPoints[5].makePlacemark(newControlPoints[5].get_y(), newControlPoints[5].get_x(),
			newControlPoints[5].get_z(), ge.ALTITUDE_RELATIVE_TO_GROUND, 'T', 'new');
	newControlPoints[6] = this.controlPoints[startPointIndexOfBezierCurve+3];
	//�N�s�W���I�[�Jthis.controlPoints(�`�N���ѫ᭱���I���[�J)
	this.controlPoints.splice(startPointIndexOfBezierCurve+3, 0, newControlPoints[5]);
	this.controlPoints.splice(startPointIndexOfBezierCurve+2, 0, newControlPoints[3]);
	this.controlPoints.splice(startPointIndexOfBezierCurve+1, 0, newControlPoints[1]);
	//���s��zcontrol point�s��
	for(var i=0; i<this.controlPoints.length; i++){
		var BPoint = this.controlPoints[i];
		BPoint.placemark.setName(i);
	}
	//�M����BezierCurveId��Bezier curve������LineString����
	this.BezierCurveSet[BezierCurveId].initializeLineStringForBezierCurve();
	//�s�W�G�qBezier curves
	var myBezierCurve1 = new BezierCurve(
			newControlPoints[0],
			newControlPoints[1],
			newControlPoints[2],
			newControlPoints[3]);
	var myBezierCurve2 = new BezierCurve(
			newControlPoints[3],
			newControlPoints[4],
			newControlPoints[5],
			newControlPoints[6]);
	//�R���s����BezierCurveId��BezierCurve�A�M��[�J�s�W���G��BezierCurve
	this.BezierCurveSet.splice(BezierCurveId, 1, myBezierCurve1, myBezierCurve2);

	this.updateBearingAtControlPoints();
	this.updateGlobalProperties();
	//�ץ��s�W���G��BezierCurve�W��control poits�W��heading�]�w
	this.useBearingForAllControlPoints(BezierCurveId, BezierCurveId+1);
}

public int indBezierCurveId(int index) {
	var me = this;
	//��X��index��control point�Ҧb��Bezier curve���s��

	int startPointIndexOfBezierCurve;
	int endPointIndexOfBezierCurve;
	int BezierCurveId = -1;
	if (index == 0) {
		indexH = 0;
		startPointIndexOfBezierCurve = 0;
		endPointIndexOfBezierCurve = startPointIndexOfBezierCurve + 3;
		BezierCurveId = endPointIndexOfBezierCurve / 3 - 1;
	} else if (index % 3 == 0) {
		indexH = 3;
		startPointIndexOfBezierCurve = index - 3;
		endPointIndexOfBezierCurve = startPointIndexOfBezierCurve + 3;
		BezierCurveId = endPointIndexOfBezierCurve / 3 - 1;
	} else {
		//�p�Gindex�ҥN���I�O����Y�qBezier curve��������
		indexH = index % 3;
		startPointIndexOfBezierCurve = index - index % 3;
		endPointIndexOfBezierCurve = startPointIndexOfBezierCurve + 3;
		BezierCurveId = endPointIndexOfBezierCurve / 3 - 1;
	}
	doLog("BezierCurveId="+BezierCurveId);
	return BezierCurveId;
}

BezierCurveMaker.prototype.deletePoint = function() {
	var me = this;
	if(this.controlPoints.length == 0) return;
	//�u��R���̫�@��control point
	var index = this.controlPoints.length-1;  //�̫�@��control point��index
	if (this.BezierCurveSet.length == 0 || index > this.BezierCurveSet.length * 3) {
		//��ܦ��I�|���c��Bezier curve �A�]���i�R��
		//todo  �R�����I
        if(this.controlPoints.length >=2){
	  	  var BPoint = this.controlPoints[this.controlPoints.length-2];
		  this.currentControlPoint = BPoint;
		  this.currentControlPointId = this.controlPoints.length-1;
	      if(this.myCameraInfo) this.myCameraInfo.setLocationAndOrientation(BPoint);
        }
        this.controlPoints[index].removePlacemark();
		this.controlPoints.pop();
		return;
	}
}

public setUpDoubleLinkedListForJpyuBPoint(JpyuBezierPoint JpyuBPoint) {

	int len = this.JpyuBPoints.length;
	if(len == 1){
		JpyuBezierPoint prevJpyuBPoint = null;
		JpyuBPoint.prevJpyuBPoint = prevJpyuBPoint;
		JpyuBPoint.nextJpyuBPoint = null;
	}else{
		JpyuBezierPoint prevJpyuBPoint = this.JpyuBPoints[len-2];
		prevJpyuBPoint.nextJpyuBPoint = JpyuBPoint;
		JpyuBPoint.prevJpyuBPoint = prevJpyuBPoint;
		JpyuBPoint.nextJpyuBPoint = null;
	}
}

public JpyuBezierPoint setUpDoubleLinkedListForBPoint(JpyuBezierPoint BPoint) {

	int len = this.controlPoints.length;
	if(len == 1){
		JpyuBezierPoint prevBPoint = null;
		BPoint.prevBPoint = prevBPoint;
		BPoint.nextBPoint = null;
	}else{
		JpyuBezierPoint prevBPoint = this.controlPoints[len-2];
		prevBPoint.nextBPoint = BPoint;
		BPoint.prevBPoint = prevBPoint;
		BPoint.nextBPoint = null;
	}
}

public JpyuBezierPoint addPoint(double lon,double lat,double alt,boolean isEndOfPath,double controlPointGlobalTimeId) {
	
	double x = lon;
	double y = lat;
	double z = alt;
    
	  if( typeof isEndOfPath == "undefined"){
		  isEndOfPath = false;
	  }
	  
	var myBezierCurve = null;
	if(this.controlPoints.length == 0){
		myBezierCurve = new BezierCurve(
				null,null,null,null);
		this.BezierCurveSet.push(myBezierCurve);
		//doLog("=== new BezierCurve id="+(this.BezierCurveSet.length-1));
	}

	var JpyuBPoint = new JpyuBezierPoint(x, y, z, controlPointGlobalTimeId);
	this.JpyuBPoints.push(JpyuBPoint);
	this.setUpDoubleLinkedListForJpyuBPoint(JpyuBPoint);
	
	var BPoint = new BezierPoint(JpyuBPoint, controlPointGlobalTimeId);
	this.controlPoints.push(BPoint);
	this.setUpDoubleLinkedListForBPoint(BPoint);
	BPoint.id = this.controlPoints.length-1;//of integer type
	//JpyuBPoint.id = BPoint.id;
	this.JpyuBPoints_Map[BPoint.id] = JpyuBPoint;
	myBezierCurve = this.BezierCurveSet[this.BezierCurveSet.length-1];
	myBezierCurve.addControlPointsToPath(BPoint);

	//�O��JpyuBPoint�Q��TourPath�ϥ�
	JpyuBPoint.belongToTourPath[JpyuBPoint.belongToTourPath.length] = myBezierCurve;
	//�O��JpyuBPoint�QBPoint�ϥ�
	JpyuBPoint.belongToBPoint[JpyuBPoint.belongToBPoint.length] = BPoint;
	JpyuBPoint.belongToBPointId[JpyuBPoint.belongToBPointId.length] = BPoint.id;
	//�O��BPoint�ݩ󦹱�TourPath
	BPoint.belongToTourPath[BPoint.belongToTourPath.length] = myBezierCurve;

	if (this.isDrawStraightLine) {
		this.StraightLineStringKmlObj_lineString.getCoordinates().pushLatLngAlt(
				y, x, z);
	}
	this.NoOfControlPoints = this.controlPoints.length;
	if (isEndOfPath || this.NoOfControlPoints > this.endIndexOfNextBezierCurve) {
		myBezierCurve.addPointsToLineString();
		//�ǳƵ������qTourPath�A�÷s�W�s��TourPath
		myBezierCurve = new BezierCurve(
				null,null,null,null);
		this.BezierCurveSet.push(myBezierCurve);
		//doLog("=== new BezierCurve id="+(this.BezierCurveSet.length-1));
		//�N�e��path���̫�@�I�[�J�U�@�qpath����@���q�spath���_�I
		myBezierCurve.addControlPointsToPath(BPoint);
		//�O��JpyuBPoint�Q��TourPath�ϥ�
		JpyuBPoint.belongToTourPath[JpyuBPoint.belongToTourPath.length] = myBezierCurve;
		//�O��JpyuBPoint�QBPoint�ϥ�
		JpyuBPoint.belongToBPoint[JpyuBPoint.belongToBPoint.length] = BPoint;
		JpyuBPoint.belongToBPointId[JpyuBPoint.belongToBPointId.length] = BPoint.id;
		//�O��BPoint�ݩ󦹱�TourPath
		BPoint.belongToTourPath[BPoint.belongToTourPath.length] = myBezierCurve;
		
		this.endIndexOfNextBezierCurve = this.endIndexOfNextBezierCurve + (this.desiredNoOfPointsInBezierCurve-1);
		this.updateBearingAtControlPoints();
		this.updateGlobalProperties();
		this.useBearingForAllControlPoints(this.BezierCurveSet.length-1, this.BezierCurveSet.length-1);
	}
	return BPoint;
}


public JpyuBezierPoint addPointForV5(double lon, double lat,double alt,boolean isEndOfPath,double controlPointGlobalTimeId) {
	//��������isEndOfPath==true�~�|����TourPath
	var me = this;
	var x = lon;
	var y = lat;
	var z = alt;
    
	  if( typeof isEndOfPath == "undefined"){
		  isEndOfPath = false;
	  }
	  
	var myBezierCurve = null;
	if(this.controlPoints.length == 0){
		myBezierCurve = new BezierCurve(
				null,null,null,null);
		this.BezierCurveSet.push(myBezierCurve);
		//doLog("=== new BezierCurve id="+(this.BezierCurveSet.length-1));
	}

	var JpyuBPoint = new JpyuBezierPoint(x, y, z, controlPointGlobalTimeId);
	this.JpyuBPoints.push(JpyuBPoint);
	this.setUpDoubleLinkedListForJpyuBPoint(JpyuBPoint);

	var BPoint = new BezierPoint(JpyuBPoint, controlPointGlobalTimeId);
	this.controlPoints.push(BPoint);
	this.setUpDoubleLinkedListForBPoint(BPoint);
	BPoint.id = this.controlPoints.length-1;//of integer type
	//JpyuBPoint.id = BPoint.id;
	this.JpyuBPoints_Map[BPoint.id] = JpyuBPoint;
		
	myBezierCurve = this.BezierCurveSet[this.BezierCurveSet.length-1];
	myBezierCurve.addControlPointsToPath(BPoint);

	//�O��JpyuBPoint�Q��TourPath�ϥ�
	JpyuBPoint.belongToTourPath[JpyuBPoint.belongToTourPath.length] = myBezierCurve;
	//�O��JpyuBPoint�QBPoint�ϥ�
	JpyuBPoint.belongToBPoint[JpyuBPoint.belongToBPoint.length] = BPoint;
	JpyuBPoint.belongToBPointId[JpyuBPoint.belongToBPointId.length] = this.controlPoints.length-1;
	//�O��BPoint�ݩ󦹱�TourPath
	BPoint.belongToTourPath[BPoint.belongToTourPath.length] = myBezierCurve;
	
	BPoint.belongToTourPath[BPoint.belongToTourPath.length] = myBezierCurve;

	if (this.isDrawStraightLine) {
		this.StraightLineStringKmlObj_lineString.getCoordinates().pushLatLngAlt(
				y, x, z);
	}
	this.NoOfControlPoints = this.controlPoints.length;
	if (isEndOfPath) {
		myBezierCurve.addPointsToLineString();//jpyu05xx
		//�ǳƵ������qTourPath�A�÷s�W�s��TourPath
		myBezierCurve = new BezierCurve(
				null,null,null,null);
		this.BezierCurveSet.push(myBezierCurve);
		//doLog("=== new BezierCurve id="+(this.BezierCurveSet.length-1));
		//�N�e��path���̫�@�I�[�J�U�@�qpath����@���q�spath���_�I
//		myBezierCurve.addControlPointsToPath(BPoint);
//		//�O��JpyuBPoint�Q��TourPath�ϥ�
//		JpyuBPoint.belongToTourPath[JpyuBPoint.belongToTourPath.length] = myBezierCurve;
//		//�O��JpyuBPoint�QBPoint�ϥ�
//		JpyuBPoint.belongToBPoint[JpyuBPoint.belongToBPoint.length] = BPoint;
//		JpyuBPoint.belongToBPointId[JpyuBPoint.belongToBPointId.length] = this.controlPoints.length-1;
//		//�O��BPoint�ݩ󦹱�TourPath
//		BPoint.belongToTourPath[BPoint.belongToTourPath.length] = myBezierCurve;
//		
		this.endIndexOfNextBezierCurve = this.endIndexOfNextBezierCurve + (this.desiredNoOfPointsInBezierCurve-1);
		this.updateBearingAtControlPoints();
		this.updateGlobalProperties();
		this.useBearingForAllControlPoints(this.BezierCurveSet.length-1, this.BezierCurveSet.length-1);
	}
	return BPoint;
}



BezierCurveMaker.prototype.addPointForV5_forSnapBPoint = function(snapJpyuBPoint, isEndOfPath, controlPointGlobalTimeId) {
	//��������isEndOfPath==true�~�|����TourPath
	var me = this;
    
	  if( typeof isEndOfPath == "undefined"){
		  isEndOfPath = false;
	  }
	  
	var myBezierCurve = null;
	if(this.controlPoints.length == 0){
		myBezierCurve = new BezierCurve(
				null,null,null,null);
		this.BezierCurveSet.push(myBezierCurve);
		//doLog("=== new BezierCurve id="+(this.BezierCurveSet.length-1));
	}

	var JpyuBPoint = snapJpyuBPoint;
	this.JpyuBPoints.push(JpyuBPoint);
	this.setUpDoubleLinkedListForJpyuBPoint(JpyuBPoint);

	var BPoint = new BezierPoint(JpyuBPoint, controlPointGlobalTimeId);
	this.controlPoints.push(BPoint);
	this.setUpDoubleLinkedListForBPoint(BPoint);
	BPoint.id = this.controlPoints.length-1;//of integer type
	//JpyuBPoint.id = BPoint.id;
	this.JpyuBPoints_Map[BPoint.id] = JpyuBPoint;
		
	myBezierCurve = this.BezierCurveSet[this.BezierCurveSet.length-1];
	myBezierCurve.addControlPointsToPath(BPoint);

	//�O��JpyuBPoint�Q��TourPath�ϥ�
	JpyuBPoint.belongToTourPath[JpyuBPoint.belongToTourPath.length] = myBezierCurve;
	//�O��JpyuBPoint�QBPoint�ϥ�
	JpyuBPoint.belongToBPoint[JpyuBPoint.belongToBPoint.length] = BPoint;
	JpyuBPoint.belongToBPointId[JpyuBPoint.belongToBPointId.length] = this.controlPoints.length-1;
	//�O��BPoint�ݩ󦹱�TourPath
	BPoint.belongToTourPath[BPoint.belongToTourPath.length] = myBezierCurve;
	
	BPoint.belongToTourPath[BPoint.belongToTourPath.length] = myBezierCurve;

	if (this.isDrawStraightLine) {
		this.StraightLineStringKmlObj_lineString.getCoordinates().pushLatLngAlt(
				y, x, z);
	}
	this.NoOfControlPoints = this.controlPoints.length;
	if (isEndOfPath) {
		myBezierCurve.addPointsToLineString();//jpyu05xx
		//�ǳƵ������qTourPath�A�÷s�W�s��TourPath
		myBezierCurve = new BezierCurve(
				null,null,null,null);
		this.BezierCurveSet.push(myBezierCurve);
		//doLog("=== new BezierCurve id="+(this.BezierCurveSet.length-1));
		//�N�e��path���̫�@�I�[�J�U�@�qpath����@���q�spath���_�I
//		myBezierCurve.addControlPointsToPath(BPoint);
//		//�O��JpyuBPoint�Q��TourPath�ϥ�
//		JpyuBPoint.belongToTourPath[JpyuBPoint.belongToTourPath.length] = myBezierCurve;
//		//�O��JpyuBPoint�QBPoint�ϥ�
//		JpyuBPoint.belongToBPoint[JpyuBPoint.belongToBPoint.length] = BPoint;
//		JpyuBPoint.belongToBPointId[JpyuBPoint.belongToBPointId.length] = this.controlPoints.length-1;
//		//�O��BPoint�ݩ󦹱�TourPath
//		BPoint.belongToTourPath[BPoint.belongToTourPath.length] = myBezierCurve;
//		
		this.endIndexOfNextBezierCurve = this.endIndexOfNextBezierCurve + (this.desiredNoOfPointsInBezierCurve-1);
		this.updateBearingAtControlPoints();
		this.updateGlobalProperties();
		this.useBearingForAllControlPoints(this.BezierCurveSet.length-1, this.BezierCurveSet.length-1);
	}
	return BPoint;
};

BezierCurveMaker.prototype.addPointForV5_forSnapBPoint_noBPointIncreased = function(snapJpyuBPoint, isEndOfPath, controlPointGlobalTimeId) {
	//��������isEndOfPath==true�~�|����TourPath
	var me = this;
    
	  if( typeof isEndOfPath == "undefined"){
		  isEndOfPath = false;
	  }
	  
	var myBezierCurve = null;
	if(this.controlPoints.length == 0){
		myBezierCurve = new BezierCurve(
				null,null,null,null);
		this.BezierCurveSet.push(myBezierCurve);
		//doLog("=== new BezierCurve id="+(this.BezierCurveSet.length-1));
	}
	
	var JpyuBPoint = snapJpyuBPoint;
	var BPoint = snapJpyuBPoint.belongToBPoint[0];

	myBezierCurve = this.BezierCurveSet[this.BezierCurveSet.length-1];

//	var previousBPoint = myBezierCurve.controlPoints[myBezierCurve.controlPoints.length-1];
//	var previousJpyuBPoint = BPoint.JpyuBPoint;
//	if(previousJpyuBPoint == previousJpyuBPoint) return previousBPoint;

	myBezierCurve.addControlPointsToPath(BPoint);

	//�O��JpyuBPoint�Q��TourPath�ϥ�
	JpyuBPoint.belongToTourPath[JpyuBPoint.belongToTourPath.length] = myBezierCurve;
	//�O��JpyuBPoint�QBPoint�ϥ�
	JpyuBPoint.belongToBPoint[JpyuBPoint.belongToBPoint.length] = BPoint;
	JpyuBPoint.belongToBPointId[JpyuBPoint.belongToBPointId.length] = this.controlPoints.length-1;
	//�O��BPoint�ݩ󦹱�TourPath
	BPoint.belongToTourPath[BPoint.belongToTourPath.length] = myBezierCurve;
	
	BPoint.belongToTourPath[BPoint.belongToTourPath.length] = myBezierCurve;

	return BPoint;
};


BezierCurveMaker.prototype.addPointForMouseClicked = function(lon, lat, alt, isEndOfPath, isUsePreviousBPoint, controlPointGlobalTimeId) {
	//��������isEndOfPath==true�~�|����TourPath
	var me = this;
	var x = lon;
	var y = lat;
	var z = alt;
    
	if( typeof isEndOfPath == "undefined"){
		  isEndOfPath = false;
	}

	if( typeof isUsePreviousBPoint == "undefined"){
		isUsePreviousBPoint = true;
	}
	
	var myBezierCurve = null;
	if(this.controlPoints.length == 0){
		//��ܥثe�ä��s�b����TourPath
		myBezierCurve = new BezierCurve(
				null,null,null,null);
		this.BezierCurveSet.push(myBezierCurve);
		//doLog("=== new TourPath id="+(this.BezierCurveSet.length-1));
	}
	
	var JpyuBPoint = new JpyuBezierPoint(x, y, z, controlPointGlobalTimeId);
	this.JpyuBPoints.push(JpyuBPoint);
	this.setUpDoubleLinkedListForJpyuBPoint(JpyuBPoint);

	var BPoint = new BezierPoint(JpyuBPoint, controlPointGlobalTimeId);
	this.controlPoints.push(BPoint);
	this.setUpDoubleLinkedListForBPoint(BPoint);
	BPoint.id = this.controlPoints.length-1;//of integer type
	//JpyuBPoint.id = BPoint.id;
	this.JpyuBPoints_Map[BPoint.id] = JpyuBPoint;
	
	myBezierCurve = this.BezierCurveSet[this.BezierCurveSet.length-1];
	myBezierCurve.addControlPointsToPath(BPoint);
	
	//�O��JpyuBPoint�Q��TourPath�ϥ�
	JpyuBPoint.belongToTourPath[JpyuBPoint.belongToTourPath.length] = myBezierCurve;
	//�O��JpyuBPoint�QBPoint�ϥ�
	JpyuBPoint.belongToBPoint[JpyuBPoint.belongToBPoint.length] = BPoint;
	JpyuBPoint.belongToBPointId[JpyuBPoint.belongToBPointId.length] = BPoint.id;
	//�O��BPoint�ݩ󦹱�TourPath
	BPoint.belongToTourPath[BPoint.belongToTourPath.length] = myBezierCurve;

	if (this.isDrawStraightLine) {
		this.StraightLineStringKmlObj_lineString.getCoordinates().pushLatLngAlt(
				y, x, z);
	}
	this.NoOfControlPoints = this.controlPoints.length;
	myBezierCurve.addPointsToLineStringForMouseClicked();//jpyu05xx

	if (isEndOfPath) {
		//myBezierCurve.addPointsToLineStringForMouseClicked();//jpyu05xx
		//�ǳƵ������qTourPath�A�÷s�W�s��TourPath
		myBezierCurve = new BezierCurve(
				null,null,null,null);
		this.BezierCurveSet.push(myBezierCurve);
		//doLog("=== new BezierCurve id="+(this.BezierCurveSet.length-1));
		if(isUsePreviousBPoint){
			//�N�e��path���̫�@�I�[�J�U�@�qpath����@���q�spath���_�I
			myBezierCurve.addControlPointsToPath(BPoint);
			
			//�O��JpyuBPoint�Q��TourPath�ϥ�
			JpyuBPoint.belongToTourPath[JpyuBPoint.belongToTourPath.length] = myBezierCurve;
			//�O��JpyuBPoint�QBPoint�ϥ�
			JpyuBPoint.belongToBPoint[JpyuBPoint.belongToBPoint.length] = BPoint;
			JpyuBPoint.belongToBPointId[JpyuBPoint.belongToBPointId.length] = BPoint.id;
			//�O��BPoint�ݩ󦹱�TourPath
			BPoint.belongToTourPath[BPoint.belongToTourPath.length] = myBezierCurve;

		}
		this.updateBearingAtControlPoints();
		this.updateGlobalProperties();
		this.useBearingForAllControlPoints(this.BezierCurveSet.length-1, this.BezierCurveSet.length-1);

	}
	return BPoint;
};



BezierCurveMaker.prototype.addPointForMouseClicked_forSnapBPoint = function(snapJpyuBPoint, isEndOfPath, isUsePreviousBPoint) {
	//��������isEndOfPath==true�~�|����TourPath
	var me = this;
    
	if( typeof isEndOfPath == "undefined"){
		  isEndOfPath = false;
	}

	if( typeof isUsePreviousBPoint == "undefined"){
		isUsePreviousBPoint = true;
	}
	
	var myBezierCurve = null;
	if(this.controlPoints.length == 0){
		//��ܥثe�ä��s�b����TourPath
		myBezierCurve = new BezierCurve(
				null,null,null,null);
		this.BezierCurveSet.push(myBezierCurve);
		//doLog("=== new TourPath id="+(this.BezierCurveSet.length-1));
	}

	var JpyuBPoint = snapJpyuBPoint;
	this.JpyuBPoints.push(JpyuBPoint);
	this.setUpDoubleLinkedListForJpyuBPoint(JpyuBPoint);

	var BPoint = new BezierPoint(JpyuBPoint);
	this.controlPoints.push(BPoint);
	this.setUpDoubleLinkedListForBPoint(BPoint);
	BPoint.id = this.controlPoints.length-1;//of integer type
	this.JpyuBPoints_Map[BPoint.id] = JpyuBPoint;

	myBezierCurve = this.BezierCurveSet[this.BezierCurveSet.length-1];
	myBezierCurve.addControlPointsToPath(BPoint);

	//�O��JpyuBPoint�Q��TourPath�ϥ�
	JpyuBPoint.belongToTourPath[JpyuBPoint.belongToTourPath.length] = myBezierCurve;
	//�O��JpyuBPoint�QBPoint�ϥ�
	JpyuBPoint.belongToBPoint[JpyuBPoint.belongToBPoint.length] = BPoint;
	JpyuBPoint.belongToBPointId[JpyuBPoint.belongToBPointId.length] = BPoint.id;
	//�O��BPoint�ݩ󦹱�TourPath
	BPoint.belongToTourPath[BPoint.belongToTourPath.length] = myBezierCurve;

	if (this.isDrawStraightLine) {
		this.StraightLineStringKmlObj_lineString.getCoordinates().pushLatLngAlt(
				y, x, z);
	}
	this.NoOfControlPoints = this.controlPoints.length;
	myBezierCurve.addPointsToLineStringForMouseClicked();//jpyu05xx

	if (isEndOfPath) {
		//myBezierCurve.addPointsToLineStringForMouseClicked();//jpyu05xx
		//�ǳƵ������qTourPath�A�÷s�W�s��TourPath
		myBezierCurve = new BezierCurve(
				null,null,null,null);
		this.BezierCurveSet.push(myBezierCurve);
		//doLog("=== new BezierCurve id="+(this.BezierCurveSet.length-1));
		if(isUsePreviousBPoint){
			//�N�e��path���̫�@�I�[�J�U�@�qpath����@���q�spath���_�I
			myBezierCurve.addControlPointsToPath(BPoint);
			
			//�O��JpyuBPoint�Q��TourPath�ϥ�
			JpyuBPoint.belongToTourPath[JpyuBPoint.belongToTourPath.length] = myBezierCurve;
			//�O��JpyuBPoint�QBPoint�ϥ�
			JpyuBPoint.belongToBPoint[JpyuBPoint.belongToBPoint.length] = BPoint;
			JpyuBPoint.belongToBPointId[JpyuBPoint.belongToBPointId.length] = BPoint.id;
			//�O��BPoint�ݩ󦹱�TourPath
			BPoint.belongToTourPath[BPoint.belongToTourPath.length] = myBezierCurve;

		}
		this.updateBearingAtControlPoints();
		this.updateGlobalProperties();
		this.useBearingForAllControlPoints(this.BezierCurveSet.length-1, this.BezierCurveSet.length-1);
	}
	return BPoint;
};

BezierCurveMaker.prototype.updateGlobalProperties = function() {
	var me = this;
	var startIndexId = 0;
	var startDistanceId = 0.0;
	var startTimeId = 0.0;//this.controlPoints[0].controlPointGlobalTimeId;//0.0;
	//jpyu doLog("xxx--startTimeId="+this.controlPoints[0].controlPointGlobalTimeId);
	this.globalIndexId_min = startIndexId;
	this.globalDistanceId_min = startDistanceId;
	this.globalTimeId_min = startTimeId;

	for ( var i = 0; i < this.BezierCurveSet.length; i++) {
		var tmpBezierCurve = this.BezierCurveSet[i];
		tmpBezierCurve.updateGlobalProperties(startIndexId, startDistanceId, startTimeId);
		startIndexId = tmpBezierCurve.globalIndexId_max;
		startDistanceId = tmpBezierCurve.globalDistanceId_max;
		startTimeId = tmpBezierCurve.globalTimeId_max;

	}
	this.globalIndexId_max = startIndexId;
	this.globalDistanceId_max = startDistanceId;
	this.globalTimeId_max = startTimeId;

	this.findCenterOfAllControlPoints();
};

BezierCurveMaker.prototype.findCenterOfAllControlPoints = function() {
	var me = this;
	this.centerOfAllControlPoints_lon = 0;
	this.centerOfAllControlPoints_lat = 0;
	for (var index=0; index<this.controlPoints.length; index++) {
		this.centerOfAllControlPoints_lon += this.controlPoints[index].get_x();
		this.centerOfAllControlPoints_lat += this.controlPoints[index].get_y();
	}
	this.centerOfAllControlPoints_lon = this.centerOfAllControlPoints_lon/this.controlPoints.length;
	this.centerOfAllControlPoints_lat = this.centerOfAllControlPoints_lat/this.controlPoints.length;
};

BezierCurveMaker.prototype.getControlPoint = function(index) {
	var me = this;
	return this.controlPoints[index];

}

BezierCurveMaker.prototype.modifyHeightOfAllControlPoints = function(deltaHeight) {
	var me = this;
	for (var index=0; index<this.controlPoints.length; index++) {
		//��s�s����index���I�������y��
		var new_x = this.controlPoints[index].get_x();
		var new_y = this.controlPoints[index].get_y();
		var new_z = this.controlPoints[index].get_z() + deltaHeight;
		this.controlPoints[index].moveto(new_x, new_y, new_z);
		this.controlPoints[index].updatePlacemark();
	}

	for (var i = 0; i < this.BezierCurveSet.length; i++) {
		var tmpBezierCurve = this.BezierCurveSet[i];
		tmpBezierCurve.updateBezierCurve();
		tmpBezierCurve.shiftBezierCurvePointsHeightInKmlLineString(deltaHeight);
	}
	this.updateGlobalProperties();
}


BezierCurveMaker.prototype.updateControlPoint = function(JpyuBPoint, new_lon, new_lat,
		new_alt) {
	var me = this;
	var new_x = new_lon;
	var new_y = new_lat;
	var new_z = new_alt;
	var indexH = 0;
	//��s�s����index���I�������y��
	JpyuBPoint.moveto(new_x, new_y, new_z);
	JpyuBPoint.updatePlacemark();

	if (this.isDrawStraightLine) {
		var KmlCoordArray_jpyu = this.StraightLineStringKmlObj_lineString
				.getCoordinates();
		var KmlCoord_jpyu = KmlCoordArray_jpyu.get(index);
		var lat_jpyu = KmlCoord_jpyu.getLatitude();
		var lng_jpyu = KmlCoord_jpyu.getLongitude();
		var alt_jpyu = KmlCoord_jpyu.getAltitude();
		var KmlCoord_jpyu = KmlCoordArray_jpyu.setLatLngAlt(index, new_y,
				new_x, new_z);
	}
	//��X�]�t���I������TourPath��Id
	for(var i=0; i<JpyuBPoint.belongToTourPath.length; i++){
		//��ø����TourPath
		var tourPath = JpyuBPoint.belongToTourPath[i];
		tourPath.updateBezierCurve();
		tourPath.updateBezierCurvePointsInKmlLineString();
	} 
};

BezierCurveMaker.prototype.toggle_hull = function() {
	var me = this;
	this.display_hull = !this.display_hull;
}; // toggle_hull

BezierCurveMaker.prototype.toggle_hint = function() {
	var me = this;
	this.display_hint = !this.display_hint;
}; // toggle_hint


BezierCurveMaker.prototype.getNextLocationByDistance = function(distanceId) {
	//distanceId���۲�1�I��_������
	var me = this;
	var destinationControlPoint = 0;
	//
	//�`�N:this.controlPoints[0].curveInfoObj ==> null
	//
	for ( var i = 1; i < this.controlPoints.length; i++) {
		//����XdistanceId�O������G��control points����
		if ((distanceId > this.controlPoints[i - 1].controlPointGlobalDistanceId)
				&& (distanceId <= this.controlPoints[i].controlPointGlobalDistanceId)) {
			destinationControlPoint = i; 
			//distanceId�O�����(i-1)�β�i��control point����
			//localDistanceId���ۥثe�ҦҼ{��CurveInfoObj���Ĥ@�I��_������
			var localDistanceId = distanceId
					- this.controlPoints[i - 1].controlPointGlobalDistanceId;
			var nextBpoint = this.controlPoints[i].curveInfoObj
					.getCurvePointByDistance(localDistanceId + 0.005);//nextBpoint�O���F�p��bearing
			var BPoint = this.controlPoints[i].curveInfoObj
					.getCurvePointByDistance(localDistanceId);  
			//�p��bearing
			BPoint.bearingTo(nextBpoint);
			BPoint.JpyuBPoint.OrientationInfoObj = this.controlPoints[i].interpolateOrientationByDistance(this.controlPoints[i-1], localDistanceId);

			this.currentControlPoint = BPoint;
			this.currentControlPointId = i - 1;
			return BPoint;
		}
	}

	if (distanceId == this.controlPoints[0].controlPointGlobalDistanceId) {
		return this.controlPoints[0];
	}

	if (distanceId > this.controlPoints[this.controlPoints.length - 1].controlPointGlobalDistanceId) {
        //doLog(this.currentControlPointId+" distanceId > this.controlPoints["+(this.controlPoints.length - 1)+"]");
		this.currentControlPointId = this.controlPoints.length - 1;
		return this.controlPoints[this.controlPoints.length - 1];
	}
};

BezierCurveMaker.prototype.useBearingForAllControlPoints = function(startBezierCurveId, endBezierCurveId) {
	var me = this;
	for(var i=startBezierCurveId; i<=endBezierCurveId; i++){
		for(var j=0; j<this.BezierCurveSet[i].controlPoints.length; j++){
			var BPoint = this.BezierCurveSet[i].controlPoints[j];
			BPoint.JpyuBPoint.OrientationInfoObj.heading = BPoint.bearing;
		}
	}

	for (var i=0; i<this.controlPoints.length; i++) {
		BPoint = this.controlPoints[i];
        var JpyuBPoint = BPoint.JpyuBPoint;
        //doLog("BPoint.bearing="+BPoint.bearing+"  JpyuBPoint.bearing="+JpyuBPoint.bearing);
		JpyuBPoint.OrientationInfoObj.heading =BPoint.bearing;
		JpyuBPoint.OrientationInfoObj.tilt =0.0;
		JpyuBPoint.OrientationInfoObj.roll =0.0;
		BPoint.saveSpecifyBPOrientation();
   }
}

BezierCurveMaker.prototype.updateSpecifiedOrientationAtControlPoints = function() {
	var me = this;
	var BPoint;
	for ( var i = 0; i < this.controlPoints.length - 1; i++) {
		BPoint = this.controlPoints[i].setSpecifyBPOrientation();
	}
}

BezierCurveMaker.prototype.updateBearingAtControlPoints = function() {
	var me = this;
	var BPoint;
	for ( var i = 0; i < this.controlPoints.length - 1; i++) {
		if(this.controlPoints[i + 1].curveInfoObj == null)continue;
		BPoint = this.controlPoints[i + 1].curveInfoObj.getCurvePointByT(0.01);
		this.controlPoints[i].bearingTo(BPoint);
		//�]�wOrientationInfoObj���w�]��
		this.controlPoints[i].getOrientationInfoObj().heading = this.controlPoints[i].bearing;
		this.controlPoints[i].getOrientationInfoObj().tilt = 0.0;
		this.controlPoints[i].getOrientationInfoObj().roll = 0.0;
	}
	if(this.controlPoints[this.controlPoints.length - 1].curveInfoObj == null) return;
	//�S�O�B�̳̫�@�IControl point��Bearing
	BPoint = this.controlPoints[this.controlPoints.length - 1].curveInfoObj
			.getCurvePointByT(0.99);
	var bearing = BPoint.bearingTo(this.controlPoints[this.controlPoints.length - 1]);
	this.controlPoints[this.controlPoints.length - 1].setBearing(bearing);

	this.controlPoints[this.controlPoints.length - 1].getOrientationInfoObj().heading = this.controlPoints[this.controlPoints.length - 1].bearing;
	this.controlPoints[this.controlPoints.length - 1].getOrientationInfoObj().tilt = 0.0;
	this.controlPoints[this.controlPoints.length - 1].getOrientationInfoObj().roll = 0.0;

	this.updateSpecifiedOrientationAtControlPoints();

};

BezierCurveMaker.prototype.save = function() {
	var me = this;
	var filenameWithPath = "c:/1.txt";//this.fileUrl;
	//filenameWithPath = filenameWithPath.replace(".xml",".txt");
	this.fso = new ActiveXObject("Scripting.FileSystemObject");
	//the third parameter decides whether file be written unicode or not. 
	//this.outfile = this.fso.CreateTextFile(filenameWithPath, true, true); 
	this.outfile = this.fso.CreateTextFile(filenameWithPath, true, true);
	//-------------------------
	this.outfile.WriteLine("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
	//this.outfile.WriteLine('<?xml version="1.0" encoding="UTF-16EL"?>');
	//this.outfile.WriteLine("<?xml version=\"1.0\" encoding=\"ANSI\"?>");

	//-------------------------
	this.outfile.Close();
	this.fso = null;
	this.isChanged = false;
};


BezierCurveMaker.prototype.getControlPointSpeed = function(ControlPointId) {
	var me = this;
	var deltaDistance = 0.02;//0.0005;
	if (this.controlPoints[ControlPointId].deltaDistance == -1) {
		// ���@�����e��즳�w�qdeltaS��control point�~�Ψ�deltaS�ӳ]�w
		for ( var i = ControlPointId; i >= 0; i--) {
			if (this.controlPoints[i].deltaDistance != -1) {
				deltaDistance = this.controlPoints[i].deltaDistance;
				break;
			}
			if (i == 0)
				deltaDistance = 0.02;//0.05;
		}
		return deltaDistance;
	}
}

BezierCurveMaker.prototype.prepareForOutputTrajectoryNew = function(TourPathPoints) {
	var me = this;

	this.durationTime = [];
	this.interpolation_Time = [];
	this.interpolation_BPoints = [];
	var totalTime = 0;  //�Ĥ@���ƾڤ��ѦҮɶ�
	var elapseTime = 0.0;  //�G�I���һݪ��ɶ�
	var durationTime = 0.0;
	var speed = 0.0005;
	//var TourPathPoints = this.controlPoints;
	for ( var Pid = 1; Pid < TourPathPoints.length; Pid++) {
		outputDelta = ""+TourPathPoints[Pid].outputDelta; //points����X�Ҧ�
		//�W����outputDelta���w�]�ȬO��class BezierPoint����outputDelta�ӨM�w
		//outputDelta = "1sec"  //"1km" //5
		//outputDelta = "1km"; //���y�h������
		var mode = 1;
		var delta = 2;
		if (outputDelta.indexOf("sec") >= 0) {
	        mode = 0;
	        outputDelta = outputDelta.replace("sec", "");
	        delta = parseFloat(outputDelta); //�Cdelta(sec)�L�X�@��
	    } else if (outputDelta.indexOf("km") >= 0) {
	        mode = 1;
	        outputDelta = outputDelta.replace("km", "");
	        delta = parseFloat(outputDelta); //�Cdelta(km)�L�X�@��
	    }else{
	        mode = 2;
	        delta = parseFloat(outputDelta); //����delta����
//	        //�p�GoutputDelta=5�A�h�L�k�ϥ�trim()
//	        outputDelta="5";
//	        if(outputDelta.trim().length != 0){
//		        delta = parseFloat(outputDelta); //����delta����
//	        }else{
//	        	delta = 2; 
//	        }
	        //elapseTime ����᭱�A�p��
	    }

		switch(mode){
		case 0: //�Cdelta��L�X�@�I
			//============================
			//distancex:�G�ӱ����I��������
			var distancex = TourPathPoints[Pid].controlPointGlobalDistanceId 
                            - TourPathPoints[Pid-1].controlPointGlobalDistanceId;
			speed = this.getControlPointSpeed(Pid); //���ʳt��
			var tx = distancex/speed; //��point_i��point_i+1�һݮɶ�
			var tx_ceil = Math.ceil(tx);
			if(delta > tx_ceil) delta = tx/3; //�Ydelta > tx_ceil�A�h�j������3����
			durationTime = delta;
	        elapseTime = delta;
//			doLog("Trajectory output Info: Pid="+Pid);
//			doLog("--- mode = 0 delta = "+delta+" sec elapseTime="+elapseTime+" sec");
//			doLog("--- speed="+speed+"  distancex= "+distancex+"  tx = "+tx);

			var t_ratio = 0;
			var isBreak = false;
			var k=0;
			if(Pid == 1){
				k = 0;
			}else{
				k= k+delta;
			}
			for(  ; ; k= k+delta){ //�Cdelta��L�X�@�I
				if(k >= tx){
					t_ratio = 1;
					isBreak = true;
					//doLog("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx k="+k+" totalTime="+totalTime);
				}else{
					t_ratio = k/tx;
				}
				//�L�X���I
				//doLog("Pid="+Pid);
				var BPoint = TourPathPoints[Pid].curveInfoObj.getCurvePointByT(t_ratio);
				
				//�O�����I�������ƾ�
				this.durationTithis.push(durationTime);
				this.interpolation_Tithis.push(totalTime);
				this.interpolation_BPoints.push(BPoint);
				totalTime = totalTime + elapseTime; //�U�@���ƾڤ��ѦҮɶ�
				if(isBreak) break;
			}
			//============================
			break;
		case 1: //�Cdelta(km)�L�X�@�I
			//============================
			//distancex:�G�ӱ����I��������
			var distancex = TourPathPoints[Pid].controlPointGlobalDistanceId 
                            - TourPathPoints[Pid-1].controlPointGlobalDistanceId;
			var distancex_ceil = Math.ceil(distancex);
			if(delta > distancex_ceil) delta = distancex/3; //�Ydelta > tx_ceil�A�h�j������3����

			speed = this.getControlPointSpeed(Pid); //���ʳt��
			durationTime = 1.5;//delta/speed; //�Ѧ��I�ܤU�@�I�һݮɶ�
			elapseTime = 1.5;//delta/speed; //�Ѧ��I�ܤU�@�I�һݮɶ�

//			doLog("Trajectory output Info: Pid="+Pid);
//			doLog("--- mode = 1 delta = "+delta+" sec elapseTime="+elapseTime+" sec");
//			doLog("--- speed="+speed+"  p1->p2_distancex= "+distancex);
//			doLog("--------------------------------");
			
			var t_ratio = 0;
			var isBreak = false;
			var k=0;
			if(Pid == 0){
				k = 0;
			}else{
				k= k+delta;
			}
			for(  ; ; k= k+delta){ //�Cdelta(km)�L�X�@�I
				if(k >= distancex){
					t_ratio = 1;
					isBreak = true;
					//doLog("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx k="+k+" totalTime="+totalTime);
				}else{
					t_ratio = k/distancex;
				}
				//�L�X���I
				//doLog("----k="+k+"  distancex="+distancex+" t_ratio="+t_ratio);
				var BPoint = TourPathPoints[Pid].curveInfoObj.getCurvePointByT(t_ratio);
				//�O�����I�������ƾ�
				this.durationTithis.push(durationTime);
				this.interpolation_Tithis.push(totalTime);
				this.interpolation_BPoints.push(BPoint);
				totalTime = totalTime + elapseTime; //�U�@���ƾڤ��ѦҮɶ�
				if(isBreak) break;
			}
			//============================
			break;

		default: //����delta����  
			//============================
			//distancex:�G�ӱ����I��������
			var distancex = TourPathPoints[Pid].controlPointGlobalDistanceId 
                            - TourPathPoints[Pid-1].controlPointGlobalDistanceId;
			var distancex_ceil = Math.ceil(distancex);

			var deltaS = distancex/delta; //����delta����  //�CdeltaS(km)�L�X�@���ƾ�
			if(deltaS > distancex_ceil) deltaS = tx/3; //�YdeltaS > distancex_ceil�A�h�j������3����

			speed = this.getControlPointSpeed(Pid); //���ʳt��
			durationTime = deltaS/speed; //�Ѧ��I�ܤU�@�I�һݮɶ�
			elapseTime = deltaS/speed; //�Ѧ��I�ܤU�@�I�һݮɶ�

//			doLog("Trajectory output Info: Pid="+Pid);
//			doLog("--- mode = 'default' delta = "+delta+" elapseTime="+elapseTime+" sec");
//			doLog("--- speed="+speed+"  distancex= "+distancex+" deltaS = "+deltaS);
			
			var t_ratio = 0;
			var isBreak = false;
			var k=0;
			if(Pid == 0){
				k = 0;
			}else{
				k= k+deltaS;
			}
			for(  ; ; k= k+deltaS){ //�Cdelta(km)�L�X�@�I
				if(k >= distancex){
					t_ratio = 1;
					isBreak = true;
					//doLog("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx k="+k+" totalTime="+totalTime);
				}else{
					t_ratio = k/distancex;
				}
				//�L�X���I
				var BPoint = TourPathPoints[Pid].curveInfoObj.getCurvePointByT(t_ratio);
				//�O�����I�������ƾ�
				this.durationTithis.push(durationTime);
				this.interpolation_Tithis.push(totalTime);
				this.interpolation_BPoints.push(BPoint);
				totalTime = totalTime + elapseTime; //�U�@���ƾڤ��ѦҮɶ�
				if(isBreak) break;
			}
			//============================
			break;

		}
		
	}
}

BezierCurveMaker.prototype.outputXmlTrajectory = function(filenameWithPath) {
	var me = this;

	var fso = new ActiveXObject("Scripting.FileSystemObject");
	//the third parameter decides whether file be written unicode or not. 
	//this.outfile = this.fso.CreateTextFile(filenameWithPath, true, true); 
	//doLog("filenameWithPath=" + filenameWithPath);
	//filenameWithPath = "C:/3.xml";
	try {
		var outfile = fso.CreateTextFile(filenameWithPath, true, true);
		//-------------------------
		 outfile.WriteLine('<?xml version="1.0" encoding="UTF-8"?>');
		 //outfile.WriteLine('<?xml version="1.0" encoding="UTF-16EL"?>');
		 outfile.WriteLine('<Msg>');
	     outfile.WriteLine('<Format>v4.1</Format>');
	     outfile.WriteLine('<Description>Created by jpyu</Description>');
	     outfile.WriteLine('<Description>LookAt: TI, lon, lat, alt, heading, tile, range</Description>');
	     outfile.WriteLine('<Description>ModelStatus: TI, lon, lat, alt, Rx, Ry, Rz, visibility</Description>');
	     outfile.WriteLine('<Description>Altitude: meter</Description>');
	     outfile.WriteLine('<altitudeMode>relativeToGround</altitudeMode>');
	     outfile.WriteLine('<AltitudeModification>0.0</AltitudeModification>');

		var BPoint = null;
		var OrientationInfoObj = null;
		for ( var i = 0; i < this.interpolation_Tithis.length-1; i++) {
   		 BPoint = this.interpolation_BPoints[i];
   		 OrientationInfoObj =  BPoint.getOrientationInfoObjSpecified();
   		 if(i == 0){
   			 var lookAt_HTR = [-43.488, 56.88, 13.34];
             outfile.Write('<LookAt>'+this.interpolation_Time[i]
                     +", "+BPoint.get_x()+", "+BPoint.get_y()+", "+BPoint.get_z()
               		 +", "+ lookAt_HTR[0]
               		 +", "+ lookAt_HTR[1]
               		 +", "+ lookAt_HTR[2]
               		 +", 1"
               		 );
             outfile.WriteLine('</LookAt>');
   		 }
            outfile.Write('<ModelStatus>'+this.interpolation_Time[i]+", "+BPoint.get_x()+", "+BPoint.get_y()+", "+BPoint.get_z()
           		 +", "+OrientationInfoObj.getHeading()
           		 +", "+OrientationInfoObj.getTilt()
           		 +", "+OrientationInfoObj.getRoll()
           		 +", 1"
           		 );
            outfile.WriteLine('</ModelStatus>');
			
		}
	     outfile.WriteLine('</Msg>');
		//-------------------------
		outfile.Close();
		fso = null;
	}
	catch (e) {
		fso = null;
		alert("Cannot create file:"+filenameWithPath);
	}
}

BezierCurveMaker.prototype.outputKmlTrajectory = function(filenameWithPath, tourTitle) {
	var me = this;

	var fso = new ActiveXObject("Scripting.FileSystemObject");
	//the third parameter decides whether file be written unicode or not. 
	//this.outfile = this.fso.CreateTextFile(filenameWithPath, true, true); 
	//doLog("filenameWithPath=" + filenameWithPath);
	//filenameWithPath = "C:/3.xml";
	var documentTitle ="Campus Tour " + tourTitle;

	if (typeof (tourTitle) == 'undefined') {
		tourTitle = "MCU";
	}
	
	try {
		var outfile = fso.CreateTextFile(filenameWithPath, true, true);
		//-------------------------
		//outfile.WriteLine('<?xml version="1.0" encoding="UTF-8"?>');
		//outfile.WriteLine('<?xml version="1.0" encoding="UTF-16EL"?>');
		outfile.WriteLine('<?xml version="1.0" ?>');
		outfile.WriteLine('<kml xmlns="http://www.opengis.net/kml/2.2" xmlns:gx="http://www.google.com/kml/ext/2.2" xmlns:kml="http://www.opengis.net/kml/2.2" xmlns:atom="http://www.w3.org/2005/Atom">');

		outfile.WriteLine('<Document>');
		outfile.WriteLine(' <name>'+documentTitle+'</name>');
		outfile.WriteLine(' <open>1</open>');
		outfile.WriteLine(' <!-- start of Tour -->');
		
		outfile.WriteLine("<gx:Tour>");
		outfile.WriteLine(' <name>'+tourTitle+'</name>');
		outfile.WriteLine(' <gx:Playlist>');
		outfile.WriteLine(' <!-- start of Playlist -->');
		
		var placemarkList = [];
		var pstr = null;
		
		var durationTime = 0.0;
		var BPoint = null;
		var OrientationInfoObj = null;
	   	var heading = 0.0;
	   	var tilt = 65;
	   	var range = 0.0;
	   	var bearing;
	   	var thetaX;
	   	  
		for ( var i = 0; i < this.durationTithis.length-1; i++) {
		 durationTime = this.durationTime[i];
   		 BPoint = this.interpolation_BPoints[i];
   		 OrientationInfoObj = BPoint.getOrientationInfoObjSpecified();

      	  heading = BPoint.getOrientationInfoObjSpecified().heading;
      	  tilt = 90-BPoint.getOrientationInfoObjSpecified().tilt;
      	  roll = BPoint.getOrientationInfoObjSpecified().roll;
      	  range = 0.0;  //���]��PilotView 
      	  bearing =BPoint.bearing; 
      	  thetaX = 2*bearing-heading-180;
      	  thetaX = this.fixAngle(thetaX);
      	  //=========================================================
      		outfile.WriteLine("   <gx:FlyTo id='"+i+"'>");
      		outfile.WriteLine("      <gx:duration>"+durationTime+"</gx:duration>");
      		outfile.WriteLine("      <gx:flyToMode>"+"smooth"+"</gx:flyToMode>");
      		//outfile.WriteLine("      <gx:flyToMode>"+"bounce"+"</gx:flyToMode>");
//      		outfile.WriteLine("      <LookAt>");
//      		outfile.WriteLine("        <altitudeMode>"+jpyuAltitudeMode.get(BPoint.AltitudeMode)+"</altitudeMode>");
//      		outfile.WriteLine("        <longitude>"+BPoint.get_x()+"</longitude>");
//      		outfile.WriteLine("        <latitude>"+BPoint.get_y()+"</latitude>");
//      		outfile.WriteLine("        <altitude>"+BPoint.get_z()+"</altitude>");
//      		outfile.WriteLine("        <heading>"+heading+"</heading>");
//      		outfile.WriteLine("        <tilt>"+tilt+"</tilt>");
//      		outfile.WriteLine("        <range>"+range+"</range>");
//      		outfile.WriteLine("      </LookAt>");
      		outfile.WriteLine("      <Camera>");
      		outfile.WriteLine("        <altitudeMode>"+jpyuAltitudeMode.get(BPoint.AltitudeMode)+"</altitudeMode>");
      		outfile.WriteLine("        <longitude>"+BPoint.get_x()+"</longitude>");
      		outfile.WriteLine("        <latitude>"+BPoint.get_y()+"</latitude>");
      		outfile.WriteLine("        <altitude>"+BPoint.get_z()+"</altitude>");
      		outfile.WriteLine("        <heading>"+heading+"</heading>");
      		outfile.WriteLine("        <!--<heading>"+thetaX+"</heading>-->");
      		outfile.WriteLine("        <tilt>"+tilt+"</tilt>");
      		outfile.WriteLine("        <roll>"+roll+"</roll>");
      		outfile.WriteLine("      </Camera>");
      		outfile.WriteLine("   </gx:FlyTo>");
      		if(i != this.durationTithis.length-2) outfile.WriteLine("   <!--separator-->");
      	  //=========================================================

			pstr = "     <Placemark id='"+i+"'>"+"\r\n";
			pstr +=       "       <name>"+i+"</name>"+"\r\n";
			pstr +=       "       <LookAt>"+"\r\n";
			pstr +=       "         <longitude>"+BPoint.get_x()+"</longitude>"+"\r\n";
			pstr +=       "         <latitude>"+BPoint.get_y()+"</latitude>"+"\r\n";
			pstr +=       "         <altitude>"+BPoint.get_z()+"</altitude>"+"\r\n";
			pstr +=       "         <heading>"+heading+"</heading>"+"\r\n";
			pstr +=       "         <tilt>"+tilt+"</tilt>"+"\r\n";
			pstr +=       "         <range>"+range+"</range>"+"\r\n";
			pstr +=       "         <altitudeMode>"+jpyuAltitudeMode.get(BPoint.AltitudeMode)+"</altitudeMode>"+"\r\n";
			pstr +=       "       </LookAt>"+"\r\n";

			pstr +=       "       <Style>"+"\r\n";
			pstr +=       "         <IconStyle>"+"\r\n";
			pstr +=       "           <Icon>"+"\r\n";
			pstr +=       "             <href>"
				            +"http://maps.google.com/mapfiles/kml/paddle/T.png"
			                +"</href>"+"\r\n";
			pstr +=       "           </Icon>"+"\r\n";
			pstr +=       "         </IconStyle>"+"\r\n";
			pstr +=       "         <LineStyle>"+"\r\n";
			pstr +=       "           <width>2</width>"+"\r\n";
			pstr +=       "         </LineStyle>"+"\r\n";
			pstr +=       "       </Style>"+"\r\n";

			pstr +=       "       <Point>"+"\r\n";
			pstr +=       "         <extrude>1</extrude>"+"\r\n";
			pstr +=       "         <altitudeMode>"+jpyuAltitudeMode.get(BPoint.AltitudeMode)+"</altitudeMode>"+"\r\n";
			pstr +=       "         <coordinates>"+"\r\n";
			pstr +=       "         "+ BPoint.get_x() +"," + BPoint.get_y() + "," + BPoint.get_z()+"\r\n";
			pstr +=       "         </coordinates>"+"\r\n";
			pstr +=       "       </Point>"+"\r\n";
			pstr +=       "     </Placemark>"+"\r\n";
			placemarkList.push(pstr);
		}
		outfile.WriteLine(' <!-- end of Playlist -->');
		outfile.WriteLine('  </gx:Playlist>');
		outfile.WriteLine("</gx:Tour>");

		
		outfile.WriteLine('<Folder id="resources">');
		outfile.WriteLine('   <name>resources</name>');
		outfile.WriteLine('   <Folder id="CameraLocationPlacemarks">');
		outfile.WriteLine('   <name>CameraLocationPlacemarks</name>');
		outfile.WriteLine(' <!-- start of CameraLocationPlacemarks -->');
		for(var i=0; i<placemarkList.length; i++){
			outfile.WriteLine(placemarkList[i]);
		}
		outfile.WriteLine(' <!-- End of CameraLocationPlacemarks -->');
		outfile.WriteLine('   </Folder>');

		outfile.WriteLine('<Folder id="overlays">');
		outfile.WriteLine('	<name>Overlays</name>');
		outfile.WriteLine(' <!-- start of Overlays -->');
		outfile.WriteLine(' <!-- End of Overlays -->');
		outfile.WriteLine('</Folder>');
		outfile.WriteLine('<Folder id="surfspots">');
		outfile.WriteLine('	<name>Surf Spots</name>');
		outfile.WriteLine(' <!-- start of Surf Spots -->');
		outfile.WriteLine(' <!-- End of Surf Spots -->');
		outfile.WriteLine('</Folder>');
		
		outfile.WriteLine('</Folder>');
		
		outfile.WriteLine('</Document>');
		outfile.WriteLine('</kml>');
		//-------------------------
		outfile.Close();
		fso = null;
	}
	catch (e) {
		fso = null;
		alert("Cannot create file:"+filenameWithPath);
	}
}

BezierCurveMaker.prototype.outputBezierCurvesForV4 = function(filenameWithPath, generalNodeXml
		, deltaHeight, startIndexId, startTimeId, startDistanceId) {
	var me = this;
	if (typeof (filenameWithPath) == 'undefined') {
		filenameWithPath = this.saveToFileUrl;
	}
	if (typeof (startIndexId) == 'undefined') {
		startIndexId = 0;
		startDistanceId = 0.0;//�ץ�distance_along_path���Ѧ��I
		startTimeId = 0.0;  //�ץ�TimeId���Ѧ��I
	}
	  //------------------------------------------------------------
	  
	this.fso = new ActiveXObject("Scripting.FileSystemObject");
	//the third parameter decides whether file be written unicode or not. 
	//this.outfile = this.fso.CreateTextFile(filenameWithPath, true, true); 
	doLog("filenameWithPath=" + filenameWithPath);
	//filenameWithPath = "C:/3.xml";
	try {
		// �ݭn���ժ��y�y
		this.outfile = this.fso.CreateTextFile(filenameWithPath, true, true);
		//-------------------------
		this.outfile.WriteLine("<Msg>");
		if (typeof (generalNodeXml) != 'undefined') this.outfile.WriteLine(generalNodeXml);
		this.outfile.WriteLine("<BezierCurve>");
		if (this.controlPoints.length > 3) {
			var numPoints = this.controlPoints.length - (this.controlPoints.length - 1)
					% 3;
			var BPoint;
			for ( var i = 0; i < numPoints; i++) {
				BPoint = this.controlPoints[i];
				this.outfile.Write(" <ControlPoint id='" + i + "'");
				this.outfile.Write(" indexId='"
						+ (startIndexId+this.controlPoints[i].controlPointGlobalIndexId) + "'");
				this.outfile.Write(" timeId='"
						+ (startTimeId+this.controlPoints[i].controlPointGlobalTimeId) + "'");
				this.outfile.Write(" startDistanceId='"
						+ startDistanceId + "'");
				if (this.controlPoints[i].deltaDistance != -1)
					this.outfile.Write(" deltaS='"
							+ this.controlPoints[i].deltaDistance + "'");
				this.outfile.Write(" >");

				if (typeof (deltaHeight) == 'undefined') {
					deltaHeight = 0.0;
				}
				this.outfile.Write(BPoint.get_x() + ", " + BPoint.get_y() + ", " + (BPoint.get_z()-deltaHeight));

				if(BPoint.JpyuBPoint.OrientationInfoObjSpecified != null){
					this.outfile.WriteLine();
					this.outfile.Write("   <Orientation>");
					this.outfile.Write(BPoint.JpyuBPoint.OrientationInfoObjSpecified.heading + ", " + BPoint.JpyuBPoint.OrientationInfoObjSpecified.tilt + ", " + BPoint.JpyuBPoint.OrientationInfoObjSpecified.roll);
					this.outfile.WriteLine("   </Orientation>");
				}
				this.outfile.WriteLine(" </ControlPoint>");
			}
		}
		this.outfile.WriteLine("</BezierCurve>");
		this.outfile.WriteLine("</Msg>");
		//-------------------------
		this.outfile.Close();
		this.fso = null;
		this.isChanged = false;
	}
	catch (e) {
		this.fso = null;
		alert("Cannot create file:"+filenameWithPath);
	}

};


BezierCurveMaker.prototype.outputBezierCurvesForV5 = function(filenameWithPath, generalNodeXml
		, deltaHeight, startIndexId, startTimeId, startDistanceId) {
	var me = this;
	if (typeof (filenameWithPath) == 'undefined') {
		filenameWithPath = this.saveToFileUrl;
	}
		//filenameWithPath = this.saveToFileUrl+".txt";

	if (typeof (startIndexId) == 'undefined') {
		startIndexId = 0;
		startDistanceId = 0.0;//�ץ�distance_along_path���Ѧ��I
		startTimeId = 0.0;  //�ץ�TimeId���Ѧ��I
	}
	  //------------------------------------------------------------
	  
	this.fso = new ActiveXObject("Scripting.FileSystemObject");
	//the third parameter decides whether file be written unicode or not. 
	//this.outfile = this.fso.CreateTextFile(filenameWithPath, true, true); 
	doLog("xxfilenameWithPath=" + filenameWithPath);
	//filenameWithPath = "C:/3.xml";
	try {
		// �ݭn���ժ��y�y
		this.outfile = this.fso.CreateTextFile(filenameWithPath, true, true);
		//-------------------------
		this.outfile.WriteLine("<Msg>");
		this.outfile.WriteLine("<Format>v5.1</Format>");
		if (typeof (generalNodeXml) != 'undefined') this.outfile.WriteLine(generalNodeXml);
		//====================================
		if(myTourCurveMaker.isScenePlacemarkUsed){
			var noOfScenes = this.JpyuBPoints_forScenes.length;
	        var JpyuBPoint = null;
			this.outfile.WriteLine("<scenes comment='just for reference'>");
			for ( var i = 0; i < noOfScenes; i++) {
					JpyuBPoint = this.JpyuBPoints_forScenes[i];
					var sceneId = JpyuBPoint.id;
					this.outfile.Write(" <scene id='" + sceneId + "'");
					this.outfile.Write(" >");
					this.outfile.Write(JpyuBPoint.title+", "+JpyuBPoint.get_x() + ", " + JpyuBPoint.get_y() + ", " + (JpyuBPoint.get_z()-deltaHeight));
					this.outfile.WriteLine(" </scene>");
			}
			this.outfile.WriteLine("</scenes>");
		}
		//====================================
		var noOfTourPaths = this.BezierCurveSet.length;
		for(var p=0; p<noOfTourPaths; p++){
			var myBezierCurve = this.BezierCurveSet[p];
			var numPoints = myBezierCurve.controlPoints.length;
			if(numPoints == 0) continue;
			if(numPoints == 1) continue;//�]���̫�@���|�u���@���I
			doLog("Tourpath id="+p+"  numPoints="+numPoints);
			this.outfile.WriteLine("<TourPath id='"+p+"'>");
			for ( var i = 0; i < numPoints; i++) {
				BPoint = myBezierCurve.controlPoints[i];
				var JpyuBPoint = BPoint.JpyuBPoint;
//				if(JpyuBPoint.id.contains("S")){
//					this.outfile.Write(" <point id='" + i + "' sceneId='"+ + JpyuBPoint.id + "'");
//				}else{
//					this.outfile.Write(" <point id='" + i + "'");
//				}
				this.outfile.Write(" <point id='" + BPoint.JpyuBPoint.id + "'");
//				this.outfile.Write(" indexId='"
//						+ (startIndexId+myBezierCurve.controlPoints[i].controlPointGlobalIndexId) + "'");
//				this.outfile.Write(" timeId='"
//						+ (startTimeId+myBezierCurve.controlPoints[i].controlPointGlobalTimeId) + "'");
//				this.outfile.Write(" startDistanceId='"
//						+ startDistanceId + "'");
				if (myBezierCurve.controlPoints[i].deltaDistance != -1)
					this.outfile.Write(" deltaS='"
							+ myBezierCurve.controlPoints[i].deltaDistance + "'");
				this.outfile.Write(" >");

				if (typeof (deltaHeight) == 'undefined') {
					deltaHeight = 0.0;
				}
				this.outfile.Write(BPoint.get_x() + ", " + BPoint.get_y() + ", " + (BPoint.get_z()-deltaHeight));
				

				JpyuBPoint = BPoint.JpyuBPoint;
				if(JpyuBPoint.OrientationInfoObjSpecified != null){
					if(JpyuBPoint.OrientationInfoObjSpecified.heading != 0
							|| JpyuBPoint.OrientationInfoObjSpecified.tilt != 0
							|| JpyuBPoint.OrientationInfoObjSpecified.roll != 0){
						this.outfile.WriteLine();
						this.outfile.Write("   <Orientation>");
						this.outfile.Write(JpyuBPoint.OrientationInfoObjSpecified.heading + ", " + JpyuBPoint.OrientationInfoObjSpecified.tilt + ", " + JpyuBPoint.OrientationInfoObjSpecified.roll);
						this.outfile.WriteLine("   </Orientation>");
					}
				}
				
				this.outfile.WriteLine(" </point>");
			}
			this.outfile.WriteLine("</TourPath>");
		}
		this.outfile.WriteLine("</Msg>");
		//-------------------------
		this.outfile.Close();
		this.fso = null;
		this.isChanged = false;
	}
	catch (e) {
		this.fso = null;
		alert("Cannot create file:"+filenameWithPath);
	}

};

BezierCurveMaker.prototype.outputScenesForTourGeneration = function(filenameWithPath, deltaHeight) {
	var me = this;
	if (typeof (filenameWithPath) == 'undefined') {
		filenameWithPath = this.saveScenesToFileUrl;
	}
	  //------------------------------------------------------------
	  
	this.fso = new ActiveXObject("Scripting.FileSystemObject");
	//the third parameter decides whether file be written unicode or not. 
	//this.outfile = this.fso.CreateTextFile(filenameWithPath, true, true); 
	doLog("filenameWithPath=" + filenameWithPath);
	//filenameWithPath = "C:/3.xml";
	try {
		this.outfile = this.fso.CreateTextFile(filenameWithPath, true, true);
		//-------------------------
		this.outfile.WriteLine('<?xml version="1.0" encoding="UTF-8" ?>');
		//this.outfile.WriteLine('<?xml version="1.0" encoding="UTF-16EL" ?>');
		//====================================
		var noOfScenes = this.JpyuBPoints_forScenes.length;
        var JpyuBPoint = null;
		this.outfile.WriteLine("<scenes>");
		for ( var i = 0; i < noOfScenes; i++) {

				JpyuBPoint = this.JpyuBPoints_forScenes[i];
				var sceneId = JpyuBPoint.id;
				this.outfile.Write(" <scene id='" + sceneId + "'");
				this.outfile.Write(" >");
				this.outfile.Write(JpyuBPoint.title+", "+JpyuBPoint.get_x() + ", " + JpyuBPoint.get_y() + ", " + (JpyuBPoint.get_z()-deltaHeight));
				this.outfile.WriteLine(" </scene>");
		}
		this.outfile.WriteLine("</scenes>");
	
		//====================================
		//-------------------------
		this.outfile.Close();
		this.fso = null;
		this.isChanged = false;
	}
	catch (e) {
		this.fso = null;
		alert("Cannot create file:"+filenameWithPath);
	}

};

//Keep an angle in [-180,180]
BezierCurveMaker.prototype.fixAngle = function(a) {
	var me = this;
	while (a < -180) {
	 a += 360;
	}
	while (a > 180) {
	 a -= 360;
	}
	return a;
};

BezierCurveMaker.prototype.outputBezierCurvesForTest = function(startIndexId, startTimeId, startDistanceId) {
	var me = this;
	if (typeof (startIndexId) == 'undefined') {
		startIndexId = 0;
		startDistanceId = 0.0;
		startTimeId = 0.0;  //�ץ�TimeId���Ѧ��I
	}
	this.updateBearingAtControlPoints();
	this.updateGlobalProperties();
	var filenameWithPath = jpyuFileInfo.absolutePath_of_webPage + "/1.txt";//this.fileUrl;
	filenameWithPath = "C:/1a.xml";
	doLog("outputBezierCurvesForTest():filenameWithPath="+filenameWithPath);
	//filenameWithPath = filenameWithPath.replace(".xml",".txt");
	this.fso = new ActiveXObject("Scripting.FileSystemObject");
	//the third parameter decides whether file be written unicode or not. 
	//this.outfile = this.fso.CreateTextFile(filenameWithPath, true, true); 
	try {
		this.outfile = this.fso.CreateTextFile(filenameWithPath, true, true);
		//-------------------------
		for ( var i = 0; i < this.BezierCurveSet.length; i++) {
			this.outfile.WriteLine("---The "+i+"th Bezier curve(distributed points)-------");
			this.BezierCurveSet[i].outputBezierCurvePoints(this.outfile, startIndexId, startTimeId, startDistanceId);
		}
		//-------------------------
		for ( var i = 0; i < this.BezierCurveSet.length; i++) {
			this.outfile.WriteLine("---The "+i+"th Bezier curve(control points)-------");
			this.BezierCurveSet[i].outputBezierCurveControlPoints(this.outfile, startIndexId, startTimeId, startDistanceId);
		}
		//-------------------------
		this.outfile.WriteLine("Bezier curves information-----------------------------------------");

		this.outfile.WriteLine("id\t\t\tGlobalIndexId\t\t\tGlobalTimeId\t\t\tGlobalDistanceId");
		for ( var i = 0; i < this.controlPoints.length; i++) {
			this.outfile.Write(i);
			this.outfile.Write("\t\t\t");
			this.outfile.Write(startIndexId+this.controlPoints[i].controlPointGlobalIndexId);
			this.outfile.Write("\t\t\t\t");
			this.outfile.Write(startTimeId+this.controlPoints[i].controlPointGlobalTimeId);
			this.outfile.Write("\t\t\t");
			this.outfile.Write(startDistanceId+this.controlPoints[i].controlPointGlobalDistanceId);
			this.outfile.WriteLine(" ");

		}
		//-------------------------
//		for ( var i = 0; i < this.controlPoints.length; i++) {
//			this.outfile.WriteLine("this.controlPoints[i].bearing-----------------------------------------");
//			this.outfile.WriteLine(i + "  " + this.controlPoints[i].bearing);
//		}
		//-------------------------
		this.outfile.Close();
		this.fso = null;
		this.isChanged = false;
	}
	catch (e) {
		this.fso = null;
		alert("Cannot create file:"+filenameWithPath);
	}
};

}
