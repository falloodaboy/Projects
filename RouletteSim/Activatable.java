// CS 0401 Fall 2018
// Interface to allow a RoulettePanel to interact with its
// encapsulated RouletteWheel.  This method will be called
// by the RouletteWheel after a spin() to indicate that the spin
// has been completed.  For details see WheelTest.java. 
public interface Activatable
{
	public void activate();
}