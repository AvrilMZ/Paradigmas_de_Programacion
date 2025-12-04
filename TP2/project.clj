(defproject tp2 "0.1.0-SNAPSHOT"
		:description "Generador de animaciones..."
		:license {:name "EPL-2.0 OR GPL-2.0-or-later..."
							:url  "https://www.eclipse.org/legal/epl-2.0/"}
		:dependencies [[org.clojure/clojure "1.12.3"]]
		:main ^:skip-aot tp2.core
		:target-path "target/%s"
		:profiles {:uberjar {:aot      :all
												 :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}})