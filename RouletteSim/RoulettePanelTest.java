// CS 0401 Fall 2018
// This program tests the RoulettePanel class, which is required
// for Assignment 4.
// Note how the RoulettePanel class is created and used below, and
// also note its behavior in the snapshots file TestRoulette.htm

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class RoulettePanelTest implements GameInterface
{
	private JFrame theWindow;
	private RoulettePanel theGame;
	private JButton startGame;
	private RoulettePlayer P;
	private final Font myFont = new Font("TimesRoman", Font.BOLD, 40);
	
	public RoulettePanelTest()
	{
		theWindow = new JFrame("Testing RoulettePanel");
		startGame = new JButton("Start Game");
		startGame.setFont(myFont);
		
		ActionListener theListener = new GameListener();
		startGame.addActionListener(theListener);
		
		// For this test program, we are creating a fixed Player every time
		// the program is run.  In a real game, we would have the player log in
		// and then play the game.  See RoulettePlayer specifications in 
		// Assignment 3.
		P = new RoulettePlayer("Herbert", "Weasel", 250.0, 50.0);
		
		// The overall layout is (1,3), even though only one component is added
		// here.  The full layout is utilized via the GameListener.
		theWindow.setLayout(new GridLayout(1,1));
		theWindow.add(startGame);

		theWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		theWindow.setSize(600, 150);
		theWindow.setVisible(true);
		
	}
	
	private class GameListener implements ActionListener
	{
		// When the "Start Game" button is clicked, a RoulettePanel
		// will be created and they it replace the button in the display.  The playing
		// of the game is then handled entirely within the RoulettePanel, using the
		// RoulettePlayer that was passed in.
		
		// Note that the final argument to the RoulettePanel constructor is the
		// RoulettePanelTest object itself.  This enables the RoulettePanel to call 
		// the gameOver() method when it is finished.  The gameOver() method is the 
		// sole method in GameInterface.  The parameter type for the last argument 
		// within the RoulettePanel constructor should be GameInterface rather than
		// RoulettePanelTest.  The idea is that if a different class also needed this
		// same RoulettePanel, it could use it as long as it also implements GameInterface.
		public void actionPerformed(ActionEvent e)
		{
			theGame = new RoulettePanel(P, RoulettePanelTest.this);
			theWindow.remove(startGame);
			theWindow.add(theGame);
			theWindow.pack();
		}
	}
	
	// This method will be called from the RoulettePanel when it is finished with the
	// game.  The method will then switch out the RoulettePanel switch in the
	// "Start Game" button.  Note that since the RoulettePlayer P was passed to the 
	// RoulettePanel, updates to the player from the RoulettePanel are accessible back 
	// here in the main program.
	public void gameOver()
	{
		theWindow.remove(theGame);
		theWindow.add(startGame);
		theWindow.setSize(600, 150);
		theWindow.repaint();
		JOptionPane.showMessageDialog(theWindow, "Back in Main Program\nPlayer:\n" + P.toString());
	}
	
	public static void main(String [] args)
	{
		// These calls will set the default fonts for JOptionPanes.
		UIManager.put("OptionPane.messageFont", new Font("TimesRoman", Font.BOLD, 35));
		UIManager.put("OptionPane.buttonFont", new Font("TimesRoman", Font.PLAIN, 35));
		UIManager.put("TextField.font", new Font("TimesRoman", Font.PLAIN, 35));
		new RoulettePanelTest();
	}
}