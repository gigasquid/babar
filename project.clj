(defproject babar "0.1.0"
  :description "A little language for machines based on the speech acts"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :main babar.repl
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [instaparse "1.1.0"]
                 [clj-time "0.5.0"]
                 [me.raynes/conch "0.5.0"]
                 [clj-drone "0.1.4"]]
  :profiles {:dev {:dependencies [[midje "1.4.0"]]
                   :plugins [[lein-midje "2.0.1"]] }})
