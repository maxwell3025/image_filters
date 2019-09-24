package perlin;

import filters.imagegen1;
import vectors.Point2D;

/**
 * this is the code for perlin noise
 */
public class Perlin2D {
	public static double CalcPerlin2D(Point2D[][] edges, double x, double y, int interpolationtype) {
		int xfloor = (int) x;
		int xceil = (int) x + 1;
		int yfloor = (int) y;
		int yceil = (int) y + 1;
		double xdif = x - xfloor;
		double ydif = y - yfloor;
		double cornerbl = Point2D.dotProduct(new Point2D(xdif, ydif), edges[xfloor][yfloor]);
		double cornerbr = Point2D.dotProduct(new Point2D(xdif - 1, ydif), edges[xceil][yfloor]);
		double cornerul = Point2D.dotProduct(new Point2D(xdif, ydif - 1), edges[xfloor][yceil]);
		double cornerur = Point2D.dotProduct(new Point2D(xdif - 1, ydif - 1), edges[xceil][yceil]);
		double out = interpolate(interpolate(cornerbl, cornerbr, xdif, interpolationtype),
				interpolate(cornerul, cornerur, xdif, interpolationtype), ydif, interpolationtype);
		return out;
	}

	public static double Perlin2Drepeating(Point2D[][] edges, double x, double y, int interpolationtype) {
		int xsize = edges[0].length;
		int ysize = edges.length;
		int xfloor = (int) x;
		int xceil = (int) x + 1;
		int yfloor = (int) y;
		int yceil = (int) y + 1;
		double xdif = x - xfloor;
		double ydif = y - yfloor;
		double cornerbl = Point2D.dotProduct(new Point2D(xdif, ydif), edges[rem(xfloor, ysize)][rem(yfloor, xsize)]);
		double cornerbr = Point2D.dotProduct(new Point2D(xdif - 1, ydif), edges[rem(xceil, ysize)][rem(yfloor, xsize)]);
		double cornerul = Point2D.dotProduct(new Point2D(xdif, ydif - 1), edges[rem(xfloor, ysize)][rem(yceil, xsize)]);
		double cornerur = Point2D.dotProduct(new Point2D(xdif - 1, ydif - 1),
				edges[rem(xceil, ysize)][rem(yceil, xsize)]);
		double out = interpolate(interpolate(cornerbl, cornerbr, xdif, interpolationtype),
				interpolate(cornerul, cornerur, xdif, interpolationtype), ydif, interpolationtype);
		return out;
	}

	public static int rem(int a, int b) {
		if (a < 0) {
			return a + b;
		}
		if (a >= b) {
			return a - b;
		}
		return a;
	}

	public static Point2D[][] randomArray(int width, int height, double amplitude) {
		Point2D[][] output = new Point2D[width][height];
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				double randomangle = Math.random() * Math.PI;
				Point2D temp = new Point2D(Math.sin(randomangle) * amplitude, Math.cos(randomangle) * amplitude);
				output[x][y] = temp;
			}
		}
		return output;
	}

	public static double lerp(double start, double end, double intfactor) {
		return start * (1 - intfactor) + end * intfactor;
	}

	public static double interpolate(double start, double end, double intfactor, int type) {
		return lerp(start, end, imagegen1.interpolationfunc(intfactor, type));
	}

}
