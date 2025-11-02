package org.game.controladores;

import org.game.modelo.Direccion;
import org.game.modelo.Juego;
import javafx.scene.input.KeyEvent;

public class ControladorJuego {
    private final Juego gameModel;
    private Direccion direccionJugador1;
    private Direccion direccionJugador2;

    /**
     * Crea un controlador de juego asociado al modelo de juego dado.
     * @param modeloJuego Instancia del modelo de juego
     */
    public ControladorJuego(Juego modeloJuego) {
        this.gameModel = modeloJuego;
        this.direccionJugador1 = null;
        this.direccionJugador2 = null;
    }

    /**
     * Maneja el evento de presionar una tecla. Actualiza la dirección de movimiento de los jugadores o ejecuta disparos.
     * @param evento Evento de teclado presionado
     */
    public void handleKeyPressed(KeyEvent evento) {
        switch (evento.getCode()) {
            case W:
                direccionJugador1 = Direccion.ARRIBA;
                break;
            case S:
                direccionJugador1 = Direccion.ABAJO;
                break;
            case A:
                direccionJugador1 = Direccion.IZQUIERDA;
                break;
            case D:
                direccionJugador1 = Direccion.DERECHA;
                break;
            case SPACE:
                gameModel.disparoJugador(1, direccionJugador1);
                break;

            case UP:
                direccionJugador2 = Direccion.ARRIBA;
                break;
            case DOWN:
                direccionJugador2 = Direccion.ABAJO;
                break;
            case LEFT:
                direccionJugador2 = Direccion.IZQUIERDA;
                break;
            case RIGHT:
                direccionJugador2 = Direccion.DERECHA;
                break;
            case ENTER:
                gameModel.disparoJugador(2, direccionJugador2);
                break;
        }
    }

    /**
     * Maneja el evento de soltar una tecla. Detiene el movimiento del jugador correspondiente.
     * @param evento Evento de teclado liberado
     */
    public void handleKeyReleased(KeyEvent evento) {
        switch (evento.getCode()) {
            case W: case S: case A: case D:
                direccionJugador1 = null;
                if (gameModel.obtenerJugador1() != null) {
                    gameModel.obtenerJugador1().actualizarMovimiento(false);
                }
                break;
            case UP: case DOWN: case LEFT: case RIGHT:
                direccionJugador2 = null;
                if (gameModel.obtenerJugador2() != null) {
                    gameModel.obtenerJugador2().actualizarMovimiento(false);
                }
                break;
        }
    }

    /**
     * Actualiza el movimiento de los tanques de ambos jugadores según la dirección actual y el tiempo transcurrido.
     * @param tiempoDelta Tiempo transcurrido desde la última actualización (en segundos)
     */
    public void actualizarMovimiento(double tiempoDelta) {
        if (direccionJugador1 != null) {
            gameModel.moverTanque(gameModel.obtenerJugador1(), direccionJugador1, tiempoDelta);
        }
        if (direccionJugador2 != null) {
            gameModel.moverTanque(gameModel.obtenerJugador2(), direccionJugador2, tiempoDelta);
        }
    }
}
