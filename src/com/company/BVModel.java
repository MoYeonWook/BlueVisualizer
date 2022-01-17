package com.company;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Pattern;

public class BVModel {

    private HashMap<String, Type> typeMap = new HashMap<>();
    private HashMap<String,String> interfaceMap = new HashMap<>();
    private HashMap<String,String> symbolMap = new HashMap<>();
    private List<HashMap<String,Instance>> timeLine = new ArrayList<>();
    private HashSet<String> FIFOSet = new HashSet<>(); //candidate for user to choose to visualize
    private HashSet<String> regSet= new HashSet<>(); // candidate for user to choose to visualize
    private List<String> rfile = new ArrayList<>();
    private Decoder decoder = new Decoder();
    private List<String> FIFOList;
    private List<String> regList;
    private String[] AsmInFIFO;
    private HashMap<Integer,Integer> hazardRec = new HashMap<>();
    private HashMap<Integer,Integer> controlHazardRec = new HashMap<>();
    private HashMap<Integer,Integer> instRec = new HashMap<>();
    private BitType bitBitType = BitType.BIN;
    private boolean asmMode = false;
    private boolean detectControlHazard = false;
    private int hazardCnt = 0;
    private int controlHazardCnt =0;
    private int instCnt =0;
    private int cycle = 1;

    private String msg = "";

    public BVModel() throws IOException {
        String addr = "./";
        BufferedReader br = new BufferedReader(new FileReader(addr+ "instance.txt"));

        System.out.println("Interface map parsing...");
        mkInterfaceMap(br);
        br.close();

        System.out.println("Instance symbol parsing...");
        br= new BufferedReader(new FileReader(addr+ "type.txt"));
        mkTypeMap(br);
        br.close();

        br= new BufferedReader(new FileReader(addr+ "dump.vcd"));
        mkTimeline(br);//make symbolmap timeline
        br.close();
    }

    private void mkTypeMap(BufferedReader br) throws IOException {
        String st;
        while((st= br.readLine())!= null){
            String[] part = st.split(" ");
            int len = part.length;
            ArrayList<Type> list;
            Type type;
            if(part[0].equals("STRUCT")){
                list = new ArrayList<>();
                String intf= part[1];
                int size = Integer.parseInt(part[2]);
                String[] eles = splitWithBrackets(st,'{','}');
                for(String ele : eles){
                    try {
                        String[] pieces = ele.split(" ");
                        list.add(new Type(pieces[0], pieces[1], Integer.parseInt(pieces[2])));
                    } catch (Exception ignored) {}
                }
                type = new Type(intf,size);
                type.setSub(list);
                typeMap.put(intf,type);
            }
        }
    }

    private void mkInterfaceMap (BufferedReader br) throws IOException {
        String st;
        String name;
        String intf;
        
        while((st= br.readLine())!= null){

            if(st.matches("(.*)/data/_element_0 .(.*)")){
                String[] parts = st.split("/");

                for(int i=0;i<parts.length;i++) {
                    if (parts[i].equals("data")){
                        name = parts[i - 1];
                        intf = splitWithBrackets(parts[parts.length-1],'(',')')[0];
                        if(parts[parts.length-1].matches("(.*)Maybe#(.*)")) intf ="Maybe#_" + intf; //check for maybe case
                        interfaceMap.put(name,intf);
                        break;
                    }
                }
            }

        }
    }
    
    private void mkTimeline(BufferedReader br)throws IOException{
        String st;
        boolean defFlag = true;
        boolean initializeFlag = true;
        int timelineCycle = -1;
        FIFO currInst;
        // timelineCycle == 0 => initialization/ timelineCycle == 1 => cycle start

        while((st=br.readLine())!=null){
            if(defFlag){
                mkSymbolMap(st);//symbol definition
                if(st.matches("(.*)enddefinitions(.*)")) defFlag= false;
            }else{// record value
                String name;
                if(st.matches("0!")){// to determine the cycle start of the negedge clock/ is going to ignore the first initialization of dumpvar section.
                    timelineCycle++;
                    timeLine.add(new HashMap<>());
                    initializeFlag= false;
                }

                if(initializeFlag)  continue;
                if(st.matches("#[0-9]*")) continue;
                if(st.matches("([01x])(.*)")){//for bit scalar
                    name = symbolMap.get(st.substring(1));
                    String bit = st.substring(0,1);
                    boolean val = bit.equals("1");
                    boolean isBitVector = false;
                    if(name==null) continue;

                    String flag = name.substring(0,2);//to check for full, enq, deq, empty => flag : fu, en, de, em
                    if(flag.matches("(de|en|fu|em)")){
                        name = name.substring(3); // remove flag and get FIFO name
                        currInst = referPastInstance(isBitVector,name,bit,timelineCycle);
                        switch (flag){
                            case "fu" :
                                currInst.full= val; currInst.empty =!val;
                                break;
                            case "de" :
                                currInst.deq=val;
                                break;
                            case "em" :
                                currInst.empty= val; currInst.full =!val;
                                break;
                            case "en" :
                                currInst.enq = val;
                                break;
                            default: break;
                        }

                    } else{
                        currInst = new FIFO(name,bit,interfaceMap.getOrDefault(name,"reg"));
                    }
                    timeLine.get(timelineCycle).put(name,currInst);

                }else if(st.matches("b[0-9](.*)")){//for bit vector
                    String[] value= st.split(" ");
                    name = symbolMap.get(value[1]);
                    Boolean isBitVector = true;
                    if(name==null) continue;
                    String bit = value[0].substring(1);
                    currInst = referPastInstance(isBitVector,name,bit,timelineCycle);
                    timeLine.get(timelineCycle).put(name,currInst);
                }
            }
        }
    }

    private void mkSymbolMap(String parsedline){
        String [] splits;
        String symbol;
        String name;
        if (parsedline.matches("(.*)_data_0 (.*)|(.*)_data_0_ehrReg (.*)")) { //piplelined, Bypass Fifo
            splits = parsedline.split(" ");
            symbol = splits[3];
            name = splits[4].substring(0, splits[4].indexOf('_'));
            FIFOSet.add(name);
            symbolMap.put(symbol, name);
        } else if (parsedline.matches("(.*) rfile_[0-9]*_ehrReg (.*)|(.*) rfile_[0-9]* (.*)")) {// register
            splits = parsedline.split(" ");
            String[] rawName = splits[4].split("_");
            symbol = splits[3];
            name = rawName[0] + rawName[1];
            symbolMap.put(symbol, name);
        } else if (parsedline.matches("(.*)_full_ehrReg (.*)")) {
            splits = parsedline.split(" ");
            symbol = splits[3];
            name = splits[4].substring(0, splits[4].indexOf('_'));
            symbolMap.put(symbol, "fu-" + name);
        } else if (parsedline.matches("(.*)_empty_ehrReg (.*)")) {
            splits = parsedline.split(" ");
            symbol = splits[3];
            name = splits[4].substring(0, splits[4].indexOf('_'));
            symbolMap.put(symbol, "em-" + name);
        }else if (parsedline.matches("(.*)_enqP_wires_0 (.*)")) {
            splits = parsedline.split(" ");
            symbol = splits[3];
            name = splits[4].substring(0, splits[4].indexOf('_'));
            symbolMap.put(symbol, "en-" + name);
        } else if (parsedline.matches("(.*)_deqP_wires_0 (.*)")) {
            splits = parsedline.split(" ");
            symbol = splits[3];
            name = splits[4].substring(0, splits[4].indexOf('_'));
            symbolMap.put(symbol, "de-" + name);
        }else if(parsedline.matches("(.*) [a-zA-Z0-9]* \\$end")) {
            splits = parsedline.split(" ");
            if (splits.length >= 5) {
                symbol = splits[3];
                name = splits[4];
                regSet.add(name);
                symbolMap.put(symbol, name);
            }
        }
    }

    private String[] splitWithBrackets(String st, char front, char back){
        int len = st.length();
        int pos= 0;
        boolean qflag = false;
        ArrayList<String> list = new ArrayList<>();
        for(int i=0; i< len; i++){
            if(st.charAt(i)== front ){
                pos= i;
                qflag= false;
            }else if(st.charAt(i)== back &&i!=pos+1){
                if(qflag) break;
                list.add(st.substring(pos+1,i));
                qflag=true;
            }else qflag = false;

        }
        return list.toArray(new String[list.size()]);
    }


    private FIFO referPastInstance(Boolean isBitVector, String instanceName, String bit, int timelineCycle){
        FIFO befInstance = null;
        FIFO currInstance;

        while (true) {// find the closest already-made-FIFO
            befInstance = (FIFO) timeLine.get(timelineCycle--).get(instanceName);
            if (timelineCycle <= 0 || befInstance != null) break;
        }

        if(isBitVector) currInstance =  new FIFO(instanceName,bit, interfaceMap.getOrDefault(instanceName,"reg"));
        else if(befInstance != null){
            currInstance = new FIFO(befInstance.getName(),befInstance.getBit(),befInstance.getIntf());
        }
        else currInstance = new FIFO(); //temporary uninitialized, will be initialized with same-cycle bitvector instance.

        if(befInstance != null) {
            currInstance.setFull(befInstance.isFull());
            currInstance.setEmpty(befInstance.isEmpty());
            currInstance.setEnq(befInstance.isEnq());
            currInstance.setDeq(befInstance.isDeq());
        }
        currInstance.getSub(typeMap);

        return currInstance;
    }
    
    public void initializeStat(int cycle){ // 각 cycle마다 stall 발생 횟수  또한 visualize 전 cycle 별로 빠진 FIFO가 있으면 앞 cycle에서 가져와 채워 넣는다.
        // vcd 파일에서 cycle별로 값의 변화가 없으면 기록되지 않으므로 cycle 별로 hashmap에 없는 fifo, register가 존재한다.

//        Timeline.get(cycle).get(regList.get(0)).

        if(cycle == 0) return;  //first cycle does not need fifo initializations or hazard checks.

        HashMap<String, Instance> preInfoMap = timeLine.get(cycle-1);
        HashMap<String, Instance> currInfoMap = timeLine.get(cycle);

        //initialize FIFO and Register
        initRegFIFO("eEpoch",preInfoMap,currInfoMap,cycle);
        for(String target: regList) initRegFIFO(target,preInfoMap,currInfoMap,cycle);
        for(String target: rfile) initRegFIFO(target,preInfoMap,currInfoMap,cycle);
        for(String target: FIFOList) initRegFIFO(target,preInfoMap,currInfoMap,cycle);

        //initialize assembly code for each FIFO
        initAsm(preInfoMap,currInfoMap,cycle);

        //initialize FIFO status
        initFIFOStatus(preInfoMap,currInfoMap,cycle);
    }

    private void initRegFIFO(String target,HashMap<String, Instance> preInfoMap,HashMap<String, Instance> currInfoMap,int cycle){
        if(currInfoMap.get(target)==null){
            if(preInfoMap.get(target) == null) currInfoMap.put(target,new FIFO(target,"0",interfaceMap.getOrDefault(target,"reg")));
            else currInfoMap.put(target,preInfoMap.get(target));
        }
        timeLine.set(cycle,currInfoMap);
    }

    private void initAsm(HashMap<String, Instance> preInfoMap,HashMap<String, Instance> currInfoMap,int cycle){
        for(int i=0; i<FIFOList.size();i++){ //initialize asm code
            String targetFIFOName= FIFOList.get(i);
            FIFO currTargetFIFO = (FIFO) currInfoMap.get(targetFIFOName);
            String asm="";

            if(currTargetFIFO == null) break; // early empty FIFO. should not be filled with asm code
            if(i==0){
                if(currTargetFIFO.empty||!currTargetFIFO.full) asm ="nop";
                else {
                    for (Instance inst : currTargetFIFO.getChildren()) {
                        if (inst.getName().equals("inst")) {
                            asm = decoder.decode(inst.getBit());
                            break;
                        }
                    }
                }
            }else{ // 1) preTargetFIFO enq (true) => get preAheadFIFO asm code;
                // 2) preTargetFIFO deq (true) => set asm as nop;
                // 3) else => get preTargetFIFO asm code;
                String aheadFifoName = FIFOList.get(i-1);
                FIFO preTargetFIFO = (FIFO) preInfoMap.get(targetFIFOName);
                FIFO preAheadFIFO = (FIFO) preInfoMap.get(aheadFifoName);
                if(preTargetFIFO == null) break;

                if(preTargetFIFO.enq) asm = preAheadFIFO.asm;
                else if(preTargetFIFO.deq) asm = "nop";
                else asm = preTargetFIFO.asm;
            }
            currTargetFIFO.asm = asm;
        }

        timeLine.set(cycle,currInfoMap);
    }

    public void initFIFOStatus(HashMap<String, Instance> preInfoMap,HashMap<String, Instance> currInfoMap,int cycle){
        int len = FIFOList.size();

        for( int i =0 ; i < len ; i++){ //evaluate FIFO status

            String targetFIFOname = FIFOList.get(i);
            FIFO targetFIFO = (FIFO) currInfoMap.get(targetFIFOname);
            if(targetFIFO == null||targetFIFO.getAsm().equals("empty"))  continue;

            if(targetFIFO.isFull()){
                if(!targetFIFO.isDeq()&&!targetFIFO.isEnq()){
                    targetFIFO.setStatusType(FIFOStatusType.StallFull); //type 3
                }else if(targetFIFO.isMaybe()&&!targetFIFO.isValid()){
                    targetFIFO.setStatusType(FIFOStatusType.Invalid); // type 4
                }else targetFIFO.setStatusType(FIFOStatusType.Full); //type 1
            }else if(targetFIFO.isEmpty()){
                targetFIFO.setStatusType(FIFOStatusType.StallEmpty); // type 2
            }


            if(targetFIFO.getStatusType()==FIFOStatusType.StallEmpty||targetFIFO.getStatusType()==FIFOStatusType.Invalid) { // evalulate hazard occurance
                if (i != 0) {
                    String preFIFOname = FIFOList.get(i - 1);
                    FIFO preFIFO = (FIFO) preInfoMap.get(preFIFOname);
                    if (preFIFO == null) continue;
                    if (preFIFO.getStatusType() == FIFOStatusType.Full||preFIFO.getStatusType()==FIFOStatusType.StallFull)
                        hazardRec.put(cycle,++hazardCnt);

                } else {
                    hazardRec.put(cycle,++hazardCnt);
                }
            }



            if(i==len - 1 && targetFIFO.isDeq())  {instRec.put(cycle,++instCnt);}
        }

        if(detectControlHazard&&preInfoMap.get("eEpoch")!=null){
            if(!currInfoMap.get("eEpoch").equals(preInfoMap.get("eEpoch"))){
                controlHazardRec.put(cycle,++controlHazardCnt);
            }
        }

    }


    public HashMap<String, Type> getTypeMap() {
        return typeMap;
    }

    public void setTypeMap(HashMap<String, Type> typeMap) {
        this.typeMap = typeMap;
    }

    public HashMap<String, String> getInterfaceMap() {
        return interfaceMap;
    }

    public void setInterfaceMap(HashMap<String, String> interfaceMap) {
        this.interfaceMap = interfaceMap;
    }

    public HashMap<String, String> getSymbolMap() {
        return symbolMap;
    }

    public void setSymbolMap(HashMap<String, String> symbolMap) {
        this.symbolMap = symbolMap;
    }

    public List<HashMap<String, Instance>> getTimeLine() {
        return timeLine;
    }

    public void setTimeLine(List<HashMap<String, Instance>> timeLine) {
        this.timeLine = timeLine;
    }

    public HashSet<String> getFIFOSet() {
        return FIFOSet;
    }

    public void setFIFOSet(HashSet<String> FIFOSet) {
        this.FIFOSet = FIFOSet;
    }

    public HashSet<String> getRegSet() {
        return regSet;
    }

    public void setRegSet(HashSet<String> regSet) {
        this.regSet = regSet;
    }

    public List<String> getFIFOList() {
        return FIFOList;
    }

    public void setFIFOList(List<String> FIFOList) {
        this.FIFOList = FIFOList;
    }

    public List<String> getRegList() {
        return regList;
    }

    public void setRegList(List<String> regList) {
        this.regList = regList;
    }

    public HashMap<Integer, Integer> getHazardRec() {
        return hazardRec;
    }

    public void setHazardRec(HashMap<Integer, Integer> hazardRec) {
        this.hazardRec = hazardRec;
    }

    public HashMap<Integer, Integer> getControlHazardRec() {
        return controlHazardRec;
    }

    public void setControlHazardRec(HashMap<Integer, Integer> controlHazardRec) {
        this.controlHazardRec = controlHazardRec;
    }

    public HashMap<Integer, Integer> getInstRec() {
        return instRec;
    }

    public void setInstRec(HashMap<Integer, Integer> instRec) {
        this.instRec = instRec;
    }

    public BitType getBitRepresentation() {
        return bitBitType;
    }

    public void setBitRepresentation(BitType bitBitType) {
        this.bitBitType = bitBitType;
    }

    public boolean isAsmMode() {
        return asmMode;
    }

    public void setAsmMode(boolean asmMode) {
        this.asmMode = asmMode;
    }

    public int getHazardCnt() {
        return hazardCnt;
    }

    public void setHazardCnt(int hazardCnt) {
        this.hazardCnt = hazardCnt;
    }
    public boolean canDetectControlHazard() {
        return detectControlHazard;
    }

    public void setDetectControlHazard(boolean detectControlHazard) {
        this.detectControlHazard = detectControlHazard;
    }

    public int getControlHazardCnt() {
        return controlHazardCnt;
    }

    public void setControlHazardCnt(int controlHazardCnt) {
        this.controlHazardCnt = controlHazardCnt;
    }

    public int getInstCnt() {
        return instCnt;
    }

    public void setInstCnt(int instCnt) {
        this.instCnt = instCnt;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String[] getAsmInFIFO() {
        return AsmInFIFO;
    }

    public void setAsmInFIFO(String[] asmInFIFO) {
        AsmInFIFO = asmInFIFO;
    }

    public List<String> getRfile() {
        return rfile;
    }

    public void setRfile(List<String> rfile) {
        this.rfile = rfile;
    }

    public int getCycle() {
        return cycle;
    }

    public void setCycle(int cycle) {
        this.cycle = cycle;
    }


}
