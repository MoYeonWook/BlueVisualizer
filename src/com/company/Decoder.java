package com.company;


// reference from Computer Architecture Lab - ProcTypes.bsv
public class Decoder {

    //OPCODE
    final static String opLoad = "0000011";
    final static String opMiscMem =" 0001111";
    final static String opOPImm = "0010011";
    final static String opAuipc = "0010111";
    final static String opStore = "0100011";
    final static String opAmo = "0101111";
    final static String opOP = "0110011";
    final static String opLui = "0110111";
    final static String opBranch = "1100011";
    final static String opJalr ="1100111";
    final static String opJal = "1101111";
    final static String opSystem = "1110011";

    //function code
    //ALU
    final static String ADD = "000";
    final static String SLL = "001";
    final static String SLT = "010";
    final static String SLTU ="011";
    final static String XOR ="100";
    final static String SR ="101";
    final static String OR ="110";
    final static String AND ="111";
    //Branch
    final static String BEQ = "000";
    final static String BNE = "001";
    final static String BLT = "100";
    final static String BGE = "101";
    final static String BLTU = "110";
    final static String BGEU = "111";
    //Load & Store
    final static String LW ="010";
    final static String SW ="010";
    //System
    final static String CSRRW ="001";
    final static String CSRRS = "010";

    final static int INSTSIZE = 32;
    public Decoder(){}
    public String decode(String inst){
        String ret;
       int len = inst.length();
       if(len< INSTSIZE){
           for(int i =0; i< INSTSIZE-len; i++) inst= "0"+ inst;
       }

       String opcode = inst.substring(25);
       String rd = "x"+Integer.parseInt(inst.substring(20,25),2);
       String funct3 =inst.substring(17,20);
       String rs1 = "x"+ Integer.parseInt(inst.substring(12,17),2);
       String rs2 = "x" + Integer.parseInt(inst.substring(7,12), 2);
       String funct7 = inst.substring(0,7);
       Boolean aluSel =(inst.substring(1,2).equals("0")) ? true : false ;

       String immI = convertToAbsolute(inst.substring(0,12));
       String immS = convertToAbsolute(inst.substring(0,7)+inst.substring(20,25));
       String immB = convertToAbsolute(inst.substring(0,1)+inst.substring(24,25)+inst.substring(1,7)+inst.substring(20,24)+"1");
       String immU = convertToAbsolute(inst.substring(0,20)); //12 following zeros
       String immJ = convertToAbsolute(inst.substring(0,1)+inst.substring(12,20)+inst.substring(11,12)+inst.substring(1,7)+inst.substring(7,11)+"1");

       int immItoInt = Integer.parseInt(immI,2);
       long immUtoInt = Long.parseLong(immU,2);
       int immBtoInt = Integer.parseInt(immB,2);
       int immJtoInt = Integer.parseInt(immJ,2);



       switch (opcode){
           case opOPImm: {
               switch (funct3) {
                   case ADD:
                       ret = "addi";
                       break;
                   case SLT:
                       ret = "slti";
                       break;
                   case SLTU:
                       ret = "sltiu";
                       break;
                   case AND:
                       ret = "andi";
                       break;
                   case OR:
                       ret = "ori";
                       break;
                   case XOR:
                       ret = "xori";
                       break;
                   case SLL:
                       ret = "slli";
                       break;
                   case SR:
                       ret = (aluSel) ? "srli" : "srai";
                       break;
                   default:
                       ret = "N/A";
                       break;
               }
               ret +=" "+rd+", "+rs1+", ";
               ret += (funct3.equals(SLL) || funct3.equals(SR)) ? Integer.parseInt(immI.substring(7, 12)) : immItoInt;
           }
           break;
           case opOP:{
               switch(funct3){
                   case ADD : ret = (aluSel) ? "add": "sub";
                       break;
                   case SLT : ret = "slt";
                       break;
                   case SLTU : ret = "sltu";
                       break;
                   case AND : ret = "and";
                       break;
                   case OR: ret = "or";
                       break;
                   case XOR: ret = "xor";
                       break;
                   case SLL: ret = "sll";
                       break;
                   case SR: ret = (aluSel) ?  "srl" : "sra";
                       break;
                   default : ret = "N/A";
                   break;
               }
               ret +=" "+rd+", "+rs1+", "+rs2;
           }
           break;
           case opLui: ret ="lui"+" "+rd+", "+immUtoInt+">>12";
           break;
           case opAuipc: ret ="auipc"+" "+rd+", "+immUtoInt+">>12";
           break;
           case opJal: ret ="jal"+" "+rd+", "+immJtoInt;
           break;
           case opJalr: ret ="jalr"+" "+rd+", "+immItoInt;
           break;
           case opBranch:{
               switch(funct3){
                   case BEQ : ret = "beq";
                   break;
                   case BNE : ret ="bne";
                   break;
                   case BLT : ret = "blt";
                   break;
                   case BLTU : ret = "bltu";
                   break;
                   case BGE : ret = "bge";
                   break;
                   case BGEU : ret = "bgeu";
                   break;
                   default: ret = "N/A";
                   break;
               }
               ret +=" "+rs1+", "+rs2+", "+immBtoInt;
           }
           case opLoad: ret="lw "+rd+", "+immItoInt+" ("+ rs1+")";
           break;
           case opStore: ret="sw "+rs1+", "+immItoInt+" ("+ rs2+")";
           break;
           case opSystem: ret= " system operation";
           break;
           default: ret= "nop";
       }

       return ret;
    }
    public String convertToAbsolute(String bin){
        int len = bin.length();
        String ret="";
        if(bin.charAt(0)=='0') ret = bin;
        else {
            while (--len > 0) {
                if (bin.charAt(len) == '0') ret = '1' + ret;
                else ret = '0' + ret;
            }
            ret = "-" + Integer.toBinaryString(Integer.parseInt(ret,2) + 1);
        }
        return ret;
    }
}
