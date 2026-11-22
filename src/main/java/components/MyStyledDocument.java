// Code based on https://github.com/stark9000/java-Syntax-Highlight

/*
 * what the fuck is this mess i've made?
 * idk but i tried! still can't detect
 * the strings and comments though welp!
 */

 /*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package components;

import java.awt.Color;
import java.io.*;
import javax.swing.text.*;

/**
 *
 * @author tianmei
 */
public class MyStyledDocument extends DefaultStyledDocument {

    File configFile = new File("src/main/resources/config.properties");
    String selectedLafTheme = "";

    final StyleContext cont = StyleContext.getDefaultStyleContext();

    // idk how to do this better tbh... but hey it kinda works somewhat for now..?
    final AttributeSet attrBoldText = cont.addAttribute(cont.getEmptySet(), StyleConstants.Bold, true);
    final AttributeSet attrItlcText = cont.addAttribute(cont.getEmptySet(), StyleConstants.Italic, true);

    final AttributeSet attrDtp = cont.addAttribute(attrBoldText, StyleConstants.Foreground, new Color(153, 68, 255));
    final AttributeSet attrFun = cont.addAttribute(attrBoldText, StyleConstants.Foreground, new Color(34, 119, 204));
    final AttributeSet attrCon = cont.addAttribute(attrBoldText, StyleConstants.Foreground, new Color(230, 48, 138));
    final AttributeSet attrVal = cont.addAttribute(attrItlcText, StyleConstants.Foreground, new Color(0, 191, 125));
    final AttributeSet attrSym = cont.addAttribute(attrBoldText, StyleConstants.Foreground, new Color(221, 51, 51));

    AttributeSet attrDef = cont.addAttribute(cont.getEmptySet(), StyleConstants.Foreground, new Color(0, 180, 197));

    // BASIS FOR THE REGEX
    String decimal = "[0-9]+\\.[0-9]+";
    String integer = "[0-9]+";
    String string = "\"[^\"\\r\\n]*\"";
    String bool = "true|false";

    String values = "(?:"
            + decimal + "|"
            + integer + "|"
            + string + "|"
            + bool + ")";

    String datatypes = "int|dec|str|bool";
    String functions = "echo";
    String conditions = "if|elif|else";

    private int findLastNonWordChar(String text, int index) {
        while (--index >= 0) {
            if (String.valueOf(text.charAt(index)).matches("\\W")) {
                break;
            }
        }
        return index;
    }

    private int findFirstNonWordChar(String text, int index) {
        while (index < text.length()) {
            if (String.valueOf(text.charAt(index)).matches("\\W")) {
                break;
            }
            index++;
        }
        return index;
    }

    @Override
    public void insertString(int offset, String str, AttributeSet a) throws BadLocationException {
        super.insertString(offset, str, a);

        String text = getText(0, getLength());
        int before = findLastNonWordChar(text, offset);
        if (before < 0) {
            before = 0;
        }
        int after = findFirstNonWordChar(text, offset + str.length());
        int wordL = before;
        int wordR = before;

        while (wordR <= after) {
            if (wordR == after || String.valueOf(text.charAt(wordR)).matches("\\W")) {
                if (text.substring(wordL, wordR).matches("(\\W)*(" + datatypes + ")")) {
                    setCharacterAttributes(wordL, wordR - wordL, attrDtp, false);
                } else if (text.substring(wordL, wordR).matches("(\\W)*(" + functions + ")")) {
                    setCharacterAttributes(wordL, wordR - wordL, attrFun, false);
                } else if (text.substring(wordL, wordR).matches("(\\W)*(" + conditions + ")")) {
                    setCharacterAttributes(wordL, wordR - wordL, attrCon, false);
                } else if (text.substring(wordL, wordR).matches("(\\W)*(" + values + ")")) {
                    setCharacterAttributes(wordL, wordR - wordL, attrVal, false);
                } else {
                    if (text.charAt(wordL) == '>'
                            || text.charAt(wordL) == '<'
                            || text.charAt(wordL) == '='
                            || text.charAt(wordL) == '+'
                            || text.charAt(wordL) == '-'
                            || text.charAt(wordL) == '*'
                            || text.charAt(wordL) == '/') {
                        setCharacterAttributes(wordL, wordR - wordL, attrSym, false);
                    }
                }
                wordL = wordR;
            }
            wordR++;
        }
    }

    @Override
    public void remove(int offs, int len) throws BadLocationException {
        super.remove(offs, len);
        String text = getText(0, getLength());
        int before = findLastNonWordChar(text, offs);
        if (before < 0) {
            before = 0;
        }
        int after = findFirstNonWordChar(text, offs);
        try {
            if (text.substring(before, after - before).matches("(\\W)*(" + datatypes + ")")) {
                setCharacterAttributes(before, after - before, attrDtp, false);
            } else if (text.substring(before, after - before).matches("(\\W)*(" + functions + ")")) {
                setCharacterAttributes(before, after - before, attrFun, false);
            } else if (text.substring(before, after - before).matches("(\\W)*(" + conditions + ")")) {
                setCharacterAttributes(before, after - before, attrCon, false);
            } else if (text.substring(before, after - before).matches("(\\W)*(" + values + ")")) {
                setCharacterAttributes(before, after - before, attrVal, false);
            }
        } catch (Exception e) {
        }
    }
}
