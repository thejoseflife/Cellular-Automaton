package cellular;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferStrategy;

import javax.swing.JFrame;

public class CellularAutomaton extends Canvas implements Runnable, MouseListener, MouseMotionListener {

	private static final long serialVersionUID = 1L;

	// Width and height of window
	private static final int WIDTH = 1000, HEIGHT = 800;
	
	// Do not touch
	private Thread thread;
	private boolean running = false;
	
	private int timer = 0;
	
	// Adjust size of grid
	private int gridSize = 35; // 35 boxes width and height
	private Box[][] grid = new Box[gridSize][gridSize];
	private int boxWidth = 20; // Pixels
	
	// Red, increments by one manually
	private Rectangle incrementButton = new Rectangle(gridSize * boxWidth + 10, 10, 50, 50);
	
	// Black, increments based on time, 1 increment every few seconds
	private Rectangle timeButton = new Rectangle(gridSize * boxWidth + 10, 70, 50, 50);
	
	// Blue, clears the cells
	private Rectangle clearButton = new Rectangle(gridSize * boxWidth + 10, 130, 50, 50);
	
	private void init() {
		
		// To enable clicking
		addMouseListener(this);
		addMouseMotionListener(this);
		
		for (int i = 0; i < gridSize; i++) {
			for (int j = 0; j < gridSize; j++) {
				grid[i][j] = new Box(i * boxWidth, j * boxWidth, boxWidth);
			}
		}
		
	}
	
	// Checks the rules
	private void incrementTime() {
		
		/* Rules:
					3 around turns a cell on
					to survive, 2 or 3 cells around must be on
		 */
		
		Box[][] copyGrid = new Box[gridSize][gridSize];
		
		for (int i = 0; i < gridSize; i++) {
			for (int j = 0; j < gridSize; j++) {
				copyGrid[i][j] = new Box(i * boxWidth, j * boxWidth, boxWidth);
				
				int num = numberOnCellsAround(i, j);
				
				if (grid[i][j].on && (num > 3 || num < 2)) {
					copyGrid[i][j].on = false;
				} else if (!grid[i][j].on && num == 3) {
					copyGrid[i][j].on = true;
				} else if (grid[i][j].on && (num == 2 || num == 3)) {
					copyGrid[i][j].on = true;
				} else {
					copyGrid[i][j].on = false;
				}

			}
		}
		
		grid = copyGrid;
		
	}
	
	// Checks the number of cells around the box that are on
	private int numberOnCellsAround(int x, int y) {
		int count = 0;
		
		// Top left
		if (x - 1 >= 0 && y - 1 >= 0) {
			if (grid[x - 1][y - 1].on) {
				count++;
			}
		}
		
		// Top middle
		if (y - 1 >= 0) {
			if (grid[x][y - 1].on) {
				count++;
			}
		}
		
		// Top right
		if (x + 1 < gridSize && y - 1 >= 0) {
			if (grid[x + 1][y - 1].on) {
				count++;
			}
		}
		
		// Left middle
		if (x - 1 >= 0) {
			if (grid[x - 1][y].on) {
				count++;
			}
		}
		
		
		// Right middle
		if (x + 1 < gridSize) {
			if (grid[x + 1][y].on) {
				count++;
			}
		}
		
		// Bottom left
		if (x - 1 >= 0 && y + 1 < gridSize) {
			if (grid[x - 1][y + 1].on) {
				count++;
			}
		}
		
		// Bottom middle
		if (y + 1 < gridSize) {
			if (grid[x][y + 1].on) {
				count++;
			}
		}
		
		// Bottom right
		if (x + 1 < gridSize && y + 1 < gridSize) {
			if (grid[x + 1][y + 1].on) {
				count++;
			}
		}
		
		return count;
	}
	
	private synchronized void start() {
		thread = new Thread(this);
		thread.start();
		running = true;
	}

	private synchronized void stop() {
		try {
			thread.join();
			running = false;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	// Game loop because I used a game template for the rendering
	public void run() {
		init();
		while(running) {
			render();
			if (timer > 0) {
				timer++;
				if (timer % 300 == 0) {
					incrementTime();
				}
			}
			
			
		}
		stop();
	}
	
	private void render() {
		BufferStrategy bs = this.getBufferStrategy();
		if(bs == null) {
			this.createBufferStrategy(2);
			return;
		}
		Graphics2D g = (Graphics2D) bs.getDrawGraphics().create();
		g.setColor(Color.white);
		g.fillRect(0, 0, WIDTH, HEIGHT);
		g.setColor(Color.black);
		
		for (int i = 0; i < gridSize; i++) {
			for (int j = 0; j < gridSize; j++) {
				grid[i][j].render(g);
			}
		}
		
		g.setColor(Color.red);
		g.fill(incrementButton);
		
		g.setColor(Color.BLACK);
		g.fill(timeButton);
		
		g.setColor(Color.blue);
		g.fill(clearButton);
		
		g.setColor(Color.white);
		g.setFont(new Font("arial", Font.BOLD, 20));
		g.drawString("i++", incrementButton.x + 10, incrementButton.y + 30);
		g.drawString("auto", timeButton.x, timeButton.y + 30);
		g.drawString("clear", clearButton.x, clearButton.y + 30);
		
		g.dispose();
		bs.show();
	}
	
	public static void main(String args[]) {
		JFrame frame = new JFrame("Cellular Automaton");
		frame.setPreferredSize(new Dimension(WIDTH, HEIGHT));
		frame.setMaximumSize(new Dimension(WIDTH, HEIGHT));
		frame.setMinimumSize(new Dimension(WIDTH, HEIGHT));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		CellularAutomaton automaton = new CellularAutomaton();
		frame.add(automaton);
		frame.setVisible(true);
		automaton.start();
		
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (incrementButton.contains(new Point(e.getX(), e.getY()))) {
			incrementTime();
		} else if (clearButton.contains(new Point(e.getX(), e.getY()))) {
			for (int i = 0; i < gridSize; i++) {
				for (int j = 0; j < gridSize; j++) {
					grid[i][j].on = false;
				}
			}
		} else if (timeButton.contains(new Point(e.getX(), e.getY()))) {
			if (timer > 0) {
				timer = 0;
			} else {
				timer++;
			}
			
		} else {
			for (int i = 0; i < gridSize; i++) {
				for (int j = 0; j < gridSize; j++) {
					if (grid[i][j].rect.contains(new Point(e.getX(), e.getY()))) {
						grid[i][j].changeStatus();
						return;
					}
				}
			}
		}
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		for (int i = 0; i < gridSize; i++) {
			for (int j = 0; j < gridSize; j++) {
				if (grid[i][j].rect.contains(new Point(e.getX(), e.getY()))) {
					grid[i][j].changeStatus();
					return;
				}
			}
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		
	}
}