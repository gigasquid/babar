(ns babar.parser
  (:require [instaparse.core :as insta]
            [babar.commands :refer :all]
            [clj-time.core :as time]
            [clj-time.format :as tformat]))

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
    <item> = command / commitment/ request/ query/
             string / number / boolean / keyword / bvector /
             map / identifier
    query = 'answer.query' <space> querytype <space> commitment
    querytype = 'request.value' | 'request.details' | 'request.completed' |
                'request.created' | 'request.errors'
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

(defn eval-program [expr-list]
  (let [evaled-list (doall (map eval expr-list))]
    (last evaled-list)))

(def commitments (atom {}))

(defrecord Commitment [fn val completed created errors])

(def built-in-formatter (tformat/formatters :date-hour-minute-second-ms))
(tformat/unparse built-in-formatter (time/now))

(defn gen-timestamp []
  (tformat/unparse built-in-formatter (time/now)))

(defn make-commitment [fn val completed errors]
  (Commitment. fn val completed (gen-timestamp) errors))

(defn request [name id expr]
  `((keyword ~id)
    (swap! commitments merge
           {(keyword ~id) (make-commitment ~expr nil nil nil)})))

(defn commitment [name]
  `((keyword ~name) @commitments))

(defn commitment-query [c key]
  `(~key ~c))

(defn query [name type c]
  (when (= name "answer.query")
    (case type
      "request.value" (commitment-query c :val)
      "request.fn" (commitment-query c :fn)
      "request.completed" (commitment-query c :completed)
      "request.created" (commitment-query c :created)
      "request.errors" (commitment-query c :errors))))

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
   :querytype identity
   :query query
   :program (comp eval-program list)})

(defn parse [input]
  (->> (parser input) (insta/transform transform-options)))
