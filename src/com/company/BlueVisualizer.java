package com.company;


import com.company.GUI.FIFOPanelSet;
import com.company.GUI.InfoMatching;
import com.company.GUI.RegPanelSet;
import com.company.GUI.UserRegPanelSet;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.util.*;
import java.util.List;
import java.util.regex.Pattern;


public class BlueVisualizer {
    // data after parsing
    static public HashMap<String, Type> typeMap = new HashMap<>();
    static public HashMap<String,String> interfaceMap = new HashMap<>();
    static public HashMap<String,String> symbolMap = new HashMap<>();
    static public List<HashMap<String,Instance>> Timeline = new ArrayList<>();
    static public HashSet<String> FIFOSet = new HashSet<>(); //candidate for user to choose to visualize
    static public HashSet<String> regSet= new HashSet<>(); // candidate for user to choose to visualize
    static private String timeUnit;


    static List<String> FIFOList; // user's choice will be saved here
    static List<String> regList; //user's choice will be saved here
    static List<String> rfile = new ArrayList<>();
    static public String msg="";
    static public int cycle = 1;
    static public boolean binToHex = false;
    static JFrame frame;
    static LeftSubPanel leftSubPanel;
    static RightSubPanel rightSubPanel;

    //statistics
    static protected HashMap<Integer,Integer> dataHazardRec = new HashMap<>();
    static protected HashMap<Integer,Integer> controlHazardRec = new HashMap<>();
    static protected HashMap<Integer,Integer> instRec = new HashMap<>();
    static int dataCnt = 0;
    static int controlCnt =0;
    static int instCnt =0;

    // Font & Size
    static Font titleFont = new Font("Franklin Gothic Heavy",Font.PLAIN,100);
    static Font title2 = new Font("Candara Light",Font.PLAIN,30);
    static Dimension initialSize = new Dimension(500,450);
    static Dimension frameSize = new Dimension(1550,930);
    static Dimension topPanelSize = new Dimension(1400,100);
    static Dimension leftPanelSize = new Dimension(1000,750);
    static Dimension rightPanelSize = new Dimension(400,750);
    static Dimension rightUpperPanelSize = new Dimension(360,490);
    static Dimension rightBottomPanelSize = new Dimension(360,220);



    public static void main(String[]args) throws IOException {
        String addr = "./";
        BufferedReader br = new BufferedReader(new FileReader(addr+ "instance.txt"));

        System.out.println("Interface map parsing...");
        mkInterfaceMap(br);
        br.close();

        System.out.println("Instance symbol parsing...");
        br= new BufferedReader(new FileReader(addr+ "type.txt"));
        mkTypeMap(br);

//        typeMap.entrySet().forEach(a->{
//            Type type = a.getValue();
//            System.out.println(type.intf);
//            for(Type t:type.sub){
//                System.out.print(t.name+" ");
//            }
//            System.out.println();
//        });
        System.out.println("Initializing the timeline...");
        br= new BufferedReader(new FileReader(addr+ "test.vcd"));
        mkSymbolMapTimeLine(br);//make symbolmap timeline


//        for(Map.Entry<String,String> k : symbolMap.entrySet()) System.out.println(k.getKey()+" "+k.getValue());

        System.out.println("Choose FIFO or register to visualize in order");
        frame = new JFrame("Visualizer");

        JLabel description1 = new JLabel("Choose the number of FIFO and register to visualize.");
        JLabel description2 = new JLabel("Then match the name each.");
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
                System.out.println("Visualizing Chosen data...");
                FIFOList = manageFIFO.getPosName();
                regList = manageReg.getPosName();
                for(int i =0;i<32;i++)rfile.add("rfile"+i); // add all the name of cpu register
                for(int i =2; i<Timeline.size();i++) initializeStat(i); // start at i = 2, cycle 1 has already initialized.
                frame.setPreferredSize(frameSize);
                frame.pack();
                redraw();
            }
        });
        introPanel.add(description1);
        introPanel.add(description2);
        introPanel.add(manageFIFO);
        introPanel.add(manageReg);
        introPanel.add(submit);
        frame.setLayout(new FlowLayout(1));
        frame.add(description1);
        frame.add(description2);
        frame.add(introPanel);
        frame.add(submit);
        frame.setPreferredSize(initialSize);
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
    private static void mkSymbolMapTimeLine(BufferedReader br)throws IOException{
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
//                    System.out.println(symbol + name);
                    FIFOSet.add(name);
                    symbolMap.put(symbol, name);
                } else if (st.matches("(.*) rfile_[0-9]*_ehrReg (.*)|(.*) rfile_[0-9]* (.*)")) {// register
                    info = st.split(" ");
                    String[] rawName = info[4].split("_");
                    symbol = info[3];
                    name = rawName[0] + rawName[1];
                    symbolMap.put(symbol, name);
                } else if (st.matches("(.*)_full_ehrReg (.*)")) {
                     info = st.split(" ");
                     symbol = info[3];
                     name = info[4].substring(0, info[4].indexOf('_'));
                    symbolMap.put(symbol, "fu-" + name);
                } else if (st.matches("(.*)_empty_ehrReg (.*)")) {
                    info = st.split(" ");
                    symbol = info[3];
                    name = info[4].substring(0, info[4].indexOf('_'));
                    symbolMap.put(symbol, "em-" + name);
                }else if (st.matches("(.*)_enqP_wires_0 (.*)")) {
                    info = st.split(" ");
                    symbol = info[3];
                    name = info[4].substring(0, info[4].indexOf('_'));
                    symbolMap.put(symbol, "en-" + name);
                } else if (st.matches("(.*)_deqP_wires_0 (.*)")) {
                    info = st.split(" ");
                    symbol = info[3];
                    name = info[4].substring(0, info[4].indexOf('_'));
                    symbolMap.put(symbol, "de-" + name);
//                    System.out.print("d-" + name+" "+symbol);
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
                if(st.matches("0!")){// to determine the cycle start of the negedge clock
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
//                    else System.out.println(name);
                    String flag = name.substring(0,2);//to check for full, enq, deq, empty
                    if(flag.matches("(de|en|fu|em)")){
                        String fifoSymbol = name.substring(3);
//                       System.out.println(cycle+" "+flag+" "+fifoSymbol);
                        boolean val = bit.equals("1");
                        FIFO currInst= (FIFO) Timeline.get(pos).get(fifoSymbol);// check whether there is already made FIFO instance.
                        if(currInst==null){//find the closest FIFO instance and bring it to current position
                            int tempos = pos;
                            FIFO befInst = null;
                            while(befInst==null&&tempos>0) befInst= (FIFO) Timeline.get(tempos--).get(fifoSymbol);
                            if(tempos==0) currInst = new FIFO(); //temporary FIFO , will be initialized in the same cycle
                            else {
                                currInst = new FIFO(befInst.name, befInst.bit, befInst.intf); //
                                currInst.getSub();
                                currInst.deq = befInst.deq;
                                currInst.enq = befInst.enq;
                                currInst.full = befInst.full;
                                currInst.empty = befInst.empty;
                            }
                        }

                        if(flag.substring(0,2).equals("fu") ){currInst.full= val; currInst.empty =!val;}
                        else if(flag.substring(0,2).equals("de")) currInst.deq= val;
                        else if(flag.substring(0,2).equals("em")) {currInst.empty= val; currInst.full =!val;}
                        else if(flag.substring(0,2).equals("en"))  currInst.enq = val;
//                        System.out.println(pos+" "+fifoSymbol+" "+name+" "+val+" "+currInst.empty);
                        Timeline.get(pos).put(fifoSymbol,currInst);
                    }

                    FIFO tmp = new FIFO(name,bit,interfaceMap.getOrDefault(name,"reg"));
                    Timeline.get(pos).put(name,tmp);
                }else if(st.matches("b[0-9](.*)")){//for bit vector
                    String[] value= st.split(" ");
                    name = symbolMap.get(value[1]);
                    int tempos = pos;
                    if(name==null) continue;
//                    System.out.println("pla"+value[1]+name);
                    String bit = value[0].substring(1);
                    FIFO tmp = new FIFO(name,bit, interfaceMap.getOrDefault(name,"reg"));
                    tmp.getSub();

                    //setting full empty enq deq status for FIFO
                   if(tempos>0&&!tmp.intf.equals("reg")){
//                       System.out.println(name+" "+tempos);
                       FIFO befInst =(FIFO)Timeline.get(tempos--).get(name);//check if FIFO instance was already made;
                       while(befInst==null&&tempos>=0) befInst=(FIFO)Timeline.get(tempos--).get(name);
                       if(befInst!=null) {
                           tmp.full = befInst.full;
                           tmp.empty = befInst.empty;
                           tmp.deq = befInst.deq;
                           tmp.enq = befInst.enq;
                       }
                   }
//                    System.out.println(name);
                    Timeline.get(pos).put(name,tmp);
                }
        }
    }
    }

    public static FIFO readTimeline(int idx,String name){
        FIFO target = (FIFO) Timeline.get(idx).get(name);
        if(target==null) return readTimeline(idx-1,name);
        else return target;
    }

    public static void cycleFinder(String mode){
        int result = cycle;
        msg ="";
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
        while (i > 0 && i < Timeline.size()) {
            i+=dir;
            int status =stallStatus(i);
            if(status!=-1) return i;

        }
        return current;
    }

    public static void initializeStat(int cycle){ // 각 cycle마다 stall 발생 횟수  또한 visualize 전 cycle 별로 빠진 FIFO가 있으면 앞 cycle에서 가져와 채워 넣는다.
                                            // vcd 파일에서 cycle별로 값의 변화가 없으면 기록되지 않으므로 cycle 별로 hashmap에 없는 fifo, register가 존재한다.

        HashMap<String, Instance> preFIFOInfoMap = Timeline.get(cycle-1);
        HashMap<String, Instance> currFIFOInfoMap =Timeline.get(cycle);

        for(String str: regList){ //for register.
            if(currFIFOInfoMap.get(str)==null){
                if(preFIFOInfoMap.get(str)==null) continue;
//                System.out.println(cycle+" "+str+" "+preFIFOInfoMap.get(str).getBit());
                currFIFOInfoMap.put(str,preFIFOInfoMap.get(str));
            }

        }

        for(String str: rfile){ //for register.
            if(currFIFOInfoMap.get(str)==null){
                if(preFIFOInfoMap.get(str)==null) continue;
//                System.out.println(cycle+" "+str+" "+preFIFOInfoMap.get(str).getBit());
                currFIFOInfoMap.put(str,preFIFOInfoMap.get(str));
            }

        }

        for(String str: FIFOList){ // if there is empty FIFO record, get it from the previous one.
            if(currFIFOInfoMap.get(str)==null){
                if(preFIFOInfoMap.get(str)==null) return; // to ignore empty FIFO in the early cycle.
                currFIFOInfoMap.put(str,preFIFOInfoMap.get(str));
            }
        }
        Timeline.set(cycle,currFIFOInfoMap);
        String fstFifoInfo = FIFOList.get(0);
        String sndFifoInfo = FIFOList.get(1);
        String lastFifoInfo = FIFOList.get(FIFOList.size()-1);

        FIFO currFstFifo = (FIFO) currFIFOInfoMap.get(fstFifoInfo);
        FIFO currSndFifo = (FIFO) currFIFOInfoMap.get(sndFifoInfo);
        FIFO preFstFifo = (FIFO) preFIFOInfoMap.get(fstFifoInfo);
        FIFO preSndFifo = (FIFO) preFIFOInfoMap.get(sndFifoInfo);
        FIFO currlastFifo = (FIFO) currFIFOInfoMap.get(lastFifoInfo);

        if(!currFstFifo.full || currFstFifo.empty) controlHazardRec.put(cycle,++controlCnt);
        else if( preFstFifo.full&&!currSndFifo.full) dataHazardRec.put(cycle,++dataCnt);
        if(currlastFifo.full||!currlastFifo.empty) instRec.put(cycle,++instCnt);
    }

    public static int  stallStatus(int cycle){
        int num=cycle;
        int result = -1;
        int control= controlHazardRec.getOrDefault(cycle,0);
        int data= dataHazardRec.getOrDefault(cycle,0);
        int inst;

        if(control != 0){
            msg+="Control Hazard occurred at cycle "+cycle+"\n";
            result = 0;// control hazard
        }
        else if(data != 0){
            msg+="Data Hazard occurred at cycle "+cycle+"\n";
            result = 1;// data hazard
        }

        while((control= controlHazardRec.getOrDefault(num--,0))== 0&&num>0); // 현재 cycle보다 작은 cycle에서 control, data hazard 수와 instruction 수를 찾는다.
            num = cycle;
        while((data= dataHazardRec.getOrDefault(num--,0))== 0&&num >0);
            num = cycle;
        while((inst= instRec.getOrDefault(num--,0))== 0&&num > 0);

        msg += "Total control Hazards: "+ control+"\n";
        msg += "Total data Hazards: "+ data+"\n";
        msg += "Total Instructions: "+ inst+"\n";
        return result;
    }

    public static String convertBin(String bin, boolean binToHex){
        int idx =-1;// no leading zero;
        for(int i=0; i<bin.length();i++){
            if(bin.charAt(i)=='0') idx = i;
            else break;
        }
        if(idx == bin.length()-1) bin = "0";
        else bin = bin.substring(idx+1);


        if(binToHex)  return new BigInteger(bin, 2).toString(16);
        else return bin;
    }


    // GUI class and information.
    public static class TopPanel extends JPanel {
        private JLabel title = new JLabel("BlueVisualizer");
        private JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        private JButton binHexButton;
        public TopPanel(){
            binHexButton = (binToHex)? new JButton("Hexadecimal"):new JButton("Binary");
            title.setFont(titleFont);
            title.setOpaque(true);
            title.setForeground(new Color(0,176,240));// light blue
            buttonPanel.setPreferredSize(new Dimension(160,30));
            binHexButton.setPreferredSize(new Dimension(130,30));
            binHexButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    binToHex =!binToHex;
                    if(binToHex)binHexButton.setText("Hexadecimal");
                    else binHexButton.setText("Binary");
                    redraw();
                }
            });

            setLayout(new BorderLayout());
            setPreferredSize(topPanelSize);

            buttonPanel.add(binHexButton);
            add(title,BorderLayout.WEST);
            add(buttonPanel,BorderLayout.EAST);
        }
    }

    public static class LeftSubPanel extends Panel {

        private FIFOPanelSet fifoPanelSet;
        private Panel control = new Panel();
        private Panel buttons = new Panel(new FlowLayout());
        private Panel cycleInfo = new Panel(new FlowLayout());
        private JLabel cycletxt = new JLabel("Cycle: ");
        private JTextField cycleNum = new JTextField(String.valueOf(cycle),10);
        private JTextArea description= new JTextArea(msg,5,50);
        private JButton fir = new JButton("first cycle");
        private JButton prs = new JButton("previous hazard");
        private JButton pre = new JButton("previous cycle");
        private JButton nxt = new JButton("next cycle");
        private JButton nxs = new JButton("next stall");
        private JButton lst = new JButton("last cycle");

        public LeftSubPanel(int status) {

            // declare
            fifoPanelSet = new FIFOPanelSet(FIFOList.size(),status, FIFOList, Timeline.get(cycle),binToHex);

            //settings
            setLayout(new FlowLayout());
            setPreferredSize(leftPanelSize);
            control.setLayout(new BorderLayout());
            control.setPreferredSize(new Dimension(leftPanelSize.width,50));
            description.setPreferredSize(new Dimension(leftPanelSize.width,200));
            description.setFont(new Font("",Font.PLAIN,15));

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

            buttons.add(fir);
            buttons.add(prs);
            buttons.add(pre);
            buttons.add(nxt);
            buttons.add(nxs);
            buttons.add(lst);
            cycleInfo.add(cycletxt,BorderLayout.EAST);
            cycleInfo.add(cycleNum,BorderLayout.EAST);

            control.add(buttons,BorderLayout.WEST);
            control.add(cycleInfo,BorderLayout.EAST);
            add(fifoPanelSet);
            add(control);
            add(description);
        }

        public void setFifoPanelSet(FIFOPanelSet fifoPanelSet) {
            this.fifoPanelSet = fifoPanelSet;
        }

    }

    public static class RightSubPanel extends JPanel {
        public JPanel bottomPanel = new JPanel();
        public JPanel topPanel = new JPanel();
        public UserRegPanelSet userRegPanelSet;
        public RegPanelSet regPanelBottom1;
        public RegPanelSet regPanelBottom2;
        public RightSubPanel(){

            HashMap<String, Instance> reginfo = Timeline.get(cycle);

            regPanelBottom1 = new RegPanelSet(reginfo, rfile,0,binToHex);
            regPanelBottom2 = new RegPanelSet(reginfo, rfile,16,binToHex);
            userRegPanelSet = new UserRegPanelSet(reginfo,regList,binToHex);

            setLayout(new BorderLayout());
            bottomPanel.setPreferredSize(rightBottomPanelSize);
            topPanel.setPreferredSize(rightUpperPanelSize);
            topPanel.setLayout(new FlowLayout());
            topPanel.setBorder(BorderFactory.createTitledBorder(new LineBorder(Color.black),"Register", TitledBorder.CENTER,TitledBorder.ABOVE_TOP,title2));
            bottomPanel.setBorder(BorderFactory.createTitledBorder(new LineBorder(Color.black),"User-Chosen Register", TitledBorder.CENTER,TitledBorder.ABOVE_TOP,title2));
            setPreferredSize(rightPanelSize);

            bottomPanel.add(userRegPanelSet);
            topPanel.add(regPanelBottom1);
            topPanel.add(regPanelBottom2);
            add(topPanel,BorderLayout.NORTH);
            add(bottomPanel,BorderLayout.SOUTH);

        }

    }

    public static void redraw(){
        msg ="";
        int status =stallStatus(cycle);
        frame.getContentPane().removeAll();
        frame.add(new TopPanel());
        frame.add(new LeftSubPanel(status));
        frame.add(new RightSubPanel());
        frame.revalidate();
    }

}


