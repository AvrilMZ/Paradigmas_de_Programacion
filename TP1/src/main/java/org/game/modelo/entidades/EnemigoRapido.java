package org.game.modelo.entidades;

import org.game.modelo.Posicion;
import org.game.visualizacion.GestorSonido;

public class EnemigoRapido extends TanqueEnemigo {
	private static final double VELOCIDAD = 200.0; // Píxeles por segundo
	private static final int TIEMPO_POR_DISPARO = 2; // Segundos
	private static final int SALUD = 1;

	/**
	 * Crea un enemigo rápido en la posición indicada.
	 * @param posicionInicial Posición inicial del enemigo
	 * @param gestorSonido   Gestor de sonidos para efectos del tanque
	 */
	public EnemigoRapido(Posicion posicionInicial, GestorSonido gestorSonido) {
		super(posicionInicial, VELOCIDAD, SALUD, TIEMPO_POR_DISPARO, gestorSonido);
	}

	/**
	 * Actualiza el estado del enemigo básico.
	 * Llama a la lógica de actualización de TanqueEnemigo.
	 * @param tiempoDelta Tiempo transcurrido desde la última actualización (en segundos)
	 */
	@Override
	public void actualizar(double tiempoDelta) {
		super.actualizar(tiempoDelta);
	}

	/**
	 * Devuelve la clave de imagen asociada a este enemigo para su visualización.
	 * @return Clave de imagen
	 */
	@Override
	public String obtenerClaveImagen() {
		if (seMueve && System.currentTimeMillis() % 200 > 100) {
			return "ENEMIGO_RAPIDO_1";
		}
		return "ENEMIGO_RAPIDO_0";
	}
}
