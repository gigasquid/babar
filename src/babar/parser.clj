(ns babar.parser
  (:require [instaparse.core :as insta]
            [clojure.java.io :as io]
            [babar.commands :refer :all]
            [babar.speech-acts :refer :all]))

(declare parse)

(def parser
  (insta/parser
   "program =   (expr / vector) / (expr <'.'> space*)+
    expr = item | command | functioncall | readprogram
    readprogram = <'read'> space string
    command = commandkey space vector |
               <'('> (space)* commandkey space vector (space)* <')'>
    commandkey = operation | special
    functioncall =  <'('> item  <')'> /
                    <'('> item space vector <')'> /
                    item <':'> space vector /
                    item <':'>
    map = <'{'> ((space)* item (space)*)+ <'}'>
    <vector>  = svector | bvector
    svector = ((space)* item (space)*)+
    bvector =  <#'\\['> ((space)* item (space)*)+ <#'\\]'> |
               <#'\\[\\]'>
    <space> = <#'[\\s\\t\\n]+'>
    <item> = command / speech-act / deref / functioncall / string / number / boolean /
             keyword / bvector / map / identifier
    speech-act = commitment | belief | query | request | convince | assertion
    assertion = <'assert'> space #'[a-z][0-9a-zA-Z\\-\\_]*' space bvector space item /
                <'assert'> space #'[a-z][0-9a-zA-Z\\-\\_]*' space item
    query = 'answer' space querytype space (commitment | belief) /
            'answer' space querytype
    querytype = 'request-value' | 'request-details' | 'request-completed' |
                'request-created' | 'request-errors' | 'request-fn' |
                'request-when' | 'belief-str' | 'belief-fn' |
                'requests-all' | 'beliefs-all'
    request =   'request' space <'*'>  #'[a-z][0-9a-zA-Z\\-\\_]*' space
                   <'when'> space belief space expr  /
                'request' space <'*'>  #'[a-z][0-9a-zA-Z\\-\\_]*' space expr
    convince = 'convince' space <'#'> #'[a-z][0-9a-zA-Z\\-\\_]*'
               space string space expr
    commitment = <'*'> #'[a-z][0-9a-zA-Z\\-\\_]*'
    belief = <'#'> #'[a-z][0-9a-zA-Z\\-\\_]*'
    <operation> =  '+' | '-' | '*' | '/'
    deref = <'@'> identifier
    identifier =  #'[a-z][0-9a-zA-Z\\-\\_]*'
    <special> = 'def' | 'if' | 'defn' | '=' | '<' | '>' | 'and' | 'or'
                | 'import' | 'fn' | 'println' | 'get' | 'do'
    string =  <'\\\"'> #'([^\"\\\\]|\\\\.)*' <'\\\"'>
    keyword = <#'[:]'> #'[\\w|-]+'
    boolean = #'true' | #'false'
    number = integer | decimal
    <decimal> = #'-?[0-9]+\\.[0-9]+'
    <integer> = #'-?[0-9]+'"))


(defn babar-eval [expr]
  (eval expr))

(defn eval-program [expr-list]
  (let [evaled-list (doall (map babar-eval expr-list))]
    (last evaled-list)))

(defn read-program [filename]
  `(parse (slurp ~filename)))

(def transform-options
  {:number read-string
   :string str
   :keyword keyword
   :boolean read-string
   :svector (comp vec list)
   :bvector (comp vec list)
   :map hash-map
   :deref babar-deref
   :assertion babar-assert
   :commitment commitment
   :belief belief
   :request request
   :convince convince
   :speech-act identity
   :identifier read-string
   :commandkey identity
   :command babar-command
   :functioncall babar-functioncall
   :expr identity
   :querytype identity
   :query query
   :readprogram read-program
   :program (comp eval-program list)})

(defn parse [input]
  (->> (parser input) (insta/transform transform-options)))

(defn init []
  (init-commitments))

