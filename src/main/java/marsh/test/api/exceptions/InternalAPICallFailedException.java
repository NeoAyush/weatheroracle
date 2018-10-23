package marsh.test.api.exceptions;

public class InternalAPICallFailedException extends RuntimeException {

    public InternalAPICallFailedException(final String msg){
        super(msg);
    }
}
