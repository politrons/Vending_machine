package impl.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Stack;

/**
 * Created by pabloperezgarcia on 23/7/15.
 */
public class VendingMachine {

    @Getter
    @Setter
    private Pepsi pepsi;

    @Getter
    @Setter
    private KitKat kitkat;

    @Getter
    @Setter
    private Smint smint;

    @Getter
    @Setter
    private Stack<CoinType> insertedCoins = new Stack<>();

    public BigDecimal clientMoney = new BigDecimal("0.0");

    public BigDecimal clientChange = new BigDecimal("0.0");

    public Integer tenPence = 3;

    public Integer twentyPence = 3;

    public Integer fiftyPence = 3;

    public Integer pounds = 3;

    @Getter
    @Setter
    private Boolean powerMachine = false;

    @Getter
    @Setter
    private VendingMachineState vendingMachineState = VendingMachineState.OFF;

    /**
     * Init items and money when the machine was provide
     */
    public VendingMachine() {
        this.pepsi = new Pepsi(1);
        this.kitkat = new KitKat(1);
        this.smint = new Smint(1);
    }


}
