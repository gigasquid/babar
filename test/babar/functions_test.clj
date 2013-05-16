(ns babar.functions-test
  (:require [midje.sweet :refer :all]
            [babar.parser :refer :all]))
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

(facts "about anonymous functions"
  ((parse "fn [x] (+ x 1)") 3) => 4
  (parse "((fn [x] + x 1) 3)")  => 4
  (parse "((fn [x y z] + x y z) [1 2 3])") => 6
  (parse "((fn [] [4 5 6]))") => [4 5 6])

