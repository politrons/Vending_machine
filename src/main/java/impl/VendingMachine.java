package impl;

import impl.exceptions.NoChangeAvailableException;
import impl.exceptions.NoEnoughMoneyException;
import impl.exceptions.NoProductAvailableException;
import impl.model.*;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Stack;

/**
 * Created by pabloperezgarcia on 23/7/15.
 */
public class VendingMachine {

    private Pepsi pepsi;

    private KitKat kitkat;

    private Smint smint;

    private Stack<CoinType> insertedCoins = new Stack<>();

    private BigDecimal clientMoney = new BigDecimal("0.0");

    private BigDecimal clientChange = new BigDecimal("0.0");

    private Integer tenPence = 3;

    private Integer twentyPence = 3;

    private Integer fiftyPence = 3;

    private Integer pounds = 3;

    private Boolean powerMachine = false;

    private VendingMachineState vendingMachineState = VendingMachineState.OFF;

    /**
     * Init items and money when the machine was provide
     */
    public VendingMachine() {
        this.pepsi = new Pepsi(1);
        this.kitkat = new KitKat(1);
        this.smint = new Smint(1);
    }

    public boolean isOn() {
        return powerMachine;
    }

    public void setOn() {
        powerMachine = true;
        vendingMachineState = VendingMachineState.NO_COIN;
    }

    public void setOff() {
        powerMachine = false;
        vendingMachineState = VendingMachineState.OFF;
    }


    /**
     * Access to increase the machine coins and client money on the machine
     *
     * @param coinType
     * @return
     */
    public OutputMachine insertMoney(CoinType coinType) {
        switch (coinType) {
            case TEN_PENCE:
                tenPence++;
                break;
            case TWENTY_PENCE:
                twentyPence++;
                break;
            case FIFTY_PENCE:
                fiftyPence++;
                break;
            case POUND:
                pounds++;
                break;
            default:
                return new OutputMachine("No coin accepted", coinType.getCoin());
        }
        insertedCoins.push(coinType);
        vendingMachineState = VendingMachineState.AVAILABLE_MONEY;
        return new OutputMachine(String.format("Inserted money %s", getTotalClientMoney()));
    }

    /**
     * We iterate over the coins that the user introduce in the machine to restore the machine coins
     *
     * @return
     */
    public OutputMachine returnMoney() {
        clientMoney = getTotalClientMoney();
        while (insertedCoins.size() > 0) {
            CoinType insertedCoin = insertedCoins.pop();
            switch (insertedCoin) {
                case TEN_PENCE:
                    tenPence--;
                    break;
                case TWENTY_PENCE:
                    twentyPence--;
                    break;
                case FIFTY_PENCE:
                    fiftyPence--;
                    break;
                case POUND:
                    pounds--;
                    break;
            }
        }
        vendingMachineState = VendingMachineState.NO_COIN;
        return new OutputMachine(clientMoney);
    }

    /**
     * Access to select a product and interact with the sate machine that the vending machine will go through, in order to provide the product
     *
     * @param item
     * @return
     */
    public OutputMachine selectProduct(Item item) {
        return runStateMachineProcesses(new OutputMachine(item));
    }

    private OutputMachine runStateMachineProcesses(OutputMachine outputMachine) {
        try {
            switch (vendingMachineState) {
                case OFF:
                    return outputMachine;
                case NO_COIN:
                    throw new NoEnoughMoneyException(String.format("No enough money %s", outputMachine.getItem().getPrice()));
                case AVAILABLE_MONEY:
                    processAvailableProduct(outputMachine);
                    break;
                case INSERTED_MONEY:
                    processMoney(outputMachine);
                    break;
                case AVAILABLE_ITEMS:
                    processSelectedProduct(outputMachine);
                    break;
                case PRODUCT_SELECTED:
                    resetVendingStateMachine();
                    break;
            }
        } catch (Exception e) {
            outputMachine.setScreenMessage(e.getMessage());
        }
        return outputMachine;
    }

    /**
     * This method will check if it still stock of the item
     *
     * @param outputMachine
     * @throws NoProductAvailableException
     */
    private void processAvailableProduct(OutputMachine outputMachine) throws NoProductAvailableException {
        checkAvailableProduct(getItem(outputMachine));
        vendingMachineState = VendingMachineState.INSERTED_MONEY;
        runStateMachineProcesses(outputMachine);
    }

    /**
     * Return the item type that the client selected
     *
     * @param outputMachine
     * @return
     * @throws NoProductAvailableException
     */
    private Item getItem(final OutputMachine outputMachine) throws NoProductAvailableException {
        switch (outputMachine.getItem().getItemType()) {
            case PEPSI:
                return pepsi;
            case KIT_KAT:
                return kitkat;
            case SMINT:
                return smint;
            default:
                throw new NoProductAvailableException("No product available");
        }
    }

    private void checkAvailableProduct(Item item) throws NoProductAvailableException {
        if (!item.isAvailable()) {
            throw new NoProductAvailableException("No product available");
        }
    }

    /**
     * this method will calc if the client money is enough to pay the product and return the change if it is necessary
     *
     * @param outputMachine
     * @throws NoChangeAvailableException
     * @throws NoEnoughMoneyException
     */
    private void processMoney(OutputMachine outputMachine) throws NoChangeAvailableException, NoEnoughMoneyException {
        clientMoney = getTotalClientMoney();
        if (clientMoney.compareTo(outputMachine.getItem().getPrice()) >= 0) {
            loadClientChange(outputMachine);
            vendingMachineState = VendingMachineState.AVAILABLE_ITEMS;
            runStateMachineProcesses(outputMachine);
        } else {
            throw new NoEnoughMoneyException(String.format("No enough money %s", outputMachine.getItem().getPrice()));
        }
    }

    private BigDecimal getTotalClientMoney() {
        return insertedCoins.stream().map(CoinType::getCoin).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private void loadClientChange(OutputMachine outputMachine) throws NoChangeAvailableException {
        clientMoney = clientMoney.subtract(outputMachine.getItem().getPrice(), MathContext.UNLIMITED);
        outputMachine.setChange(clientMoney.compareTo(new BigDecimal("0.0")) == 0 ? new BigDecimal("0.0") : getClientChange());
    }

    /**
     * Recursive method to extract the client change
     *
     * @return
     * @throws NoChangeAvailableException
     */
    private BigDecimal getClientChange() throws NoChangeAvailableException {
        BigDecimal fractionChange = getFractionChange(clientMoney);
        clientMoney = clientMoney.subtract(fractionChange);
        clientChange = clientChange.add(fractionChange);
        return clientMoney.compareTo(new BigDecimal("0")) == 0 ? clientChange : getClientChange();
    }

    /**
     * Here we calc the fraction of money that we need to reduce from the coins machine to give it to the client as change
     *
     * @param clientMoney
     * @return
     * @throws NoChangeAvailableException
     */
    private BigDecimal getFractionChange(final BigDecimal clientMoney) throws NoChangeAvailableException {
        int numberOfCoins;
        if ((numberOfCoins = getNumberOfCoins(clientMoney, CoinType.POUND)) >= 1 && pounds >= numberOfCoins) {
            pounds -= numberOfCoins;
            return new BigDecimal(numberOfCoins).multiply(CoinType.POUND.getCoin());
        } else if ((numberOfCoins = getNumberOfCoins(clientMoney, CoinType.FIFTY_PENCE)) >= 1 && fiftyPence >= numberOfCoins) {
            fiftyPence -= numberOfCoins;
            return new BigDecimal(numberOfCoins).multiply(CoinType.FIFTY_PENCE.getCoin());
        } else if ((numberOfCoins = getNumberOfCoins(clientMoney, CoinType.TWENTY_PENCE)) >= 1 && twentyPence >= numberOfCoins) {
            twentyPence -= numberOfCoins;
            return new BigDecimal(numberOfCoins).multiply(CoinType.TWENTY_PENCE.getCoin());
        } else if ((numberOfCoins = getNumberOfCoins(clientMoney, CoinType.TEN_PENCE)) >= 1 && tenPence >= numberOfCoins) {
            tenPence -= numberOfCoins;
            return new BigDecimal(numberOfCoins).multiply(CoinType.TEN_PENCE.getCoin());
        } else {
            throw new NoChangeAvailableException("No change for this product");
        }
    }

    private int getNumberOfCoins(final BigDecimal clientMoney, CoinType coinType) {
        return clientMoney.divide(coinType.getCoin(), 2, BigDecimal.ROUND_HALF_UP).setScale(0, RoundingMode.DOWN).intValueExact();
    }

    /**
     * This method will set the product as selected by the user, and it will reduce the item amount
     *
     * @param outputMachine
     * @throws NoProductAvailableException
     * @throws NoChangeAvailableException
     */
    private void processSelectedProduct(final OutputMachine outputMachine) throws NoProductAvailableException, NoChangeAvailableException {
        outputMachine.getItem().setSelected(true);
        reduceItemAmount(getItem(outputMachine));
        vendingMachineState = VendingMachineState.PRODUCT_SELECTED;
        runStateMachineProcesses(outputMachine);
    }

    /**
     * This method will set the state machine as no coin, and it will clear the client coin stack
     */
    private void resetVendingStateMachine() {
        insertedCoins.clear();
        vendingMachineState = VendingMachineState.NO_COIN;
    }

    private void reduceItemAmount(Item item) throws NoProductAvailableException {
        item.setAmount(item.getAmount() - 1);
    }

}
