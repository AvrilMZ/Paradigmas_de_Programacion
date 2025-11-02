package org.game.modelo.terreno;

import org.game.modelo.Posicion;

public class BloqueBosque extends Bloque {
	private static final int VIDA_BLOQUE = -1;

	/**
	 * Crea un bloque de bosque en la posición indicada.
	 * @param posicion Posición del bloque en el tablero
	 */
	public BloqueBosque(Posicion posicion) {
		super(posicion, VIDA_BLOQUE, false, false);
	}

	/**
	 * Devuelve la clave de imagen asociada a este bloque para su visualización.
	 * @return Clave de imagen "BOSQUE"
	 */
	@Override
	public String obtenerClaveImagen() {
		return "BOSQUE";
	}
}
