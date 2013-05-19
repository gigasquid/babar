(ns babar.parser
  (:require [instaparse.core :as insta]
            [babar.commands :refer :all]
            [babar.speech-acts :refer :all]))

(def parser
  (insta/parser
   "program =   (expr (<'\n'>))+ expr |
                (expr (<';'>) space)+ expr | expr
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
    <item> = command / speech-act / string / number / boolean /
             keyword / bvector / map / identifier
    speech-act = commitment | belief | query | request
    query = 'answer.query' <space> querytype <space> (commitment | belief)
    querytype = 'request.value' | 'request.details' | 'request.completed' |
                'request.created' | 'request.errors' | 'request.fn' |
                'belief.str' | 'belief.fn'
    request = 'accept.request' <space> <'*'>  #'[a-z][0-9a-zA-Z\\-\\_]*'
               <space> expr
    commitment = <'*'> #'[a-z][0-9a-zA-Z\\-\\_]*'
    belief = <'#'> #'[a-z][0-9a-zA-Z\\-\\_]*'
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

(defn babar-eval [expr]
  (do
    (fufill-commitments)
    (eval expr)))

(defn eval-program [expr-list]
  (let [evaled-list (doall (map babar-eval expr-list))]
    (last evaled-list)))

(def transform-options
  {:number read-string
   :string str
   :keyword keyword
   :boolean read-string
   :svector (comp vec list)
   :bvector (comp vec list)
   :map hash-map
   :commitment commitment
   :belief belief
   :request request
   :speech-act identity
   :identifier read-string
   :commandkey identity
   :command babar-command
   :functioncall babar-functioncall
   :expr identity
   :querytype identity
   :query query
   :program (comp eval-program list)})

(defn parse [input]
  (->> (parser input) (insta/transform transform-options)))
