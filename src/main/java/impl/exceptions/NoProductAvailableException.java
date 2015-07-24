package impl.exceptions;

/**
 * Created with IntelliJ IDEA.
 * User: pabloperezgarcia
 * Date: 14/01/14
 * Time: 17:07
 * To change this template use File | Settings | File Templates.
 */
public class NoProductAvailableException extends Exception{

    public NoProductAvailableException() {
    }

    public NoProductAvailableException(String s) {
        super(s);
    }

    public NoProductAvailableException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public NoProductAvailableException(Throwable throwable) {
        super(throwable);
    }
}
