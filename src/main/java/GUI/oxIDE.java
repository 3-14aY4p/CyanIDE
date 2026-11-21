package GUI;

import com.formdev.flatlaf.*;
import com.formdev.flatlaf.themes.*;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

/**
 *
 * @author tianmei
 */
public class oxIDE {

    public static void main(String[] args) {
        // Set FlatLaf look and feel
        try {
            javax.swing.UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            System.err.println("Failed to initialize LaF");
        }
        
        new GUI_main().setVisible(true);
    }
}
