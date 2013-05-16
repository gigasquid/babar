(ns babar.speech-acts-test
  (:require [midje.sweet :refer :all]
            [babar.parser :refer :all]))

(defn setup-commitments []
  (swap! commitments merge
         {:test (make-commitment (fn [] "test") 1 "completed" "error")}))

(facts "about accepting requests"
  (type (parse "accept.request *up-temp fn [x] (+ x 1)")) => babar.parser.Commitment
  (nil? (:up-temp @commitments)) => false)


(facts "about answering queries"
  (parse "answer.query request.value *test") => 1
  (parse "answer.query request.completed *test") => "completed"
  (nil? (parse "answer.query request.created *test")) => false
  (parse "answer.query request.errors *test") => "error"
  (against-background (before :facts (setup-commitments))))
