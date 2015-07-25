package impl.service;

import impl.model.ClientAction;
import impl.model.OutputMachine;
import impl.model.VendingMachine;

/**
 * Created by pabloperezgarcia on 24/7/15.
 */
public interface VendingMachineService {

    boolean isOn();

    default String version() {
        return VendingMachine.version;
    }

    void setOn();

    void setOff();

    OutputMachine processClientAction(ClientAction clientAction);
}
