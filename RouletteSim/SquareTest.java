// CS 401 Fall 2018
// Test program for your RouletteSquare class.  Your class should run with this program
// and the output should be similar to that shown in SquareTest.htm

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;  

public class SquareTest
{
	private JFrame theWindow;
	private JButton toggleSquare, showData;
	private RouletteSquare theSquare;
	public SquareTest(int val)
	{
		theWindow = new JFrame("SquareTest Program");
		theWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		toggleSquare = new JButton("Select");
		toggleSquare.setFont(new Font("Serif", Font.BOLD, 40));
		showData = new JButton("Show Data");
		showData.setFont(new Font("Serif", Font.BOLD, 40));
		theSquare = new RouletteSquare(val);  // Create a RouletteSquare
			// RouletteSquare must extend JLabel so that it can be
			// rendered as a component in a window

		ActionListener listen = new SquareListener();
		toggleSquare.addActionListener(listen);
		showData.addActionListener(listen);
	
		theWindow.setLayout(new GridLayout(3,1));
		theWindow.add(theSquare);
		theWindow.add(toggleSquare);
		theWindow.add(showData);

		theWindow.pack();
		theWindow.setVisible(true);
	}
	
	private class SquareListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			// toggleSquare button has fired.  Call the appropriate methods
			// in the RouletteSquare object to choose and unchoose it.  This
			// will change the background color of the RouletteSquare.
			if (e.getSource() == toggleSquare)
			{
				if (theSquare.isChosen())
				{
					theSquare.unChoose();
					toggleSquare.setText("Select");
				}
				else
				{
					theSquare.choose();
					toggleSquare.setText("Unselect");
				}
			}
			else // showData button has fired.  Grab information from the
				 // RouletteSquare object and show in in a JOptionPane.
			{
				StringBuilder output = new StringBuilder();
				output.append("Value: " + theSquare.getValue());
				output.append("\nColor: " + theSquare.getColor());
				output.append("\nParity: " + theSquare.getParity());
				output.append("\nRange: " + theSquare.getRange());
				JOptionPane.showMessageDialog(theWindow, output.toString());
			}
		}
	}
	
	public static void main(String [] args)
	{
		// Calls to set fonts for JOptionPanes
		UIManager.put("OptionPane.messageFont", new Font("TimesRoman", Font.BOLD, 35));
		UIManager.put("OptionPane.buttonFont", new Font("TimesRoman", Font.PLAIN, 35));
		UIManager.put("TextField.font", new Font("TimesRoman", Font.PLAIN, 35));
		new SquareTest(Integer.parseInt(args[0]));
	}
}
