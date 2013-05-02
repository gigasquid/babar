(ns babar.repl
  (:require [babar.parser :as parser]))

(defn repl []
  (do
    (print "babar> ")
    (flush))
  (let [input (read-line)]
    (println (parser/parse input))
    (recur)))


(defn -main [& args]
  (println "Hello Babar!")
  (println "===============")
  (flush)
  (repl))

