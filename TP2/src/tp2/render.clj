(ns tp2.render
		(:require [tp2.vm :as vm]
							[tp2.parseo :as parseo]))

(def image-size 256)

(defn- ceil-div
		"Devuelve ceil(a / b) para enteros positivos usando aritmética entera.
		 Si b <= 0 retorna a (evita div 0)."
		[a b]
		(if (pos? b)
				(quot (+ a (dec b)) b)
				a))

(defn eval-pixel
		"Evalúa el color RGB de un pixel.
		 Acepta código como vector de instrucciones precompiladas.
		 Si recibe String valida primero (longitud y comandos) y luego compila."
		[codigo x y t]
		(let [instrucciones (if (vector? codigo)
														codigo
														(-> codigo
																parseo/parseo-codigo
																vm/compile-code))]
				(vm/pixel-color-compiled instrucciones x y t)))

(defn- balanced-chunks
		"Particiona en chunks de tamaño balanceado según la cantidad de cores."
		[total n-cores]
		(let [chunk-size (max 1 (ceil-div total n-cores))]
				(partition-all chunk-size (range total))))

(defn- process-row
		"Calcula el vector de colores para una fila y dada."
		[codigo y t]
		(mapv (fn [x] (eval-pixel codigo x y t)) (range image-size)))

(defn- process-chunk
		"Procesa un chunk de filas."
		[codigo t chunk]
		(mapv (fn [y] (process-row codigo y t)) chunk))

(defn render-frame
		"Renderiza un frame completo."
		[codigo t]
		(let [^Runtime rt (Runtime/getRuntime)
					raw-cores (.availableProcessors rt)
					n-cores (max 1 raw-cores)
					chunks (balanced-chunks image-size n-cores)]
				(vec (apply concat (pmap #(process-chunk codigo t %) chunks)))))