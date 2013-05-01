(ns babar.parser-test
  (:require [midje.sweet :refer :all]
            [babar.parser :refer :all]))

(facts "about parsing numbers"
  (parse "1") => 1)

(facts "about parsing vectors"
  (parse "1 2")
  (parse "1 2 3 4") => [1 2 3 4]
  (parse "1 2 3 4 5") => [1 2 3 4 5])

(facts "about operations"
  (parse "+ 1 2") => 3
  (parse "+ 1 2 3 4 5") => 15
  (parse "- 5 3") => 2
  (parse "- 5 3 1") => 1
  (parse "* 2 3") => 6
  (parse "* 2 3 2") => 12
  (parse "/ 4 2") => 2
  (parse "/ 8 2 2") => 2)