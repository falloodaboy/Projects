
/**
Program: Assignment 4 for CS401
Name: Zohaib Wasim
Year of College: Freshman
Status: Single
Description of this program: This is the RoulettePanel class in the program. The whole
program consists of RouletteSquare, RoulettePanel, RouletteWheel, and LoginPanel. 
This class encapsulates all of the other class objects that are needed to run the game.
Note the RoulettePanel is a JPanel with a 1 by 2 layout. It will have a Controls panel
Followed by a Roulette Wheel Panel. Essentially, this JPanel contains two side-by-side JPanels.

**/


import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class RoulettePanel extends JPanel implements Activatable{
		private JPanel panel1;
		private GameInterface gamer;
		private RoulettePlayer player;
		private RouletteWheel wheel;
		private JButton spinButton;
		private JButton makeBet;
		private JButton info;
		private JButton quit;
		private ActionListener listener;
		private RouletteBet bet;
		private JLabel area;
		private int result;
		private String betmoney = "0";
	
	public RoulettePanel(RoulettePlayer p, GameInterface g){

		setLayout(new GridLayout(1,2));
		wheel = new RouletteWheel(this); //pass in the Activatable Interface which is this object to RouletteWheel.
		listener = new ButtonListener(); //create listener to listen for pressing of button. One listener handles all button action
		spinButton = new JButton("Spin Wheel");
		makeBet = new JButton("Make Roulette Bet");
		info = new JButton("Show My Info");
		quit = new JButton("Quit");
		spinButton.setFont(new Font(this.getFont().getName(), Font.BOLD, 20)); //setFonts for buttons
		makeBet.setFont(new Font(this.getFont().getName(), Font.BOLD, 20));
		info.setFont(new Font(this.getFont().getName(), Font.BOLD, 20));
		quit.setFont(new Font(this.getFont().getName(), Font.BOLD, 20));
		spinButton.addActionListener(listener);
		makeBet.addActionListener(listener);
		info.addActionListener(listener);
		quit.addActionListener(listener);
		player = p; //assign reference of RoulettePlayer to p
		gamer = g; // gamer here is a reference to an Object that implements GameInterface which contains the gameOver() method.
		area = new JLabel(player.getName() + ", you have $" + player.getMoney() + " left."); //set the text display above button panel
		area.setFont(new Font(this.getFont().getName(), Font.PLAIN, 20));
		panel1 = new JPanel();
		panel1.setLayout(new GridLayout(5,1)); //set layout for the button panel next to the RouletteWheel panel.
		panel1.add(area); //	add the buttons to panel1 which is the controls panel for the RouletteWheel.
		panel1.add(spinButton);
		panel1.add(makeBet);
		panel1.add(info);
		panel1.add(quit);
		add(panel1); //add panel 1 first to display the controls
		add(wheel); // add RouletteWheel to display the wheel.
		JOptionPane.showMessageDialog(null, "Welcome to Roulette " + player.getName());
	}
	
	public void activate(){
					JOptionPane.showMessageDialog(null, wheel.getResults());
					result = wheel.checkBet(bet);
					if(result == 1){ // if the RoulettePlayer Wins
						JOptionPane.showMessageDialog(null, "Even money Winner! You win $" + betmoney);
						player.updateMoney(Double.parseDouble(betmoney));
						area.setText(player.getName() + ", you have $" + player.getMoney() + " left.");
					}
					else if(result == 0){ //else if the player loses
						JOptionPane.showMessageDialog(null, "Sorry, You lost the Bet  and $" + betmoney);
						player.updateMoney(-Double.parseDouble(betmoney));
						area.setText(player.getName() + ", you have $" + player.getMoney() + " left.");
					}
					else if(result == 35){ // else if a value winner
						player.updateMoney(Double.parseDouble(betmoney)*35);
						double moneyholder =Double.parseDouble(betmoney)*35;
						JOptionPane.showMessageDialog(null, "Value Winner! You win $" + moneyholder);
						area.setText(player.getName() + ", you have $" + player.getMoney() + " left.");
					}
					//the shutWheel method checks if Debt has exceeded for a player and allows a player to borrow if 
					//their total debt has not reached $500. Note that the player only has this option once the player's
					// money has reached 0 and only then. Thus selecting no on the option will forever bar you from
					//borrowing money.
					shutWheel();
					quit.setEnabled(true);
	}
	// 
	public  void shutWheel(){
		//if player has no money and debt is $500.00
		if(player.getMoney() <= 0 && player.getDebt() >= 500){
			//spinButton.setEnabled(false);
			makeBet.setEnabled(false);
		}
		//if money is 0 BUT player debt is less than $500, give them the option of borrowing some mula :)
		else if(player.getMoney() == 0 && player.getDebt() <= 500){
			int ans = JOptionPane.showConfirmDialog(null, "You have run out of money, would you like to borrow some?");
			if(ans == JOptionPane.YES_OPTION){
				String newmoney = JOptionPane.showInputDialog(null, "Enter the amount you would like to borrow(up to "+ (500-player.getDebt()) + " )");
				 double dobans = Double.parseDouble(newmoney);
				while(dobans > (500-player.getDebt()) ){
					JOptionPane.showMessageDialog(null, "House can only allow you to borrow up to $500");
					newmoney = JOptionPane.showInputDialog(null, "Enter the amount you would like to borrow(up to "+ (500-player.getDebt()) + " )");
					dobans = Double.parseDouble(newmoney);
				}
				player.borrow(dobans);
				area.setText(player.getName() + ", you have $" + player.getMoney() + " left.");
			//	spinButton.setEnabled(true);
				makeBet.setEnabled(true);
			}
			else{
			//	spinButton.setEnabled(false);
				makeBet.setEnabled(false);
			}
			
		}
		else{
			//spinButton.setEnabled(false);
			makeBet.setEnabled(true);
		}
		spinButton.setEnabled(false);
	}
	private class ButtonListener implements ActionListener{ //Main Listener for This class that checks all the buttons.

		public void actionPerformed(ActionEvent e){
			RBets rbets = null;
			if(e.getSource() == spinButton){
				if(player.getMoney() > Double.parseDouble(betmoney) || player.getMoney() == Double.parseDouble(betmoney)){
					if(bet == null){
						JOptionPane.showMessageDialog(null,"Please make a bet first");
					}
					else{
						//wheel.set();
						spinButton.setEnabled(false);
						makeBet.setEnabled(false);
						quit.setEnabled(false);
						wheel.spin(); //spin the wheel (execute a thread that shows this) and disable all other buttons 
						//except showInfo button;

						
					}
				}
				else{
					JOptionPane.showMessageDialog(null, "Bet amount exceeds player cash. Please make a new bet");
				}
			
				
			}
			else if(e.getSource() == makeBet){ //If they want to make a bet
				//betmoney stores string of how much they want to bet.
				betmoney = JOptionPane.showInputDialog(null, "How much to bet? <= " + player.getMoney());

				while(Double.parseDouble(betmoney) > player.getMoney()){//try again if it exceeds how much they have
					betmoney = JOptionPane.showInputDialog(null, "How much to bet? <= " + player.getMoney());
				}
				
				String bettype = JOptionPane.showInputDialog(null, "Bet Type?[Color,Range,Parity,Value]"); //enter betType value
				String betvalue = null;
				if(bettype.equals("Color")){
					betvalue = JOptionPane.showInputDialog(null, "Bet Color?[Red,Black]");
					while(!betvalue.equals("Red") && !betvalue.equals("Black")){
						betvalue = JOptionPane.showInputDialog(null, "Bet Color?[Red,Black]");
					}
					rbets = RBets.Color;
				}	
				else if(bettype.equals("Range")){
					betvalue = JOptionPane.showInputDialog(null, "Bet Range?[High,Low]");
					while(!betvalue.equals("High") && !betvalue.equals("Low")){
						betvalue = JOptionPane.showInputDialog(null, "Bet Range?[High,Low]");
					}	
					rbets = RBets.Range;
				}
				else if(bettype.equals("Parity")){
					betvalue = JOptionPane.showInputDialog(null, "Bet Parity?[Even,Odd]");
					rbets = RBets.Parity;
					while(!betvalue.equals("Even") && !betvalue.equals("Odd")){
						betvalue = JOptionPane.showInputDialog(null, "Bet Parity?[Even,Odd]");
					}
				}
				else if(bettype.equals("Value")){
					betvalue = JOptionPane.showInputDialog(null, "Bet Value?[0-36]");
					int dobbetval = Integer.parseInt(betvalue);
					while(dobbetval > 36){
						betvalue = JOptionPane.showInputDialog(null, "Bet Value?[0-36]");
						dobbetval = Integer.parseInt(betvalue);
					}
					rbets = RBets.Value;

				}
				else {
					//For the bet Value prompt, if they do not enter any of the following: [Color,Range,Parity,Value], set a default one
					JOptionPane.showMessageDialog(null,"Not a Valid Option! Bet Type has been set to Value and Bet is 0");
					rbets = RBets.Value;
					betvalue = "0"; 

				}
				if(rbets != null)
				{
					bet = new RouletteBet(rbets, betvalue); // make the new bet once bet Type and bet value are determined
				}
				area.setText("Your Bet is $" + betmoney + " and your choice is " + bet.getBetValue()); //update the text display
																									   // above the controls
				
				spinButton.setEnabled(true); //let the spinbutton be free and reinitialize the wheel.
				wheel.set();
			}
			else if(e.getSource()== info){
				JOptionPane.showMessageDialog(null,"Player info: " + player.toString());
			}
			else if(e.getSource() == quit){
				gamer.gameOver(); //call gameover if they press quit.
			}
			else {
				System.out.println("Something went wrong"); // if You somehow managed to trigger the button event with a button not there
															// well, kudos to you.
			}
		}
	}


}