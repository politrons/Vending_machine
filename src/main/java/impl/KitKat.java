package impl;

import java.math.BigDecimal;

/**
 * Created by pabloperezgarcia on 23/7/15.
 */
public class KitKat extends Item{

    public KitKat() {
        super();
    }

    public KitKat(final Integer amount) {
        super(amount);
    }

    @Override
    public ItemType getItemType() {
        return ItemType.KIKAT;
    }

    @Override
    public BigDecimal getPrice() {
        return new BigDecimal("1.70");
    }

    @Override
    public Boolean isAvailable() {
        return amount > 0;
    }
}
