//An interface to represent a decision that may have multiple options
//number of decisions are equal to the length of the string
public interface DecisionInterface {
	public  boolean isTerminalDecision();
	public  boolean hasNextOption() ;
	//return a new Decision object without changing the current decision
	public  DecisionInterface getNextDecision();
	//advance to next option
	public  void nextOption();
	//getters
	public int getCurrentDecision();
	public int getCurrentOption();
}
