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

(defn choose-operator [op]
  +)

(def transform-options
  {:number read-string
   :vector (comp vec list)
   :operator choose-operator
   :operation apply
   :expr identity})

(defn parse [input]
  (->> (parser input) (insta/transform transform-options)))

