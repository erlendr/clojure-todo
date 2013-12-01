(ns hello-world.core
(:use
   compojure.core
   hiccup.core
   hiccup.page
   )
(:require [compojure.route :as route]
          [compojure.handler :as handler]
          )
)

(require 'clojure.pprint)
(require 'compojure.core)
(require 'compojure.route)
(require 'hiccup.core)
(require 'hiccup.page)

(def todos [
            {:task "Task 1" :done? false }
            {:task "Task 2" :done? true }
            {:task "Task 3" :done? false }
            ])

(defn template [head, body]
  (html5 
         [:head head]
         [:body body]
        )
  )

(defn task-template [tasks]
  (for [task tasks]
    (html
     [:div 
      [:h3 (task :task)]
      [:p "Done? " (task :done?)]
     ] 
     )
    )
  )

(def index (template "" (html
                         [:h1 "TODO!"]
                         [:p "All tasks:"]
                         (task-template todos)
                         )))

(def response {:html index })

(defroutes app-routes
  (GET "/" [] (response :html))
  (route/resources "/")
  (route/not-found (template "" "Not Found")))

(def app
  (handler/site app-routes))
