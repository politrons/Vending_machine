package impl;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Created by pabloperezgarcia on 23/7/15.
 */
public abstract class  Item {

    @Getter
    @Setter
    protected Integer amount;

    @Getter
    @Setter
    protected Boolean selected=false;

    public Item() {}

    public Item(final Integer amount) {
        this.amount = amount;
    }

    public abstract ItemType getItemType();

    public abstract BigDecimal getPrice();

    public abstract Boolean isAvailable();
}
