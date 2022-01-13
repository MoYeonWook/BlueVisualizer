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

    public static void main(String[]args) throws IOException {
        BVModel bvModel = new BVModel();
        BVView bvView = new BVView("BlueVisualizer");
        BVControl bvControl = new BVControl(bvModel,bvView);

        System.out.println("Choose FIFO or register to visualize in order");

        bvControl.initialize();
    }

}


