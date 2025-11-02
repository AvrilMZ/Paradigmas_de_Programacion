package org.game.niveles;

import org.game.modelo.Posicion;
import org.game.modelo.entidades.*;
import org.game.modelo.terreno.*;
import org.game.modelo.terreno.Bloque;
import org.game.visualizacion.GestorSonido;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;

public class NivelLoader {
    private final GestorSonido gestorSonido;

    /**
     * Constructor de NivelLoader. Recibe el gestor de sonido para asociarlo a los elementos del nivel.
     * @param gestorSonido gestor de sonidos para reproducir efectos durante el juego
     */
    public NivelLoader(GestorSonido gestorSonido) {
        this.gestorSonido = gestorSonido;
    }

	/**
	 * Obtiene la posición ajustada desde un elemento XML, restando half y evitando valores negativos.
	 * @param e Elemento XML con atributos 'x' e 'y'
	 * @param half Valor a restar para centrar la posición
	 * @return Posición ajustada y no negativa
	 */
	private Posicion obtenerPosicionDesdeXML(Element e, double half) {
		double x = Double.parseDouble(e.getAttribute("x")) - half;
		double y = Double.parseDouble(e.getAttribute("y")) - half;
		if (x < 0) {
			x = 0;
		}
		if (y < 0) {
			y = 0;
		}
		return new Posicion(x, y);
	}

    /**
     * Carga un nivel desde un archivo XML ubicado en la ruta especificada.
     * Parsea jugadores, enemigos y bloques, y los agrega al nivel resultante.
     * @param rutaArchivo número de nivel a cargar
     * @return instancia de Nivel cargada con los datos del archivo, o un nivel vacío si hay error
     */
    public Nivel load(int rutaArchivo) {
        String levelPath = "/niveles/GeneratedLevels/Level" + rutaArchivo + ".xml";
        Nivel level;
        try {
            InputStream inputStream = NivelLoader.class.getResourceAsStream(levelPath);
            if (inputStream == null) {
				throw new RuntimeException("No se pudo encontrar el archivo de nivel: " + levelPath);
			}

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(inputStream);
            doc.getDocumentElement().normalize();

            NodeList levelNodes = doc.getElementsByTagName("level");
            if (levelNodes.getLength() == 0) {
                return new Nivel(gestorSonido);
            }
            level = new Nivel(gestorSonido);

            double half = Bloque.ANCHO_BLOQUE / 2.0;

            // Jugadores
            NodeList playerNodes = doc.getElementsByTagName("player");
            for (int i = 0; i < playerNodes.getLength(); i++) {
                Element p = (Element) playerNodes.item(i);
                String id = p.getAttribute("id");
                Posicion pos = obtenerPosicionDesdeXML(p, half);
                if ("player1".equalsIgnoreCase(id)) {
                    level.setPosInicialJugador1(pos);
                } else if ("player2".equalsIgnoreCase(id)) {
                    level.setPosInicialJugador2(pos);
                }
            }

            // Enemigos
            NodeList enemyNodes = doc.getElementsByTagName("enemy");
            for (int i = 0; i < enemyNodes.getLength(); i++) {
                Element enemyElement = (Element) enemyNodes.item(i);
                String type = enemyElement.getAttribute("type");
                Posicion pos = obtenerPosicionDesdeXML(enemyElement, half);
                TanqueEnemigo enemy = crearTanque(type, pos);
                if (enemy != null) {
                    level.agregarEnemigo(enemy);
                }
            }

            // Bloques
            NodeList staticNodes = doc.getElementsByTagName("staticObject");
            for (int i = 0; i < staticNodes.getLength(); i++) {
                Element element = (Element) staticNodes.item(i);
                String type = element.getAttribute("type");
                Posicion pos = obtenerPosicionDesdeXML(element, half);
                Bloque block = crearBloque(type, pos);
                if (block != null) {
                    level.agregarBloque(block);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new Nivel(gestorSonido);
        }
        return level;
    }

    /**
     * Método fábrica para crear tanques enemigos según su tipo.
     * @param type tipo de enemigo
     * @param position posición inicial del enemigo
     * @return instancia de TanqueEnemigo correspondiente, o null si el tipo es desconocido
     */
    private TanqueEnemigo crearTanque(String type, Posicion position) {
		return switch (type) {
			case "regularEnemy" -> new EnemigoBasico(position, gestorSonido);
			case "heavyEnemy" -> new EnemigoBlindado(position, gestorSonido);
			case "fastEnemy" -> new EnemigoRapido(position, gestorSonido);
			case "powerfulEnemy" -> new EnemigoPotente(position, gestorSonido);
			default -> {
				System.err.println("Tipo de enemigo desconocido: " + type);
				yield null;
			}
		};
    }

    /**
     * Método fábrica para crear bloques según su tipo.
     * @param type tipo de bloque
     * @param position posición del bloque
     * @return instancia de Bloque correspondiente, o null si el tipo es desconocido
     */
    private Bloque crearBloque(String type, Posicion position) {
		return switch (type) {
			case "brickBlock" -> new BloqueLadrillo(position);
			case "waterBlock" -> new BloqueAgua(position);
			case "steelBlock" -> new BloqueAcero(position);
			case "forestBlock" -> new BloqueBosque(position);
			case "baseBlock" -> new BloqueBase(position);
			default -> {
				System.err.println("Tipo de bloque desconocido: " + type);
				yield null;
			}
		};
    }
}