
(ns app.rss
  (:require-macros 
    [cljs.core.async.macros :refer [go]])
  (:require
    [cljs.core.async :refer [<!]]
    ["node-http-xhr" :as XMLHttpRequest]
    [cljs-http.client :as http]
    [tubax.core :refer [xml->clj]]))
;  

(set! js/XMLHttpRequest XMLHttpRequest)

;; https://github.com/r0man/cljs-http
(defn get-xml [url]
  (go
    (let [result (<! (http/get url))]
      (xml->clj (:body result)))))
;

(defn title-link [tags]
  (reduce
    (fn [a b]
      (condp = (:tag b)
        :id    (assoc a :id (first (:content b)))
        :title (assoc a :title (first (:content b)))
        :link  (assoc a :link (-> b :attributes :href))
        a))
    {}
    tags))
;

(defn entries [xml]
  (->>
    (:content xml)
    (filter #(= :entry (get % :tag)))
    (map #(title-link (:content %)))))
;

;;.