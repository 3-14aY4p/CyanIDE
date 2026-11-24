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
public class MyCodeParser {

    // BNF GRAMMAR STUFFS
    final static String IDENT = "[A-Za-z_][A-Za-z0-9_]*";

    final static String INTEGER = "-?[0-9]+";
    final static String DECIMAL = "-?[0-9]+\\.[0-9]+";
    final static String STRING = "\"[^\"\\r\\n]*\"";
    final static String BOOLEAN = "true|false";

    final static String OPRT = "(\\+|-|\\*|/)";
    final static String EXPR = "("
            + INTEGER + "|" + DECIMAL
            + "\\s*" + OPRT + "\\s*"
            + INTEGER + "|" + DECIMAL
            + ")";

    final static String VALUE = "(?:"
            + DECIMAL + "|"
            + INTEGER + "|"
            + STRING + "|"
            + BOOLEAN + "|"
            + EXPR + ")";

    final static String GRAMMAR
            = "\\s*(int|dec|str|bool)\\s+"
            + "(" + IDENT + ")\\s*=\\s*"
            + "(" + VALUE + ")\\s*;\\s*";

    public static Stack<HashMap<String, Object>> tokenizer() {
        
    }
    
    public static HashMap<String, Object> parsePrintStatement(String line, Stack<HashMap<String, Object>> variableContainer) {
        // dictionary to be returned
        HashMap<String, Object> valueMap = new HashMap<>();

        String outerRegex = "^\\s*tell\\((.*)\\)\\s*;$";
        String innerRegex = "^\\\"(.*)\\\"$";

        Pattern pattern = Pattern.compile(outerRegex);
        Matcher matcher = pattern.matcher(line);

        if (!matcher.matches()) {
            valueMap.put("ERROR", "INVALID SYNTAX: Invalid 'print' structure.");
            return valueMap;
        }
        String extr = matcher.group(1);
        String stmt = "";

        if (extr.matches(innerRegex)) {
            // removes "" and extracts STRING inside
            stmt = extr.replaceAll("\\\"", "");
        } else if (extr.matches(INTEGER + "|" + DECIMAL + "|" + BOOLEAN)) {
            stmt = extr;
        } else if (extr.matches(IDENT)) {
            for (HashMap hm : variableContainer) {
                String varIdent = (String) hm.get("IDENT");
                String varValue = (String) hm.get("VALUE");

                if (varIdent.equals(extr)) {
                    stmt = varValue;
                } else {
                    valueMap.put("ERROR", "INVALID SYNTAX: Variable " + extr + " not found.");
                    return valueMap;
                }
            }
        } else if (extr.matches(EXPR)) {
            stmt = evaluateExpression(line, true);
            if (stmt.contains("INVALID")) {
                valueMap.put("ERROR", stmt);
                return valueMap;
            }
        }

        valueMap.put("TEXT", stmt);
        return valueMap;
    }

    public static HashMap<String, Object> parseVariableAssignment(String line, Stack<HashMap<String, Object>> variableContainer) {
        // dictionary to be returned
        HashMap<String, Object> valueMap = new HashMap<>();

        Pattern pattern = Pattern.compile(GRAMMAR);
        Matcher matcher = pattern.matcher(line);

        if (!matcher.matches()) {
            valueMap.put("ERROR", "INVALID SYNTAX: Declaration doesn't follow the grammar.");
            return valueMap;
        }

        String dtype = matcher.group(1);
        String ident = matcher.group(2);
        String value = matcher.group(3);

        boolean isValidType = false;

        // determine if value matches with dtype;
        // this could probably be shorter but meh
        switch (dtype) {
            case "int" -> {
                if (value.matches(INTEGER)) {
                    isValidType = true;
                } else if (value.matches(IDENT)) {
                    for (HashMap hm : variableContainer) {
                        String varDtype = (String) hm.get("DTYPE");
                        String varIdent = (String) hm.get("IDENT");
                        String varValue = (String) hm.get("VALUE");

                        if (varDtype.equals("int") && varIdent.equals(value)) {
                            value = varValue;
                            isValidType = true;
                        } else {
                            isValidType = false;
                        }
                    }
                } else if (value.matches(EXPR)) {
                    value = evaluateExpression(line, true);
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
                if (value.matches(INTEGER + "|" + DECIMAL)) {
                    isValidType = true;
                } else if (value.matches(IDENT)) {
                    for (HashMap hm : variableContainer) {
                        String varDtype = (String) hm.get("DTYPE");
                        String varIdent = (String) hm.get("IDENT");
                        String varValue = (String) hm.get("VALUE");

                        if (varDtype.equals("dec") && varIdent.equals(value)) {
                            value = varValue;
                            isValidType = true;
                        } else {
                            isValidType = false;
                        }
                    }
                } else if (value.matches(EXPR)) {
                    value = evaluateExpression(line, false);
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
                if (value.matches(STRING)) {
                    isValidType = true;
                } else if (value.matches(IDENT)) {
                    for (HashMap hm : variableContainer) {
                        String varDtype = (String) hm.get("DTYPE");
                        String varIdent = (String) hm.get("IDENT");
                        String varValue = (String) hm.get("VALUE");

                        if (varDtype.equals("str") && varIdent.equals(value)) {
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
                if (value.matches(BOOLEAN)) {
                    isValidType = true;
                } else if (value.matches(IDENT)) {
                    for (HashMap hm : variableContainer) {
                        String varDtype = (String) hm.get("DTYPE");
                        String varIdent = (String) hm.get("IDENT");
                        String varValue = (String) hm.get("VALUE");

                        if (varDtype.equals("bool") && varIdent.equals(value)) {
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
//    public static boolean parseLogicalExpression(String expression) {
//        
//    }
    // just returns the clean string of the evaluated expression
    public static String evaluateExpression(String line, boolean preferINT) {
        try {
            double parsedString = parseArithmeticExpression(line);

            if (preferINT) {
                return Integer.toString((int) parsedString);
            } else {
                return Double.toString(parsedString);
            }
        } catch (Exception e) {
            return "INVALID EXPRESSION: Invalid 'expression' structure.";
        }
    }

    // SOURCE: https://www.geeksforgeeks.org/java/how-to-evaluate-math-expression-given-in-STRING-form-in-java/
    // Function to evaluate a mathematical expression given in STRING form
    public static double parseArithmeticExpression(String expression) {
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
        } catch (NumberFormatException numberFormatException) {
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
