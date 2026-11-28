/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package compiler;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
public class MyCompiler {

    // ERROR TYPES
    /*  INVALID SYNTAX
     *      illegal start of expression
     *      illegal '_' structure
     *      missing [\"] at the start/end of string
     *      
     *  INVALID ASSIGNMENT
     *      value does not match datatype
     *      variable 'VAR' already exists
     *
     *  MISSING VALUES
     *      variable 'VAR' not found
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
    final static String IDENT = "[A-Za-z_][A-Za-z0-9_]*";

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
    final static String DTYPE = "(int|dec|str|boo)";

    // grammar rules
    final static String GRAMMAR_VAR = "\\s*" + DTYPE + "\\s+"
            + "(" + IDENT + ")\\s*<-\\s*"
            + "(" + VALUE + ")\\s*;\\s*";

    // REMOVE LATER: for testing
    public static void main(String[] args) {
        String sampleCode = """
            # this is a comment
                            
            int num <- 1;
            dec dadcm <- 2dasda.0;
            str yes <- "no";
            boo fct <- true;
                            
            echo "hello";
                            
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

        compileCode(sampleCode);
    }

    public static HashMap<String, Stack> compileCode(String sourceCode) {
        // return value
        HashMap<String, Stack> finalMap = new HashMap<>();

        Stack<String> blocks = groupBlocks(sourceCode);

        Stack<HashMap<String, Object>> vars = new Stack<>();
        Stack<HashMap<String, Object>> errs = new Stack<>();
        Stack<HashMap<String, Object>> prnt = new Stack<>();

        for (String blk : blocks) {
            // temp storage
            HashMap<String, Object> hashMap = new HashMap<>();

            int lnum = blocks.indexOf(blk) + 1;
            String tokf = blk.split("\\h", 2)[0];

            if (!(blk.startsWith(COMNTPRFX) || blk.isBlank())) {
                if (tokf.equals("echo")) {
                    hashMap = parsePrintStatement(lnum, blk, vars);
                    if (hashMap.containsKey("ERROR")) {
                        hashMap.put("LNUM", lnum);

                        errs.add(hashMap);
                    } else {
                        hashMap.put("LNUM", lnum);
                        prnt.add(hashMap);
                    }
                } else if (tokf.matches(DTYPE)) {
                    hashMap = parseVariableAssignment(lnum, blk, vars);
                    if (hashMap.containsKey("ERROR")) {
                        hashMap.put("LNUM", lnum);

                        errs.add(hashMap);
                    } else {
                        hashMap.put("LNUM", lnum);
                        vars.add(hashMap);
                    }
                    // FIXME: if-statements and beyond won't get proper numbering
                } else if (tokf.equals("if")) {
                    hashMap = parseIfStatement(lnum, blk, vars);
                    if (hashMap.containsKey("ERROR")) {
                        hashMap.put("LNUM", lnum);

                        errs.add(hashMap);
                    } else {
                        // TODO: Return value of the if-statement
                        hashMap.put("LNUM", lnum);
                    }
                } else {
                    hashMap.put("ERROR", "INVALID SYNTAX: Illegal start of expression.");
                    hashMap.put("LNUM", lnum);

                    errs.add(hashMap);
                }
            }
        }

        for (HashMap v : vars) {
            System.out.println(v);
        }
        for (HashMap v : prnt) {
            System.out.println(v);
        }
        for (HashMap v : errs) {
            System.out.println(v);
        }

        finalMap.put("VARS", vars);
        finalMap.put("ERRS", errs);
        finalMap.put("PRNT", prnt);

        return finalMap;
    }

    public static Stack<String> groupBlocks(String sourceCode) {
        // return value
        Stack<String> codeBlocks = new Stack<>();

        // split source code per newline character
        Stack<String> lines = new Stack<>();
        lines.addAll(Arrays.asList(sourceCode.split("\\n")));

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
            String tokf = line.split("\\h", 2)[0];

            // check if line is inside if
            if (tokf.equals("#") || tokf.isBlank()) {
                codeBlocks.add("");
                pc++;
            } else if (tokf.equals("if")) {
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
            } else {
                block.append(line + " ");
                // only create new block if it's not in if
                if (!isInIf) {
                    codeBlocks.add(block.toString());
                    block = new StringBuilder();
                }
                pc++;
            }
        }

        return codeBlocks;
    }

    public static HashMap<String, Object> parsePrintStatement(int lnum, String codeBlock,
            Stack<HashMap<String, Object>> vars) {
        // return value
        HashMap<String, Object> valueMap = new HashMap<>();

        Pattern p = Pattern.compile("\\s*echo\\s+(.*)\\s*;");
        Matcher m = p.matcher(codeBlock);

        if (!m.find()) {
            valueMap.put("ERROR", "INVALID SYNTAX: Invalid 'print' structure.");
            return valueMap;
        }
        // extracted string
        String extr = m.group(1);
        String stmt = "";

        if (extr.contains("\"")) {
            if (extr.matches("\\\"(.*)\\\"")) {
                // remove "" and extract inside string
                stmt = extr.replaceAll("\\\"", "");
            } else {
                valueMap.put("ERROR", "INVALID SYNTAX: Missing [\"] at the start/end of string.");
                return valueMap;
            }
        } else if (extr.contains(INTGR + "|" + DECML + "|" + BOOLN)) {
            stmt = extr;    // print value as is
        } else if (extr.matches(IDENT)) {
            HashMap<String, String> varTable = new HashMap<>();
            HashMap<String, Integer> varIndex = new HashMap<>();

            for (HashMap var : vars) {
                String varIdn = (String) var.get("IDENT");
                String varVal = (String) var.get("VALUE");
                int varLnm = (int) var.get("LNUM");

                varTable.put(varIdn, varVal);
                varIndex.put(varIdn, varLnm);
            }

            if (varTable.containsKey(extr)) {
                if (varIndex.get(extr) < lnum) {
                    stmt = varTable.get(extr);
                }
            } else {
                valueMap.put("ERROR", "MISSING VALUES: Variable '" + extr + "' not found.");
                return valueMap;
            }
        } else if (extr.matches(EXPR)) {
            // TODO: evaluate expressions
        }

        valueMap.put("TEXT", stmt);
        return valueMap;
    }

    public static HashMap<String, Object> parseVariableAssignment(int lnum, String codeBlock,
            Stack<HashMap<String, Object>> vars) {
        // return value
        HashMap<String, Object> valueMap = new HashMap<>();

        Pattern p = Pattern.compile(GRAMMAR_VAR);
        Matcher m = p.matcher(codeBlock);

        if (!m.find()) {
            valueMap.put("ERROR", "INVALID SYNTAX: Invalid 'declaration' structure.");
            return valueMap;
        }
        String dtype = m.group(1);
        String ident = m.group(2);
        String value = m.group(3);

        boolean isTypeValid = false;

        for (HashMap var : vars) {
            String varIdn = (String) var.get("IDENT");
            if (ident.equals(varIdn)) {
                valueMap.put("ERROR", "INVALID ASSIGNMENT: Variable '" + ident + "' already exists");
            }
        }

        // convert variable elements first: identifiers, expressions
        if (value.matches(IDENT) && !value.matches(BOOLN) && !vars.isEmpty()) {
            HashMap<String, Object> varTable_v = new HashMap<>();
            HashMap<String, String> varTable_d = new HashMap<>();
            HashMap<String, Integer> varIndex = new HashMap<>();

            for (HashMap var : vars) {
                String varIdn = (String) var.get("IDENT");
                String varDtp = (String) var.get("DTYPE");
                var varVal = var.get("VALUE");
                int varLnm = (int) var.get("LNUM");

                varTable_v.put((String) varIdn, varVal);
                varTable_d.put((String) varIdn, varDtp);
                varIndex.put((String) varIdn, varLnm);
            }

            if (varTable_v.containsKey(ident)) {
                if (varIndex.get(ident) < lnum) {
                    if (!dtype.equals(varTable_d.get(ident))) {
                        valueMap.put("ERROR", "INVALID ASSIGNMENT: Value does not match datatype.");
                        return valueMap;
                    }
                    value = (String) varTable_v.get(ident);
                }
            } else {
                valueMap.put("ERROR", "MISSING VALUES: Variable '" + ident + "' not found.");
                return valueMap;
            }
        } else if (value.matches(EXPR)) {
            // TODO: evaluate expressions
        }
        
        switch (dtype) {
            case "int":
                
                break;
            case "dec":
                
                break;
            case "str":
                
                break;
            case "boo":
                
                break;
            default:
                throw new AssertionError();
        }
        

        return valueMap;
    }

    public static HashMap<String, Object> parseIfStatement(int lnum, String codeBlock,
            Stack<HashMap<String, Object>> vars) {
        // return value
        HashMap<String, Object> valueMap = new HashMap<>();

        return valueMap;
    }
}
