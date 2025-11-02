package org.game.visualizacion;

import javafx.scene.media.AudioClip;
import java.util.HashMap;
import java.util.Map;

public class GestorSonido {
	private final Map<String, AudioClip> sonidos;

	/**
	 * Constructor de GestorSonido. Inicializa el mapa de sonidos y carga todos los clips necesarios.
	 */
	public GestorSonido() {
		sonidos = new HashMap<>();
		cargarSonidos();
	}

	/**
	 * Carga un sonido desde el recurso especificado y lo asocia a una clave en el mapa.
	 * Si ocurre un error o el recurso no existe, muestra un mensaje de error.
	 * @param clave Identificador único para el sonido.
	 * @param ruta Ruta del recurso de sonido dentro del proyecto.
	 */
	private void cargarSonido(String clave, String ruta) {
		try {
			java.net.URL url = getClass().getResource(ruta);
			if (url == null) {
				System.err.println("No se pudo encontrar el recurso: " + ruta);
				return;
			}
			AudioClip clip = new AudioClip(url.toExternalForm());
			sonidos.put(clave, clip);
		} catch (Exception e) {
			System.err.println("Error al cargar el sonido: " + ruta);
			e.printStackTrace();
		}
	}

	/**
	 * Carga todos los sonidos necesarios para el juego y los almacena en el mapa.
	 * Asocia cada sonido a una clave identificadora.
	 */
	private void cargarSonidos() {
		cargarSonido("DESTRUCCION_BASE", "/sonidos/DestruccionBase.mp3");
		cargarSonido("DISPARO", "/sonidos/Disparo.mp3");
		cargarSonido("IMPACTO_LADRILLO", "/sonidos/ImpactoLadrillo.mp3");
		cargarSonido("IMPACTO_TANQUE_BLINDADO", "/sonidos/ImpactoTanqueBlindado.mp3");
		cargarSonido("MUERTE_TANQUE", "/sonidos/MuerteTanque.mp3");
		cargarSonido("MUSICA_FONDO", "/sonidos/MusicaFondo.mp3");
	}

	/**
	 * Reproduce el sonido asociado a la clave indicada. Si es música de fondo, la reproduce en bucle.
	 * @param clave Identificador del sonido a reproducir.
	 */
	public void reproducir(String clave) {
		AudioClip clip = sonidos.get(clave);
		if (clip != null) {
			if ("MUSICA_FONDO".equals(clave)) {
				clip.setCycleCount(AudioClip.INDEFINITE);
			} else {
				clip.setCycleCount(1);
			}
			clip.play();
		}
	}

	/**
	 * Detiene la reproducción del sonido asociado a la clave indicada.
	 * @param clave Identificador del sonido a detener.
	 */
	public void detener(String clave) {
		AudioClip clip = sonidos.get(clave);
		if (clip != null) {
			clip.stop();
		}
	}
}