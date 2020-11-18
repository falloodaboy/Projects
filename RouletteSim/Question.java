/**
Uses of this class:
1. Store question and answer
2. methods for retrieving and checking these

**/

public class Question {

	private String question;
	private String answer;
	
	public Question(String quest, String ans){
	
		question = new String(quest);
		answer = new String(ans);
	}

	
	public String retQuest(){
		return question;
	}
	

	public String retAns(){
		return answer;
	}

	public String QandA() {
		String str = "Q: " + question + "A: " + answer;
		return str;
	}
	public boolean equals(String q, String a) {
			if(question.equals(q) && answer.equals(a)){
				return true;
			}
			else {
				return false;
			}
	}

}