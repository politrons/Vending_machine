package impl.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Created by pabloperezgarcia on 23/7/15.
 */
public class OutputMachine {

    @Getter
    @Setter
    String screenMessage;

    @Getter
    @Setter
    BigDecimal change = new BigDecimal(0.0);

    @Getter
    @Setter
    Item item;

    public OutputMachine(final String screenMessage) {
        this.screenMessage = screenMessage;
    }

    public OutputMachine(final Item item) {
        this.item = item;
    }

    public OutputMachine(final String screenMessage, final BigDecimal change) {
        this.screenMessage= screenMessage;
        this.change=change;
    }

    public OutputMachine(final BigDecimal change) {
        this.change = change;
    }
}
