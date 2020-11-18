// CS 401 Fall 2018
// Demo of the Runnable interface in Java.  This can be used with
// Threads to run code in an asynchronous way.  You will need to
// implement Runnable in your RouletteWheel class.

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

public class RunnableTest
{
	private JFrame theWindow;	// instance variables
	private JButton button1, button2;
	private JLabel label1, label2;
	private MyListener theListener;
	private final Font theFont = new Font("Serif", Font.BOLD, 60);

	public RunnableTest()	// initialize everything in the constructor
	{
		theWindow = new JFrame("Demonstration of Runnable Interface");
		theWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		button1 = new JButton("Color Changer");
		button1.setFont(theFont);
		button2 = new JButton("Countdown Timer");
		button2.setFont(theFont);
		
		label1 = new JLabel("Colors");
		label1.setFont(theFont);
		label1.setOpaque(true);
		label1.setHorizontalAlignment(SwingConstants.CENTER);
		label2 = new JLabel("0");
		label2.setFont(theFont);
		label2.setHorizontalAlignment(SwingConstants.CENTER);
		
		theListener = new MyListener();
		button1.addActionListener(theListener);
		button2.addActionListener(theListener);
		
		theWindow.setLayout(new GridLayout(2,2));
		theWindow.add(button1);  theWindow.add(button2);
		theWindow.add(label1);  theWindow.add(label2);

		//theWindow.setSize(400, 200);
		theWindow.pack();
		theWindow.setVisible(true);
	}
	
	class MyListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			// Initialize the Runnable objects.  See more details in the classes below.
			if (e.getSource() == button1)
			{
				long delay = Long.parseLong(JOptionPane.showInputDialog(null, "Enter the delay (ms)"));
				long duration = Long.parseLong(JOptionPane.showInputDialog(null, "Enter the duration (ms)"));
				Runnable colorizer = new ColorChanger(delay, duration);
				//colorizer.run();  // See the difference in execution if we call run() directly rather
									// than using a Thread
				new Thread(colorizer).start();
			}
			else if (e.getSource() == button2)
			{
				long duration = Long.parseLong(JOptionPane.showInputDialog(null, "Enter the duration (ms)"));
				Runnable counter = new CountDown(duration);
				new Thread(counter).start();	
			}
		}
	}

	// This class will change the colors of the JLabel based on parameters
	// passed to the constructor.
	class ColorChanger implements Runnable
	{
		final Color [] theColors = {Color.RED, Color.BLUE, Color.CYAN,
									 Color.ORANGE, Color.MAGENTA};
		int index = 0;
		long delay, duration;
		
		public ColorChanger(long del, long dur)
		{
			delay = del;    // how long between changes
			duration = dur; // how long to keep doing this
		}
		
		// This method is the implementation of the Runnable interface.
		// Any code can be in here, but typically there is some loop with
		// a delay in it.  The delay is done using the Thread.sleep() method.
		// This method requires an exception handler -- see how it is done
		// below.  Since a Thread is used with this the code will execute
		// asynchronously -- allowing your program to do other things "at the
		// same time"
		public void run()
		{
			long start = System.nanoTime();
			long end = System.nanoTime();
			long delta = end - start;  // this is the elapsed time
			long durNano = duration * 1000000;  // convert to nanoTime
			// Loop until the elapsed time is more than the requested duration
			while (delta <= durNano)  // keep iterating as long as the elapsed
			{						  // time is less than the duration
				
				label1.setForeground(theColors[index]);
				index = (index + 1) % theColors.length;
				label1.setBackground(theColors[index]);
				try {
					Thread.sleep(delay);  // sleep between changes
				}
				catch (InterruptedException e)
				{  System.out.println("Problem with Thread!"); }
				
				end = System.nanoTime();  // recalculate elapsed time
				delta = end - start;
			}
		}
	}

	// This is a primitive countdown timer, counting by 10ths of seconds
	class CountDown implements Runnable
	{
		private long duration;
		
		// Duration is passed in as milliseconds
		public CountDown(long dur)
		{
			duration = dur;
			long durSec = dur / 1000;  // convert to seconds
			label2.setText(""+durSec);
		}
		
		// Another implementation of Runnable.  In this code we want to
		// update the JLabel by 10ths of seconds as it counts down to 0.
		// We do this by subtracting the amount of time that has elapsed
		// from the original duration and rounding to one decimal place.
		public void run()
		{
			long start = System.nanoTime();
			long end = System.nanoTime();
			long elapsed = end - start;
			long durNano = duration * 1000000;  // convert to nanoTime
			double displaySec = durNano / 1000000000.0;  // convert to seconds
			// Loop until the elapsed time is more than the requested duration
			while (displaySec > 0.0)
			{
				try {  // Note that in this loop we are sleeping 50 milliseconds
				       // at each iteration.  The amount that we sleep should be
					   // small enough that it will keep the clock accurate.  Note
					   // also that the sleep amount is approximate -- so we cannot
					   // simply add these amounts together to get the time elapsed.
					   // Rather, we must subtract the start time from the current
					   // time.
					Thread.sleep(50);
				}
				catch (InterruptedException e)
				{  System.out.println("Problem with Thread!"); }
				
				end = System.nanoTime();  // recalculate elapsed time
				elapsed = end - start;
				displaySec = (durNano - elapsed)/1000000000.0;
				displaySec = (long)(displaySec * 10 + 0.5)/10.0;  // round
				label2.setText(""+displaySec);
			}
			JOptionPane.showMessageDialog(null, "Timer Complete");
		}
	}
	
	public static void main(String [] args)
	{
		UIManager.put("OptionPane.messageFont", new Font("TimesRoman", Font.BOLD, 35));
		UIManager.put("OptionPane.buttonFont", new Font("TimesRoman", Font.PLAIN, 35));
		UIManager.put("TextField.font", new Font("TimesRoman", Font.PLAIN, 35));
		new RunnableTest();
	}
}