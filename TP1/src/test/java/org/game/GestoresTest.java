package org.game;

import org.game.visualizacion.GestorImagen;
import org.game.visualizacion.GestorSonido;
import javafx.scene.image.Image;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class GestoresTest {
    private GestorImagen gestorImagen;
    private GestorSonido gestorSonido;

    @Before
    public void setUp() {
        gestorImagen = new GestorImagen();
        gestorSonido = new GestorSonido();
    }

    // Se cargan correctamente imágenes válidas.
    @Test
    public void testGestorImagenCargaImagenesValidas() {
        assertNotNull("La imagen ANILLO debe estar cargada", gestorImagen.obtenerImagen("ANILLO"));
        assertNotNull("La imagen BASE debe estar cargada", gestorImagen.obtenerImagen("BASE"));
        assertNotNull("La imagen TANQUE_PRIMER_JUGADOR_0 debe estar cargada", gestorImagen.obtenerImagen("TANQUE_PRIMER_JUGADOR_0"));
    }

    // 'obtenerImagen' devuelve null para una clave inexistente.
    @Test
    public void testGestorImagenClaveInexistente() {
        assertNull("Debe devolver null para una clave inexistente", gestorImagen.obtenerImagen("NO_EXISTE"));
    }

    // Reproducir y detener un sonido válido no lanza excepción.
    @Test
    public void testGestorSonidoReproducirYDetenerSonidoValido() {
        try {
            gestorSonido.reproducir("DISPARO");
            gestorSonido.detener("DISPARO");
        } catch (Exception e) {
            fail("No debe lanzar excepción al reproducir o detener un sonido válido");
        }
    }

    // Reproducir y detener un sonido con clave inexistente no lanza excepción.
    @Test
    public void testGestorSonidoClaveInexistente() {
        try {
            gestorSonido.reproducir("NO_EXISTE");
            gestorSonido.detener("NO_EXISTE");
        } catch (Exception e) {
            fail("No debe lanzar excepción aunque la clave no exista");
        }
    }

    // Todas las claves válidas devuelven una imagen no nula.
    @Test
    public void testGestorImagenTodasLasClavesCarganImagen() {
        String[] claves = {
            "ANILLO", "BASE", "ACERO", "AGUA", "BLANCO", "BOSQUE", "LADRILLO", "DISPARO",
            "ENEMIGO_BASICO_0", "ENEMIGO_BASICO_1", "ENEMIGO_BLINDADO_0", "ENEMIGO_BLINDADO_1",
            "ENEMIGO_POTENTE_0", "ENEMIGO_POTENTE_1", "ENEMIGO_RAPIDO_0", "ENEMIGO_RAPIDO_1",
            "LOGO", "CASCO", "ESTRELLA", "GRANADA", "PALA", "TANQUE_DESTRUIDO",
            "TANQUE_PRIMER_JUGADOR_0", "TANQUE_PRIMER_JUGADOR_1",
            "TANQUE_SEGUNDO_JUGADOR_0", "TANQUE_SEGUNDO_JUGADOR_1"
        };
        for (String clave: claves) {
            assertNotNull("La imagen '" + clave + "' debe estar cargada", gestorImagen.obtenerImagen(clave));
        }
    }

    // Obtener la misma imagen devuelve la misma instancia.
    @Test
    public void testGestorImagenMismaInstancia() {
        Image img1 = gestorImagen.obtenerImagen("BASE");
        Image img2 = gestorImagen.obtenerImagen("BASE");
		assertSame("Debe devolver la misma instancia para la misma clave", img1, img2);
    }

    // Reproducir y detener sonidos válidos no lanza excepción.
    @Test
    public void testGestorSonidoTodasLasClavesReproducenYDetienen() {
        String[] claves = {
            "DESTRUCCION_BASE", "DISPARO", "IMPACTO_LADRILLO",
            "IMPACTO_TANQUE_BLINDADO", "MUERTE_TANQUE", "MUSICA_FONDO"
        };
        for (String clave : claves) {
            try {
                gestorSonido.reproducir(clave);
                gestorSonido.detener(clave);
            } catch (Exception e) {
                fail("No debe lanzar excepción al reproducir o detener '" + clave + "'");
            }
        }
    }

    // Reproducir varias veces el mismo sonido no lanza excepción.
    @Test
    public void testGestorSonidoReproducirVariasVeces() {
        try {
            gestorSonido.reproducir("DISPARO");
            gestorSonido.reproducir("DISPARO");
        } catch (Exception e) {
            fail("Reproducir varias veces no debe lanzar excepción");
        }
    }

    // Detener un sonido que no está sonando no lanza excepción.
    @Test
    public void testGestorSonidoDetenerSinReproducir() {
        try {
            gestorSonido.detener("DISPARO");
        } catch (Exception e) {
            fail("Detener sin reproducir no debe lanzar excepción");
        }
    }
}
