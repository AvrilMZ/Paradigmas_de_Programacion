; Implementar un programa que permita simular el Juego de la vida, mostrando en la consola una generación cada vez que el usuario presiona la tecla Enter.

(ns life.core
		(:gen-class)
		(:require
						[clojure.string :as str]))

(defn- estado-inicial []
				[[0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0]
				 [0 0 0 1 0 0 0 0 0 0 0 0 0 0 0 0 0 0]
				 [0 0 0 0 1 1 0 0 0 0 0 0 0 0 0 0 0 0]
				 [0 0 0 1 1 0 0 0 0 0 0 0 0 0 0 0 0 0]
				 [0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0]
				 [0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0]
				 [0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0]
				 [0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0]
				 [0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0]
				 [0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0]
				 [0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0]])

(defn- celda-viva?
				"Devuelve true o false si la celda está viva o no.
				Si las coordenadas estan fuera de la matriz, devuelve false"
				[estado x y]
				(= 1 (get-in estado [y x] false)))

(defn- cantidad-vecinos-vivos
				"devuelve la cantidad de vecinos vivos"
				[estado x y]
				(apply + (for [[dx dy] [[-1 -1]
																[-1 0]
																[-1 1]
																[0 -1]
																[0 1]
																[1 -1]
																[1 0]
																[1 1]]]
												 (let [x-vecino (+ x dx)
															 y-vecino (+ y dy)]
																 (if (celda-viva? estado x-vecino y-vecino) 1 0)))))

(defn- actualizar-celda
				"devuelve 0 o 1 si la celda pasa a estar muerta o viva"
				[estado x y]
				(let [n-vivos (cantidad-vecinos-vivos estado x y)]
								(if (celda-viva? estado x y)
												(cond
												 (< n-vivos 2) 0
												 (> n-vivos 3) 0
												 :else 1)
												(if (= n-vivos 3) 1 0))))

(defn- siguiente-estado [estado]
				(vec (for [y (range (count estado))]
										 (vec (for [x (range (count (first estado)))]
																	(actualizar-celda estado x y))))))

(defn- mostrar-fila
				"devuelve una cadena compacta mostrando una fila de la matriz"
				[fila]
				(apply str (for [celda fila]
													 (if (zero? celda) \  \#))))

(defn- mostrar-estado
				"devuelve una representación para imprimir en la consola"
				[estado]
				(str/join "\n" (map mostrar-fila estado)))

(defn -main []
				(loop [estado (estado-inicial)]
								(println (mostrar-estado estado))
								(let [s (read-line)]
												(when (not= (first s) \e)
															(recur (siguiente-estado estado)))))
				(println "chau"))