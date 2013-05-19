(ns babar.speech-acts
  (:require [clj-time.core :as time]
            [clj-time.format :as tformat]))

(def commitments (atom {}))
(def beliefs (atom {}))

(defrecord Commitment [fn val completed created errors])
(defrecord Belief [str fn])

(def built-in-formatter (tformat/formatters :date-hour-minute-second-ms))
(tformat/unparse built-in-formatter (time/now))

(defn gen-timestamp []
  (tformat/unparse built-in-formatter (time/now)))

(defn belief [name]
  `((keyword ~name) @beliefs))

(defn make-belief [str fn]
  (let [cfn (if (vector? fn) (first fn) fn)]
    (Belief. str fn)))

(defn accept-belief [id str expr]
  `((keyword ~id)
    (swap! beliefs merge
           {(keyword ~id) (make-belief str ~expr)})))

(defn make-commitment [fn val completed errors]
  (let [cfn (if (vector? fn) (first fn) fn)]
   (Commitment. cfn val completed (gen-timestamp) errors)))

(defn request [name id expr]
  `((keyword ~id)
    (swap! commitments merge
           {(keyword ~id) (make-commitment ~expr nil nil nil)})))

(defn commitment [name]
  `((keyword ~name) @commitments))

(defn commitment-belief-query [c key]
  `(~key ~c))

(defn query [name type c]
  (when (= name "answer.query")
    (case type
      "request.value" (commitment-belief-query c :val)
      "request.fn" (commitment-belief-query c :fn)
      "request.completed" (commitment-belief-query c :completed)
      "request.created" (commitment-belief-query c :created)
      "request.errors" (commitment-belief-query c :errors)
      "belief.str" (commitment-belief-query c :str)
      "belief.fn" (commitment-belief-query c :fn))))

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