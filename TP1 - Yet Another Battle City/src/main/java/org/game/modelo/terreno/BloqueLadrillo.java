package org.game.modelo.terreno;

import org.game.modelo.Posicion;
import org.game.visualizacion.GestorSonido;

public class BloqueLadrillo extends Bloque {
    private static final int VIDA_BLOQUE = 3;

    /**
     * Crea un bloque de ladrillo en la posición indicada.
     * @param posicion Posición del bloque en el tablero
     */
    public BloqueLadrillo(Posicion posicion) {
        super(posicion, VIDA_BLOQUE, true, true);
    }

    /**
     * Devuelve la clave de imagen asociada a este bloque para su visualización.
     * @return Clave de imagen "LADRILLO"
     */
    @Override
    public String obtenerClaveImagen() {
        return "LADRILLO";
    }

    /**
     * Reproduce el sonido de impacto de ladrillo al ser golpeado por una bala.
     * @param gestorSonido Gestor de sonidos del juego
     */
    @Override
    public void reproducirSonidoImpacto(GestorSonido gestorSonido) {
        gestorSonido.reproducir("IMPACTO_LADRILLO");
    }
}
