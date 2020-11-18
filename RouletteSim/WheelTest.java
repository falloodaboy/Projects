// CS 401 Fall 2018
// Test program for your RouletteWheel class.  Your class should run with this program
// and the output should be similar to that shown in WheelTest.htm.  See also a
// partial spec of the RouletteWheel class in the file RouletteWheel.java.  You must
// complete this class so that it runs correctly.

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;  

public class WheelTest implements Activatable  // Activatable is an interface
{						// with one method: public void activate().  This is
						// used by the RouletteWheel to tell this program that
						// it has finished spinning.  We need a method like this
						// since a spin of the wheel is asynchronous -- we don't
						// know how long it will take so we wait for a signal to
						// indicate that it has finished.  See more details about
						// this below.

	private JFrame theWindow;
	private JPanel buttonPanel;
	private RouletteWheel theWheel;
	private RouletteBet [] theBets;
	private JButton setButton, checkButton, spinButton;
	private final Font myFont = new Font("TimesRoman", Font.BOLD, 40);
	
	public WheelTest()
	{
		theWindow = new JFrame("WheelTest Program");
		theWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		ActionListener listen = new ButtonListener();
		
		setButton = new JButton("Set Wheel");  
		setButton.setFont(myFont);
		setButton.addActionListener(listen);
		
		spinButton = new JButton("Spin Wheel");  
		spinButton.setFont(myFont);
		spinButton.addActionListener(listen);
		spinButton.setEnabled(false);
		
		checkButton = new JButton("Check Results");  
		checkButton.setFont(myFont);
		checkButton.addActionListener(listen);
		checkButton.setEnabled(false);
		
		initBets();  // See below
		
		buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridLayout(1,3));
		buttonPanel.add(setButton);
		buttonPanel.add(spinButton);
		buttonPanel.add(checkButton);
	
		// Pass "this" object into the RouletteWheel.  This will enable the
		// RouletteWheel to call the activate() method when appropriate (see
		// below).  To allow this your RouletteWheel constructor should have
		// a parameter of type Activatable
		theWheel = new RouletteWheel(this);
		theWindow.add(theWheel, BorderLayout.CENTER);
		theWindow.add(buttonPanel, BorderLayout.SOUTH);

		theWindow.pack();
		theWindow.setVisible(true);
	}
	
	// Method to initialize some bets.  These will simply be used to test the
	// checkBet() method in the RouletteWheel.  See more details below.
	public void initBets()
	{
		theBets = new RouletteBet[8];
		theBets[0] = new RouletteBet(RBets.Value, "13");
		theBets[1] = new RouletteBet(RBets.Value, "24");
		theBets[2] = new RouletteBet(RBets.Color, "Red");
		theBets[3] = new RouletteBet(RBets.Color, "Black");
		theBets[4] = new RouletteBet(RBets.Parity, "Even");
		theBets[5] = new RouletteBet(RBets.Parity, "Odd");
		theBets[6] = new RouletteBet(RBets.Range, "Low");
		theBets[7] = new RouletteBet(RBets.Range, "High");
	}
	
	private class ButtonListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			if (e.getSource() == setButton)
			{
				setButton.setEnabled(false);
				spinButton.setEnabled(true);
				checkButton.setEnabled(false);
				theWheel.set();  // set the wheel.  This basically reinitializes
						// the wheel to "unassigned" to prepare it for another spin.
			}
			else if (e.getSource() == spinButton)
			{
				spinButton.setEnabled(false);
				theWheel.spin();  // The spin() method should use a Thread
					// and the run() method in the RouletteWheel class to
					// try numbers in the wheel for a while and then stop on
					// a final number.  It should use the choose() / unChoose()
					// methods in the RouletteSquare class to alternate through the
					// various locations on the wheel and graphically show the current
					// selected square.  This process utilizes the Runnable interface
					// which the RouletteWheel must implement.  For an example of how
					// this would be done see RunnableTest.java.  For a snapshot
					// of how the wheel might look while running, see
					// WheelTest.htm.
					
					// After the spin() method finishes it will make a callback to
					// the activate() method of this program to signal that the
					// wheel spin is complete.  The idea is this:  The wheel spins for
					// an indeterminate period of time.  Any entity (ex: this program)
					// that wants to check the result (ex: did my bet win?) needs to
					// wait until the spin is complete to actually check.  Thus, the
					// RouletteWheel will call the activate() method to tell this program
					// that it is safe to proceed.
			}
			else if (e.getSource() == checkButton)
			{
				// This method is testing several pre-set bets by calling the
				// checkBet() method.  In an actual game the bet would be entered
				// by a player.
				for (int i = 0; i < theBets.length; i++)
				{
					StringBuilder str = new StringBuilder("");
					str.append("Bet:" + theBets[i].toString());
					str.append("\n");
					// Note the checkBet method call.  This is very similar to
					// checkBet in your previous RouletteWheel class.  However, now
					// the RouletteWheel is simply checking the bet parameter vs. the
					// final chosen RouletteSquare in the wheel after the spin.
					int res = theWheel.checkBet(theBets[i]);
					if (res == 0)
						str.append("Losing bet!  Sorry");
					else if (res == 1)
						str.append("Even money winner!");
					else if (res == 35)
						str.append("Big winner!");
					JOptionPane.showMessageDialog(null, str.toString());
				}
			}
		}
	}
	
	// Called by the RouletteWheel class after the spin() method has completed.
	// This method can be called because a reference to the WheelTest object is
	// passed to the RouletteWheel in the constructor (see above).  In this case
	// the method enables the checkButton so that the results can be checked and
	// the setButton so that the wheel can be reset if desired.
	public void activate()
	{
		checkButton.setEnabled(true);
		setButton.setEnabled(true);
	}
		
	public static void main(String [] args)
	{
		// Calls to set fonts for JOptionPanes
		UIManager.put("OptionPane.messageFont", new Font("TimesRoman", Font.BOLD, 35));
		UIManager.put("OptionPane.buttonFont", new Font("TimesRoman", Font.PLAIN, 35));
		UIManager.put("TextField.font", new Font("TimesRoman", Font.PLAIN, 35));
		new WheelTest();
	}
}
