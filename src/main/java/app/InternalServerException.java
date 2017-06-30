package app;


public class InternalServerException extends Exception {

    private static final long serialVersionUID = 5492744448359310130L;
    
    public InternalServerException(Exception e) {
        super(e);
    }
    
    public InternalServerException(Throwable e, String msg) {
        super(msg, e);
    }
 
}
