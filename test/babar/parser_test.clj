(ns babar.parser-test
  (:require [midje.sweet :refer :all]
            [babar.parser :refer :all]))

(facts "about programs that are one expression"
  (parse "+ 1 2") => 3)

(facts "about programs that are more than one expression delimeted by semicolon"
  (parse "def z 2; (+ z 1)") => 3)

(facts "about programs that are more than one expression delimeted by newline"
  (parse "def z 2\n(+ z 1)") => 3)


