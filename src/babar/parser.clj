(ns babar.parser
  (:require [instaparse.core :as insta]))


(def parser
  (insta/parser
   "expr = item | operation
    operation = operator space vector
    operator = '+' | '-' | '*' | '/'
    vector = ((space)* item (space)*)+ |
             <#'\\['> ((space)* item+ (space)*)+ <#'\\]'>
    <space> = <#'[ ]+'>
    <item> = string | number | boolean
    string =  <'\\\"'> #'([^\"\\\\]|\\\\.)*' <'\\\"'>
    boolean = #'true' | #'false'
    number = integer | decimal
    <decimal> = #'-?[0-9]+\\.[0-9]+'
    <integer> = #'-?[0-9]+'"))

; (re-find #"^\"[a-zA-z][0-9a-zA-Z\-\_]*\"$" "\"test-1-2\"")

(defn choose-operator [op]
  (case op
    "+" +
    "-" -
    "*" *
    "/" /))

(read-string "true")

(def transform-options
  {:number read-string
   :string str
   :boolean read-string
   :vector (comp vec list)
   :operator choose-operator
   :operation apply
   :expr identity})

(defn parse [input]
  (->> (parser input) (insta/transform transform-options)))
