/*************************************************************************
 *  Compilation:  javac LZW.java
 *  Execution:    java LZW - < input.txt   (compress)
 *  Execution:    java LZW + < input.txt   (expand)
 *  Dependencies: BinaryIn.java BinaryOut.java
 *
 *  Compress or expand binary input from standard input using LZW.
 *
 *  WARNING: STARTING WITH ORACLE JAVA 6, UPDATE 7 the SUBSTRING
 *  METHOD TAKES TIME AND SPACE LINEAR IN THE SIZE OF THE EXTRACTED
 *  SUBSTRING (INSTEAD OF CONSTANT SPACE AND TIME AS IN EARLIER
 *  IMPLEMENTATIONS).
 *
 *  See <a href = "http://java-performance.info/changes-to-string-java-1-7-0_06/">this article</a>
 *  for more details.
 *
 *************************************************************************/
import java.io.*;
public class MyLZW {
    private static final int R = 256;        // number of input chars
    private static int L;      // number of codewords = 2^W
    private static int W = 9;         // codeword width
    private static char mode = 0;
    private static double oldratio = 0;
    private static double uncompressed = 0;
    private static double compressed = 0;
    // private static PrintWriter cwriter, ewriter;
    // private static FileOutputStream cOutput, eOutput;

    public static void compress() { 
        BinaryStdOut.write(mode);
        int val = 1;
        W = 9;
        for(int i=0; i < W; i++) {
            val *= 2;
        }
        L = val;


        // try{
        //     cOutput = new FileOutputStream("compress.txt");
        //     cwriter = new PrintWriter(cOutput);
        // }
        // catch(FileNotFoundException e){
        //     e.printStackTrace();
        // }


        String input = BinaryStdIn.readString();
        TST<Integer> st = new TST<Integer>();
        //make a string that takes contains the words from the output in st.
        for (int i = 0; i < R; i++)
            st.put("" + (char) i, i);
       
        int code = R+1;  // R is codeword for EOF
        
        while (input.length() > 0) {
            String s = st.longestPrefixOf(input);  // Find max prefix match s.
            uncompressed += (s.length() * 12); //calculate and aggregate uncompressed data size
            compressed += W;                //calculate and aggregate compressed data size.
           
            BinaryStdOut.write(st.get(s), W);      // Print s's encoding.
            int t = s.length();
            if (t < input.length() && code < L) {
                String yeet = input.substring(0, t + 1);
             //   cwriter.append("Input:" + s + " Output:" + st.get(s) + " Book:" + (code+1) + " :" + yeet + "\n");
                st.put(yeet, code++); // Add s to symbol table.

            }
            else if(t < input.length() && code == L && W < 16){
          //      cwriter.println("codebook width increased");
                W += 1;
                L = 2 * L;
                String yeet = input.substring(0, t + 1);
               // cwriter.append("Input:" + s + " Output:" + st.get(s) + " Book:" + (code+1) + " :" + yeet + '\n');
                st.put(yeet, code++); // Add s to symbol table.
            }
            else if(t < input.length() && code == L && W == 16 && mode == 'r'){
                  st = new TST<Integer>();
                    for (int i = 0; i < R; i++)
                        st.put("" + (char) i, i);
                     
                     code = R+1;  // R is codeword for EOF
                    W = 9;
                    val = 1;
                    for(int i=0; i < W; i++) {
                         val *= 2;
                     }
                        L = val;
                String yeet = input.substring(0, t + 1);
               // cwriter.append("Input:" + s + " Output:" + st.get(s) + " Book:" + (code+1) + " :" + yeet + '\n');
                st.put(yeet, code++); // Add s to symbol table.
            }
            else if(t < input.length() && code == L && W == 16 && mode == 'm'){
                if(oldratio == 0)
                    oldratio = uncompressed / compressed;
                // cwriter.append("uncompressed: " + uncompressed +  " compressed: " + compressed + "\n");
                double newratio = uncompressed / compressed;
                double ratioOfRatios = oldratio / newratio;
               // cwriter.append(/*"oldratio: " + oldratio + " newratio " + newratio +*/ " ratioOfRatios: " + ratioOfRatios + "\n");
                
                if(ratioOfRatios > 1.1){
                     st = new TST<Integer>();
                    for (int i = 0; i < R; i++)
                        st.put("" + (char) i, i);
                     
                     code = R+1;  // R is codeword for EOF
                    W = 9;
                    val = 1;
                    for(int i=0; i < W; i++) {
                         val *= 2;
                     }
                        L = val;
                 String yeet = input.substring(0, t + 1);
               //  cwriter.append("ratio deteriorated, ratio: " + ratioOfRatios);
               //  cwriter.append("Input:" + s + " Output:" + st.get(s) + " Book:" + (code+1) + " :" + yeet + '\n');
                 st.put(yeet, code++); // Add s to symbol table.
                 oldratio = 0; //for the sentinel
                }
            }
            input = input.substring(t);            // Scan past s in input.
        }
        
      //  cwriter.append("\n");
      //  cwriter.close();
        BinaryStdOut.write(R, W);
        BinaryStdOut.close();
    } 


    public static void expand() {
        mode = BinaryStdIn.readChar();
        int vale = 1;
        W = 9;
        for(int i=0; i < W; i++){
            vale *= 2;
        }
        L = vale;
        int iL = L;
        for(int i=0; i < 7; i++)
            iL *= 2;
        String[] st = new String[iL];
        int i; // next available codeword value

        // initialize symbol table with all 1-character strings
        for (i = 0; i < R; i++)
            st[i] = "" + (char) i;
        st[i++] = "";                        // (unused) lookahead for EOF

        
        //  try{
        //     eOutput = new FileOutputStream("expand.txt");
        //     ewriter = new PrintWriter(eOutput);
        // }
        // catch(FileNotFoundException e){
        //     e.printStackTrace();
        // }

        try{
            int codeword = BinaryStdIn.readInt(W);
            //ewriter.append(codeword + " :" + st[codeword] + "\n");
            if (codeword == R) return;           // expanded message is empty string
            String val = st[codeword];
            uncompressed += (val.length()*12);
            compressed += W;
            while (true) {
                
                BinaryStdOut.write(val);
                //add a stringBuilder that adds the val to it so that it's size can be determined
                codeword = BinaryStdIn.readInt(W);
                if (codeword == R) break;
                String s = st[codeword];
                if (i == codeword) s = val + val.charAt(0);   // special case hack
                uncompressed += (s.length()*12);
                compressed += W;

                if (i < L){ 
                    st[i++] = val + s.charAt(0);
                    //add a string that contains the codewords in the array and to get its size.
                 //    ewriter.append("codeword: " + codeword +" i: " + i + " :" + val + s.charAt(0)  + "\n");
                }
               
                if(i == L && W == 16 && mode == 'r'){

                    W = 9;
                    vale = 1;
                  
                    for(int j=0; j < W; j++){
                        vale *= 2;
                     }
                    L = vale;
                    st = new String[iL];
                    
                    for (i = 0; i < R; i++)
                        st[i] = "" + (char) i;
                    
                    st[i++] = "";
                   
                 //   ewriter.append("codeword: " + codeword +" i: " + i + " :" + val + s.charAt(0)  + "\n");
                }
                if(i == L && W == 16 && mode == 'm'){
                    if(oldratio == 0)
                        oldratio = uncompressed / compressed;
                   
                    double newratio = uncompressed / compressed;
                    double ratioOfRatios = oldratio / newratio;
                 //    ewriter.append("ratioOfRatios: " + ratioOfRatios + "\n");
                    //ewriter.append("uncompressed: " + uncompressed +  " compressed: " + compressed + "\n");
                    if(ratioOfRatios > 1.1 ){
                             W = 9;
                        vale = 1;
                      
                        for(int j=0; j < W; j++){
                            vale *= 2;
                         }
                        L = vale;
                        st = new String[iL];
                        
                        for (i = 0; i < R; i++)
                            st[i] = "" + (char) i;
                        
                        st[i++] = "";
                   //     ewriter.append("codebook reset \n");
                   //     ewriter.append("codeword: " + codeword +" i: " + i + " :" + val + s.charAt(0)  + "\n");
                        oldratio = 0;
                    }
                }
               if(i == L && W < 16){ //don't change this as it may cause problems
                    W += 1;
                    L *= 2;
                    //ewriter.append("codeword: " + codeword +" i: " + i + " :" + val + s.charAt(0)  + "\n");
                }
                
                val = s;
            }
            
            BinaryStdOut.close();
           // ewriter.append("\n");
           // ewriter.close();

        }
        catch(Exception e){
           // e.printStackTrace();
            BinaryStdOut.close();
            //ewriter.append("\n");
           // ewriter.close();
        }

        
    }



    public static void main(String[] args) {

        if(args[0].equals("-")){

             if(args[1].equals("r") || args[1].equals("m") || args[1].equals("n")){
                 mode = args[1].charAt(0);
             }
            else
                throw new IllegalArgumentException("Illegal command line argument");
             
             // System.err.println(args[1]);
            compress();
        } 
        else if (args[0].equals("+")){
            expand();
        }
        else throw new IllegalArgumentException("Illegal command line argument");
        

        //System.err.println(L);
        // cwriter.close();
        // ewriter.close();
    }

}
