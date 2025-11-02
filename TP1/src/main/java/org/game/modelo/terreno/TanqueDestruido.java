package org.game.modelo.terreno;

import org.game.modelo.Posicion;

public class TanqueDestruido extends Bloque {
    /**
     * Crea un bloque de tanque destruido en la posición indicada.
     * @param posicion Posición donde se muestra el tanque destruido
     */
    public TanqueDestruido(Posicion posicion) {
        super(posicion, -1, true, true);
    }

    /**
     * Devuelve la clave de imagen asociada a este bloque para su visualización.
     * @return Clave de imagen "TANQUE_DESTRUIDO"
     */
    @Override
    public String obtenerClaveImagen() {
        return "TANQUE_DESTRUIDO";
    }

    /**
     * Devuelve el ancho del sprite de tanque destruido.
     * @return Ancho en píxeles
     */
    @Override
    public int getAncho() {
        return ANCHO_TANQUE;
    }

    /**
     * Devuelve el alto del sprite de tanque destruido.
     * @return Alto en píxeles
     */
    @Override
    public int getAlto() {
        return ALTURA_TANQUE;
    }
}
