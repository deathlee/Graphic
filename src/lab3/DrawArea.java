package lab3;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.Time;
import java.time.Year;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.management.Query;
import javax.naming.InitialContext;
import javax.swing.JComponent;

/*
 * DrawArea - a simple JComponent for drawing.  The "offscreen" BufferedImage is 
 * used to draw to,  this image is then used to paint the component.
 * Eric McCreath 2009 2015
 */

public class DrawArea extends JComponent implements MouseMotionListener,
		MouseListener {

	private BufferedImage offscreen;
	Dimension dim;
	DrawIt drawit;
	Graphics2D g;
	int startX;
	int startY;
	int endX;
	int endY;
	float thickness;
	float alpha;
	public double[][] smudgeRed = new double[7][7];
	public double[][] smudgeBlue = new double[7][7];
	public double[][] smudgeGreen = new double[7][7];
	List<List<LineInformation>> lines;
	List<LineInformation> tmpLine;
	List<LineInformation> markedLine;
	public DrawArea(Dimension dim, DrawIt drawit) {
		this.setPreferredSize(dim);
		offscreen = new BufferedImage(dim.width, dim.height,
				BufferedImage.TYPE_INT_RGB);
		this.dim = dim;
		this.drawit = drawit;
		this.addMouseMotionListener(this);
		this.addMouseListener(this);
		this.thickness=1;
		this.alpha=1;
		this.lines=new ArrayList<>();
		this.tmpLine=new ArrayList<>();
		clearOffscreen();
	}

	public void clearOffscreen() {
		Graphics2D g = offscreen.createGraphics();
		g.setColor(Color.white);
		g.fillRect(0, 0, dim.width, dim.height);
		repaint();
	}

	public Graphics2D getOffscreenGraphics() {
		return offscreen.createGraphics();
	}

	public void drawOffscreen() {
		repaint();
	}

	protected void paintComponent(Graphics g) {
		g.drawImage(offscreen, 0, 0, null);
	}
	public void initialContext(){
		g = offscreen.createGraphics();
		Color tbColor = (Color) drawit.colorToolbar.getSelectCommand();
		Color alphaColor=new Color(tbColor.getRed(),tbColor.getGreen(),tbColor.getBlue(),(int)(alpha*255));
		g.setColor(alphaColor);
		g.setStroke(new BasicStroke(thickness));
	}
	public void mouseDragged(MouseEvent m) {
		initialContext();
		endX=m.getX();
		endY=m.getY();
		int intervalX=endX-startX;
		int intervalY=endY-startY;
		if(((String)drawit.toolbar.getSelectCommand()).equals("Spray")){
		drawSpray(g,endX,endY);
		}else if(((String)drawit.toolbar.getSelectCommand()).equals("Smudge")){
			swapSmudgeData(m.getX(), m.getY());
			// Make change visible on the screen.
		}else if(((String)drawit.toolbar.getSelectCommand()).equals("Line")){
		g.drawLine(startX , startY , endX, endY);
		tmpLine.add(new LineInformation(startX, startY, endX, endY));
		}else if(((String)drawit.toolbar.getSelectCommand()).equals("Drag")&&markedLine!=null){
			for (LineInformation node : markedLine) {
				g.drawLine(node.startX +intervalX, node.startY+intervalY , node.endX+intervalX, node.endY+intervalY);
			}
		}
		startX=endX;
		startY=endY;
		drawOffscreen();
	}
	private void floodFill(Color color,int touchedX, int touchedY) {
		int x=touchedX;
		int y=touchedY;
		if(color.getRGB()==offscreen.getRGB(x,y)){
			return;
		}
		boolean visited[][] = new boolean[dim.width][dim.height];
		Queue<PixelNode> queue = new LinkedList<>();
		queue.add(new PixelNode(x, y));
		while (!queue.isEmpty()) {
			PixelNode node = queue.poll();
			if(color.getRGB()==offscreen.getRGB(node.x,node.y)){
				continue;
			}
			if(!visited[node.x][node.y]){
				offscreen.setRGB(node.x, node.y, color.getRGB());
				if(node.x+1<dim.width&&!visited[node.x+1][node.y]){
					queue.add(new PixelNode(node.x+1, node.y));
				}
				if(node.x-1>=0&&!visited[node.x-1][node.y]){
					queue.add(new PixelNode(node.x-1, node.y));
				}
				if(node.y+1<dim.height&&!visited[node.x][node.y+1]){
					queue.add(new PixelNode(node.x, node.y+1));
				}
				if(node.y-1>=0&&!visited[node.x][node.y-1]){
					queue.add(new PixelNode(node.x, node.y-1));
				}
			}
			visited[node.x][node.y]=true;
			
		}
		drawOffscreen();
	}

	public void swapSmudgeData(int x, int y) {
        int w = offscreen.getWidth();  
        int h = offscreen.getHeight();  
        for (int i = 0; i < 7; i++) { // row number in the smudge data arrays
            int c = x + i - 3;  // column number (x-coord) of a pixel in the image.
            for (int j = 0; j < 7; j++) {  // column number in the smudge data arrays
                int r = y + j - 3;  // row number (y-coord) of a pixel in the image
                if ( ! (r < 0 || r >= h || c < 0 || c >= w || smudgeRed[i][j] == -1) ) {
                    int curCol = offscreen.getRGB(c,r);  // Current color of the pixel in the image.
                    int curRed = (curCol >> 16) & 0xFF;  // RGB components from image
                    int curGreen = (curCol >> 8) & 0xFF;
                    int curBlue = curCol & 0xFF;
                    int newRed = (int)(curRed*0.7 + smudgeRed[i][j]*0.3);  // New RGB's for image.
                    int newGreen = (int)(curGreen*0.7 + smudgeGreen[i][j]*0.3);
                    int newBlue = (int)(curBlue*0.7 + smudgeBlue[i][j]*0.3);
                    int newCol = newRed << 16 | newGreen << 8 | newBlue;
                    offscreen.setRGB(c,r,newCol); // Replace the color of the pixel in the image.
                    smudgeRed[i][j] = curRed*0.3 + smudgeRed[i][j]*0.7; // New RGBs for smudge arrays
                    smudgeGreen[i][j] = curGreen*0.3 + smudgeGreen[i][j]*0.7;
                    smudgeBlue[i][j] = curBlue*0.3 + smudgeBlue[i][j]*0.7;
                }
            }
        }
        repaint(x-3,y-3,7,7);
    }

	public void drawSpray(Graphics2D g,int touchedX,int touchedY){
		 int dotsToDrawAtATime = 100;
		 double brushRadius = 20.0; // This is however large they set the brush size, could be (1), could be whatever the max size of your brush is, e.g., (50), but set it based on what they choose
		    for (int i = 0; i < dotsToDrawAtATime; i++){

		        // Get the location to draw to
		        int x = (int)(touchedX + (new Random().nextGaussian())*brushRadius);
		        int y = (int)(touchedY + (new Random().nextGaussian())*brushRadius);
		        g.fill(new Ellipse2D.Double(x-1, y-1, 2, 2));
		        // Draw the point, using the random color, and the X/Y value

		    }
	}
	public void mouseMoved(MouseEvent m) {
	}

	public void mouseClicked(MouseEvent e) {
		int x=e.getX();
		int y=e.getY();
		initialContext();
		if(((String)drawit.toolbar.getSelectCommand()).equals("Spray")){
		drawSpray(g,x,y);
		}else if(((String)drawit.toolbar.getSelectCommand()).equals("Smudge")){
		swapSmudgeData(x, y);	
		}else if(((String)drawit.toolbar.getSelectCommand()).equals("Flood")){
			floodFill(((Color)drawit.colorToolbar.getSelectCommand()),x,y);
		}
		drawOffscreen();
		
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
		startX=e.getX();
		startY=e.getY();
		if (((String)drawit.toolbar.getSelectCommand()).equals("Drag")) {
			for (List<LineInformation> line : lines) {
				LineInformation node = line.get(line.size()-1);
				if((node.endX+5>startX||(node.endX-5)<startX)&&(node.endY+5>startY||node.endY-5<startY)){
					markedLine=line;
				}
			}
		}

	}

	public void mouseReleased(MouseEvent e) {
		int x=e.getX();
		int y=e.getY();
		if (((String)drawit.toolbar.getSelectCommand()).equals("Line")) {
			g.draw(new Rectangle(x-5, y-5, 10, 10));
			lines.add(new ArrayList<LineInformation>(tmpLine));
			tmpLine.clear();
			markedLine=null;
			drawOffscreen();
		}
	}

	public void export(File file) {
		try {
			ImageIO.write(offscreen, "png", file);
		} catch (IOException e) {
			System.out.println("problem saving file");
		}
	}
	public void setTransparency(float alpha){
		this.alpha=alpha;
		
	}
	public void setThickness(float thickness){
		this.thickness=thickness;
		
	}
}