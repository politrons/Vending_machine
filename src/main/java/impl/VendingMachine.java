package impl;

import impl.model.ClientAction;
import impl.model.OutputMachine;

/**
 * Created by pabloperezgarcia on 24/7/15.
 */
public interface VendingMachine {

    boolean isOn();

    OutputMachine setOn();

    OutputMachine setOff();

    OutputMachine processClientAction(ClientAction clientAction);
}
