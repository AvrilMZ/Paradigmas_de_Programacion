package org.game.modelo.terreno;

import org.game.modelo.Posicion;

public class BloqueAgua extends Bloque {
    private static final int VIDA_BLOQUE = -1;

    /**
     * Crea un bloque de agua en la posición indicada.
     * @param posicion Posición del bloque en el tablero
     */
    public BloqueAgua(Posicion posicion) {
        super(posicion, VIDA_BLOQUE, false, true);
    }

    /**
     * Devuelve la clave de imagen asociada a este bloque para su visualización.
     * @return Clave de imagen "AGUA"
     */
    @Override
    public String obtenerClaveImagen() {
        return "AGUA";
	}
}
