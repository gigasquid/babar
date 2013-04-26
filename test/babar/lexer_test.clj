(ns babar.lexer-test
  (:require [clojure.test :refer :all]
            [midje.sweet :refer :all]
            [babar.lexer :refer :all]))


(facts "about number"
  (tokenize "1") =>  [[:NUMBER 1]]
  (tokenize "2") =>  [[:NUMBER 2]]
  (tokenize "32.2") =>  [[:NUMBER 32.2]])

(facts "about operators"
  (tokenize "+") => [[:OPERATOR "+"]])

(facts "simple math"
  (tokenize "+ 1 2 ") => [ [:OPERATOR "+"] [:NUMBER 1] [:NUMBER 2] ])