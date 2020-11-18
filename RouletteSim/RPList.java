import java.util.Scanner;
import java.io.PrintWriter;
import java.io.File;
import java.io.*;
public class RPList {
	private File file;
	private RoulettePlayer[] playerlist;
	private PrintWriter printer;
	private Scanner scan;
	private int playercount;
	private String filename;
	private String[] prop;

	public RPList(String fil) {
		filename = fil;
		String line;
		try{
			file = new File(filename);
			scan = new Scanner(file);
			playercount = Integer.parseInt(scan.nextLine());
			playerlist = new RoulettePlayer[playercount];
			for(int i=0; i < playercount; i++){
					line = scan.nextLine();
					 prop = line.split(",");
					String name = prop[0];
					String pass = prop[1];
					double money = Double.parseDouble(prop[2]);
					double debt = Double.parseDouble(prop[3]);
					RoulettePlayer player = new RoulettePlayer(name,pass,money,debt);
					if(prop.length > 4) {
						Question[] QA = new Question[2];
						Question q1 = new Question(prop[4],prop[5]);
						Question q2 = new Question(prop[6],prop[7]);
						QA[0] = q1;
						QA[1] = q2;
						player.addQuestions(QA);
					}
					else{
					}
					playerlist[i] = player;
				}
				
			printer = new PrintWriter(file);
		}
		catch(IOException e){
			System.out.println("IO Exception has occurred");
		}
		//checkmeout();
	}
	// public void checkmeout() {
	// 	System.out.println("Hey, Check me out.");
	// }
	
	public String toString(){
			StringBuffer b = new StringBuffer();
		for(int i=0; i < playercount; i++){
			b.append(playerlist[i].toString() + "\n");

		}
		return b.toString();
	}
	

	public int getSize(){
		return playercount;
	}
		
	public String getPassword(String id){
			boolean istrue=false;
			int store = 0;
		for(int i=0; i < playercount; i++){
			if(playerlist[i].getName().equals(id)){
				istrue = true;
				store = i;
			}
			
		}
		if(istrue = true){
			return playerlist[store].getPassword();
		}
		else {
			return null;
		}
	}

	public int getASize() {
		return playerlist.length;
	}
	


	public RoulettePlayer[] resize(RoulettePlayer[] oldA){

			int oldL = oldA.length;
			int newL = 2*oldA.length;

			RoulettePlayer[] newA = new RoulettePlayer[newL];
			for(int i=0; i < oldL; i++){
				newA[i] = oldA[i];
			}
			return newA;


	}
	public boolean AreSame(RoulettePlayer p){
			boolean sameInfo = false;
			boolean sameQuestions = false;
			boolean noQuestions = false;
		for(int i=0; i < playercount; i++){
			if(playerlist[i].getName().equals(p.getName()) && playerlist[i].getPassword().equals(p.getPassword())){
				sameInfo = true;
			}
			if(playerlist[i].doQuestionsExist() == true && p.doQuestionsExist() == true){
				if(playerlist[i].getQuestions()[0].equals(p.getQuestions()[0]) && playerlist[i].getQuestions()[1].equals(p.getQuestions()[1])){
				sameQuestions = true;
				}
			}
			else {
				noQuestions = true;
			}
			
		}
		if(sameInfo == true && sameQuestions == true){
			return true;
		}
		else if(sameInfo == true && noQuestions == true){
			return true;
		}
		else {
			return false;
		}
	}

	public boolean add(RoulettePlayer p){
		if(AreSame(p)){
			System.out.println("Player already exists. Cannot add " + p.getName());
			return false;
		}
		else {
		if(playercount >= playerlist.length){
			playerlist = resize(playerlist);
			playerlist[playercount] = p;
			playercount++;
			return true;
		}
		else {
			playerlist[playercount] = p;
			playercount++;
			return true;
		}
	}
		
}

	public boolean checkId(String id){
			boolean namesMatch = false;

			for(int i=0; i < playercount; i++){
				if(playerlist[i].getName().equals(id)){
					namesMatch = true;
				}
			}
			
			if(namesMatch == true){
				return true;
			}
			else {
				return false;
			}
	}

	public RoulettePlayer getPlayerPassword(String ids, String pass){
				boolean infoMatch = false;
				RoulettePlayer p = null;
				for(int i=0; i < playercount; i++){
					if(checkId(ids) == true && playerlist[i].getPassword().equals(pass)){
						infoMatch = true;
						p = playerlist[i];
					}
				}
				
				if(infoMatch==true){
					return p;
				}
				else {
					return null;
				}
	}
	// public String[] getQuestionsById(String id){
	// 		boolean istrue = false;
	// 		int store=0;
	// 	for(int i=0; i < playercount; i++){
	// 		if(playerlist[i].getName().equals(id)){
	// 			istrue = true;
	// 			store = i;
	// 			break;
	// 		}
	// 	}
	// 	if(istrue = true){
	// 	Question[] qu =	playerlist[store].getQuestions();
	// 	return qu;
	// 	}
	// 	else {
	// 		return null;
	// 	}
	// }

	public RoulettePlayer getPlayerQuestions(String id, Question[] q){

				boolean qm=false;
				RoulettePlayer p = null;
			for(int i=0; i < playercount; i++){
				if(playerlist[i].getName().equals(id)){
					if(playerlist[i].doQuestionsExist() == true){
						//playerlist[i].getQuestions()[0].equals(q[0].retQuest()) && playerlist[i].getQuestions()[1].equals(q[1].retQuest())
						if(playerlist[i].matchQuestions(q)){
							qm = true;
							p = playerlist[i];
						}
						else {
							//System.out.println("Questions do not match for " + playerlist[i].getName());
							qm = false;

						}
					}
					else{
						System.out.println(playerlist[i].getName() + " does not have any questions");
					}
				}
			}
		return p;
	}	
	public String[] getQuestions(String id){
		String[] newS = new String[2];
		boolean gotQuestions = false;
		for(int i=0; i < playercount; i++){
			if(checkId(id) && playerlist[i].doQuestionsExist()){	
				newS = playerlist[i].getQuestions();
				gotQuestions = true;
			}
		}
		if(gotQuestions){
			return newS;
		}
		else {
			return null;
		}
	}

	public void saveList() {
		printer.println(playercount);
		for(int i = 0; i < playercount; i++){
			printer.println(playerlist[i].saveString());
			
		}
		printer.close();
		
	}

}