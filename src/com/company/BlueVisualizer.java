package com.company;


import com.company.GUI.FIFOPanelSet;
import com.company.GUI.InfoMatching;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.regex.Pattern;


public class BlueVisualizer {
    // data after parsing
    static public HashMap<String, Type> typeMap;
    static public HashMap<String,String> interfaceMap;
    static public HashMap<String,String> symbolMap;
    static public List<HashMap<String,FIFO>> Timeline = new ArrayList<>();
    static public HashSet<String> FIFOSet = new HashSet<>(); //candidate for user to choose to visualize
    static public HashSet<String> regSet= new HashSet<>(); // candidate for user to choose to visualize
    static private String timeUnit;


    public static List<String> FIFOList; // user's choice will be saved here
    static protected List<String> regList; //user's choice will be saved here
    static private String msg="";
    static private int cycle = 1;

    //us
    static protected HashMap<Integer,Integer> dataHazardCnt = new HashMap<>();
    static protected HashMap<Integer,Integer> controlHazardCnt = new HashMap<>();
    static protected HashMap<Integer,Integer> instTotCnt = new HashMap<>();
    static int datacnt = 0;
    static int controlcnt =0;
    static int instCnt =0;
    static JFrame frame;

    public static void main(String[]args) throws IOException {
        typeMap = new HashMap<>();
        interfaceMap = new HashMap<>();
        symbolMap = new HashMap<>();
        BufferedReader br = new BufferedReader(new FileReader("C:/Users/ADMIN/Desktop/instance.txt"));
        mkInterfaceMap(br);
        interfaceMap.entrySet().forEach(System.out::println);
        br.close();
        br= new BufferedReader(new FileReader("C:/Users/ADMIN/Desktop/type.txt"));
        mkTypeMap(br);
        typeMap.entrySet().forEach(a->{
            Type type = a.getValue();
            System.out.println(type.intf);
            for(Type t:type.sub){
                System.out.print(t.name+" ");
            }
            System.out.println();
        });
        br= new BufferedReader(new FileReader("C:/Users/ADMIN/Desktop/dump.vcd"));
        mkSMTL(br);
        for(Map.Entry<String,String> k : symbolMap.entrySet()) System.out.println(k.getKey()+" "+k.getValue());

        for(int i=2; i< 500; i++) {
            FIFO tmp = readTimeline(i, "f2d");
            System.out.println(i + "" +tmp.full + "" + tmp.enq + "" + tmp.deq);
        }


        frame = new JFrame("Visualizer");

        frame.setLayout(new FlowLayout());
        JLabel description= new JLabel("Choose the number of FIFO and register to visualize and match the name each.");
        //Main Panel
        JLabel title = new JLabel("BlueVisualizer");

        //Intro Panel
        Panel introPanel = new Panel();
        InfoMatching manageFIFO = new InfoMatching("FIFO",FIFOSet);
        InfoMatching manageReg = new InfoMatching("Register",regSet);
        JButton submit =  new JButton("submit");
        submit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                FIFOList = manageFIFO.getPosName();
                regList = manageReg.getPosName();
                for(int i =2; i<Timeline.size();i++) stallCnt(i);

                frame.setSize(new Dimension(1100,800));
                redraw();
            }
        });
        introPanel.add(description);
        introPanel.add(manageFIFO);
        introPanel.add(manageReg);
        introPanel.add(submit);
        frame.add(description);
        frame.add(introPanel);
        frame.add(submit);
        frame.setPreferredSize(new Dimension(500,500));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    private static void mkTypeMap(BufferedReader br) throws IOException {
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
                String[] eles = splitWithP(st,'{','}');
                for(String ele : eles){
                    String[] pieces = ele.split(" ");
                    list.add(new Type(pieces[0],pieces[1],Integer.parseInt(pieces[2])));
                }
                type = new Type(intf,size);
                type.sub = list;
                typeMap.put(intf,type);
            }
        }
    }

    private static String[] splitWithP(String st,char p, char q){
        int len = st.length();
        int front= 0;
        boolean qflag = false;
        ArrayList<String> list = new ArrayList<>();
        for(int i=0; i< len; i++){
            if(st.charAt(i)== p ){
                front= i;
                qflag= false;
            }else if(st.charAt(i)== q &&i!=front+1){
                if(qflag) break;
                list.add(st.substring(front+1,i));
                qflag=true;
            }else qflag = false;

        }

        return list.toArray(new String[list.size()]);
    }

    private static void mkInterfaceMap (BufferedReader br) throws IOException {
        Pattern pattern= Pattern.compile("/data/_element_0 .");

        String st;
        String name;
        String intf;
        while((st= br.readLine())!= null){

            if(st.matches("(.*)/data/_element_0 .(.*)")){
               String[] parts = st.split("/");

               for(int i=0;i<parts.length;i++) {
                   if (parts[i].equals("data")){
                       name = parts[i - 1];
                       intf = splitWithP(parts[parts.length-1],'(',')')[0];
                       interfaceMap.put(name,intf);
                       break;
                   }
               }
            }

        }
    }
    private static void mkSMTL(BufferedReader br)throws IOException{
        String st;
        boolean defFlag = true;
        boolean initializeFlag = true;
        int pos = -1;

        while((st=br.readLine())!=null){

            if(defFlag) {//symbol definition
                String [] info;
                String symbol;
                String name;
                if (st.matches("( *)1 [a-zA-Z]s( *)")) timeUnit = st.split(" ")[1]; //time unit
                if (st.matches("(.*)_data_0 (.*)|(.*)_data_0_ehrReg (.*)")) { //piplelined, Bypass Fifo
                     info = st.split(" ");
                     symbol = info[3];
                     name = info[4].substring(0, info[4].indexOf('_'));
                    System.out.println(symbol + name);
                    FIFOSet.add(name);
                    symbolMap.put(symbol, name);
                } else if (st.matches("(.*) rfile_[0-9]*_ehrReg (.*)")) {// register
                    info = st.split(" ");
                    String[] rawName = info[4].split("_");
                    symbol = info[3];
                    name = rawName[0] + rawName[1];
                    symbolMap.put(symbol, name);
                } else if (st.matches("(.*)_full_ehrReg (.*)")) {
                     info = st.split(" ");
                     symbol = info[3];
                     name = info[4].substring(0, info[4].indexOf('_'));
                    symbolMap.put(symbol, "f-" + name);
                } else if (st.matches("(.*)_enqP_wires_0 (.*)")) {
                    info = st.split(" ");
                    symbol = info[3];
                    name = info[4].substring(0, info[4].indexOf('_'));
                    symbolMap.put(symbol, "e-" + name);
                } else if (st.matches("(.*)_deqP_wires_0 (.*)")) {
                    info = st.split(" ");
                    symbol = info[3];
                    name = info[4].substring(0, info[4].indexOf('_'));
                    symbolMap.put(symbol, "d-" + name);
                    System.out.print("d-" + name+" "+symbol);
                }else if(st.matches("(.*) [a-zA-Z0-9]* \\$end")){
                    info = st.split(" ");
                    if(info.length<5) continue;
                    symbol = info[3];
                    name = info[4];
                    regSet.add(name);
                    symbolMap.put(symbol,name);
                }else if(st.matches("(.*)enddefinitions(.*)")) defFlag= false;
            }else {// record value
                String name ="";
                if(st.matches("0!")){// to determine the cycle start of the negedge.
                    pos++;
                    Timeline.add(new HashMap<>());
                    initializeFlag= false;
                }

                if(initializeFlag)  continue;


                  if(st.matches("#[0-9]*")) continue;
                  if(st.matches("(0|1|x)(.*)")){//for bit scalar
                    name = symbolMap.get(st.substring(1));
                    String bit = st.substring(0,1);
                    if(name==null) continue;
                    else System.out.println(name);
                    String flag = name.substring(0,2);//to check for full, enq, deq
                    if(flag.matches("(d|e|f)-")){
                        String fifoSymbol = name.substring(2);
                        if(flag.equals("d-")) System.out.println(flag);
                        boolean val = bit.equals("1");
                        FIFO currInst= Timeline.get(pos).get(fifoSymbol);// check whether there is already made FIFO instance.
                        if(currInst==null){//find the closest FIFO instance and bring it to current position
                            int tempos = pos;
                            FIFO befInst = null;
                            while(befInst==null&&tempos>0) befInst=Timeline.get(tempos--).get(fifoSymbol);
                            if(tempos==0) currInst = new FIFO(); //temporary FIFO , will be initialized in the same cycle
                            else {
                                currInst = new FIFO(befInst.name, befInst.bit, befInst.intf); //
                                currInst.getSub();
                                currInst.deq = befInst.deq;
                                currInst.enq = befInst.enq;
                                currInst.full = befInst.full;
                            }
                        }

                        if(flag.charAt(0)=='f') currInst.full= val;
                        else if(flag.charAt(0)=='d') currInst.deq= val;
                        else currInst.enq = val;
                        if(name.equals("d-f2d"))System.out.println(pos+" "+st+" "+name+" "+val+" "+currInst.deq);
                        Timeline.get(pos).put(fifoSymbol,currInst);
                    }

                    FIFO tmp = new FIFO(name,bit,interfaceMap.getOrDefault(name,"reg"));
                    Timeline.get(pos).put(name,tmp);
                }else{//for bit vector
                    String[] value= st.split(" ");
                    name = symbolMap.get(value[1]);
                    int tempos = pos;
                    if(name==null) continue;
//                    System.out.println("pla"+value[1]+name);
                    String bit = value[0].substring(1);
                    FIFO tmp = new FIFO(name,bit, interfaceMap.getOrDefault(name,"reg"));
                    tmp.getSub();

                    //setting full enq deq status for FIFO
                   if(tempos>0&&!tmp.intf.equals("reg")){
//                       System.out.println(name+" "+tempos);
                       FIFO befInst =Timeline.get(tempos--).get(name);//check if FIFO instance was already made;
                       while(befInst==null&&tempos>=0) befInst=Timeline.get(tempos--).get(name);
                       if(befInst!=null) {
                           tmp.full = befInst.full;
                           tmp.deq = befInst.deq;
                           tmp.enq = befInst.enq;
                       }
                   }

                    Timeline.get(pos).put(name,tmp);
                }
        }
    }
    }

    public static FIFO readTimeline(int idx,String name){
        FIFO target = Timeline.get(idx).get(name);
        if(target==null) return readTimeline(idx-1,name);
        else return target;
    }

    public static void cycleFinder(String mode){
        int result = cycle;
        switch(mode){
            case "first":
                result = 1;
                break;
            case "previous stall":
                result = findStall(cycle,-1);
                if(result==cycle) msg= "This is the first stall of your program"; // 수정
                break;
            case "previous":
                if(cycle == 1) msg ="This is the first cycle";
                else result--;
                break;
            case "next stall":
                result = findStall(cycle,1);
                if(result==cycle) msg = "This is the last stall of your program";
                break;
            case "next":
                if(cycle == Timeline.size()-1) msg ="This is the last cycle";
                else result++;
                break;
            case "last":
                result = Timeline.size()-1;
                break;
            default:
                result = cycle;
                msg = "button error";
                break;
        }
        cycle = result;
    }

    public static int findStall(int i, int dir) {//이전 또는 이후 stall 발생 위치. i : cycle dir: +1/-1 탐색할 방향.
        HashMap<String, FIFO> fifoInfo;
        int current = i;
        while (i > 1 && i < Timeline.size()) {
            i+=dir;
            int status =stallStatus(i);
            if(status==0||status ==1) return i;

        }
        return current;
    }

    public static void stallCnt(int cycle){ // 각 cycle마다 stall 발생 횟수  또한 visualize 전 cycle 별로 빠진 FIFO가 있으면 앞 cycle에서 가져와 채워 넣는다.
                                            // vcd 파일에서 cycle별로 값의 변화가 없으면 기록되지 않으므로 cycle 별로 hashmap에 없는 fifo, register가 존재한다.

        HashMap<String, FIFO> preFIFOInfo = Timeline.get(cycle-1);
        HashMap<String, FIFO> currFIFOInfo =Timeline.get(cycle);

        for(String str: FIFOList){ // cycle 별로 빠진 FIFO가 있다면 앞에서 가져오기.
            if(currFIFOInfo.get(str)==null){
                if(preFIFOInfo.get(str)==null) return;
                currFIFOInfo.put(str,preFIFOInfo.get(str));
            }
            Timeline.set(cycle,currFIFOInfo);
        }

        String fstFifo = FIFOList.get(0);
        String sndFifo = FIFOList.get(1);
        String lastFifo = FIFOList.get(FIFOList.size()-1);

        if(! currFIFOInfo.get(fstFifo).full) controlHazardCnt.put(cycle,++controlcnt);
        else if( ! currFIFOInfo.get(sndFifo).full && preFIFOInfo.get(fstFifo).full) dataHazardCnt.put(cycle,++datacnt);

        if(currFIFOInfo.get(lastFifo).full) instTotCnt.put(cycle,++instCnt);
    }

    public static int stallStatus(int cycle){
        int num=cycle;
        int result = -1;
        int control= controlHazardCnt.getOrDefault(cycle,0);
        int data=dataHazardCnt.getOrDefault(cycle,0);
        int inst;

        if(control != 0){
            msg+="Control Hazard occurred at cycle"+cycle+"\n";
            result = 0;// control hazard
        }
        else if(data != 0){
            msg+="Data Hazard occurred at cycle"+cycle+"\n";
            result = 1;// data hazard
        }

        while((control=controlHazardCnt.getOrDefault(num--,0))== 0&&num!=0); // 현재 cycle보다 작은 cycle에서 control, data hazard 수와 instruction 수를 찾는다.
            num = cycle;
        while((data=dataHazardCnt.getOrDefault(num--,0))== 0&&num !=0);
            num = cycle;
        while((inst=instTotCnt.getOrDefault(num--,0))== 0&&num != 0);

        msg += "Total control Hazards: "+ control+"\n";
        msg += "Total data Hazards: "+ data+"\n";
        msg += "Total Instructions: "+ inst+"\n";
        return result;
    }

    // GUI class and information.
    public static class LeftSubPanel extends Panel {
        public LeftSubPanel() {

            // leftSubPanel
            msg ="";
            int status = stallStatus(cycle);
            setLayout(new FlowLayout());
            setPreferredSize(new Dimension(1000,1200));
            FIFOPanelSet fifoPanelSet = new FIFOPanelSet(FIFOList.size(),status, FIFOList, Timeline.get(cycle));
            Panel control = new Panel();
            control.setLayout(new FlowLayout());
            JButton fir = new JButton("fir");
            JButton prs = new JButton("bes");
            JButton pre = new JButton("bef");
            JButton nxt = new JButton("nxt");
            JButton nxs = new JButton("nxs");
            JButton lst = new JButton("lst");
            JLabel cycletxt = new JLabel("Cycle: ");
            JTextField cycleNum = new JTextField(String.valueOf(cycle),10);
            JTextArea description = new JTextArea(msg,5,50);
            fir.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    cycleFinder("first");
                    redraw();

                }
            });
            prs.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    cycleFinder("previous stall");
                    redraw();
                }
            });

            pre.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    cycleFinder("previous");
                    redraw();
                }
            });
            nxt.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    cycleFinder("next");
                    redraw();
                }
            });
            nxs.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    cycleFinder("next stall");
                    redraw();
                }
            });
            lst.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    cycleFinder("last");
                    redraw();
                }
            });

            cycleNum.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    cycle = Integer.parseInt(cycleNum.getText());
                    redraw();
                }
            });

            control.add(fir);
            control.add(prs);
            control.add(pre);
            control.add(nxt);
            control.add(nxs);
            control.add(lst);
            control.add(cycletxt);
            control.add(cycleNum);

            add(fifoPanelSet);
            add(control);
            add(description);
        }

    }

    public static void redraw(){
        frame.getContentPane().removeAll();
        frame.add(new LeftSubPanel());
        frame.revalidate();
    }

}


