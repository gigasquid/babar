# babar

```
Hello Babar!
    ____ 
   /.   \_ 
  /_  \_/  \
 // \  ___ ||
 \\  |_| |_|  
 
ctl-c or quit to exit
===============
babar> 
```

A little language for machines with Speech Acts inspired by
[Elephant 2000](http://www-formal.stanford.edu/jmc/elephant/elephant.html).
The parser uses the wonderful Clojure
[Instaparse](https://github.com/Engelberg/instaparse) library.
The language aims to have syntactically sugared "speech acts" that the
machine uses as inputs and outputs.  The language also supports
beliefs and goals from McCarthy's paper,
[Ascribing Mental Qualities to Machines](http://www-formal.stanford.edu/jmc/ascribing/ascribing.html).

Expressions and programs are run through the Babar REPL and have the
following features:

- The Babar program can accept requests, that are then stored as
  internal commitments.
- The Babar program can be convinced of beliefs that can affect when
  and how often a request is executed.
- The Babar program has one goal - to fulfill its commitments.  It
  checks every 5ms to see if it has any commitments to fulfill and will
  execute them based on its beliefs.  (An Elephant is true 100 percent.)
- The Babar program can be queried about its commitments.  For
  example, was the request completed, what was the value, etc..
- The Babar program can speak aloud its beliefs.  Specifically, it
  will vocalize any belief that is held (evaluate to true), while it
  is fulfilling commitments.
- The Babar program remembers all the commitments that it ever had
  and they can all be queried - even cancelled ones. (An Elephant
  never forgets.)
- The Babar program can ask a question - (very experimental still).
  The only questions that it will ask currently is about undeclared vars.


Let's back up a bit and look at the basic datatypes and commands.

## Data Types
Most of the data types are directly from Clojure.  You have integers,
decimals, strings, booleans, keywords, maps, vectors, and atoms

```clojure
1     ;=> 1
2.3   ;=> 2.3
-3.4  ;=> 3.4
"cat" ;=> cat
:bird ;=> bird
true  ;=> true
{:cat :meow :dog :bark} ;=> {:cat :meow :dog :bark}
[1 2 true :bird] ;=> [1 2 true bird]
atom 1 ;=> #<Atom 1>
```
Vectors are a bit interesting in the respect that you don't need
to input the square brackets.  If you just put in space delimited
items, it will automatically construct a vector for you.

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
The interesting thing to note is that parens are optional,
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
grow in time.  Mind you parens are optional in most cases.  You can call functions
with the typical () or with a shorthand : syntax

- **def**  - def identifier expression
```clojure
  (def dog 16)
  dog ;=> 16
  def cat 18
  cat ;=> 18
```
- **defn** -  defn identifier params expression
```clojure
  (defn cat [x] (+ x 2))
  (cat 2); => 4
  defn dog [] "woof"
  dog: ;=> "woof"
```
- **if**  - if predicate truecase falsecase
```clojure
  if true :cat :dog ;=> :cat
```
- **=**, **<**,**>** -  operator val1 val2
```clojure
  = :dog :dog ;=> true
  ```
- **and** - and val1 val2 & others
```clojure
  and true true true ;=> true
  and true true false ;=> false
```
- **or**  - or val1 val2 & others
```clojure
  or true false true ;=> true
  or false false false ;=> false
```
- **import**  - import "ns"
There is basic support for importing clojure namespaces.
At this basic level it imports the whole namespace and does require
:refer :all
```clojure
  import "clojure.java.io"
```

- **println** -  println item & others

Concatenates the items as a string and prints it out to stdout

```clojure
println "cat" ;=> "cat" (returns nil)
println "cat" " " 1 " " :duck ;=> "cat 1 :duck" (returns nil)
```

- **get** -  get hash key
Gets the value of a hash by key

```clojure
get {:a 1} :a => :a
```

- **do**  - do expr expr+
Do multiple expressions

```clojure
do def s1 1
   def s2 2
s1 ;=> 1
s2 ;=> 2
```

- **sleep** - sleep ms
Sleep for given milliseconds

```clojure
sleep 5
```

- **first**  - first vec
First element of vector

````clojure
first [1 2 3] ;=> 1
```

- **swap!**  - swap atom fn
applies a fn to the atom and changes the value in a safe manner

```clojure
def x atom 1 ;=> x
swap! x inc ;=> 2
@x ;=> 2
```

- **reset!** -  swap atom val
resets the value of atom in a safe manner

```clojure
def x atom 1 ;=> x
reset! x 8 ;=> 8
@x ;=> 8
```


### Anonymous Functions
You can create anonymous functions with the fn [x] syntax from
clojure. And call them with surrounding parens.
````clojure
fn [x] + x 1 ;=> fn
(fn [x] + x 1) ;=> fn
((fn [x] + x 1) 3) ;=> 4
((fn [x y z] + x y z) 1 2 3) ;=> 6
((fn [] [4 5 6])) ;=> [4 5 6]
```

## Speech Acts
According to John Searle's
[Speech Acts](http://en.wikipedia.org/wiki/Speech_act)
There are [Illocutionary Acts](http://en.wikipedia.org/wiki/Illocutionary_act)
that involve the pragmatic meaning of a behind a sentence. Some of the
english verbs denoting these acts are "assert", "command", "request",
"query". For example the sentence, "Pass the salt.", is an
illocutionary act.  When a person hears the sentence, the meaning is
interpreted as a command.  There are also
[Perlocutionary Acts](http://en.wikipedia.org/wiki/Perlocutionary_act),
in which significance is on the statement's effect on the hearer's
actions, thoughts, and beliefs.  An example of this is "persuade" or
"convince".  Some of these speech acts have been incorporated into the
language.  So far there is support for:

### Datatypes
- **Commitment** -  *name

A commitment is a datatype designated by a *name

```clojure
*bark
```

- **Belief**  - #name

A belief is a datatype designated by a #name

```clojure
#sunny
```

### Convincing

- **convince**  - convinced belief string predicate-function

To be convinced will create an internal belief that has a human
readable string as a description and a predicate function that
evaluates to true when the machine "believes" it.

```clojure
convince #sunny "It is sunny" fn [x] (= 1 1)
```

### Requests
- **request**  - request commitment function

Accepting a request creates an internal commitment that is evaluated
at a future time.  Behind the scenes there is a cron-like watcher
that continually sees if it has any commitments to execute.  If there
is an error that occurs, then it will have an error captured that you
can query by using "query request-errors".

```clojure
request *dog fn [] :bark ;=> babar.speech_acts.Commitment
```

- **request when** -  request commitment when belief function

You can also specify a request to be executed when a belief is held.
The request is executed when the belief predicate function evaluates
to true.
```clojure
convince #too-warm "It is too warm." fn [] > temperature 70
request *lower-temp when #too-warm fn [] :lower-the-temp-action
```

- **request until** -  request commitment until belief function

You can specify a request to be executed until a belief is held.
The request will continue to execute until the belief is held.
```clojure
convince #just-right "It is just-right" fn [] > @temp 70
request *raise-temp until #just-right fn [] (increase-temp)
```

- **request when until** -  request commitment when belief until function

You can specify a request to be executed when a belief is held and
until another belief is held.

````clojure
convince #just-right "It is just-right" fn [] > @temp 70
convince #start "Time to start" fn [] > @temp 68
request *raise-temp when #start until #just-right fn [] (increase-temp)
````

- **request ongoing** -  request commitment ongoing function

You can specify a request to be executed repeatedly with no end.

```clojure
request *count ongoing fn [] (inc-x1)
```

- **request when ongoing** - request commitment when belief ongoing function

You can specify a request to be executed repeatedly with no end, when
a belief is true.

```clojure
convince #start \"Time to start\" fn [] = y2 2
request *count when #start ongoing fn [] (inc-x1)
```

- **cancel-request**  - cancel-request request

You can cancel a request.  The request itself is still remembered and
can be queried, but it will not be executed.

```clojure
cancel-request *dog
```

### Answering Queries
* query

Answering questions about requests, beliefs and values.

- **query** -
request-[fn | completed | value | errors | created | when | until | is-done | cancelled | ongoing]
request)

```clojure
request *dog fn [] :bark.
query request-value *dog ;=> :bark
query request-completed *dog? ;=> "2013-05-17T19:58:07.882"
query request-is-done? ;=> true
```

- **query belief-[str | fn ]**

```clojure
convince #sunny "It is sunny" fn [] = 1 1 ;=> belief
query belief-str #sunny ;=> "It is sunny"
query belief-fn #sunny ;=> function
```

- **query requests-all**

```clojure
request *step1 fn [] + 1 1 ;=> commitment
request *step2 fn [] + 2 2 ;=> commitment
query requests-all ;=>  [:step1 :step2]
```

- **query beliefs-all**

```clojure
convince #sunny "It is sunny" fn [] = 1 1 ;=> belief
convince #rainy "It is rainy" fn [] = 1 2 ;=> belief
query beliefs-all? ;=> [:sunny :rainy]
```

- **query value identifier**

You can ask what the value of a identifier is

```clojure
assert x 1 ;=> x
query value x ;=> 1
```

### Asking Queries
*Experimental*

You can ask queries are well as answering them. Asking a query is
manifested as a side effect - a printed speech act.  Right now the
statement prints on the REPL console.  It always could be directed to
an external file that another system could read...

- **ask-query** identifier

```clojure
ask-query what-is-this ;=> query what-is-this.
```

- **ask-config** true | false

This configures the repl to automcatically ask questions about unbound
vars.

```clojure
ask-config true
```

The REPL will also respond with an ask-query if you define
a function with a undeclared variable. You need to config to ask
questions automatically first.

```clojure
ask-config true
assert cat [] + x 1 ;=> query x.
assert cat x 2 ;=> x
cat: ;=> 3
```

Even cooler - if the speak-beliefs flag is true, it will also
speak the query aloud as well :)

### Speaking the Beliefs using Say
* **speak-config**
- speak-config [true | false ]
- speak-config true voice-name

```clojure
speak-config true ;=> default voice
speak-config true "Zarvox" ;=> speak with Zarvox
```

If you toggle on the speak-beliefs, then (if you have a mac and say),
then any beliefs will be spoken aloud when there belief fns evaluate
to true - or the beliefs are being held.  If there are multiple
beliefs, (like using an until), then it will only speak when the
belief changes.

## Reading babar programs
* **read** - read filename

This command will read a *.babar file into the repl and evaluate it
it.  A program is composed of multiple expressions that are delimited
by a period or a question mark. Question marks can of course be used
for queries.

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

or run the standalone shell script

   ./bin/babar.sh

## Examples
There are a couple of examples to get you going with playing with it.
To run the examples:

- launch a repl using ```'lein run'``` or ```./bin/babar.sh'```

At the babar repl prompt

````
read "./examples/simple.babar"
```

There are also other example programs.
 - examples/speech_acts.babar
 - examples/requests_until.babar
 - examples/asking.babar

The programs has the speak-config
set to true.  This will work fine if you have a mac, just turn it to
false if you are on another system.

Have fun!

## Videos

I made a few videos to show Babar in action

- An example of the Babar REPL speaking beliefs with requests
  [video](https://www.youtube.com/watch?v=bt2iYsVyCOM)
- An example of Babar REPL using a request with an until belief
  [video] (https://www.youtube.com/watch?v=aT8MK0w71LM)
- An example of Babar REPL asking you a question about an undeclared
  var [video] (https://www.youtube.com/watch?v=aT8MK0w71LM)
- An example of Babar REPL flying an AR Drone with Speech Acts [video] (https://www.youtube.com/watch?v=CIzR8jD2d3c)

## TESTS

    lein midje

## Ascii Art
  The lovely ascii elepant is a modified version of the one found [here](http://www.retrojunkie.com/asciiart/animals/elephant.htm)

## License

Copyright © 2013 Carin Meier

Distributed under the Eclipse Public License, the same as Clojure.
