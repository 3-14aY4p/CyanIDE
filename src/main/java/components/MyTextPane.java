/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package components;

import java.awt.Color;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JTextPane;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;

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
    final static String SPCCH = "+|-|*|/|(|)|<|<<|>|>>|<>|><|<-";
    final static String SPCVL = "true|false|null";
    final static String DTYPE = "int|dec|str|boo";
    final static String CONDS = "if|elif|else|endif";
    final static String FUNCS = "echo";
    final static String LOOPS = "while|endwhile";

    public MyTextPane(JButton btn_undo, JButton btn_redo,
            JMenuItem mni_undo, JMenuItem mni_redo) {

        this.UNDO_BTN = btn_undo;
        this.REDO_BTN = btn_redo;
        this.UNDO_MNI = mni_undo;
        this.REDO_MNI = mni_redo;

        MyStyledDocument doc = new MyStyledDocument();
        
        applySyntaxColors(doc);

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

    public void applySyntaxColors(MyStyledDocument doc) {
        Style col = doc.getStyle(StyleContext.DEFAULT_STYLE);

        Color prp = new Color(147, 112, 219);
        Color pnk = new Color(241, 91, 181);
        Color sea = new Color(65, 125, 193);
        Color tel = new Color(0, 130, 127);
        Color orn = new Color(254, 90, 29);

        // set syntax to be highlighted
        for (String s : SPCCH.split("\\|")) {
            StyleConstants.setForeground(doc.addStyle(s, col), prp);
        }
        for (String s : SPCVL.split("\\|")) {
            StyleConstants.setForeground(doc.addStyle(s, col), tel);
        }
        for (String s : DTYPE.split("\\|")) {
            StyleConstants.setForeground(doc.addStyle(s, col), pnk);
        }
        for (String s : CONDS.split("\\|")) {
            StyleConstants.setForeground(doc.addStyle(s, col), orn);
        }
        for (String s : FUNCS.split("\\|")) {
            StyleConstants.setForeground(doc.addStyle(s, col), sea);
        }
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
        applySyntaxColors((MyStyledDocument) getStyledDocument());
        
        undoManager.undoableEditHappened(uee);
    }
}
