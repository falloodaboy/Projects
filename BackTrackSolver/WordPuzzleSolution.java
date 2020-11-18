import java.io.*;
import java.util.*;


public class WordPuzzleSolution implements SolutionInterface{
		int rows,cols;
		char[][] grid;
		String[] phrase;
		Stack<Cell> container;
		private int RIGHT = 0, DOWN = 1, LEFT = 2, UP = 3;
//takes in the rows, columns, the String[] containing the phrase and the board.
	public WordPuzzleSolution(int r, int c,String[] enter,char[][] array){
		phrase = enter;
		grid = array;
		rows = r; cols = c;
		container = new Stack<>();
	}
	/*
	* This method must check the curent decision's direction
	  and then decide which way to build the word using the
	  Cell in decision. it will increment the location each
	  time it applies a letter in the given direction.
		It will also increment the currentDecision as it
		acts as the index for the given word.
	*/
		//Check the Directions and based on what it is
		//in the decision object passed into this function,
		//go to each location and upperCase the word.
		@SuppressWarnings("unchecked")
	public void applyOption(DecisionInterface decision)
	{

		WordPuzzleDecision dec = (WordPuzzleDecision) decision;
		int index = dec.getCurrentDecision();
		String word = phrase[index];
		Cell cell = dec.getCell();
		
		int dir = dec.getDirection();
		
		if(dir == RIGHT){
			if(index==0){
				container.push(new Cell(cell));
			}
			else{
				container.push(new Cell(cell.getX(), cell.getY()+1));
			}
				for(int i=0; i < word.length(); i++){
					if(index > 0 && i == 0 && cell.getY() < cols-1)
						cell.incY();
						int x = cell.getX();
						int y = cell.getY();
						grid[x][y] = Character.toUpperCase(grid[x][y]);
						if(cell.getY() < cols-1 &&  i < word.length()-1)
							cell.incY();
				}
				container.push(new Cell(cell));

			}
		else if(dir == DOWN){
			if(index == 0){
				//if it is the first word, push the location into the container
				//this is because subsequent words must start with the letter
				//in a location around the last letter of the previous word.
				//This is the case for all directions.
				container.push(new Cell(cell));
			}
			else{
				container.push(new Cell(cell.getX()+1,cell.getY()));
			}
				for(int i=0; i < word.length(); i++){
					if(index > 0 && i==0 && cell.getX() < rows-1)
						cell.incX();
						int x = cell.getX();
						int y = cell.getY();
						grid[x][y] = Character.toUpperCase(grid[x][y]);
						if(cell.getX() < rows-1 && i < word.length()-1)
							cell.incX();
					
				}
				container.push(new Cell(cell));

			}
		else if(dir == LEFT){
			if(index==0){
				container.push(new Cell(cell));
			}
			else{
				container.push(new Cell(cell.getX(),cell.getY()-1));
			}
				for(int i=0; i < word.length(); i++){
					if(index > 0 && i==0 && cell.getY() > 0)
							cell.decY();
						int x = cell.getX();
						int y = cell.getY();
						grid[x][y] = Character.toUpperCase(grid[x][y]);
						if(cell.getY() > 0 && i < word.length() -1)
							cell.decY();

				}
				container.push(new Cell(cell));

			}
		else if(dir == UP){
			if(index==0){
				container.push(new Cell(cell));
			}
			else{
				container.push(new Cell(cell.getX()-1, cell.getY()));
			}
				for(int i=0; i < word.length(); i++){
					if(index > 0 && i==0 && cell.getX() > 0)
				 		cell.decX();
						int x = cell.getX();
						int y = cell.getY();
						grid[x][y] = Character.toUpperCase(grid[x][y]);
						if(cell.getX() > 0 && i < word.length()-1)
							cell.decX();

				}
				container.push(new Cell(cell));
			} 

			
		}
	/*
	  Use the DecisionInterface Cell and then undo the letter at
	  each location. Then change the location based on the direction
	*/
	  public char[][] getGrid(){
	  	return grid;
	  }
	public void undoOption(DecisionInterface decision)
	{
		WordPuzzleDecision dec = (WordPuzzleDecision) decision;
		int index = dec.getCurrentDecision();
		int dir = dec.getDirection();
		String word = phrase[index];
		Cell cell = dec.getCell();
			if(dir == RIGHT){
				for(int i=0; i < word.length(); i++){
					grid[cell.getX()][cell.getY()] = Character.toLowerCase(grid[cell.getX()][cell.getY()]);
					if(cell.getY() > 0)//if cell is not out of the board
					cell.decY();
				}
				container.pop();
				container.pop();
			}
			else if(dir == DOWN){
				for(int i=0; i < word.length(); i++){
					grid[cell.getX()][cell.getY()] = Character.toLowerCase(grid[cell.getX()][cell.getY()]);
					if(cell.getX() > 0)
						cell.decX();
				}
				container.pop();//pop last known location of the word to undo
				container.pop();//pop first know location of the word to undo
			}
			else if(dir == LEFT){
				for(int i=0; i < word.length();i++){
					grid[cell.getX()][cell.getY()] = Character.toLowerCase(grid[cell.getX()][cell.getY()]);
					if(cell.getY() < cols-1)
						cell.incY();
				}
				container.pop();
				container.pop();
			}
			else if(dir == UP){
				for(int i=0; i < word.length(); i++){
					grid[cell.getX()][cell.getY()] = Character.toLowerCase(grid[cell.getX()][cell.getY()]);
					if(cell.getX() < rows-1)
						cell.incX();
				}
				container.pop();
				container.pop();
			}
	}
	/**
	  Based on the direction in the DecisionInterface, search
	  the board and match all characters with those in the word
		uses direction,Cell,and currentDecision in the @param decision 
		object to find the next letter in the word, searching in the
		direction that 
	**/
	  @SuppressWarnings("unchecked")
	public boolean isFeasible(DecisionInterface decision)
	{
		WordPuzzleDecision dec = (WordPuzzleDecision) decision;
		int index = dec.getCurrentDecision();
		String word = phrase[index];
		Cell cell = new Cell(dec.getCell());
		int dir = dec.getDirection();
		boolean result = true;
	   if(dir == RIGHT){
			for(int i=0; i < word.length(); i++){
					if(index > 0 && i == 0 && cell.getY() < cols-1)//only update location before checking if it is not the first decision
						cell.incY();//move the cell to the right

					if(word.charAt(i) != grid[cell.getX()][cell.getY()]){
						result = false;
						break;
					}	
					if(cell.getY() < cols-1 && i < word.length()-1)
						cell.incY();
			}
     	}
     else if(dir == DOWN){
				for(int i=0; i < word.length(); i++){
					if(index > 0 && i==0 && cell.getX() < rows-1)//only update location before checking if it is not the first decision
						cell.incX();//move the location down
						 if(word.charAt(i) != grid[cell.getX()][cell.getY()]){
							 result = false;
							 break;
						 }				
						 if(cell.getX() < rows-1 && i < word.length()-1)
						 	cell.incX();	 
			 }
				
    	 }
     else if(dir == LEFT){
				for(int i=0; i < word.length(); i++){
						if(index > 0 && i==0 && cell.getY() > 0)//only update location before checking if it is not the first decision
							cell.decY();//move the location one space left
		 				 if(word.charAt(i) != grid[cell.getX()][cell.getY()]){
		 					 result = false;
		 					 break;
		 				 }
		 				 if(cell.getY() > 0 && i < word.length()-1)
		 				 	cell.decY();
        		}

     		}
     else if(dir == UP){
				 for(int i=0; i < word.length(); i++){
				 	if(index > 0 && i==0 && cell.getX() > 0)
				 		cell.decX();//move the location one space up
						 if(word.charAt(i) != grid[cell.getX()][cell.getY()]){
							 result = false;
							 break;
						 }
					if(cell.getX() > 0 && i < word.length()-1)
						cell.decX();
	       		}
     		}
     else{
       result = false;
     }
		return result;
	}
	//return the last element in the Stack of locations corresponding to the 
	//start and end of each word in the phrase.
	public Cell getCell(){
		return container.pop();
	}
	
	public Stack<Cell> getStack(){
		if(!container.isEmpty())
		return container;
		else
			return null;
	}

}
