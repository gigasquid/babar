(ns babar.parser
  (:require [instaparse.core :as insta]))


(def parser
  (insta/parser
   "expr = number | vector | operation
    operation = operator space+ vector
    operator = '+'
    vector = (number space number)+
    <space> = <#'[ ]+'>
    number = #'[0-9]+'"))

(def transform-options
  {:number read-string  :vector (comp vec list) :plus read-string :operator identity :operation apply :expr identity})

(defn parse [input]
  (->> (parser input) (insta/transform transform-options)))

(parser "1 2 3")

(parse " 1 2")

