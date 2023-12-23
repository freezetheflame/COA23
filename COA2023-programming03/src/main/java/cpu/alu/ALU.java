package cpu.alu;

import util.DataType;


/**
 * Arithmetic Logic Unit
 * ALU封装类
 */
public class ALU {
    /**
     * 返回两个二进制整数的乘积(结果低位截取后32位)
     * dest * src
     *
     * @param src  32-bits
     * @param dest 32-bits
     * @return 32-bits
     */
    public DataType mul(DataType src, DataType dest) {
        String SRC = src.toString();
        String DEST = dest.toString();

        StringBuilder builder = new StringBuilder("00000000000000000000000000000000" + DEST + "0");
        int len = builder.length();
        for(int i = 0; i < 32; i++){
            if(builder.charAt(len - 1) == '1' && builder.charAt(len - 2) == '0'){
                builder.replace(0, 32, add(SRC, builder.substring(0, 32)));
            }
            else if(builder.charAt(len - 1) == '0' && builder.charAt(len - 2) == '1'){
                builder.replace(0, 32, sub(SRC, builder.substring(0, 32)));
            }
            builder.delete(len - 1, len);
            builder.insert(0, builder.charAt(0));
        }
        return new DataType(String.valueOf(builder.substring(32, 64)));
    }

    DataType remainderReg;

    /**
     * 返回两个二进制整数的除法结果
     * dest ÷ src
     *
     * @param src  32-bits
     * @param dest 32-bits
     * @return 32-bits
     */
    public DataType div(DataType src, DataType dest) {
        String SRC = src.toString();
        String DEST = dest.toString();

        if(SRC.equals("00000000000000000000000000000000")) throw new ArithmeticException();

        String NEG_SRC = neg(SRC);
        Boolean flag;
        StringBuilder builder = new StringBuilder(DEST);

        builder.insert(0, DEST.charAt(0) == '0' ? "00000000000000000000000000000000" : "11111111111111111111111111111111");
        builder.replace(0, 32, add((DEST.charAt(0) == SRC.charAt(0) ? NEG_SRC : SRC), builder.substring(0, 32)));
        flag = builder.charAt(0) == SRC.charAt(0);

        for(int i = 0; i < 32; i++){
            builder.delete(0, 1);
            builder.append(flag ? "1" : "0");
            builder.replace(0, 32, add((flag ? NEG_SRC : SRC), builder.substring(0, 32)));
            flag = builder.charAt(0) == SRC.charAt(0);
        }

        StringBuilder res = new StringBuilder(builder.substring(32));
        res.delete(0, 1);
        res.append(flag ? "1" : "0");
        DataType ans = new DataType(DEST.charAt(0) == SRC.charAt(0) ? res.toString() : add(res.toString(), "00000000000000000000000000000001"));

        remainderReg = new DataType(builder.substring(0, 32));

        if(DEST.charAt(0) != remainderReg.toString().charAt(0)){
            remainderReg = new DataType(add(DEST.charAt(0) == SRC.charAt(0) ? SRC : NEG_SRC, remainderReg.toString()));
        }

        if(SRC.equals(remainderReg.toString())){
            remainderReg = new DataType("00000000000000000000000000000000");
            return new DataType(add(ans.toString(), "00000000000000000000000000000001"));
        }
        else if(SRC.equals(neg(remainderReg.toString()))){
            remainderReg = new DataType("00000000000000000000000000000000");
            return new DataType(add(ans.toString(), "11111111111111111111111111111111"));
        }
        return ans;
     }

    public String add(String src, String dest) {
        char[] SRC;
        char[] DEST;
        char[] ans = new char[32];

        SRC = src.toCharArray();
        DEST = dest.toCharArray();

        int cin = 0; //计算进位
        for(int i = 31;i >= 0; i--){
            int OnlyAdd = SRC[i] + DEST[i] + cin - '0' - '0';
            ans[i] = (char)(OnlyAdd % 2 + '0');
            cin = OnlyAdd / 2;
        }
        return new DataType(String.valueOf(ans)).toString();
    }

    public String sub(String src, String dest) {
        char[] SRC;
        char[] DEST;

        SRC = src.toCharArray();
        DEST = dest.toCharArray();

        //取反加一
        for(int i = 31; i >= 0; i--){
            if(SRC[i] == '0') SRC[i] = '1';
            else if(SRC[i] == '1') SRC[i] = '0';
        }
        return add(String.valueOf(DEST),add(String.valueOf(SRC), "00000000000000000000000000000001"));
    }

    /**
     * add one to the operand
     *
     * @return result after adding, the first position means overflow (not equal to the carray to the next) and the remains means the result
     */
    private String neg(String src) {
        char[] SRC = src.toString().toCharArray();

        for(int i = 31; i >= 0; i--){
            if(SRC[i] == '0') SRC[i] = '1';
            else SRC[i] = '0';
        }

        return add(String.valueOf(SRC), "00000000000000000000000000000001");
    }
}
