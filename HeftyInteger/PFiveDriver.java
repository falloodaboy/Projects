import java.math.*;
import java.util.*;
import java.io.*;

public class PFiveDriver{
	




	public static void main(String[] args){
		Scanner scan = new Scanner(System.in);
		boolean sentinel = true;

		while(sentinel){

			System.out.println("Enter E to quit or enter C to continue:");
			String ans = scan.nextLine();

			if(ans.equals("E")){
				break;
			}
			else{
				System.out.println("Testing HeftyMod method:");
				System.out.println("Enter first number to be modded: ");
				String toMod = scan.nextLine();
				System.out.println("Enter the modulus number: ");
				String isMod = scan.nextLine();
				byte[] tooMod = new BigInteger(toMod).toByteArray();
				byte[] issMod = new BigInteger(isMod).toByteArray();
				HeftyInteger yeet = new HeftyInteger(tooMod);
				System.out.println("byte presented: " + new BigInteger(tooMod).toString());
				System.out.println("byte presented: " + new BigInteger(issMod).toString());
				// System.out.println("testing isLarger method on tooMod, issMod: " + HeftyInteger.isLargerBetter(tooMod, issMod));
				// System.out.println("testing toInt method on issMod: " + HeftyInteger.toInt(issMod));
				//byte[] ans2 = yeet.heftyMod(tooMod, issMod);
				//HeftyInteger[] result = yeet.longdivideandRemainder(yeet.getVal(), issMod);
			//	System.out.println("a / b: " + new BigInteger(result[0].getVal()).toString());
			//	System.out.println("a % b: " + new BigInteger(result[1].getVal()).toString());
				// System.out.println(toMod + " % " + isMod + " = " + new BigInteger(ans2).toString());
				System.out.println("Testing big numbers: ");
				BigInteger big1 = new BigInteger(toMod);
				BigInteger big2 = new BigInteger(isMod);
				HeftyInteger heft1 = new HeftyInteger(big1.toByteArray());
				HeftyInteger heft2 = new HeftyInteger(big2.toByteArray());
				System.out.println("heft1: " + new BigInteger(heft1.getVal()).toString() + " heft1 size: " + heft1.getVal().length);

				byte pad = (byte) 0;

				if(heft1.getVal()[0] < 0){
					System.out.println("byte negative padded");
					pad = (byte) 0xFF;
				}

				for(int i=0; i < 10; i++){
					heft1.extend(pad);
				}
				System.out.println("heft1: " + new BigInteger(heft1.getVal()).toString() + " heft1 size: " + heft1.getVal().length);
				System.out.println("pruning...");
				byte[] res = HeftyInteger.pruneArray(heft1.getVal());
				System.out.println("res: " + new BigInteger(res).toString() + " res size: " + res.length);
				// BigInteger mul1 = big1.multiply(big2);
				// System.out.println("BigInteger multiplication: " + mul1.toString());
				// HeftyInteger mul2 = heft1.multiply(heft2);
				// System.out.println("HeftyInteger Multiplication: " + new BigInteger(mul2.getVal()).toString());
				// HeftyInteger[] ans3 = heft1.longdivideandRemainder(big1.toByteArray(), big2.toByteArray());
				// System.out.println("a / b: " + new BigInteger(ans3[0].getVal()).toString());
				// System.out.println("a % b: " + new BigInteger(ans3[1].getVal()).toString());


				// BigInteger gcd1 = big1.gcd(big2);
				// System.out.println("BigInteger GCD: " + gcd1.toString());
				// HeftyInteger[] gcd2 = heft1.XGCD(heft2);
				// System.out.println("HeftyInteger GCD: " + new BigInteger(gcd2[0].getVal()).toString());
				// System.out.print("x: " + new BigInteger(gcd2[1].getVal()).toString() + "	");
				// System.out.print("y: " + new BigInteger(gcd2[2].getVal()).toString());
				// System.out.println();
			}
		
		}
	



	}

}