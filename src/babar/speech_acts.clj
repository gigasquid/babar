(ns babar.speech-acts
  (:require [clj-time.core :as time]
            [clj-time.format :as tformat]))

(def commitments (atom {}))

(defrecord Commitment [fn val completed created errors])

(def built-in-formatter (tformat/formatters :date-hour-minute-second-ms))
(tformat/unparse built-in-formatter (time/now))

(defn gen-timestamp []
  (tformat/unparse built-in-formatter (time/now)))

(defn make-commitment [fn val completed errors]
  (let [cfn (if (vector? fn) (first fn) fn)]
   (Commitment. cfn val completed (gen-timestamp) errors)))

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

(defn unfufilled-commitments []
  (into {} (filter (comp nil? :completed val) @commitments)))

(defn fufill-commitment [entry]
  (try
    (let [[k c] entry
         result ((:fn c))]
      [ k (merge c {:val result :completed (gen-timestamp)})]
      )
    (catch Exception e
      [ (first entry) (merge (last entry) {:errors (.getMessage e)})])))

(defn fufill-commitments []
  (swap! commitments merge
         (into {} (map fufill-commitment (unfufilled-commitments)))))