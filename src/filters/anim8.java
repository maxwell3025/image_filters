package filters;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;

import perlin.Perlin2D;
import vectors.Point2D;

public class anim8 extends JPanel implements Runnable, KeyListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 9020477412984598531L;
	final static float blurriness = 0.5f;
	Point2D ball;
	Point2D vel = new Point2D(Math.signum(Math.random() - 0.5) * 2, 4 * (Math.random() - 0.5));
	JFrame frame =new JFrame();
	int leftpaddle;
	int rightpaddle;
	int leftscore = 0;
	int rightscore = 0;
	BufferedImage screen;
	BufferedImage comblayers;
	BufferedImage fscreen;
	Graphics2D graphics;
	Graphics2D copier;
	Graphics2D layerer;
	Point2D[][] xperlinframe;
	Point2D[][] yperlinframe;
	int screenwidth;
	int screenheight;
	int arraywidth;
	int arrayheight;
	int particlecount;
	int fps = 0;
	int threadnum = 0;
	double xgap;
	double ygap;
	boolean[] ispressed = new boolean[500];
	int[] wait = new int[1000];
	int timemilis;

	public static void main(String[] args) {
		anim8 a = new anim8(1080, 720, 6, 4);
		new Thread(a).start();
		new Thread(a).start();

	}

	public anim8(int width, int height, int arwidth, int arheight) {

		frame.setDefaultCloseOperation(3);
		frame.setResizable(false);
		screenwidth = width;
		screenheight = height;
		arraywidth = arwidth;
		arrayheight = arheight;
		frame.addKeyListener(this);
		addKeyListener(this);
		xgap = (double) screenwidth / arraywidth;
		ygap = (double) screenheight / arrayheight;
		screen = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		fscreen = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		comblayers = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		graphics = screen.createGraphics();
		copier = fscreen.createGraphics();
		layerer = comblayers.createGraphics();
		ball = new Point2D(screenwidth / 2, screenheight / 2);
		this.setPreferredSize(new Dimension(screenwidth,screenheight));
		frame.add(this);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

	public synchronized void paint(Graphics g) {
		g.drawImage(fscreen, 0, 0, screenwidth, screenheight, null);
	}

	public void update() {
		graphics.setColor(Color.black);
		graphics.fillRect(0, 0, screenwidth, screenheight);
		if (ispressed[KeyEvent.VK_W]) {
			leftpaddle -= 5;
		}
		if (ispressed[KeyEvent.VK_S]) {
			leftpaddle += 5;
		}
		if (ispressed[KeyEvent.VK_UP]) {
			rightpaddle -= 5;
		}
		if (ispressed[KeyEvent.VK_DOWN]) {
			rightpaddle += 5;
		}
		if (ball.y < screenheight - 3 == ball.y < 0) {
			vel.y = -vel.y;
		}
		if (ball.x < 7) {
			if (Math.abs(ball.y - leftpaddle) < 50) {
				vel.x = -vel.x * 1.1;
				vel.y = (Math.random() - 0.5) * vel.x;
			} else {
				endgame(false);
			}
		}
		if (ball.x > screenwidth - 7) {
			if (Math.abs(ball.y - rightpaddle) < 50) {
				vel.x = -vel.x * 1.1;
				vel.y = (Math.random() - 0.5) * vel.x;
			} else {
				endgame(true);
			}
		}
		ball = Point2D.add(ball, vel);
		graphics.setColor(Color.white);
		graphics.drawRect(5, leftpaddle - 50, 2, 100);
		graphics.drawRect(screenwidth - 8, rightpaddle - 50, 2, 100);
		graphics.fillOval((int) ball.x - 5, (int) ball.y - 5, 10, 10);
		layerer.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, blurriness));
		layerer.drawImage(screen, 0, 0, null);

		copier.clearRect(0, 0, screenwidth, screenheight);
		copier.drawImage(comblayers, 0, 0, null);
		copier.setColor(Color.green);
		copier.setFont(new Font("arial", Font.PLAIN, 20));
		copier.drawString("fps:" + String.valueOf(fps), 3, 20);
		copier.setFont(new Font("arial", Font.PLAIN, 50));
		copier.drawString(String.valueOf(leftscore), 3, 70);
		FontMetrics fm = copier.getFontMetrics();
		copier.drawString(String.valueOf(rightscore), (screenwidth - 3) - fm.stringWidth(String.valueOf(rightscore)),
				70);
	}

	public void endgame(boolean player) {
		vel = new Point2D(Math.signum(Math.random() - 0.5) * 2, 4 * (Math.random() - 0.5));
		ball = new Point2D(screenwidth / 2, screenheight / 2);
		if (player) {
			leftscore++;

		} else {
			rightscore++;
		}
	}

	public void run() {
		threadnum++;
		if (threadnum == 1) {
			while (true) {
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
				}

				update();
				repaint();
				timemilis++;
				for (int i = 0; i < 1000; i++) {
					wait[i]++;
				}

			}
		}
		if (threadnum == 2) {
			while (true) {
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
				}
				fps = wait[1];
				for (int i = 1; i < 1000; i++) {
					wait[i - 1] = wait[i];
				}
				wait[999] = 0;
			}

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
		ispressed[e.getKeyCode()] = true;
	}

	public void keyReleased(KeyEvent e) {
		ispressed[e.getKeyCode()] = false;

	}

	public static double rem(double a, double b) {
		double downscaled = a / b;
		double out = (downscaled - Math.floor(downscaled)) * b;
		if (out < 0) {
			return 0;
		}
		if (out > b) {
			return b;
		}
		return out;
	}

}
