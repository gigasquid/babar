(ns babar.repl
  (:require [babar.parser :as parser]))

(defn repl []
  (let [input (read-line)]
    (println (parser/parse input))
    (recur)))


(defn -main [& args]
  (println "Hello Babar!")
  (println "->")
  (flush)
  (repl))

