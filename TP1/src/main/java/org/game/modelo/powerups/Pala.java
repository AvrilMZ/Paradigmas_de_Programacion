package org.game.modelo.powerups;

import org.game.modelo.Posicion;
import org.game.modelo.TipoPowerUp;

public class Pala extends PowerUp {
    /**
     * Crea un power-up de tipo Pala en la posición indicada.
     * @param pos Posición del power-up en el tablero
     */
    public Pala(Posicion pos) {
        super(pos, TipoPowerUp.PALA);
    }

    /**
     * Devuelve la clave de imagen asociada para su visualización.
     * @return Clave de imagen "PALA"
     */
    @Override
    public String obtenerClaveImagen() {
        return "PALA";
    }
}
