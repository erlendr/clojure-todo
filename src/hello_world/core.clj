(ns hello-world.core
(:use
   compojure.core
   hiccup.core
   hiccup.page
   hiccup.form
   fleetdb.client
   )
(:require [compojure.route :as route]
          [compojure.handler :as handler]
          clojure.contrib.logging
          )
)

(require 'clojure.pprint)
(require 'compojure.core)
(require 'compojure.route)
(require 'hiccup.core)
(require 'hiccup.page)
(require 'hiccup.form)
(require 'fleetdb.client)
(require 'clojure.contrib.logging)

(def client (connect))

(def todos [
            {:task "Task 1" :done? false }
            {:task "Task 2" :done? true }
            {:task "Task 3" :done? false }
            ])

(defn select-all-tasks [] (client ["select" "tasks"]))       
                                 

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
      [:h3 (task "task")]
      
      (if (task "done?")
        [:button.btn.btn-success
         [:span.glyphicon.glyphicon-ok-sign]
        ]
        [:button.btn.btn-default
         [:span.glyphicon.glyphicon-ok-circle]
        ]
      )
     ] 
     )
    )
  )

(defn index [] (template (html
                      [:title "Clojure Todo"]
                      [:meta {
                              :name "viewport"
                              :content "width=device-width, initial-scale=1.0"
                              }
                      ]
                      [:link
                       {:href "http://fonts.googleapis.com/css?family=Open+Sans:400italic,400,700"
                        :rel "stylesheet"
                        :type "text/css"}
                       ]
                      (include-css "/css/bootstrap.css")
                      (include-css "/css/bootstrap-theme.css")
                      (include-css "/css/screen.css")
                      )
                     (html
                      [:div.navbar.navbar-inverse.navbar-fixed-top {:role "navigation"}
                       [:div.container
                        [:div.navbar-header
                         [:a.navbar-brand
                         {:href "/"} "Clojure Todo"]
                         ]
                        [:div.collapse.navbar-collapse]
                        ]
                       ]
                      [:div.container
                       [:div.starter-template
                        [:h1 "Clojure Todo"]
                        [:p.lead "All tasks:"]
                        (task-template (select-all-tasks))
                        (form-to {:class "form-inline" :role "form"} [:post "/task"]
                                 [:div.form-group
                                  (label "task" "Task:")
                                  (text-area {:class "form-control" :placeholder "Enter task text" :id "task" } "task")
                                  (submit-button {:class "btn btn-default"} "Add task")
                                 ]
                                 )
                          ]
                       ]
                       (include-js "https://code.jquery.com/jquery.js")
                       (include-js "/js/bootstrap.min.js")
                     )))

(defn insert-task [task]
  (if (empty? (select-all-tasks))
    (def id 1)
    (def id (inc ((first (select-all-tasks)) "id")))
  )
  (client ["insert" "tasks" {:id id :task task :done? false}])
  (html [:p "Task added with id: " id] )
  )
(defroutes app-routes
  (GET "/" [] (index))
  (GET "/api/task" [] (str (select-all-tasks)))
  (POST "/task" {params :params} (insert-task (params :task)))
  (route/resources "/")
  (route/not-found (template "" "Not Found")))

(def app
  (handler/site app-routes))
