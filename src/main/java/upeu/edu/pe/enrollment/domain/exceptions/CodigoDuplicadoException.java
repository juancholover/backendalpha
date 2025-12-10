package upeu.edu.pe.enrollment.domain.exceptions;


public class CodigoDuplicadoException extends RuntimeException {
    
    private final String codigo;
    
    public CodigoDuplicadoException(String codigo) {
        super(String.format("Ya existe una universidad con el código: %s", codigo));
        this.codigo = codigo;
    }
    
    public String getCodigo() {
        return codigo;
    }
}
