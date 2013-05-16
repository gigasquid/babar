(ns babar.parser
  (:require [instaparse.core :as insta]
            [babar.commands :refer :all]))

(def parser
  (insta/parser
   "program = (expr <';'>)+ expr | expr
    expr = item | command | vector | functioncall
    command = commandkey space vector |
               <'('> (space)* commandkey space vector (space)* <')'>
    commandkey = operation | special
    functioncall =  <'('> (space)* identifier (space)* ?[vector] (space)* <')'> |
                    identifier <':'> ?[vector] |
                    <'('> (space)* command (space)* ?[vector] (space)* <')'>
    map = <'{'> ((space)* item (space)*)+ <'}'>
    <vector>  = svector | bvector
    svector = ((space)* item (space)*)+
    bvector =  <#'\\['> ((space)* item (space)*)+ <#'\\]'> |
               <#'\\[\\]'>
    <space> = <#'[\\s\\t\\n]+'>
    <item> = command / commitment/ request/
             string / number / boolean / keyword / bvector /
             map / identifier
    request = 'accept.request' <space> <'*'>  #'[a-z][0-9a-zA-Z\\-\\_]*'
               <space> expr
    commitment = <'*'> #'[a-z][0-9a-zA-Z\\-\\_]*'
    <operation> =  '+' | '-' | '*' | '/'
    identifier =  #'[a-z][0-9a-zA-Z\\-\\_]*' !special
    <special> = 'def' | 'if' | 'defn' | '=' | '<' | '>' | 'and' | 'or'
                | 'import' | 'fn'
    string =  <'\\\"'> #'([^\"\\\\]|\\\\.)*' <'\\\"'>
    keyword = <#'[:]'> #'[\\w|-]+'
    boolean = #'true' | #'false'
    number = integer | decimal
    <decimal> = #'-?[0-9]+\\.[0-9]+'
    <integer> = #'-?[0-9]+'"))


;(parse "+ 1 3")
;(parse "def x 2;(+ x 1)")
;(doall (map eval ()) )
;(parser "accept.request *up-temp fn [x] (+ x 1)")
                              ;(parse "accept.request *up-temp fn [x] (+ x 1)")



(defn eval-program [expr-list]
  (let [evaled-list (doall (map eval expr-list))]
    (last evaled-list)))

(def commitments (atom {}))

(defrecord Commitment [fn val completed errors])

(defn make-commitment [fn val completed errors]
  (Commitment. fn val completed errors))

(defn request [name id expr]
  `(swap! commitments merge
          {(keyword ~id) (make-commitment ~expr nil nil nil)}))

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
   :request request
   :identifier read-string
   :commandkey identity
   :command babar-command
   :functioncall babar-functioncall
   :expr identity
   :program (comp eval-program list)})

(defn parse [input]
  (->> (parser input) (insta/transform transform-options)))
