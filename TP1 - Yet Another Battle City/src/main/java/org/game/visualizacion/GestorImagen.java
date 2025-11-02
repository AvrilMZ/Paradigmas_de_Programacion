package org.game.visualizacion;

import javafx.scene.image.Image;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class GestorImagen {
    private final Map<String, Image> imagenes;

    /**
     * Constructor de GestorImagen. Inicializa el mapa de imágenes y carga todos los sprites necesarios.
     */
    public GestorImagen() {
        this.imagenes = new HashMap<>();
        cargarImagenes();
    }

    /**
     * Carga una imagen desde el recurso especificado y la asocia a una clave en el mapa.
     * Si ocurre un error o el recurso no existe, muestra un mensaje de error.
     * @param clave Identificador único para la imagen.
     * @param ruta Ruta del recurso de la imagen dentro del proyecto.
     */
    private void cargarImagen(String clave, String ruta) {
        try {
            InputStream inputStream = GestorImagen.class.getResourceAsStream(ruta);
            if (inputStream == null) {
                System.err.println("No se pudo encontrar el recurso: " + ruta);
                return;
            }
            Image image = new Image(inputStream);
            imagenes.put(clave, image);
        } catch (Exception e) {
            System.err.println("Error al cargar la imagen: " + ruta);
            e.printStackTrace();
        }
    }

    /**
     * Carga todas las imágenes necesarias para el juego y las almacena en el mapa.
     * Asocia cada imagen a una clave identificadora.
     */
    private void cargarImagenes() {
        cargarImagen("ANILLO", "/imagenes/AnilloInvulnerable.png");
        cargarImagen("BASE", "/imagenes/Base.png");
        cargarImagen("ACERO", "/imagenes/BloqueAcero.png");
        cargarImagen("AGUA", "/imagenes/BloqueAgua.png");
        cargarImagen("BLANCO", "/imagenes/BloqueBlanco.png");
        cargarImagen("BOSQUE", "/imagenes/BloqueBosque.png");
        cargarImagen("LADRILLO", "/imagenes/BloqueLadrillo.png");
        cargarImagen("DISPARO", "/imagenes/Disparo.png");
        cargarImagen("ENEMIGO_BASICO_0", "/imagenes/EnemigoBasico0.png");
        cargarImagen("ENEMIGO_BASICO_1", "/imagenes/EnemigoBasico1.png");
        cargarImagen("ENEMIGO_BLINDADO_0", "/imagenes/EnemigoBlindado0.png");
        cargarImagen("ENEMIGO_BLINDADO_1", "/imagenes/EnemigoBlindado1.png");
        cargarImagen("ENEMIGO_POTENTE_0", "/imagenes/EnemigoPotente0.png");
        cargarImagen("ENEMIGO_POTENTE_1", "/imagenes/EnemigoPotente1.png");
        cargarImagen("ENEMIGO_RAPIDO_0", "/imagenes/EnemigoRapido0.png");
        cargarImagen("ENEMIGO_RAPIDO_1", "/imagenes/EnemigoRapido1.png");
        cargarImagen("LOGO", "/imagenes/Logo.png");
        cargarImagen("CASCO", "/imagenes/PowerUp-Casco.png");
        cargarImagen("ESTRELLA", "/imagenes/PowerUp-Estrella.png");
        cargarImagen("GRANADA", "/imagenes/PowerUp-Granada.png");
        cargarImagen("PALA", "/imagenes/PowerUp-Pala.png");
        cargarImagen("TANQUE_DESTRUIDO", "/imagenes/TanqueDestruido.png");
        cargarImagen("TANQUE_PRIMER_JUGADOR_0", "/imagenes/TanquePrimerJugador0.png");
        cargarImagen("TANQUE_PRIMER_JUGADOR_1", "/imagenes/TanquePrimerJugador1.png");
        cargarImagen("TANQUE_SEGUNDO_JUGADOR_0", "/imagenes/TanqueSegundoJugador0.png");
        cargarImagen("TANQUE_SEGUNDO_JUGADOR_1", "/imagenes/TanqueSegundoJugador1.png");
    }

    /**
     * Obtiene una imagen previamente cargada.
     * @param clave El identificador del sprite.
     * @return El objeto Image listo para ser dibujado.
     */
    public Image obtenerImagen(String clave) {
        return imagenes.get(clave);
    }
}
