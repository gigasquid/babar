(ns babar.parser
  (:require [instaparse.core :as insta]))


(def parser
  (insta/parser
   "expr = number | vector | operation
    operation = operator space+ vector
    operator = '+'
    vector = snumber+ number
    <snumber> = (number space)*
    <space> = <#'[ ]+'>
    number = #'[0-9]+'"))

(def transform-options
  {:number read-string
   :vector (comp vec list)
   :operator choose-operator
   :operation apply
   :expr identity})

(defn parse [input]
  (->> (parser input) (insta/transform transform-options)))

(parser "1 2 3")

(parse "1 2 3 4")
(parse " 2")
(parse "1 2 ")
(parse "1 2")
(parse "1 2")
(parse "1 2 3")
(parser "+ 1 1")
(parse "+ 1 2")



