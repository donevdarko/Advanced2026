package aud;

import java.util.Scanner;
class Calculator {
    private double result;
    private static final char PLUS = '+';
    private static final char MINUS = '-';
    private static final char MULTIPLY = '*';
    private static final char DIVIDE = '/';

    public Calculator() {
        result = 0;
    }

    public String init() {
        return String.format("result = %f", result);
    }

    public double getResult() {
        return result;
    }

    public String execute(char operator, double value)
            throws UnknownOperatorException {

        if (operator == PLUS) {
            result += value;
        } else if (operator == MINUS) {
            result -= value;
        } else if (operator == MULTIPLY) {
            result *= value;
        } else if (operator == DIVIDE) {
            result /= value;
        } else {
            throw new UnknownOperatorException(operator);
        }
        return String.format("result %c %f = %f", operator, value, result);
    }

    class UnknownOperatorException extends Exception {
        public UnknownOperatorException(char operator) {
            super(String.format("%c is unknown operation", operator));
        }
    }

    @Override
    public String toString() {
        return String.format("updated result = %f", result);
    }

}

public class CalculatorTest {
    static final char RESULT = 'r';

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            Calculator calculator = new Calculator();
            System.out.println(calculator.init());
            while (true) {
                String line = scanner.nextLine();
                char choice = getCharLower(line);
                if (choice == RESULT) {
                    System.out.println(String.format("final result = %f", calculator.getResult()));
                    break;
                }
                String[] parts = line.split("\\s+");
                char operator = parts[0].charAt(0);
                double value = Double.parseDouble(parts[1]);
                try {
                    String result = calculator.execute(operator, value);
                    System.out.println(result);
                    System.out.println(calculator);
                } catch (Calculator.UnknownOperatorException e) {
                    System.out.println(e.getMessage());
                }
            }
            System.out.println("(Y/N)");
            String line = scanner.nextLine();
            char choice = getCharLower(line);
            if (choice == 'n') {
                break;
            }
        }
    }

    static char getCharLower(String line) {
        if (line.trim().length() > 0) {
            return Character.toLowerCase(line.charAt(0));
        }
        return '?';
    }
}