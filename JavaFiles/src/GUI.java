import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class GUI extends JFrame {
	
	int spacing = 1;
	
	public int mx = -100;
	public int my = -100;
	public int startX = -100;
	public int startY = -100;
	public int endX = -100;
	public int endY = -100;
	
	public boolean[][] off = new boolean[21][21];
	public boolean[][] isFirstLetter = new boolean[21][21];
	
	public boolean submitted = false;
	public boolean finished = false;
	
	public char[][] finishedGrid = new char[21][21];
	
	public GUI() {
		this.setTitle("Crossword Maker");
		this.setSize(490, 510);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setVisible(true);
		this.setResizable(false);
		
		Grid grid = new Grid();
		this.setContentPane(grid);
		
		Move move = new Move();
		this.addMouseMotionListener(move);
		Click click = new Click();
		this.addMouseListener(click);
	}
	
	public class Grid extends JPanel {
		public void paintComponent(Graphics g) {
			g.setColor(Color.DARK_GRAY);
			g.fillRect(0, 0, 490, 510);
			//submit button
			g.setColor(Color.LIGHT_GRAY);
			g.fillRect(190, 455, 100, 20);
			g.setColor(Color.BLACK);
			g.drawString("Generate", 215, 470);
			//21x21 grid with box dimensions of 34
			for(int i=0; i<21; i++) {
				for(int j=0; j<21; j++) {
					g.setColor(Color.WHITE);
					if(off[i][j]) {
						g.setColor(Color.BLACK);
					}
					//show across down and both
					/*if(off[i][j]==false) {
						if((off[Math.max(i-1,0)][j] && off[i][Math.max(j-1,0)]) || (i==0 && j==0) || (off[Math.max(i-1,0)][j] && j==0) || (off[i][Math.max(j-1,0)] && i==0)) {
							g.setColor(Color.PINK);
						} else if(off[i][Math.max(j-1,0)] || j==0) {
							g.setColor(Color.RED);
						} else if(off[Math.max(i-1,0)][j] || i==0) {
							g.setColor(Color.BLUE);
						}
					}*/
					//Highlight selected box
					/*if(mx>=spacing+i*20+30 && mx<i*20+30+20-2*spacing && my>=spacing+j*20+30+26 && my<j*20+26+30+20-2*spacing) {
						g.setColor(Color.BLUE);
					}*/
					g.fillRect(spacing+i*20+30, spacing+j*20+30, 20-2*spacing, 20-2*spacing);
					if(submitted) {
						if(finishedGrid[i][j]!='.' && finishedGrid[i][j]!='!') {
							g.setColor(Color.BLACK);
							g.drawString(""+finishedGrid[i][j], spacing+i*20+36, spacing+j*20+43);
						}
					}
				}
			}
		}
	}
	
	public class Move implements MouseMotionListener {
		
		@Override
		public void mouseDragged(MouseEvent e) {
			
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			mx = e.getX();
			my = e.getY();
			
		}
		
	}
	
	public class Click implements MouseListener {

		@Override
		public void mouseClicked(MouseEvent e) {
			if(submitted == false) {
				if(xBox(mx,my) !=-1 && yBox(mx,my) != -1) {
					if(off[xBox(mx,my)][yBox(mx,my)]) {
						off[xBox(mx,my)][yBox(mx,my)] = false;
					} else {
						off[xBox(mx,my)][yBox(mx,my)] = true;
					}
				}
			}
			
		}

		@Override
		public void mouseEntered(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseExited(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mousePressed(MouseEvent e) {
			startX = e.getX();
			startY = e.getY();
			if(submitted == false) {
				if(xBox(mx,my) !=-1 && yBox(mx,my) != -1) {
					if(off[xBox(mx,my)][yBox(mx,my)]) {
						off[xBox(mx,my)][yBox(mx,my)] = false;
					} else {
						off[xBox(mx,my)][yBox(mx,my)] = true;
					}
				}
			}
		
		if (mx>=190 && mx<290 && my>=480 && my<500) {
			submitted = true;
			Thread fill = new Thread() {
				public void run() { 
					long startTime = System.nanoTime();
					finishedGrid = CrosswordMaker.makeCustom(off);
					long endTime = System.nanoTime();
					long duration = (endTime - startTime)/100000000;  //divide by 1000000 to get milliseconds.
					System.out.println("time: "+duration);
			    }
			};
			fill.start();
		}
			
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			endX = e.getX();
			endY = e.getY();
			int startXBox = xBox(startX,startY);
			int startYBox =yBox(startX,startY);
			int endXBox =xBox(endX,endY);
			int endYBox =yBox(endX,endY);
			
			if(submitted==false) {
				if(startXBox!=-1 && startYBox!=-1 && endXBox!=-1 && endYBox!=-1) {
					//down and right
					if(startXBox<=endXBox && startYBox<=endYBox) {
						for(int i=startXBox; i<= endXBox; i++) {
							for(int j=startYBox; j<= endYBox; j++) {
								if(off[i][j]==false) {
									off[i][j]=true;
								}else {
									off[i][j]=false;
								}
							}
						}
					}
					//down and left
					if(startXBox>=endXBox && startYBox<=endYBox) {
						for(int i=startXBox; i>= endXBox; i--) {
							for(int j=startYBox; j<= endYBox; j++) {
								if(off[i][j]==false) {
									off[i][j]=true;
								}else {
									off[i][j]=false;
								}
							}
						}
					}
					//up and right
					if(startXBox<=endXBox && startYBox>=endYBox) {
						for(int i=startXBox; i<= endXBox; i++) {
							for(int j=startYBox; j>= endYBox; j--) {
								if(off[i][j]==false) {
									off[i][j]=true;
								}else {
									off[i][j]=false;
								}
							}
						}
					}
					//up and left
					if(startXBox>=endXBox && startYBox>=endYBox) {
						for(int i=startXBox; i>= endXBox; i--) {
							for(int j=startYBox; j>= endYBox; j--) {
								if(off[i][j]==false) {
									off[i][j]=true;
								}else {
									off[i][j]=false;
								}
							}
						}
					}
					if(off[startXBox][startYBox]==false) {
						off[startXBox][startYBox]=true;
					}else {
						off[startXBox][startYBox]=false;
					}
				}
			}
			
		}
		
	}
	
	public int xBox(int x, int y) {
		for(int i=0; i<21; i++) {
			for(int j=0; j<21; j++) {
				if(x>=spacing+i*20+30 && x<i*20+30+20-2*spacing && y>=spacing+j*20+30+26 && y<j*20+26+30+20-2*spacing) {
					return i;
				}
			}
		}
		return -1;
	}
	
	public int yBox(int x, int y) {
		for(int i=0; i<21; i++) {
			for(int j=0; j<21; j++) {
				if(x>=spacing+i*20+30 && x<i*20+30+20-2*spacing && y>=spacing+j*20+30+26 && y<j*20+26+30+20-2*spacing) {
					return j;
				}
			}
		}
		return -1;
	}

}
