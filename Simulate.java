import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;

public class Simulate {
    /**
     * Performs translation for a given index, x
     * @param x the index of the Page Reference
     * @return the Page Reference
     */
    public static int translation(int x){
        int[] pageTable = {2,4,1,7,3,5,6}; 
        return pageTable[x];
    }

    /**
     * Converts an integer array of length 8 to a hexadecimal string
     * @param arr the integer array to be converted
     * @return a sring representaion of the array in Hexadecimal form
     */
    public static String[] toHex(int[] arr){
        String[] output = new String[8];
        for(int i=0; i<arr.length; i++){
            output[i] = Integer.toHexString(arr[i]);
            if(output[i].length()<2){
                output[i] = "0" + output[i];
            }
        }
        return output;
    }

    /**
     * Converts a String to a given base
     * @param value the String to be converted
     * @param base the base to be converted to
     * @return a converted String to the base specified
     */
    public static String toBinary(String value, int base) {
		String output = new BigInteger(value, base).toString(2);
        Integer length = output.length();
        if (length < 8) { //using a length of 8 to simplify processig later on in the program
            for (int i = 0; i < 8 - length; i++) {
                output = "0" + output;
            }
        }
        return output;
    }

    /**
     * @param args the filename
     * @throws IOException
     */
    public static void main(String[] args) throws IOException{
        FileWriter writer = new FileWriter("output-OS1");
        byte[] buffer = new byte[8];
        try{
        InputStream stream = new BufferedInputStream(new FileInputStream(args[0]), 8);

        while(stream.read(buffer)!= -1){
            int[] temp = new int[8];
            for(int i=0; i<buffer.length; i++){ //reversing the array and unsigning. Unsiging by shifting.
                if(buffer[7-i] < 0){
                    temp[i] = buffer[7-i] + 256;
                }else{
                    temp[i] = buffer[7-i];
                }
            }
            String[] hexArray = toHex(temp);
            String hexToBin = "";
            for(int i=0; i<hexArray.length; i++){
                hexToBin = hexToBin + toBinary(hexArray[i], 16);
            }
            hexToBin = hexToBin.substring(hexToBin.length()-12, hexToBin.length());
            String pageRef = hexToBin.substring(0, 5);
            int pageRefDec = Integer.parseInt(pageRef, 2);
            pageRef = Integer.toBinaryString(translation(pageRefDec));
            if(pageRef.length()<5){
                pageRef = "00000"+pageRef;
                pageRef = pageRef.substring(pageRef.length()-5, pageRef.length()); //ensure that a value is present, even if it's zero
            }
            String offSet = hexToBin.substring(hexToBin.length()-7, hexToBin.length());
            String physicalAddress = pageRef+offSet; 
            String output = new BigInteger(physicalAddress, 2).toString(16);
            output = "00000000"+output;
            output = "0x"+output.substring(output.length()-8, output.length()); //ensure that a value is present, even if it's zero
            writer.write(output+"\n");
        }
        writer.close();
    }catch(Exception e){
        System.out.println("File not found!");}
    } 
}
