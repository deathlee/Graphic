package lab2;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.QuadCurve2D;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.Timer;

public class LittleWall implements ActionListener {

	/**
	 * Little Wall Rock Climbing Copyright 2009 Eric McCreath GNU LGPL
	 */

	final static Dimension dim = new Dimension(800, 600);
	final static XYPoint wallsize = new XYPoint(8.0, 6.0);

	JFrame jframe;
	GameComponent canvas;
	Wall wall;
	PlayerSpring player;
	Timer timer;

	public LittleWall() {
		jframe = new JFrame("Little Wall");
		jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		canvas = new GameComponent(dim);
		jframe.getContentPane().add(canvas);
		jframe.pack();
		jframe.setVisible(true);
	}

	public static void main(String[] args) throws InterruptedException {
		LittleWall lw = new LittleWall();
		lw.drawTitleScreen();
		lw.startRunningGame();
	}

	private void startRunningGame() {
		wall = new Wall(dim, wallsize);
		wall.draw(canvas.getBackgroundGraphics());
		player = new PlayerSpring(wall);
		canvas.addMouseMotionListener(player);
		canvas.addKeyListener(player);

		timer = new Timer(1000 / 15, this);
		timer.start();
		
	}

	private void drawTitleScreen() throws InterruptedException {
		Graphics2D bg = canvas.getBackgroundGraphics();
		bg.setColor(Color.white);
		bg.fillRect(0, 0, dim.width, dim.height);
		canvas.clearOffscreen();

		Graphics2D os = canvas.getOffscreenGraphics();
		os.setColor(Color.black);
		os.setFont(new Font("TimesRoman", Font.ITALIC, 36)); 
		os.drawString("Little Wall Climbing", 100, 100);
		os.setFont(new Font("TimesRoman", Font.BOLD, 12)); 
		os.drawString("Instruction:", 100, 150);
		os.drawString("A : move up", 100, 200);
		os.drawString("B : move down", 100, 250);
		os.drawString("C : move left", 100, 300);
		os.drawString("D : move right", 100, 350);
		Graphics2D g2 = canvas.getOffscreenGraphics();
		g2.setColor(Color.black);
		AffineTransform at = new AffineTransform();
	    at.rotate(Math.PI / 6);
	    at.translate(100, 0);
	    
	    g2.setTransform(at);
		
		g2.draw(new Ellipse2D.Double(300, 200,
                100,
                100));
		
		g2.draw(new Line2D.Double(320, 230, 340, 230));
		g2.draw(new Line2D.Double(360, 230, 380, 230));
		QuadCurve2D q = new QuadCurve2D.Float();
		// draw QuadCurve2D.Float with set coordinates
		q.setCurve(325, 275, 350, 290, 375, 275);
		g2.draw(q);
		
		//g2.setTransform(saveXform);
		
		// add your code here to make the title screen more interesting.
		canvas.drawOffscreen();
		Thread.sleep(1000);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == timer) {
			canvas.clearOffscreen();
			Graphics2D of = canvas.getOffscreenGraphics();
			//try {
				player.draw(of);
	//		} catch (IOException e1) {
				// TODO Auto-generated catch block
		//		e1.printStackTrace();
			//}
			player.update(canvas, wall);
			canvas.drawOffscreen();
		}
	}
}
