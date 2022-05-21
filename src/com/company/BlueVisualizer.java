package com.company;


import com.company.View.*;
import com.company.Model.BVModel;

import java.io.IOException;


public class BlueVisualizer {

    public static void main(String[]args) throws IOException {
        BVView bvView = new BVView("BlueVisualizer");
        BVModel bvModel = new BVModel();
        BVControl bvControl = new BVControl(bvModel,bvView);
        System.out.println("Choose FIFO or register to visualize in order");

        bvControl.initialize();
    }

}


