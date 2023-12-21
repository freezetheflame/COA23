package cpu.alu;

import util.DataType;
import java.io.StringWriter;
import java.util.Optional;

/**
 * Arithmetic Logic Unit
 * ALU封装类
 */
public class ALU {

    /**
     * 返回两个二进制整数的和
     * dest + src
     *
     * @param src  32-bits
     * @param dest 32-bits
     * @return 32-bits
     */
    public DataType add(DataType src, DataType dest) {
        String srcStr = src.toString();
        String destStr = dest.toString();
        String ans = "";
        char c = '0';
        for(int i = 31;i >=0;i--)
        {
            ans = full_add(srcStr.charAt(i),bit_of(destStr,i),c) + ans;
            c = carry(bit_of(srcStr,i),bit_of(destStr,i),c);
        }
        return new DataType(ans);
    }
    public char bit_of(String a,int i){
        return a.charAt(i);
    }
    public String full_add(char a,char b,char c){
        String ans = "";
        int sum = a-'0'+b-'0'+c-'0';
        ans = ans + sum%2;
        return ans;
    }
    public char carry(char a,char b,char c){
        int sum = (a-'0'+b-'0'+c-'0')/2;
        char ans = (char)(sum+'0');
        return ans;
    }
    /**
     * 返回两个二进制整数的差
     * dest - src
     *
     * @param src  32-bits
     * @param dest 32-bits
     * @return 32-bits
     */
    public DataType sub(DataType src, DataType dest) {

        return add(dest,not(src));
    }
    public DataType not(DataType src){
        String srcStr = src.toString();
        String ans = "";
        for(int i = 0;i < 32;i++){
            if(srcStr.charAt(i) == '0'){
                ans = ans + '1';
            }
            else{
                ans = ans + '0';
            }
        }
        return add(new DataType(ans),new DataType("00000000000000000000000000000001"));
    }

}
