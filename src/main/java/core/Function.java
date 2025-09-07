package core;

import java.math.BigDecimal;
import java.math.MathContext;

public enum Function {

    FUNCTION_1("f(x)=1/2*x^2 - sin(x)", x -> {
        BigDecimal half = new BigDecimal("0.5");
        BigDecimal xSquared = x.multiply(x, MathContext.DECIMAL128);
        BigDecimal sinX = BigDecimal.valueOf(Math.sin(x.doubleValue()));
        return half.multiply(xSquared, MathContext.DECIMAL128).subtract(sinX, MathContext.DECIMAL128);
    }),

    FUNCTION_2("f(x)=ln(1+x^2) - sin(x)", x -> {
        if (x.add(BigDecimal.ONE).compareTo(BigDecimal.ONE) < 0) {
            throw new IllegalArgumentException("Argument for logarithm must be positive. For x=" + x + ", (1+x^2) is not positive.");
        }
        BigDecimal one = BigDecimal.ONE;
        BigDecimal xSquared = x.multiply(x, MathContext.DECIMAL128);
        BigDecimal lnArg = one.add(xSquared);
        BigDecimal lnVal = BigDecimal.valueOf(Math.log(lnArg.doubleValue()));
        BigDecimal sinX = BigDecimal.valueOf(Math.sin(x.doubleValue()));
        return lnVal.subtract(sinX, MathContext.DECIMAL128);
    }),

    FUNCTION_3("f(x)=x^2 - 3x + x*ln(x)", x -> {
        if (x.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Argument for logarithm must be positive. x must be > 0. Got x=" + x);
        }
        BigDecimal xSquared = x.multiply(x, MathContext.DECIMAL128);
        BigDecimal threeX = new BigDecimal("3").multiply(x, MathContext.DECIMAL128);
        BigDecimal lnX = BigDecimal.valueOf(Math.log(x.doubleValue()));
        BigDecimal xLnX = x.multiply(lnX, MathContext.DECIMAL128);
        return xSquared.subtract(threeX, MathContext.DECIMAL128).add(xLnX, MathContext.DECIMAL128);
    }),

    FUNCTION_4("f(x)=1/4*x^4 - x^2 - 8x + 12", x -> {
        BigDecimal quarter = new BigDecimal("0.25");
        BigDecimal xSquared = x.multiply(x, MathContext.DECIMAL128);
        BigDecimal xFourth = xSquared.multiply(xSquared, MathContext.DECIMAL128);
        BigDecimal eightX = new BigDecimal("8").multiply(x, MathContext.DECIMAL128);
        BigDecimal term1 = quarter.multiply(xFourth, MathContext.DECIMAL128);
        BigDecimal term4 = new BigDecimal("12");
        return term1.subtract(xSquared, MathContext.DECIMAL128).subtract(eightX, MathContext.DECIMAL128).add(term4, MathContext.DECIMAL128);
    });

    private final String description;
    private final java.util.function.Function<BigDecimal, BigDecimal> calculation;

    Function(String description, java.util.function.Function<BigDecimal, BigDecimal> calculation) {
        this.description = description;
        this.calculation = calculation;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal calculateValue(BigDecimal x) {
        return calculation.apply(x);
    }

    @Override
    public String toString() {
        return description;
    }

}