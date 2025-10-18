; Definir la función nth-fibo que reciba un número entero no negativo y devuelva el correspondiente término de la sucesión de Fibonacci.

(defn nth-fibo [numero]
				(cond ; cond: maneja todos los if que habria
								 (<= numero 0) 0
								 (= numero 1) 1
								 :else (+ (nth-fibo (- numero 1)) (nth-fibo (- numero 2)))))