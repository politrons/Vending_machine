package impl.exceptions;

/**
 * Created with IntelliJ IDEA.
 * User: pabloperezgarcia
 * Date: 14/01/14
 * Time: 17:07
 * To change this template use File | Settings | File Templates.
 */
public class NoChangeAvailableException extends Exception{

    public NoChangeAvailableException() {
    }

    public NoChangeAvailableException(String s) {
        super(s);
    }

    public NoChangeAvailableException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public NoChangeAvailableException(Throwable throwable) {
        super(throwable);
    }
}
