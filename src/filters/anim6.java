package filters;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;

import perlin.Perlin3D;

public class anim6 extends JFrame implements Runnable, KeyListener, MouseMotionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 9020477412984598531L;
	final static float blurriness = 1f;
	final static int depspeed = 1024;
	BufferedImage screen;
	BufferedImage comblayers;
	BufferedImage fscreen;
	Graphics2D graphics;
	Graphics2D copier;
	Graphics2D layerer;
	int screenwidth;
	int screenheight;
	int arraywidth;
	int arrayheight;
	double xgap;
	double ygap;
	int timemilis;
	int[][] map;

	public static void main(String[] args) {
		anim6 a = new anim6(1080, 720, 6, 4);
		new Thread(a).start();

	}

	public anim6(int width, int height, int arwidth, int arheight) {
		map = new int[width][height];
		setDefaultCloseOperation(3);
		setResizable(false);
		screenwidth = width;
		screenheight = height;
		arraywidth = arwidth;
		arrayheight = arheight;
		addKeyListener(this);
		addMouseMotionListener(this);
		xgap = (double) screenwidth / arraywidth;
		ygap = (double) screenheight / arrayheight;
		setSize(screenwidth, screenheight);
		setLocationRelativeTo(null);
		screen = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		fscreen = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		comblayers = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		graphics = screen.createGraphics();
		copier = fscreen.createGraphics();
		layerer = comblayers.createGraphics();
		setVisible(true);
	}

	public synchronized void paint(Graphics g) {
		g.drawImage(fscreen, 0, 0, screenwidth, screenheight, null);
	}

	public int getat(int x, int y) {
		return map[Perlin3D.rem(x, screenwidth)][Perlin3D.rem(y, screenheight)];
	}

	public void update() {
		int[][] buffer = new int[screenwidth][screenheight];
		for (int x = 0; x < map.length; x++) {
			for (int y = 0; y < map[x].length; y++) {
				buffer[x][y] = map[x][y];
			}
		}
		for (int x = 0; x < map.length; x++) {
			for (int y = 0; y < map[x].length; y++) {
				if (getat(x + 1, y) == 1&&buffer[x][y] == 0) {
					if (Math.random() < 0.25) {
					buffer[x][y] = 1;
					}
				}
				if (getat(x, y + 1) == 1&&buffer[x][y] == 0) {
					if (Math.random() < 0.25) {
					buffer[x][y] = 1;
					}
				}
				if (getat(x - 1, y) == 1&&buffer[x][y] == 0) {
					if (Math.random() < 0.25) {
					buffer[x][y] = 1;
					}
				}
				if (getat(x, y - 1) == 1&&buffer[x][y] == 0) {
					if (Math.random() < 0.25) {
					buffer[x][y] = 1;
					}
				}
				if (getat(x + 1, y) == 3&&buffer[x][y] <2) {
					if (Math.random() < 0.5) {
					buffer[x][y] = 3;
					}
				}
				if (getat(x, y + 1) == 2&&buffer[x][y] <2) {
					if (Math.random() < 0.5) {
					buffer[x][y] = 2;
					}
				}
				if (getat(x - 1, y) == 3&&buffer[x][y] <2) {
					if (Math.random() < 0.5) {
					buffer[x][y] = 3;
					}
				}
				if (getat(x, y - 1) == 2&&buffer[x][y] <2) {
					if (Math.random() < 0.5) {
					buffer[x][y] = 2;
					}
				}
				if(map[x][y]==1){
					if(Math.random()<0.000001){
						if(Math.random()<0.5){
							buffer[x][y]=2;
						}else{
							buffer[x][y]=3;
						}
					}
				}

				if(map[x][y]==3||map[x][y]==2){
					if(Math.random()<0.01){
						if(Math.random()<0.5){
							buffer[x][y]=2;
						}else{
							buffer[x][y]=3;
						}
					}
					if(Math.random()<0.01){
						buffer[x][y]=4;
					}
				}
			}
		}
		map = buffer;
		for (int x = 0; x < screenwidth; x++) {
			for (int y = 0; y < screenheight; y++) {
				if (map[x][y] == -1) {
					graphics.setColor(Color.gray);
				}
				if (map[x][y] == 0) {
					graphics.setColor(Color.white);
				}
				if (map[x][y] == 1) {
					graphics.setColor(Color.orange);
				}
				if (map[x][y] == 2) {
					graphics.setColor(Color.black);
				}
				if (map[x][y] == 3) {
					graphics.setColor(Color.red);
				}
				if (map[x][y] == 4) {
					graphics.setColor(Color.green );
				}
				graphics.drawRect(x, y, 0, 0);
			}
		}
		System.out.println("hi");
		layerer.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, blurriness));
		layerer.drawImage(screen, 0, 0, null);
		copier.clearRect(0, 0, screenwidth, screenheight);
		copier.drawImage(comblayers, 0, 0, null);
		copier.setColor(Color.green);
		copier.setFont(new Font("arial", Font.PLAIN, 20));
	}

	public void run() {
		while (true) {
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
			}
			timemilis++;

			update();
			repaint();
		}

	}

	public static int clip255(int in) {
		if (in < 0) {
			return 0;
		}
		if (in > 255) {
			return 255;
		} else {
			return in;
		}
	}

	public void keyTyped(KeyEvent e) {

	}

	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ENTER) {

		}
	}

	public void keyReleased(KeyEvent e) {

	}

	public void mouseDragged(MouseEvent e) {
		map[e.getX()][e.getY()] = 2;
	}

	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub

	}

}
