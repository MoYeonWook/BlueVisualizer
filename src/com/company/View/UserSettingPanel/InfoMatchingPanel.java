package com.company.View.UserSettingPanel;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.util.*;
import java.util.List;

import static java.lang.String.valueOf;

public class InfoMatchingPanel extends JPanel {// step1.fifo matching.
    int[] numFifo = {2,3,4};
    String[] choice;
    JPanel upperPannel = new JPanel();
    JPanel middlePanel = new JPanel();
    List<String> posName = new ArrayList<>();
    JRadioButton[] inputNum;
    ButtonGroup group;

    public List<String> getPosName() {
        return posName;
    }

    public InfoMatchingPanel(String name, HashSet<String> set){
        choice =  set.toArray(new String[set.size()]);
        Arrays.sort(choice);
        //declare
        JLabel content = new JLabel(name);
        JRadioButton[] numInput2 = new JRadioButton[numFifo.length];


        // option
        //numInput.setSize(20,10);
        upperPannel.setLayout(new FlowLayout(FlowLayout.LEFT,10,20));
        upperPannel.setSize(200,150);
        middlePanel.setLayout(new GridLayout(4,1));
        setLayout(new FlowLayout());
        setBorder(BorderFactory.createTitledBorder(new LineBorder(Color.black),name, TitledBorder.CENTER,TitledBorder.ABOVE_TOP));
        setPreferredSize(new Dimension(200,300));
        setVisible(true);

        //add
        createButtonPanel();
        add(upperPannel);
        add(middlePanel);

        createChoiceList(numFifo[0],choice);
    }
    private void createButtonPanel(){
        inputNum = new JRadioButton[numFifo.length];
        group = new ButtonGroup();

        for(int i =0; i<numFifo.length;i++){
            inputNum[i] = new JRadioButton(valueOf(numFifo[i]));
            if(i==0) inputNum[i].setSelected(true);
            group.add(inputNum[i]);
            upperPannel.add(inputNum[i]);

            inputNum[i].addItemListener(e -> {
                if(e.getStateChange()==ItemEvent.SELECTED){
                    AbstractButton button = (AbstractButton) e.getItemSelectable();
                    int num = Integer.parseInt(button.getActionCommand());
                    createChoiceList(num,choice);
                }
            });

        }
    }

    public void createChoiceList(int num, String[] choice){
        middlePanel.removeAll();
        posName = new ArrayList<>();

        for(int i=0; i<num; i++) {
            int current = i;
            Panel entry = new Panel();
            String labelName = valueOf(i + 1) + ' ';
            JComboBox<String> choiceList = new JComboBox<>(choice);
            entry.setPreferredSize(new Dimension(180, 50));

            entry.add(new JLabel(labelName));
            entry.add(choiceList);
            posName.add(choice[0]);
            middlePanel.add(entry);

            choiceList.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    posName.set(current, (String) choiceList.getSelectedItem());
                }
            });
        }
        revalidate();
    }
}
