(defproject babar "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :main babar.repl
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [instaparse "1.0.1"]
                 [clj-time "0.5.0"]]
  :profiles {:dev {:dependencies [[midje "1.4.0"]]
                   :plugins [[lein-midje "2.0.1"]] }})
