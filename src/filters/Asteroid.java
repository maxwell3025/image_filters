package filters;

import vectors.Point2D;

public class Asteroid {
	Point2D pos;
	Point2D vel;
	Point2D prevpos;
	double rad;
	double area;

	public Asteroid(Point2D in, double ar) {
		area = ar;
		pos = in;
		prevpos = new Point2D(pos.x, pos.y);
		vel = new Point2D(0,0);
	}

	public Asteroid(Point2D in, Point2D veloc, double ar) {
		area = ar;
		pos = in;
		prevpos = new Point2D(pos.x, pos.y);
		vel = veloc;
		}

	public Asteroid(double x, double y, double ar) {
		area = ar;
		pos = new Point2D(x, y);
		prevpos = new Point2D(pos.x, pos.y);
		updaterad();
		vel = new Point2D(0,0);
	}

	public void update(double speed) {
		prevpos = new Point2D(pos.x, pos.y);
		pos = Point2D.add(pos, vel.scale(speed / area));
		vel = new Point2D(vel.x, vel.y);
		updaterad();
	}

	public void updaterad() {
		rad = Math.sqrt(area / Math.PI);
	}
}
