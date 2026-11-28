/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package compiler;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Stack;

/**
 *
 * @author tianmei
 */

/*
 * COMMENT
 * ----------------------------------
 * # this is a comment
 * 
 * VARIABLE ASSIGNMENT
 * ----------------------------------
 * data types: int|dec|str|boo
 * 
 * var ident <- value;
 * 
 * FUNCTIONS
 * ----------------------------------
 * print: echo "hello world";
 * 
 * EXPRESSIONS
 * ----------------------------------
 * arithmetic: +, -, *, /, ()
 * 
 *                   >=  <=  !=  ==
 * relational: >, <, >>, <<, <>, ><
 */
public class Parser {

    // ERROR CODES
    /*  INVALID SYNTAX
     *  
     *
     *
     */
    
    // marking comments
    final static String COMNTPRFX = "#";
    
    
    // atomic values
    final static String INTGR = "-?[0-9]+";
    final static String DECML = "-?[0-9]+\\.[0-9]+";
    final static String STRNG = "\"[^\"\\r\\n]*\"";
    final static String BOOLN = "true|false";
    
    // variable
    final static String IDENT = "";
    
    // operator types
    final static String AOPRT = "\\+|-|\\*|/|\\(|\\)";
    final static String ROPRT = "<|>|<<|>>|<>|><";
    final static String LOPRT = "&&|\\|\\||!\b";          // leaving this one out for now
    
    // for expressions
    final static String COMP = "(" + IDENT + "|" + INTGR + "|" + DECML + ")";
    final static String OPRT = "(" + AOPRT + "|" + ROPRT + ")";
    final static String EXPR 
            = "(" + COMP + "(" + "\\s*" + OPRT + "\\s*" + COMP + ")+" + ")";
    
    final static String VALUE = "(?:" 
            + IDENT + "|"
            + INTGR + "|" 
            + DECML + "|" 
            + STRNG + "|" 
            + BOOLN + "|" 
            + EXPR + ")";
    
    // keywords
    final static String DTYPE = "int|dec|str|boo";
    
    // grammar rules
    final static String VAR_GRAMMAR = "\\s*" + DTYPE + "\\s*"
            + "(" + IDENT + ")\\s*<-\\s*"
            + "(" + VALUE + ")\\s*;\\s*";

    // REMOVE LATER: for testing
    public static void main(String[] args) {
        String sampleCode = """
            # this is a comment
                            
            int num = 1;
            dec dcm = 2.0;
            str yes = "no";
            boo fct = true;
                            
            echo();
                            
            if cond:
                code 1;                
            elif cond:
                code 2;            
            elif cond:
                code 3;            
            else:
                code 4;            
            endif
            """;

        buildPerBlock(sampleCode);
    }

    public static HashMap<String, Stack> compileCode(String sourceCode) {
        // return value
        HashMap<String, Stack> finalMap = new HashMap<>();

        Stack<String> blocks = buildPerBlock(sourceCode);

        Stack<HashMap<String, Object>> vars = new Stack<>();
        Stack<HashMap<String, Object>> errs = new Stack<>();
        Stack<HashMap<String, Object>> prnt = new Stack<>();

        for (String blk : blocks) {
            String tokf = blk.split("\\h")[0];
            
            if (tokf.equals("echo")) {
            
            } else if (tokf.matches(DTYPE)) {
            
            } else if (tokf.equals("if")) {
            
            } else {
                // error here
            }
        }

        return finalMap;
    }

    public static Stack<String> buildPerBlock(String sourceCode) {
        // return value
        Stack<String> codeBlocks = new Stack<>();

        // split source code per newline character
        Stack<String> lines = new Stack<>();
        for (String l : Arrays.asList(sourceCode.split("\\n"))) {
            if (!(l.isBlank() || l.strip().startsWith(COMNTPRFX))) {
                lines.add(l);
            }
        }

        // split source code per horizontal whitespace character
        Stack<String> tokens = new Stack<>();
        tokens.addAll(Arrays.asList(sourceCode.split("\\h")));

        // block to be parsed; value will be overwritten later
        StringBuilder block = new StringBuilder();

        boolean isInIf = false; // checks if if has ended
        int pc = 0;             // program counter
        while (pc < lines.size()) {
            String line = lines.elementAt(pc).strip();

            // first token of the line
            String tokf = line.split("\\h")[0];

            // check if line is inside if
            if (tokf.equals("if")) {
                isInIf = true;
                block = new StringBuilder();
                block.append(line + " ");
                pc++;
            } else if (tokf.equals("endif")) {
                block.append(line);
                codeBlocks.add(block.toString());
                // create new block after adding
                block = new StringBuilder();
                // reset to false
                isInIf = false;
                pc++;
            } else if (line.charAt(line.length() - 1) == ';') {
                block.append(line + " ");
                // only create new block if it's not in if
                if (!isInIf) {
                    codeBlocks.add(block.toString());
                    block = new StringBuilder();
                }
                pc++;
            } else {
                block.append(line + " ");
                pc++;
            }
        }
        for (String s : codeBlocks) {
            System.out.println(s);
        }

        return codeBlocks;
    }

    public static void parsePrintStatement(String codeBlock) {
    }

    public static void parseVariableAssignment(String codeBlock) {
    }

    public static void parseIfStatement(String codeBlock) {
    }
}
