package spaceShips;

import java.util.ArrayList;
import java.util.Scanner;
import java.lang.Math;
import javax.swing.JFrame;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.TimerTask;
public class Entity {
	
	//this variable is no longer used
	public int millisPerXUpdate = 0;
	//the following variable indicates what kind of entity the current entity is.
	//1 means player's ship, 2 means enemy, 3 means explosion, and 4 meant bullet 
	//back in the version of the game where the player's ship had a gun
	public int type = 1;
	//this variable is no longer used
	public int millisSinceLastXUpdate = 0;
	
	
	public double xVelocity = 0;
	public double xPosition = 0;
	public double yVelocity = 0;
	public double yPosition = 0;
	
	//these variables are only relevant for explosions, and it tells the canvas how long the explosions has been in 
	//existence for so that the canvas knows how big to draw the explosion (because the explosions are drawn bigger
	//or smaller depending how long they have been in existence for)
	//curBlastTime is also relevant for bullets so the game can erase them once they have existed for 6 seconds
	public double curBlastTime = 0;
	public double initialBlastSize = 0;
	public double maxBlastSize = 0;
	public double maxBlastTime = 0;
	
	
	
	//this arrayList is intended to hold all of entities hitRadii. However, in the recent versions of
	//the game, because I only had each entity have one hitRadius, the code make an entity with multiple
	//hitRadii work probperly isn't really in place anyways.
	public ArrayList<HitRadius> hitRadii = new ArrayList<HitRadius>();
	
	//simple constructor method for an entity with one hitRadius
	public Entity(ArrayList<HitRadius> rs, int x,int y)
	{
		yPosition = y;
		xPosition = x;
		hitRadii = rs;
	}
	
	//a method I call from enemies when they are destroyed. 
	//it saves from having to manually type out all the stuff enclosed.
	public void makeIntoAnExplosion()
	{
		type = 3;
		initialBlastSize = hitRadii.get(0).radius;
		maxBlastSize = initialBlastSize*2;
		maxBlastTime = 120;
	
	}
}