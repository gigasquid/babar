(ns babar.parser-test
  (:require [midje.sweet :refer :all]
            [babar.parser :refer :all]))

(facts "about parsing numbers"
  (parse "1") => 1
  (parse "1.2") => 1.2
  (parse "-1") => -1
  (parse "-1.3") => -1.3)

(facts "about parsing strings"
  (parse "cat") => "cat"
  (parse "The cat is nice.") => "The cat is nice.")

(facts "about parsing vectors"
  (parse "1 2")
  (parse "1 2 3 4") => [1 2 3 4]
  (parse "1 2 3 4 5") => [1 2 3 4 5]
  (parse "1.2 3.4 2.5") => [1.2 3.4 2.5]
  (parse "-5.0 -2 -3.2") => [-5.0 -2 -3.2]
  (parse "[1]") => [1]
  (parse "[ 1 ]") => [1]
  (parse "[1 2 3]") => [1 2 3]
  (parse "[\"cat\" \"dog\" \"bird\"]") => ["cat" "dog" "bird"]
  (parse "[1 2.0 -4.5 \"cat\"]") => [1 2.0 -4.5 "cat"])

(facts "about operations"
  (parse "+ 1 2") => 3
  (parse "+ 1 2 3 4 5") => 15
  (parse "- 5 3") => 2
  (parse "- 5 3 1") => 1
  (parse "* 2 3") => 6
  (parse "* 2 3 2") => 12
  (parse "/ 4 2") => 2
  (parse "/ 8 2 2") => 2
  (parse "+ 1.2 3.4 2.5") => 7.1
  (parse "+ 5.2 -2.6 1.2") => 3.8)