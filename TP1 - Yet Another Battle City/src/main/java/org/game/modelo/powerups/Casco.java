package org.game.modelo.powerups;

import org.game.modelo.Posicion;
import org.game.modelo.TipoPowerUp;

public class Casco extends PowerUp {
    /**
     * Crea un power-up de tipo Casco en la posición indicada.
     * @param pos Posición del power-up en el tablero
     */
    public Casco(Posicion pos) {
        super(pos, TipoPowerUp.CASCO);
    }

    /**
     * Devuelve la clave de imagen asociada para su visualización.
     * @return Clave de imagen "CASCO"
     */
    @Override
    public String obtenerClaveImagen() {
        return "CASCO";
    }
}
