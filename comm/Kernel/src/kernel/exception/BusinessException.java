package kernel.exception;

public class BusinessException extends RuntimeException {

    private static final long serialVersionUID = 1877734890673094989L;

    private int sign;

    public BusinessException(String message) {
        this(1, message);
    }

    public BusinessException(int sign, String message) {
        super(message);
        this.sign = sign;
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }

    public BusinessException(Throwable cause) {
        super(cause);
    }

    public int getSign() {
        return sign;
    }

    public void setSign(int sign) {
        this.sign = sign;
    }

}
