(ns babar.parser-test
  (:require [midje.sweet :refer :all]
            [babar.parser :refer :all]))

(fact "about programs that are one expression"
  (parse "+ 1 2") => 3)

(facts "about ignorning newline as whitespace in expression"
  (parse "(+ 1\n 3)") => 4
  (parse "+ 1\n 3") => 4)

(facts "about reading babar files"
  (parse "read \"simple.babar\"") => nil
  (parse "c")  => [:a :b 11])
