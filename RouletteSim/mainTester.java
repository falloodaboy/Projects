
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
// import java.rmi.activation.Activatable;
 class Tester extends JPanel implements Runnable{

 	private RouletteSquare[] squareque;
 	// private Activatable mainWheel;
	public Tester(){
		GridLayout grid = new GridLayout(5,8);
		grid.setHgap(25);
		setLayout(grid);
		squareque = new RouletteSquare[37];
		
		for(int i=0; i < 37; i++){
			RouletteSquare ns = new RouletteSquare(i);
			squareque[i] = ns;
		}

		for(int i=0; i < squareque.length;i++){
			add(squareque[i]);
		}
	}

	public void run(){
		int z = 0;
		while(z < 100){
				System.out.println("On thread: " + z);
				z++;
		}
	
	}
	public void spin(){
		 class runClass{

			public void main(String[] args){
					Tester t = new Tester();
					new Thread(t).start();
			}

		}
	}

}

public class mainTester {

	public mainTester(){
		JFrame frame = new JFrame("mainTester Program");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Tester test = new Tester();
		test.spin();
		frame.setLayout(new FlowLayout());
		frame.add(test, BorderLayout.CENTER);
		frame.setVisible(true);
		frame.pack();
	}

	public static void main(String[] args){
			new mainTester();

	}

}