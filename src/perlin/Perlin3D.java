package perlin;

import vectors.Point3D;

/**
 * 
 * @author Administrator
 *
 */
public class Perlin3D {
	/**
	 * This function calculates perlin noise for a given array, note that you should not have x, y, or z to be higher than their corrresponding array dimension lengths. 
	 * @exception 
	 * @param edges
	 
	 * @param x
	 * @param y
	 * @param z
	 * @param interpolationtype
	 * @return the perlin noise function for the given vector array at the given coordinates interpolating the corners with the interolationtype.
	 */
	public static double CalcPerlin3D(Point3D[][][] edges, double x, double y, double z,Function f) {
		int xsize = edges.length;
		int ysize = edges[0].length;
		int zsize = edges[0][0].length;
		int xfloor = (int) x;
		int xceil = (int) x + 1;
		int yfloor = (int) y;
		int yceil = (int) y + 1;
		int zfloor = (int) z;
		int zceil = (int) z + 1;
		double xdif = x - xfloor;
		double ydif = y - yfloor;
		double zdif = z - zfloor;
		double cornerbbl = Point3D.dotProduct(new Point3D(xdif    , ydif    ,zdif  ), edges[rem(xfloor,xsize)][rem(yfloor,ysize)][rem(zfloor,zsize)]);
		double cornerbbr = Point3D.dotProduct(new Point3D(xdif - 1, ydif    ,zdif  ), edges[rem(xceil ,xsize)][rem(yfloor,ysize)][rem(zfloor,zsize)]);
		double cornerbul = Point3D.dotProduct(new Point3D(xdif    , ydif - 1,zdif  ), edges[rem(xfloor,xsize)][rem(yceil ,ysize)][rem(zfloor,zsize)]);
		double cornerbur = Point3D.dotProduct(new Point3D(xdif - 1, ydif - 1,zdif  ), edges[rem(xceil ,xsize)][rem(yceil ,ysize)][rem(zfloor,zsize)]);
		double cornerfbl = Point3D.dotProduct(new Point3D(xdif    , ydif    ,zdif-1), edges[rem(xfloor,xsize)][rem(yfloor,ysize)][rem(zceil ,zsize)] );
		double cornerfbr = Point3D.dotProduct(new Point3D(xdif - 1, ydif    ,zdif-1), edges[rem(xceil ,xsize)][rem(yfloor,ysize)][rem(zceil ,zsize)] );
		double cornerful = Point3D.dotProduct(new Point3D(xdif    , ydif - 1,zdif-1), edges[rem(xfloor,xsize)][rem(yceil ,ysize)][rem(zceil ,zsize)] );
		double cornerfur = Point3D.dotProduct(new Point3D(xdif - 1, ydif - 1,zdif-1), edges[rem(xceil ,xsize)][rem(yceil ,ysize)][rem(zceil ,zsize)] );
		double out = interpolate(interpolate(interpolate(cornerbbl, cornerbbr, xdif, f),
											 interpolate(cornerbul, cornerbur, xdif, f), ydif, f),
								 interpolate(interpolate(cornerfbl, cornerfbr, xdif, f),
							 				 interpolate(cornerful, cornerfur, xdif, f), ydif, f),zdif,f);
		return out;
	}
	/*
	public static double Perlin3Drepeating(Point3D[][][] edges, double x, double y, double z, int interpolationtype) {
		int xsize = edges[0].length;
		int ysize = edges.length;
		int xfloor = (int) x;
		int xceil = (int) x + 1;
		int yfloor = (int) y;
		int yceil = (int) y + 1;
		double xdif = x - xfloor;
		double ydif = y - yfloor;
		double cornerbl = Point3D.dotProduct(new Point3D(xdif, ydif), edges[rem(xfloor, ysize)][rem(yfloor, xsize)]);
		double cornerbr = Point3D.dotProduct(new Point3D(xdif - 1, ydif), edges[rem(xceil, ysize)][rem(yfloor, xsize)]);
		double cornerul = Point3D.dotProduct(new Point3D(xdif, ydif - 1), edges[rem(xfloor, ysize)][rem(yceil, xsize)]);
		double cornerur = Point3D.dotProduct(new Point3D(xdif - 1, ydif - 1),
				edges[rem(xceil, ysize)][rem(yceil, xsize)]);
		double out = interpolate(interpolate(cornerbl, cornerbr, xdif, interpolationtype),
				interpolate(cornerul, cornerur, xdif, interpolationtype), ydif, interpolationtype);
		return out;
	}
	*/
	public static int rem(int a, int b) {
		if (a < 0) {
			return a + b;
		}
		if (a >= b) {
			return a - b;
		}
		return a;
	}

	public static Point3D[][][] randomArray(int width, int height, int depth, double amplitude) {
		Point3D[][][] output = new Point3D[width][height][depth];
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				for (int z = 0; z < depth; z++) {
					Point3D temp = new Point3D(Math.random() - 0.5, Math.random() - 0.5, Math.random() - 0.5);
					while(temp.dist()<0.5){
						temp = new Point3D(Math.random() - 0.5, Math.random() - 0.5, Math.random() - 0.5);
					}
					temp = temp.scale(1 / temp.dist());
					output[x][y][z] = temp;
				}
			}
		}
		return output;
	}

	public static double lerp(double start, double end, double intfactor) {
		return start * (1 - intfactor) + end * intfactor;
	}

	public static double interpolate(double start, double end, double intfactor, Function f) {
		return lerp(start, end, f.calculate(intfactor));
	}

}
