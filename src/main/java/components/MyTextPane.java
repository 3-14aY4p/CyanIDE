/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package components;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
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

    private UndoManager undoManager = new UndoManager();

    private final JButton UNDO_BTN;
    private final JButton REDO_BTN;
    private final JMenuItem UNDO_MNI;
    private final JMenuItem REDO_MNI;

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

    public MyTextPane(JButton btn_undo, JButton btn_redo,
            JMenuItem mni_undo, JMenuItem mni_redo) {

        this.UNDO_BTN = btn_undo;
        this.REDO_BTN = btn_redo;
        this.UNDO_MNI = mni_undo;
        this.REDO_MNI = mni_redo;

        setStyledDocument(new MyStyledDocument());
        getDocument().addUndoableEditListener(this);
    }

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

    public void setUndoManagerFocusState(boolean isActive) {
        if (isActive) {
            UNDO_BTN.addActionListener(undoActionListener);
            REDO_BTN.addActionListener(redoActionListener);
            UNDO_MNI.addActionListener(undoActionListener);
            REDO_MNI.addActionListener(redoActionListener);
        } else {
            UNDO_BTN.removeActionListener(undoActionListener);
            REDO_BTN.removeActionListener(redoActionListener);
            UNDO_MNI.removeActionListener(undoActionListener);
            REDO_MNI.removeActionListener(redoActionListener);
        }
    }

    @Override
    public void undoableEditHappened(UndoableEditEvent uee) {
        if (!uee.getEdit().getPresentationName().equals("style change")) {
            undoManager.addEdit(uee.getEdit());
        }
    }
}
