//TODO: I implement all this inside one of my Spring projects, you will need to change the package to make it works

import impl.*;

import impl.model.*;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import java.math.BigDecimal;


/**
 *
 * @author Joeri Leemans
 */
public class VendingMachineTest {


    @Test
    public void wrongCoinType() {
        VendingMachineImpl machine = new VendingMachineImpl();
        machine.setOn();
        ClientAction clientAction = ClientAction.INSERT_MONEY;
        CoinType coinType = CoinType.OTHER;
        coinType.setCoin(new BigDecimal("3.0"));
        clientAction.setCoinType(coinType);
        OutputMachine outputMachine = machine.processClientAction(clientAction);
        assertTrue(("No coin accepted").equals(outputMachine.getScreenMessage()));
        assertTrue(new BigDecimal("3.0").equals(outputMachine.getChange()));
    }

    @Test
    public void insertCoinsReturnTotal() {
        VendingMachineImpl machine = new VendingMachineImpl();
        machine.setOn();
        ClientAction clientAction = ClientAction.INSERT_MONEY;
        clientAction.setCoinType(CoinType.POUND);
        OutputMachine outputMachine = machine.processClientAction(clientAction);
        assertTrue(("Inserted money 1.0").equals(outputMachine.getScreenMessage()));

        clientAction.setCoinType(CoinType.FIFTY_POUND);
        outputMachine = machine.processClientAction(clientAction);
        assertTrue(("Inserted money 1.50").equals(outputMachine.getScreenMessage()));

        clientAction.setCoinType(CoinType.TEN_PENCE);
        outputMachine = machine.processClientAction(clientAction);
        assertTrue(("Inserted money 1.60").equals(outputMachine.getScreenMessage()));

        clientAction.setCoinType(CoinType.TWENTY_POUND);
        outputMachine = machine.processClientAction(clientAction);
        assertTrue(("Inserted money 1.80").equals(outputMachine.getScreenMessage()));

        clientAction = ClientAction.COIN_RETURN;
        outputMachine = machine.processClientAction(clientAction);
        assertThat(outputMachine.getChange(), is(new BigDecimal("1.80")));

    }

    @Test
    public void insertCoinsReturnTotalAndTryToBuyWithoutSuccess() {
        VendingMachineImpl machine = new VendingMachineImpl();
        machine.setOn();
        ClientAction clientAction = ClientAction.INSERT_MONEY;
        clientAction.setCoinType(CoinType.POUND);
        machine.processClientAction(clientAction);

        clientAction.setCoinType(CoinType.FIFTY_POUND);
        machine.processClientAction(clientAction);

        clientAction.setCoinType(CoinType.TEN_PENCE);
        machine.processClientAction(clientAction);

        clientAction.setCoinType(CoinType.TWENTY_POUND);
        machine.processClientAction(clientAction);

        clientAction = ClientAction.COIN_RETURN;
        machine.processClientAction(clientAction);

        clientAction = ClientAction.GET_ITEM;
        clientAction.setItem(new Smint());
        OutputMachine outputMachine = machine.processClientAction(clientAction);
        assertTrue("No enough money 0.60".equals(outputMachine.getScreenMessage()));

    }


    @Test
    public void buyPepsiWithChangeAndSuccess() {
        VendingMachineImpl machine = new VendingMachineImpl();
        machine.setOn();
        ClientAction clientAction = ClientAction.INSERT_MONEY;
        clientAction.setCoinType(CoinType.POUND);
        machine.processClientAction(clientAction);
        clientAction.setCoinType(CoinType.FIFTY_POUND);
        machine.processClientAction(clientAction);
        clientAction = ClientAction.GET_ITEM;
        clientAction.setItem(new Pepsi());
        OutputMachine outputMachine = machine.processClientAction(clientAction);
        assertThat(outputMachine.getChange(), is(new BigDecimal("0.50")));
        assertTrue(outputMachine.getItem().getSelected());
    }

    @Test
    public void buyKitKatWithChangeAndSuccess() {
        VendingMachineImpl machine = new VendingMachineImpl();
        machine.setOn();
        ClientAction clientAction = ClientAction.INSERT_MONEY;
        clientAction.setCoinType(CoinType.POUND);
        machine.processClientAction(clientAction);
        clientAction.setCoinType(CoinType.POUND);
        machine.processClientAction(clientAction);
        clientAction = ClientAction.GET_ITEM;
        clientAction.setItem(new KitKat());
        OutputMachine outputMachine = machine.processClientAction(clientAction);
        assertThat(outputMachine.getChange(), is(new BigDecimal("0.30")));
        assertTrue(outputMachine.getItem().getSelected());
    }

    @Test
    public void buySmintWithChangeAndSuccess() {
        VendingMachineImpl machine = new VendingMachineImpl();
        machine.setOn();
        ClientAction clientAction = ClientAction.INSERT_MONEY;
        clientAction.setCoinType(CoinType.POUND);
        machine.processClientAction(clientAction);
        clientAction.setCoinType(CoinType.POUND);
        machine.processClientAction(clientAction);
        clientAction = ClientAction.GET_ITEM;
        clientAction.setItem(new Smint());
        OutputMachine outputMachine = machine.processClientAction(clientAction);
        assertThat(outputMachine.getChange(), is(new BigDecimal("1.40")));
        assertTrue(outputMachine.getItem().getSelected());
    }

    @Test
    public void buyPepsiWithoutEnoughMoney() {
        VendingMachineImpl machine = new VendingMachineImpl();
        machine.setOn();
        ClientAction clientAction = ClientAction.INSERT_MONEY;
        clientAction.setCoinType(CoinType.FIFTY_POUND);
        machine.processClientAction(clientAction);
        clientAction = ClientAction.GET_ITEM;
        clientAction.setItem(new Pepsi());
        OutputMachine outputMachine = machine.processClientAction(clientAction);
        assertTrue("No enough money 1.00".equals(outputMachine.getScreenMessage()));
    }

    @Test
    public void buyKitKatWithoutEnoughMoney() {
        VendingMachineImpl machine = new VendingMachineImpl();
        machine.setOn();
        ClientAction clientAction = ClientAction.INSERT_MONEY;
        clientAction.setCoinType(CoinType.POUND);
        machine.processClientAction(clientAction);
        clientAction.setCoinType(CoinType.FIFTY_POUND);
        machine.processClientAction(clientAction);
        clientAction = ClientAction.GET_ITEM;
        clientAction.setItem(new KitKat());
        OutputMachine outputMachine = machine.processClientAction(clientAction);
        assertTrue("No enough money 1.70".equals(outputMachine.getScreenMessage()));
    }


    @Test
    public void buySmintiWithoutEnoughMoney() {
        VendingMachineImpl machine = new VendingMachineImpl();
        machine.setOn();
        ClientAction clientAction = ClientAction.INSERT_MONEY;
        clientAction.setCoinType(CoinType.FIFTY_POUND);
        machine.processClientAction(clientAction);
        clientAction = ClientAction.GET_ITEM;
        clientAction.setItem(new Smint());
        OutputMachine outputMachine = machine.processClientAction(clientAction);
        assertTrue("No enough money 0.60".equals(outputMachine.getScreenMessage()));
    }

    @Test
    public void buyPepsiWithoutEnoughMoneyPutMoreMoneyAndGetPepsi() {
        VendingMachineImpl machine = new VendingMachineImpl();
        machine.setOn();
        ClientAction clientAction = ClientAction.INSERT_MONEY;
        clientAction.setCoinType(CoinType.FIFTY_POUND);
        machine.processClientAction(clientAction);
        clientAction = ClientAction.GET_ITEM;
        clientAction.setItem(new Pepsi());
        OutputMachine outputMachine = machine.processClientAction(clientAction);
        assertTrue("No enough money 1.00".equals(outputMachine.getScreenMessage()));

        clientAction = ClientAction.INSERT_MONEY;
        clientAction.setCoinType(CoinType.FIFTY_POUND);
        machine.processClientAction(clientAction);
        clientAction = ClientAction.GET_ITEM;
        clientAction.setItem(new Pepsi());
        outputMachine = machine.processClientAction(clientAction);
        assertThat(outputMachine.getChange(), is(new BigDecimal("0.0")));
        assertTrue(outputMachine.getItem().getSelected());
    }

    @Test
    public void buyPepsiAndReturnNotPepsiAvailable() {
        VendingMachineImpl machine = new VendingMachineImpl();
        machine.setOn();
        ClientAction clientAction = ClientAction.INSERT_MONEY;
        clientAction.setCoinType(CoinType.POUND);
        machine.processClientAction(clientAction);
        clientAction = ClientAction.GET_ITEM;
        clientAction.setItem(new Pepsi());
        machine.processClientAction(clientAction);
        clientAction = ClientAction.INSERT_MONEY;
        clientAction.setCoinType(CoinType.POUND);
        machine.processClientAction(clientAction);
        clientAction = ClientAction.GET_ITEM;
        clientAction.setItem(new Pepsi());
        OutputMachine outputMachine = machine.processClientAction(clientAction);
        assertTrue("No product available".equals(outputMachine.getScreenMessage()));
        assertFalse(outputMachine.getItem().getSelected());
    }

    @Test
    public void buyKitKatAndReturnNotKitKatAvailable() {
        VendingMachineImpl machine = new VendingMachineImpl();
        machine.setOn();
        ClientAction clientAction = ClientAction.INSERT_MONEY;
        clientAction.setCoinType(CoinType.POUND);
        machine.processClientAction(clientAction);
        clientAction.setCoinType(CoinType.FIFTY_POUND);
        machine.processClientAction(clientAction);
        clientAction.setCoinType(CoinType.TWENTY_POUND);
        machine.processClientAction(clientAction);
        clientAction = ClientAction.GET_ITEM;
        clientAction.setItem(new KitKat());
        machine.processClientAction(clientAction);

        clientAction = ClientAction.INSERT_MONEY;
        clientAction.setCoinType(CoinType.POUND);
        machine.processClientAction(clientAction);
        clientAction.setCoinType(CoinType.FIFTY_POUND);
        machine.processClientAction(clientAction);
        clientAction.setCoinType(CoinType.TWENTY_POUND);
        machine.processClientAction(clientAction);
        clientAction = ClientAction.GET_ITEM;
        clientAction.setItem(new KitKat());
        OutputMachine outputMachine = machine.processClientAction(clientAction);
        assertTrue("No product available".equals(outputMachine.getScreenMessage()));
        assertFalse(outputMachine.getItem().getSelected());
    }

    @Test
    public void buySmintAndReturnNotSmintAvailable() {
        VendingMachineImpl machine = new VendingMachineImpl();
        machine.setOn();
        ClientAction clientAction = ClientAction.INSERT_MONEY;
        clientAction.setCoinType(CoinType.FIFTY_POUND);
        machine.processClientAction(clientAction);
        clientAction.setCoinType(CoinType.TEN_PENCE);
        machine.processClientAction(clientAction);
        clientAction = ClientAction.GET_ITEM;
        clientAction.setItem(new Smint());
        machine.processClientAction(clientAction);
        clientAction = ClientAction.INSERT_MONEY;
        clientAction.setCoinType(CoinType.FIFTY_POUND);
        machine.processClientAction(clientAction);
        clientAction.setCoinType(CoinType.TEN_PENCE);
        machine.processClientAction(clientAction);
        machine.processClientAction(clientAction);
        clientAction = ClientAction.GET_ITEM;
        clientAction.setItem(new Smint());
        OutputMachine outputMachine = machine.processClientAction(clientAction);
        assertTrue("No product available".equals(outputMachine.getScreenMessage()));
        assertFalse(outputMachine.getItem().getSelected());
    }

    @Test
    public void buyPepsiAndReturnNotPepsiAvailableSoBuySmint() {
        VendingMachineImpl machine = new VendingMachineImpl();
        machine.setOn();
        ClientAction clientAction = ClientAction.INSERT_MONEY;
        clientAction.setCoinType(CoinType.POUND);
        machine.processClientAction(clientAction);
        clientAction = ClientAction.GET_ITEM;
        clientAction.setItem(new Pepsi());
        machine.processClientAction(clientAction);
        clientAction = ClientAction.INSERT_MONEY;
        clientAction.setCoinType(CoinType.POUND);
        machine.processClientAction(clientAction);
        clientAction = ClientAction.GET_ITEM;
        clientAction.setItem(new Pepsi());
        OutputMachine outputMachine = machine.processClientAction(clientAction);
        assertTrue("No product available".equals(outputMachine.getScreenMessage()));
        clientAction.setItem(new Smint());
        outputMachine = machine.processClientAction(clientAction);
        assertThat(outputMachine.getChange(), is(new BigDecimal("0.40")));
        assertTrue(outputMachine.getItem().getSelected());
    }

    @Test
    public void defaultStateIsOff() {
        VendingMachineImpl machine = new VendingMachineImpl();
        assertFalse(machine.isOn());
    }

    @Test
    public void turnsOn() {
        VendingMachineImpl machine = new VendingMachineImpl();
        machine.setOn();
        assertTrue(machine.isOn());
    }


}
