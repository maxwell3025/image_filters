package filters;

import vectors.Point2D;

public class ball {
	Point2D pos;
	Point2D vel;
	Point2D prevpos;
	double rad;
	public ball(Point2D in, double radius){
		rad = radius;
		pos=in;
		prevpos=new Point2D(pos.x,pos.y);
		vel = new Point2D(Math.random()-0.5,Math.random()-0.5);
	}public ball(double x,double y, double radius){
		rad = radius;
		pos= new Point2D(x,y);
		prevpos=new Point2D(pos.x,pos.y);
		vel = new Point2D(Math.random()-0.5,Math.random()-0.5);
	}
	public void update(){
		prevpos=new Point2D(pos.x,pos.y);
		pos = new Point2D(pos.x+vel.x,pos.y+vel.y);
		vel=new Point2D(vel.x*0.99,vel.y*0.99);
	}
}
