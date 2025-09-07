package service;

import core.Function;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Arrays;
import java.util.Comparator;

public class QuadraticApproximationOptimizer {

    private static final MathContext MC = MathContext.DECIMAL128;
    private static final BigDecimal HALF = new BigDecimal("0.5");
    private static final BigDecimal TWO = new BigDecimal("2");
    private static final BigDecimal ZERO = BigDecimal.ZERO;

    public OptimizationResult findMinimum(Function function, BigDecimal initialX, BigDecimal deltaX,
                                          BigDecimal epsilon1, BigDecimal epsilon2) {

        validateInput(deltaX, epsilon1, epsilon2);

        // Инициализация точек
        BigDecimal x1 = initialX;
        BigDecimal x2 = x1.add(deltaX, MC);
        BigDecimal x3;

        // Определяем направление для третьей точки
        BigDecimal f1 = function.calculateValue(x1);
        BigDecimal f2 = function.calculateValue(x2);

        if (f1.compareTo(f2) < 0) {
            x3 = x1.subtract(deltaX, MC); // минимум слева
        } else {
            x3 = x1.add(deltaX.multiply(TWO, MC), MC); // минимум справа
        }

        int iterations = 0;
        final int maxIterations = 1000;

        while (iterations < maxIterations) {
            iterations++;

            // Проверяем порядок точек
            if (x2.subtract(x1, MC).multiply(x3.subtract(x2, MC), MC).compareTo(ZERO) < 0) {
                x2 = x1.add(deltaX, MC);
                f1 = function.calculateValue(x1);
                f2 = function.calculateValue(x2);

                if (f1.compareTo(f2) < 0) {
                    x3 = x1.subtract(deltaX, MC);
                } else {
                    x3 = x1.add(deltaX.multiply(TWO, MC), MC);
                }
            }

            BigDecimal f3 = function.calculateValue(x3);

            // Находим минимальное значение и соответствующую точку
            BigDecimal f0;
            BigDecimal x0;

            if (f1.compareTo(f2) <= 0 && f1.compareTo(f3) <= 0) {
                f0 = f1;
                x0 = x1;
            } else if (f2.compareTo(f1) <= 0 && f2.compareTo(f3) <= 0) {
                f0 = f2;
                x0 = x2;
            } else {
                f0 = f3;
                x0 = x3;
            }

            // Вычисляем x_min по формуле квадратичной аппроксимации
            BigDecimal numerator = x2.subtract(x1, MC).pow(2)
                    .multiply(f2.subtract(f3, MC), MC)
                    .subtract(x2.subtract(x3, MC).pow(2)
                            .multiply(f2.subtract(f1, MC), MC), MC);

            BigDecimal denominator = x2.subtract(x1, MC)
                    .multiply(f2.subtract(f3, MC), MC)
                    .subtract(x2.subtract(x3, MC)
                            .multiply(f2.subtract(f1, MC), MC), MC);

            if (denominator.compareTo(ZERO) == 0) {
                // Вырожденный случай - продолжаем с x2
                x1 = x2;
                x2 = x1.add(deltaX, MC);
                continue;
            }

            BigDecimal xMin = x2.subtract(
                    HALF.multiply(numerator, MC).divide(denominator, MC), MC
            );

            BigDecimal fMin = function.calculateValue(xMin);

            // Проверяем условия завершения
            BigDecimal relValueChange = calculateRelativeDifference(fMin, f0);
            BigDecimal relPointChange = calculateRelativeDifference(xMin, x0);

            if (relValueChange.compareTo(epsilon1) < 0 && relPointChange.compareTo(epsilon2) < 0) {
                return new OptimizationResult(xMin, fMin, iterations);
            }

            // Находим границы интервала
            BigDecimal leftDot = min(x1, x2, x3);
            BigDecimal rightDot = max(x1, x2, x3);

            // Создаем массив всех точек и сортируем
            BigDecimal[] allPoints = {x1, x2, x3, xMin};
            Arrays.sort(allPoints, Comparator.naturalOrder());

            // Обновляем точки
            if (xMin.compareTo(leftDot) >= 0 && xMin.compareTo(rightDot) <= 0) {
                // xMin внутри интервала
                if (fMin.compareTo(f0) < 0) {
                    // Используем xMin и соседние точки
                    int minIndex = findIndex(allPoints, xMin);
                    x1 = allPoints[Math.max(0, minIndex - 1)];
                    x2 = xMin;
                    x3 = allPoints[Math.min(allPoints.length - 1, minIndex + 1)];
                } else {
                    // Используем x0 и соседние точки
                    int zeroIndex = findIndex(allPoints, x0);
                    x1 = allPoints[Math.max(0, zeroIndex - 1)];
                    x2 = x0;
                    x3 = allPoints[Math.min(allPoints.length - 1, zeroIndex + 1)];
                }
            } else {
                // xMin вне интервала - начинаем с новой точки
                x1 = xMin;
                x2 = x1.add(deltaX, MC);

                f1 = function.calculateValue(x1);
                f2 = function.calculateValue(x2);

                if (f1.compareTo(f2) < 0) {
                    x3 = x1.subtract(deltaX, MC);
                } else {
                    x3 = x1.add(deltaX.multiply(TWO, MC), MC);
                }
            }
        }

        throw new RuntimeException("Алгоритм не сошелся за " + maxIterations + " итераций");
    }

    private BigDecimal min(BigDecimal a, BigDecimal b, BigDecimal c) {
        return a.min(b).min(c);
    }

    private BigDecimal max(BigDecimal a, BigDecimal b, BigDecimal c) {
        return a.max(b).max(c);
    }

    private int findIndex(BigDecimal[] array, BigDecimal value) {
        for (int i = 0; i < array.length; i++) {
            if (array[i].compareTo(value) == 0) {
                return i;
            }
        }
        return -1;
    }

    private BigDecimal calculateRelativeDifference(BigDecimal a, BigDecimal b) {
        if (b.compareTo(ZERO) == 0) {
            return a.abs(MC);
        }
        return a.subtract(b, MC).abs(MC).divide(b.abs(MC), MC);
    }

    private void validateInput(BigDecimal deltaX, BigDecimal epsilon1, BigDecimal epsilon2) {
        if (deltaX.compareTo(ZERO) <= 0) {
            throw new IllegalArgumentException("Шаг должен быть положительным");
        }
        if (epsilon1.compareTo(ZERO) <= 0 || epsilon2.compareTo(ZERO) <= 0) {
            throw new IllegalArgumentException("Точности должны быть положительными");
        }
    }

    public record OptimizationResult(BigDecimal xMin, BigDecimal fMin, int iterations) {}
}