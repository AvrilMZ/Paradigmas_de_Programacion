package org.game;

import org.game.modelo.Direccion;
import org.game.modelo.Posicion;
import org.game.modelo.entidades.*;
import org.game.modelo.terreno.*;
import org.game.niveles.Nivel;
import org.game.niveles.NivelLoader;
import org.game.visualizacion.GestorSonido;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class NivelTest {
    private Nivel nivel;
    private GestorSonido gestorSonido;

    @Before
    public void setUp() {
        gestorSonido = new GestorSonido();
        nivel = new Nivel(gestorSonido);
    }

    // Agregar y obtener bloques.
    @Test
    public void testAgregarYObtenerBloques() {
        Bloque bloque = new BloqueLadrillo(new Posicion(10, 10));
        nivel.agregarBloque(bloque);
        List<Bloque> bloques = nivel.obtenerBloques();
        assertTrue("El bloque debe estar en la lista de bloques", bloques.contains(bloque));
        assertEquals("Debe haber exactamente un bloque", 1, bloques.size());
        nivel.agregarBloque(bloque);
        assertEquals("No debe duplicar bloques", 1, bloques.size());
    }

    // Agregar y obtener tanques.
    @Test
    public void testAgregarYObtenerTanques() {
        Tanque jugador = new TanquePrimerJugador(new Posicion(0,0), gestorSonido);
        nivel.agregarTanque(jugador);
        assertTrue("El tanque debe estar en la lista de tanques", nivel.obtenerTanques().contains(jugador));
        assertEquals("Debe haber exactamente un tanque", 1, nivel.obtenerTanques().size());
    }

    // Agregar y obtener enemigos.
    @Test
    public void testAgregarYObtenerEnemigos() {
        EnemigoBasico enemigo = new EnemigoBasico(new Posicion(5,5), gestorSonido);
        nivel.agregarEnemigo(enemigo);
        assertTrue("El enemigo debe estar en la lista de enemigos", nivel.obtenerTanquesEnemigos().contains(enemigo));
        assertEquals("Debe haber exactamente un enemigo", 1, nivel.obtenerTanquesEnemigos().size());
    }

    // Agregar y obtener balas.
    @Test
    public void testAgregarYObtenerBalas() {
        Tanque jugador = new TanquePrimerJugador(new Posicion(0,0), gestorSonido);
        Bala bala = new Bala(new Posicion(1,1), Direccion.ARRIBA, 1.0, jugador, gestorSonido);
        nivel.agregarBala(bala);
        assertTrue("La bala debe estar en la lista de balas", nivel.obtenerBalas().contains(bala));
        assertEquals("Debe haber exactamente una bala", 1, nivel.obtenerBalas().size());
    }

    // Posiciones iniciales de jugadores.
    @Test
    public void testSetYGetPosInicialJugadores() {
        Posicion p1 = new Posicion(100, 200);
        Posicion p2 = new Posicion(300, 400);
        nivel.setPosInicialJugador1(p1);
        nivel.setPosInicialJugador2(p2);
        assertEquals("La posición inicial del jugador 1 debe coincidir", p1, nivel.obtenerPosInicialJugador1());
        assertEquals("La posición inicial del jugador 2 debe coincidir", p2, nivel.obtenerPosInicialJugador2());
    }

    // Nivel completo.
    @Test
    public void testNivelCompleto() {
        EnemigoBasico enemigo = new EnemigoBasico(new Posicion(5,5), gestorSonido);
        nivel.agregarEnemigo(enemigo);
        assertFalse("El nivel no debe estar completo si hay tanques", nivel.nivelCompleto());
        nivel.obtenerTanques().clear();
        assertTrue("El nivel debe estar completo si no hay tanques", nivel.nivelCompleto());
    }

    // Obtener base.
    @Test
    public void testObtenerBase() {
        BloqueBase base = new BloqueBase(new Posicion(50, 50));
        nivel.agregarBloque(base);
        assertEquals("La base debe ser la agregada", base, nivel.obtenerBase());
    }

    // Carga y validación de un nivel real.
    @Test
    public void testIntegracionNivelLoaderCompleto() {
        NivelLoader loader = new NivelLoader(gestorSonido);
        Nivel nivelCargado = loader.load(1);
        assertNotNull("El nivel cargado no debe ser null", nivelCargado);
        assertNotNull("La lista de bloques no debe ser null", nivelCargado.obtenerBloques());
        assertNotNull("La lista de tanques no debe ser null", nivelCargado.obtenerTanques());
        assertNotNull("La base no debe ser null", nivelCargado.obtenerBase());
        assertTrue("Debe haber al menos un bloque", nivelCargado.obtenerBloques().size() > 0);
        assertTrue("Debe haber al menos un tanque", nivelCargado.obtenerTanques().size() > 0);
        assertNotNull("La posición inicial del jugador 1 debe estar definida", nivelCargado.obtenerPosInicialJugador1());
        assertTrue("La base debe estar en la lista de bloques", nivelCargado.obtenerBloques().contains(nivelCargado.obtenerBase()));
        for (Tanque t: nivelCargado.obtenerTanques()) {
            assertNotNull("El tanque debe tener posición", t.obtenerPosicion());
        }
    }
}
