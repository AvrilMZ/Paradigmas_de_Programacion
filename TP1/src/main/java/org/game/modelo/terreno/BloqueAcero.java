package org.game.modelo.terreno;

import org.game.modelo.Posicion;

public class BloqueAcero extends Bloque {
    private static final int VIDA_BLOQUE = -1;

    /**
     * Crea un bloque de acero en la posición indicada.
     * @param posicion Posición del bloque en el tablero
     */
    public BloqueAcero(Posicion posicion) {
        super(posicion, VIDA_BLOQUE, true, true);
    }

    /**
     * Devuelve la clave de imagen asociada a este bloque para su visualización.
     * @return Clave de imagen "ACERO"
     */
    @Override
    public String obtenerClaveImagen() {
        return "ACERO";
    }
}