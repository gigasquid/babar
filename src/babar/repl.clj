(ns babar.repl
  (:require [babar.parser :as parser]))


(defn complete-input? [input]
  (let [lbrackets (count (re-find #"\[" input))
        rbrackets (count (re-find #"]" input))
        rparens (count (re-find #"\(" input))
        lparens (count (re-find #"\)" input))
        ]
    (and (= lparens rparens) (= lbrackets rbrackets))))

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
    (if-not (= input "quit")
     (do
       (println (try (parser/parse input)
                     (catch Exception e (str "Sorry: " e " - " (.getMessage e)))))
       (recur))
     (do (println "Bye!")
         (System/exit 0)))))


(defn -main [& args]
  (parser/init)


  (println "Hello Babar!")
  (println "    ____ ")
  (println "   /.   \\_ ")
  (println "  /_  \\_/  \\")
  (println " // \\  ___ ||")
  (println " \\\\  |_| |_|  ")
  (println " ")

  (println "ctl-c or quit to exit")
  (println "===============")
  (flush)
  (repl))

