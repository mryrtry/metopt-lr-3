package core;

import java.math.BigDecimal;
import java.math.MathContext;

public enum Function {
    FUNCTION_1("f(x)=1/2*x^2 - sin(x)") {
        @Override
        public BigDecimal calculateValue(BigDecimal x) {
            MathContext mc = MathContext.DECIMAL128;
            BigDecimal half = new BigDecimal("0.5");
            BigDecimal xSquared = x.multiply(x, mc);
            BigDecimal sinX = BigDecimal.valueOf(Math.sin(x.doubleValue()));
            return half.multiply(xSquared, mc).subtract(sinX, mc);
        }

        @Override
        public boolean requiresPositiveX() {
            return false;
        }
    },

    FUNCTION_2("f(x)=ln(1+x^2) - sin(x)") {
        @Override
        public BigDecimal calculateValue(BigDecimal x) {
            MathContext mc = MathContext.DECIMAL128;
            BigDecimal onePlusXSquared = x.pow(2).add(BigDecimal.ONE);
            if (onePlusXSquared.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Аргумент логарифма должен быть положительным");
            }
            BigDecimal lnVal = BigDecimal.valueOf(Math.log(onePlusXSquared.doubleValue()));
            BigDecimal sinX = BigDecimal.valueOf(Math.sin(x.doubleValue()));
            return lnVal.subtract(sinX, mc);
        }

        @Override
        public boolean requiresPositiveX() {
            return false;
        }
    },

    FUNCTION_3("f(x)=x^2 - 3x + x*ln(x)") {
        @Override
        public BigDecimal calculateValue(BigDecimal x) {
            if (x.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("x должен быть > 0 для ln(x)");
            }
            MathContext mc = MathContext.DECIMAL128;
            BigDecimal xSquared = x.multiply(x, mc);
            BigDecimal threeX = new BigDecimal("3").multiply(x, mc);
            BigDecimal lnX = BigDecimal.valueOf(Math.log(x.doubleValue()));
            BigDecimal xLnX = x.multiply(lnX, mc);
            return xSquared.subtract(threeX, mc).add(xLnX, mc);
        }

        @Override
        public boolean requiresPositiveX() {
            return true;
        }
    },

    FUNCTION_4("f(x)=1/4*x^4 - x^2 - 8x + 12") {
        @Override
        public BigDecimal calculateValue(BigDecimal x) {
            MathContext mc = MathContext.DECIMAL128;
            BigDecimal quarter = new BigDecimal("0.25");
            BigDecimal xSquared = x.multiply(x, mc);
            BigDecimal xFourth = xSquared.multiply(xSquared, mc);
            BigDecimal eightX = new BigDecimal("8").multiply(x, mc);

            return quarter.multiply(xFourth, mc)
                    .subtract(xSquared, mc)
                    .subtract(eightX, mc)
                    .add(new BigDecimal("12"), mc);
        }

        @Override
        public boolean requiresPositiveX() {
            return false;
        }
    };

    private final String description;

    Function(String description) {
        this.description = description;
    }

    public abstract BigDecimal calculateValue(BigDecimal x);
    public abstract boolean requiresPositiveX();

    @Override
    public String toString() {
        return description;
    }
}