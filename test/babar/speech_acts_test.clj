(ns babar.speech-acts-test
  (:require [midje.sweet :refer :all]
            [babar.parser :refer :all]
            [babar.speech-acts :refer :all]))

(defn setup-commitments []
  (swap! commitments merge
         {:test (make-commitment (fn [] "test") 1 "completed" "error")}))

(facts "about parsing commitments"
  (= babar.speech_acts.Commitment (type (parse "*raise-temp"))) => true
  (against-background  (before :facts
                               (swap! commitments merge
                                      {:raise-temp (make-commitment '(+ 1 1) 1 true nil)}))))


(facts "about accepting requests"
  (type (parse "accept.request *up-temp fn [x] (+ x 1)")) => babar.speech_acts.Commitment
  (nil? (:up-temp @commitments)) => false)


(facts "about answering queries"
  (parse "answer.query request.value *test") => 1
  (parse "answer.query request.completed *test") => "completed"
  (nil? (parse "answer.query request.created *test")) => false
  (parse "answer.query request.errors *test") => "error"
  (against-background (before :facts (setup-commitments))))
