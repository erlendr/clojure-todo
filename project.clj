(defproject clojure-todo "0.1.0-SNAPSHOT"
  :description "Clojure-Todo"
  :url "http://github.com/erlendr/clojure-todo"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [
                 [org.clojure/clojure "1.5.1"]
                 [compojure "1.1.6"]
                 [hiccup "1.0.4"]
                 [fleetdb-client "0.2.2"]
                 [cheshire "5.2.0"]
                 ]
  :plugins [[lein-ring "0.8.8"]]
  :ring {:handler clojure-todo.handler/app}
  :profiles
  {:dev {:dependencies [[ring-mock "0.1.5"]]}})
