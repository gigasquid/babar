(ns babar.speech-acts-test
  (:require [midje.sweet :refer :all]
            [babar.parser :refer :all]))

(fact "about accepting requests"
  (type (parse "accept.request *up-temp fn [x] (+ x 1)")) => babar.parser.Commitment
  (nil? (:up-temp @commitments)) => false)

