package impl;

import java.math.BigDecimal;

/**
 * Created by pabloperezgarcia on 23/7/15.
 */
public class Smint extends Item {

    public Smint() {
        super();
    }

    public Smint(final Integer amount) {
        super(amount);
    }

    @Override
    public ItemType getItemType() {
        return ItemType.SMINT;
    }

    @Override
    public BigDecimal getPrice() {
        return new BigDecimal("0.60");
    }

    @Override
    public Boolean isAvailable() {
        return amount > 0;
    }
}