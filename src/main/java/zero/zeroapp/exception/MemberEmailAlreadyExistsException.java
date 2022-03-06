package zero.zeroapp.exception;

public class MemberEmailAlreadyExistsException extends RuntimeException {
    public MemberEmailAlreadyExistsException(String message){
        super(message);
    }
}
