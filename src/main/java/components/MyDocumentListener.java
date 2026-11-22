/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package components;

import javax.swing.*;
import javax.swing.event.*;

/**
 *
 * @author tianmei
 */
public class MyDocumentListener implements DocumentListener {
    private final JTabbedPane tabbedPane;
    private final int tabIndex;

    private final ImageIcon icon = new ImageIcon("src/main/resources/icons/asterisk.png");

    public MyDocumentListener(JTabbedPane tabbedPane, int tabIndex) {
        this.tabbedPane = tabbedPane;
        this.tabIndex = tabIndex;
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        updateIcon();
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        updateIcon();
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        updateIcon();
    }

    // update icon/indicator whenever there are changes in the document
    public void updateIcon() {
        tabbedPane.setIconAt(tabIndex, icon);
    }

    // might add more functions to this in the future, it depends
}
