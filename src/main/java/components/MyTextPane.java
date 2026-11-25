/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package components;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;
import javax.swing.undo.*;

/*
 * All my undo and redo events happen here, as
 * I have already removed the action listeners
 * of my buttons from GUI_main.
 */
/**
 *
 * @author tianmei
 */
public class MyTextPane extends JTextPane implements UndoableEditListener {

    private UndoManager undoManager = new MyUndoManager();

    // undo/redo button references
    private final JMenuItem UNDO_MNI;
    private final JMenuItem REDO_MNI;
    private final JButton UNDO_BTN;
    private final JButton REDO_BTN;

    // syntax reference/regex
    final static String SPCCH = "\\+|-|\\*|/|=|>|<|==|>=|<=|!=|<-";
    final static String SPCVL = "null|true|false";
    final static String DTYPE = "int|dec|str|bool";
    final static String CONDS = "if|elif|else";
    final static String FUNCS = "echo";

    public MyTextPane(JButton btn_undo, JButton btn_redo,
            JMenuItem mni_undo, JMenuItem mni_redo) {

        this.UNDO_BTN = btn_undo;
        this.REDO_BTN = btn_redo;
        this.UNDO_MNI = mni_undo;
        this.REDO_MNI = mni_redo;

        MyStyledDocument doc = new MyStyledDocument();
        Style col = doc.getStyle(StyleContext.DEFAULT_STYLE);

        Color prp = new Color(147, 112, 219);
        Color pnk = new Color(241, 91, 181);
        Color sea = new Color(69, 123, 157);
        Color ocn = new Color(29, 53, 87);

        // set syntax to be highlighted
        for (String s : SPCCH.split("\\|")) {
            StyleConstants.setForeground(doc.addStyle(s, col), prp);
        }
        for (String s : SPCVL.split("\\|")) {
            StyleConstants.setForeground(doc.addStyle(s, col), ocn);
        }
        for (String s : DTYPE.split("\\|")) {
            StyleConstants.setForeground(doc.addStyle(s, col), pnk);
        }
        for (String s : CONDS.split("\\|")) {
            StyleConstants.setForeground(doc.addStyle(s, col), sea);
        }
        for (String s : FUNCS.split("\\|")) {
            StyleConstants.setForeground(doc.addStyle(s, col), sea);
        }

        doc.addUndoableEditListener(this);
        setStyledDocument(doc);
    }

    private final ActionListener undoActionListener = (ActionEvent ae) -> {
        try {
            undoManager.undo();
        } catch (CannotUndoException e) {
            Toolkit.getDefaultToolkit().beep();
        }
    };
    private final ActionListener redoActionListener = (ActionEvent ae) -> {
        try {
            undoManager.redo();
        } catch (CannotRedoException e) {
            Toolkit.getDefaultToolkit().beep();
        }
    };

    public void undo() {
        try {
            undoManager.undo();
        } catch (CannotUndoException cue) {
            Toolkit.getDefaultToolkit().beep();
        }
    }

    public void redo() {
        try {
            undoManager.redo();
        } catch (CannotRedoException cue) {
            Toolkit.getDefaultToolkit().beep();
        }
    }

    public void addListeners() {
        UNDO_MNI.addActionListener(undoActionListener);
        REDO_MNI.addActionListener(redoActionListener);
        UNDO_BTN.addActionListener(undoActionListener);
        REDO_BTN.addActionListener(redoActionListener);
    }

    private void remListeners() {
        UNDO_MNI.removeActionListener(undoActionListener);
        REDO_MNI.removeActionListener(redoActionListener);
        UNDO_BTN.removeActionListener(undoActionListener);
        REDO_BTN.removeActionListener(redoActionListener);
    }

    public void setUndoManagerFocusState(boolean isActive) {
        if (isActive) {
            addListeners();
        } else {
            remListeners();
        }
    }

    @Override
    public void undoableEditHappened(UndoableEditEvent uee) {
        undoManager.undoableEditHappened(uee);
    }
}
