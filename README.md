# babar

A Programming Language with Speech Acts inspired by
[Elephant 2000](http://www-formal.stanford.edu/jmc/elephant/elephant.html).
The parser uses the wonderful Clojure
[Instaparse](https://github.com/Engelberg/instaparse) library.
The language goals are to explore some concepts of speech acts. To
support this goal, there is some syntatic sugar to make the language
more readable as sentances. The main manifestation is that parens
are optional.

This language is a playground, so don't expect things to
be stable or documentation up to date.


## Data Types
Most of the data types are directly from Clojure.  You have integers,
decimals, strings, booleans, keywords, maps, and vectors,

```clojure
1     ;=> 1
2.3   ;=> 2.3
-3.4  ;=> 3.4
"cat" ;=> cat
:bird ;=> bird
true  ;=> true
{:cat :meow :dog :bark} ;=> {:cat :meow :dog :bark}
[1 2 true :bird] ;=> [1 2 true bird]
```
Vectors are a bit interesting in the respect that you don't need
to input the square brackets.  If you just put in space delimited
items, it will automatically constuct a vector for you.

```clojure
1 2 3 4 ;=> [1 2 3 4]
```

If you want to nest the vectors, you need to include the square
brackets.

```clojure
1 2 [3 4 5] ;=> [1 2 [3 4 5]]
```

## Operations
The basic usual suspects are supported : ( +, -, / , *).
The interesing thing to note is that parens are optional,
and all operations work on a vector by default - so:

```clojure
(+ 1 2 3 4 5) ;=> 15
```
Is the same as:

````clojure
+ 1 2 3 4 5 ;=> 15
````

## Commands
A subset of the clojure commands have been included. This will
grow in time.  Mind you parens are optional.  You can call functions
with the typical () or with a shorthand : syntax

- def (def identifier expression)
```clojure
  (def dog 16)
  dog ;=> 16
  
  def cat 18
  cat ;=> 18
```
- defn (defn identifier params expression)
```clojure
  (defn cat [x] (+ x 2))
  (cat 2); => 4
  
  defn dog [] "woof"
  dog: ;=> "woof"
```
- if  (if predicate truecase falsecase)
```clojure
  if true :cat :dog ;=> :cat
```
- =, <, >
```clojure
  = :dog :dog ;=> true
  ```
- and  (and val1 val2 & others)
```clojure
  and true true true ;=> true
  and true true false ;=> false
```
- or  (or val1 val2 & others)
```clojure
  or true false true ;=> true
  or false false false ;=> false
```
- import (import "ns")
There is basic support for importing clojure namespaces.
At this basic level it imports the whole namespace and does require
:refer :all
```clojure
  import "clojure.java.io"
```

- println (println item & others)

Concatonates the items as a string and prints it out to stdout

```clojure
println "cat" ;=> "cat" (returns nil)
println "cat" " " 1 " " :duck ;=> "cat 1 :duck" (returns nil)
```

- get (get hash key)
Gets the value of a hash by key

```clojure
get {:a 1} :a => :a
```

- do (do expr expr+)
Do multiple expressions

```clojure
do (def s1 1) (def s2 2)
s1 ;=> 1
s2 ;=> 2
```

- sleep (sleep ms)
Sleep for given milliseconds

```clojure
sleep 5
```

### Anonymous Functions
You can create anonymous functions with the fn [x] syntax from
clojure. And call them with surrounding parens.
````clojure
   ((fn [x] + x 1) 3) ;=> 4
   ((fn [x y z] + x y z) [1 2 3]) ;=> 6
   ((fn [] [4 5 6])) ;=> [4 5 6]
```

## Speech Acts
According to John Searle's
[Speech Acts](http://en.wikipedia.org/wiki/Speech_act)
There are [Illocutionary Acts](http://en.wikipedia.org/wiki/Illocutionary_act)
that involve the pragmatic meaning of a behind a sentence. Some of the
english verbs denoting these acts are "assert", "command", "request", 
"answer question". For example the sentance, "Pass the salt.", is an
illocutionary act.  When a person hears the sentance, the meaning is
interpreted as a command.  There are also
[Perlocutionary Acts](http://en.wikipedia.org/wiki/Perlocutionary_act),
in which signifigance is on the stametment's effect on the hearer's
actions, thoughts, and beliefs.  An example of this is "persuade" or
"convince".  Some of these speech acts have been incorporated into the
language.  So far there is support for:

### Datatypes
- Commitment (*name)

A commitment is a datatype designated by a *name

```clojure
  *bark
```

- Belief (#name)

A belief is a datatype designated by a #name

```clojure
  #sunny
```

### Convincing

- convinced (convinced belief string predicate-function)

To be convinced will create an internal belief that has a human
readable string as a description and a predicate function that
evaluates to true when the machine "believes" it.

```clojure
   convince #sunny "It is sunny" fn [x] (= 1 1)
```

### Requests
- request (request commitment function)

Accepting a request creates an internal commitment that is evaluated
at a future time.  Behind the scenese there is a cron-like watcher
that continually sees if it has any commitments to execute.  If there
is an error that occurs, then it will have an error captured that you
can query by using "query request-errors".

```clojure
  request *dog fn [] :bark ;=> babar.speech_acts.Commitment
```

- request (request commitment when belief function)
You can also specify a request to be executed when a belief is held.
The request is executed when the belief predicate function evaluates
to true.
```clojure
  convince #too-warm "It is too warm." fn [] > temperature 70"
  request *lower-temp when #too-warm fn [] :lower-the-temp-action
```

- request (request commitment until belief function)
You can specify a request to be executed until a belief is held.
The request will continue to execute until the belief is held.
```clojure
  convince #just-right "It is just-right" fn [] > @temp 70"
  request *raise-temp until #just-right fn [] (increase-temp)
```

- request (request commitment when belief until function)
You can specify a request to be executed when a belief is held and
until another belief is held.

````clojure
   convince #just-right "It is just-right" fn [] > @temp 70
   convince #start "Time to start" fn [] > @temp 68
   request *raise-temp when #start until #just-right fn [] (increase-temp)
````

### Query
* query

query request-[fn | completed | value | errors | created | when | until | is-done]
fn)

query belief.[str | fn ])

query requests.all

query beliefs.all

```clojure
   request *dog fn [] :bark.
   query request-value *dog ;=> :bark
   query request-completed * dog ;=> "2013-05-17T19:58:07.882"
   query request-is-done ;=> true
```

### Speaking the Beliefs using Say
* speak-beliefs (speak-beliefs [true | false ]

If you toggle on the speak-beliefs, then (if you have a mac and say),
then any beliefs will be spoken aloud when there belief fns evaluate
to true - or the beliefs are being held.  If there are multiple
beliefs, (like using an until), then it will only speak when the
belief changes.

## Reading babar programs
* read (read filename)

This command will read a *.babar file into the repl and evaluate it
it.  A program is composed of multiple expressions that are delimited
by a period.

simple.babar
```clojure
assert a 1.
assert b 10.
assert c [:a :b (+ a b)].
```

```clojure
read "simple.babar" ;=> #'user/c
c ;=> [:a :b 11]
```


## REPL
Launch a REPL

    lein run

## TESTS

    lein midje


## License

Copyright © 2013 Carin Meier

Distributed under the Eclipse Public License, the same as Clojure.
