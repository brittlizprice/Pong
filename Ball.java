package pong;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.Random;

import javax.swing.JOptionPane;

public class Ball {

	public int x, y, width = 25, height = 25;
	public int motionX, motionY;
	public Random random;
	private Pong game, pong;
	public int amtOfHits;

	public Ball(Pong pong) {
		//this.game = game;
		this.pong = pong;
		this.random = new Random();
		spawn();
	}

	/**
	 * Updates the rackets and ball.
	 * Also responsible for the ball's motion.
	 * @param racket1
	 * @param racket2
	 */
	public void update(Racket racket1, Racket racket2) {
		int speed = 1;
		this.x += motionX * speed;
		this.y += motionY * speed;
		
		if (this.y + height > Pong.HEIGHT || this.y < 0) {// on the right side
			if(this.motionY < 0) {
				this.y = 0;
				this.motionY = random.nextInt(4);
				if(this.motionY == 0) {
					this.motionY = 1;
				}
			}
			else {
				this.motionY = -random.nextInt(4);
				this.y = Pong.HEIGHT - height;
				if(this.motionY == 0) {
					this.motionY = -1;
				}
			}
		}
		
//		if (this.x + height > Pong.WIDTH || this.x < 0) {// on the right side
//		
//		else {
//			this.motionY = -random.nextInt(4);
//		}
//	}
		if(checkCollision(racket1) == 1) {
			this.motionX = 1 + (amtOfHits / 5);//speed up every 5 hits
			this.motionY = -2 + random.nextInt(4);//ball going up or down
			if(motionY == 0) {
		    	 motionY = 1;
		     }
			amtOfHits++;
		}
		
		else if(checkCollision(racket2) == 1) {
			this.motionX = 1 - (amtOfHits / 5);
			this.motionY = -2 + random.nextInt(4);//ball going up or down
			if(motionY == 0) {
		    	 motionY = 1;
		     }
			amtOfHits++;
		}

		if(checkCollision(racket1) == 2) {
			racket2.score++;
			spawn();
		}
		
		else if(checkCollision(racket2) == 2) {
			racket1.score++;
			spawn();
		}
	}
	
	/**
	 *if the ball hits the top or bottom of the screen
	 * @return
	 */
	protected boolean hit() {
		return y < 0 || y > game.getHeight() - height - 27;
	}
	
	

	public Rectangle getBounds() {
		return new Rectangle(x, y, width, height);
	}


	/**
	 * Checks for collision with either racket.
	 */
	public int checkCollision(Racket racket) {
		// handles collisions
		// if mouse is at racket & if it hits the paddle
		if (this.x < racket.x + racket.width && this.x + width > racket.x 
				&& this.y < racket.y + racket.height && this.y + height > racket.y) {// on the right side
			return 1;//bounce
		}
		else if ((racket.x > x && racket.racketNumber == 1) || 
				racket.x < x - width && racket.racketNumber == 2) {// hitting inside the right racket
			return 2;//score
		}
		return 0;
	}
	
	/**
	 * Keeps the ball on the screen.
	 */
	public void spawn() {
		this.amtOfHits = 0;
		x = pong.getWidth() / 2 - this.width/2;
	     y = pong.getHeight() / 2 - this.height/2;
	     this.motionY = -2 + random.nextInt(4);
	     //this.motionX = -1 + random.nextInt(1);
	     
	     if(motionY == 0) {
	    	 motionY = 1;
	     }
	     
	     if(random.nextBoolean()) {
	    	 motionX = 1; 
	     }
	     
	     else {
	    	 motionX = -1;
	     }
	}

	public void paint(Graphics g) {
		g.setColor(Color.white);
		g.fillOval(x, y, width, height);
	}
}