package impl.exceptions;

/**
 * Created with IntelliJ IDEA.
 * User: pabloperezgarcia
 * Date: 14/01/14
 * Time: 17:07
 * To change this template use File | Settings | File Templates.
 */
public class NoEnoughMoneyException extends Exception{

    public NoEnoughMoneyException() {
    }

    public NoEnoughMoneyException(String s) {
        super(s);
    }

    public NoEnoughMoneyException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public NoEnoughMoneyException(Throwable throwable) {
        super(throwable);
    }
}
