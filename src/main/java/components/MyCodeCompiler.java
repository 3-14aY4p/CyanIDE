/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package components;

import java.util.*;
import java.util.regex.*;
import javax.script.*;

/**
 *
 * @author tianmei
 */
public class MyCodeCompiler {

    // BNF GRAMMAR STUFFS
    final static String IDENT = "[A-Za-z_][A-Za-z0-9_]*";

    final static String INTEGER = "-?[0-9]+";
    final static String DECIMAL = "-?[0-9]+\\.[0-9]+";
    final static String STRING = "\"[^\"\\r\\n]*\"";
    final static String BOOLEAN = "true|false";

    final static String OPRT = "(\\+|-|\\*|/|>|<|>=|<=|!=|&&|\\|\\||!)";
    final static String EXPR = "("
            + "(" + INTEGER + "|" + DECIMAL + "|" + IDENT + ")"
            + "(" + "\\s*" + OPRT + "\\s*"
            + "(" + INTEGER + "|" + DECIMAL + "|" + IDENT + ")" + ")+"
            + ")";

    final static String NULL = "null";

    final static String DTYPE = "(int|dec|str|bool)";

    final static String VALUE = "(?:"
            + IDENT + "|"
            + DECIMAL + "|"
            + INTEGER + "|"
            + STRING + "|"
            + BOOLEAN + "|"
            + EXPR + "|"
            + NULL + ")";

    final static String GRAMMAR
            = "\\s*" + DTYPE + "\\s+"
            + "(" + IDENT + ")\\s*<-\\s*"
            + "(" + VALUE + ")\\s*;\\s*";

    final static String COMMENTPREFIX = ">>";

    // MAIN FUNCTION
    public static HashMap<String, Stack> compileProgram(String sourceCode) {
        Stack<String> lines = new Stack<>();
        lines.addAll(Arrays.asList(sourceCode.split("\n")));

        HashMap<String, Stack> finalHashMap = new HashMap<>();

        Stack<HashMap<String, Object>> vars = new Stack<>();
        Stack<HashMap<String, Object>> errs = new Stack<>();
        Stack<HashMap<String, Object>> prnt = new Stack<>();

        for (String l : lines) {
            int lnum = lines.indexOf(l) + 1;
            l = l.strip();
            HashMap<String, Object> hashMap = new HashMap<>();

            if (!(l.isBlank() || l.startsWith(COMMENTPREFIX))) {
                if (l.matches("^" + DTYPE + ".*")) {
                    hashMap = parseVariableAssignment(lnum, l, vars);
                    if (hashMap.containsKey("ERROR")) {
                        hashMap.put("LNUM", lnum);
                        errs.add(hashMap);
                    } else {
                        hashMap.put("LNUM", lnum);
                        vars.add(hashMap);
                    }
                } else if (l.matches("^echo.*")) {
                    hashMap = parsePrintStatement(lnum, l, vars);
                    if (hashMap.containsKey("ERROR")) {
                        hashMap.put("LNUM", lnum);
                        errs.add(hashMap);
                    } else {
                        hashMap.put("LNUM", lnum);
                        prnt.add(hashMap);
                    }
                } else {
                    hashMap.put("ERROR", "INVALID SYNTAX: Illegal start of expression.");
                    hashMap.put("LNUM", lnum);

                    errs.add(hashMap);
                }
            }
        }

        finalHashMap.put("VARS", vars);
        finalHashMap.put("ERRS", errs);
        finalHashMap.put("PRNT", prnt);

        return finalHashMap;
    }

    public static HashMap<String, Object> parsePrintStatement(int lnum, String line, Stack<HashMap<String, Object>> variableContainer) {
        // dictionary to be returned
        HashMap<String, Object> valueMap = new HashMap<>();

        String outerRegex = "^\\s*echo\\((.*)\\)\\s*;$";
        String innerRegex = "^\\\"(.*)\\\"$";

        Pattern pattern = Pattern.compile(outerRegex);
        Matcher matcher = pattern.matcher(line);

        if (!matcher.matches()) {
            valueMap.put("ERROR", "INVALID SYNTAX: Invalid 'print' structure.");
            return valueMap;
        }
        String extr = matcher.group(1);
        String stmt = "";

        if (extr.contains("\"")) {
            if (extr.matches(innerRegex)) {
                // removes "" and extracts STRING inside
                stmt = extr.replaceAll("\\\"", "");
            } else {
                valueMap.put("ERROR", "INVALID SYNTAX: Missing [\"] at the end of string.");
                return valueMap;
            }
        } else if (extr.matches(NULL + "|" + INTEGER + "|" + DECIMAL + "|" + BOOLEAN)) {
            stmt = extr;
        } else if (extr.matches(IDENT)) {
            HashMap<String, String> variableTable = new HashMap<>();
            HashMap<String, Integer> variableIndex = new HashMap<>();
            for (HashMap hm : variableContainer) {
                String varIdent = (String) hm.get("IDENT");
                String varValue = (String) hm.get("VALUE");
                int varLnNum = (int) hm.get("LNUM");

                variableTable.put(varIdent, varValue);
                variableIndex.put(varIdent, varLnNum);
            }
            if (variableTable.containsKey(extr)) {
                if (variableIndex.get(extr) < lnum) {
                    stmt = variableTable.get(extr);
                }
            } else {
                valueMap.put("ERROR", "INVALID SYNTAX: Variable '" + extr + "' not found.");
                return valueMap;
            }
        } else if (extr.matches(EXPR)) {
            String value = "";
            double res = evaluateExpression(extr, variableContainer);
            if (res % 1 != 0) {
                value = Double.toString(res);
            } else {
                value = Integer.toString((int) res);
            }

            if (value.contains("INVALID")) {
                valueMap.put("ERROR", value);
                return valueMap;
            } else {
                stmt = value;
            }
        }

        valueMap.put("TEXT", stmt);
        return valueMap;
    }

    public static HashMap<String, Object> parseVariableAssignment(int lnum, String line, Stack<HashMap<String, Object>> variableContainer) {
        // dictionary to be returned
        HashMap<String, Object> valueMap = new HashMap<>();

        Pattern pattern = Pattern.compile(GRAMMAR);
        Matcher matcher = pattern.matcher(line);

        if (!matcher.matches()) {
            valueMap.put("ERROR", "INVALID SYNTAX: Invalid 'declaration' structure.");
            return valueMap;
        }

        String dtype = matcher.group(1);
        String ident = matcher.group(2);
        String value = matcher.group(3);

        boolean isValidType = false;

        for (HashMap hm : variableContainer) {
            String varIdent = (String) hm.get("IDENT");
            if (ident.equals(varIdent)) {
                valueMap.put("ERROR", "INVALID ASSIGNMENT: Variable '" + ident + "' already exists.");
            }
        }

        // determine if value matches with dtype;
        // this could probably be shorter but meh
        switch (dtype) {
            case "int" -> {
                if (value.matches(INTEGER) || value.matches(NULL)) {
                    isValidType = true;
                } else if (value.matches(IDENT)) {
                    for (HashMap hm : variableContainer) {
                        String varDtype = (String) hm.get("DTYPE");
                        String varIdent = (String) hm.get("IDENT");
                        String varValue = (String) hm.get("VALUE");
                        int varLnNum = (int) hm.get("LNUM");

                        if (varDtype.equals("int") && varIdent.equals(value) && lnum > varLnNum) {
                            value = varValue;
                            isValidType = true;
                        } else {
                            isValidType = false;
                        }
                    }
                } else if (value.matches(EXPR)) {
                    value = Integer.toString((int) evaluateExpression(line, variableContainer));
                    if (value.contains("INVALID")) {
                        valueMap.put("ERROR", value);
                        return valueMap;
                    } else {
                        isValidType = true;
                    }
                } else {
                    isValidType = false;
                }
            }

            case "dec" -> {
                if (value.matches(INTEGER + "|" + DECIMAL) || value.matches(NULL)) {
                    isValidType = true;
                } else if (value.matches(IDENT)) {
                    for (HashMap hm : variableContainer) {
                        String varDtype = (String) hm.get("DTYPE");
                        String varIdent = (String) hm.get("IDENT");
                        String varValue = (String) hm.get("VALUE");
                        int varLnNum = (int) hm.get("LNUM");

                        if (varDtype.equals("dec") && varIdent.equals(value) && lnum > varLnNum) {
                            value = varValue;
                            isValidType = true;
                        } else {
                            isValidType = false;
                        }
                    }
                } else if (value.matches(EXPR)) {
                    value = Double.toString(evaluateExpression(line, variableContainer));
                    if (value.contains("INVALID")) {
                        valueMap.put("ERROR", value);
                        return valueMap;
                    } else {
                        isValidType = true;
                    }
                } else {
                    isValidType = false;
                }
            }

            case "str" -> {
                if (value.matches(STRING) || value.matches(NULL)) {
                    isValidType = true;
                } else if (value.matches(IDENT)) {
                    for (HashMap hm : variableContainer) {
                        String varDtype = (String) hm.get("DTYPE");
                        String varIdent = (String) hm.get("IDENT");
                        String varValue = (String) hm.get("VALUE");
                        int varLnNum = (int) hm.get("LNUM");

                        if (varDtype.equals("str") && varIdent.equals(value) && lnum > varLnNum) {
                            value = varValue;
                            isValidType = true;
                        } else {
                            isValidType = false;
                        }
                    }
                } else {
                    isValidType = false;
                }
            }

            case "bool" -> {
                if (value.matches(BOOLEAN) || value.matches(NULL)) {
                    isValidType = true;
                } else if (value.matches(IDENT)) {
                    for (HashMap hm : variableContainer) {
                        String varDtype = (String) hm.get("DTYPE");
                        String varIdent = (String) hm.get("IDENT");
                        String varValue = (String) hm.get("VALUE");
                        int varLnNum = (int) hm.get("LNUM");

                        if (varDtype.equals("bool") && varIdent.equals(value) && lnum > varLnNum) {
                            value = varValue;
                            isValidType = true;
                        } else {
                            isValidType = false;
                        }
                    }
                } else if (value.matches(EXPR)) {
                    // TODO

                    if (value.contains("INVALID")) {
                        valueMap.put("ERROR", value);
                        return valueMap;
                    } else {
                        isValidType = true;
                    }
                } else {
                    isValidType = false;
                }
            }

        }

        // set values for return
        if (isValidType) {
            valueMap.put("DTYPE", dtype);
            valueMap.put("IDENT", ident);
            valueMap.put("VALUE", value);
        } else {
            valueMap.put("ERROR", "INVALID DECLARATION: Value doesn't match datatype.");
        }
        return valueMap;
    }

    public static HashMap<String, Object> parseIfElseStatement(Stack<String> lines) {
        // dictionary to be returned
        HashMap<String, Object> valueMap = new HashMap<>();

        return valueMap;
    }

    public static double evaluateExpression(String expression, Stack<HashMap<String, Object>> variableContainer) {
        String fString = expression;
        for (String s : expression.split(OPRT)) {
            String v = s.strip();
            if (v.matches(IDENT)) {
                for (HashMap hm : variableContainer) {
                    String varDtype = (String) hm.get("DTYPE");
                    String varIdent = (String) hm.get("IDENT");
                    String varValue = (String) hm.get("VALUE");

                    if (v.equals(varIdent) && (varDtype.equals("int") || varDtype.equals("dec"))) {
                        fString = fString.replace(v, varValue);
                    }
                }
            }
        }
        Pattern p = Pattern.compile("(" + EXPR + ")");
        Matcher m = p.matcher(fString);

        if (m.find()) {
            fString = m.group(2);
        }

        return parseExpression(fString);
    }

    // SOURCE: https://www.geeksforgeeks.org/java/how-to-evaluate-math-expression-given-in-STRING-form-in-java/
    // Function to evaluate a mathematical expression given in STRING form
    public static double parseExpression(String expression) {
        try {
            char[] tokens = expression.toCharArray();

            // Stacks to store operands and operators
            Stack<Double> values = new Stack<>();
            Stack<Character> operators = new Stack<>();

            // Iterate through each character in the expression
            for (int i = 0; i < tokens.length; i++) {
                if (tokens[i] == ' ') {
                    continue;
                }

                // If the character is a digit or a DECIMAL point, parse the number
                if ((tokens[i] >= '0' && tokens[i] <= '9')
                        || tokens[i] == '.') {
                    StringBuilder sb = new StringBuilder();
                    // Continue collecting digits and the DECIMAL point to form a number
                    while (i < tokens.length
                            && (Character.isDigit(tokens[i])
                            || tokens[i] == '.')) {
                        sb.append(tokens[i]);
                        i++;
                    }
                    // Parse the collected number and push it to the values stack
                    values.push(Double.valueOf(sb.toString()));
                    i--; // Decrement i to account for the extra
                    // increment in the loop
                } else if (tokens[i] == '(') {
                    // If the character is '(', push it to the operators stack
                    operators.push(tokens[i]);
                } else if (tokens[i] == ')') {
                    // If the character is ')', pop and apply operators until '(' is encountered
                    while (operators.peek() != '(') {
                        values.push(applyOperator(
                                operators.pop(), values.pop(),
                                values.pop()));
                    }
                    operators.pop(); // Pop the '('
                } else if (tokens[i] == '+' || tokens[i] == '-'
                        || tokens[i] == '*'
                        || tokens[i] == '/') {
                    // If the character is an operator, pop and apply operators with higher precedence
                    while (!operators.isEmpty()
                            && hasPrecedence(tokens[i],
                                    operators.peek())) {
                        values.push(applyOperator(
                                operators.pop(), values.pop(),
                                values.pop()));
                    }
                    // Push the current operator to the operators stack
                    operators.push(tokens[i]);
                }
            }

            // Process any remaining operators in the stack
            while (!operators.isEmpty()) {
                values.push(applyOperator(operators.pop(),
                        values.pop(),
                        values.pop()));
            }

            // The result is the only remaining element in the values stack
            return values.pop();
        } catch (Exception e) {
            return 0;
        }
    }

    // Function to check if operator1 has higher precedence than operator2
    private static boolean hasPrecedence(char operator1, char operator2) {
        if (operator2 == '(' || operator2 == ')') {
            return false;
        }
        return (operator1 != '*' && operator1 != '/')
                || (operator2 != '+' && operator2 != '-');
    }

    // Function to apply the operator to two operands
    private static double applyOperator(char operator, double b, double a) {
        switch (operator) {
            case '+' -> {
                return a + b;
            }
            case '-' -> {
                return a - b;
            }
            case '*' -> {
                return a * b;
            }
            case '/' -> {
                if (b == 0) {
                    throw new ArithmeticException(
                            "Cannot divide by zero");
                }
                return a / b;
            }
        }
        return 0;
    }
}
