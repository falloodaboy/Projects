
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.*;
public class RouletteWheel extends JPanel implements Runnable{
	private Activatable mainWheel = null;
	private Random rand;			//Note all of the check___ variables are there to get values of the RouletteBet that the
	private RColors col, checkcol;	// player made in the CheckBet method.
	private RRanges ran, checkran;
	private RParities par, checkpar;
	private int val;
	private RouletteSquare[] squareque;
	
	public RouletteWheel(Activatable L){
		GridLayout grid = new GridLayout(5,8); //5 rows and 8 columns
		grid.setHgap(25); //sets the gap in "grids " in the layout.
		setLayout(grid); //set the defined layout to this JPanel.
		squareque = new RouletteSquare[37];						//lines 19-24: create RouletteSquare values up to 36 and add to 
		for(int i=0; i < 37; i++){								//the grid
			RouletteSquare nsquare = new RouletteSquare(i);
			squareque[i] = nsquare;
			add(squareque[i]);
		}

			mainWheel = L; //Store the reference to the main RoulettePanel to this variable.
	}
	public void run(){ //Is what technically shows when ever spinButton is called
		rand = new Random(); //rand object to get cycle through values.
		int num = 0;
		int time = rand.nextInt(76);
		while(time < 15){ //if the number is less than 15, then get a new random number
			time = rand.nextInt(76);
		}
		int counter = 0;
		
			while(counter < time){ //actual loop that chooses and unchooses values in the RouletteWheel.
			squareque[num].unChoose();
			num = rand.nextInt(37);
			squareque[num].choose();
			try{
			Thread.sleep(50);
			}
			catch(Exception e){
			System.out.println("Something went wrong"); //if something went wrong in defining the sleep method.
			}
			counter++;
		}
		val = squareque[num].getValue();//assign properties to variables to check the bet.
		col = squareque[num].getColor();
		par = squareque[num].getParity();
		ran = squareque[num].getRange();
		mainWheel.activate(); // call activate to let the main RoulettePanel program know that it is done spinning.

	}

	public void set(){
		for(int i=0; i < squareque.length; i++){
			squareque[i].unChoose();
		}
		col = null;
		ran = null;
		par = null;
		val = 0;
	}


	public void spin(){
		Thread t = new Thread(this); //define a new Thread from this Object and start it when spin() is called.
		t.start();
		
	}
	public String getResults(){ //return a String object with the results of the bet as Defined in RouletteResult class.
		RouletteResult result = new RouletteResult(col,ran,par,val);
		return result.toString();
	}

	public int checkBet(RouletteBet rb){
		
		if(rb.getBetType() == RBets.Value){
				if(Integer.parseInt(rb.getBetValue()) == val){
					return 35;
				}
				else {
					return 0;
				}
		}
		else if(rb.getBetType() == RBets.Color){
			checkcol = RColors.valueOf(rb.getBetValue());
				if(checkcol == col){
					return 1;
				}
				else {
					return 0;
				}
		}
		 else if(rb.getBetType() == RBets.Range){
		 	checkran = RRanges.valueOf(rb.getBetValue());
		 		if(checkran == ran){
		 			return 1;
		 		}
		 		else {
		 			return 0;
		 		}
		 }
		else if(rb.getBetType() == RBets.Parity){
			checkpar = RParities.valueOf(rb.getBetValue());
				if(checkpar == par){
					return 1;
				}
				else {
					return 0;
				}
		}
		else {
			return 0;
		}


	}
		

}

