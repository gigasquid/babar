(ns babar.speech-acts
  (:require [clj-time.core :as time]
            [clj-time.format :as tformat]
            [babar.commands :refer :all]
            [me.raynes.conch.low-level :as conchll]))

(def commitments (atom {}))
(def beliefs (atom {}))
(def commitments-agent (agent {}))
(def speak-flag (atom false))
(def ask-flag (atom false))
(def last-said (atom nil))
(def speak-voice (atom "Bruce"))

(defrecord Commitment [fn val completed created errors when until ongoing cancelled])
(defrecord Belief [str fn])

(def built-in-formatter (tformat/formatters :date-hour-minute-second-ms))
(tformat/unparse built-in-formatter (time/now))

(defn speak-config [val & [voice]]
  (if val (reset! speak-flag true) (reset! speak-flag false))
  (when voice (reset! speak-voice voice)))

(defn ask-config [val]
  (if val (reset! ask-flag true) (reset! ask-flag false)))

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

(defn make-commitment [fn val completed errors when until ongoing]
  (let [cfn (if (vector? fn) (first fn) fn)]
   (Commitment. cfn val completed (gen-timestamp) errors when until ongoing nil)))


(defn request-plain [name id expr ongoing]
  `((keyword ~id)
    (swap! commitments merge
           {(keyword ~id) (make-commitment ~expr nil nil nil nil nil ~ongoing)})))

(defn request-when-until [name id when until expr ongoing]
  `((keyword ~id)
    (swap! commitments merge
           {(keyword ~id) (make-commitment ~expr nil nil nil ~when ~until ~ongoing)})))

(defn cancel-commitment [id]
  `(swap! commitments merge
         {(keyword ~id) (assoc ((keyword ~id) @commitments) :cancelled true)}))

(defn request
  ([name id] (when (= name "request-cancel") (cancel-commitment id)))
  ([name id expr] (request-plain name id expr nil))
  ([name id ongoing expr] (request-plain name id expr true))
  ([name id type belief expr] (case type
                                "when" (request-when-until name id belief nil expr nil)
                                "until" (request-when-until name id nil belief expr nil)))
  ([name id when when-belief ongoing expr]
     (request-when-until name id when-belief nil expr true))
  ([name id when when-belief until until-belief expr]
     (request-when-until name id when-belief until-belief expr nil)))

(defn commitment [name]
  `((keyword ~name) @commitments))

(defn commitment-belief-query [c key]
  `(~key ~c))

(defn commitment-is-done [c]
  `(not (nil? (:completed ~c))))

(defn all-commitments-beliefs [a]
  (vec (keys @a)))

(defn ask-query [s]
  (let [question (str "\nquery " s "?")]
   (do
     (println question)
     (when @speak-flag
       (future (conchll/proc "say" "-v" @speak-voice question))))))

(defn answer-query [type v]
  (case type
      "request-value" (commitment-belief-query v :val)
      "request-fn" (commitment-belief-query v :fn)
      "request-completed" (commitment-belief-query v :completed)
      "request-is-done" (commitment-is-done v)
      "request-created" (commitment-belief-query v :created)
      "request-errors" (commitment-belief-query v :errors)
      "request-when" (commitment-belief-query v :when)
      "request-until" (commitment-belief-query v :until)
      "request-ongoing" (commitment-belief-query v :ongoing)
      "request-cancelled" (commitment-belief-query v :cancelled)
      "requests-all" (all-commitments-beliefs commitments)
      "belief-str" (commitment-belief-query v :str)
      "belief-fn" (commitment-belief-query v :fn)
      "beliefs-all" (all-commitments-beliefs beliefs)
      "value" `~v))

(defn query [name id & v]
  (case name
    "query" (answer-query id (first v))
    "ask-query" (ask-query id)))

(defn say-belief [str]
  (when-not (= @last-said str)
    (do
      (reset! last-said str)
      (future (conchll/proc "say" "-v" @speak-voice str)))))

(defn babar-assert ([id val] (babar-def (list (symbol id) val)))
  ([id params form] (babar-defn (list (symbol id) params form))))


(defn check-when-belief [when-pred]
  (if ((:fn when-pred))
    (do (when @speak-flag (say-belief (:str when-pred))
              )
        true)))

(defn need-to-fufill-commitment? [c]
  (try
    (let [not-complete (and
                        (nil? (:completed (val c)))
                        (nil? (:cancelled (val c))))
          when-pred (:when (val c))]
      (and not-complete
           (if when-pred (check-when-belief when-pred) true)))
    (catch Exception e (do
                         (swap! commitments merge {(key c) (assoc ((key c) @commitments) :errors (str e " " (.getMessage e)))})
                         nil))))

(defn unfufilled-commitments []
  (into {} (filter need-to-fufill-commitment? @commitments)))

(defn complete-until [until]
  (do
    (when @speak-flag (say-belief (:str until)))
    true))

(defn should-mark-complete? [c]
  (if (:until c)
    (if ((:fn (:until c))) (complete-until (:until c)) false)
    (not (:ongoing c))))

(defn fufill-commitment [entry]
  (try
    (let [[k c] entry
         result ((:fn c))]
      [ k (merge c {:val result :completed (when (should-mark-complete? c) (gen-timestamp))})]
      )
    (catch Exception e
      [ (first entry) (merge (last entry) {:errors (str e " " (.getMessage e))})])))

(defn fufill-commitments [_]
  (do
   (swap! commitments merge
          (into {} (map fufill-commitment (unfufilled-commitments))
                ))
   (Thread/sleep 5)
   (recur nil)))

(defn init-commitments []
  (send commitments-agent fufill-commitments))

(defn b-declare [s]
  (eval `(declare ~s)))

(defn babar-indentifier [s]
  (if (resolve (read-string s))
    (read-string s)
    (if @ask-flag
      (do
        (ask-query s)
        (b-declare (read-string s))
        (symbol s))
      (read-string s))))

