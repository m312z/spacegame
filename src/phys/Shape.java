package phys;

import java.util.ArrayList;

import utils.StringParser;

public class Shape {

	public static final Shape nullShape = new Shape(new Point2D[] {});
	
	protected Point2D[] points;
	protected Point2D[] normals;

	private float radius;

	public Shape(Point2D[] points) {
		this.points = points;
		setup();
	}

	private void setup() {
		
		normals = new Point2D[points.length];
		radius = 0f;
		
		if(points.length==0)
			return;
		
		// normal
		normals[0] = new Point2D();
		normals[0].x = points[points.length-1].y-points[0].y;
		normals[0].y = points[0].x-points[points.length-1].x;
		Point2D.normalise(normals[0]);
		// radius
		radius = Point2D.magnitude(points[0]);
		
		for(int i=0;i<points.length-1;i++) {
			// normal
			normals[i+1] = new Point2D();
			normals[i+1].x = points[i].y-points[i+1].y;
			normals[i+1].y = points[i+1].x-points[i].x;
			Point2D.normalise(normals[i+1]);
			// radius
			if(Point2D.magnitude(points[i+1])>radius)
				radius = Point2D.magnitude(points[i+1]);
		}
	}
	
	public static Shape scale(Shape shape, float scalar) {
		Point2D[] p = new Point2D[shape.getPoints().length];
		for(int i=0;i<p.length;i++) {
			p[i] = new Point2D(
					shape.getPoints()[i].x*scalar,
					shape.getPoints()[i].y*scalar);
		}
		return new Shape(p);
	}

	public Point2D[] getNormals() {
		return normals;
	}
	

	public void translate(Point2D v) {
		for(int i=0;i<points.length;i++) {
			points[i].x += v.x;
			points[i].y += v.y;
		}
	}
	
	public void rotate(float angle, Point2D p) {
		translate(p);
		rotate(angle);
		translate(Point2D.invert(p));
	}
	
	public void rotate(float angle) {
		for(int i=0;i<points.length;i++)
			points[i].rotate(angle);
	}
	
	public float radius() {
		return radius;
	}
	
	public Point2D[] getPoints() {
		return points;
	}

	public float getMinX() {
		if(points.length==0)
			return 0;
		float smallest = points[0].x;
		for(int i=1;i<points.length;i++)
			if(points[i].x<smallest)
				smallest = points[i].x;
		return smallest;
	}
	
	public float getMaxX() {
		if(points.length==0)
			return 0;
		float largest = points[0].x;
		for(int i=1;i<points.length;i++)
			if(points[i].x>largest)
				largest = points[i].x;
		return largest;
	}
	
	public float getMinY() {
		if(points.length==0)
			return 0;
		float smallest = points[0].y;
		for(int i=1;i<points.length;i++)
			if(points[i].y<smallest)
				smallest = points[i].y;
		return smallest;
	}
	
	public float getMaxY() {
		if(points.length==0)
			return 0;
		float largest = points[0].y;
		for(int i=1;i<points.length;i++)
			if(points[i].y>largest)
				largest = points[i].y;
		return largest;
	}
	
	private Projection project(Point2D axis) {

		// assumes that the axis is normalised!
		Projection p = new Projection();
		
		p.min = ((points[0].y*axis.y)+(points[0].x*axis.x));
		p.max = p.min;
		for (int i = 1; i < points.length; i++) {
			float d = ((points[i].y*axis.y)+(points[i].x*axis.x));
			if (d < p.min) {
				p.min = d;
			} else if (d > p.max) {
				p.max = d;
			}
		}

		return p;		
	}
	
	public Projection project(Point2D axis, Point2D offset) {

		Point2D v = new Point2D();
		
		// assumes that the axis is normalised!
		Projection p = new Projection();
		
		v.x = offset.x + points[0].x;
		v.y = offset.y + points[0].y;
		p.min = (v.y*axis.y + v.x*axis.x);
		p.max = p.min;
		
		for (int i = 1; i < points.length; i++) {
			
			v.x = offset.x + points[i].x;
			v.y = offset.y + points[i].y;
			
			float d = (v.y*axis.y + v.x*axis.x);
			
			if (d < p.min) {
				p.min = d;
			} else if (d > p.max) {
				p.max = d;
			}
		}

		return p;
	}

	public String write() {
		String line = "";
		for(Point2D p: points)
			line += "{"+p.write()+"}";
		return line;
	}
	
	public static Shape read(String line) {		
		ArrayList<Point2D> points = new ArrayList<Point2D>(4);
		while(line!="") {
			points.add(Point2D.read(StringParser.getBracket(line)));
			line = StringParser.removeBracket(line);
		}
		
		Point2D[] pointArray = new Point2D[points.size()];
		for(int i=0;i<points.size();i++)
			pointArray[i] = points.get(i);
		return new Shape(pointArray);
	}
	
	/*---------------------*/
	/* collision detection */
	/*---------------------*/

	public static Point2D shapeContainsPoint(Point2D pos, Shape shape, Point2D offset) {
		
		if(shape == nullShape)
			return Point2D.nullVector;
		
		Point2D[] axes1 = shape.getNormals();

		float overlap = Float.MAX_VALUE;
		Point2D bump = new Point2D();

		// loop over the axes1
		for (int i = 0; i < axes1.length; i++) {
			
			Point2D axis = axes1[i];
			
			// project both shape and point onto the axis
			float c = ((pos.y - offset.y)*axis.y) + ((pos.x - offset.x)*axis.x);
			Projection p = shape.project(axis);
			
			// does the shape projection contain the point?
			if (p.min > c || p.max < c) {
				return Point2D.nullVector;
			} else {
				// get the overlap
				float o = Math.min(Math.abs(p.min-c),Math.abs(p.max-c));
				// check for minimum
				if (Math.abs(o) < Math.abs(overlap)) {
					// then set this one as the smallest
					overlap = o;
					bump.x = axes1[i].x * overlap;
					bump.y = axes1[i].y * overlap;
				}
			}
		}
		
		// if we get here then we know that every axis had overlap on it
		// so we can guarantee an intersection
		return bump;
	}
	
	public static Point2D collide(Shape[] shapes1, Shape[] shapes2, Point2D offset) {
		Point2D mtv = Point2D.nullVector;
		for(int i=0;i<shapes1.length;i++) {
			for(int j=0;j<shapes2.length;j++) {
				Point2D m = collide(shapes1[i],shapes2[j],offset);
				if(Point2D.magnitude(m) > Point2D.magnitude(mtv))
					mtv = m;
			}
		}
		return mtv;
	}
	
	public static Point2D collide(Shape shape1, Shape shape2, Point2D offset) {

		if(shape1 == nullShape || shape2 == nullShape)
			return Point2D.nullVector;
		
		Point2D[] axes1 = shape1.getNormals();
		Point2D[] axes2 = shape2.getNormals();

		float overlap = Float.MAX_VALUE;
		Point2D bump = new Point2D();

		// loop over the axes1
		for (int i = 0; i < axes1.length; i++) {
			Point2D axis = axes1[i];
			// project both shapes onto the axis
			Projection p1 = shape1.project(axis);
			Projection p2 = shape2.project(axis,offset);
			// do the projections overlap?
			if (!p1.overlap(p2)) {
				return Point2D.nullVector;
			} else {
				// get the overlap
				float o = p1.getOverlap(p2);
				// check for minimum
				if (Math.abs(o) < Math.abs(overlap)) {
					// then set this one as the smallest
					overlap = o;
					bump.x = axes1[i].x * overlap;
					bump.y = axes1[i].y * overlap;
				}
			}
		}

		// loop over the axes2
		for (int i = 0; i < axes2.length; i++) {
			Point2D axis = axes2[i];
			// project both shapes onto the axis
			Projection p1 = shape1.project(axis);
			Projection p2 = shape2.project(axis,offset);
			// do the projections overlap?
			if (!p1.overlap(p2)) {
				return Point2D.nullVector;
			} else {
				// get the overlap
				float o = p1.getOverlap(p2);
				// check for minimum
				if (Math.abs(o) < Math.abs(overlap)) {
					// then set this one as the smallest
					overlap = o;
					bump.x = axes2[i].x * overlap;
					bump.y = axes2[i].y * overlap;
				}
			}
		}
		
		// if we get here then we know that every axis had overlap on it
		// so we can guarantee an intersection
		return bump;
	}

	class Projection
	{
		float min;
		float max;

		public boolean overlap(Projection p2) {
			if(min > p2.max) return false;
			if(max < p2.min) return false;
			return true;
		}
		
		@Override
		public String toString() {
			return "proj: ("+min+", "+max+")";
		}

		public float getOverlap(Projection p2) {
			float overlap = max-p2.min;
			if(Math.abs(overlap) > Math.abs(max-min))
				return min-p2.max;
			return overlap;
		}
	}
		
	public Shape clone() {
		Point2D[] p = new Point2D[points.length];
		for(int i=0;i<points.length;i++)
			p[i] = new Point2D(points[i].x,points[i].y);
		return new Shape(p);
	}
}

