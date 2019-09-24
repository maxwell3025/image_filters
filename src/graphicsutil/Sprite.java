package graphicsutil;

import java.awt.image.BufferedImage;

import vectors.Point3D;

public class Sprite {
	public BufferedImage tex;
	public Point3D pos;
	public double size;

	public Sprite(Point3D position, BufferedImage texture, double size) {
		tex = texture;
		pos = position;
		this.size = size;
	}
}
