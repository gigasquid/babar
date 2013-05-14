(ns babar.parser-test
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
  (parse "+ 5.2 -2.6 1.2") => 3.8
  (parse "* 2 5") => 10
  (parse "- 10 5") => 5
  (parse "/ 10 5") => 2)

(declare dog1)
(facts "about def"
  dog1  => 16
  (parse "dog1") => 16
  (parse "1 3 4 dog1") => [1 3 4 16]
  (parse "+ 1 dog1") => 17
  (against-background (before :facts (parse "def dog1 16"))))

(declare dog2)
(facts "about defn and functions"
  (dog2 2)  => 8
  (parse "(dog2 2)") => 8
  (parse "dog2: 2") => 8
  (against-background (before :facts (parse "defn dog2 [x] + x 1 2 3"))))

(declare dog3)
(facts "about functions with many arguments"
  (dog3 3 4 5)  => 12
  (parse "(dog3 3 4 5)") => 12
  (parse "dog3: 3 4 5") => 12
  (against-background (before :facts (parse "defn dog3 [x y z] + x y z"))))

(declare dog4)
(facts "about functions with no arguments"
  (dog4)  => [3 4 4]
  (parse "(dog4)") => [3 4 4]
  (parse "dog4:") => [3 4 4]
  (against-background (before :facts (parse "defn dog4 [] [3 4 4]"))))


(facts "about if"
  (parse "if true 3 4") => 3
  (parse "if dog1 5 2") => 2
  (parse "[ (if true 3 2) 5 6 ]") => [3 5 6]
  (parse "if true (if true 4 3) 6") => 4
  (against-background (before :facts (parse "def dog1 false"))))

