package submarineattack.persistencia;

/**
 * Excepción propia (checked) para errores de lectura/escritura del archivo de
 * récords. Así las capas superiores no dependen de IOException ni de detalles
 * del formato del archivo.
 */
public class PersistenciaException extends Exception {

    public PersistenciaException(String mensaje) {
        super(mensaje);
    }

    public PersistenciaException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
