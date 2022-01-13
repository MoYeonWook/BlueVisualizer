package com.company;


import com.company.GUI.*;

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
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
    static public Decoder decoder = new Decoder();
    static private String timeUnit;


    static List<String> FIFOList; // user's choice will be saved here
    static List<String> regList; //user's choice will be saved here
    static List<String> rfile = new ArrayList<>();
    static public String msg="";
    static public int cycle = 0;
    ;
    static JFrame frame;
    static LeftSubPanel leftSubPanel;
    static RightSubPanel rightSubPanel;

    //statistics
    static protected HashMap<Integer,Integer> dataHazardRec = new HashMap<>();
    static protected HashMap<Integer,Integer> controlHazardRec = new HashMap<>();
    static protected HashMap<Integer,Integer> instRec = new HashMap<>();
    static protected HashMap<Integer,String[]> assmRec = new HashMap<>();
    static int dataCnt = 0;
    static int controlCnt =0;
    static int instCnt =0;
    static String[] AsmInFIFO;

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
        BVModel bvModel = new BVModel();
        BVView bvView = new BVView("BlueVisualizer");
        BVControl bvControl = new BVControl(bvModel,bvView);

        System.out.println("Choose FIFO or register to visualize in order");

        bvControl.initialize();
    }

}


