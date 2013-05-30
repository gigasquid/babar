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
                                 (make-belief "Everything is fine"
                                              (fn [] (= 1 1))) nil nil)}))


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
                                      {:raise-temp (make-commitment '(+ 1 1) 1 true nil nil nil nil)}))))


(facts "about accepting requests"
  (type (parse "request *up-temp fn [] (+ 3 1)")) => babar.speech_acts.Commitment
  (nil? (:up-temp @commitments)) => false)


(facts "about querying queries about requests"
  (parse "query request-value *test") => 1
  (parse "query request-completed *test") => "completed"
  (nil? (parse "query request-created *test")) => false
  (nil? (parse "query request-fn *test")) => false
  (nil? (parse "query request-when *test")) => false
  (nil? (parse "query request-until *test")) => true
  (nil? (parse "query request-ongoing *test")) => true
  (nil? (parse "query request-cancelled *test")) => true
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

(facts "about querying all beliefs"
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
  (Thread/sleep 80)
  (parse "query request-value *lower-temp") => :lower-the-temp-action
  (nil? (parse "query request-completed *lower-temp")) => false
  (against-background (before :facts (reset-commitments))))

(def temp (atom 65))
(defn increase-temp []
  (swap! temp inc))

(facts "about processing commitments with until"
  (parse "convince #just-right \"It is just-right\" fn [] > @temp 70") => anything
  (parse "request *raise-temp until #just-right fn [] (increase-temp)") => anything
  (Thread/sleep 60)
  (parse "query request-is-done *raise-temp") => true
  (parse "query request-value *raise-temp")   => 71
  (nil?(parse "query request-completed *raise-temp")) => false
  (against-background (before :facts (reset! temp 69))))

(facts "about processing commitments with when and until"
  (parse "convince #just-right \"It is just-right\" fn [] > @temp 70") => anything
  (parse "convince #start \"Time to start\" fn [] > @temp 68") => anything
  (parse "request *raise-temp when #start until #just-right fn [] (increase-temp)") => anything
  (Thread/sleep 60)
  (parse "query request-is-done *raise-temp") => false
  (parse "query request-value *raise-temp")   => nil
  (nil? (parse "query request-completed *raise-temp")) => true
  (reset! temp 69) => anything
  (Thread/sleep 60)
  (parse "query request-is-done *raise-temp") => true
  (parse "query request-value *raise-temp")   => 71
  (nil? (parse "query request-completed *raise-temp")) => false
  (against-background (before :facts (reset! temp 65))))


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
  (parse "query request-errors *cat") => "java.lang.ArithmeticException: Divide by zero Divide by zero"
  (against-background (before :facts (reset-commitments))))


(facts "about processing commitment with a when error"
  (type (parse "convince #bad \"This is really bad.\" fn [] / 0 0")) => babar.speech_acts.Belief
  (type (parse "request *cat when #bad fn [] + 1 1")) => babar.speech_acts.Commitment
  (Thread/sleep 30) => anything
  (parse "query request-completed *cat") => nil
  (parse "query request-is-done *cat") => false
  (parse "query request-value *cat") => nil
  (parse "query request-errors *cat") => "java.lang.ArithmeticException: Divide by zero Divide by zero"
  (against-background (before :facts (reset-commitments))))

(def x1 (atom 1))
(defn inc-x1 []
  (swap! x1 inc))
(facts "about processing ongoing commitments"
  (parse "request *count ongoing fn [] (inc-x1)") => anything
  (Thread/sleep 20) => anything
  (> (parse "@x1") 2) => true
  (against-background (before :facts (reset-commitments))))

(def y2 1)
(facts "about processing when and ongoing commitments"
  (parse "convince #start \"Time to start\" fn [] = y2 2")
  (parse "request *count when #start ongoing fn [] (inc-x1)") => anything
  (Thread/sleep 20) => anything
  (parse "@x1") => 1
  (def y2 2) => anything
  (Thread/sleep 20) => anything
  (> (parse "@x1") 2) => true
  (against-background (before :facts (do (reset! x1 1)
                                         (reset-commitments)))))

(facts "about processing multi step requests"
  (parse "request *step1 fn [] + 1 1") => anything
  (parse "convince #done1 \"Done with 1\" fn [] query request-is-done *step1") => anything
  (parse "request *step2 when #done1 fn [] + 2 2") => anything
  (parse "convince #done2 \"Done with 2\" fn [] query request-is-done *step2") => anything
  (parse "request *step3 when #done2 fn [] + 3 3") => anything
  (Thread/sleep 30) => anything
  (parse "query request-is-done *step1") => true
  (parse "query request-is-done *step2") => true
  (parse "query request-is-done *step3") => true
  (against-background (before :facts (reset-commitments))))


(facts "about speak-beliefs"
  (parse "speak-beliefs true") => anything
  @speak-flag => true
  (parse "speak-beliefs false") => anything
  @speak-flag => false)
