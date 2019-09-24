package graphicsutil;

import vectors.Triangle3D;

public class Surface {
	TriangularTexture tex;
	Triangle3D area;
	public Surface(TriangularTexture texture, Triangle3D area){
		this.area = area;
		tex = texture;
	}
}
