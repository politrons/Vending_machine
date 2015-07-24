package impl.model;

import java.math.BigDecimal;

/**
 * Created by pabloperezgarcia on 23/7/15.
 */
public class Pepsi extends Item{

    public Pepsi() {
        super();
    }

    public Pepsi(final Integer amount) {
        super(amount);
    }

    @Override
    public ItemType getItemType() {
        return ItemType.PEPSI;
    }

    @Override
    public BigDecimal getPrice() {
        return new BigDecimal("1.00");
    }

    @Override
    public Boolean isAvailable() {
        return amount > 0;
    }
}