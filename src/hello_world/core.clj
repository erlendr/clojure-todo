(ns hello-world.core
(:use
   compojure.core
   hiccup.core
   hiccup.page
   hiccup.form
   fleetdb.client
   cheshire.core
   )
(:require [compojure.route :as route]
          [compojure.handler :as handler]
          [cheshire.core :refer :all]
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

(defn insert-task [task]
  (if (empty? (select-all-tasks))
    (def id 1)
    (def id (inc ((first (select-all-tasks)) "id")))
  )
  (client ["insert" "tasks" {:id id :task task :done? false}])
  (html [:p "Task added with id: " id] )
  )

(defn select-all-tasks [] (client ["select" "tasks"]))       

(defn select-task [id] (first (client ["select" "tasks" {"where" ["=" "id" id]}])))

(defn update-task-status [id status] (client ["update" "tasks" {:done? status} {"where" ["=" "id" id]}])) 

(defn delete-task [id] (client ["delete" "tasks" {"where" ["=" "id" id]} ]))

(defn template [head, body]
  (html5 
         [:head head]
         [:body body]
        )
  )

(defn task-template [tasks]
  (for [task tasks]
    (html
     [:div.task
      [:h3 (task "task")]

      [:div.btn-group
      (if (task "done?")
        [:button.btn.btn-success {:data-task-id (task "id") }
         [:span.glyphicon.glyphicon-ok-sign]]
        [:button.btn.btn-default {:data-task-id (task "id")}
         [:span.glyphicon.glyphicon-ok-circle]] 
        )
        [:button.btn.btn-danger.task-delete {:data-task-id (task "id") }
         [:span.glyphicon.glyphicon-remove]]
       ]
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
                        (form-to {:class "form-inline" :role "form"} [:post "/tasks"]
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
                       (include-js "/js/site.js")
                     )))

(defroutes app-routes
  (GET "/" [] (index))
  (GET "/tasks" [] (str (select-all-tasks)))
  (GET "/tasks/:id" [id] (generate-string (select-task (read-string id))))
  (POST "/tasks/update-status" {params :params}
        (generate-string
         (update-task-status
          (read-string (params :id))
          (read-string (params :done))
          )
         ))
  (POST "/tasks" {params :params} (insert-task (params :task)))
  (DELETE "/tasks/:id" [id] (generate-string (delete-task (read-string id))))
  (route/resources "/")
  (route/not-found (template "" "Not Found")))

(def app
  (handler/site app-routes))
