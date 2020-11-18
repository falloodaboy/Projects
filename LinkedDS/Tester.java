
public class Tester {


public static void main(String[] args){
	ReallyLongInt int1 = new ReallyLongInt("19090");
	ReallyLongInt int2 = new ReallyLongInt("9090");
	 ReallyLongInt R3 = int1.add(int2);

	// ReallyLongInt sum = int1.add(int2);
	// System.out.println(diff);
	 System.out.println("Addition: "+ R3);
	System.out.println("Subtraction: " + int1.subtract(int2));
	int1.shiftRight();
	System.out.println("Shifted Right: " + int1);
	int1.shiftLeft();
	System.out.println("Shifted Left: " + int1);
// 	LinkedDS<Integer> list = new LinkedDS<>();

// 	for(int i=0; i < 10; i++){
// 		list.addItem(i);
// 	}
// 	System.out.print("Shuffling" + "\n" + list.toString());
// 	System.out.println();
// 	list.shuffle(100);
// 	System.out.println(list.toString());
}

}
