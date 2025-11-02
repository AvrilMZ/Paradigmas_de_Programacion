package org.game;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.game.controladores.ControladorJuego;
import org.game.modelo.Juego;
import org.game.visualizacion.GestorImagen;
import org.game.visualizacion.GestorSonido;
import org.game.visualizacion.VistaJuego;
import org.game.visualizacion.VistaMenu;

public class Main extends Application {
    private static final int ANCHO_VENTANA = 800;
    private static final int ALTURA_VENTANA = 600;

    private GestorSonido gestorSonido = new GestorSonido();
    private GestorImagen gestorImagen;
    private VistaMenu vistaMenu;
    private Scene escenaMenu;

    /**
     * Método principal de inicio de la aplicación. Configura la ventana principal,
     * inicializa los gestores de imagen y sonido, y muestra el menú principal.
     * Asocia las acciones del menú para iniciar el juego o salir.
     * @param stagePrimaria la ventana principal de la aplicación
     */
    @Override
    public void start(Stage stagePrimaria) {
        stagePrimaria.setTitle("Yet Another Battle City");
        stagePrimaria.setResizable(false);

        gestorImagen = new GestorImagen();
        vistaMenu = new VistaMenu(gestorImagen);
        escenaMenu = new Scene(vistaMenu, ANCHO_VENTANA, ALTURA_VENTANA);

        Image logo = gestorImagen.obtenerImagen("LOGO");
        if (logo != null) {
            stagePrimaria.getIcons().add(logo);
        } else {
            System.out.println("No se pudo cargar el ícono 'LOGO' desde GestorImagen.");
        }

        gestorSonido.reproducir("MUSICA_FONDO");

        vistaMenu.setOnSeleccion1Jugador(() -> iniciarJuego(stagePrimaria, 1));
        vistaMenu.setOnSeleccion2Jugadores(() -> iniciarJuego(stagePrimaria, 2));
        vistaMenu.setOnSeleccionSalir(stagePrimaria::close);

        stagePrimaria.setScene(escenaMenu);
        stagePrimaria.show();
        vistaMenu.requestFocus();
    }

    /**
     * Inicia una nueva partida del juego con la cantidad de jugadores especificada.
     * Configura la escena del juego, el controlador y el bucle de animación principal.
     * Permite volver al menú al finalizar la partida y reinicia la música de fondo.
     * @param stagePrimaria la ventana principal de la aplicación
     * @param cantJugadores cantidad de jugadores (1 o 2)
     */
    private void iniciarJuego(Stage stagePrimaria, int cantJugadores) {
        Juego modeloJuego = new Juego(cantJugadores, gestorSonido);
        VistaJuego vistaJuego = new VistaJuego(modeloJuego, gestorImagen);
        vistaJuego.centrarTablero(ANCHO_VENTANA, ALTURA_VENTANA);
        ControladorJuego controladorJuego = new ControladorJuego(modeloJuego);

        Scene escenaJuego = new Scene(vistaJuego, ANCHO_VENTANA, ALTURA_VENTANA);
        escenaJuego.setOnKeyPressed(controladorJuego::handleKeyPressed);
        escenaJuego.setOnKeyReleased(controladorJuego::handleKeyReleased);

        stagePrimaria.setScene(escenaJuego);

        new AnimationTimer() {
            private long ultimaActualizacion = 0;

            @Override
            public void handle(long ahora) {
                if (ultimaActualizacion == 0) {
                    ultimaActualizacion = ahora;
                    return;
                }
                double tiempoDelta = (ahora - ultimaActualizacion) / 1_000_000_000.0;
                ultimaActualizacion = ahora;

                if (vistaJuego.isFinTransicion()) {
                    this.stop();
                    vistaMenu.setOnSeleccion1Jugador(() -> iniciarJuego(stagePrimaria, 1));
                    vistaMenu.setOnSeleccion2Jugadores(() -> iniciarJuego(stagePrimaria, 2));
                    vistaMenu.setOnSeleccionSalir(stagePrimaria::close);
                    stagePrimaria.setScene(escenaMenu);
                    gestorSonido.detener("MUSICA_FONDO");
                    gestorSonido.reproducir("MUSICA_FONDO");
                    vistaJuego.resetTransicionFin();
                    vistaMenu.requestFocus();
                    return;
                }

                controladorJuego.actualizarMovimiento(tiempoDelta);
                modeloJuego.update(tiempoDelta);
                vistaJuego.render();
            }
        }.start();
    }
}