# babar

A Programming Language with Speech Acts inspired by
[Elephant 2000](http://www-formal.stanford.edu/jmc/elephant/elephant.html).
The parser uses the wonderful Clojure
[Instaparse](https://github.com/Engelberg/instaparse) library

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
1 2 3 4 => [1 2 3 4]
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
- import
There is basic support for importing clojure namespaces.
At this basic level it imports the whole namespace and does require
:refer :all
```clojure
  import "clojure.java.io"
  parse file
```

### Anonymous Functions
You can create anonymous functions with the fn [x] syntax from
clojure. And call them with surrounding parens.
````clojure
   ((fn [x] + x 1) 3) ;=> 4
   ((fn [x y z] + x y z) [1 2 3]) ;=> 6
   ((fn [] [4 5 6]))") ;=> [4 5 6]
```

## REPL
Launch a REPL

    lein run

## TESTS

    lein midje


## License

Copyright © 2013 Carin Meier

Distributed under the Eclipse Public License, the same as Clojure.
