/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package components;

import javax.swing.event.DocumentEvent;
import javax.swing.event.UndoableEditEvent;
import javax.swing.text.AbstractDocument;
import javax.swing.undo.UndoManager;

/**
 *
 * @author tianmei
 */
public class MyUndoManager extends UndoManager {

    @Override
    public void undoableEditHappened(UndoableEditEvent e) {
        //  Check for an attribute change
        AbstractDocument.DefaultDocumentEvent event
                = (AbstractDocument.DefaultDocumentEvent) e.getEdit();

        if (event.getType().equals(DocumentEvent.EventType.CHANGE)) {
            return;
        } else {
            super.undoableEditHappened(e);
        }
    }
}
