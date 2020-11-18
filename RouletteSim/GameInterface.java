// CS 0401 Fall 2018
// This interface is used by the RoulettePanel to make a call back to the object
// that initiated it.  See how the initiating object is passed into the RoulettePanel
// and see the code for it in RoulettePanelTest.java.

public interface GameInterface
{
	public void gameOver();
}