package com.calculator.vault.privacy.core.utilities;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.regex.Pattern;

public final class CalculatorEngine {
    private static final String OPERATORS = "+-×÷^";
    private static final Pattern PIN_PATTERN = Pattern.compile("^\\d{4,8}$");
    private static final int MAX_EXPRESSION_LENGTH = 24;

    private String expression = "";
    private double memory;
    private double lastResult;
    private String pinCandidate = "";
    private boolean justEvaluated;

    public String getDisplay() {
        if ("Error".equals(expression)) return "Error";
        return expression.isEmpty() ? "0" : expression;
    }

    public void inputDigit(String digit) {
        if ("Error".equals(expression)) clear();
        if (justEvaluated) {
            clear();
            justEvaluated = false;
        }
        if (expression.length() >= MAX_EXPRESSION_LENGTH) return;
        pinCandidate += digit;
        expression += digit;
    }

    public void inputDecimal() {
        if ("Error".equals(expression)) clear();
        if (justEvaluated) {
            clear();
            justEvaluated = false;
        }
        pinCandidate = "";
        String[] parts = expression.split("[+\\-×÷%^]");
        String current = parts.length == 0 ? "" : parts[parts.length - 1];
        if (!current.contains(".") && expression.length() < MAX_EXPRESSION_LENGTH) {
            expression = expression.isEmpty() ? "0." : expression + ".";
        }
    }

    public void inputOperator(String op) {
        justEvaluated = false;
        pinCandidate = "";
        if ("Error".equals(expression)) {
            if ("-".equals(op)) expression = "-";
            return;
        }
        if (expression.isEmpty()) {
            if ("-".equals(op)) expression = "-";
            return;
        }
        char last = expression.charAt(expression.length() - 1);
        if (OPERATORS.indexOf(last) >= 0) {
            expression = expression.substring(0, expression.length() - 1) + op;
        } else {
            expression += op;
        }
    }

    public void inputPercent() {
        pinCandidate = "";
        if (expression.isEmpty()) return;
        try {
            double value = evaluateExpression(expression) / 100.0;
            expression = formatResult(value);
        } catch (Exception ignored) {
            expression = "Error";
        }
    }

    public void inputSquareRoot() {
        pinCandidate = "";
        try {
            double value = expression.isEmpty() ? lastResult : evaluateExpression(expression);
            expression = formatResult(Math.sqrt(value));
        } catch (Exception ignored) {
            expression = "Error";
        }
    }

    public void inputPower() {
        pinCandidate = "";
        if (!expression.isEmpty() && OPERATORS.indexOf(expression.charAt(expression.length() - 1)) < 0) {
            expression += "^";
        }
    }

    public void clear() {
        expression = "";
        pinCandidate = "";
        justEvaluated = false;
    }

    public void backspace() {
        if ("Error".equals(expression)) {
            clear();
            return;
        }
        if (!expression.isEmpty()) {
            expression = expression.substring(0, expression.length() - 1);
            syncPinCandidateFromExpression();
        }
    }

    public void memoryClear() { memory = 0.0; }

    public void memoryRecall() {
        expression = formatResult(memory);
        syncPinCandidateFromExpression();
    }

    public void memoryAdd() {
        Double value = safeEvaluate();
        if (value != null) memory += value;
    }

    public void memorySubtract() {
        Double value = safeEvaluate();
        if (value != null) memory -= value;
    }

    public void memoryStore() {
        Double value = safeEvaluate();
        if (value != null) memory = value;
    }

    public String evaluate() {
        if (isPinAttempt()) return null;
        try {
            validateExpression(expression.isEmpty() ? "0" : expression);
            double result = evaluateExpression(expression.isEmpty() ? "0" : expression);
            lastResult = result;
            String formatted = formatResult(result);
            expression = formatted;
            pinCandidate = "";
            justEvaluated = true;
            return formatted;
        } catch (Exception ignored) {
            expression = "Error";
            pinCandidate = "";
            return "Error";
        }
    }

    public boolean isPinAttempt() {
        return PIN_PATTERN.matcher(expression).matches();
    }

    public String getPinForValidation() {
        return expression;
    }

    public boolean isScientificMode() {
        return false;
    }

    public void setScientificMode(boolean enabled) {
        // Reserved for phase 2 UI toggle.
    }

    private void syncPinCandidateFromExpression() {
        int index = expression.length() - 1;
        StringBuilder trailing = new StringBuilder();
        while (index >= 0 && Character.isDigit(expression.charAt(index))) {
            trailing.insert(0, expression.charAt(index));
            index--;
        }
        String prefix = expression.substring(0, index + 1);
        if (trailing.length() > 0 && !hasOperators(prefix)) {
            pinCandidate = trailing.toString();
        } else {
            pinCandidate = "";
        }
    }

    private boolean hasOperators(String value) {
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            if (OPERATORS.indexOf(c) >= 0 || c == '%' || c == '^' || c == '.') return true;
        }
        return false;
    }

    private Double safeEvaluate() {
        try {
            validateExpression(expression.isEmpty() ? "0" : expression);
            return evaluateExpression(expression.isEmpty() ? "0" : expression);
        } catch (Exception ignored) {
            return null;
        }
    }

    private void validateExpression(String expr) {
        if (expr.isEmpty()) return;
        char last = expr.charAt(expr.length() - 1);
        if (OPERATORS.indexOf(last) >= 0) throw new IllegalStateException("Trailing operator");
    }

    private double evaluateExpression(String expr) {
        String normalized = expr.replace("×", "*").replace("÷", "/").replace("%", "/100");
        return evaluateWithPrecedence(normalized);
    }

    private double evaluateWithPrecedence(String expr) {
        List<Token> tokens = tokenize(expr);
        if (tokens.isEmpty()) return 0.0;

        Deque<Double> values = new ArrayDeque<>();
        Deque<String> ops = new ArrayDeque<>();

        for (Token token : tokens) {
            if (token instanceof NumberToken) {
                values.addLast(((NumberToken) token).value);
            } else if (token instanceof OperatorToken) {
                String symbol = ((OperatorToken) token).symbol;
                while (shouldPopStackTop(ops, symbol)) {
                    applyOp(values, ops);
                }
                ops.addLast(symbol);
            }
        }
        while (!ops.isEmpty()) applyOp(values, ops);
        if (values.size() != 1) throw new IllegalStateException("Invalid expression");
        return values.removeLast();
    }

    private boolean shouldPopStackTop(Deque<String> ops, String currentOp) {
        if (ops.isEmpty()) return false;
        String top = ops.peekLast();
        if ("^".equals(currentOp)) return precedence(top) > precedence(currentOp);
        return precedence(top) >= precedence(currentOp);
    }

    private int precedence(String op) {
        switch (op) {
            case "+":
            case "-":
                return 1;
            case "*":
            case "/":
                return 2;
            case "^":
                return 3;
            default:
                return 0;
        }
    }

    private void applyOp(Deque<Double> values, Deque<String> ops) {
        if (values.size() < 2 || ops.isEmpty()) return;
        double b = values.removeLast();
        double a = values.removeLast();
        String op = ops.removeLast();
        switch (op) {
            case "+":
                values.addLast(a + b);
                break;
            case "-":
                values.addLast(a - b);
                break;
            case "*":
                values.addLast(a * b);
                break;
            case "/":
                if (b == 0.0) throw new ArithmeticException();
                values.addLast(a / b);
                break;
            case "^":
                values.addLast(Math.pow(a, b));
                break;
            default:
                throw new IllegalArgumentException("Unknown operator");
        }
    }

    private List<Token> tokenize(String expr) {
        List<Token> tokens = new ArrayList<>();
        int i = 0;
        while (i < expr.length()) {
            char c = expr.charAt(i);
            if (Character.isDigit(c) || c == '.') {
                int start = i;
                while (i < expr.length()) {
                    char current = expr.charAt(i);
                    if (!Character.isDigit(current) && current != '.') break;
                    i++;
                }
                tokens.add(new NumberToken(Double.parseDouble(expr.substring(start, i))));
            } else if ("+-*/^".indexOf(c) >= 0) {
                if (c == '-' && (tokens.isEmpty() || tokens.get(tokens.size() - 1) instanceof OperatorToken)) {
                    int start = i;
                    i++;
                    while (i < expr.length()) {
                        char current = expr.charAt(i);
                        if (!Character.isDigit(current) && current != '.') break;
                        i++;
                    }
                    tokens.add(new NumberToken(Double.parseDouble(expr.substring(start, i))));
                } else {
                    tokens.add(new OperatorToken(String.valueOf(c)));
                    i++;
                }
            } else {
                i++;
            }
        }
        return tokens;
    }

    private String formatResult(double value) {
        if (Double.isNaN(value) || Double.isInfinite(value)) return "Error";
        if (value == (long) value) return String.valueOf((long) value);
        String formatted = String.format("%.8f", value);
        while (formatted.endsWith("0")) formatted = formatted.substring(0, formatted.length() - 1);
        if (formatted.endsWith(".")) formatted = formatted.substring(0, formatted.length() - 1);
        return formatted;
    }

    private interface Token {}

    private static final class NumberToken implements Token {
        private final double value;

        private NumberToken(double value) {
            this.value = value;
        }
    }

    private static final class OperatorToken implements Token {
        private final String symbol;

        private OperatorToken(String symbol) {
            this.symbol = symbol;
        }
    }
}
