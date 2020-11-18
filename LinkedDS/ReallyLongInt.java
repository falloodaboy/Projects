
/** A partial implementation of the ReallyLongInt class.
 * @author Sherif Khattab (Adapted  from Dr. John Ramirez's Spring 2017 CS 0445 Assignment 2 code)
 * You need to complete the implementations of the remaining methods.  Also, for this class
 *  to work, you must complete the implementation of the ArrayDS class. See additional comments below.
 */

@SuppressWarnings("overrides")
public class ReallyLongInt extends LinkedDS<Integer>
implements Comparable<ReallyLongInt>
{
	// Instance variables are inherited.  You may not add any new instance variables

	public ReallyLongInt() {
		super();
	}

	/**
	 * @param s a string representing the integer (e.g., "123456") with no leading
	 * zeros except for the special case "0".
	 * 	Note that we are adding the digits here to the END. This results in the
	 * MOST significant digit first. It is assumed that String s is a valid representation
	 * of an unsigned integer with no leading zeros.
	 */
	public ReallyLongInt(String s)
	{
		super();
		char c;
		Integer digit;
		// Iterate through the String, getting each character and converting it into
		// an int.  Then add at the end.  Note that
		// the addItem() method (from LinkedDS) adds at the end.
		for (int i = 0; i < s.length(); i++)
		{
			c = s.charAt(i);
			if (('0' <= c) && (c <= '9'))
			{
				digit = c - '0';
				addItem(digit);
			}
			else
				throw new NumberFormatException("Illegal digit " + c);
		}
	}
	public String toString() {
		StringBuilder b = new StringBuilder();
		for (Node curr = firstNode; curr != null; curr = curr.getNextNode())
		{
			b.append(curr.getData().toString());
		}
		return b.toString();
	}

	/** Simple call to super to copy the items from the argument ReallyLongInt into a new one.
	 * @param rightOp the object to copy
	 */
	public ReallyLongInt(ReallyLongInt rightOp)
	{
		super(rightOp);
	}

	// You must implement the methods below.  See the descriptions in the
	// assignment sheet
	//1. reverse lists.
	//2. determine which is larger and assign nodes and marker RLIs to them
	//3. iterate over the longer value. while(shortNode.getNextNode() != null)
	//add the short Node
	public ReallyLongInt add(ReallyLongInt rightOp) 	{
		ReallyLongInt answer = new ReallyLongInt();
		this.reverse();
		rightOp.reverse();
		int carry = 0;
		ReallyLongInt longRLI= null;
		ReallyLongInt shortRLI = null;
		if(this.numberOfEntries > rightOp.numberOfEntries){
			longRLI = this;
			shortRLI = rightOp;
		}
		else {
				longRLI = rightOp;
			shortRLI = this;
		}
		Node longNode = longRLI.firstNode;
		Node shortNode = shortRLI.firstNode;

		while(longNode != null){
			Integer sum;
			if(shortNode != null){
				sum = new Integer(longNode.getData().intValue() + shortNode.getData().intValue() + carry);
			}
			else {
				 sum = new Integer(longNode.getData().intValue() + carry);
			}

			if(sum >= 10){
				answer.addItem(sum-10);
				carry = 1;
			}
			else if(carry > 0 && sum < 10){
				carry = 0;
				answer.addItem(sum);
			}
			else{
				answer.addItem(sum);
			}
			longNode = longNode.getNextNode();
			if(shortNode != null)
				shortNode = shortNode.getNextNode();
		}
		if(carry > 0 && longNode == null){
			answer.addItem(carry);
		}

		this.reverse();
		rightOp.reverse();
		answer.reverse();
		return answer;
	}
	private void printLists(ReallyLongInt r){
		System.out.println("This List: "+ this.toString());
		System.out.println("rightOp:" + r.toString());
	}
	/**
		Side Note: worked on the first run. Kind of happy about that.
		returns a new ReallyLongInt that has memebers that are the
		digits of the difference of this RLI and rightOp RLI
		@param rightOp the ReallyLongInt to subtract
		@return ReallyLongInt that is the difference of this and rightOp.
	*/
	public ReallyLongInt subtract(ReallyLongInt rightOp) throws ArithmeticException
	{
		ReallyLongInt answer = new ReallyLongInt();
		ReallyLongInt thisRLI = new ReallyLongInt(this);
		ReallyLongInt othRLI = new ReallyLongInt(rightOp);
		thisRLI.reverse();
		othRLI.reverse();
		int longValue = 0;

		Node thisNode = thisRLI.firstNode;
		Node othNode = othRLI.firstNode;

			if(this.numberOfEntries < rightOp.numberOfEntries)
				throw new ArithmeticException("Invalid Difference -- Negative Number");
				else if(this.compareTo(rightOp) < 0){
					throw new ArithmeticException("Invalid Difference -- Negative Number");
				}
			else {
				Integer diff = null;
			while(thisNode != null){
					if(othNode != null){
						if(thisNode.getData().intValue() < othNode.getData().intValue()){
						int borrowed = borrow(thisNode.getNextNode());
						thisNode.setData(new Integer(thisNode.getData().intValue() + borrowed));
							
						}
						diff = new Integer(thisNode.getData().intValue() - othNode.getData().intValue());
						answer.addItem(diff);
					}
					else if(thisNode != null){
						answer.addItem(new Integer(thisNode.getData().intValue()));
					}

					thisNode = thisNode.getNextNode();
					if(othNode != null)
						othNode = othNode.getNextNode();
				}

			}

		answer.reverse();
		answer.removeZeros();
		return answer;
	}

	private void removeZeros(){
			for(int i= numberOfEntries; i > 0; i--){
				if(i > 1 && firstNode.getData().intValue() == 0){
					this.firstNode = firstNode.getNextNode();
				}

			}

	}

	/**
	Recursive method that borrows and returns borrowed value for the
	current node to subtract.
	@param startNode pass current node.next and then subtract two RLI Node values.
	*/
	private int borrow(Node startNode){
	// 	Case: 1,000,000 -1. Must provide the null check here because 
	// when the loop for subtract reaches the final value of 1,000,000,
	// the 1 is actually a 0 and the number is 9999990 after the first borrow. So the code compares 0 
	// from at the end of thisRLI to 1 from othRLI and tries to borrow from 
	// the next Node. Since this node does not exit, there needs to be this
	// null check to ensure we don't get a NullPointerException.
		if(startNode != null){
			int answer = startNode.getData().intValue();
			if(answer > 0){
				startNode.setData(new Integer(answer-1));
				return 10;
			}
			else {
				answer = borrow(startNode.getNextNode());
				startNode.setData(new Integer((answer-1) + startNode.getData().intValue()));

				return answer;
			}
		}
		else {
			return 0;
		}
		

	}
	public int compareTo(ReallyLongInt rOp)
	{
		int answer = 0;
		Node thisNode = firstNode;
		Node othNode = rOp.firstNode;

		 if(this.numberOfEntries == rOp.numberOfEntries){
			for(int i=0; i < numberOfEntries;i++){
				if(thisNode.getData().intValue() > othNode.getData().intValue()){
					answer = 1;
				}
				else if( thisNode.getData().intValue() < othNode.getData().intValue())
					answer = -1;

				thisNode = thisNode.getNextNode();
				othNode = othNode.getNextNode();
			}
		}
		else if(this.numberOfEntries > rOp.numberOfEntries)
			answer = 1;
		else if(this.numberOfEntries < rOp.numberOfEntries)
			answer = -1;

		return answer;
	}

	public boolean equals(Object rightOp)
	{
		ReallyLongInt newOp = (ReallyLongInt) rightOp;
		boolean answer = true;
		if(newOp.numberOfEntries == this.numberOfEntries){
			
			String thisString = this.toString();
			String othString = newOp.toString();
			if(!thisString.equals(othString)){
				answer = false;
			}
		}
		else {
			answer = false;
		}
		return answer;
	}

	public ReallyLongInt multTenToThe(int num)
	{
		ReallyLongInt answer = new ReallyLongInt(this);
		for(int i = 0; i < num; i++){
			answer.addItem(new Integer(0));
		}
		return answer;
	}

	public ReallyLongInt divTenToThe(int num)
	{

		ReallyLongInt answer = new ReallyLongInt(this);
		answer.reverse();
		for(int i=0; i < num; i++){
			answer.removeItem();
		}
		answer.reverse();
		return answer;
	}
}
