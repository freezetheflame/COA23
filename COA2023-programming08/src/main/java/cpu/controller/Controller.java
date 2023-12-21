package cpu.controller;

import cpu.alu.ALU;
import memory.Memory;
import util.DataType;
import util.Transformer;

import javax.xml.crypto.Data;


public class Controller {
    // general purpose register
    char[][] GPR = new char[32][32];
    // program counter
    char[] PC = new char[32];
    // instruction register
    char[] IR = new char[32];
    // memory address register
    char[] MAR = new char[32];
    // memory buffer register
    char[] MBR =  new char[32];
    char[] ICC = new char[2];

    // 单例模式
    private static final Controller controller = new Controller();

    private Controller(){
        //规定第0个寄存器为zero寄存器
        GPR[0] = new char[]{'0','0','0','0','0','0','0','0',
                '0','0','0','0','0','0','0','0',
                '0','0','0','0','0','0','0','0',
                '0','0','0','0','0','0','0','0'};
        ICC = new char[]{'0','0'}; // ICC初始化为00
    }

    public static Controller getController(){
        return controller;
    }

    public void reset(){
        PC = new char[32];
        IR = new char[32];
        MAR = new char[32];
        GPR[0] = new char[]{'0','0','0','0','0','0','0','0',
                '0','0','0','0','0','0','0','0',
                '0','0','0','0','0','0','0','0',
                '0','0','0','0','0','0','0','0'};
        ICC = new char[]{'0','0'}; // ICC初始化为00
        interruptController.reset();
    }

    public InterruptController interruptController = new InterruptController();
    public ALU alu = new ALU();

    private int judge_ICC(){
        if(ICC[0]=='0'){
            if(ICC[1]=='0')
                return 1;
            else return 2;
        }
        else {
            if(ICC[1]=='0')
                return 3;
            else return 4;
        }

    }
    /*
    * 2.2.1 时钟周期的实现
    本次实验我们使用一个tick函数来模拟每个时钟周期。在每个时钟周期中，我们需要完成三件事：

        判断ICC中内容，得到当前处于哪个时钟周期
        执行对应周期的指令微操作序列
        根据指令执行情况判断ICC的下一个状态
    ICC是一个2位的寄存器，请参考课堂讲解内容完成对应状态的判断。

    在判断ICC下一个状态时请注意，我们在框架代码中提供了一个中断控制器，其中包含了表示中断
    * 是否发生的标志，请参考代码中InterruptController部分。 是否进入间址周期的判断请阅
    * 读2.2.3节间址周期的实现。（你可以在完成间址周期的处理和中断的处理后再回到时钟周期的实现）
    * */
    public void tick(){
        boolean flag = true;
        switch(judge_ICC()){
            case 1:
                getInstruct();
                String opcode = new String(IR).substring(0, 7);
                if (opcode.equals("1101110")) {
                    ICC=new char[]{'0','1'};
                }else {
                    ICC=new char[]{'1','0'};
                }
                break;
            case 2:
                findOperand();
                ICC=new char[]{'1','0'};
                break;
            case 3:
                operate();
                if(interruptController.signal){
                    ICC=new char[]{'1','1'};
                }else {
                    ICC=new char[]{'0','0'};
                }
                break;
            case 4:
                interrupt();
                ICC=new char[]{'0','0'};
                interruptController.signal = false;
                break;
            default:
                break;
        }
    }


    private char[] byteToCharArray(byte[] byteArray) {
        StringBuilder binaryStr = new StringBuilder();
        for (byte b : byteArray) {
            String s = Integer.toBinaryString(b & 0xFF);
            while (s.length() < 8) {  // 1 byte is 8 bits
                s = "0" + s;
            }
            binaryStr.append(s);
        }
        return binaryStr.toString().toCharArray();
    }
    /** 执行取指操作 */
    /*
    * 2.2.2 取指微操作序列
    在取指过程中，我们要进行以下操作：

    将PC中的内容加载到MAR（PC中的内容会被初始化为全0，即我们默认程序开始位置为主存开始位置，此处我们忽略了系统程序，请阅读测试用例对整个流程进行理解）
    根据MAR中保存的地址，读取memory中对应内容到MBR（请注意memory中读出的数据是byte数组类型，而寄存器类型是char数组）
    增加PC到下一条指令的位置（此时PC应该加上多少？为什么？考虑指令的长度）
    将MBR中的内容装载到IR中
    取出的指令格式请参考：reference-card.请注意 我们只使用在1.1节提到的指令。
    * */
    private void getInstruct(){
        //load PC to MAR
        MAR = PC;
        //read memory
        byte[] memoryData = Memory.getMemory().read(String.valueOf(MAR),4);
        MBR = byteToCharArray(memoryData);
        //add PC
        //change PC to int
        int pc = Integer.parseInt(String.valueOf(PC),2);
        pc += 4;
        //change PC to char
        String pc_str = Integer.toBinaryString(pc);
        int len = pc_str.length();
        for(int i = 0;i < 32-len;i++){
            pc_str = "0" + pc_str;
        }
        PC = pc_str.toCharArray();
        //load MBR to IR
        IR = MBR;

    }

    /** 执行间址操作 */
    /*
    由于risc-v并不具备间址类型的指令，因此我们额外规定一种间址指令addc。
    我们规定addc指令格式和add相同，根据reference-card我们可以得到正常
    的R类型add指令opcode为1100110， 此处规定addc的opcode为1101110.只
    有寄存器rs2中保存的是操作数的地址。所以如何判断是否需要进入间址周期
    的方式就交给聪明的你了！
    在间址过程中，我们规定了以下操作：
    将rs2中的内容加载到MAR中
    根据MAR中的地址读出内存中对应数据存回rs2中
    在完成间址周期后，我们就可以和正常的add指令一样进入执行周期了。
    */
    private void findOperand(){
        int rs2 = Integer.parseInt(String.valueOf(IR).substring(20,25),2);
        //load rs2 to MAR
        MAR = GPR[rs2];
        //read memory
        byte[] memoryData = Memory.getMemory().read(String.valueOf(MAR),4);
        //load it to rs2
        GPR[Integer.parseInt(String.valueOf(IR).substring(20,25),2)] = byteToCharArray(memoryData);
    }


    private String Extend(String str){
        StringBuilder result = new StringBuilder(str);
        for(int i = 0;i < 32-str.length();i++){
            result.append("0");
        }
        return result.toString();
    }
    private String lowerExtend(String str){
        String result = str;
        for(int i = 0;i < 32-str.length();i++){
            result="0"+result;
        }
        return result;
    }
    /*
    * 执行周期需要根据不同的opcode进行不同的操作。此处add指令可以调用ALU中已经实现好的加法进行。
    * 请将对应结果存到相应的位置中， 在测试中，我们将对寄存器和主存进行检查。本次实验我们不设置
    * 隐藏用例，请同学们认真阅读测试用例中的memory部分进行debug工作。

    有两个指令在执行阶段需要我们特殊关注：

    jalr: 保存并跳转指令。在改变PC之前，我们要先将返回的位置保存到ra寄存器中，我们规定GPR的
    * 第1个寄存器是返回地址寄存器（第0个GPR寄存器保存0）
    ecall: 系统调用中断指令。同样要保存返回位置，同时要设置中断控制器。
    请注意，寄存器和立即数的下标在指令中为了方便处理采用大端存储的方式，即从低到高直接截取转化为
    * 十进制即可。不明白的同学请参考测试用例。
    * */
    /** 执行周期 */
    private void operate() {
        int rs1 = Integer.parseInt(String.valueOf(IR).substring(15, 20), 2);
        int rs2 = Integer.parseInt(String.valueOf(IR).substring(20, 25), 2);
        DataType rs1_data = new DataType(String.valueOf(GPR[rs1]));
        DataType rs2_data = new DataType(String.valueOf(GPR[rs2]));
        DataType result=null;
        String result_str=null;
        int rd = Integer.parseInt(String.valueOf(IR).substring(7, 12), 2);
        // TODO
        String opcode = new String(IR).substring(0, 7);
        if(opcode.equals("1100110")) {
            //add
            //changing char[] to DataType and using ALU
            result = alu.add(rs1_data, rs2_data);
            //changing DataType to char[]
            result_str = result.toString();
            for (int i = 0; i < 32; i++) {
                GPR[rd][i] = result_str.charAt(i);
            }
            //GPR[rd] = alu.add(GPR[rs1], GPR[rs2]);
        }
        else if (opcode.equals("1100100"))
        {
            //addi
            rd = Integer.parseInt(String.valueOf(IR).substring(7, 12), 2);
            //changing char[] to DataType and using ALU
            result = alu.add(rs1_data, new DataType(lowerExtend(String.valueOf(IR).substring(20, 32))));
            //changing DataType to char[]
            result_str = result.toString();
            GPR[rd] = result_str.toCharArray();
        } else if (opcode.equals("1100000")) {
            //lw
            //changing char[] to DataType and using ALU
            result = alu.add(rs1_data, new DataType(lowerExtend(String.valueOf(IR).substring(20, 32))));
            //changing DataType to char[]
            result_str = result.toString();
            //load it from memory to rd
            GPR[rd] = byteToCharArray(Memory.getMemory().read(result_str, 4));
        }
        else if(opcode.equals("1110011")){
            //jalr
            //save PC to rd
            String imm = Extend(String.valueOf(IR).substring(20,32));
            GPR[1] = PC;
            //change PC to int
            //get the result of PC+4 TO rd
            int pc = Integer.parseInt(String.valueOf(GPR[1]),2);
            //pc += 4;
            //change PC to char
            String pc_str = Integer.toBinaryString(pc);
            int len = pc_str.length();
            for(int i = 0;i < 32-len;i++){
                pc_str = "0" + pc_str;
            }
            GPR[rd] = pc_str.toCharArray();
            //store the result of rs1 + imm to PC
            pc = Integer.parseInt(String.valueOf(GPR[rs1]),2);
            pc += Integer.parseInt(imm,2);
            //change PC to char
            pc_str = Integer.toBinaryString(pc);
            len = pc_str.length();
            for(int i = 0;i < 32-len;i++){
                pc_str = "0" + pc_str;
            }
            PC = pc_str.toCharArray();
        }
        else if(opcode.equals("1100111")){
            //ecall
            //ecall: 系统调用中断指令。同样要保存返回位置，同时要设置中断控制器
            //save PC to ra
            GPR[1] = PC;
            //set interrupt signal
            interruptController.signal = true;


        }
        else if(opcode.equals("1110110")){
            //lui
            //load imm to rd
            //extern imm to 32 bits
            String imm = Extend(String.valueOf(IR).substring(12,32));
            GPR[rd] = imm.toCharArray();
        }
    }

    private String Read_Jump_Imm(String str){
        StringBuilder Jump_Imm = new StringBuilder();
        Jump_Imm.append(str.substring(21,31));
        Jump_Imm.append(str.charAt(20));
        Jump_Imm.append(str.substring(12,20));
        Jump_Imm.append(str.charAt(31));
        return Jump_Imm.toString();
    }
    /** 执行中断操作 */
    private void interrupt(){
        //save PC to ra
        GPR[1] = PC;
        //change PC to int
        int pc = Integer.parseInt(String.valueOf(PC),2);
        pc = 0;
        //change PC to char
        String pc_str = Integer.toBinaryString(pc);
        int len = pc_str.length();
        for(int i = 0;i < 32-len;i++){
            pc_str = "0" + pc_str;
        }
        PC = pc_str.toCharArray();
        //set interrupt signal
        interruptController.signal = true;
        //handle interrupt
        interruptController.handleInterrupt();
    }

    public class InterruptController{
        // 中断信号：是否发生中断
        public boolean signal;
        public StringBuffer console = new StringBuffer();
        /** 处理中断 */
        public void handleInterrupt(){
            console.append("ecall ");
        }
        public void reset(){
            signal = false;
            console = new StringBuffer();
        }
    }

    // 以下一系列的get方法用于检查寄存器中的内容进行测试，请勿修改

    // 假定代码程序存储在主存起始位置，忽略系统程序空间
    public void loadPC(){
        PC = GPR[0];
    }

    public char[] getRA() {
        //规定第1个寄存器为返回地址寄存器
        return GPR[1];
    }

    public char[] getGPR(int i) {
        return GPR[i];
    }
}
