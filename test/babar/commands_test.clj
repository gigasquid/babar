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

(facts "about ="
  (parse "= 1 1") => true
  (parse "= 3 4") => false)

(facts "about >"
  (parse "> 2 1") => true
  (parse "> 3 4") => false)

(facts "about <"
  (parse "< 2 1") => false
  (parse "< 3 4") => true)

(facts "about and"
  (parse "and true true") => true
  (parse "and true false") => false
  (parse "and true true true true") => true
  (parse "and true true false true") => false)

(facts "about or"
  (parse "or true true") => true
  (parse "or true false") => true
  (parse "or false false") => false
  (parse "or true true true true") => true
  (parse "or true true false true") => true)


(facts "about import"
   (parse "import \"clojure.java.io\"")
   (class (parse "file")) => clojure.java.io$file)

(facts "about println"
  (parse "println \"cat\"") => nil
  (parse "println 1 2 3") => nil )

(facts "about get"
  (parse "get {:a 1} :a") => 1)

(facts "about do"
  (parse "do (def s1 1) (def s2 2)") => anything
  (parse "s1") => 1
  (parse "s2") => 2)

(def bird (atom 5))
(facts "about derefering clojure atoms"
  (parse "@bird") => 5)

(facts "about sleep"
  (parse "sleep 5") => anything)

(facts "about first"
  (parse "first [1 2 3 4]") => 1
  (def x [2 3 4]) => anything
  (parse "first x") => 2)