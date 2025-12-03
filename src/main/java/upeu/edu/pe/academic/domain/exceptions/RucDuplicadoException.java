package upeu.edu.pe.academic.domain.exceptions;


public class RucDuplicadoException extends RuntimeException {
    
    private final String ruc;
    
    public RucDuplicadoException(String ruc) {
        super(String.format("Ya existe una universidad con el RUC: %s", ruc));
        this.ruc = ruc;
    }
    
    public String getRuc() {
        return ruc;
    }
}
