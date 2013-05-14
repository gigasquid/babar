(ns babar.commands)

(defn babar-defn [v]
  (let [s (first v)
        params (second v)
        expr (nth v 2)]
    `(defn ~s ~params ~expr)))

(defn babar-def [v]
  (let [s (first v)
        val (second v)]
    `(def ~s ~val)))

(defn babar-if [v]
  (let [[test then else] v]
    `(if ~test ~then ~else)))

(defn babar-compare [op v]
  (let [[x y] v]
    `(~op ~x ~y)))

(defn babar-operation [op v]
  `(apply ~op ~v))

(defn babar-command [command v]
  (case command
    "+" (babar-operation + v)
    "-" (babar-operation - v)
    "*" (babar-operation * v)
    "/" (babar-operation / v)
    "def" (babar-def v)
    "defn" (babar-defn v)
    "if" (babar-if v)
    "=" (babar-compare = v)
    ">" (babar-compare > v)
    "<" (babar-compare < v)))

(defn babar-functioncall [sym & [v]]
  `(apply ~sym ~v))
