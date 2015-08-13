import impl.VendingMachine;
import impl.model.*;
import org.junit.Test;

import java.math.BigDecimal;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;


/**
 * @author Pablo Perez Garcia
 */
public class VendingMachineTest {


    @Test
    public void wrongCoinType() {
        VendingMachine machine = new VendingMachine();
        machine.setOn();
        CoinType coinType = CoinType.OTHER;
        coinType.setCoin(new BigDecimal("3.0"));
        OutputMachine outputMachine = machine.insertMoney(coinType);
        assertTrue(("No coin accepted").equals(outputMachine.getScreenMessage()));
        assertTrue(new BigDecimal("3.0").equals(outputMachine.getChange()));
    }

    @Test
    public void insertCoinsReturnTotal() {
        VendingMachine machine = new VendingMachine();
        machine.setOn();
        machine.insertMoney(CoinType.POUND);
        machine.insertMoney(CoinType.FIFTY_PENCE);
        machine.insertMoney(CoinType.TEN_PENCE);
        machine.insertMoney(CoinType.TWENTY_PENCE);
        OutputMachine outputMachine = machine.returnMoney();
        assertThat(outputMachine.getChange(), is(new BigDecimal("1.80")));

    }

    @Test
    public void insertCoinsReturnTotalAndTryToBuyWithoutSuccess() {
        VendingMachine machine = new VendingMachine();
        machine.setOn();
        machine.insertMoney(CoinType.POUND);
        machine.insertMoney(CoinType.FIFTY_PENCE);
        machine.insertMoney(CoinType.TEN_PENCE);
        machine.insertMoney(CoinType.TWENTY_PENCE);
        machine.returnMoney();
        OutputMachine outputMachine = machine.selectProduct(new Smint());
        assertTrue("No enough money 0.60".equals(outputMachine.getScreenMessage()));

    }


    @Test
    public void buyPepsiWithChangeAndSuccess() {
        VendingMachine machine = new VendingMachine();
        machine.setOn();
        machine.insertMoney(CoinType.POUND);
        machine.insertMoney(CoinType.FIFTY_PENCE);
        OutputMachine outputMachine = machine.selectProduct(new Pepsi());
        assertThat(outputMachine.getChange(), is(new BigDecimal("0.50")));
        assertTrue(outputMachine.getItem().getSelected());
    }

    @Test
    public void buyKitKatWithChangeAndSuccess() {
        VendingMachine machine = new VendingMachine();
        machine.setOn();
        machine.insertMoney(CoinType.POUND);
        machine.insertMoney(CoinType.POUND);
        OutputMachine outputMachine = machine.selectProduct(new KitKat());
        assertThat(outputMachine.getChange(), is(new BigDecimal("0.30")));
        assertTrue(outputMachine.getItem().getSelected());
    }

    @Test
    public void buySmintWithChangeAndSuccess() {
        VendingMachine machine = new VendingMachine();
        machine.setOn();
        machine.insertMoney(CoinType.POUND);
        machine.insertMoney(CoinType.POUND);
        OutputMachine outputMachine = machine.selectProduct(new Smint());
        assertThat(outputMachine.getChange(), is(new BigDecimal("1.40")));
        assertTrue(outputMachine.getItem().getSelected());
    }

    @Test
    public void buyPepsiWithoutEnoughMoney() {
        VendingMachine machine = new VendingMachine();
        machine.setOn();
        machine.insertMoney(CoinType.FIFTY_PENCE);
        OutputMachine outputMachine = machine.selectProduct(new Pepsi());
        assertTrue("No enough money 1.00".equals(outputMachine.getScreenMessage()));
    }

    @Test
    public void buyKitKatWithoutEnoughMoney() {
        VendingMachine machine = new VendingMachine();
        machine.setOn();
        machine.insertMoney(CoinType.POUND);
        machine.insertMoney(CoinType.FIFTY_PENCE);
        OutputMachine outputMachine = machine.selectProduct(new KitKat());
        assertTrue("No enough money 1.70".equals(outputMachine.getScreenMessage()));
    }


    @Test
    public void buySmintiWithoutEnoughMoney() {
        VendingMachine machine = new VendingMachine();
        machine.setOn();
        machine.insertMoney(CoinType.FIFTY_PENCE);
        OutputMachine outputMachine = machine.selectProduct(new Smint());
        assertTrue("No enough money 0.60".equals(outputMachine.getScreenMessage()));
    }

    @Test
    public void buyPepsiWithoutEnoughMoneyPutMoreMoneyAndGetPepsi() {
        VendingMachine machine = new VendingMachine();
        machine.setOn();
        machine.insertMoney(CoinType.FIFTY_PENCE);
        machine.selectProduct(new Pepsi());
        machine.insertMoney(CoinType.FIFTY_PENCE);
        OutputMachine outputMachine = machine.selectProduct(new Pepsi());
        assertThat(outputMachine.getChange(), is(new BigDecimal("0.0")));
        assertTrue(outputMachine.getItem().getSelected());
    }

    @Test
    public void buyPepsiAndReturnNotPepsiAvailable() {
        VendingMachine machine = new VendingMachine();
        machine.setOn();
        machine.insertMoney(CoinType.POUND);
        machine.selectProduct(new Pepsi());
        machine.insertMoney(CoinType.POUND);
        OutputMachine outputMachine = machine.selectProduct(new Pepsi());
        assertTrue("No product available".equals(outputMachine.getScreenMessage()));
        assertFalse(outputMachine.getItem().getSelected());
    }

    @Test
    public void buyKitKatAndReturnNotKitKatAvailable() {
        VendingMachine machine = new VendingMachine();
        machine.setOn();
        machine.insertMoney(CoinType.POUND);
        machine.insertMoney(CoinType.FIFTY_PENCE);
        machine.insertMoney(CoinType.TWENTY_PENCE);
        machine.selectProduct(new KitKat());
        machine.insertMoney(CoinType.POUND);
        machine.insertMoney(CoinType.FIFTY_PENCE);
        machine.insertMoney(CoinType.TWENTY_PENCE);
        OutputMachine outputMachine = machine.selectProduct(new KitKat());
        assertTrue("No product available".equals(outputMachine.getScreenMessage()));
        assertFalse(outputMachine.getItem().getSelected());
    }

    @Test
    public void buySmintAndReturnNotSmintAvailable() {
        VendingMachine machine = new VendingMachine();
        machine.setOn();
        machine.insertMoney(CoinType.FIFTY_PENCE);
        machine.insertMoney(CoinType.TEN_PENCE);
        machine.selectProduct(new Smint());
        machine.insertMoney(CoinType.FIFTY_PENCE);
        machine.insertMoney(CoinType.TEN_PENCE);
        OutputMachine outputMachine = machine.selectProduct(new Smint());
        assertTrue("No product available".equals(outputMachine.getScreenMessage()));
        assertFalse(outputMachine.getItem().getSelected());
    }

    @Test
    public void buyPepsiAndReturnNotPepsiAvailableSoBuySmint() {
        VendingMachine machine = new VendingMachine();
        machine.setOn();
        machine.insertMoney(CoinType.POUND);
        machine.selectProduct(new Pepsi());
        machine.insertMoney(CoinType.POUND);
        machine.selectProduct(new Pepsi());
        OutputMachine outputMachine = machine.selectProduct(new Smint());
        assertThat(outputMachine.getChange(), is(new BigDecimal("0.40")));
        assertTrue(outputMachine.getItem().getSelected());
    }

    @Test
    public void defaultStateIsOff() {
        VendingMachine machine = new VendingMachine();
        assertFalse(machine.isOn());
    }

    @Test
    public void turnsOn() {
        VendingMachine machine = new VendingMachine();
        machine.setOn();
        assertTrue(machine.isOn());
    }

    @Test
    public void turnsOff() {
        VendingMachine machine = new VendingMachine();
        machine.setOff();
        assertFalse(machine.isOn());
    }


}
