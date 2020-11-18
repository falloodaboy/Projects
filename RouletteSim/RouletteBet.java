// CS 0401 Fall 2018
// RouletteBet class.  You must use this class as is for Assignment 2

// Note that the betValue field here is a String.  This allows the RouletteBet
// class to work with any of the RBet possibilities.  However, the result of the
// wheel spin will produce enum values for the various bet types, so you will have
// to convert the String in this class into an enum value in order to compare it
// with the result.  For more on this see comments in the RouletteTest.java file.

public class RouletteBet
{
	private RBets betType;
	private String betValue;
	
	public RouletteBet(RBets bet, String val)
	{
		betType = bet;
		betValue = new String(val);
	}
	
	public String toString()
	{
		String ans = new String("Type:" + betType + "  Value:" + betValue);
		return ans;
	}
	
	public RBets getBetType()
	{
		return betType;
	}
	
	public String getBetValue()
	{
		return betValue;
	}
}
	
	