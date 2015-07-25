package impl.model;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by pabloperezgarcia on 23/7/15.
 */
public enum ClientAction {
    INSERT_MONEY {
        @Override
        public CoinType getCoinType() {
            return coinType;
        }
    },
    COIN_RETURN {
        @Override
        public CoinType getCoinType() {
            return coinType;
        }
    },
    GET_ITEM {
        @Override
        public Item getItem() {
            return item;
        }
    };
    
    @Getter
    @Setter
    private static CoinType coinType;

    @Getter
    @Setter
    private static Item item;


    public CoinType getCoinType() {
        return null;
    }

    public Item getItem() {
        return null;
    }
}
