// Code from https://ateraimemo.com/Swing/SimpleSyntaxHighlight.html
// edited it to include comments

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package components;

import java.awt.Color;
import java.util.Objects;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Element;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

/**
 *
 * @author tianmei
 */
public class MyStyledDocument extends DefaultStyledDocument {

    private static final String DELIMITERS = ".,:;\\(\\)";

    @Override
    public void insertString(int offset, String text, AttributeSet a) throws BadLocationException {
        super.insertString(offset, text, a);
        processChangedLines(offset, text.length());
    }

    @Override
    public void remove(int offset, int length) throws BadLocationException {
        super.remove(offset, length);
        processChangedLines(offset, 0);
    }

    private void processChangedLines(int offset, int length) throws BadLocationException {
        Element root = getDefaultRootElement();
        int startLine = root.getElementIndex(offset);
        int endLine = root.getElementIndex(offset + length);
        for (int i = startLine; i <= endLine; i++) {
            applyHighlighting(root.getElement(i));
        }
    }

    private void applyHighlighting(Element line) throws BadLocationException {
        int startOffset = line.getStartOffset();
        // Exclude the trailing newline; the last line element also ends with
        // an implied newline at getLength() + 1, so this never exceeds the length
        int endOffset = line.getEndOffset() - 1;
        
        final StyleContext cont = StyleContext.getDefaultStyleContext();
        final AttributeSet comments = cont.addAttribute(cont.getEmptySet(), StyleConstants.Foreground, new Color(60,179,113));
        
        // Fetch only the changed line instead of the whole document text
        String text = getText(startOffset, endOffset - startOffset);
        setCharacterAttributes(startOffset, text.length(), getStyle(StyleContext.DEFAULT_STYLE), true);
        highlightKeywords(text, startOffset);
        
        // for comments
        if (text.strip().startsWith("#")) {
            setCharacterAttributes(startOffset, text.length(), comments, true);
        }
    }

    private void highlightKeywords(String text, int lineOffset) {
        int length = text.length();
        int index = 0;
        while (index < length) {
            int tokenStart = index;
            while (index < length && !isDelimiter(text.charAt(index))) {
                index++;
            }
            if (index > tokenStart) {
                Style style = getStyle(text.substring(tokenStart, index));
                if (Objects.nonNull(style)) {
                    setCharacterAttributes(lineOffset + tokenStart, index - tokenStart, style, false);
                }
            }
            index++; // skip the delimiter
        }
    }

    protected boolean isDelimiter(char ch) {
        return Character.isWhitespace(ch) || DELIMITERS.indexOf(ch) >= 0;
    }
}
