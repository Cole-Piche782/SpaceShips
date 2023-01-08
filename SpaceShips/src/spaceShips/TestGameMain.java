package spaceShips;
import java.util.ArrayList;
import java.lang.Math;
import javax.swing.JFrame;

import java.awt.BorderLayout;
import java.awt.MouseInfo;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;


public class TestGameMain extends JFrame implements MouseListener, MouseMotionListener, KeyListener
{
  //on the line following these comments, I declare an array list of arrays of instances of a class I wrote called entity
  //these arrays will always have a length of exactly two, as these arrays are meant to hold pairs of entities that have
  //recently collided
  public static ArrayList<Entity[]> collisions = new ArrayList<Entity[]>();
  
  //an arrayList designed to hold all the instances of the class entity
  public static ArrayList<Entity> entities = new ArrayList<Entity>();
  
  //an arrayList designed to hold all the instances of the class spawner
  public static ArrayList<Spawner> spawners = new ArrayList<Spawner>();
  
  //on the line following these comments, I declare a final double meant to represent the amount by which the missile speed changes
  //each time it is changed, in other words the missile acceleration rate
  public static final double missleAcceleration = 0.12;

  //tell the computer if the player has Started the game yet
  public static boolean hasStarted = false;
  //tell the computer if the player has lost the game yet
  public static boolean hasLost = false;
  //tell the computer if the player has won the game yet
  public static boolean hasWon = false;
  
  //on the line following these comments, I declare a variable meant to tell the computer what the time was in 
  //milliseconds since the last time the computer checked
  public static long lastTime = 0;
  
  //on the line following these comments, I declare a variable meant to tell the computer what time is currently
  //on the computer's clock in milliseconds
  public static long now = 0;
  
  //on the line following these comments, I declare a variable meant to tell the computer what the character was 
  //on the last letter key that was pressed (e.g. g if the g key was last pressed)
  public static char ch = 'z';
  
  //the type number that indicates that an entity is an enemy
  public static final int enemyNum = 2;
  
  //the type number that indicates that an entity is a bullet
  public static final int bulletNum = 4;
  
  //the type number that indicates that an entity is the player's ship
  public static final int playerShipNum = 1;
  
  //the type number that indicates that an entity is an explosion
  public static final int explosionNum = 3;
  
  //just a constant to track how large the hit radii are
  public static final int shipRad = 10;

  //where the player starts
  private static final int playIniX = 200;
  private static final int playIniY = 100;
  
  //where the first enemy starts
  private static final int enIniX = 0;
  private static final int enIniY = 300;
  
  //where the first spawner can appear
  private static final int spawner1PosRange = 2000;
  private static final int spawner1Center = -1000;
  
  //the number of milliseconds that go by between all entities positions being updated
  private static final int milliSecondsPerMove = 17;
  
  //the speed at which the player's ship accelerates
  private static final int playerAccelerationRate = 2;
  
  //get a random number between 0 and the below constant to determine if a given spawner spawns an enemy
  private static final int spawnerRandomCoefficient = 10000;
  
  //a given spawner spawns an enemy if the random number generated is less than the below
  private static final int spawnerMaxSpawnValue = 20;
  
  //the dimensions of the game window
  public static final int initialScreenWidth = 1200;
  public static final int initialScreenHeight = 600;
  public static int screenWidth = initialScreenWidth;
  public static int screenHeight = initialScreenHeight;
  
  //whether the player enabled the gun
  private static boolean gunEnabled = false;
  
  //how far the mouse was clicked from the values returned by the methods I use to determine where it was clicked
  private static final int mouseShiftY = 565;
  private static final int mouseShiftX = 979;
  
  //how long bullets should stay on the map before they fade away
  private static final int bulletExistenceTime = 3000; 
  
  //how large the explosions should start before they have time to expand
  private static final int initialExplosionSize = 20;
  
  public static void resetAndStartTimer()
  {
	  lastTime = System.currentTimeMillis();
	  now = System.currentTimeMillis();
  }
  
  //if 0 is input, return 1, if 1 is input, return 0
  //precondition: first is equal to either 0 or 1
  private static int getOtherInt(int first)
  {
	  return -1 * first + 1;
  }
  
  //handles the case where two entities crash by 
  //removing them from the list and putting an explosion
  //where they both used to be
  private static void handleCrash(Entity en, Entity other)
  {
	  TestGameMain.entities.remove(en);
	  TestGameMain.entities.remove(other);
	 
	  HitRadius hitRadiusOne = new HitRadius(0,0,initialExplosionSize);
      ArrayList<HitRadius> temp = new ArrayList<HitRadius>();
      temp.add(hitRadiusOne);
      Entity entNew = new Entity(temp,(int)en.xPosition,(int)other.yPosition);
      entNew.xVelocity = 0;
      entNew.makeIntoAnExplosion();
      TestGameMain.entities.add(entNew);
  }
  
  //removes bullets and explosions that shouldn't be around anymore
  private static void removeExpiredBullets()
  {
	  for(int i = entities.size() - 1; i > -1; i--)
	  {
		  Entity tempEnt = entities.get(i);
		  if(tempEnt.type == bulletNum)
		  {
			  if(tempEnt.curBlastTime > bulletExistenceTime)
			  {
				  entities.remove(i);
			  }
		  }
	  }
  }
  
 
  //this method loops through the arrayList called collisions and does different things to the entities in
  //the arraylists based on what the value of their type field is. e.g. removing enemies from the entities list if they collide
  //or setting hasLost to true if the player ship collides with an enemy.
  public static void handleAllCollisions()
  {
	  //loop through collisions
	  for(int i = TestGameMain.collisions.size()-1; i >=0; i --)
	  {
		  //filter out instances of null from collisions
		  while(TestGameMain.collisions.contains(null))
		  {
			  TestGameMain.collisions.remove(null);
		  }
		  for(int z = 0; z < TestGameMain.collisions.get(i)[1].hitRadii.size(); z++)
		  {
			  ArrayList<Entity> tempEnts = new ArrayList<Entity>();
			  tempEnts.add(TestGameMain.collisions.get(i)[0]);
			  tempEnts.add(TestGameMain.collisions.get(i)[1]);
	    		 
			  //track if one of the colliding entites is an enemy, because if neither are enemies then we don't have to do anything
			  boolean enemyCrash = false;
	    		 
			  //index of the entity of the enemy type in the collision
			  int enIndex = 0;
	    		 
			  //index of the entity of the non-enemy type in the collision
			  int otherIndex = 1;
	    		 
			  //figure out which entity in the collision is an enemy if either
			  for(int m = 0; m < tempEnts.size(); m++)
			  {
				  if(tempEnts.get(m).type==enemyNum)
				  {
					  enemyCrash = true;
					  enIndex = m;
					  otherIndex = getOtherInt(m);
				  }
			  }
		  	  if(enemyCrash)
    		  {
		  		  //the entity of the enemy type in collisions
		  		  Entity en = tempEnts.get(enIndex);
	    		 
		  		  //the entity of the more common type in collisions
		  		  Entity other = tempEnts.get(otherIndex);
	    		 
		  		  //if the enemy collided with the player
		  		  if(other.type == playerShipNum)
		  		  {
		  			  TestGameMain.hasLost = true;
		  		  }
    			 
		  		  //if the following conditional statement proves true, both entities from the current collision
		  		  //are removed from the list of entities, because they are two enemies that collided
		  		  if(other.type == enemyNum)
		  		  {
		  			  handleCrash(en, other);
		  		  }
    			 
		  		  //test if bullets collided with enemies
		  		  if(other.type==bulletNum)
		  		  {
		  			  handleCrash(en, other);
		  		  }
    		  }    
		  }    
		  TestGameMain.collisions.remove(TestGameMain.collisions.size()-1);
	    }      
  }
  
  //the following method is called each time the entities' coordinates are updated
  //it loops through the arrayList of entities
  //and for each entity that is a missile, it changes that entity's velocity
  public static void handleAllMissleChoices()
  {
	  //loop through all entities
	  for(int i = 0; i < entities.size(); i++)
	  {
		  	  Entity playerShip = entities.get(0);
		  	  Entity tempEnt = entities.get(i);
		  	  //if and only if the current entity is a missile
			  if(tempEnt.type==enemyNum)
			  {
				  //determine how far apart the enemies are on each axis
				  double xDis = (playerShip.xPosition + playerShip.hitRadii.get(0).xPosition)-(tempEnt.xPosition+tempEnt.hitRadii.get(0).xPosition);
				  double yDis = (playerShip.yPosition + playerShip.hitRadii.get(0).yPosition)-(tempEnt.yPosition + tempEnt.hitRadii.get(0).yPosition);
				  	
				  	//if the enemny and player are not already aligned on the x axis
				  	//and the enemy is moving away from the player on the x axis
				  	//this includes the case where the player is already aligned with the enemy
				  	//on the y axis
				    if(xDis!=0&&(tempEnt.xVelocity/xDis<=0))
				    {
				    		
					    	//if the enemy is to the right of the player
					    	if(xDis<0)
							  {
								  tempEnt.xVelocity -= missleAcceleration;
							  }
							  else
							  {
								  tempEnt.xVelocity += missleAcceleration;
							  }
					    
				    }
				    //if the enemy is not already aligned with the player on the y axis
				    //and is moving away from the player on the y axis, and is either:
				    //moving towards the player on the x axis or:
				    //aligned with the player on the x axis
				    else if(yDis!=0&&(tempEnt.yVelocity/yDis<=0))
				    {	
				    	//the following if statement is redundant and useless :)
				    	if(yDis!=0)
					    {
					    	//if the enemy is below that player's ship
					    	if(yDis<0)
						    {
					    		tempEnt.yVelocity -= missleAcceleration;
						    }
					    	//if the enemy is above the player's ship
							  else
							  {
								  tempEnt.yVelocity += missleAcceleration;
							  }
					    }
				    }
				    
				    else
				    {
				    	double requiredSlope = yDis/xDis;
				    	double actualSlope = tempEnt.yVelocity/tempEnt.xVelocity;
				    	//if the slope of the velocity vector is lower than the
				    	//slope of a line drawn from the enemy to the player
				    	if(requiredSlope>actualSlope)
				    	{
				    		//if the velocity slope is too low and the target is to the
				    		//right of the missile
				    		if(playerShip.xPosition>tempEnt.xPosition)
				    		{
				    			//we are now moving either right too slowly and downwards (with a positive slope on
				    			//a non cartesian java plane) too slowly or 
				    			//to the right too slowly and upwards into Q1 too quickly
				    			
				    			//of we are going into Q4 too slowly
				    			if(tempEnt.yVelocity>0)
				    			{
				    				//go down faster
				    				tempEnt.yVelocity+=missleAcceleration;
				    			}
				    			else if(tempEnt.yVelocity<0)
				    			{
				    				//go right faster 
				    				tempEnt.xVelocity+=missleAcceleration;
				    			}
				    		}
				    		//if the velocity slope is too low and the target is to the
				    		//left of the missile
				    		else if(playerShip.xPosition<tempEnt.xPosition)
				    		{
				    			//We are now either going left too slowly and up too quickly
				    			//or we are going left to quickly and down too slowly
				    			//if we are going leftwards too quickly
				    			if(tempEnt.yVelocity<0)
				    			{
				    				//go up faster
				    				tempEnt.yVelocity-=missleAcceleration;
				    			}
				    			//if we are going downwards too quickly
				    			else if(tempEnt.yVelocity>0)
				    			{
				    				//go left faster
				    				tempEnt.xVelocity-=missleAcceleration;
				    			}
				    		}
				    	}
				    	//if the slope of the velocity vector is greater than thed
				    	//slope of a line drawn from the enemy to the player
				    	else if(requiredSlope<actualSlope)
				    	{
				    		//if the target is to the right of the missile
				    		if(playerShip.xPosition>tempEnt.xPosition)
				    		{
				    			//we are now either moving right too slowly and down too quickly
				    			//or we are moving right too quickly and up too slowly
				    			
				    			//if we are moving down
				    			if(tempEnt.yVelocity<0)
				    			{
				    				//accelerate up
				    				tempEnt.yVelocity-=missleAcceleration;
				    			}
				    			//if we are moving down
				    			if(tempEnt.yVelocity>0)
				    			{
				    				//accelerate right
				    				tempEnt.xVelocity+=missleAcceleration;
				    			}
				    		}
				    		
				    		//if we get to this point, it means we are moving directly towards the target
				    		{
				    			//if we are moving directly towards the target and the target is below us
				    			if(tempEnt.yVelocity>0)
				    			{
				    				//accelerate down
				    				tempEnt.yVelocity+=missleAcceleration;
				    			}
				    			//if we are moving directly towards the target and the target is above us
				    			if(tempEnt.yVelocity<0)
				    			{
				    				//accelerate up
				    				tempEnt.xVelocity-=missleAcceleration;
				    			}
				    		}
				    	}
				    }
			  }
		}
  }
  
  public static long getMilliSecondsGoneBy()
  {
	  now = System.currentTimeMillis();
	  return now-lastTime;
  }
  
  //the following method loops through the arrayList of entities, and for each entity, loops through the arrayList
  //of entities again to see if said entity has collided with any other entity.
  //if it has, pack the two entities into an entity array, and add this array to collisions
  public static void testForAllCollisions()
  {
	  //loop through the entities
	  for(int i = 0; i < entities.size(); i++)
	  {
		  Entity tempEnt1 = entities.get(i);
		  for(int x = 0; x < tempEnt1.hitRadii.size();x++)
		  {
			  HitRadius tempRad1 = tempEnt1.hitRadii.get(x);
			  //loop through the entities
			  for(int z = i; z < entities.size(); z++)
			  {
				  Entity tempEnt2 = entities.get(z);
				  //if we aren't currently trying to test if an entity collided with itself
				  if(z!=i)
				  {
					  for(int k = 0; k < tempEnt2.hitRadii.size();k++)
					  {
						  HitRadius tempRad2 = tempEnt2.hitRadii.get(k);
						  //determine the distance between the two entities along each axis
						  double xDis = Math.abs((tempEnt1.xPosition+tempRad1.xPosition) - (tempEnt2.xPosition+tempRad2.xPosition));
						  double yDis = Math.abs((tempEnt1.yPosition + tempRad1.yPosition) - (tempEnt2.yPosition + tempRad2.yPosition));
						     
						 //calculate the maximum possible distance that can exist between the two entities
						 //at which the two entities still collided
					     double combinedRadius = tempRad1.radius + tempRad2.radius;
					     
					     //calculate the distance between the two entities.
					     double distance = Math.sqrt((xDis*xDis)+(yDis*yDis));
					     //if the distance between the entities < the max possible distance between them at which they collided
					     if(combinedRadius > distance)
					     {
					    	 //copy the two entities into an entity array and append the array to the
					    	 //arrayList called collisions
					    	 Entity[] col = {tempEnt1, tempEnt2}; 
					    	 collisions.add(col);
					     }
					  }
				  }
			  }
		  }
	  }
	  //now, we proceed to test if any spawners have collided with the player ship
	  //loop through the arrayList of spawners
	  for(int i = 0; i < spawners.size(); i++)
	  {
		  Spawner tempSpawner = spawners.get(i);
		  //loop through the arrayList of entities to see if the spawner collided with a bullet or the player's ship
		  for(int j = 0; j < entities.size(); j++)
		  {
			  Entity tempEnt = entities.get(j);
			  handleSpawnerInteraction(tempSpawner, tempEnt, i);
		  }   
	  }
  }
  
  private static void handleSpawnerInteraction(Spawner tempSpawner, Entity tempEnt, int i)
  {
	  if(tempEnt.type == playerShipNum || tempEnt.type == bulletNum)
	  {
		  //determine the distance between the current spawner and the player ship along both axies
		  double xDis = Math.abs(tempEnt.xPosition-tempSpawner.xPos);
		  double yDis = Math.abs(tempEnt.yPosition-tempSpawner.yPos);
		  double distance = Math.sqrt((xDis*xDis)+(yDis*yDis));
		  
		  //if the current spawner has collided with the current entity
		  if(distance<4*entities.get(0).hitRadii.get(0).radius)
		  {
			  //remove the spawner from the list of spawners
			 spawners.remove(i); 
			 
			 //if there are no spawners left in the game
			 if(spawners.size()==0)
			 {
				 //the player wins
				 hasWon=true;
			 }
			 i--;
			 
			 HitRadius hitRadiusOne = new HitRadius(0,0,initialExplosionSize);
		     ArrayList<HitRadius> temp = new ArrayList<HitRadius>();
		     temp.add(hitRadiusOne);
		     Entity entNew = new Entity(temp,(int)(tempEnt.xPosition),(int)(tempEnt.yPosition));
		     entNew.xVelocity = 0;
		     entNew.makeIntoAnExplosion();
		     TestGameMain.entities.add(entNew);
			  
		  }
	  }
  }
  
  
  private static void setUpFrame(TestGameMain frame, Canvas thePanel)
  {
      thePanel.setDoubleBuffered(true);
      frame.add(thePanel,BorderLayout.CENTER);
      
      frame.addMouseListener(frame);
	  frame.addMouseMotionListener(frame);
	  frame.addKeyListener(frame);
      
	  frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	  frame.setSize(screenWidth, screenHeight);
      frame.setResizable(true);
      
      //the next line makes it display in the center of the screen
      frame.setLocationRelativeTo(null);
	    
	  frame.setVisible(true);
  }
  
  private static void createPlayerShip()
  {
      HitRadius hitRadiusOne = new HitRadius(0,0,shipRad);
      ArrayList<HitRadius> temp = new ArrayList<HitRadius>();
      temp.add(hitRadiusOne);
      Entity entNew = new Entity(temp, playIniX, playIniY);
      entNew.xVelocity = 0;
      entNew.type = playerShipNum;
      entities.add(entNew);
  }
  
  private static void createFirstEnemy()
  {
      HitRadius hitRadiusThree = new HitRadius(0,0,shipRad);
      ArrayList<HitRadius> temp2 = new ArrayList<HitRadius>();
      temp2.add(hitRadiusThree);
      Entity entNew = new Entity(temp2, enIniX, enIniY);
      entNew.xVelocity = 0;
      entNew.type = enemyNum;
      entities.add(entNew);
  }
  
  //spawn between 1 and 5 spawners at random locations
  private static void createAllSpawners()
  {
      int r2 = (int)(Math.random()*5)+1;
      for(int i= 0; i < r2; i++)
      {
    	  double r3 = (int)(Math.random()*spawner1PosRange)-spawner1Center; 
    	  double r4 = (int)(Math.random()*spawner1PosRange)-spawner1Center; 
      Spawner theGenerator = new Spawner(r3,r4);
      spawners.add(theGenerator);
      }
  }
  
  private static void testForPressedKeys()
  {
	  //if the player hit p, start the game with the gun disabled
	  if(ch=='p')
	  {
		  hasStarted = true;
		  ch ='z';
	  }
	  
	  //if the player hit o, start the game with the gun enabled
	  if(ch=='i')
	  {
		  hasStarted = true;
		  gunEnabled = true;
		  ch ='z';
	  }
	  
	  if(ch=='a')
	  {
		  entities.get(0).xVelocity-=playerAccelerationRate;
		  ch = 'z';
	  }
	  if(ch=='d')
	  {	
		  entities.get(0).xVelocity+=playerAccelerationRate;
		  ch = 'z';
	  }
	  if(ch=='s')
	  {
		  entities.get(0).yVelocity+=playerAccelerationRate;
		  ch = 'z';
	  }
	  if(ch=='w')
	  {
		  entities.get(0).yVelocity-=playerAccelerationRate;
		  ch = 'z';
	  }
  }
  
  private static void handleSpawns()
  {
	  //loop through the spawners, and for each one, make there be a 1 in
	  //500 chance that it spawns an enemy
	  for(int i = 0; i < spawners.size(); i++)
	  {
		  int r = (int)(spawnerRandomCoefficient*Math.random());
		  if(r<spawnerMaxSpawnValue)
		  {
			  HitRadius hitRadiusNew = new HitRadius(0,0,shipRad);
		      ArrayList<HitRadius> tempNew = new ArrayList<HitRadius>();
		      tempNew.add(hitRadiusNew);
		      Entity entNew = new Entity(tempNew,(int)spawners.get(i).xPos,(int)spawners.get(i).yPos);
		      entNew.xVelocity = 0;
		      entNew.type = enemyNum;
		      entities.add(entNew);
		  }
	  }
  }
  
  private static void incrementAllEntityPositions()
  {
	  for(int i = 0; i < entities.size(); i++)
	  {
		  entities.get(i).xPosition+=entities.get(i).xVelocity;
		  entities.get(i).yPosition+=entities.get(i).yVelocity; 
	  }
  }

  private static void updateScreenSize(TestGameMain frame)
  {
	  screenWidth = frame.getSize().width;
	  screenHeight = frame.getSize().height;
  }
  
  public static void main(String[] args)
  {
      //create an instance of the main class and set it up so that it can be drawn on
      TestGameMain frame = new TestGameMain();
      
      Canvas thePanel = new Canvas();
      
      setUpFrame(frame, thePanel);
      
      resetAndStartTimer();
      
      //create the player's ship
      createPlayerShip();
      
      //create the first enemy
      createFirstEnemy();
      
      //create between 1 and 5 spawners
      createAllSpawners();
      
      //this is where the game begins
      while(true)
      {
    	  //this is the loop that checks if
    	  //the player hit any keys
    	  while(getMilliSecondsGoneBy()<milliSecondsPerMove||!hasStarted||hasLost||hasWon)
    	  {
    		    testForPressedKeys();
    		    thePanel.repaint();
    	  }
    	  	
    	  resetAndStartTimer();
    	  handleSpawns();
    	  
    	  //the following code handles making the missiles aim at and rush their target
    	  handleAllMissleChoices();
    	  
    	  //increment each entity's position
    	  incrementAllEntityPositions();
    	  
    	  testForAllCollisions();
    	  handleAllCollisions();  
    	  
    	  removeExpiredBullets();
    	  
    	  updateScreenSize(frame);
      }     
   }
  		//this is the method that is called if the user presses a key
        @Override
	    public void keyPressed(KeyEvent e) {
			ch = e.getKeyChar();
		}

		@Override
		public void keyTyped(KeyEvent e) {
			
		}

		@Override
		public void keyReleased(KeyEvent e) {
			  
			
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mousePressed(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			  if((gunEnabled == true) && (!(hasLost||hasWon)))
			  {
				  HitRadius hitRadiusNew = new HitRadius(0,0,5);
			      ArrayList<HitRadius> tempNew = new ArrayList<HitRadius>();
			      tempNew.add(hitRadiusNew);
			      Entity entNew = new Entity(tempNew,(int)entities.get(0).xPosition,(int)entities.get(0).yPosition);
			      
			      double yRetCoord = MouseInfo.getPointerInfo().getLocation().y-mouseShiftY;
			      double xRetCoord = MouseInfo.getPointerInfo().getLocation().x-mouseShiftX;
			      
			      double m = (yRetCoord)/((xRetCoord));
			      
			      //speed of the bullets
			      double r = 16;
			      entNew.xVelocity = Math.sqrt((r*r)/(m*m+1));
			      
			      entNew.yVelocity = Math.sqrt((r*r)-(entNew.xVelocity*entNew.xVelocity));
			      if(yRetCoord<0)
			      {
			    	  entNew.yVelocity*=-1;
			      }
			      if(xRetCoord<0)
			      {
			    	  entNew.xVelocity*=-1;
			      }
			      entNew.xVelocity+=entities.get(0).xVelocity;
			      entNew.yVelocity+=entities.get(0).yVelocity;
			      entNew.type = bulletNum;
			      entities.add(entNew);
			  }     
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseExited(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}
    }