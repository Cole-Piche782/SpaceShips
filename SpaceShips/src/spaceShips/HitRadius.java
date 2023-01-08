package spaceShips;

//this class represents a single circle on an entity used to check if it collided with something
//currently no entities have more than one hit radius, but in later versions of the game I may add
//large entities such as bosses, and they may need multiple hitradii
public class HitRadius {
	
	public int xPosition = 0;
	public int yPosition = 0;
	public int radius = 0;
	
	public HitRadius(int x,int y, int r)
	{
		yPosition = y;
		xPosition = x;
		radius = r;
	}
}