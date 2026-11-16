package submarineattack.modelo;

/**
 * Los 3 niveles del juego. Todos tienen las mismas reglas; lo único que cambia
 * es la velocidad: cada nivel aumenta un 20% la velocidad de movimiento de los
 * barcos y la velocidad de caída de las cargas respecto del nivel anterior.
 *
 * factorVelocidad = 1.2 ^ (numero - 1)  ->  1.00, 1.20, 1.44
 */
public enum Nivel {
    UNO(1),
    DOS(2),
    TRES(3);

    private static final double INCREMENTO_POR_NIVEL = 0.20;

    private final int numero;
    private final double factorVelocidad;

    Nivel(int numero) {
        this.numero = numero;
        this.factorVelocidad = Math.pow(1 + INCREMENTO_POR_NIVEL, numero - 1);
    }

    public int getNumero() {
        return numero;
    }

    public double getFactorVelocidad() {
        return factorVelocidad;
    }

    public boolean esUltimo() {
        return this == values()[values().length - 1];
    }

    /** Devuelve el nivel siguiente. Solo debe llamarse si !esUltimo(). */
    public Nivel siguiente() {
        if (esUltimo()) {
            throw new IllegalStateException("El nivel " + numero + " es el último");
        }
        return values()[ordinal() + 1];
    }

    public static Nivel desdeNumero(int numero) {
        for (Nivel n : values()) {
            if (n.numero == numero) {
                return n;
            }
        }
        throw new IllegalArgumentException("No existe el nivel " + numero);
    }
}
