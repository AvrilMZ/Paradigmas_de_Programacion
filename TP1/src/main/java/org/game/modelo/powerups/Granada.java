package org.game.modelo.powerups;

import org.game.modelo.Posicion;
import org.game.modelo.TipoPowerUp;

public class Granada extends PowerUp {
    /**
     * Crea un power-up de tipo Granada en la posición indicada.
     * @param pos Posición del power-up en el tablero
     */
    public Granada(Posicion pos) {
        super(pos, TipoPowerUp.GRANADA);
    }

    /**
     * Devuelve la clave de imagen asociada para su visualización.
     * @return Clave de imagen "GRANADA"
     */
    @Override
    public String obtenerClaveImagen() {
        return "GRANADA";
    }
}
