package org.game.modelo.entidades;

import org.game.modelo.Posicion;
import org.game.visualizacion.GestorSonido;

public class TanqueJugador extends Tanque {
    private final int idJugador;
    private final String skin0;
    private final String skin1;

    /**
     * Crea el tanque de jugador con parámetros personalizados.
     * @param idJugador Identificador del jugador (1 o 2)
     * @param posicionInicial Posición inicial del tanque
     * @param gestorSonido Gestor de sonidos para efectos del tanque
     * @param velocidad Velocidad de movimiento
     * @param salud Salud inicial
     * @param tiempoPorDisparo Tiempo mínimo entre disparos
     * @param skin0 Clave de imagen principal
     * @param skin1 Clave de imagen alternativa
     */
    public TanqueJugador(int idJugador, Posicion posicionInicial, GestorSonido gestorSonido,
                        double velocidad, int salud, int tiempoPorDisparo,
                        String skin0, String skin1) {
        super(posicionInicial, velocidad, salud, tiempoPorDisparo, gestorSonido);
        this.idJugador = idJugador;
        this.skin0 = skin0;
        this.skin1 = skin1;
    }

    /**
     * Actualiza el estado del tanque del jugador.
     * @param tiempoDelta Tiempo transcurrido desde la última actualización (en segundos)
     */
    @Override
    public void actualizar(double tiempoDelta) {}

    /**
     * Devuelve la clave de imagen asociada a este tanque para su visualización.
     * @return Clave de imagen
     */
    @Override
    public String obtenerClaveImagen() {
        if (seMueve && System.currentTimeMillis() % 200 > 100) {
            return skin1;
        }
        return skin0;
    }

    /**
     * Indica si el tanque es el primer jugador.
     * @return true si es el primer jugador
     */
    @Override
    public boolean esPrimerJugador() {
        return idJugador == 1;
    }

    /**
     * Indica si el tanque es el segundo jugador.
     * @return true si es el segundo jugador
     */
    @Override
    public boolean esSegundoJugador() {
        return idJugador == 2;
    }
}
