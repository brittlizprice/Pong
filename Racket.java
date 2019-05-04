package pong;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

public class Racket {
	public int racketNumber;
	protected int x;
	protected int y;
	protected int width = 40;
	protected int height = 120;
	private int up;
	private int down;
	private int yAxis;
	private Pong game;
	public int score, totalWins = 0;
	
	/**
	 * Creates both rackets.
	 * @param game
	 * @param racketNumber
	 */
	public Racket(Pong game,int racketNumber) {
		this.racketNumber = racketNumber;
		this.game = game;
		if(racketNumber == 1) {
			this.x = 0;
		}
		
		if(racketNumber == 2) {//right side of the screen
			this.x = Pong.WIDTH - width;
		}
		y = game.getHeight() / 2 - this.height/2;// ----|--- each side of board
	}
	
	/**
	 * @param game instance of Pong
	 * @param racketNumber racket for each player
	 * @param score        score for both players
	 * @param totalWins    number of wins each player has
	 */
	public Racket(Pong game,int racketNumber, int score, int totalWins) {
		this.racketNumber = racketNumber;
		this.game = game;
		this.score = score;
		this.totalWins = totalWins;
	}
	
	/**
	 * Determines the speed of the rackets
	 * as well as if they are moving up or down.
	 * @param r
	 */
	public void move(boolean up) {
		int velocity = 10;
		if(up) {
			if(y - velocity > 0) {
				y-=velocity;
			}
			
			else {
				y = 0;
			}
		}
		else {
			if(y + height + velocity < Pong.pong.HEIGHT) {
				y+= velocity;
			}
			
			else {
				y = Pong.pong.HEIGHT - height;
			}
		}
	}
	
	/**
	 * Updates the position of each racket along the y-axis.
	 */
	protected void update() {
		if (y > 0 && y < game.getHeight() - height - 29)
			y += yAxis;
		else if (y == 0)
			y++;
		else if (y == game.getHeight() - height - 29)
			y--;
	}

	protected void pressed(int keyCode) {
		if (keyCode == up)
			yAxis = -1;
		else if (keyCode == down)
			yAxis = 1;
	}

	protected void released(int keyCode) {
		if (keyCode == up || keyCode == down)
			yAxis = 0;
	}

	public Rectangle getBounds() {
		return new Rectangle(x, y, width, height);
	}


	public void paint(Graphics g) {
		g.setColor(Color.white);
		g.fillRect(x, y, width, height);
	}
}