(ns babar.commands-test
  (:require [midje.sweet :refer :all]
            [babar.parser :refer :all]))

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

(facts "about if"
  (parse "if true 3 4") => 3
  (parse "if dog1 5 2") => 2
  (parse "[ (if true 3 2) 5 6 ]") => [3 5 6]
  (parse "if true (if true 4 3) 6") => 4
  (against-background (before :facts (parse "def dog1 false"))))

