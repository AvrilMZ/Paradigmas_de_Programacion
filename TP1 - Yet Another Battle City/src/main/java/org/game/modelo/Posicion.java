package org.game.modelo;

public record Posicion(double x, double y) {
    /**
     * Devuelve una nueva posición que resulta de mover esta posición según un vector de dirección y una distancia (velocidad).
     * No modifica la instancia actual, sino que retorna una nueva.
     * @param direccion La dirección del movimiento.
     * @param distancia La distancia a mover.
     * @return Una nueva instancia de Posicion con las coordenadas actualizadas.
     */
    public Posicion mover(Direccion direccion, double distancia) {
        double nuevaX = this.x + direccion.obtenerDx() * distancia;
        double nuevaY = this.y + direccion.obtenerDy() * distancia;
        return new Posicion(nuevaX, nuevaY);
    }
}
