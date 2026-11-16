package submarineattack.vista;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 * Carga imágenes desde la carpeta "imagenes" (ubicada en la raíz del proyecto,
 * al lado de src). Si una imagen no se puede leer devuelve null y la vista
 * dibuja una figura de reemplazo, para que el juego no se caiga.
 */
public final class Imagenes {

    private static final String CARPETA = "imagenes";

    private Imagenes() { }

    public static BufferedImage cargar(String nombreArchivo) {
        try {
            return ImageIO.read(new File(CARPETA, nombreArchivo));
        } catch (IOException | IllegalArgumentException e) {
            System.err.println("No se pudo cargar la imagen " + nombreArchivo + ": " + e.getMessage());
            return null;
        }
    }
}
