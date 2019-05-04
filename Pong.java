package pong;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.UIManager;

import java.awt.Robot;

import java.awt.Toolkit;
import java.awt.Rectangle;

public class Pong extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	{
		try {
			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected static final int WIDTH = 700;
	protected final static int HEIGHT = 500;
	public static PongPanel panel;
	public static Pong pong;

	protected ArrayList<String> leaderboard;
	protected String wName;

	protected Image img;
	protected ImageIcon image;
	Component component;
	private Thread run;
	private AtomicBoolean running = new AtomicBoolean(false);
	private AtomicBoolean stopped = new AtomicBoolean(false);
	
	/**
	 * Sets up the frame of the board along with the title. Also makes the rackets,
	 * ball, and score board visible.
	 */
	public Pong() {
		setSize(WIDTH, HEIGHT + 30);
		setTitle("Pong");
		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());
		contentPane.setBackground(Color.black);

		panel = new PongPanel(this);

		contentPane.add(panel);

		// contentPane.setLocation(200, -200);
		setResizable(false);
		setVisible(true);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

	}

	/**
	 * Shows leaderboard in Doodle with the label "Leaderboard".
	 */
	public void showLeaderboard() {
		panel.showLeaderboard();
	}

	public static void main(String[] args) {
		pong = new Pong();
	}

	/**
	 * Getter for PongPanel
	 * 
	 * @return panel
	 */
	public PongPanel getPanel() {
		return panel;
	}

	public void interrupt() {
        running.set(false);
        run.interrupt();
    }
	
	boolean isRunning() {
        return running.get();
    }
 
    boolean isStopped() {
        return stopped.get();
    }
    
    
	/**
	 * Getting the dimension (the area to be captured),
	 * filing the area with content  (i.e your screen),
	 * and finally writing (saving the image to a file)
	 * Takes screenshot after delay.
	 * @param delay
	 * @param fileName
	 */
	public void screenShot(int delay, String fileName) {
		try {
			while(running.get()) {
				running.set(true);			
			}	
			long time = Long.valueOf(delay) * 1000L;
			Thread.sleep(time);
			System.out.println("Creating a delay of " + delay +
					" second(s)...");
			Rectangle screen = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
			Robot robot = new Robot();
			BufferedImage capture = robot.createScreenCapture(screen);

			fileName = "TestImages/ScreenShot.png";
			ImageIO.write(capture, "png", new File(fileName));//NPE; FileNotFound
		} 
		
		catch (InterruptedException ie) {
			Thread.currentThread().interrupt();
			System.out.println("Error with Thread\n" + ie);
		} 
		catch (IOException | AWTException i) {
			System.err.println("Caught IO or AWT Exception\n" + i);
		}
	}

	/**
	 * Allows users to capture the app’s state as image 
	 * and capturing the JFrameForm which is being displayed.
	 * @param frame
	 * @param fileName
	 */
	public void fullFrame(JFrame frame, String fileName) {

		//Rectangle frameSize = frame.getBounds(); // Getting the size of JFrame
		try {
			int delay = 10;
			while(running.get()) {
				
				running.set(true);
				System.out.println("Creating a delay of " + delay +
						" second(s)...");
			}
			long time = Long.valueOf(delay) * 1000L;
			Thread.sleep(time);
			BufferedImage screenShot = new BufferedImage(WIDTH, HEIGHT,
					BufferedImage.TYPE_INT_ARGB);
			frame.paint(screenShot.getGraphics());
			fileName = "TestImages/PongScreenShot.png";
			ImageIO.write(screenShot, "png", new File(fileName));

		} catch (IOException ioe) {
			System.out.println("Caught exception:\n" + ioe);

		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			System.out.println("Error with Thread\n" + e);
		}

	}
	
	/**
	 * We get width and height of our computer screen using Toolkit class and 
	 * then create a Rectangle with that dimension. 
	 * Then we extract the graphics displayed within that rectangle using Robot class. 
	 * Finally, we write it to a file and save it as a image. 
	 * The string passed into the method will be used to name the image captured.
	 * @param evt
	 */
	private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {                                         
        screenShot(10, "ScreenShot.png");
        fullFrame(null, "PongScreenShot");
}
	/**
	 * Paints the basic frame for the game.
	 * 
	 * @return g
	 */
	public void paint(Graphics g) {
		super.paint(g);
		g.setColor(Color.black);
		// g.fillRect(0, 0, WIDTH, HEIGHT);
	}

	///////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////

	public class PongPanel extends JPanel implements ActionListener, KeyListener {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private int score1, score2;
		protected int scoreLimit = 10, playerWon;
		private Pong game;
		private Ball ball;
		private Racket player1, player2;
		public boolean computer = false, selectingDifficulty;
		public int computerDifficulty, computerMoves, computerCooldown = 0;
		private Leaderboard board;
		public boolean w, s, up, down;
		protected int gameStatus = 0;// 0 = Stopped, 1 = Paused, 2 = Playing, 3 = Game Over
		protected ArrayList<String> leaderboard;
		protected String wName;

		{
			try {
				UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		/**
		 * Starts the game, paints the background of the panel, calls the Leaderboard
		 * class, and adds key listeners.
		 * 
		 * @param game
		 */
		public PongPanel(Pong game) {
			setBackground(Color.black);

			img = Toolkit.getDefaultToolkit().getImage("TestImages/Atari.jpg");

			this.game = game;

			startGame();
			Timer timer = new Timer(5, this);
			timer.start();

			board = new Leaderboard();
			// board.displayScore();
			board.setVisible(false);

			addKeyListener(this);
			setFocusable(true);
		}

		public void showLeaderboard() {
			board.setVisible(true);
		}

		public void hideLeaderboard() {
			board.setVisible(false);
		}



		/**
		 * Method to instantiate player 1, player 2, and the ball.
		 */

		public void startGame() {
			player1 = new Racket(game, 1);
			player2 = new Racket(game, 2);
			ball = new Ball(game);
		}

		/**
		 * Increases the score for each player.
		 * 
		 * @param playerNo
		 */
		protected void increaseScore(int playerNo) {
			if (playerNo == 1)
				score1++;
			else
				score2++;
		}

		/**
		 * Getter for both players' scores.
		 * 
		 * @param playerNo
		 * @return score1 or score2
		 */
		protected int getScore(int playerNo) {
			if (playerNo == 1)
				return score1;
			else
				return score2;
		}

		public void actionPerformed(ActionEvent e) {
			if (gameStatus == 2) {
				update();
			}
			repaint();
		}

		/**
		 * Updates player 1 and player 2's movements. Resets the score back to 0 for
		 * each new game. Displays the winner once player 1 or 2 wins. The winner gets
		 * to type in their name which is saved to string and added to the arraylist.
		 * Also makes leaderboard visible after each game.
		 */
		protected void update() {

			if (w) {
				player1.move(true);
			}

			if (s) {
				player1.move(false);
			}

			if (!computer) {
				if (up) {
					player2.move(true);
				}

				if (down) {
					player2.move(false);
				}
			} else {
				if (computerCooldown > 0) {
					computerCooldown--;
					if (computerCooldown == 0) {
						computerMoves = 0;
					}
				}

				if (computerMoves < 10) {
					if (player2.y + player2.height / 2 < ball.y) {
						player2.move(false);
						computerMoves++;
					}

					if (player2.y + player2.height / 2 > ball.y) {
						player2.move(true);
						computerMoves++;
					}

					if (computerDifficulty == 0) {
						computerCooldown = 100;
					}
					if (computerDifficulty == 1) {
						computerCooldown = 50;
					}
					if (computerDifficulty == 2) {
						computerCooldown = 10;
					}
				}
			}

			ball.update(player1, player2);

			if (player1.score == 2) {
				player1.score = 0;
				player2.score = 0;
				playerWon = 1;
				JOptionPane.showMessageDialog(null, "Player 1 is the winner!", "Pong", JOptionPane.PLAIN_MESSAGE);
				wName = JOptionPane.showInputDialog("Enter Winners Name: ");
				leaderboard = new ArrayList<String>();
				leaderboard.add(wName);

				board.saveScore(leaderboard);
				board.clear();
				board.displayScore();
				showLeaderboard();

				gameStatus = 3;
				System.out.println(leaderboard);
			}

			else if (player2.score == 2) {
				player1.score = 0;
				player2.score = 0;
				playerWon = 2;
				JOptionPane.showMessageDialog(null, "Player 2 is the winner!", "Pong", JOptionPane.PLAIN_MESSAGE);
				wName = JOptionPane.showInputDialog("Enter Winners Name: ");

				leaderboard = new ArrayList<String>();
				leaderboard.add(wName);

				board.saveScore(leaderboard);
				board.clear();
				board.displayScore();
				showLeaderboard();

				gameStatus = 3;
				System.out.println(leaderboard);
			}
		}

		/**
		 * Keeps track of the keys pressed to move the rackets up and down the screen
		 * and the key to start and pause the game.
		 * @param e instance of KeyEvent
		 */
		public void keyPressed(KeyEvent e) {
			int id = e.getKeyCode();
			if (id == KeyEvent.VK_W) {
				w = true;
			}

			if (id == KeyEvent.VK_S) {
				s = true;
			}

			if (id == KeyEvent.VK_UP) {
				up = true;
			}

			if (id == KeyEvent.VK_DOWN) {
				down = true;
			}

			else if (id == KeyEvent.VK_RIGHT) {
				if (selectingDifficulty) {
					if (computerDifficulty < 2) {
						computerDifficulty++;
					} else {
						computerDifficulty = 0;
					}
				} else if (gameStatus == 0) {
					scoreLimit++;
				}
			}

			else if (id == KeyEvent.VK_LEFT) {
				if (selectingDifficulty) {
					if (computerDifficulty > 0) {
						computerDifficulty--;
					} else {
						computerDifficulty = 2;
					}
				} else if (gameStatus == 0 && scoreLimit > 1) {
					scoreLimit--;
				}
			}

			else if (id == KeyEvent.VK_ESCAPE && (gameStatus == 2 || gameStatus == 3)) {
				gameStatus = 0;
			}

			else if (id == KeyEvent.VK_SHIFT && gameStatus == 0) {
				computer = true;
				selectingDifficulty = true;
			}

			if (id == KeyEvent.VK_SPACE) {
				if (gameStatus == 0) {
					gameStatus = 2;
				}

				else if (gameStatus == 0) {
					gameStatus = 1;
				}

				else if (gameStatus == 2) {
					gameStatus = 1;
				}

				else if (gameStatus == 1) {
					gameStatus = 2;
				}

				else if (gameStatus == 0 || gameStatus == 3) {
					if (!selectingDifficulty) {
						computer = false;
					} else {
						selectingDifficulty = false;
					}
					gameStatus = 2;// Starts new game
					startGame();
				}
			}
		}

		/**
		 *Keeps track of when a key is released.
		 *@param e instance of KeyEvent
		 */
		public void keyReleased(KeyEvent e) {
			int id = e.getKeyCode();
			if (id == KeyEvent.VK_W) {
				w = false;
			}

			if (id == KeyEvent.VK_S) {
				s = false;
			}

			if (id == KeyEvent.VK_UP) {
				up = false;
			}

			if (id == KeyEvent.VK_DOWN) {
				down = false;
			}
		}

		public void keyTyped(KeyEvent e) {
			;
		}

		/**
		 * Paints the players, rackets, ball, scores, game status, 
		 * and the start screen.
		 * @param g instance of the Graphics class.
		 */
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2 = (Graphics2D) g;

			if (gameStatus == 0) {
				g2.drawImage(img, 0, 0, Pong.WIDTH, Pong.HEIGHT, this);
				g.setColor(Color.WHITE);
				g.setFont(new Font("Arial", 1, 50));
				g.drawString("PONG", Pong.WIDTH / 2 - 70, 50);

				if (!selectingDifficulty) {
					g.setFont(new Font("Arial", Font.BOLD, 25));
					g.drawString("Press Space to Play", Pong.WIDTH / 2 - 150, Pong.HEIGHT / 2 - 25);
					g.drawString("Press Shift to Play with computer", Pong.WIDTH / 2 - 240, Pong.HEIGHT / 2 + 20);
				}

			}
			if (selectingDifficulty) {
				g.setFont(new Font("Arial", Font.BOLD, 30));
				g.setColor(Color.white);
				String string = computerDifficulty == 0 ? "Easy" : (computerDifficulty == 1 ? "Medium" : "Hard");
				g.drawString("<< computer Difficulty: " + string + " >>", WIDTH / 2 + 130, HEIGHT / 2 + 250);
				g.drawString("Press Space to Play", WIDTH / 2 + 200, HEIGHT / 2 + 300);
				
				if(gameStatus == 2) {
					g.setColor(Color.black);
					g.drawString("<< computer Difficulty: " + string + " >>", WIDTH / 2 + 130, HEIGHT / 2 + 250);
					g.drawString("Press Space to Play", WIDTH / 2 + 200, HEIGHT / 2 + 300);
				}
			}
			if (gameStatus == 1) {
				g2.drawImage(img, 0, 0, Pong.WIDTH, Pong.HEIGHT, this);
				g.setColor(Color.WHITE);
				g.setFont(new Font("Arial", 1, 50));
				g.drawString("Paused", Pong.WIDTH / 2 - 90, Pong.HEIGHT / 2 - 50);
			}
			if (gameStatus == 2) {
				g.setColor(Color.WHITE);
				g.drawLine(700 / 2, 0, 700 / 2, 550);
				g.drawOval(WIDTH / 2 + 200, HEIGHT / 2 + 100, 300, 300);

				player1.paint(g);
				player2.paint(g);
				ball.paint(g);

				g.setFont(new Font("Arial", 1, 25));
				g.setColor(Color.WHITE);
				g.drawString("Player 1: " + String.valueOf(player1.score), WIDTH / 2 + 175, 40);
				g.drawString("Player 2: " + String.valueOf(player2.score), WIDTH / 2 + 400, 40);
			}
			if (gameStatus == 3) {
				g.setColor(Color.WHITE);
				g.setFont(new Font("Arial", 1, 50));

				g.drawString("PONG", WIDTH / 2 + 280, 50);

				if (computer && playerWon == 2) {
					g.drawString("The computer Wins!", WIDTH / 2 + 110, 200);
				} else {
					g.drawString("Player " + playerWon + " Wins!", WIDTH / 2 + 165, 200);
				}

				g2.drawImage(img, 0, 0, Pong.WIDTH, Pong.HEIGHT, this);
				g.setFont(new Font("Arial", 1, 20));
				g.drawString("Press Space to Play Again", WIDTH / 2 + 220, HEIGHT / 2 + 280);// fix
				g.drawString("Press ESC for Start Screen", WIDTH / 2 + 250, HEIGHT / 2 + 250);
			}

		}
	}

	public ArrayList<String> getwName() {
		return leaderboard;
	}

	///////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////

	public class Leaderboard extends JFrame {
		// keep track of the number of wins per player
		// needs to PERMANENTLY save the scores in a separate text
		// file from PongPanel
		/**
		 * Keeps track of the number of wins per player.
		 */
		private static final long serialVersionUID = 1L;
		{
			try {
				UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		protected static final int WIDTH = 700;
		protected final static int HEIGHT = 700;

		protected String label, fileName;
		protected Leaderboard lb;
		protected DisplayBoard display;
		protected Racket player1, player2;
		protected int bp = 0;
		protected int gh = 0;
		protected int jl = 0;
		protected int jj = 0;
		protected int jm = 0;
		protected int c = 0;

		/**
		 * Sets up the frame of the board along with the title and makes the leader
		 * board visible.
		 */
		public Leaderboard() {
			setSize(WIDTH, HEIGHT);
			setTitle("Leaderboard");
			Container contentPane = getContentPane();
			contentPane.setLayout(new BorderLayout());
			contentPane.setBackground(Color.black);

			display = new DisplayBoard();
			contentPane.add(display);

			// contentPane.setLocation(500, -200);
			setResizable(false);
			setVisible(true);
			setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		}

		public Leaderboard(int bp, int gh, int jl, int jj, int jm, int c) {
			this.bp = bp;
			this.gh = gh;
			this.jl = jl;
			this.jj = jj;
			this.jm = jm;
			this.c = c;
		}

		public void main(String[] args) {
			lb = new Leaderboard();
		}

		/**
		 * Saves the names of the winner of each game into a text file after the file is
		 * read in to see if it is empty.
		 * 
		 * @param fileName
		 * @return
		 */
		public void saveScore(ArrayList<String> alist) {
			File scores = null;
			String line = null;
			int lineNumber = 0;
			PrintWriter saveOut = null;
			try {
				BufferedReader br = new BufferedReader(new FileReader("src/Wins.txt"));
				// read line by line
				while ((line = br.readLine()) != null) {
					lineNumber++;
					System.out.println("Line " + lineNumber + " : " + line);
				}
				scores = new File("src/Wins.txt");
				FileWriter fw = new FileWriter(scores, true);// want to add to file
				BufferedWriter bw = new BufferedWriter(fw);
				saveOut = new PrintWriter(bw);
				// saveOut.println("Player: " + panel.wName);
				for (int i = 0; i < alist.size(); i++) {
					wName = alist.get(i);// turns into string
					saveOut.println("Player: " + wName);

					// saveOut.printf("Winner's Name: " + "%d%n", wName);
				}
				br.close();
				bw.close();
				fw.close();
				saveOut.close();// can't write after this; data flushed
				System.out.println("Done");
			}

			catch (IOException f) {
				System.err.println("File not found" + f + "\n");
			}
			// return line;
		}

		public void clear() {
			bp = gh = jl = jm = jj = c = 0;
		}

		/**
		 * Reads in the text file with the players' names and keeps track of number of
		 * wins for each player.
		 * 
		 * @param fileName
		 * @return
		 */
		public void displayScore() {
			BufferedReader br = null;
			String line = null;

			// reads in lines here
			// iterate over names to get leaderboard count
			try {
				br = new BufferedReader(new FileReader("src/Wins.txt"));
				while ((line = br.readLine()) != null) {
					if (line.contains("Brittany")) {
						bp++;
					}

					else if (line.contains("Grant")) {
						gh++;
					}

					else if (line.contains("Jaime")) {
						jl++;
					}

					else if (line.contains("Josh J")) {
						jj++;
					}

					else if (line.contains("Josh M")) {
						jm++;
					}

					else if (line.contains("Computer") || line.contains("computer")) {
						c++;
					}
					continue;
				}
				br.close();
			}

			catch (IOException io) {
				System.err.println(io + "Error reading file");
			} finally {
				repaint();
			}
		}

		/**
		 * This class handles the graphics for the Leaderboard.
		 */
		class DisplayBoard extends JPanel {
			Leaderboard board;

			public DisplayBoard() {
				setBackground(Color.black);
			}

			protected void update() {
				displayScore();
			}

			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				Graphics2D g2 = (Graphics2D) g;
				g2.drawImage(img, 0, 0, Leaderboard.WIDTH, Leaderboard.HEIGHT, this);

				g.setColor(Color.WHITE);

				g.setFont(new Font("Arial", 1, 30));
				g.setColor(Color.WHITE);
				g.drawString("Player Stats ", WIDTH / 2 + 250, HEIGHT / 2 + 50);

				g.setFont(new Font("Arial", 1, 20));
				g.setColor(Color.WHITE);
				g.drawString("Brittany's Wins: " + String.valueOf(bp), WIDTH / 2 + 250, HEIGHT / 2 + 100);
				g.drawString("Grant's Wins: " + String.valueOf(gh), WIDTH / 2 + 250, HEIGHT / 2 + 150);
				g.drawString("Jaime's Wins: " + String.valueOf(jl), WIDTH / 2 + 250, HEIGHT / 2 + 200);
				g.drawString("Josh J's Wins: " + String.valueOf(jj), WIDTH / 2 + 250, HEIGHT / 2 + 250);
				g.drawString("Josh M's Wins: " + String.valueOf(jm), WIDTH / 2 + 250, HEIGHT / 2 + 300);
				g.drawString("Computer's Wins: " + String.valueOf(c), WIDTH / 2 + 250, HEIGHT / 2 + 350);
			}
		}
	}
}