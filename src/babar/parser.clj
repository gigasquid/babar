(ns babar.parser
  (:require [instaparse.core :as insta]))


(def parser
  (insta/parser
   "program = expr
    expr = item | command | vector
    command = commandkey space vector |
               <'('> (space)* commandkey space vector (space)* <')'>
    commandkey = operation | special
    map = <'{'> ((space)* item (space)*)+ <'}'>
    <vector>  = svector | bvector
    svector = ((space)* item (space)*)+
    bvector =  <#'\\['> ((space)* item+ (space)*)+ <#'\\]'>
    <space> = <#'[\\s\\t\\n]+'>
    <item> = command / string / number / boolean / keyword / bvector /
              map / identifier
    <operation> =  '+' | '-' | '*' | '/'
    identifier =  #'[a-z][0-9a-zA-Z\\-\\_]*' !special
    <special> = 'def' | 'if'
    string =  <'\\\"'> #'([^\"\\\\]|\\\\.)*' <'\\\"'>
    keyword = <#'[:]'> #'\\w+'
    boolean = #'true' | #'false'
    number = integer | decimal
    <decimal> = #'-?[0-9]+\\.[0-9]+'
    <integer> = #'-?[0-9]+'"))


(defn babar-def [v]
  (let [s (str (first v))
        val (second v)]
    `(def ~(symbol s) ~val)))

(defn babar-if [v]
  (let [[test then else] v]
    `(if ~test ~then ~else)))

(defn babar-operation [op v]
  `(apply ~op ~v))

(defn babar-command [command v]
  (case command
    "+" (babar-operation + v)
    "-" (babar-operation - v)
    "*" (babar-operation * v)
    "/" (babar-operation / v)
    "def" (babar-def v)
    "if" (babar-if v)))


(def transform-options
  {:number read-string
   :string str
   :keyword keyword
   :boolean read-string
   :svector (comp vec list)
   :bvector (comp vec list)
   :map hash-map
   :identifier read-string
   :commandkey identity
   :command babar-command
   :expr identity
   :program eval})

(defn parse [input]
  (->> (parser input) (insta/transform transform-options)))
