package impl.service;

import impl.model.ClientAction;
import impl.model.OutputMachine;

/**
 * Created by pabloperezgarcia on 24/7/15.
 */
public interface VendingMachineService {

    boolean isOn();

    void setOn();

    void setOff();

    OutputMachine processClientAction(ClientAction clientAction);
}
