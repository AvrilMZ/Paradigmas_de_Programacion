package org.game.modelo.powerups;

import org.game.modelo.Posicion;
import org.game.modelo.TipoPowerUp;

public class Estrella extends PowerUp {
    /**
     * Crea un power-up de tipo Estrella en la posición indicada.
     * @param pos Posición del power-up en el tablero
     */
    public Estrella(Posicion pos) {
        super(pos, TipoPowerUp.ESTRELLA);
    }

    /**
     * Devuelve la clave de imagen asociada para su visualización.
     * @return Clave de imagen "ESTRELLA"
     */
    @Override
    public String obtenerClaveImagen() {
        return "ESTRELLA";
    }
}
