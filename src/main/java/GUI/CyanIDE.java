/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package GUI;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;
import java.io.File;
import java.io.FileReader;
import java.util.Properties;
import javax.swing.JOptionPane;
import javax.swing.LookAndFeel;

/**
 *
 * @author tianmei
 */
public class CyanIDE {

    public static void main(String[] args) {
        // Set FlatLaf look and feel
        File configFile = new File("src/main/resources/config.properties");
        String selectedLafTheme;

        LookAndFeel selectedLAF = new FlatLightLaf(); // defaults to light mode

        try {
            try (FileReader fileReader = new FileReader(configFile)) {
                Properties properties = new Properties();
                properties.load(fileReader);

                selectedLafTheme = properties.getProperty("LookAndFeel");

                if (selectedLafTheme.equals("FlatDarkLaf")) {
                    selectedLAF = new FlatDarkLaf();
                } else {
                    selectedLAF = new FlatLightLaf();
                }
            }
        } catch (Exception ex) {
        }

        try {
            javax.swing.UIManager.setLookAndFeel(selectedLAF);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            JOptionPane.showMessageDialog(null, "Failed to initialize theme.");
        }

        new GUI_main().setVisible(true);
    }
}
