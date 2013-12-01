(ns hello-world.core
(:use
   compojure.core
   hiccup.core
   hiccup.page
   hiccup.form
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
(require 'hiccup.form)

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
      
      (if (task :done?)
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

(def index (template (html
                      [:title "Clojure TODO!"]
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
                         {:href="/"} "Clojure Todo"]
                         ]
                        [:div.collapse.navbar-collapse]
                        ]
                       ]
                      [:div.container
                       [:div.starter-template
                        [:h1 "Clojure Todo"]
                        [:p.lead "All tasks:"]
                        (task-template todos)
                        (form-to {:class "form-inline" :role "form"} [:task "/post"]
                                 [:div.form-group
                                  (label "task" "Task:")
                                  (text-area {:class "form-control" :placeholder "Enter task text"} "task")
                                  (submit-button {:class "btn btn-default"} "Add task")
                                 ]
                        )
                        ]
                       ]
                       (include-js "https://code.jquery.com/jquery.js")
                       (include-js "/js/bootstrap.min.js")
                     )))

(def response {:html index })

(defroutes app-routes
  (GET "/" [] (response :html))
  (GET "/task" [id] id)
  (route/resources "/")
  (route/not-found (template "" "Not Found")))

(def app
  (handler/site app-routes))
