(ns babar.examples.simple
  (require [babar.parser :as babar]))

(babar/init)
(babar/parser "read \"simple.babar\"")

