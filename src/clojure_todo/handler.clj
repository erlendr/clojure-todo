(ns clojure-todo.handler
  (:use
   compojure.core
   hiccup.core
   hiccup.page
   hiccup.form
   cheshire.core
   [monger.core :only [connect! connect-via-uri! connect set-db! get-db]]
   [monger.collection :only [insert find-maps find-one-as-map update-by-id remove-by-id]]
   monger.operators
   [ring.adapter.jetty :as ring]
   )
(:require [compojure.route :as route]
          [compojure.handler :as handler]
          [cheshire.core :refer :all]
          [cheshire.generate :refer [add-encoder encode-str remove-encoder]]
          monger.json
          )
(:import
 [com.mongodb MongoOptions ServerAddress]
 [org.bson.types ObjectId]
 [com.mongodb DB WriteConcern]
 )
)

(add-encoder com.mongodb.WriteResult (fn [c jsonGenerator] (.writeString jsonGenerator(.toString c))))

(def mongo-uri "mongodb://clojure-todo:clojure-todo@ds057548.mongolab.com:57548/heroku_app20119486")

(connect-via-uri! mongo-uri)

(def client (connect))
(defn select-all-tasks [] (find-maps "clojure-todo"))

(defn select-task [id] (find-one-as-map "clojure-todo" { :_id (ObjectId. id) }))

(defn update-task-status [id status]
  (update-by-id "clojure-todo" (ObjectId. id) {$set {:done? status } }))

(defn delete-task [id] (remove-by-id "clojure-todo" (ObjectId. id)))

(defn insert-task [task]
  (insert "clojure-todo" {:task (hiccup.util/escape-html task) :done? false})
  (str "task added")
)

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
      [:h3 (task :task)]

      [:div.btn-group
      (if (task :done?)
        [:button.btn.btn-success {:data-task-id (str (task :_id)) }
         [:span.glyphicon.glyphicon-ok-sign]]
        [:button.btn.btn-default {:data-task-id (str (task :_id))}
         [:span.glyphicon.glyphicon-ok-circle]] 
        )
        [:button.btn.btn-danger.task-delete {:data-task-id (str (task :_id)) }
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
  (GET "/tasks" [] (generate-string (select-all-tasks)))
  (GET "/tasks/:id" [id] (generate-string (select-task id)))
  (POST "/tasks/update-status" {params :params}
        (generate-string
         (update-task-status
          (params :id)
          (read-string (params :done))
          )
         ))
  (POST "/tasks" {params :params} (insert-task (params :task)))
  (DELETE "/tasks/:id" [id] (generate-string (delete-task id)))
  (route/resources "/")
  (route/not-found (template "" "Not Found")))

(def app
  (handler/site app-routes))

(defn start [port]
  (run-jetty #'app {:port port :join? false}))

(defn -main [port]
  (start (Integer/parseInt port)))
