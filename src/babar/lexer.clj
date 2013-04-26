(ns babar.lexer)

(defn create-token [input]
  (cond
   (number? (read-string input)) [:NUMBER (read-string input)]
   (re-find #"\+" input) [:OPERATOR input]
   ))

(defn tokenize [input]
  (let [chunks (clojure.string/split input #"\ +")]
    (vec (map create-token chunks))))

