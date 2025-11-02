package org.game.modelo.terreno;

import org.game.modelo.Posicion;
import org.game.visualizacion.GestorSonido;

import java.awt.*;

public abstract class Bloque {
    public static int ANCHO_BLOQUE = 600 / 13;
    public static int ALTURA_BLOQUE = 600 / 13;
    public static final int ANCHO_TANQUE = (int)(ANCHO_BLOQUE * 0.9);
    public static final int ALTURA_TANQUE = (int)(ALTURA_BLOQUE * 0.9);
    private static final int INDESTRUCTIBLE = -1;

    private final Posicion posicion;
    private int vida;
    private final boolean bloqueaBala;
    private final boolean impidePaso;

    /**
     * Crea un nuevo bloque con la posición, vida y propiedades de colisión indicadas.
     * @param posicion Posición del bloque en el tablero
     * @param vida Cantidad de impactos que resiste
     * @param bloqueaBala Indica si el bloque detiene balas
     * @param impidePaso Indica si el bloque impide el paso de tanques
     */
    public Bloque(Posicion posicion, int vida, boolean bloqueaBala, boolean impidePaso) {
        this.posicion = posicion;
        this.vida = vida;
        this.bloqueaBala = bloqueaBala;
        this.impidePaso = impidePaso;
    }

    /**
     * Devuelve la posición del bloque en el tablero.
     * @return Posición del bloque
     */
    public Posicion obtenerPosicion() {
        return posicion;
    }

    /**
     * Indica si el bloque existe.
     * @return true si el bloque existe, false si fue destruido
     */
    public boolean existe() {
        if (vida == INDESTRUCTIBLE) {
            return true;
        } else {
            return vida > 0;
        }
    }

    /**
     * Procesa el impacto de una bala sobre el bloque, reduciendo su vida si es destructible.
     * @return true si la bala es bloqueada, false si la atraviesa
     */
    public boolean impactoBala() {
        if (vida != INDESTRUCTIBLE) {
            vida--;
        }
        return bloqueaBala;
    }

    /**
     * Indica si el bloque impide el paso de tanques.
     * @return true si impide el paso, false si es transitable
     */
    public boolean impidePaso() {
        return impidePaso;
    }

    /**
     * Devuelve el área rectangular ocupada por el bloque en el tablero.
     * @return Rectángulo con la posición y tamaño del bloque
     */
    public Rectangle obtenerArea() {
        Posicion pos = obtenerPosicion();
        return new Rectangle(
                (int)pos.x(),
                (int)pos.y(),
                getAncho(),
                getAlto()
        );
    }

    /**
     * Devuelve el ancho del bloque en píxeles.
     * @return Ancho del bloque
     */
    public int getAncho() {
        return ANCHO_BLOQUE;
    }

    /**
     * Devuelve el alto del bloque en píxeles.
     * @return Alto del bloque
     */
    public int getAlto() {
        return ALTURA_BLOQUE;
    }

    /**
     * Indica si el bloque es la base del nivel.
     * @return true si es la base, false en caso contrario
     */
    public boolean esBase() {
        return false;
    }

    /**
     * Reproduce el sonido de impacto correspondiente al tipo de bloque.
     * Por defecto no hace nada, pero puede ser sobreescrito por subclases.
     * @param gestorSonido Gestor de sonidos del juego
     */
    public void reproducirSonidoImpacto(GestorSonido gestorSonido) {}

    /**
     * Devuelve la clave de la imagen asociada al bloque para su visualización.
     * @return Clave de imagen
     */
    public abstract String obtenerClaveImagen();
}
