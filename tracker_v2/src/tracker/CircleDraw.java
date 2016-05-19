package tracker;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

public class CircleDraw extends Frame {
	
	Graphics2D ga;
	Graphics g;

	public void paint(Graphics g) {
		this.g = g;
		drawCircle(10, 10);
	}
	
	public void drawCircle(float x, float y) {
		Shape circle = new Ellipse2D.Float(10.0f, 10.0f, 100.0f, 100.0f);
		ga = (Graphics2D) g;
		ga.draw(circle);
		ga.setPaint(Color.green);
		
	}
	
	public CircleDraw() {
		super();
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent we) {
				System.exit(0);
			}
		});
		this.setSize(300, 250);
		this.setVisible(true);
	}

	public static void main(String args[]) {
		new CircleDraw();
	}
}