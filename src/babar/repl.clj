(ns babar.repl
  (:require [babar.parser :as parser]))


(defn complete-input? [input]
  (let [lbrackets (count (re-find #"\[" input))
        rbrackets (count (re-find #"]" input))]
    (= lbrackets rbrackets)))


(defn get-input [input]
  (let [new-input (str input (read-line))]
    (if (complete-input? new-input)
      new-input
      (do
        (print "  ..babar> ")
        (flush)
        (recur (str new-input "\n"))))))

(defn repl []
  (do
    (print "babar> ")
    (flush))
  (let [input (get-input "")]
    (println (parser/parse input))
    (recur)))


(defn -main [& args]
  (println "Hello Babar!")
  (println "===============")
  (flush)
  (repl))


