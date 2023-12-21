package cpu.alu;

import util.DataType;
import util.Transformer;

import java.util.Collections;


/**
 * Arithmetic Logic Unit
 * ALU封装类
 */
public class ALU {
    /**
     * 返回两个二进制整数的乘积(结果低位截取后32位)
     * dest * src
     *Put multiplicand in BR and multiplier in QR
     * and then the algorithm works as per the following conditions :
     * 1. If Qn and Qn+1 are same i.e. 00 or 11 perform arithmetic shift by 1 bit.
     * 2. If Qn Qn+1 = 01 do A= A + BR and perform arithmetic shift by 1 bit.
     * 3. If Qn Qn+1 = 10 do A= A – BR and perform arithmetic shift by 1 bit.
     * @param src  32-bits
     * @param dest 32-bits
     * @return 32-bits
     */
    public DataType mul(DataType src, DataType dest) {
        String s1 = src.toString();
        String d1 = dest.toString();
        StringBuilder temp = new StringBuilder("00000000000000000000000000000000");
        temp.append(s1);
        temp.append("0");
        //get the booth algorithm
        for(int i = 0;i< s1.length();i++){
            if(temp.charAt(64)=='0'&&temp.charAt(63)=='1'){
                //Yi - Yi+1 = -1
                temp.replace(0,32,sub(d1,temp.substring(0,32)));
            }else if(temp.charAt(64)=='1'&&temp.charAt(63)=='0'){
                //Yi - Yi+1 = -1
                temp.replace(0,32,add(d1,temp.substring(0,32)));
            }
            temp.insert(0,temp.charAt(0));
            temp.deleteCharAt(65);
        }
        return new DataType(temp.substring(32,64));
    }

    static DataType remainderReg = new DataType("00000000000000000000000000000000");

    /**
     * 返回两个二进制整数的除法结果
     * dest ÷ src
     *
     * @param src  32-bits
     * @param dest 32-bits
     * @return 32-bits
     */
    public DataType div(DataType src, DataType dest) {
       String Src  = src.toString();
       String Dest = dest.toString();
       int  sign = 0;
       if((Src.charAt(0)=='1'&&Dest.charAt(0)=='0')||(Src.charAt(0)=='0'&&Dest.charAt(0)=='1')){
           sign = 1;
       }
        if(Src.equals(String.join("", Collections.nCopies(32,"0")))){
            throw new ArithmeticException("ddd");
        }
       //正数除法
        if(Src.charAt(0)=='1'){
            Src = not(Src);
        }
        if(Dest.charAt(0)=='1'){
            Dest= not(Dest);
        }
        StringBuilder ans = new StringBuilder(String.join("", Collections.nCopies(32,"0")));
        ans.append(Dest);
        //operate the division
        String temp = "";
        String answer  = "";
        for(int i = 1;i<=Dest.length();i++){
            temp = sub(Src,ans.substring(i,i+32));
            if(temp.charAt(0)=='0'){
                ans.replace(i,i+32,temp);
                answer+="1";
            }
            else {
                answer+="0";
            }
        }
        if(sign==1)
        {
            answer = not(answer);
        }
        remainderReg = new DataType(ans.substring(32,64));
        if(dest.toString().charAt(0)=='1'){
            remainderReg = new DataType(not(ans.substring(32,64)));
        }
        return new DataType(answer);
    }
    public static void main(String[] args){
        ALU alu=new ALU();
        DataType src = new DataType(intToBinary("10"));
        DataType dest = new DataType(intToBinary("-25"));
        String ans=alu.div(src,dest).toString();
        System.out.println(binaryToInt(remainderReg.toString()));
        System.out.println(binaryToInt(ans));
    }
    public String add(String src, String dest) {
       int carry = 0;
       StringBuilder ans = new StringBuilder();
       for(int i = src.length()-1;i>=0;i--){
           ans.append((src.charAt(i)-'0')^(dest.charAt(i)-'0')^carry);
           carry = ((src.charAt(i)-'0')&(dest.charAt(i)-'0'))|(carry&(dest.charAt(i)-'0'))|((src.charAt(i)-'0')&carry);
       }
       return ans.reverse().toString();
    }
    /*
    sub: dest - src
     */
    public String sub(String srcStr, String destStr) {
        return add(destStr, not(srcStr));
    }
    public String not(String srcStr){
        String ans = "";
        for(int i = 0;i < 32;i++){
            if(srcStr.charAt(i) == '0'){
                ans = ans + '1';
            }
            else{
                ans = ans + '0';
            }
        }
        return add(ans,"00000000000000000000000000000001");
    }
    public static String intToBinary(String numStr) {
        return BinarytoComplement(DecimaltoBinary(numStr,true));
    }

    public static String binaryToInt(String binStr) {
        int num=0;
        int len=binStr.length(),result =0;
        for(int i=1;i<=len-1;i++)
        {
            num=binStr.charAt(i)-'0';
            result+=num*Math.pow(2,len-1-i);
        }
        if(binStr.charAt(0)=='1')
            result-=Math.pow(2,len-1);

        return String.valueOf(result);
    }
    public static String BinarytoComplement(String binStr){
        int len=binStr.length();
        StringBuilder result=new StringBuilder();
        if(binStr.charAt(0)=='1'){
            result.append("1");
            for(int i=1;i<len;i++) {
                if (binStr.charAt(i) == '0')
                    result.append("1");
                else
                    result.append("0");
            }
            //System.out.println(result.toString());
            StringBuilder temp=new StringBuilder();
            int i=result.length()-1;
            while(result.charAt(i)=='1'&&i>0){
                temp.append("0");
                i--;
            }
            if(i>0)
                temp.append("1");
            i--;
            for(;i>0;i--){
                temp.append(result.charAt(i));
            }
            temp.append("1");
            return temp.reverse().toString();
        }
        else {
            return binStr;
        }
    }
    public static String DecimaltoBinary(String decimalStr,boolean f){
        StringBuilder result=new StringBuilder();
        int num=Integer.parseInt(decimalStr);
        int flag=0;
        if(num<0){
            num=-num;
            flag=1;
        }
        int len=0;
        while(num>0){
            result.append(num%2);
            num/=2;
            len++;
        }
        while(len<31&&f)
        {
            result.append("0");
            len++;
        }
        if (f)
        {
            if (flag == 1) {
                result.append("1");
            } else
                result.append("0");
        }
        return result.reverse().toString();
    }
}
