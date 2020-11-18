
public class WordPuzzleDecision implements DecisionInterface {
	String[] phrase;
	int currentDecision, rows, cols;
	int currentOption;
	int direction;
	StringBuilder compare;
	Cell coord;

	private int START=-2,NONE = -1, RIGHT = 0, DOWN = 1, LEFT = 2, UP = 3;


	public WordPuzzleDecision(Cell cell,int rs, int cs,int cd, String[] arg) {
		compare = new StringBuilder();
		currentDecision = cd;
		currentOption = -1;
		phrase = arg;
		rows = rs;
		cols = cs;
		direction = NONE; //initially set the direction to none
		coord = cell;
	}

	public boolean isTerminalDecision()
	{
		return currentDecision == phrase.length;//all words have been found as phrase is the array with words in the phrase
	}

	public boolean hasNextOption()
	{

		if(currentDecision == 0){
			return currentOption < (rows*cols)-2;
		}
		else {
			return direction != UP;//if direction is up, then right, down, and left have already been trid
			//so return null if the direction is up as there are no more directions left to try.
		}
	}

	public DecisionInterface getNextDecision()
	{
		//pass the current location and the index for the next word (currentDecision) as well as the dimensions with the phrase
		return new WordPuzzleDecision(coord,rows,cols,currentDecision + 1, phrase);
	}

	public void nextOption()
	{
		//the first word can be found anywhere on the board
		//so a distinction will be made in how we treat it
		//and the subsequent words
		if(currentDecision == 0){

			if(direction == NONE){
				direction = RIGHT;
			}
			else if(direction == RIGHT){
				direction = DOWN;
			}
			else if(direction == DOWN){
				direction = LEFT;
			}
			else if(direction == LEFT){
				direction = UP;
			}
			else if(direction == UP){
				currentOption++;
				if(coord.getY() < cols-1){
					coord.incY();
				}
				else if(coord.getY() == cols-1){
					coord.setY(0);
					if(coord.getX() < rows-1){
							coord.incX();
					}

				}
				direction = RIGHT;
			}
		}

		else {
			//update direction as needed but stop after direction is UP.
			if(direction == NONE){
				direction = RIGHT;
			}
			else if(direction == RIGHT){
				direction = DOWN;
			}
			else if(direction == DOWN){
				direction = LEFT;
			}
			else if(direction == LEFT){
				direction = UP;
			}
		}
	}
//getter methods
	public int getCurrentDecision()
	{
		return currentDecision;
	}
	public int getCurrentOption()
	{
		return currentOption;
	}

	public int getDirection(){
		return direction;
	}

	public Cell getCell(){
		return coord;
	}

}
