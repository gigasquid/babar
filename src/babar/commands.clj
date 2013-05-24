(ns babar.commands)

(defn babar-defn [v]
  (let [s (first v)
        params (second v)
        expr (nth v 2)]
    `(defn ~s ~params ~expr)))

(defn babar-fn [v]
  (let [params (first v)
        expr (second v)]
    `(fn ~params ~expr)))

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

(defn babar-and [v]
  `(reduce #(and %1 %2) ~v))

(defn babar-or [v]
  `(reduce #(or %1 %2) ~v))

(defn babar-println [v]
  `(println (apply str ~v)))

(defn babar-import [v]
  `(require '[~(symbol (first v)) :refer :all]))

(defn babar-deref [item]
  `@~item)

(defn babar-get [v]
  `(get (first ~v) (second ~v)))

(defn babar-do [v]
  `(do ~v))

(defn babar-sleep [v]
  `(Thread/sleep (first ~v)))

(defn babar-command [command v]
  (case command
    "+" (babar-operation + v)
    "-" (babar-operation - v)
    "*" (babar-operation * v)
    "/" (babar-operation / v)
    "def" (babar-def v)
    "defn" (babar-defn v)
    "fn" (babar-fn v)
    "if" (babar-if v)
    "=" (babar-compare = v)
    ">" (babar-compare > v)
    "<" (babar-compare < v)
    "and" (babar-and v)
    "or" (babar-or v)
    "import" (babar-import v)
    "println" (babar-println v)
    "get" (babar-get v)
    "do" (babar-do v)
    "sleep" (babar-sleep v)))

(defn babar-functioncall [sym & [v]]
  `(apply ~sym ~v))

