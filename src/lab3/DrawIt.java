package lab3;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollBar;
import javax.swing.SwingUtilities;


/*
 * DrawIt - 
 * Eric McCreath 2009
 */

public class DrawIt  implements Runnable {

	static final Dimension dim = new Dimension(800,600);
	
	JFrame jf;
	DrawArea da;
	JMenuBar bar;
	JMenu jmfile;
	JMenuItem jmiquit, jmiexport;
	//transparency button
	JMenu transparency;
	JMenuItem t1, t2,t3;
	JMenu thickness;
	JMenuItem th1, th2;
	Button clean;
	JMenuItem s, ns;
	ToolBar colorToolbar;
	ToolBar toolbar;
	boolean ifspray=false;
	boolean ifsmudge=false;
	public DrawIt() {
		SwingUtilities.invokeLater(this);
	}
	
	public void run() {
		jf = new JFrame();
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		da = new DrawArea(dim,this);
		//da.setFocusable(true);
		jf.getContentPane().add(da,BorderLayout.CENTER);
		
		// create a toolbar
		colorToolbar = new ToolBar(BoxLayout.Y_AXIS);
		colorToolbar.addbutton("Red", Color.RED);
		colorToolbar.addbutton("Blue", Color.BLUE);
		colorToolbar.addbutton("Green", Color.GREEN);
		jf.getContentPane().add(colorToolbar,BorderLayout.LINE_END);
		//create style 
		toolbar = new ToolBar(BoxLayout.X_AXIS);
		toolbar.addbutton("Line", "Line");
		toolbar.addbutton("Spray", "Spray");
		toolbar.addbutton("Smudge", "Smudge");
		toolbar.addbutton("Flood", "Flood");
		toolbar.addbutton("Drag", "Drag");
		jf.getContentPane().add(toolbar,BorderLayout.AFTER_LAST_LINE);
		// create some menus
		bar = new JMenuBar();
		jmfile = new JMenu("File");
		jmiexport = new JMenuItem("Export");
		jmfile.add(jmiexport);
		jmiexport.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				da.export(new File("export.png"));
			}});
		
		jmiquit = new JMenuItem("Quit");
		jmfile.add(jmiquit);
		jmiquit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}});
		//set transparency
		transparency = new JMenu("Transparency");
		t3 = new JMenuItem("0.1");
		transparency.add(t3);
		t3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				da.setTransparency((float) 0.1);
			}});
		
		t1 = new JMenuItem("0.5");
		transparency.add(t1);
		t1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				da.setTransparency((float) 0.5);
			}});
		
		t2 = new JMenuItem("1");
		transparency.add(t2);
		t2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				da.setTransparency(1);
			}});
		bar.add(jmfile);
		bar.add(transparency);
		//set thickness
		thickness = new JMenu("Thickness");
		th1 = new JMenuItem("1");
		thickness.add(th1);
		th1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				da.setThickness(1);
			}});
		
		th2 = new JMenuItem("10");
		thickness.add(th2);
		th2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				da.setThickness(10);
			}});
		clean=new Button("Clean Screen");
		clean.setMaximumSize(new Dimension(200,50));
		clean.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				da.clearOffscreen();
			}});
		bar.add(jmfile);
		bar.add(transparency);
		bar.add(thickness);
		bar.add(clean);
		jf.setJMenuBar(bar);
		
		jf.pack();
		jf.setVisible(true);
	}
	
	
	public static void main(String[] args) {
		DrawIt sc = new DrawIt();
	}
}