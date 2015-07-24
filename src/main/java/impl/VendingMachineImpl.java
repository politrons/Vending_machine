package impl;

import impl.exceptions.NoChangeAvailableException;
import impl.exceptions.NoEnoughMoneyException;
import impl.exceptions.NoProductAvailableException;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pabloperezgarcia on 23/7/15.
 */
public class VendingMachineImpl implements VendingMachine {

    @Getter
    @Setter
    private Pepsi pepsi;

    @Getter
    @Setter
    private KitKat kitkat;

    @Getter
    @Setter
    private Smint smint;

    private List<CoinType> insertedCoins = new ArrayList<>();

    @Getter
    @Setter
    private BigDecimal clientMoney = new BigDecimal("0.0");

    @Getter
    @Setter
    private BigDecimal clientChange = new BigDecimal("0.0");

    @Getter
    @Setter
    private Integer tenPence = 3;

    @Getter
    @Setter
    private Integer twentyPence = 3;

    @Getter
    @Setter
    private Integer fiftyPence = 3;

    @Getter
    @Setter
    private Integer pounds = 3;

    private Boolean powerMachine = false;

    @Getter
    @Setter
    private VendingMachineState vendingMachineState = VendingMachineState.NO_COIN;

    public VendingMachineImpl() {
        //Init items and money when the machine was provide
        this.pepsi = new Pepsi(1);
        this.kitkat = new KitKat(1);
        this.smint = new Smint(1);
    }

    @Override
    public boolean isOn() {
        return powerMachine;
    }

    @Override
    public void setOn() {
        powerMachine = true;
        vendingMachineState = VendingMachineState.NO_COIN;
    }

    @Override
    public void setOff() {
        powerMachine = false;
        vendingMachineState = VendingMachineState.OFF;
    }

    @Override
    public OutputMachine processClientAction(ClientAction clientAction) {
        switch (clientAction) {
            case TUNR_ON:
                setOn();
                return new OutputMachine("welcome");
            case TURN_OFF:
                setOff();
                return new OutputMachine("GoodBye");
            case INSERT_MONEY:
                return insertMoney(clientAction.getCoinType());
            case GET_ITEM:
                return selectProduct(new OutputMachine(clientAction.getItem()));
            case COIN_RETURN:
                return returnClientMoney();
            default:
                return new OutputMachine("Action not available");
        }
    }

    private OutputMachine returnClientMoney() {
        for (CoinType insertedCoin : insertedCoins) {
            switch (insertedCoin) {
                case TEN_PENCE:
                    tenPence--;
                    break;
                case TWENTY_POUND:
                    twentyPence--;
                    break;
                case FIFTY_POUND:
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

    private OutputMachine insertMoney(CoinType coinType) {
        switch (coinType) {
            case TEN_PENCE:
                tenPence++;
                break;
            case TWENTY_POUND:
                twentyPence++;
                break;
            case FIFTY_POUND:
                fiftyPence++;
                break;
            case POUND:
                pounds++;
                break;
            default:
                return new OutputMachine("No coin accepted", coinType.getCoin());
        }
        insertedCoins.add(coinType);
        clientMoney = clientMoney.add(coinType.getCoin(), MathContext.UNLIMITED);
        vendingMachineState = VendingMachineState.AVAILABLE_MONEY;
        return new OutputMachine(String.format("Inserted money %s", clientMoney));
    }


    private OutputMachine selectProduct(OutputMachine outputMachine) {
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


    private void processMoney(OutputMachine outputMachine) throws NoChangeAvailableException, NoEnoughMoneyException {
        if (clientMoney.compareTo(outputMachine.getItem().getPrice()) >= 0) {
            loadClientChange(outputMachine);
            vendingMachineState = VendingMachineState.AVAILABLE_ITEMS;
            selectProduct(outputMachine);
        } else {
            throw new NoEnoughMoneyException(String.format("No enough money %s", outputMachine.getItem().getPrice()));
        }
    }

    private void loadClientChange(OutputMachine outputMachine) throws NoChangeAvailableException {
        clientMoney = clientMoney.subtract(outputMachine.getItem().getPrice(), MathContext.UNLIMITED);
        outputMachine.setChange(clientMoney.compareTo(new BigDecimal("0.0")) == 0 ? new BigDecimal("0.0") : getClientChange());
    }

    private BigDecimal getClientChange() throws NoChangeAvailableException {
        BigDecimal extractMoney = getExtractMoney(clientMoney);
        clientMoney = clientMoney.subtract(extractMoney);
        clientChange = clientChange.add(extractMoney);
        return clientMoney.compareTo(new BigDecimal("0")) == 0 ? clientChange : getClientChange();
    }

    private BigDecimal getExtractMoney(final BigDecimal clientMoney) throws NoChangeAvailableException {
        int numberOfCoins;
        BigDecimal extractMoney;
        if ((numberOfCoins = clientMoney.divide(CoinType.POUND.getCoin(), 2, BigDecimal.ROUND_HALF_UP).setScale(0, RoundingMode.DOWN).intValueExact()) >= 1 &&
                pounds >= numberOfCoins) {
            pounds -= numberOfCoins;
            extractMoney = new BigDecimal(numberOfCoins).multiply(CoinType.POUND.getCoin());
        } else if ((numberOfCoins = clientMoney.divide(CoinType.FIFTY_POUND.getCoin(), 2, BigDecimal.ROUND_HALF_UP).setScale(0, RoundingMode.DOWN).intValueExact()) >= 1 &&
                fiftyPence >= numberOfCoins) {
            fiftyPence -= numberOfCoins;
            extractMoney = new BigDecimal(numberOfCoins).multiply(CoinType.FIFTY_POUND.getCoin());
        } else if ((numberOfCoins = clientMoney.divide(CoinType.TWENTY_POUND.getCoin(), 2, BigDecimal.ROUND_HALF_UP).setScale(0, RoundingMode.DOWN).intValueExact()) >= 1 &&
                twentyPence >= numberOfCoins) {
            twentyPence -= numberOfCoins;
            extractMoney = new BigDecimal(numberOfCoins).multiply(CoinType.TWENTY_POUND.getCoin());
        } else if ((numberOfCoins = clientMoney.divide(CoinType.TEN_PENCE.getCoin(), 2, BigDecimal.ROUND_HALF_UP).setScale(0, RoundingMode.DOWN).intValueExact()) >= 1 &&
                tenPence >= numberOfCoins) {
            tenPence -= numberOfCoins;
            extractMoney = new BigDecimal(numberOfCoins).multiply(CoinType.TEN_PENCE.getCoin());
        } else {
            throw new NoChangeAvailableException("No change for this product");
        }
        return extractMoney;
    }


    private void processAvailableProduct(OutputMachine outputMachine) throws NoProductAvailableException {
        checkAvailableProduct(getItem(outputMachine));
        vendingMachineState = VendingMachineState.INSERTED_MONEY;
        selectProduct(outputMachine);
    }

    private Item getItem(final OutputMachine outputMachine) throws NoProductAvailableException {
        switch (outputMachine.getItem().getItemType()) {
            case PEPSI:
                return pepsi;
            case KIKAT:
                return kitkat;
            case SMINT:
                return smint;
            default:
                throw new NoProductAvailableException("Error no product available");
        }
    }

    private void checkAvailableProduct(Item item) throws NoProductAvailableException {
        if (!item.isAvailable()) {
            throw new NoProductAvailableException("Error no product available");
        }
    }

    private void processSelectedProduct(final OutputMachine outputMachine) throws NoProductAvailableException, NoChangeAvailableException {
        outputMachine.getItem().setSelected(true);
        reduceItemAmount(getItem(outputMachine));
        vendingMachineState = VendingMachineState.PRODUCT_SELECTED;
        selectProduct(outputMachine);
    }

    private void resetVendingStateMachine() {
        vendingMachineState = VendingMachineState.NO_COIN;
    }

    private void reduceItemAmount(Item item) throws NoProductAvailableException {
        item.setAmount(item.getAmount() - 1);
    }

}
