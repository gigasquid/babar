(ns babar.parser
  (:require [instaparse.core :as insta]
            [babar.commands :refer :all]))

(def commitments (atom {}))

(defrecord Commitment [expr val completed errors])

(defn make-commitment [expr val completed errors]
  (Commitment. expr val completed errors))

(def parser
  (insta/parser
   "program = expr
    expr = item | command | vector | functioncall
    command = commandkey space vector |
               <'('> (space)* commandkey space vector (space)* <')'>
    commandkey = operation | special
    functioncall =  <'('> (space)* identifier (space)* ?[vector] (space)* <')'> |
                    identifier <':'> ?[vector]
    map = <'{'> ((space)* item (space)*)+ <'}'>
    <vector>  = svector | bvector
    svector = ((space)* item (space)*)+
    bvector =  <#'\\['> ((space)* item (space)*)+ <#'\\]'> |
               <#'\\[\\]'>
    <space> = <#'[\\s\\t\\n]+'>
    <item> = command / commitment/ string / number / boolean / keyword / bvector /
              map / identifier
    commitment = <'*'> #'[a-z][0-9a-zA-Z\\-\\_]*'
    <operation> =  '+' | '-' | '*' | '/'
    identifier =  #'[a-z][0-9a-zA-Z\\-\\_]*' !special
    <special> = 'def' | 'if' | 'defn' | '=' | '<' | '>' | 'and' | 'or' | 'import'
    string =  <'\\\"'> #'([^\"\\\\]|\\\\.)*' <'\\\"'>
    keyword = <#'[:]'> #'[\\w|-]+'
    boolean = #'true' | #'false'
    number = integer | decimal
    <decimal> = #'-?[0-9]+\\.[0-9]+'
    <integer> = #'-?[0-9]+'"))

(defn commitment [name]
  `((keyword ~name) @commitments))

(def transform-options
  {:number read-string
   :string str
   :keyword keyword
   :boolean read-string
   :svector (comp vec list)
   :bvector (comp vec list)
   :map hash-map
   :commitment commitment
   :identifier read-string
   :commandkey identity
   :command babar-command
   :functioncall babar-functioncall
   :expr identity
   :program eval})

(defn parse [input]
  (->> (parser input) (insta/transform transform-options)))
