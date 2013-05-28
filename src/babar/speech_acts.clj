(ns babar.speech-acts
  (:require [clj-time.core :as time]
            [clj-time.format :as tformat]
            [babar.commands :refer :all]
            [me.raynes.conch :as conch]
            ))

(conch/programs say)

(def commitments (atom {}))
(def beliefs (atom {}))
(def commitments-agent (agent {}))
(def speak-flag (atom false))

(defrecord Commitment [fn val completed created errors when until])
(defrecord Belief [str fn])

(def built-in-formatter (tformat/formatters :date-hour-minute-second-ms))
(tformat/unparse built-in-formatter (time/now))

(defn speak-beliefs [val]
  (if val (reset! speak-flag true) (reset! speak-flag false)))

(defn gen-timestamp []
  (tformat/unparse built-in-formatter (time/now)))

(defn belief [name]
  `((keyword ~name) @beliefs))

(defn make-belief [str fn]
  (let [cfn (if (vector? fn) (first fn) fn)]
    (Belief. str cfn)))

(defn be-convinced [id str expr]
  `((keyword ~id)
    (swap! beliefs merge
           {(keyword ~id) (make-belief ~str ~expr)})))

(defn convince [name id str expr]
  (if (= name "convince")
    (be-convinced id str expr)))

(defn make-commitment [fn val completed errors when until]
  (let [cfn (if (vector? fn) (first fn) fn)]
   (Commitment. cfn val completed (gen-timestamp) errors when until)))


(defn request-plain [name id expr]
  `((keyword ~id)
    (swap! commitments merge
           {(keyword ~id) (make-commitment ~expr nil nil nil nil nil)})))

(defn request-when [name id when expr]
  `((keyword ~id)
    (swap! commitments merge
           {(keyword ~id) (make-commitment ~expr nil nil nil ~when nil)})))

(defn request-until [name id until expr]
  `((keyword ~id)
    (swap! commitments merge
           {(keyword ~id) (make-commitment ~expr nil nil nil nil ~until)})))


(defn request
  ([name id expr] (request-plain name id expr))
  ([name id type belief expr] (case type
                                "when" (request-when name id belief expr)
                                "until" (request-until name id belief expr))))

(defn commitment [name]
  `((keyword ~name) @commitments))

(defn commitment-belief-query [c key]
  `(~key ~c))

(defn commitment-is-done [c]
  `(not (nil? (:completed ~c))))

(defn all-commitments-beliefs [a]
  (vec (keys @a)))

(defn query [name type & [c]]
  (when (= name "query")
    (case type
      "request-value" (commitment-belief-query c :val)
      "request-fn" (commitment-belief-query c :fn)
      "request-completed" (commitment-belief-query c :completed)
      "request-is-done" (commitment-is-done c)
      "request-created" (commitment-belief-query c :created)
      "request-errors" (commitment-belief-query c :errors)
      "request-when" (commitment-belief-query c :when)
      "request-until" (commitment-belief-query c :until)
      "requests-all" (all-commitments-beliefs commitments)
      "belief-str" (commitment-belief-query c :str)
      "belief-fn" (commitment-belief-query c :fn)
      "beliefs-all" (all-commitments-beliefs beliefs))))


(defn babar-assert ([id val] (babar-def (list (symbol id) val)))
  ([id params form] (babar-defn (list (symbol id) params form))))


(defn check-when-belief [when-pred]
  (if ((:fn when-pred))
    (do (when @speak-flag (say (:str when-pred)))
        true)))

(defn need-to-fufill-commitment? [c]
  (try
    (let [not-complete (nil? (:completed (val c)))
          when-pred (:when (val c))]
      (and not-complete
           (if when-pred (check-when-belief when-pred) true)))
    (catch Exception e (do
                         (swap! commitments merge {(key c) (assoc ((key c) @commitments) :errors (.getMessage e))})
                         nil))))

(defn unfufilled-commitments []
  (into {} (filter need-to-fufill-commitment? @commitments)))

(defn complete-until [until]
  (do
    (when @speak-flag (say (:str until)))
    true))

(defn should-mark-complete? [c]
  (if (:until c)
    (if ((:fn (:until c))) (complete-until (:until c)) false)
    true))

(defn fufill-commitment [entry]
  (try
    (let [[k c] entry
         result ((:fn c))]
      [ k (merge c {:val result :completed (when (should-mark-complete? c) (gen-timestamp))})]
      )
    (catch Exception e
      [ (first entry) (merge (last entry) {:errors (.getMessage e)})])))

(defn fufill-commitments [_]
  (do
   (swap! commitments merge
          (into {} (map fufill-commitment (unfufilled-commitments))
                ))
   (Thread/sleep 10)
   (recur nil)))

(defn init-commitments []
  (send commitments-agent fufill-commitments))