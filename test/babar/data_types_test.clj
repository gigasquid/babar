(ns babar.data-types-test
  (:require [midje.sweet :refer :all]
            [babar.parser :refer :all]))

(facts "about parsing numbers"
  (parse "1") => 1
  (parse "100") => 100
  (parse "1.2") => 1.2
  (parse "100.2") => 100.2
  (parse "-1") => -1
  (parse "-1.3") => -1.3
  (parse "-140.3") => -140.3)

(facts "about parsing strings"
  (parse "\"cat\"") => "cat"
  (parse "\"The cat is nice.\"") => "The cat is nice.")

(facts "about parsing booleans"
  (parse "false") => false
  (parse "true") => true)

(facts "about parsing keywords"
  (parse ":key1") => :key1)

(facts "about parsing vectors"
  (parse "1 2")
  (parse "1 2 3 4") => [1 2 3 4]
  (parse "1 2 3 4 5") => [1 2 3 4 5]
  (parse "1.2 3.4 2.5") => [1.2 3.4 2.5]
  (parse "-5.0 -2 -3.2") => [-5.0 -2 -3.2]
  (parse "[1]") => [1]
  (parse "[]") => []
  (parse "[ 1 ]") => [1]
  (parse "[1 2 3]") => [1 2 3]
  (parse "[\"cat\" \"dog\" \"bird\"]") => ["cat" "dog" "bird"]
  (parse "[1 2.0 -4.5 \"cat\"]") => [1 2.0 -4.5 "cat"]
  (parse "true false true") => [true false true]
  (parse ":key1 :key2 :key3") => [:key1 :key2 :key3]
  (parse "1 1.2 true \"cat\" :key1") => [1 1.2 true "cat" :key1]
  (parse "[1 [2 3]]") => [1 [2 3]]
  (parse "1 2 3 {:cat 1}") => [1 2 3 {:cat 1}]
  (parse  "1 2 \t 3 \n 4") => [1 2 3 4])

(facts "about parsing maps"
  (parse "{:cat 1 :dog 2}") => {:cat 1 :dog 2}
  (parse "{:cat [1 3 4]}") => {:cat [1 3 4]}
  (parse "{:cat {:dog 1}}") => {:cat {:dog 1}})

(facts "about parsing commitments"
  (= babar.parser.Commitment (type (parse "*raise-temp"))) => true
  (against-background  (before :facts
                               (swap! commitments merge
                                      {:raise-temp (make-commitment '(+ 1 1) 1 true nil)}))))
