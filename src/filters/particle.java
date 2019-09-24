package filters;

import vectors.Point2D;

public class particle {
	Point2D pos;
	Point2D vel;
	Point2D prevpos;
	public particle(Point2D in){
		pos=in;
		prevpos=new Point2D(pos.x,pos.y);
		vel = new Point2D(Math.random()-0.5,Math.random()-0.5);
	}public particle(double x,double y){
		pos= new Point2D(x,y);
		prevpos=new Point2D(pos.x,pos.y);
		vel = new Point2D(Math.random()-0.5,Math.random()-0.5);
	}
	public void update(){
		prevpos=new Point2D(pos.x,pos.y);
		pos = new Point2D(pos.x+vel.x,pos.y+vel.y);
		vel=new Point2D(vel.x*0.9,vel.y*0.9);
	}
}
