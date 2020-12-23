package cellular;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

public class Box {

	boolean on = false;
	int x, y, width;
	Rectangle rect;
	
	public Box(int x, int y, int width) {
		this.x = x;
		this.y = y;
		this.width = width;
		
		rect = new Rectangle(x, y, width, width);
	}
	
	public void changeStatus() {
		on = !on;
	}
	
	public void render(Graphics2D g) {
		g.setColor(Color.black);
		if (on) {
			
			g.fill(rect);
		} else {
			
			g.draw(rect);
		}
	}
	
}
