/**
 * Diseñar e implementar un sistema que permita contabilizar las materias aprobadas de un plan de estudios de FIUBA.
 *
 * Cada materia de FIUBA tiene un código, un nombre y una cantidad de créditos, y puede ser obligatoria u optativa.
 *
 * Una carrera está compuesta por un listado de materias (que pueden ser obligatorias u optativas), y una cantidad mínima de créditos.
 * Para terminar la carrera hay que aprobar todas las materias obligatorias y además cumplir la cantidad de créditos.
 *
 * El sistema ofrece sus servicios a múltiples usuarios. Cada usuario puede cursar cero, una o más carreras.
 * El sistema debe permitir al usuario inscribirse a una carrera, marcar materias como aprobadas y consultar el estado de cada una de sus carreras.
 */

public class Materia {
	public final int codigo;
	public final String nombre;
	public final int creditos;
	public final boolean optativa;
	private int nota;

	public Materia(int codigo, String nombre, int creditos, boolean optativa) {
		this.codigo = codigo;
		this.nombre = nombre;
		this.creditos = creditos;
		this.optativa = optativa;
		this.nota = -1;
	}

	public void aprobar(int notaFinal) {
		this.nota = notaFinal;
	}

	public boolean aprobada() {
		return this.nota >= 4;
	}

	public int getNota() {
		return nota;
	}
}

public class Carrera {
	public final String nombre;
	public final Materia[] materias;
	public final int creditosSolicitados;

	public Carrera(String nombre, Materia[] materias, int creditosSolicitados) {
		this.nombre = nombre;
		this.materias = materias;
		this.creditosSolicitados = creditosSolicitados;
	}
}

public class Alumno {
	public final int padron;
	public final Carrera[] carreras;
	public final int MAX_CARRERAS = 10;
	public final int MAX_MATERIAS = 100;
	private Materia[][] materiasAprobadasPorCarrera;
	private int[] cantidadMateriasPorCarrera;
	private int cantidadCarreras;

	public Alumno(int padron) {
		this.padron = padron;
		this.carreras = new Carrera[MAX_CARRERAS];
		this.materiasAprobadasPorCarrera = new Materia[MAX_CARRERAS][MAX_MATERIAS];
		this.cantidadMateriasPorCarrera = new int[MAX_CARRERAS];
		this.cantidadCarreras = 0;
	}

	private int indiceCarrera(Carrera carrera) {
		for (int i = 0; i < cantidadCarreras; i++) {
			if (carreras[i] == carrera) return i;
		}
		return -1;
	}

	public void inscribirse(Carrera carrera) {
		if (cantidadCarreras < MAX_CARRERAS) {
			carreras[cantidadCarreras] = carrera;
			cantidadMateriasPorCarrera[cantidadCarreras] = 0;
			cantidadCarreras++;
		}
	}

	public void aprobarMateria(Carrera carrera, Materia materia, int nota) {
		if (nota < 4) {
			return;
		}
		materia.aprobar(nota);
		int idx = indiceCarrera(carrera);
		if (idx >= 0) {
			boolean yaAprobada = false;
			for (int j = 0; j < cantidadMateriasPorCarrera[idx]; j++) {
				if (materiasAprobadasPorCarrera[idx][j] == materia) {
					yaAprobada = true;
				}
			}
			if (!yaAprobada && cantidadMateriasPorCarrera[idx] < MAX_MATERIAS) {
				materiasAprobadasPorCarrera[idx][cantidadMateriasPorCarrera[idx]] = materia;
				cantidadMateriasPorCarrera[idx]++;
			}
		}
	}

	public boolean carreraFinalizada(Carrera carrera) {
		int idx = indiceCarrera(carrera);
		boolean todasObligatorias = true;
		if (idx >= 0) {
			for (int j = 0; j < carrera.materias.length; j++) {
				Materia m = carrera.materias[j];
				boolean aprobada = false;
				for (int k = 0; k < cantidadMateriasPorCarrera[idx]; k++) {
					if (materiasAprobadasPorCarrera[idx][k] == m) {
						aprobada = true;
					}
				}
				if (!m.optativa && !aprobada) {
					todasObligatorias = false;
				}
			}
		} else {
			todasObligatorias = false;
		}

		int creditos = 0;
		if (idx >= 0) {
			for (int k = 0; k < cantidadMateriasPorCarrera[idx]; k++) {
				creditos += materiasAprobadasPorCarrera[idx][k].creditos;
			}
		}

		boolean creditosSuficientes = creditos >= carrera.creditosSolicitados;
		return todasObligatorias && creditosSuficientes;
	}

	public float getPromedio(Carrera carrera) {
		int idx = indiceCarrera(carrera);

		float promedio = 0;
		if (idx >= 0 && cantidadMateriasPorCarrera[idx] > 0) {
			int sumaNotas = 0;
			for (int k = 0; k < cantidadMateriasPorCarrera[idx]; k++) {
				sumaNotas += materiasAprobadasPorCarrera[idx][k].getNota();
			}
			promedio = (float) sumaNotas / cantidadMateriasPorCarrera[idx];
		}

		return promedio;
	}
}