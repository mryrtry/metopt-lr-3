package service;

import core.Function;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Arrays;

public class QuadraticApproximationOptimizer {

    private static final MathContext MC = MathContext.DECIMAL128;
    private static final BigDecimal TWO = new BigDecimal("2");
    private static final BigDecimal ZERO = BigDecimal.ZERO;

    public OptimizationResult findMinimum(Function function, BigDecimal a, BigDecimal b, BigDecimal epsilon1, BigDecimal epsilon2) {
        validateInput(function, a, b, epsilon1, epsilon2);

        BigDecimal x1 = a;
        BigDecimal x3 = b;
        BigDecimal x2 = a.add(b, MC).divide(TWO, MC);

        BigDecimal f1 = function.calculateValue(x1);
        BigDecimal f2 = function.calculateValue(x2);
        BigDecimal f3 = function.calculateValue(x3);

        BigDecimal xMin = findBestX(x1, f1, x2, f2, x3, f3);
        BigDecimal fMin = function.calculateValue(xMin);

        int iteration = 0;
        final int maxIterations = 100;

        while (iteration < maxIterations) {
            iteration++;

            BigDecimal xBar = calculateXBar(x1, f1, x2, f2, x3, f3);

            if (xBar == null) {

                System.out.println("Warning: Quadratic approximation failed (denominator zero). Returning current best estimate.");
                break;
            }

            BigDecimal fBar;
            try {
                fBar = function.calculateValue(xBar);
            } catch (Exception e) {

                System.out.println("Warning: Candidate point x=" + xBar + " is outside function domain. Returning current best estimate.");
                break;
            }

            BigDecimal absFMinMinusFBar = fMin.subtract(fBar, MC).abs();
            BigDecimal relativeFChange = (fBar.compareTo(ZERO) != 0) ? absFMinMinusFBar.divide(fBar.abs(), MC) : absFMinMinusFBar;

            BigDecimal absXMinMinusXBar = xMin.subtract(xBar, MC).abs();
            BigDecimal relativeXChange = (xBar.compareTo(ZERO) != 0) ? absXMinMinusXBar.divide(xBar.abs(), MC) : absXMinMinusXBar;

            boolean criteriaMet = (relativeFChange.compareTo(epsilon1) < 0) && (relativeXChange.compareTo(epsilon2) < 0);

            if (criteriaMet) {
                xMin = xBar;
                fMin = fBar;
                break;
            }

            BigDecimal bestXAmongAll = findBestX(x1, f1, x2, f2, x3, f3, xBar, fBar);
            BigDecimal bestFAmongAll = function.calculateValue(bestXAmongAll);

            BigDecimal[] allPoints = {x1, x2, x3, xBar};

            Arrays.sort(allPoints);

            int bestIndex = -1;
            for (int i = 0; i < allPoints.length; i++) {
                if (allPoints[i].compareTo(bestXAmongAll) == 0) {
                    bestIndex = i;
                    break;
                }
            }

            if (bestIndex == 0) {
                x1 = allPoints[0];
                x2 = allPoints[1];
                x3 = allPoints[2];
            } else if (bestIndex == allPoints.length - 1) {

                x1 = allPoints[bestIndex - 2];
                x2 = allPoints[bestIndex - 1];
                x3 = allPoints[bestIndex];
            } else {

                x1 = allPoints[bestIndex - 1];
                x2 = allPoints[bestIndex];
                x3 = allPoints[bestIndex + 1];
            }

            f1 = function.calculateValue(x1);
            f2 = function.calculateValue(x2);
            f3 = function.calculateValue(x3);

            xMin = bestXAmongAll;
            fMin = bestFAmongAll;
        }

        if (iteration >= maxIterations) {
            System.out.println("Warning: Reached maximum number of iterations (" + maxIterations + "). Result may not be fully converged.");
        }

        return new OptimizationResult(xMin, fMin, iteration);
    }

    private BigDecimal calculateXBar(BigDecimal x1, BigDecimal f1, BigDecimal x2, BigDecimal f2, BigDecimal x3, BigDecimal f3) {

        BigDecimal x1Sq = x1.multiply(x1, MC);
        BigDecimal x2Sq = x2.multiply(x2, MC);
        BigDecimal x3Sq = x3.multiply(x3, MC);

        BigDecimal term1Num = x2Sq.subtract(x3Sq, MC).multiply(f1, MC);
        BigDecimal term2Num = x3Sq.subtract(x1Sq, MC).multiply(f2, MC);
        BigDecimal term3Num = x1Sq.subtract(x2Sq, MC).multiply(f3, MC);
        BigDecimal numerator = term1Num.add(term2Num, MC).add(term3Num, MC);

        BigDecimal term1Den = x2.subtract(x3, MC).multiply(f1, MC);
        BigDecimal term2Den = x3.subtract(x1, MC).multiply(f2, MC);
        BigDecimal term3Den = x1.subtract(x2, MC).multiply(f3, MC);
        BigDecimal denominator = term1Den.add(term2Den, MC).add(term3Den, MC);

        if (denominator.compareTo(ZERO) == 0) {
            return null;
        }

        return numerator.divide(denominator, MC).multiply(new BigDecimal("0.5"), MC);
    }

    private BigDecimal findBestX(BigDecimal x1, BigDecimal f1, BigDecimal x2, BigDecimal f2, BigDecimal x3, BigDecimal f3) {

        if (f1.compareTo(f2) <= 0 && f1.compareTo(f3) <= 0) {
            return x1;
        } else if (f2.compareTo(f1) <= 0 && f2.compareTo(f3) <= 0) {
            return x2;
        } else {
            return x3;
        }
    }

    private BigDecimal findBestX(BigDecimal x1, BigDecimal f1, BigDecimal x2, BigDecimal f2, BigDecimal x3, BigDecimal f3, BigDecimal x4, BigDecimal f4) {

        BigDecimal bestX = x1;
        BigDecimal bestF = f1;

        if (f2.compareTo(bestF) < 0) {
            bestX = x2;
            bestF = f2;
        }
        if (f3.compareTo(bestF) < 0) {
            bestX = x3;
            bestF = f3;
        }
        if (f4.compareTo(bestF) < 0) {
            bestX = x4;
        }

        return bestX;
    }

    private void validateInput(Function function, BigDecimal a, BigDecimal b, BigDecimal epsilon1, BigDecimal epsilon2) {
        if (a.compareTo(b) >= 0) {
            throw new IllegalArgumentException("Invalid interval: a must be less than b. a=" + a + ", b=" + b);
        }
        if (epsilon1.compareTo(ZERO) <= 0 || epsilon2.compareTo(ZERO) <= 0) {
            throw new IllegalArgumentException("Epsilon values must be positive. epsilon1=" + epsilon1 + ", epsilon2=" + epsilon2);
        }

        try {
            function.calculateValue(a);
            function.calculateValue(b);
        } catch (Exception e) {
            throw new IllegalArgumentException("Function cannot be evaluated at the interval boundaries. " + e.getMessage(), e);
        }
    }

    public record OptimizationResult(BigDecimal xMin, BigDecimal fMin, int iterations) {
    }

}