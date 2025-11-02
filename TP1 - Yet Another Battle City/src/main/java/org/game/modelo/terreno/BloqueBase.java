package org.game.modelo.terreno;

import org.game.modelo.Posicion;
import org.game.visualizacion.GestorSonido;

public class BloqueBase extends Bloque {
    private static final int VIDA_BLOQUE = 1;

    /**
     * Crea la base en la posición indicada.
     * @param posicion Posición de la base en el tablero
     */
    public BloqueBase(Posicion posicion) {
        super(posicion, VIDA_BLOQUE, true, true);
    }

    /**
     * Devuelve la clave de imagen asociada a la base para su visualización.
     * @return Clave de imagen "BASE"
     */
    @Override
    public String obtenerClaveImagen() {
        return "BASE";
    }

    /**
     * Reproduce el sonido de destrucción de la base.
     * @param gestorSonido Gestor de sonidos del juego
     */
    @Override
    public void reproducirSonidoImpacto(GestorSonido gestorSonido) {
        gestorSonido.reproducir("DESTRUCCION_BASE");
    }

    /**
     * Indica que este bloque es la base del nivel.
     * @return true
     */
    @Override
    public boolean esBase() {
        return true;
    }
}