package impl.service.impl;

import impl.exceptions.NoChangeAvailableException;
import impl.exceptions.NoEnoughMoneyException;
import impl.exceptions.NoProductAvailableException;
import impl.model.*;
import impl.service.VendingMachineService;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

/**
 * Created by pabloperezgarcia on 23/7/15.
 */
public class VendingMachineServiceImpl implements VendingMachineService {

    private VendingMachine vendingMachine;

    /**
     * Init vending machine when the machine was provide
     */
    public VendingMachineServiceImpl() {
        vendingMachine = new VendingMachine();
    }

    @Override
    public boolean isOn() {
        return vendingMachine.getPowerMachine();
    }

    @Override
    public void setOn() {
        vendingMachine.setPowerMachine(true);
        vendingMachine.setVendingMachineState(VendingMachineState.NO_COIN);
    }

    @Override
    public void setOff() {
        vendingMachine.setPowerMachine(false);
        vendingMachine.setVendingMachineState(VendingMachineState.OFF);
    }

    /**
     * Main point access  where the user pick up one of the vending machine actions
     *
     * @param clientAction
     * @return
     */
    @Override
    public OutputMachine processClientAction(ClientAction clientAction) {
        switch (clientAction) {
            case INSERT_MONEY:
                return insertMoney(clientAction.getCoinType());
            case COIN_RETURN:
                return returnMoney();
            case GET_ITEM:
                return selectProduct(new OutputMachine(clientAction.getItem()));
            default:
                return new OutputMachine("Action not available");
        }
    }

    /**
     * Access to increase the machine coins and client money on the machine
     *
     * @param coinType
     * @return
     */
    private OutputMachine insertMoney(CoinType coinType) {
        switch (coinType) {
            case TEN_PENCE:
                vendingMachine.tenPence++;
                break;
            case TWENTY_POUND:
                vendingMachine.twentyPence++;
                break;
            case FIFTY_POUND:
                vendingMachine.fiftyPence++;
                break;
            case POUND:
                vendingMachine.pounds++;
                break;
            default:
                return new OutputMachine("No coin accepted", coinType.getCoin());
        }
        vendingMachine.getInsertedCoins().push(coinType);
        vendingMachine.setVendingMachineState(VendingMachineState.AVAILABLE_MONEY);
        return new OutputMachine(String.format("Inserted money %s", getTotalClientMoney()));
    }

    /**
     * We iterate over the coins that the user introduce in the machine to restore the machine coins
     *
     * @return
     */
    private OutputMachine returnMoney() {
        vendingMachine.clientMoney = getTotalClientMoney();
        while (vendingMachine.getInsertedCoins().size() > 0) {
            CoinType insertedCoin = vendingMachine.getInsertedCoins().pop();
            switch (insertedCoin) {
                case TEN_PENCE:
                    vendingMachine.tenPence--;
                    break;
                case TWENTY_POUND:
                    vendingMachine.twentyPence--;
                    break;
                case FIFTY_POUND:
                    vendingMachine.fiftyPence--;
                    break;
                case POUND:
                    vendingMachine.pounds--;
                    break;
            }
        }
        vendingMachine.setVendingMachineState(VendingMachineState.NO_COIN);
        return new OutputMachine(vendingMachine.clientMoney);
    }

    /**
     * Access to select a product and interact with the sate machine that the vending machine will go through, in order to provide the product
     *
     * @param outputMachine
     * @return
     */
    private OutputMachine selectProduct(OutputMachine outputMachine) {
        try {
            switch (vendingMachine.getVendingMachineState()) {
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
        vendingMachine.setVendingMachineState(VendingMachineState.INSERTED_MONEY);
        selectProduct(outputMachine);
    }

    /**
     * Return the item type that the client selected
     * @param outputMachine
     * @return
     * @throws NoProductAvailableException
     */
    private Item getItem(final OutputMachine outputMachine) throws NoProductAvailableException {
        switch (outputMachine.getItem().getItemType()) {
            case PEPSI:
                return vendingMachine.getPepsi();
            case KIT_KAT:
                return vendingMachine.getKitkat();
            case SMINT:
                return vendingMachine.getSmint();
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
        vendingMachine.clientMoney = getTotalClientMoney();
        if (vendingMachine.clientMoney.compareTo(outputMachine.getItem().getPrice()) >= 0) {
            loadClientChange(outputMachine);
            vendingMachine.setVendingMachineState(VendingMachineState.AVAILABLE_ITEMS);
            selectProduct(outputMachine);
        } else {
            throw new NoEnoughMoneyException(String.format("No enough money %s", outputMachine.getItem().getPrice()));
        }
    }

    private BigDecimal getTotalClientMoney() {
        return vendingMachine.getInsertedCoins().stream().map(CoinType::getCoin).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private void loadClientChange(OutputMachine outputMachine) throws NoChangeAvailableException {
        vendingMachine.clientMoney = vendingMachine.clientMoney.subtract(outputMachine.getItem().getPrice(), MathContext.UNLIMITED);
        outputMachine.setChange(vendingMachine.clientMoney.compareTo(new BigDecimal("0.0")) == 0 ? new BigDecimal("0.0") : getClientChange());
    }

    /**
     * Recursive method to extract the client change
     * @return
     * @throws NoChangeAvailableException
     */
    private BigDecimal getClientChange() throws NoChangeAvailableException {
        BigDecimal fractionChange = getFractionChange(vendingMachine.clientMoney);
        vendingMachine.clientMoney = vendingMachine.clientMoney.subtract(fractionChange);
        vendingMachine.clientChange = vendingMachine.clientChange.add(fractionChange);
        return vendingMachine.clientMoney.compareTo(new BigDecimal("0")) == 0 ? vendingMachine.clientChange : getClientChange();
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
        if ((numberOfCoins = getNumberOfCoins(clientMoney, CoinType.POUND)) >= 1 && vendingMachine.pounds >= numberOfCoins) {
            vendingMachine.pounds -= numberOfCoins;
            return new BigDecimal(numberOfCoins).multiply(CoinType.POUND.getCoin());
        } else if ((numberOfCoins = getNumberOfCoins(clientMoney, CoinType.FIFTY_POUND)) >= 1 && vendingMachine.fiftyPence >= numberOfCoins) {
            vendingMachine.fiftyPence -= numberOfCoins;
            return new BigDecimal(numberOfCoins).multiply(CoinType.FIFTY_POUND.getCoin());
        } else if ((numberOfCoins = getNumberOfCoins(clientMoney, CoinType.TWENTY_POUND)) >= 1 && vendingMachine.twentyPence >= numberOfCoins) {
            vendingMachine.twentyPence -= numberOfCoins;
            return new BigDecimal(numberOfCoins).multiply(CoinType.TWENTY_POUND.getCoin());
        } else if ((numberOfCoins = getNumberOfCoins(clientMoney, CoinType.TEN_PENCE)) >= 1 && vendingMachine.tenPence >= numberOfCoins) {
            vendingMachine.tenPence -= numberOfCoins;
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
        vendingMachine.setVendingMachineState(VendingMachineState.PRODUCT_SELECTED);
        selectProduct(outputMachine);
    }

    /**
     * This method will set the state machine as no coin, and it will clear the client coin stack
     */
    private void resetVendingStateMachine() {
        vendingMachine.getInsertedCoins().clear();
        vendingMachine.setVendingMachineState(VendingMachineState.NO_COIN);
    }

    private void reduceItemAmount(Item item) throws NoProductAvailableException {
        item.setAmount(item.getAmount() - 1);
    }

}
