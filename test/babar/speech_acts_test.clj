(ns babar.speech-acts-test
  (:require [midje.sweet :refer :all]
            [babar.parser :refer :all]
            [babar.speech-acts :refer :all]))

(init-commitments)

(defn reset-commitments []
  (reset! commitments {}))

(defn setup-commitments []
  (swap! commitments merge
         {:test (make-commitment (fn [] "test") 1 "completed" "error"
                                 (make-belief "Everything is fine" (fn [] (= 1 1))))}))


(defn reset-beliefs []
  (reset! beliefs {}))

(defn setup-beliefs []
  (swap! beliefs merge
         {:nice-day (make-belief "It is a nice day."(fn [] (= 2 2)))}))


(facts "about parsing beliefs"
  (= babar.speech_acts.Belief (type (parse "#rainy"))) => true
  (against-background  (before :facts
                               (swap! beliefs merge
                                      {:rainy (make-belief "It is rainy out."
                                                           (fn [] (= 1 1)))}))))

(facts "about being convinced of a belief"
  (type
   (parse "convince #sunny \"It is sunny\" fn [] = 1 1")) => babar.speech_acts.Belief
   (parse "query belief-str #sunny") => "It is sunny"
   ((parse "query belief-fn #sunny")) => true)

(facts "about parsing commitments"
  (= babar.speech_acts.Commitment (type (parse "*raise-temp"))) => true
  (against-background  (before :facts
                               (swap! commitments merge
                                      {:raise-temp (make-commitment '(+ 1 1) 1 true nil nil)}))))


(facts "about accepting requests"
  (type (parse "request *up-temp fn [] (+ 3 1)")) => babar.speech_acts.Commitment
  (nil? (:up-temp @commitments)) => false)


(facts "about querying queries about requests"
  (parse "query request-value *test") => 1
  (parse "query request-completed *test") => "completed"
  (nil? (parse "query request-created *test")) => false
  (nil? (parse "query request-fn *test")) => false
  (nil? (parse "query request-when *test")) => false
  (parse "query request-errors *test") => "error"
  (against-background (before :facts (setup-commitments))))

(facts "about querying query about all requests"
  (parse "request *up-temp fn [] (+ 3 1)") => anything
  (parse "request *down-temp fn [] (- 3 1)") => anything
  (parse "query requests-all") => (contains[:up-temp :down-temp] :in-any-order)
  (against-background (before :facts (reset-commitments))))

(facts "about querying queries about beliefs"
  (parse "query belief-str #nice-day") => "It is a nice day."
  ((parse "query belief-fn #nice-day")) => true
  (against-background (before :facts (setup-beliefs))))

(facts "about query query about all beliefs"
  (parse "convince #sunny \"It is sunny\" fn [] = 1 1")
  (parse "convince #cloudy \"It is cloudy\" fn [] = 1 3")
  (parse "query beliefs-all") => (contains [:sunny :cloudy] :in-any-order)
  (against-background (before :facts (reset-beliefs))))

(facts "about assertions"
  (parse "assert apple 1") => anything
  (parse "apple") => 1
  (parse "assert apple-pie [x] + x 1")
  (parse "(apple-pie 3)") => 4)

(facts "about processing commitments"
  (type (parse "request *dog fn [] :bark")) => babar.speech_acts.Commitment
  (Thread/sleep 30)
  (parse "query request-value *dog") => :bark
  (nil? (parse "query request-completed *dog")) => false
  (against-background (before :facts (reset-commitments))))

(facts "about processing commitments with when"
  (parse "def temperature 65")
  (parse "convince #too-warm \"It is too warm.\" fn [] > temperature 70")
  (parse "request *lower-temp when #too-warm fn [] :lower-the-temp-action")
  (type (parse "query request-when *lower-temp")) => babar.speech_acts.Belief
  (parse "query request-completed *lower-temp") => nil
  (parse "query request-value *lower-temp") => nil
  (parse "def temperature 75") => anything
  (Thread/sleep 30)
  (parse "query request-value *lower-temp") => :lower-the-temp-action
  (nil? (parse "query request-completed *lower-temp")) => false
  (against-background (before :facts (reset-commitments))))

(facts "about processing multiple commitments"
  (type (parse "request *cat fn [] :meow")) => babar.speech_acts.Commitment
  (type (parse "request *bird fn [] :tweet")) => babar.speech_acts.Commitment
  (type (parse "request *horse fn [] :neigh")) => babar.speech_acts.Commitment
  (parse "query request-value *cat") => :meow
  (nil? (parse "query request-completed *cat")) => false
  (parse "query request-is-done *cat") => true
  (parse "query request-value *bird") => :tweet
  (nil? (parse "query request-completed *bird")) => false
  (parse "query request-is-done *bird") => true
  (parse "query request-value *horse") => :neigh
  (nil? (parse "query request-completed *horse")) => false
  (parse "query request-is-done *horse") => true
  (against-background (before :facts (reset-commitments))))

(facts "about processing commitment with an error"
  (type (parse "request *cat fn [] / 0 0")) => babar.speech_acts.Commitment
  (parse "query request-completed *cat") => nil
  (parse "query request-is-done *cat") => false
  (parse "query request-value *cat") => nil
  (parse "query request-errors *cat") => "Divide by zero"
  (against-background (before :facts (reset-commitments))))
