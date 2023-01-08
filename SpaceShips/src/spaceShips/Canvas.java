package spaceShips;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JPanel;
import java.util.ArrayList;
import javax.swing.JTextArea;

public class Canvas extends JPanel{
	public int minimapShiftConstant = 100;
	
	//this variable holds the value of the side length of the rectangles drawn to represent entities on the minimap
	public int minimapItemSize = 3;
	
	//how long the edge of the square background needs to be. Make is 4500 so it works on any 4k monitor
	private final int backgroundBorderLength = 4500;
	
	//distance between gridlines
	private final int gridlineDistance = 60;
	
	//how many times smaller than the normal game the minimap should be
	private final int minimapScaleConstant = 50;
	
	//many objects need to be drawn at their coordinate - their size/2 because
	//the methods that perform the drawing take in the coordinates of the top left corner,
	//so we calculate that coordinate by subtracting size/2 on each axis
	private final int shiftCenterToTopLeftDivisor = 2;
	
	//how wide the map gridlines should be
	private final int gridLineWidth = 1;
	
	private final int spawnerWidth = 40;
	
	//the text for the user to see on the left side of the starting screen
	
	//coordinates of top left corner of where the left text should go
	private final int leftTextXShiftFromCenter = 100;
	private int leftTextX = 100;
	private int leftTextY = 100;
	private int leftTextWidth = 400;
	
	
	//how many pixels between each line of drawn text
	private final int textIncrement = 20;
	
	//what fraction of the increment the text box needs to be shifted by to cover the letters
	private final double textBoxShiftConstant = 0.7;
	
	//how many pixels should be on the left border of the text box;
	private final int textBoxLeftBorder = 5;
	
	//the array list holding all the strings of the game instructions to be drawn on the left
	private ArrayList<String> leftText = new ArrayList<String>();
	
	
	//coordinates of the top left corner of where the center text should go
	private final int centerTextX = 500;
	private final int centerTextY = 200;
	private final int centerTextWidth = 500;
	
	//the array list holding all the strings of the game instructions to be drawn on the center
	public ArrayList<String> centerText = new ArrayList<String>();
	
	public Canvas()
	{
		//create the part of the game instructions that appears  on the left
    	leftText.add("the blue dot in the center of the screen represents your spaceship");
    	leftText.add("all red dots represent enemy ships");
    	leftText.add("if an enemy ship collides with your ship, you lose");
    	leftText.add("if an enemy ship collides with another enemy ship, both ships die");
    	leftText.add("");
    	
    	leftText.add("enemy ships spawn periodically on the yellow squares");
    	leftText.add("use 'wasd' keys to accelerate");
    	leftText.add("your ship will maintain a constant velocity unless you press keys");
    	leftText.add("do not hold keys to accelerate your ship, just tap them");
    	leftText.add("tap the key repeatedly to accelerate the ship quickly");
    	leftText.add("");
    	
    	leftText.add("there is a minimap in the top left");
    	leftText.add("if your ship collides with a yellow square, the square will be destroyed");
    	leftText.add("destroy all the yellow squares to win");
    	leftText.add("");
    	
    	leftText.add("the game has two modes, one where you can cick to shoot");
    	leftText.add("on some computers, the gun simply doesn't work");
    	leftText.add("on others, the gun works only if the window is fullscreen");
    	leftText.add("press 'i' to play with the gun enabled");
    	leftText.add("");
    	
    	
    	leftText.add("for a challenge, press 'p' to play without the gun");
	}
    
    //this is where all the code that causes graphics to display is written
	public void paintComponent(Graphics g)
	{
		//firstly, we have to draw the background every time we draw anything new
		//because when we are drawing something in a new position we need to erase the picture
		//of it in its old position, and the only way to do this is to draw over the old picture.
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, backgroundBorderLength, backgroundBorderLength);
		
		//now we set the color to black so that we can draw the gridlines
		g.setColor(Color.GREEN);
		
		Entity playerShip = TestGameMain.entities.get(0);
		
		//loop through all the positions at which we want to draw gridlines that run from left to right
		for(int i = 0; i < (backgroundBorderLength); i+=gridlineDistance)
		{
			//draw a gridline
			g.fillRect(i - ((int)playerShip.xPosition%gridlineDistance), 0, gridLineWidth, backgroundBorderLength);
		}
		
		//loop through all the positions at which we want to draw gridlines that run from
		//the top of the map to the bottom of the map
		for(int i = 0; i < (backgroundBorderLength); i+=gridlineDistance)
		{
			//draw a gridline from the top of the map to the bottom of the map
			g.fillRect(0, i - ((int)playerShip.yPosition%gridlineDistance), backgroundBorderLength, gridLineWidth);
		}
			
		//loop through the list of entities
	    for(int i = 0; i < TestGameMain.entities.size(); i ++)
	    {
    		 Entity curEnt = TestGameMain.entities.get(i);
    		 
    		 HitRadius firstRad = curEnt.hitRadii.get(0);
    		 //loop through the current entitiy's list of hit Radii (doing this is redundant
    		 //in the current version of the game because each entity has only one hit Radius)
    		 //but this is designed so that we can see all the hit Radii of entities if new
    		 //entities with multiple hitRadii are later added
	    	 for(int z = 0; z < curEnt.hitRadii.size(); z++)
	    	 {
	    		 HitRadius curRad = curEnt.hitRadii.get(z);
	    		 //if the current entity is not an explosion
	    		 if(curEnt.type!=TestGameMain.explosionNum)
	 	    	 {
	    			 //if the current entity is the player's ship
	    			 if(curEnt.type == TestGameMain.playerShipNum)
	    			 {
	    				 //draw a tiny black rectangle to represent the player's ship on the minimap at the coordinates
	    				 //of the minimap's center + (the player's ship's coordinates/minimapScaleConstant)
	    				 g.setColor(Color.CYAN);
	    				 g.fillRect(minimapShiftConstant+(int)(curEnt.xPosition/minimapScaleConstant)-(minimapItemSize/shiftCenterToTopLeftDivisor),minimapShiftConstant+(int)(curEnt.yPosition/minimapScaleConstant)-(minimapItemSize/shiftCenterToTopLeftDivisor),minimapItemSize,minimapItemSize);
	    			    	
	    			 }
	    			 //if the current entity is an enemy
	    			 if(curEnt.type == TestGameMain.enemyNum) {
	    				
		    			 //draw a tiny magenta rectangle to represent the current entity if it is an enemy (or a a bullet)
		    			 //determining exactly where to put it in the same way we determined where to put the player's ship
			    		 g.setColor(Color.RED);
			    		 g.fillRect(minimapShiftConstant+(int)(curEnt.xPosition/minimapScaleConstant)-(minimapItemSize/shiftCenterToTopLeftDivisor),minimapShiftConstant+(int)(curEnt.yPosition/minimapScaleConstant)-(minimapItemSize/shiftCenterToTopLeftDivisor),minimapItemSize,minimapItemSize);	
	    			 }
	    			 
	    			//if the current entity is a bullet
	    			if(curEnt.type == TestGameMain.bulletNum) {
	    				
	    				//draw a tiny magenta rectangle to represent the current entity if it is an enemy (or a a bullet)
		    			//determining exactly where to put it in the same way we determined where to put the player's ship
	    				g.setColor(Color.GREEN);
			    		g.fillRect(minimapShiftConstant+(int)(curEnt.xPosition/minimapScaleConstant)-(minimapItemSize/shiftCenterToTopLeftDivisor),minimapShiftConstant+(int)(curEnt.yPosition/minimapScaleConstant)-(minimapItemSize/shiftCenterToTopLeftDivisor),minimapItemSize,minimapItemSize);	
			    		curEnt.curBlastTime += 1;
	    			}
	    			 
	    			 //if we are about to draw the player's ship
	    			 if(i==0)
	    			 {
	    				 g.setColor(Color.BLUE);
	    			 }
	    			 //draw the current entity on the actual map in the same color as it was drawn on the minimap
	    			 //at coordinates equal to the coordinates of the center of the map + its coordinates-the player's ships coordinates
		    		 g.fillOval(TestGameMain.screenWidth/2  - curRad.radius + (int)curEnt.xPosition+(int)curRad.xPosition-(int)playerShip.xPosition, TestGameMain.screenHeight/2 - curRad.radius + (int)curEnt.yPosition+curRad.yPosition-(int)playerShip.yPosition, curRad.radius*2, curRad.radius*2);		    		
	 	    	 }
	    		 //if the current entity is an explosion
	    		 else
	 	    	 {
	    			//determine how big the explosion should be drawn using a concave down quadratic function for
	    			//explosion radius as a function of the amount of time the explosion has existed for
	    			double a = 2*(curEnt.maxBlastTime)*((curEnt.maxBlastSize-firstRad.radius)/(curEnt.maxBlastTime*curEnt.maxBlastTime));
	    			double b = ((curEnt.maxBlastSize-firstRad.radius)/(curEnt.maxBlastTime*curEnt.maxBlastTime));
	    		    int tempRadius = (int)((a*curEnt.curBlastTime)-(b*curEnt.curBlastTime*curEnt.curBlastTime))+firstRad.radius;
	 	    		g.setColor(Color.ORANGE);
	 	    		g.fillOval(TestGameMain.screenWidth/2+(int)curEnt.xPosition+(int)curRad.xPosition-(int)playerShip.xPosition-(tempRadius/shiftCenterToTopLeftDivisor), TestGameMain.screenHeight/2 + (int)curEnt.yPosition+curRad.yPosition-(int)playerShip.yPosition-(tempRadius/shiftCenterToTopLeftDivisor), tempRadius, tempRadius);
	 	    		curEnt.curBlastTime += 1;
	 	    	 } 
	    		 
	    	 }
	    }
	    //loop through the arrayList of spawners
	    for(int z = 0; z < TestGameMain.spawners.size(); z++)
    	{
	    	Spawner tempSpawner = TestGameMain.spawners.get(z);
	    	//draw the spawner on the actual map at the coordinates of the spawner
	    	//+ the coordinates of the center of the map - the coordinates of the player's ship
    		g.setColor(Color.YELLOW);
    		g.fillRect(TestGameMain.screenWidth/2-spawnerWidth/2+(int)tempSpawner.xPos-(int)TestGameMain.entities.get(0).xPosition, TestGameMain.screenHeight/2-spawnerWidth/2+(int)tempSpawner.yPos-(int)TestGameMain.entities.get(0).yPosition, spawnerWidth, spawnerWidth);
    		
    		//draw a little yellow rectangle to represent the spawner on the minimap draw it at
	    	//the coordinates of the center of the minimap + (the coordinates of the spawner/minimapScaleConstant)
    		g.fillRect(minimapShiftConstant+(int)(tempSpawner.xPos/minimapScaleConstant)-(minimapItemSize/shiftCenterToTopLeftDivisor),minimapShiftConstant+(int)(tempSpawner.yPos/minimapScaleConstant)-(minimapItemSize/shiftCenterToTopLeftDivisor),minimapItemSize,minimapItemSize);
    	}
	    
	    //if the game has not yet started
	    if(!TestGameMain.hasStarted)
	    {
	    	leftTextX = TestGameMain.screenWidth/2 + leftTextXShiftFromCenter;
	    	//display the instructions on how to play the game
	    	for(int i = 0; i < leftText.size(); i++)
	    	{
	    		g.setColor(Color.BLACK);
	    		g.fillRect(leftTextX-textBoxLeftBorder, leftTextY + (int)((i-textBoxShiftConstant)*textIncrement), leftTextWidth, textIncrement);
	    		
	    		g.setColor(Color.GREEN);
	    		g.drawString(leftText.get(i), leftTextX, leftTextY + (i*textIncrement));
	    	}
	    		    

	    }
	    
	    //if the player has lost the game
	    if(TestGameMain.hasLost)
 	    {
	    	//display the message telling them that they have lost and that
	    	//they need to open a new instance of the window to play again
	    	//display the instructions on how to play the game
	    	if(centerText.size()<2)
	    	{
		    	centerText.add("YOU LOSE!");
		    	centerText.add("close the gameplay window and then open a new instance of the program to play again");
	    	}

	    	for(int i = 0; i < centerText.size(); i++)
	    	{
	    		g.setColor(Color.BLACK);
	    		g.fillRect(centerTextX-textBoxLeftBorder, centerTextY + (int)((i-textBoxShiftConstant)*textIncrement), centerTextWidth, textIncrement);
	    		
	    		g.setColor(Color.RED);
	    		g.drawString(centerText.get(i), centerTextX, centerTextY + (i*textIncrement));
	    	}


 	    }
	    //if the player has won the game
	    if(TestGameMain.hasWon)
 	    {
	    	//display the message telling them that they have lost and that
	    	//they need to open a new instance of the window to play again
	    	//display the instructions on how to play the game
	    	if(centerText.size()<2)
	    	{
		    	centerText.add("YOU WIN!");
		    	centerText.add("close the gameplay window and then open a new instance of the program to play again");
	    	}

	    	for(int i = 0; i < centerText.size(); i++)
	    	{
	    		g.setColor(Color.BLACK);
	    		g.fillRect(centerTextX-textBoxLeftBorder, centerTextY + (int)((i-textBoxShiftConstant)*textIncrement), centerTextWidth, textIncrement);
	    		
	    		g.setColor(Color.BLUE);
	    		g.drawString(centerText.get(i), centerTextX, centerTextY + (i*textIncrement));
	    	}
 	    }
	}
}