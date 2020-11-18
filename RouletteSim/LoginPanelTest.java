// CS 0401 Fall 2018
// This program tests the LoginPanel class, which is required for Assignment 4
// Note how it is used and also note the snapshots shown in TestLogin.htm
// Note also that this program also requires working versions of the
// RoulettePlayer and RPList classes as required in Assignment 3

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class LoginPanelTest implements LoginInterface
{
	private JFrame theWindow;
	private RPList players;
	private RoulettePlayer P;
	private LoginPanel logPan;
	private JButton login;
	
	public LoginPanelTest()
	{
		theWindow = new JFrame("Testing Login Panel");
		theWindow.setLayout(new GridLayout(1,2));
		players = new RPList("players.txt");
		
		// Note that the TestLoginPanel object is passed as an argument to
		// the LoginPanel.  Since TestLoginPanel implements LoginInterface
		// it can be accessed in that way from LoginPanel.  In other words,
		// the header for this constructor should be:
		// public LoginPanel (RoulettePlayerList pl, LoginInterface L)
		logPan = new LoginPanel(players, this);
		
		login = new JButton("Login");
		login.setFont(new Font("Serif", Font.ITALIC + Font.BOLD, 35));
		login.addActionListener(new LoginListener());
		
		theWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		theWindow.add(login);
		theWindow.pack();
		theWindow.setVisible(true);
	}

	// This method is from LoginInterface and will be called from the
	// LoginPanel.  The idea is that information obtained from LoginPanel
	// will be encapsulated within that Panel, and this method enables the
	// player to be passed back to this program once the login is complete.
	// Once we have the player set we can remove the LoginPanel and add the
	// button back for the next login.
	public void setPlayer(RoulettePlayer newPlayer)
	{
		P = newPlayer;
		theWindow.remove(logPan);
		theWindow.add(login);
		theWindow.pack();
		JOptionPane.showMessageDialog(theWindow, "Player: " + P.toString());
	}
		
	// When the login button is clicked we add the LoginPanel to the window
	// and allow it to "do its job".  When it is finished, it will pass the
	// new Player back with the setPlayer method.  For details on the expected
	// functionality of LoginPanel, see the snapshot file LoginPanelTest.htm
	private class LoginListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			theWindow.remove(login);
			theWindow.add(logPan);
			theWindow.pack();
		}
	}
	
	public static void main(String [] args)
	{
		UIManager.put("OptionPane.messageFont", new Font("TimesRoman", Font.BOLD, 35));
		UIManager.put("OptionPane.buttonFont", new Font("TimesRoman", Font.PLAIN, 35));
		UIManager.put("TextField.font", new Font("TimesRoman", Font.PLAIN, 35));
		new LoginPanelTest();	
	}
}