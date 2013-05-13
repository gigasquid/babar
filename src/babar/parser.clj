(ns babar.parser
  (:require [instaparse.core :as insta]))


(def parser
  (insta/parser
   "expr = item | command | vector
    command = commandkey space vector
    commandkey = operation | special
    map = <'{'> ((space)* item (space)*)+ <'}'>
    <vector>  = svector | bvector
    svector = ((space)* item (space)*)+
    bvector =  <#'\\['> ((space)* item+ (space)*)+ <#'\\]'>
    <space> = <#'[ ]+'>
    <item> = string / number / boolean / keyword / bvector / map / identifier
    <operation> =  '+' | '-' | '*' | '/'
    identifier =  #'[a-z][0-9a-zA-Z\\-\\_]*' !special
    <special> = 'def'
    string =  <'\\\"'> #'([^\"\\\\]|\\\\.)*' <'\\\"'>
    keyword = <#'[:]'> #'\\w+'
    boolean = #'true' | #'false'
    number = integer | decimal
    <decimal> = #'-?[0-9]+\\.[0-9]+'
    <integer> = #'-?[0-9]+'"))


;(insta/parses parser "true")

(defn babar-def [s v]
  `(def ~(symbol s) ~v))


(defn eval-command [command vector]
  (case command
    "+" (apply + vector)
    "-" (apply - vector)
    "*" (apply * vector)
    "/" (apply / vector)
    "def" (eval (babar-def (str (first vector)) (second vector)))
    )
  )


(def transform-options
  {:number read-string
   :string str
   :keyword keyword
   :boolean read-string
   :svector (comp vec list)
   :bvector (comp vec list)
   :map hash-map
   :identifier identity
   :commandkey identity
   :command eval-command
   :expr identity})

(defn parse [input]
  (->> (parser input) (insta/transform transform-options)))
