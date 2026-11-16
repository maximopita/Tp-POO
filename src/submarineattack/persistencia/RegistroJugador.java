package submarineattack.persistencia;

import submarineattack.modelo.Nivel;

/**
 * Datos persistentes de un jugador: su nombre, su récord de puntos y hasta qué
 * nivel tiene desbloqueado (el nivel 1 siempre está desbloqueado).
 */
public class RegistroJugador {

    private final String nombre;
    private int record;
    private int nivelDesbloqueado;

    public RegistroJugador(String nombre) {
        this(nombre, 0, 1);
    }

    public RegistroJugador(String nombre, int record, int nivelDesbloqueado) {
        this.nombre = nombre;
        this.record = record;
        this.nivelDesbloqueado = nivelDesbloqueado;
    }

    /** Solo reemplaza el récord si el nuevo puntaje lo supera. */
    public void actualizarRecord(int puntos) {
        if (puntos > record) {
            record = puntos;
        }
    }

    /** Solo avanza el desbloqueo (nunca retrocede) y respeta el máximo de niveles. */
    public void desbloquearNivel(int numeroNivel) {
        int maximo = Nivel.values().length;
        if (numeroNivel > nivelDesbloqueado && numeroNivel <= maximo) {
            nivelDesbloqueado = numeroNivel;
        }
    }

    public String getNombre() {
        return nombre;
    }

    public int getRecord() {
        return record;
    }

    public int getNivelDesbloqueado() {
        return nivelDesbloqueado;
    }
}
