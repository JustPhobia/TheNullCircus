package com.thenullcircus.view;

import javax.swing.*;

public class BasePanel extends JPanel {

    protected MainWindow mainWindow;
    public BasePanel(MainWindow mainWindow){
        this.mainWindow = mainWindow;
    }

    protected void navigateTo(String panelName){
        mainWindow.navigateTo(panelName);
    }

    public void onVisible(){
    }

}
