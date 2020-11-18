// CS 0401 Fall 2018
// 
// This program tests the Assignment 4 required classes in a simple
// graphical implementation of the Roulette Game.  It depends on the following
// classes:
// 		RoulettePlayer [same spec from Assignment 3]
// 		RPList [same spec from Assignment 3]
//		LoginPanel [you must write new for this project]
//		RoulettePanel [you must write new for this project]
//			Note that RoulettePanel will encapsulate a RouletteWheel object, which
//			in turn will encapsulate an array of RouletteSquare objects.  You must
//			also complete these classes.
// See more comments below.

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Assig4 implements LoginInterface, GameInterface
{
	// Note the variables -- we have variables for the window and all of
	// the various panels.  Not all of these will be visible at the same
	// time however.  We also have 3 buttons which will initially be shown
	// to the user.
	private JFrame theWindow;
	private RoulettePanel theGame;
	private LoginPanel theLogin;
	private JPanel buttonPanel;
	private JButton quitButton, loginButton, playButton;
	private RoulettePlayer P;
	private RPList PL;
	
	public Assig4()
	{
		theWindow = new JFrame("CS 0401 Graphical Roulette");
		PL = new RPList("players.txt");

		loginButton = new JButton("Player Login");
		loginButton.setFont(new Font("Serif", Font.BOLD, 35));
		playButton = new JButton("Start Game");
		playButton.setFont(new Font("Serif", Font.BOLD, 35));
		playButton.setEnabled(false); // cannot play until we have a Player
		quitButton = new JButton("Quit");
		quitButton.setFont(new Font("Serif", Font.BOLD, 35));
		
		buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridLayout(3,1));
		buttonPanel.add(quitButton);
		buttonPanel.add(loginButton);
		buttonPanel.add(playButton);
		
		ActionListener theListener = new GameListener();
		quitButton.addActionListener(theListener);
		loginButton.addActionListener(theListener);
		playButton.addActionListener(theListener);
		
		theWindow.setLayout(new GridLayout(1,3));
		theWindow.add(buttonPanel);

		theWindow.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		theWindow.setSize(400, 400);
		theWindow.setVisible(true);
		
	}
	
	// This single listener will handle the 3 buttons in the buttonPanel.  Note
	// the logic.  Initially, playButton is not enabled because there is no
	// player.  loginButton creates a LoginPanel to allow a new RoulettePlayer
	// to login.  That panel will call setPlayer() when it finishes, which
	// will enable the playButton for that player.   playButton will create
	// the RoulettePanel so that the current RoulettePlayer can play the
	// game.  When the RoulettePlayer finishes, the RoulettePanel will call
	// the gameOver() method.  This will reset the buttons so that a new
	// RoulettePlayer will have to login to play.  It will also set the 
	// RoulettePlayer to null (so even if the same player wants to play twice
	// in a row, he / she would have to login again to do it).
	// quitButton will save the RPList back to the file before quitting the
	// program.
	private class GameListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			if (e.getSource() == loginButton)
			{
				if (P != null)
				{
					JOptionPane.showMessageDialog(theWindow, "Player " + P.getName() +
						" quitting without playing ");
				}
				// Create the LoginPanel and add it to the window.
				theLogin = new LoginPanel(PL, Assig4.this);
				theWindow.remove(buttonPanel);
				theWindow.add(theLogin);
				theWindow.pack();
			}
			else if (e.getSource() == playButton)
			{
				// Create the RoulettePanel and add it to the window.
				theGame = new RoulettePanel(P, Assig4.this);
				theWindow.remove(buttonPanel);
				theWindow.add(theGame);
				theWindow.pack();
			}
			else if (e.getSource() == quitButton)
			{
				if (P != null)
				{
					JOptionPane.showMessageDialog(theWindow, "Player " + P.getName() +
						" quitting without playing ");
				}
				PL.saveList();
				JOptionPane.showMessageDialog(theWindow, "Overall results:\n" + PL.toString());
				System.exit(0);
			}		
		}
	}
	
	// This method will be called from the LoginPanel after the RoulettePlayer
	// has been selected.  This allows the RoulettePlayer to be passed back to
	// this program.
	public void setPlayer(RoulettePlayer pl)
	{
		P = pl;
		playButton.setEnabled(true);
		playButton.setText(P.getName() + " Start Game ");
		theWindow.remove(theLogin);
		theWindow.add(buttonPanel);
		JOptionPane.showMessageDialog(theWindow, "Ready to play with \n" +
			P.toString());
		theWindow.setSize(400,400);
	}
	
	// This method will be called from the RoulettePanel when it is finished with the
	// game.  It removes the RoulettePanel, adds back the buttonPanel and sets the
	// RoulettePlayer P back to null.
	public void gameOver()
	{
		theWindow.remove(theGame);
		theWindow.add(buttonPanel);
		P = null;
		playButton.setText("Start Game");
		playButton.setEnabled(false);
		theWindow.setSize(400,400);
		theWindow.repaint();
		
	}
	
	public static void main(String [] args)
	{
		UIManager.put("OptionPane.messageFont", new Font("TimesRoman", Font.BOLD, 35));
		UIManager.put("OptionPane.buttonFont", new Font("TimesRoman", Font.PLAIN, 35));
		UIManager.put("TextField.font", new Font("TimesRoman", Font.PLAIN, 35));
		new Assig4();
	}
}