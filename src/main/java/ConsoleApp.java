import core.Function;
import service.QuadraticApproximationOptimizer;

import java.math.BigDecimal;
import java.util.Scanner;

public class ConsoleApp {

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            QuadraticApproximationOptimizer optimizer = new QuadraticApproximationOptimizer();

            Function selectedFunction = selectFunction(scanner);
            BigDecimal initialPoint = getInitialPoint(scanner, selectedFunction);
            BigDecimal stepSize = getStepSize(scanner);

            // BigDecimal epsilon1 = getEpsilon(scanner, "Введите относительную точность для значения функции (epsilon1): ");
            // BigDecimal epsilon2 = getEpsilon(scanner, "Введите относительную точность для координаты (epsilon2): ");

            BigDecimal epsilon1 = new BigDecimal("0.00001");
            BigDecimal epsilon2 = new BigDecimal("0.00001");

            System.out.println("\nЗапуск оптимизации для " + selectedFunction +
                    " с начальной точкой x1=" + initialPoint +
                    " и шагом Δx=" + stepSize + "...");

            QuadraticApproximationOptimizer.OptimizationResult result = optimizer.findMinimum(
                    selectedFunction, initialPoint, stepSize, epsilon1, epsilon2);

            System.out.println("Оптимизация завершена за " + result.iterations() + " итераций.");
            System.out.println("Найден минимум в точке x = " + result.xMin());
            System.out.println("Значение функции в минимуме f(x) = " + result.fMin());

        } catch (Exception e) {
            System.err.println("Ошибка: " + e.getMessage());
        }
    }

    private static Function selectFunction(Scanner scanner) {
        System.out.println("Выберите функцию для минимизации:");
        for (int i = 0; i < Function.values().length; i++) {
            System.out.println((i + 1) + ". " + Function.values()[i]);
        }

        while (true) {
            System.out.print("Введите ваш выбор (1-" + Function.values().length + "): ");
            String input = scanner.nextLine().trim();
            try {
                int choice = Integer.parseInt(input);
                if (choice >= 1 && choice <= Function.values().length) {
                    return Function.values()[choice - 1];
                } else {
                    System.out.println("Пожалуйста, введите число от 1 до " + Function.values().length + ".");
                }
            } catch (NumberFormatException e) {
                System.out.println("Неверный ввод. Пожалуйста, введите число.");
            }
        }
    }

    private static BigDecimal getInitialPoint(Scanner scanner, Function selectedFunction) {
        while (true) {
            try {
                System.out.print("Введите начальную точку (x1): ");
                BigDecimal point = new BigDecimal(scanner.nextLine().trim());

                // Проверка области определения для функций с x > 0
                if (selectedFunction.requiresPositiveX() && point.compareTo(BigDecimal.ZERO) <= 0) {
                    System.out.println("Ошибка: Для выбранной функции начальная точка должна быть > 0.");
                    continue;
                }

                // Проверим, что функция может быть вычислена в этой точке
                selectedFunction.calculateValue(point);

                return point;
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: Пожалуйста, введите корректное число.");
            } catch (IllegalArgumentException e) {
                System.out.println("Ошибка: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("Ошибка при вычислении функции в точке: " + e.getMessage());
            }
        }
    }

    private static BigDecimal getStepSize(Scanner scanner) {
        while (true) {
            try {
                System.out.print("Введите размер шага (Δx > 0): ");
                BigDecimal step = new BigDecimal(scanner.nextLine().trim());
                if (step.compareTo(BigDecimal.ZERO) <= 0) {
                    System.out.println("Ошибка: Размер шага должен быть положительным.");
                    continue;
                }
                return step;
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: Пожалуйста, введите корректное число.");
            }
        }
    }

    private static BigDecimal getEpsilon(Scanner scanner, String message) {
        while (true) {
            try {
                System.out.print(message);
                BigDecimal epsilon = new BigDecimal(scanner.nextLine().trim());
                if (epsilon.compareTo(BigDecimal.ZERO) <= 0) {
                    System.out.println("Ошибка: Точность должна быть положительной.");
                } else {
                    return epsilon;
                }
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: Пожалуйста, введите корректное число.");
            }
        }
    }
}