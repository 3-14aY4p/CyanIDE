/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package components;

import java.util.*;
import java.util.regex.*;

/**
 *
 * @author tianmei
 */
public class MyCodeCompiler {

    final static double ERRORCODE = Double.MAX_VALUE - Double.MAX_VALUE * 2;

    // BNF VARGRAMMAR STUFFS
    final static String IDENT = "[A-Za-z_][A-Za-z0-9_]*";

    final static String INTEGER = "-?[0-9]+";
    final static String DECIMAL = "-?[0-9]+\\.[0-9]+";
    final static String STRING = "\"[^\"\\r\\n]*\"";
    final static String BOOLEAN = "true|false";

    /*
    <  :
    << : <=
    >  :
    >> : >=
    <> : !=
     */
    final static String OPRT = "(\\+|-|\\*|/|<|>|<<|>>|<>)";
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

    final static String VARGRAMMAR
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

            if (!(l.isBlank() || l.startsWith(COMMENTPREFIX) || l.startsWith("..."))) {
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
                } else if (l.matches("(^if.*|.*}*elif.*|.*}*else.*|^endif$)")) {
                    if (l.matches("(^if.*)")) {
                        HashMap<String, Stack<HashMap<String, Object>>> ifResults = parseIfElseStatement(lnum, lines, vars);

                        vars.addAll(ifResults.get("VARS"));
                        errs.addAll(ifResults.get("ERRS"));
                        prnt.addAll(ifResults.get("PRNT"));
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

            if (value.contains(Double.toString(ERRORCODE))) {
                valueMap.put("ERROR", "INVALID SYNTAX: Invalid 'expression'.");
                return valueMap;
            } else {
                if (!extr.matches(".*(<|<<|>|>>|<>).*")) {
                    stmt = value;
                } else {
                    switch (value) {
                        case "1":
                            stmt = "true";
                            break;
                        case "0":
                            stmt = "false";
                            break;
                        default:
                            valueMap.put("ERROR", "INVALID SYNTAX: Invalid relational 'expression'.");
                            return valueMap;
                    }
                }
            }
        }

        valueMap.put("TEXT", stmt);
        return valueMap;
    }

    public static HashMap<String, Object> parseVariableAssignment(int lnum, String line, Stack<HashMap<String, Object>> variableContainer) {
        // dictionary to be returned
        HashMap<String, Object> valueMap = new HashMap<>();

        Pattern pattern = Pattern.compile(VARGRAMMAR);
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
                    if (value.contains(String.valueOf(ERRORCODE))) {
                        isValidType = false;
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
                    if (value.contains(String.valueOf(ERRORCODE))) {
                        isValidType = false;
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
                    String tvalue = Integer.toString((int) evaluateExpression(line, variableContainer));
                    if (tvalue.contains(String.valueOf(ERRORCODE))) {
                        isValidType = false;
                    } else {
                        switch (tvalue) {
                            case "1":
                                value = "true";
                                isValidType = true;
                                break;
                            case "0":
                                value = "false";
                                isValidType = true;
                                break;
                            default:
                                isValidType = false;
                                break;
                        }
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

    // TODO: parse if-else statement
    public static HashMap<String, Stack<HashMap<String, Object>>> parseIfElseStatement(int lnum, Stack<String> lines, Stack<HashMap<String, Object>> variableContainer) {
        // dictionary to be returned
        HashMap<String, Stack<HashMap<String, Object>>> valueMap = new HashMap<>();

        Stack<HashMap<String, Object>> vars = new Stack<>();
        Stack<HashMap<String, Object>> errs = new Stack<>();
        Stack<HashMap<String, Object>> prnt = new Stack<>();

        String ifPart = "^if\\s*\\((.*)\\)\\s*:\\s*$";
        String elifPart = "^elif\\s*\\((.*)\\)\\s*:\\s*$";
        String elsePart = "^else\\s*:\\s*$";

        valueMap.put("VARS", vars);
        valueMap.put("ERRS", errs);
        valueMap.put("PRNT", prnt);

        return valueMap;
    }

    // TODO: parse bool statement
    public static double evaluateExpression(String expression, Stack<HashMap<String, Object>> variableContainer) {
        String fString = expression;
        for (String s : expression.split(OPRT)) {
            String v = s.strip();
            if (v.matches(IDENT)) {
                for (HashMap hm : variableContainer) {
                    String varDtype = (String) hm.get("DTYPE");
                    String varIdent = (String) hm.get("IDENT");
                    String varValue = (String) hm.get("VALUE");

                    if (v.equals(varIdent) && (varDtype.equals("int") || varDtype.equals("dec") || varDtype.equals("bool"))) {
                        if (varDtype.equals("bool")) {
                            int b;
                            if (varValue.equals("true")) {
                                b = 1;
                            } else {
                                b = 0;
                            }
                            fString = fString.replace(v, String.valueOf(b));
                        }
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
            Stack<String> operators = new Stack<>();

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
                    operators.push(String.valueOf(tokens[i]));
                } else if (tokens[i] == ')') {
                    // If the character is ')', pop and apply operators until '(' is encountered
                    while (operators.peek() != "(") {
                        values.push(applyOperator(
                                operators.pop(), values.pop(),
                                values.pop()));
                    }
                    operators.pop(); // Pop the '('
                } else if (tokens[i] == '<' || tokens[i] == '>') {
                    StringBuilder sb = new StringBuilder();

                    while (i < tokens.length
                            && (tokens[i] == '<' || tokens[i] == '>')) {
                        sb.append(tokens[i]);
                        i++;
                    }
                    while (!operators.isEmpty()
                            && hasPrecedence(String.valueOf(tokens[i]),
                                    operators.peek())) {
                        values.push(applyOperator(
                                operators.pop(), values.pop(),
                                values.pop()));
                    }

                    operators.push(sb.toString());
                    i--;
                    // increment in the loop
                } else if (tokens[i] == '+' || tokens[i] == '-'
                        || tokens[i] == '*' || tokens[i] == '/') {
                    // If the character is an operator, pop and apply operators with higher precedence
                    while (!operators.isEmpty()
                            && hasPrecedence(String.valueOf(tokens[i]),
                                    operators.peek())) {
                        values.push(applyOperator(
                                operators.pop(), values.pop(),
                                values.pop()));
                    }
                    // Push the current operator to the operators stack
                    operators.push(String.valueOf(tokens[i]));
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
            return ERRORCODE;
        }
    }

    // Function to check if operator1 has higher precedence than operator2   
    private static boolean hasPrecedence(String operator1, String operator2) {
        if (operator2 == "(" || operator2 == ")") {
            return false;
        }
        return (operator1 != "*" && operator1 != "/")
                || (operator2 != "+" && operator2 != "-")
                || (operator2 != "<" && operator2 != "<<"
                && operator2 != ">" && operator2 != ">>"
                && operator2 != "<>");
    }

    // Function to apply the operator to two operands
    private static double applyOperator(String operator, double b, double a) {
        double res = 0;

        switch (operator) {
            // arithmetic operators
            case "+" -> {
                res = a + b;
            }
            case "-" -> {
                res = a - b;
            }
            case "*" -> {
                res = a * b;
            }
            case "/" -> {
                if (b == 0) {
                    throw new ArithmeticException(
                            "Cannot divide by zero");
                }
                res = a / b;
            }

            // relational operators
            case "<" -> {
                if (a < b) {
                    res = 1;
                } else {
                    res = 0;
                }
            }
            case "<<" -> {
                if (a <= b) {
                    res = 1;
                } else {
                    res = 0;
                }
            }
            case ">" -> {
                if (a > b) {
                    res = 1;
                } else {
                    res = 0;
                }
            }
            case ">>" -> {
                if (a >= b) {
                    res = 1;
                } else {
                    res = 0;
                }
            }
            case "<>" -> {
                if (a != b) {
                    res = 1;
                } else {
                    res = 0;
                }
            }
        }

        return res;
    }
}
