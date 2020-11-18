
import java.text.DecimalFormat;
public class RoulettePlayer {
		private double playermoney;
		private double initialmoney;
		private String name;
		private double debt = 0;
		private String password;
		private DecimalFormat f;
		private Question[] quest;
	public RoulettePlayer(String id, double currentmoney){
			playermoney = currentmoney;
			initialmoney = currentmoney;
			name = new String(id);
			f = new DecimalFormat("####.##");
			quest = new Question[2];
	}
	
	public RoulettePlayer(String id, String newpass,double currentmoney ,double entdebt){
		name = new String(id);
		playermoney = currentmoney;
		debt = entdebt;
		password = new String(newpass);
		f = new DecimalFormat("####.##");
		quest = new Question[2];
	}
	
	public RoulettePlayer(String id, String pass){
		name = new String(id);
		password = new String(pass);
		f = new DecimalFormat("####.##");
		quest = new Question[2];
	}

	public double getMoney() {
		return playermoney;
	}
	public void updateMoney(double amount){
		 playermoney += amount;
	}
	public String getName() {
		return name;
	}
	public String toString(){
		StringBuilder b = new StringBuilder();
		b.append("Name:" + name + "  ");
		b.append("Cash:" + f.format(playermoney) + "  ");
		b.append("Debt:" + f.format(debt));
		return b.toString();
	}
	public boolean hasMoney(){
		if(playermoney > 0){
			return true;
		}
		return false;
	}
	public String initMoney() {
		return f.format(initialmoney);
	}
	
	public boolean equals(RoulettePlayer player){
		if(this.name.equals(player.name) && this.password.equals(player.password)){
			return true;
		}
		else {
			return false;
		}
	}


	public void borrow(double amt) {
		debt += amt;
		playermoney += amt;
	}

	public void payBack(double amt){
		if(amt < playermoney && amt < debt)
		{
			debt -= amt;
			playermoney -= amt;
		}
		else if(amt > debt && amt > playermoney){
			System.out.println("Pay back of " + amt + " is greater than debt: " + debt + " and cash: " + playermoney);
				if(playermoney > debt){
					playermoney -= debt;
					debt = 0;
					System.out.println("Paying back: " + debt);
				}
				else if(playermoney < debt){
					debt -= playermoney;
					playermoney = 0;
					System.out.println("Paying back: " + debt);
				}
				else{
					System.out.println("No Actio Taken");
				}
			// while(playermoney > debt && debt > 0){
			// 	debt -= 1;
			// 	playermoney -= 1;
			// }	
		}
		else if(amt >= debt && amt <= playermoney){
			System.out.println("Pay back of " + amt + " is greater than debt: " + debt);
			System.out.println("Paying back " + debt);
			playermoney -= debt;
			debt = 0;
		}
		else if(amt <= debt && amt >= playermoney){
			System.out.println("Pay back of " + amt + " is greater than cash: " + playermoney);
			System.out.println("Paying back: " + playermoney);
			debt -= playermoney;
			playermoney = 0;
		}
		else {
			System.out.println("Pay back of " + amt + " in unacceptable");
		}
		
	}
	public String saveString() {
		StringBuilder b = new StringBuilder();
		if(quest[0] != null && quest[1] != null)
		b.append(name + "," + password + "," + playermoney + "," + debt + "," + quest[0].retQuest()+ "," + quest[0].retAns() + "," + quest[1].retQuest() + "," + quest[1].retAns());	
		else
		b.append(name + "," + password + "," + playermoney + "," + debt );	

		return b.toString();
	}
	public void addQuestions(Question[] q){
		quest[0] = q[0];
		quest[1] = q[1];
	}
	public void showAllData() {
			StringBuilder b = new StringBuilder();
			b.append("Name: " + name + "\n");
			b.append("Password: " + password + "\n");
			b.append("Cash: " + playermoney + "\n");
			b.append("Debt: " + debt);
			if(quest[0] != null){
				b.append("\n");
				b.append(quest[0].QandA() + "\n");
				b.append(quest[1].QandA() + "\n");
				System.out.println(b.toString());
			}
			else {
				System.out.println(b.toString());
				System.out.println("Questions: None");
			}


			
					
	}
	public boolean doQuestionsExist(){
		if(quest[0] != null && quest[1] != null){
			return true;
		}
		else {
			return false;
		}
	}
	public void setPassword(String pass){
			password = pass;
	}
	
	public String getPassword(){
		return password;
	}

	public double getDebt(){
		return debt;
	}

	public String[] getQuestions(){
		String[] str = new String[2];
		if(quest[0] != null && quest[1] != null){
			str[0] = quest[0].retQuest();
			str[1] = quest[1].retQuest();
		return str;
		}
		else {
			System.out.println("Questions are null");
			return null;
		}
	}

	public boolean matchQuestions(Question[] q){
		if(quest.length == q.length){
		if(quest[0].equals(q[0].retQuest(),q[0].retAns()) && quest[1].equals(q[1].retQuest(),q[1].retAns()))
		{
			return true;

		}
		else {
			return false;
		}

		}
		else {
			return false;
		}
		
	}
}
// if(quest.length == q.length){
// 			if(quest[0].retQuest().equals(q[0].retQuest()) && quest[1].retQuest().equals(q[1].retQuest())){
// 				if(quest[0].retAns().equals(q[0].retAns()) && quest[1].retAns().equals(q[1].retAns())){
// 					return true;
// 				}
// 				else {
// 					return false;
// 				}
// 			}
// 			else {
// 				return false;
// 			}
// 		}
// 		else {
// 			return false;
// 		}