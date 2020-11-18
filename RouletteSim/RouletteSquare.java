import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
public class RouletteSquare extends JLabel implements SwingConstants {
	private int value;
	private boolean isChosen;
	private RParities par;
	private RColors col;
	private RRanges ran;
	private Color compcol;
	public RouletteSquare(int val){
		super();//call super to make sure nothing has changed
		value = val;
		 if(value == 0){	//case where spin value equals 0.
		 	setForeground(Color.green);
		 	col = RColors.Green;
		 	ran = RRanges.Medium;
		 	par = RParities.None;
		 }
		 else if(value > 0 && value <= 18){ //Same as in Assignment 2, Assign certain properties based on the value of the "spin"
		 	if(value <=10){
					if(value % 2 == 0){
						col = RColors.Black;
						par = RParities.Even;
					}
					else {
						setForeground(Color.red);
						col = RColors.Red;
						par = RParities.Odd;
					}
				}
				else{
					if(value % 2 == 0){
						setForeground(Color.red);
						col = RColors.Red;
						par = RParities.Even;
					}
					else {
						col = RColors.Black;
						par = RParities.Odd;
					}
				}
				ran = RRanges.Low;
		 }
		 else if(value > 18 && value <= 36){
			if(value <= 28){ 		// if ans is 19-28, check even or odd and assign colors
				if(value % 2 == 0){
					col = RColors.Black;
					par = RParities.Even;
				}
				else {
					setForeground(Color.red);
					col = RColors.Red;
					par = RParities.Odd;
				}
			}
			else {
				if(value % 2 == 0){
					setForeground(Color.red);
					col = RColors.Red;
					par = RParities.Even;
				}
				else {
					col = RColors.Black;
					par = RParities.Odd;
				}
			}
			ran = RRanges.High;
		}
		compcol = this.getBackground();
		Integer newInt = value;
		this.setFont(new Font(this.getFont().getName(), Font.PLAIN, 30));
		setHorizontalTextPosition(SwingConstants.CENTER);
		setHorizontalAlignment(SwingConstants.CENTER);

		setText(newInt.toString());
	}


	public void choose(){ //setBackground to indicate it has been chosen.
		setOpaque(true);
		setBackground(Color.cyan);
		isChosen = true;
	}
	public void unChoose(){
		setBackground(compcol);
		isChosen = false;
	}
	public boolean isChosen(){
		return isChosen;
	}

//accessor methods for the RouletteWheel class for each property of the wheel's spin.
	public int getValue(){
		return value;
	} 
	public RParities getParity() {
		return par;
	}
	public RColors getColor(){
		return col;
	}
	public RRanges getRange(){
		return ran;
	}
}