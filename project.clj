(defproject hello-world "0.1.0-SNAPSHOT"
  :description "Hello world!"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [
                 [org.clojure/clojure "1.5.1"]
                 [compojure "1.1.6"]
                 [hiccup "1.0.4"]
                 ]
  :plugins [[lein-ring "0.8.8"]]
  :ring {:handler hello-world.core/app}
  :profiles
  {:dev {:dependencies [[ring-mock "0.1.5"]]}})
