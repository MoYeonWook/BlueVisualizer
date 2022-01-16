package com.company.GUI;

import com.company.BitType;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class TopSubPanel extends JPanel {
    private JLabel title = new JLabel("BlueVisualizer");
    private JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    private ValueSearchPanel valueSearchPanel;
    private JButton binHexButton;
    private JButton asmModeButton;
    private Font titleFont = new Font("Franklin Gothic Heavy",Font.PLAIN,50);

    public TopSubPanel(Boolean asmMode, BitType bitType, List<String> FIFOList, ValueSearchPanel valueSearchPanel){
        binHexButton =  new JButton();
        asmModeButton = new JButton();
        this.valueSearchPanel = valueSearchPanel;

        title.setFont(titleFont);
        title.setOpaque(true);
        title.setForeground(new Color(0,176,240));// light blue
        buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        setButtonText(asmMode,bitType);
        buttonPanel.setPreferredSize(new Dimension(700,60));
        binHexButton.setPreferredSize(new Dimension(150,30));
        asmModeButton.setPreferredSize(new Dimension(150,30));
        setLayout(new BorderLayout());

        buttonPanel.add(asmModeButton);
        buttonPanel.add(binHexButton);
        buttonPanel.add(valueSearchPanel);
        add(title,BorderLayout.WEST);
        add(buttonPanel,BorderLayout.EAST);
    }

    public void setButtonText(Boolean asmMode, BitType bitType){
        if(bitType == BitType.BIN) binHexButton.setText("Binary");
        else if(bitType == BitType.HEX) binHexButton.setText("Hexadecimal");
        else binHexButton.setText("Decimal");

        if(asmMode) asmModeButton.setText("Remove assembly");
        else asmModeButton.setText("Show assembly");
    }

    public JButton getBinHexButton() {
        return binHexButton;
    }

    public JButton getAsmModeButton() {
        return asmModeButton;
    }

    public ValueSearchPanel getValueSearchPanel() {
        return valueSearchPanel;
    }

    public void setValueSearchPanel(ValueSearchPanel valueSearchPanel) {
        this.valueSearchPanel = valueSearchPanel;
    }

    public void setBinHexButton(JButton binHexButton) {
        this.binHexButton = binHexButton;
    }

    public void setAsmModeButton(JButton asmModeButton) {
        this.asmModeButton = asmModeButton;
    }
}
