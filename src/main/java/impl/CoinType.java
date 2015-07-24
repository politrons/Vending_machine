package impl;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Created by pabloperezgarcia on 23/7/15.
 */
public enum CoinType {

    TEN_PENCE {
        @Override
        public BigDecimal getCoin() {
            return new BigDecimal("0.10");
        }
    },
    TWENTY_POUND {
        @Override
        public BigDecimal getCoin() {
            return new BigDecimal("0.20");
        }
    },
    FIFTY_POUND {
        @Override
        public BigDecimal getCoin() {
            return new BigDecimal("0.50");
        }
    },
    POUND {
        @Override
        public BigDecimal getCoin() {
            return new BigDecimal("1.0");
        }
    },
    OTHER{
        @Override
        public BigDecimal getCoin() {
            return coin;
        }
    };

    @Getter
    @Setter
    private static BigDecimal coin=null;

    public BigDecimal getCoin() {
        return null;
    }



}
