(ns babar.parser
  (:require [instaparse.core :as insta]))


(def parser
  (insta/parser
   "expr = item | operation | vector
    operation = operator space vector
    operator = '+' | '-' | '*' | '/'
    <vector>  = svector | bvector
    svector = ((space)* item (space)*)+
    bvector =  <#'\\['> ((space)* item+ (space)*)+ <#'\\]'>
    <space> = <#'[ ]+'>
    <item> = string | number | boolean | keyword | bvector
    string =  <'\\\"'> #'([^\"\\\\]|\\\\.)*' <'\\\"'>
    keyword = <#'[:]'> #'\\w+'
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
   :keyword keyword
   :boolean read-string
   :svector (comp vec list)
   :bvector (comp vec list)
   :operator choose-operator
   :operation apply
   :expr identity})

(defn parse [input]
  (->> (parser input) (insta/transform transform-options)))
