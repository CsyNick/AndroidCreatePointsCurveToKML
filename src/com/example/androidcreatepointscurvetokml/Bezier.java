package com.example.androidcreatepointscurvetokml;

import java.util.ArrayList;
import java.util.List;

import com.google.android.gms.maps.model.LatLng;

/*
 * Copyright (c) 2005 David Benson
 *  
 * See LICENSE file in distribution for licensing details of this source file
 */

/**
 * Interpolates given points by a bezier curve. The first and the last two
 * points are interpolated by a quadratic bezier curve; the other points by a
 * cubic bezier curve.
 * 
 * Let p a list of given points and b the calculated bezier points, then one get
 * the whole curve by:
 * 
 * sharedPath.moveTo(p[0]) sharedPath.quadTo(b[0].x, b[0].getY(), p[1].x,
 * p[1].getY());
 * 
 * for(int i = 2; i
 * < p
 * .length - 1; i++ ) { Point b0 = b[2*i-3]; Point b1 = b[2*i-2];
 * sharedPath.curveTo(b0.x, b0.getY(), b1.x, b1.getY(), p[i].x, p[i].getY()); }
 * 
 * sharedPath.quadTo(b[b.length-1].x, b[b.length-1].getY(), p[n - 1].x, p[n -
 * 1].getY());
 * 
 * @author krueger
 */
public class Bezier {

	private static final float AP = 0.5f;
	private LatLng[] bPoints;

	/**
	 * Creates a new Bezier curve.
	 * 
	 * @param points
	 */
	public Bezier(ArrayList<Scene> points) {
		int n = points.size();
		if (n < 3) {
			// Cannot create bezier with less than 3 points
			return;
		}
		bPoints = new LatLng[2 * (n - 2)];
		double paX, paY;
		double pbX = points.get(0).latLng.latitude;
		double pbY = points.get(0).latLng.longitude;
		double pcX = points.get(1).latLng.latitude;
		double pcY = points.get(1).latLng.longitude;
		for (int i = 0; i < n - 2; i++) {
			paX = pbX;
			paY = pbY;
			pbX = pcX;
			pbY = pcY;
			pcX = points.get(i + 2).latLng.latitude;
			pcY = points.get(i + 2).latLng.longitude;
			double abX = pbX - paX;
			double abY = pbY - paY;
			double acX = pcX - paX;
			double acY = pcY - paY;
			double lac = Math.sqrt(acX * acX + acY * acY);
			acX = acX / lac;
			acY = acY / lac;

			double proj = abX * acX + abY * acY;
			proj = proj < 0 ? -proj : proj;
			double apX = proj * acX;
			double apY = proj * acY;

			double p1X = pbX - AP * apX;
			double p1Y = pbY - AP * apY;
			bPoints[2 * i] = new LatLng((int) p1X, (int) p1Y);

			acX = -acX;
			acY = -acY;
			double cbX = pbX - pcX;
			double cbY = pbY - pcY;
			proj = cbX * acX + cbY * acY;
			proj = proj < 0 ? -proj : proj;
			apX = proj * acX;
			apY = proj * acY;

			double p2X = pbX - AP * apX;
			double p2Y = pbY - AP * apY;
			bPoints[2 * i + 1] = new LatLng((int) p2X, (int) p2Y);
		}
	}

	/**
	 * Returns the calculated bezier points.
	 * 
	 * @return the calculated bezier points
	 */
	public LatLng[] getPoints() {
		return bPoints;
	}

	/**
	 * Returns the number of bezier points.
	 * 
	 * @return number of bezier points
	 */
	public int getPointCount() {
		return bPoints.length;
	}

	/**
	 * Returns the bezier points at position i.
	 * 
	 * @param i
	 * @return the bezier point at position i
	 */
	public LatLng getPoint(int i) {
		return bPoints[i];
	}



/**

 * Generates several 3D points in a continuous Bezier curve based upon 

 * the parameter list of points. 

 * @param controls

 * @param detail

 * @return

 */

public static Tuple3d[] getCurvePoints(Tuple3d[] controls, double detail){

	if ( detail > 1 || detail < 0 ){

		throw new IllegalStateException("");

	}



	List<Tuple3d> renderingPoints = new ArrayList<Tuple3d>();

	List<Tuple3d> controlPoints = new ArrayList<Tuple3d>();

	//generate the end and control points

	for ( int i = 1; i < controls.length - 1; i+=2 ){

		controlPoints.add(center(controls[i-1], controls[i]));

		controlPoints.add(controls[i]);

		controlPoints.add(controls[i+1]);

		if ( i+2 < controls.length - 1 ){

			controlPoints.add(center(controls[i+1], controls[i+2]));

		}

	}

	//Generate the detailed points. 

	Tuple3d a0, a1, a2, a3;

	for ( int i = 0; i < controlPoints.size() - 2; i+=4 ){

		a0 = controlPoints.get(i);

		a1 = controlPoints.get(i+1);

		a2 = controlPoints.get(i+2);

		if ( i + 3 > controlPoints.size() - 1 ){

			//quad

			for ( double j = 0; j < 1; j += detail){

				renderingPoints.add(quadBezier(a0,a1,a2,j));

			}

		}else{

			//cubic

			a3 = controlPoints.get(i+3);

			for ( double j = 0; j < 1; j += detail){

				renderingPoints.add(cubicBezier(a0,a1,a2,a3,j));

			}

		}

	}



	Tuple3d[] points = new Tuple3d[renderingPoints.size()];

	renderingPoints.toArray(points);

	return points;

}



/**

 * A cubic bezier method to calculate the point at t along the Bezier Curve give

 * the parameter points.

 * @param p1

 * @param p2

 * @param p3

 * @param p4

 * @param t A value between 0 and 1, inclusive. 

 * @return

 */

public static Tuple3d cubicBezier(Tuple3d p1, Tuple3d p2, Tuple3d p3, Tuple3d p4, double t){

	return new Tuple3d(

			cubicBezierPoint(p1.x, p2.x, p3.x, p4.x, t), 

			cubicBezierPoint(p1.y, p2.y, p3.y, p4.y, t), 

			cubicBezierPoint(p1.z, p2.z, p3.z, p4.z, t));

}



/**

 * A quadratic Bezier method to calculate the point at t along the Bezier Curve give

 * the parameter points.

 * @param p1

 * @param p2

 * @param p3

 * @param t A value between 0 and 1, inclusive. 

 * @return

 */

public static Tuple3d quadBezier(Tuple3d p1, Tuple3d p2, Tuple3d p3, double t){

	return new Tuple3d(

			quadBezierPoint(p1.x, p2.x, p3.x, t), 

			quadBezierPoint(p1.y, p2.y, p3.y, t), 

			quadBezierPoint(p1.z, p2.z, p3.z, t));

}

/**

 * The cubic Bezier equation. 

 * @param a0

 * @param a1

 * @param a2

 * @param a3

 * @param t

 * @return

 */

private static double cubicBezierPoint(double a0, double a1, double a2, double a3, double t){

	return Math.pow(1-t, 3) * a0 + 3* Math.pow(1-t, 2) * t * a1 + 3*(1-t) * Math.pow(t, 2) * a2 + Math.pow(t, 3) * a3;

}



/**

 * The quadratic Bezier equation,

 * @param a0

 * @param a1

 * @param a2

 * @param t

 * @return

 */

private static double quadBezierPoint(double a0, double a1, double a2, double t){

	return Math.pow(1-t, 2) * a0 + 2* (1-t) * t * a1 + Math.pow(t, 2) * a2;

}



/**

 * Calculates the center point between the two parameter points.

 * @param p1

 * @param p2

 * @return

 */

public static Tuple3d center(Tuple3d p1, Tuple3d p2){

	return new Tuple3d(

			(p1.x + p2.x) / 2, 

			(p1.y + p2.y) / 2,

			(p1.z + p2.z) / 2

			);

}



}
