package submarineattack.persistencia;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.TreeMap;

/**
 * Lee y escribe los récords en un archivo .txt, una línea por jugador:
 *
 *     nombre;record;nivelDesbloqueado
 *
 * Usa un TreeMap<String, RegistroJugador> para buscar por nombre sin
 * duplicados y dejar el archivo ordenado alfabéticamente.
 * Todo error de entrada/salida se traduce a PersistenciaException.
 */
public class RepositorioJugadores {

    private static final String SEPARADOR = ";";

    private final Path archivo;

    public RepositorioJugadores(String ruta) {
        this.archivo = Paths.get(ruta);
    }

    public Map<String, RegistroJugador> cargarTodos() throws PersistenciaException {
        Map<String, RegistroJugador> registros = new TreeMap<>();
        if (!Files.exists(archivo)) {
            return registros;   // primera vez: todavía no hay archivo
        }
        try (BufferedReader lector = Files.newBufferedReader(archivo, StandardCharsets.UTF_8)) {
            String linea;
            while ((linea = lector.readLine()) != null) {
                if (linea.trim().isEmpty()) {
                    continue;
                }
                String[] partes = linea.split(SEPARADOR);
                if (partes.length != 3) {
                    throw new PersistenciaException("Línea con formato inválido en " + archivo + ": " + linea);
                }
                RegistroJugador registro = new RegistroJugador(partes[0],
                        Integer.parseInt(partes[1].trim()),
                        Integer.parseInt(partes[2].trim()));
                registros.put(registro.getNombre(), registro);
            }
        } catch (IOException | NumberFormatException e) {
            throw new PersistenciaException("No se pudo leer el archivo de récords (" + archivo + ")", e);
        }
        return registros;
    }

    /** Devuelve el registro del jugador, o uno nuevo si todavía no existe. */
    public RegistroJugador buscar(String nombre) throws PersistenciaException {
        RegistroJugador registro = cargarTodos().get(nombre);
        return (registro != null) ? registro : new RegistroJugador(nombre);
    }

    public void guardar(RegistroJugador registro) throws PersistenciaException {
        Map<String, RegistroJugador> registros = cargarTodos();
        registros.put(registro.getNombre(), registro);
        try (BufferedWriter escritor = Files.newBufferedWriter(archivo, StandardCharsets.UTF_8)) {
            for (RegistroJugador r : registros.values()) {
                escritor.write(r.getNombre() + SEPARADOR + r.getRecord() + SEPARADOR + r.getNivelDesbloqueado());
                escritor.newLine();
            }
        } catch (IOException e) {
            throw new PersistenciaException("No se pudo guardar el archivo de récords (" + archivo + ")", e);
        }
    }
}
