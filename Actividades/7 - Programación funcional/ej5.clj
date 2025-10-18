; Definir la función qsort que recibe una secuencia y la ordena usando el algoritmo Quicksort.

(defn qsort [secuencia]
				(if (<= (count secuencia) 1)
								secuencia
								(let [pivot (nth secuencia (quot (count secuencia) 2))
											izq (for [x secuencia :when (< x pivot)] x)
											medio (for [x secuencia :when (= x pivot)] x)
											der (for [x secuencia :when (> x pivot)] x)]
											(concat (qsort izq) medio (qsort der)))))