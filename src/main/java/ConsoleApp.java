import core.Function;
import service.QuadraticApproximationOptimizer;

import java.math.BigDecimal;
import java.util.Scanner;

public class ConsoleApp {

    public static void main(String[] args) {

        try (Scanner scanner = new Scanner(System.in)) {
            QuadraticApproximationOptimizer optimizer = new QuadraticApproximationOptimizer();

            Function selectedFunction = selectFunction(scanner);

            BigDecimal[] interval = getInterval(scanner, selectedFunction);
            BigDecimal a = interval[0];
            BigDecimal b = interval[1];

            BigDecimal epsilon1 = getEpsilon(scanner, "Enter relative precision for function value (epsilon1, e.g., 0.001): ");
            BigDecimal epsilon2 = getEpsilon(scanner, "Enter relative precision for coordinate (epsilon2, e.g., 0.001): ");

            System.out.println("\nRunning optimization for " + selectedFunction + " on [" + a + ", " + b + "]...");
            QuadraticApproximationOptimizer.OptimizationResult result = optimizer.findMinimum(selectedFunction, a, b, epsilon1, epsilon2);

            System.out.println("Optimization completed in " + result.iterations() + " iterations.");
            System.out.println("Found minimum at x = " + result.xMin());
            System.out.println("Function value at minimum f(x) = " + result.fMin());

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private static Function selectFunction(Scanner scanner) {
        System.out.println("Select a function to minimize:");
        for (int i = 0; i < Function.values().length; i++) {
            System.out.println((i + 1) + ". " + Function.values()[i]);
        }

        while (true) {
            System.out.print("Enter your choice (1-" + Function.values().length + "): ");
            String input = scanner.nextLine().trim();
            try {
                int choice = Integer.parseInt(input);
                if (choice >= 1 && choice <= Function.values().length) {
                    return Function.values()[choice - 1];
                } else {
                    System.out.println("Please enter a number between 1 and " + Function.values().length + ".");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    }

    private static BigDecimal[] getInterval(Scanner scanner, Function function) {
        BigDecimal a;
        BigDecimal b;

        while (true) {
            try {
                System.out.print("Enter the left boundary of the interval (a): ");
                a = new BigDecimal(scanner.nextLine().trim());

                System.out.print("Enter the right boundary of the interval (b): ");
                b = new BigDecimal(scanner.nextLine().trim());

                if (a.compareTo(b) >= 0) {
                    System.out.println("Error: Left boundary (a) must be less than right boundary (b).");
                    continue;
                }

                function.calculateValue(a);
                function.calculateValue(b);

                break;

            } catch (NumberFormatException e) {
                System.out.println("Error: Please enter valid numbers.");
            } catch (IllegalArgumentException e) {
                System.out.println("Error: " + e.getMessage() + " Please choose different boundaries.");
            } catch (Exception e) {
                System.out.println("An unexpected error occurred during function evaluation: " + e.getMessage());
            }
        }
        return new BigDecimal[]{a, b};
    }

    private static BigDecimal getEpsilon(Scanner scanner, String message) {
        while (true) {
            try {
                System.out.print(message);
                BigDecimal epsilon = new BigDecimal(scanner.nextLine().trim());
                if (epsilon.compareTo(BigDecimal.ZERO) <= 0) {
                    System.out.println("Error: Epsilon must be positive.");
                } else {
                    return epsilon;
                }
            } catch (NumberFormatException e) {
                System.out.println("Error: Please enter a valid number.");
            }
        }
    }
}