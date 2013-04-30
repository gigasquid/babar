(ns babar.parser-test
  (:require [midje.sweet :refer :all]
            [babar.parser :refer :all]))

(facts "about parsing numbers"
  (parse "1") => 1)

(facts "about parsing vectors"
  (parse "1 2 3 4") => 1 2 3 4)