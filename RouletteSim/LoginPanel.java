import javax.swing.*;
import java.awt.*;
import java.awt.event.*;


public class LoginPanel extends JPanel {
	private JLabel id, pass;
	private JTextField idt;
	 private JPasswordField passt;
	private RPList list;
	private LoginInterface log;
	private JPanel panel1,panel2;
	private JButton submit;
	private JButton newplayer;


	public LoginPanel(RPList rp, LoginInterface L){
		ActionListener listener = new submitListener(); //create the listener for the buttons which check for Player in the list.
		this.setLayout(new GridLayout(4,1)); //set Layout to be 
		list = rp;
		log = L;
		panel1 = new JPanel();
		panel2 = new JPanel();
		panel1.setLayout(new GridLayout(1,2)); //1 row, 2 columns for ID and The TexField
		panel2.setLayout(new GridLayout(1,2)); //1 row, 2 columns Password JLabel and PasswordField.
		id = new JLabel("User ID: "); //User ID label in the main Panel.
		idt = new JTextField(10);	//define the field to enter the ID into.
		idt.setPreferredSize(new Dimension(30,20));
		idt.setFont(new Font(this.getFont().getName(), Font.PLAIN, 20));
		pass = new JLabel("Password: "); //define password label for users
		passt = new JPasswordField(10);//define the field to enter the Password into.
		passt.setPreferredSize(new Dimension(30,20));
		passt.setFont(new Font(this.getFont().getName(), Font.PLAIN, 20));
		submit = new JButton("Submit");
		submit.setFont(new Font(this.getFont().getName(), Font.PLAIN, 25));
		submit.addActionListener(listener);
		newplayer = new JButton("New Player");
		newplayer.addActionListener(listener);
		newplayer.setFont(new Font(this.getFont().getName(), Font.PLAIN, 25));
		panel1.add(id); //add to the ID panel which should contain ID JLabel and textField.
		panel1.add(idt);//add same things for password.
		panel2.add(pass);
		panel2.add(passt);
		add(panel1);//add all of the components to this LoginPanel.
		add(panel2);
		add(submit);
		add(newplayer);
	}	
	private class submitListener implements ActionListener {

		public void actionPerformed(ActionEvent e){
			if(e.getSource() == submit){
				checkLister();
			}
			else if(e.getSource() == newplayer){ //if NewPlayer then ask for their information.
				RoulettePlayer p = null;
				String id = JOptionPane.showInputDialog(null, "Please enter your Username ");
				String pass = JOptionPane.showInputDialog(null, "Please enter a password ");
				String money = JOptionPane.showInputDialog(null, "Enter player cash ");
				
				if(!id.equals("") && !pass.equals("")){ // if the fields are not empty
					double mon = Double.parseDouble(money);
					p = new RoulettePlayer(id,pass,mon,0);
					list.add(p);
					log.setPlayer(p);
				}
				else{
					JOptionPane.showMessageDialog(null, "Operation was unsuccessful. Please Try again."); //display message
				}

			}
		}
	}

	public void checkLister(){
		boolean correctID = false; //boolean if id is correct
		boolean correctPass = false; //boolean if password is correct
		String idr = idt.getText();
		String passr = new String(passt.getPassword());
		RoulettePlayer temp = null;
		
		for(int i=0; i < list.getSize(); i++){
			if(list.checkId(idr) == true){ //compare entered ID with those in the RoulettePlayer list
				correctID = true; //if one is equal to one in the database, set this boolean to true.
				// break;
			}
		}
		if(correctID == true){ //if ID is correct, proceed to check password
			for(int i =0; i < list.getSize(); i++){
				if(list.getPassword(idr).equals(passr)){ //get a RoulettePlayer's password by comparing ids, if one matches, return
														 // his or her password. if all of that equals what the password field has,
					correctPass = true;					//set correctPass to true and get RoulettePlayer by their Password and ID.
					temp = list.getPlayerPassword(idr,passr);
					// break;
				}
			}
			if(correctPass == true){
				JOptionPane.showMessageDialog(null, "Welcome " + idr + "!");
				log.setPlayer(temp); // call the setPlayer Method.
				//list.saveList(); <-- causes problems if you uncomment it. Interferes with saveList call in Assig4. Don't know why.
			}
			else{
				JOptionPane.showMessageDialog(null,"Password for " + idr + " doesn't match.");
				passt.setText("");
			}
		}
		else{ //if ID doesn't match, don't bother searching for password.
			JOptionPane.showMessageDialog(null, "ID " + idr + " not found");
			idt.setText("");
			passt.setText("");
		}

	}




}