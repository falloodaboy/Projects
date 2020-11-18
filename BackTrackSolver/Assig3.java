import java.io.*;
import java.util.*;
public class Assig3 {

public static void main(String[] args){

	//Scanner inScan = new Scanner(System.in);
		Scanner fReader, lineReader;
		File fName;
        String fString = ""; //word = "";

       	// Make sure the file name is valid
        while (true)
        {
           try
           {
           	//Scanner and stuff for reading in the line
           		lineReader = new Scanner(System.in);
               System.out.println("Please enter grid filename:");
               fString = lineReader.nextLine();
               fName = new File(fString);
               fReader = new Scanner(fName);
               
               break;
           }
           catch (IOException e)
           {
               System.out.println("Problem: " + e);
           }
        }

		// Parse input file to create 2-d grid of characters
		String [] dims = (fReader.nextLine()).split(" ");
		int rows = Integer.parseInt(dims[0]);
		int cols = Integer.parseInt(dims[1]);

		char [][] theBoard = new char[rows][cols];

		for (int i = 0; i < rows; i++)
		{
			String rowString = fReader.nextLine();
			for (int j = 0; j < rowString.length(); j++)
			{
				theBoard[i][j] = Character.toLowerCase(rowString.charAt(j));
			}
		}

		// Show user the grid
		for (int i = 0; i < rows; i++)
		{
			for (int j = 0; j < cols; j++)
			{
				System.out.print(theBoard[i][j] + " ");
			}
			System.out.println();
		}
			
		boolean controller = true;
	while(controller == true){
		//Copy the board so that it is preserved each time the search is made
			char[][] copyBoard = new char[rows][cols];
			for(int i=0; i < rows;i++){
				for(int j=0; j < cols; j++){
					copyBoard[i][j] = Character.toLowerCase(theBoard[i][j]);
				}
			}
			
			StringBuilder phrase = new StringBuilder();
			System.out.println();
			System.out.println("Please enter phrase (sep. by single spaces):");
			 String word = lineReader.nextLine();
			 if(word.length() <= 0){
			 	controller = false;
			 }
			 else{
			 	phrase = new StringBuilder();
				phrase.append(word.toLowerCase());
				String[] arg = phrase.toString().split(" ");
		
		System.out.println("Looking for: " + phrase);
		System.out.println("containing " + arg.length + " words");
		
		Cell cell  = new Cell(0,0);
		 DecisionInterface decision = new WordPuzzleDecision(cell,rows,cols,0,arg);
		 SolutionInterface solution = new WordPuzzleSolution(rows,cols,arg,copyBoard);
		BackTrackingSolver bts = new BackTrackingSolver(solution, decision);
		SolutionInterface result = bts.solve();
		//if it is not found, simply output that else print all words and their locations
		if(result == null){
			System.out.println("The phrase: " + phrase.toString() + "\n" +"was not found");
		}
		else{
			System.out.println("The phrase: " + phrase.toString() + "\n" +"was found:");
			WordPuzzleSolution castsol = (WordPuzzleSolution) result;
			List<String> list= new ArrayList<>();
			for(int i=0; i < arg.length*2; i++){
				list.add(castsol.getCell().toString());
			}
			Collections.reverse(list);
			int i=0;
			int j=0;
			while(i != list.size()){
				System.out.println(arg[j] + ": "+list.get(i) + " to " +list.get(i+1));
				i++;
				i++;
				j++;
			}
			//Show the grid once more
			for (int x = 0; x < rows; x++)
			{
				for (int z = 0; z < cols; z++)
				{
					System.out.print(copyBoard[x][z] + " ");
				}
				System.out.println();
			}
		}
				System.out.println();
			 }
				
	}
			

	}

}
class Cell {
	private int hor, ver;

	public Cell(int x, int y){
		hor = x;
		ver = y;
	}
	public Cell(Cell cell){
		hor = cell.getX();
		ver = cell.getY();
	}
	public int getX(){
		return hor;
	}

	public int getY(){
		return ver;
	}

	public void incX(){
		hor++;
	}

	public void incY(){
			ver++;
	}

	public void decX(){
		hor--;
	}

	public void decY(){
		ver--;
	}

	public void setX(int num){
		hor = num;
	}

	public void setY(int num){
		ver = num;
	}


	public String toString(){
		return new String("(" + hor + "," + ver + ")");
	}
}
