package org.game.modelo;

public enum Direccion {
    ARRIBA(0, -1),
    ABAJO(0, 1),
    IZQUIERDA(-1, 0),
    DERECHA(1, 0);

    private final int dx;
    private final int dy;

    /**
     * Constructor para el enum.
     * @param dx El cambio en el eje X (-1, 0, o 1)
     * @param dy El cambio en el eje Y (-1, 0, o 1)
     */
    Direccion(int dx, int dy) {
        this.dx = dx;
        this.dy = dy;
    }

    /**
     * @return El componente X del vector de dirección.
     */
    public int obtenerDx() {
        return dx;
    }

    /**
     * @return El componente Y del vector de dirección.
     */
    public int obtenerDy() {
        return dy;
    }
}
