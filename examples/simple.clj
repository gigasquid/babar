(ns babar.examples.simple
  (require [babar.parser :as babar]))

;Usually you would run the code in the babar repl
; but this is an example of doing it outside the repl

(babar/init)
(babar/parse "read \"./examples/simple.babar\"")
(babar/parse "c")
