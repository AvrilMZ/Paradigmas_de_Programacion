package org.game.modelo.powerups;

import java.awt.Rectangle;
import org.game.modelo.Posicion;
import org.game.modelo.TipoPowerUp;

import static org.game.modelo.terreno.Bloque.ALTURA_BLOQUE;
import static org.game.modelo.terreno.Bloque.ANCHO_BLOQUE;

public abstract class PowerUp {
    private Posicion posicion;
    private TipoPowerUp tipoPU;

    /**
     * Crea un power-up en la posición y tipo indicados.
     * @param pos Posición del power-up en el tablero
     * @param tipo Tipo de power-up
     */
    public PowerUp(Posicion pos, TipoPowerUp tipo) {
        this.posicion = pos;
        this.tipoPU = tipo;
    }

    /**
     * Devuelve la clave de imagen asociada al power-up para su visualización.
     * @return Clave de imagen
     */
    public abstract String obtenerClaveImagen();

    /**
     * Devuelve el tipo de power-up.
     * @return Tipo de power-up
     */
    public TipoPowerUp obtenerTipo() {
        return tipoPU;
    }

    /**
     * Devuelve la posición del power-up en el tablero.
     * @return Posición del power-up
     */
    public Posicion obtenerPosicion() {
        return posicion;
    }

    /**
     * Devuelve el área rectangular ocupada por el power-up para detección de colisiones.
     * @return Rectángulo con la posición y tamaño del power-up
     */
    public Rectangle obtenerArea() {
        return new Rectangle((int)posicion.x(), (int)posicion.y(), ANCHO_BLOQUE, ALTURA_BLOQUE);
    }
}
