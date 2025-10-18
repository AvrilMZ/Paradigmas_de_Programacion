; Definir la función aprox-pi que reciba la cantidad de términos a considerar entre los paréntesis de la expresión 4 · (1 - 1/3 + 1/5 - 1/7 + ... + 1/n) y devuelva la correspondiente aproximación de π.

(defn rec-aprox-pi [i n-term suma]
				(if (< i n-term)
								(let [denominador (+ (* 2 i) 1)
											termino (/ (if (even? i) 1 -1) denominador)]
												(rec-aprox-pi (inc i) n-term (+ suma termino))) ; llamada recursiva
								(* 4 suma)))

(defn aprox-pi [n-term]
				(rec-aprox-pi 0 n-term 0.0))