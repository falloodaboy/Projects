
public class HeftyInteger {

	private final byte[] ONE = {(byte) 1};

	private byte[] val;

	/**
	 * Construct the HeftyInteger from a given byte array
	 * @param b the byte array that this HeftyInteger should represent
	 */
	public HeftyInteger(byte[] b) {
		val = b;
	}

	/**
	 * Return this HeftyInteger's val
	 * @return val
	 */
	public byte[] getVal() {
		return val;
	}

	/**
	 * Return the number of bytes in val
	 * @return length of the val byte array
	 */
	public int length() {
		return val.length;
	}

	/**
	 * Add a new byte as the most significant in this
	 * @param extension the byte to place as most significant
	 */
	public void extend(byte extension) {
		byte[] newv = new byte[val.length + 1];
		newv[0] = extension;
		for (int i = 0; i < val.length; i++) {
			newv[i + 1] = val[i];
		}
		val = newv;
	}

	/**
	 * If this is negative, most significant bit will be 1 meaning most
	 * significant byte will be a negative signed number
	 * @return true if this is negative, false if positive
	 */
	public boolean isNegative() {
		return (val[0] < 0);
	}

	/**
	 * Computes the sum of this and other
	 * @param other the other HeftyInteger to sum with this
	 */
	public HeftyInteger add(HeftyInteger other) {
		byte[] a, b;
		// If operands are of different sizes, put larger first ...
		if (val.length < other.length()) {
			a = other.getVal();
			b = val;
		}
		else {
			a = val;
			b = other.getVal();
		}

		// ... and normalize size for convenience
		if (b.length < a.length) {
			int diff = a.length - b.length;

			byte pad = (byte) 0;
			if (b[0] < 0) {
				pad = (byte) 0xFF;
			}

			byte[] newb = new byte[a.length];
			for (int i = 0; i < diff; i++) {
				newb[i] = pad;
			}

			for (int i = 0; i < b.length; i++) {
				newb[i + diff] = b[i];
			}

			b = newb;
		}

		// Actually compute the add
		int carry = 0;
		byte[] res = new byte[a.length];
		for (int i = a.length - 1; i >= 0; i--) {
			// Be sure to bitmask so that cast of negative bytes does not
			//  introduce spurious 1 bits into result of cast
			carry = ((int) a[i] & 0xFF) + ((int) b[i] & 0xFF) + carry;

			// Assign to next byte
			res[i] = (byte) (carry & 0xFF);

			// Carry remainder over to next byte (always want to shift in 0s)
			carry = carry >>> 8;
		}

		HeftyInteger res_li = new HeftyInteger(res);

		// If both operands are positive, magnitude could increase as a result
		//  of addition
		if (!this.isNegative() && !other.isNegative()) {
			// If we have either a leftover carry value or we used the last
			//  bit in the most significant byte, we need to extend the result
			if (res_li.isNegative()) {
				res_li.extend((byte) carry);
			}
		}
		// Magnitude could also increase if both operands are negative
		else if (this.isNegative() && other.isNegative()) {
			if (!res_li.isNegative()) {
				res_li.extend((byte) 0xFF);
			}
		}

		// Note that result will always be the same size as biggest input
		//  (e.g., -127 + 128 will use 2 bytes to store the result value 1)
		return res_li;
	}

	/**
	 * Negate val using two's complement representation
	 * @return negation of this
	 */
	public HeftyInteger negate() {
		byte[] neg = new byte[val.length];
		int offset = 0;

		// Check to ensure we can represent negation in same length
		//  (e.g., -128 can be represented in 8 bits using two's
		//  complement, +128 requires 9)
		if (val[0] == (byte) 0x80) { // 0x80 is 10000000
			boolean needs_ex = true;
			for (int i = 1; i < val.length; i++) {
				if (val[i] != (byte) 0) {
					needs_ex = false;
					break;
				}
			}
			// if first byte is 0x80 and all others are 0, must extend
			if (needs_ex) {
				neg = new byte[val.length + 1];
				neg[0] = (byte) 0;
				offset = 1;
			}
		}

		// flip all bits
		for (int i  = 0; i < val.length; i++) {
			neg[i + offset] = (byte) ~val[i];
		}

		HeftyInteger neg_li = new HeftyInteger(neg);

		// add 1 to complete two's complement negation
		return neg_li.add(new HeftyInteger(ONE));
	}

	/**
	 * Implement subtraction as simply negation and addition
	 * @param other HeftyInteger to subtract from this
	 * @return difference of this and other
	 */
	public HeftyInteger subtract(HeftyInteger other) {
		return this.add(other.negate());
	}

	/**
	 * Compute the product of this and other
	 * @param other HeftyInteger to multiply by this
	 * @return product of this and other
	 */
	public HeftyInteger multiply(HeftyInteger other) {
		// YOUR CODE HERE (replace the return, too...)
		// byte[] a, b;
		// if(val.length < other.length()){
		// 	a = val;
		// 	b = other.getVal();
		// }
		// else{
		// 	a = other.getVal();
		// 	b = val;
		// }

		// //make a and b the same length
		// byte[] newa = null;
		// byte pad = (byte) 0;
		
		// if(a.length < b.length){
		// 	newa = new byte[b.length];
		// 	int diff = b.length - a.length;

		// 	if(a[0] < 0){
		// 		pad = (byte) 0xFF;
		// 	}

		// 	for(int i=0; i < diff; i++){
		// 		newa[i] = pad;
		// 	}

		// 	for(int i = 0; i < a.length; i++){
		// 		newa[i + diff] = a[i];
		// 	}

		// 	a = newa;
		// }

		byte[] a, b;
		// If operands are of different sizes, put larger first ...
		if (val.length < other.length()) {
			a = other.getVal();
			b = val;
		}
		else {
			a = val;
			b = other.getVal();
		}

		// ... and normalize size for convenience
		if (b.length < a.length) {
			int diff = a.length - b.length;

			byte pad = (byte) 0;
			if (b[0] < 0) {
				pad = (byte) 0xFF;
			}

			byte[] newb = new byte[a.length];
			for (int i = 0; i < diff; i++) {
				newb[i] = pad;
			}

			for (int i = 0; i < b.length; i++) {
				newb[i + diff] = b[i];
			}

			b = newb;
		}
		byte[] result = gradeSchoolMult(a, b);

		return new HeftyInteger(result);
	}

	public byte[] gradeSchoolMult(byte[] par1, byte[] par2){
		assert par1.length == par2.length; //need these to be the same.
		//need to be careful with sign extension for mult with different-signed parameters
		//integer is stored in big-endian. For loop will run backwards.
		//in what cases will magnitude increase?
		boolean isPar1Neg = false;
		boolean isPar2Neg = false;
		
		if(par1[0] < 0 && par2[0] >= 0){
			isPar1Neg = true;
			HeftyInteger park = new HeftyInteger(par1);
			par1 = park.negate().getVal();
		}
		else if(par1[0] >= 0 && par2[0] < 0){
			isPar2Neg = true;
			HeftyInteger park = new HeftyInteger(par2);
			par2 = park.negate().getVal();
		}
		else if(par1[0] < 0 && par2[0] < 0){
			isPar1Neg = true;
			isPar2Neg = true;

			HeftyInteger parh1 = new HeftyInteger(par1);
			HeftyInteger parh2 = new HeftyInteger(par2);
			par1 = parh1.negate().getVal();
			par2 = parh2.negate().getVal();
		}
		else{
			//do nothing.
		}

		//for all bytes in par1, each byte in par2 multiplies into it and adds a carry, then shifts based on the number. then adds it to a product
		
		//could start at location count away from the end based on the current "digit's" location in the number. 

		int carry = 0; 
		HeftyInteger product = new HeftyInteger(new byte[2*par1.length]); //current sum will be adapted to the length of product
		byte[] cursum = null;

		int offset = 0; //used to shift left for integers.
		int count = 0;
		
		for(int i= par1.length-1; i >= 0; i--){
			carry = 0;
			cursum = new byte[(2*par1.length)];
			int cursumLSB = cursum.length-1;
			int num = ((int) par1[i] & 0xFF);
		if(num != 0){ //helps speed up the xgcd 
			for(int j = par2.length-1; j >= 0; j--){

				carry = ( ((int) par1[i] & 0xFF) * ((int) par2[j] & 0xFF)) + carry;
				cursum[cursumLSB] = (byte) (carry & 0xFF);
				carry = carry >>> 8;
				cursumLSB--;
			}

			if(carry > 0){
				//System.out.println("carry: " + carry);
				cursum[cursumLSB] = (byte) (carry & 0xFF);
			}

			if(count != 0){
				cursum = shiftLeftSpecific(cursum, count);
			}
			product = product.add(new HeftyInteger(cursum));

		}
			count++;
		}



		// for(int i = par1.length-1; i >= 0; i--){ //iterate over par1
		// 	cursum = new byte[2*par1.length];
		// 	count = (2*par2.length) - 1;
		// 	carry = 0;
		// 	for(int j = par2.length-1; j >= 0; j--){ //iterate over par2
		// 		System.out.println("offset: " + offset + " count: " + count);
		// 		carry =  (((int) par1[i] & 0xFF) * ((int) par2[j] & 0xFF)  ) + carry;
		// 		System.out.println("befor shifting carry: " + carry);
		// 		cursum[count-offset] = (byte) (carry & 0xFF);

		// 		carry = carry >>> 8;
		// 		//System.out.println("carry: " + carry);
		// 		if(j == 0 && carry != 0){
		// 			System.out.println("reached the end of the number");

		// 			cursum[--count] = (byte) (carry & 0xFF);
		// 		}
		// 		count--;
		// 	}
		// 	product = product.add(new HeftyInteger(cursum));
		// 	offset++; //offset tells how the cursum should be handled.
		// }
		//need to check for sign
		if((isPar1Neg && !isPar2Neg) || (isPar2Neg && !isPar1Neg)){
			product = product.negate();
		}
		return product.getVal();
	}



	/**
	 * Run the extended Euclidean algorithm on this and other
	 * @param other another HeftyInteger
	 * @return an array structured as follows:
	 *   0:  the GCD of this and other
	 *   1:  a valid x value
	 *   2:  a valid y value
	 * such that this * x + other * y == GCD in index 0
	 */
	 public HeftyInteger[] XGCD(HeftyInteger other) {
		// YOUR CODE HERE (replace the return, too...)
		//need method modulus and division
		byte[] a = val;
		byte[] b = other.getVal();
		if(a == null || b == null){
			System.out.println("a or b is null in the initial call to XGCD");
		}
		HeftyInteger[] result = doXGCD(a, b, null, null, null, null, null);
		HeftyInteger[] realres = new HeftyInteger[3];
		realres[0] = result[0];
		realres[1] = result[1];
		realres[2] = result[2];
		return realres;
	 }


	 public boolean isZero(byte[] par){
	 		boolean iszero = true;
	 	for(int i = par.length-1; i >= 0; i--){
	 		if(par[i] > 0){
	 			iszero = false;
	 			break;
	 		}
	 	}
	 	return iszero;
	}
	 //returns an array of HeftyInteger[] arrays where:

	public HeftyInteger[] doXGCD(byte[] a, byte[] b, byte[] adivb, byte[] amodb, byte[] d, byte[] x, byte[] y){
		//System.out.println("Executing...");
		//base case is a mod b = 0;
		if(isZero(b)){
			//base case. In this case, GCD will have been found.
			HeftyInteger[] baseres = new HeftyInteger[3];
			baseres[0] = new HeftyInteger(a);
			baseres[1] = new HeftyInteger(ONE); //x
			baseres[2] = new HeftyInteger(new byte[1]); //y <--this might be the problem.
			return baseres;
		}

		//do division and remainder and assign them to adivb and amodb
		//call the next function.
		HeftyInteger[] bigres = longdivideandRemainder(a, b);
		adivb = bigres[0].getVal();//divide(a, b);
		amodb = bigres[1].getVal();//heftyMod(a, b);
		HeftyInteger[] res = doXGCD(b, amodb, null, null, null, null, null);
		//set d to the value returned.
		//set x and y for this accordingly
		d = res[0].getVal();
		x = res[2].getVal();//<-- res[2] = yprev
		if(adivb != null){
			
			//prune 0s and ones off of the numbers to maybe make multiplication faster?
			//prune number of zeroes at the beginning
			byte[] newres2 = HeftyInteger.pruneArray(res[2].getVal());
			HeftyInteger newreshef2 = new HeftyInteger(newres2);
			HeftyInteger lol = newreshef2.multiply(new HeftyInteger(adivb)); //<-- this calculation seems off
			HeftyInteger wow = res[1].subtract(lol);//<-- this calculation seems off
			y = wow.getVal();
			//y = (res[1].subtract(res[2].multiply(new HeftyInteger(adivb)))).getVal();
		}
		else{
	//		System.out.println("adivb is null for some reason.");
		}


		//return accordingly.
		HeftyInteger[] newres = new HeftyInteger[3];
		newres[0] = new HeftyInteger(d);
		newres[1] = new HeftyInteger(x);
		newres[2] = new HeftyInteger(y); 
		return newres;
	}

	public static byte[] pruneArray(byte[] par){
		//prune zeroes off of the number
		//for negative numbers, turn positive, get rid of zeroes, then turn negative.
		int greatnum = ((int) par[0] & 0xFF);
		if(par[0] > 0){
			return par;
		}
		else{

			if(par[0] < 0){
				HeftyInteger repl = new HeftyInteger(par);
				par = repl.negate().getVal();
				byte pad = (byte) 0xFF;
				int count = 0;
				for(int i = par.length-1; i >= 0; i--){
					int num = ((int) par[i] & 0xFF);
					int num2 = ((int) 0xFF & 0xFF);
					if(num == 0 || num == num2){
						break;
					}

					if(num > 0){
						count++;
					}
				}
				if(count > 0){
					byte[] newpar = new byte[count];
					for(int i = par.length-1; i >=0 && count > 0; i--){
						newpar[(count-1)] = par[i];
						count--;
					}
					par = newpar;
				}
				else{
				}

				repl = new HeftyInteger(par);
				return repl.negate().getVal();
			}
			else if(greatnum == 0){
				byte pad = (byte) 0;
				int count = 0;
				for(int i = par.length-1; i >= 0; i--){
					int num = ((int) par[i] & 0xFF);
					int num2 = ((int) 0xFF & 0xFF);
					if(num == 0 || num == num2){
						break;
					}

					if(num > 0){
						count++;
					}
				}
				if(count > 0){
					byte[] newpar = new byte[count];
					for(int i = par.length-1; i >=0 && count > 0; i--){
						newpar[(count-1)] = par[i];
						count--;
					}
					par = newpar;
				}
				else{
				}
				return par;
			}
			else{
				return par;
			}
		}
	}

	 //return true if par1 >  par2, false otherwise.
	 public static boolean isLarger(byte[] par1, byte[] par2){
	 		boolean ans = true;
	 		//pad both arrays if they are different lengths
	 		if(par1.length < par2.length){
	 			int diff = par2.length - par1.length;
	 			byte[] newpar1 = new byte[par2.length];

	 			byte pad = (byte) 0;
	 			if(par1[0] < 0) //probably won't need this as xgcd does not test with negative numbers.
	 				pad = (byte) 0xFF;
	 			
	 			for(int i=0; i < diff; i++){
	 				newpar1[i] = pad;
	 			}

	 			for(int i=0; i < par1.length; i++){
	 				newpar1[i + diff] = par1[i];
	 			}

	 			par1 = newpar1;
	 		}
	 		else if(par1.length > par2.length){
	 			int diff = par1.length - par2.length;
	 			byte[] newpar2 = new byte[par1.length];

	 			byte pad = (byte) 0;
	 			if(par2[0] < 0) //probably won't need this as xgcd does not test with negative numbers.
	 				pad = (byte) 0xFF;
	 			
	 			for(int i=0; i < diff; i++){
	 				newpar2[i] = pad;
	 			}

	 			for(int i=0; i < par2.length; i++)
	 				newpar2[i + diff] = par2[i];

	 			par2 = newpar2;
	 		}
	 		else{
	 			//do nothing.
	 		}
	 // 	byte[] a, b;
		// // If operands are of different sizes, put larger first ...
		// if (par1.length < par2.length()) {
		// 	a = par2;
		// 	b = par1;
		// }
		// else {
		// 	a = par1;
		// 	b = par2;
		// }

		// // ... and normalize size for convenience
		// if (b.length < a.length) {
		// 	int diff = a.length - b.length;

		// 	byte pad = (byte) 0;
		// 	if (b[0] < 0) {
		// 		pad = (byte) 0xFF;
		// 	}

		// 	byte[] newb = new byte[a.length];
		// 	for (int i = 0; i < diff; i++) {
		// 		newb[i] = pad;
		// 	}

		// 	for (int i = 0; i < b.length; i++) {
		// 		newb[i + diff] = b[i];
		// 	}

		// 	b = newb;
		// }
	 		for(int i=0; i < par1.length; i++){

	 			if(par1[i] != par2[i]){
	 				int ip = ((int) par1[i] & 0xFF);
	 				int j = ((int) par2[i] & 0xFF);
	 				return ip > j;
	 			}






	 			// if((par1[i] != 0 && par2[i] != 0) || (par1[i] != 0xFF && par2[i] != 0xFF)){

	 			// 	int par1ans = ((int) par1[i] & 0xFF);
	 			// 	int par2ans = ((int) par2[i] & 0xFF);
	 			// 	//System.out.println("par1ans: " + par1ans + " par2ans: " + par2ans);
	 			// 	if(par1ans != par2ans){
	 			// 		if(par1ans < par2ans){
	 			// 			//System.out.println("par1ans: " + par1ans + " < " + par2ans);
	 			// 		}

	 			// 		return par1ans >= par2ans; //changed > to >=
	 			// 	}
	 			// 	// else if(i == par1.length-1 && par1ans == par2ans){
	 			// 	// 	return false;
	 			// 	// }
	 			// 	else
	 			// 		continue;
	 			// }
	 		}


	 		return ans;
	 }


	 public static boolean isLargerBetter(byte[] par1, byte[] par2){
	 		if(par1.length > par2.length){
	 			if(par1[0] > 0 && par2[0] > 0){

	 			}
	 			else{

	 			}
	 			return false;
	 		}
	 		else if(par2.length > par1.length){
	 			return false;
	 		}
	 		else if(par1.length == par2.length){
	 			boolean ans = false;
	 			for(int i=0; i < par1.length; i++){
	 				if(par1[i] != par2[i] && (par1[0] > 0 && par2[0] > 0)){
	 					int n = ((int) par1[i] )& 0xFF;
	 					int m = ((int) par2[i] )& 0xFF;
	 					ans = n > m;
	 				}
	 			}
	 			return ans;
	 		}
	 		else{
	 			System.out.println("executed else statement.");
	 			return false;
	 		}
	 }


	 //return par1 % par2
	 public byte[] heftyMod(byte[] par1, byte[] par2){
	 	//use repetitive subtraction until a whole number emerges smaller than par2. <-- might take too long
	 	//could just keep multiplying and seeing which number is bigger and subtracting it and getting the remainder. <-- sounds more plausible
	 	
	 	//don't need to check for negatives 
	 	// compute sum and keep a previous 

	 	boolean notsolved = isLarger(par1, par2);
	 	HeftyInteger yeet = new HeftyInteger(ONE);
	 	byte[] prev = yeet.getVal();
	 	byte[] num = yeet.getVal();
	 	int count = 0;
	 	while(notsolved) {
	 		
	 		yeet = yeet.add(new HeftyInteger(ONE));
	 		prev = num;
	 		num = yeet.multiply(new HeftyInteger(par2)).getVal();
	 		//System.out.println("while loop executed in modulus");
	 		notsolved = isLarger(par1, num);
	 		//System.out.println("Next notsolved: " + notsolved);
	 		//System.out.println("par1: " + )
	 		count++;
	 	}
	 	HeftyInteger park = new HeftyInteger(par1);
	 		 if(count == 1){
	 		 	// System.out.println("count was executed");
	 			 return park.subtract(new HeftyInteger(par2)).getVal();
	 		 }
	 		 else{
	 			return park.subtract(new HeftyInteger(prev)).getVal();
	 		 }
	 	
	 }
	 //took too long to do this algorithm
	 public byte[] divide(byte[] par1, byte[] par2){ //should do integer division as seen with Java integer division.
	 	//mutliply par1 byte into a product until it is larger than par2. then return the integer used.
	 	
	 	boolean notsolved = isLarger(par1, par2);
	 	HeftyInteger yeet = new HeftyInteger(ONE);
	 	byte[] prev = yeet.getVal();
	 	byte[] num = yeet.getVal();
	 	// int count = 0;
	 	while(notsolved)	{
	 	//	System.out.println("while loop executed in divide");
	 		yeet = yeet.add(new HeftyInteger(ONE));
	 		prev = num;
	 		num = yeet.multiply(new HeftyInteger(par2)).getVal();

	 		notsolved = isLarger(par1, num);
	 		// count++;
	 	}
	 		return yeet.subtract(new HeftyInteger(ONE)).getVal();
	 }


	 //
	 public HeftyInteger[] longdivideandRemainder(byte[] par1, byte[] par2){
	 	
	 	// if(isEqual(par1, par2)){
	 	// 	HeftyInteger[] arr = new HeftyInteger[2];
	 	// 	arr[0] = new HeftyInteger(ONE);
	 	// 	arr[1] = new HeftyInteger(new byte[1]);
	 	// 	return arr;
	 	// }

	 	int length = 0;
	 	byte[] quotient = new byte[1];
	 	byte[] curdiv = new byte[1];
	 	curdiv[curdiv.length - 1] = par1[length];
	 	
	 	while(length < par1.length){
	 			/*MSB(par2) > MSB(curdiv)*/
	 		if(isLargerBetter(par2, curdiv) ){
	 			//System.out.println("executed if statement");
	 			quotient = shiftLeft(quotient);
	 			quotient[quotient.length-1] = 0;

	 		}
	 		else{
	 		//	System.out.println("Executing else loop");
	 			HeftyInteger count = new HeftyInteger(new byte[1]);
	 			HeftyInteger curint = new HeftyInteger(curdiv);
	 			HeftyInteger  divint = new HeftyInteger(par2);
	 			/*MSB(curint.getVal()) >= MSB(par2)*/ //<--problem found in isLargerBetter
	 			while(isLarger(curint.getVal(), par2)/*isLarger(curint.getVal(), par2) || (isLarger(curint.getVal(), par2) && MSB(curint.getVal()) == MSB(par2) )*/){ //<-- fix MSB statement
	 				//System.out.println("Executing while loop in else statement");
	 				curint = curint.subtract(divint);
	 				count = count.add(new HeftyInteger(ONE));

	 			}
	 			quotient = shiftLeft(quotient);
	 			//count's digit will be between 1 and 0xFF
	 			quotient[quotient.length-1] = count.getVal()[count.getVal().length-1];
	 			curdiv = curint.getVal();
	 		}

	 		// if(curdiv[0] > 0 && par2[0] > 0 && curdiv[0] == par2[0]){
		 	// 		//System.out.println("catch case executed.");
	 		// 	} 

	 		length++;
	 		if(length == par1.length){
	 		//	System.out.println("break executed.");
	 			break;
	 		}
	 		else{
		 	//	System.out.println("else at end of loop executed");
	 			
		 		curdiv = shiftLeft(curdiv);
		 		curdiv[curdiv.length-1] = par1[length];
	 		}
	 	
	 	}

	 	HeftyInteger[] result = new HeftyInteger[2];
	 	result[0] = new HeftyInteger(quotient);
	 	result[1] = new HeftyInteger(curdiv);
	 	return result;
	 }

	 public byte[] shiftLeft(byte[] par){
	 	//add this byte to the end of the array
	 	byte[] newpar = new byte[par.length+1];

	 	for(int i=0; i < par.length; i++){
	 		newpar[i] = par[i];
	 	}

	 	return newpar;
	 }

	 public byte[] shiftLeftSpecific(byte[] par, int n){
	 	byte[] newpar = new byte[par.length + n];

	 	for(int i=0; i < par.length; i++){
	 		newpar[i] = par[i];
	 	}

	 	return newpar;
	 }

	 public boolean isEqual(byte[] par1, byte[] par2){
	 	boolean ans = true;
	 			byte[] a, b;
		// If operands are of different sizes, put larger first ...
		if (par1.length < par2.length) {
			a = par2;
			b = par1;
		}
		else {
			a = par1;
			b = par2;
		}

		// ... and normalize size for convenience
		if (b.length < a.length) {
			int diff = a.length - b.length;

			byte pad = (byte) 0;
			if (b[0] < 0) {
				pad = (byte) 0xFF;
			}

			byte[] newb = new byte[a.length];
			for (int i = 0; i < diff; i++) {
				newb[i] = pad;
			}

			for (int i = 0; i < b.length; i++) {
				newb[i + diff] = b[i];
			}

			b = newb;
		}

		for(int i=0; i < a.length; i++){
			if(a[i] != b[i]){
				ans = false;
			}
		}

		return ans;
	 }



	 public byte MSB(byte[] par){
	 	for(int i = 0; i < par.length; i++){
	 		if(par[i] != 0 && par[i] != 0xFF){
	 			return par[i];
	 		}
	 	}
	 	return par[0];
	 }

}
