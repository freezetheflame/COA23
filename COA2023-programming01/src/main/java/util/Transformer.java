package util;

import java.util.Random;

public class Transformer {

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

    public static String decimalToNBCD(String decimalStr) {
        int num=Integer.parseInt(decimalStr);
        StringBuilder result=new StringBuilder();
        int flag=0;
        if(num<0) {
            result.append("1101");
            num = -num;
        }
        else {
            result.append("1100");
        }
        String a = String.valueOf(num);
        int len=a.length();
        while(len<7){
            result.append("0000");
            len++;
        }
        for(int i=0;i<a.length();i++){
            int temp=a.charAt(i)-'0';
            result.append(BCD(temp));
        }
        return result.toString();
    }

    public static String NBCDToDecimal(String NBCDStr) {
        int i=0;
        StringBuilder result=new StringBuilder();
        if(NBCDStr.substring(0,4).equals("1101")){
            result.append("-");
        }
        i+=4;
        for (; i < NBCDStr.length()-1; i+=4) {
            int temp=BtoD(NBCDStr.substring(i,i+4));
            result.append(temp);
        }
        return String.valueOf(Integer.parseInt(result.toString()));
    }



    public static void main(String[] args) {

        System.out.println(decimalToNBCD("0"));

    }
    //00111111000110011001100110011010
    //00111111000110011001100110011010
    public static String floatToBinary(String floatStr) {
        int flag=0;
        String sign = "";
        if(floatStr.charAt(0)=='-')
        {
            flag=1;
            sign = "1";
        }
        else
            sign = "0";
        //sign solved
        float value=Float.parseFloat(floatStr);
        if (Float.isNaN(value))
            return "Nan";
        if (Float.isInfinite(value) || value >=Float.MAX_VALUE|| value <= -Float.MAX_VALUE)
            return value > 0 ? "+Inf" : "-Inf";
        if (value == 0.0)
            return sign+"0000000000000000000000000000000";
        value=Math.abs(value);
        //special solved

        // judge is it a normal type
        int normal = 0;
        if(value>=Math.pow(2,-126))
            normal=1;
        int len=0;
        StringBuilder result=new StringBuilder();
        result.append(sign);
        int exponent=0;
        if(normal==0) {
            result.append("00000000");
            value*=(float)Math.pow(2,126);
            result.append(get(value));
        }
        else
        {
            while(value>=2){
                value/=2;
                exponent++;
            }//calculate the exponent
            while(value<1){
                value*=2;
                exponent--;
            }

            value=Float.parseFloat(floatStr);
            exponent+=127;
            //get the exponent part
            String temp1=DecimaltoBinary(String.valueOf(exponent),false);
            //System.out.println(temp1);
            while(temp1.length()<8){
                temp1="0"+temp1;
            }
            result.append(temp1);
            //expoent part solved

            value/=Math.pow(2,exponent-127);
            //follow the IEEE754
            value-=1;
            result.append(get(value));
        }

        return result.toString();
    }

    public static String binaryToFloat(String binStr) {
        int flag=0;
        if(binStr.charAt(0)=='1')
        {
            flag=1;
        }
        String exp=binStr.substring(1,9);//get the exponent part for special judge
        String fract=binStr.substring(9);//get the fraction part
        if(exp.equals("11111111")&&fract.equals("00000000000000000000000"))
            return flag==1?"-Inf":"+Inf";
        if(exp.equals("11111111"))
            return "NaN";
        if(exp.equals("00000000")&&fract.equals("00000000000000000000000"))
            return flag==1?"-0.0":"0.0";
        //special judge solved
        int temp=0;
        int exponent=Integer.parseInt(exp,2);
        if(exponent!=0) {
            int p=Integer.parseInt(exp,2);
            float f=0;
            for(int i=9;i<=31;i++){
                if(binStr.charAt(i)=='1'){
                    f+=Math.pow(2,8-i);
                }
            }
            float ans;
            ans=(1+f)*(float) Math.pow(2,p-127);
            if(flag==1)
                ans=-ans;
            return String.valueOf(ans);
        }

        else {
            float f=0;
            for(int i=9;i<=31;i++){
                if(binStr.charAt(i)=='1'){
                    f+=Math.pow(2,8-i);
                }
            }
            float ans;
            ans=(0+f)*(float) Math.pow(2,-126);
            if (flag == 1)
                ans = -ans;
            return String.valueOf(ans);
        }

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
    public static String BCD(int num){
        StringBuilder result=new StringBuilder();
        int i=4;
        while(i>0){
            result.append(num%2);
            num/=2;
            i--;
        }
        return result.reverse().toString();
    }
    public static int BinarytoDecimal(String binStr){
        int answer=0;
        int multi=1;
        int len=binStr.length(),value=0;
        for(int i=0;i<len-1;i++)
        {
            if(binStr.charAt(len-1-i)=='1') {
                //System.out.println("value = 1");
                value = 1;
            }
            else
                value=0;
            answer = answer+value*multi;
            multi*=2;

        }
        //System.out.printf("the answer of %s is %d\n",binStr,answer);
        return answer;
    }
    public static int BtoD(String binStr){
        int tmp=0,multi=1,len=binStr.length();
        for(int i=0;i<len-1;i++)
        {
            multi*=2;
        }
        if(binStr.charAt(0)=='1')
            tmp=1*multi;
        return tmp+BinarytoDecimal(binStr);
    }
    public static int B2D(String num){
        int ans = 0;
        for (int i = 0; i < num.length(); i++) {
            int temp = 0;
            if (num.charAt(i) <= '9' && num.charAt(i) >= '0') temp = num.charAt(i) - '0';
            else temp = num.charAt(i) - 'a' + 10;
            ans = ans * 2 + temp;
        }
        return ans;
    }
    public static String get(float x) {
        int length = 23;
        StringBuilder ansBuilder = new StringBuilder();
        float bit = 0.5f;
        for (int i = 0; i < length; i++) {
            if (x >= bit) {
                ansBuilder.append("1");
                x-=bit;
            } else {
                ansBuilder.append("0");
            }
            bit /= 2;
        }
        return ansBuilder.toString();
    }
}
