(ns tp2.core-test
		(:require [clojure.test :refer :all]
							[tp2.core :as core])
		(:import (clojure.lang ExceptionInfo)
						 (java.lang Exception)))

(deftest pruebas-casos-exito
		(are [codigo x y t esperado]
				(= (core/eval-pixel codigo x y t) esperado)
				"" 1 2 3 [0 0 0]
				"X" 1 2 3 [0 0 1]
				"Y" 1 2 3 [0 0 2]
				"T" 1 2 3 [0 0 3]
				"XY" 1 2 3 [0 1 2]
				"XYT" 1 2 3 [1 2 3]
				"XYTXYTXY" 1 2 3 [3 1 2]
				"N1" 1 2 3 [0 0 1]
				"N2" 1 2 3 [0 0 2]
				"N3" 1 2 3 [0 0 3]
				"N4" 1 2 3 [0 0 4]
				"N5" 1 2 3 [0 0 5]
				"N6" 1 2 3 [0 0 6]
				"N7" 1 2 3 [0 0 7]
				"N8" 1 2 3 [0 0 8]
				"N9" 1 2 3 [0 0 9]
				"N8N9" 1 2 3 [0 8 9]
				"N7N8N9" 1 2 3 [7 8 9]
				"N0N1N2N3N4N5N6N7" 1 2 3 [5 6 7]
				"N4N4N4N4***N1-" 1 2 3 [0 0 255]
				"XYD" 1 2 3 [1 2 2]
				"N4N5N6D" 1 2 3 [5 6 6]
				"XYP" 1 2 3 [0 0 1]
				"XYS" 1 2 3 [0 2 1]
				"XYTS" 1 2 3 [1 3 2]
				"XYTR" 1 2 3 [2 3 1]
				"N9XYTR" 1 2 3 [2 3 1]
				"N9XYTRP" 1 2 3 [9 2 3]
				"XY+" 1 2 3 [0 0 3]
				"N1N1+" 1 2 3 [0 0 2]
				"XYT++" 1 2 3 [0 0 6]
				"XY+" 2147483637 10 3 [0 0 2147483647]
				"XY-" 1 2 3 [0 0 -1]
				"XY*" 3 2 1 [0 0 6]
				"XY*" -3 2 1 [0 0 -6]
				"XY*" 3 -2 1 [0 0 -6]
				"XY*" -3 -2 1 [0 0 6]
				"XY/" 4 2 1 [0 0 2]
				"XY/" 4 3 1 [0 0 1]
				"XY/" -4 3 1 [0 0 -1]
				"XY/" 4 -3 1 [0 0 -1]
				"XY/" 4 5 1 [0 0 0]
				"XY%" 4 5 1 [0 0 4]
				"XY%" 7 5 1 [0 0 2]
				"XY%" 7 -5 1 [0 0 2]
				"XY%" -7 5 1 [0 0 3]
				"XY%" -7 -5 1 [0 0 3]
				"XY^" 1 3 2 [0 0 2]
				"XY&" 1 3 2 [0 0 1]
				"XY|" 1 3 2 [0 0 3]
				"X!" 0 2 3 [0 0 1]
				"X!" 1 2 3 [0 0 0]
				"Y!" 1 2 3 [0 0 0]
				"T!" 1 2 3 [0 0 0]
				"X!" -1 2 3 [0 0 0]
				"X!" -2 2 3 [0 0 0]
				"X!!" 2 2 3 [0 0 1]
				"XY=" 1 2 3 [0 0 0]
				"XX=" 1 2 3 [0 0 1]
				"XY<" 1 2 3 [0 0 1]
				"XY>" 1 2 3 [0 0 0]
				"XN0[N1+]" 1 2 3 [0 0 1]
				"XN0N1-[N1+]" 1 2 3 [0 0 1]
				"XN1[N1+]" 1 2 3 [0 0 2]
				"N2N3[N4+]" 1 2 3 [0 0 14]
				"XTX-[N9+]" 1 2 3 [0 0 19]
				"XTX-[N9+]X" 1 2 3 [0 19 1]
				"XX-[N4+]" 1 2 3 [0 0 0]
				"XX-[N4+]X" 1 2 3 [0 0 1]
				"N0N2[N3[N1+]]" 1 2 3 [0 0 6]
				"N0N2[N3[N4[N1+]]]" 1 2 3 [0 0 24]))

(deftest pruebas-casos-error
		(doseq [[codigo x y t]
						[["XYTXYTXYT" 1 2 3]
						 ["NNNNNNNNN" 1 2 3]
						 ["N0N1N2N3N4N5N6N7N8" 1 2 3]
						 ["N0N1N2N3N4N5N6N7ND" 1 2 3]
						 ["P" 1 2 3]
						 ["XYR" 1 2 3]
						 ["XS" 1 2 3]
						 ["X+" 1 2 3]
						 ["TX-[N1+]" 1 2 3]
						 ["N0N1[N1[N1[N1[N1[N1[N1[N1[N1[N1+]]]]]]]]]" 1 2 3]
						 ["XYT+++" 1 2 3]
						 ["XY+" 2147483637 11 3]
						 ["XY-" -2147483638 11 3]
						 ["XY*" 1073741823 4 3]
						 ["XY*" -1073741823 -4 3]
						 ["XY*" -1073741824 4 3]
						 ["XY*" 1073741824 -4 3]
						 ["XY/" 1 0 3]
						 ["XY%" 1 0 3]]]
				(is (thrown? Exception (core/eval-pixel codigo x y t)))))

(deftest codigo-muy-largo-lanza-excepcion
		(let [codigo (apply str (repeat 1025 "X"))]
				(is (thrown? Exception (core/eval-pixel codigo 1 2 3)))))

(deftest rgb-int-valido
		(let [f (deref #'tp2.core/rgb-int)]
				(is (integer? (f 0 0 0)))
				(is (integer? (f 255 255 255)))
				(is (integer? (f 128 64 32)))))

(deftest rgb-int-fuera-de-rango-lanza-excepcion
		(let [f (deref #'tp2.core/rgb-int)]
				(is (thrown? ExceptionInfo (f -1 0 0)))
				(is (thrown? ExceptionInfo (f 0 256 0)))
				(is (thrown? ExceptionInfo (f 0 0 999)))))