(ns babar.parser
  (:require [instaparse.core :as insta]))


(def parser
  (insta/parser
   "expr = number | string | vector | operation
    operation = operator space vector
    operator = '+' | '-' | '*' | '/'
    vector = ((space)* item (space)*)+ |
             <#'\\['> ((space)* item+ (space)*)+ <#'\\]'>
    <space> = <#'[ ]+'>
    <item> = string | number
    string =   #'^\".+\"'
    number = integer | decimal
    <decimal> = #'-?[0-9]+\\.[0-9]+'
    <integer> = #'-?[0-9]'+"))

; (re-find #"^\"[a-zA-z][0-9a-zA-Z\-\_]*\"$" "\"test-1-2\"")


(defn choose-operator [op]
  (case op
    "+" +
    "-" -
    "*" *
    "/" /))

(def transform-options
  {:number read-string
   :string read-string
   :vector (comp vec list)
   :operator choose-operator
   :operation apply
   :expr identity})

(defn parse [input]
  (->> (parser input) (insta/transform transform-options)))
