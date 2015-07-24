package impl;

/**
 * Created by pabloperezgarcia on 24/7/15.
 */
public interface VendingMachine {

    boolean isOn();

    void setOn();

    void setOff();

    OutputMachine processClientAction(ClientAction clientAction);
}
