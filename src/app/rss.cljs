
(ns app.rss
  (:require-macros 
    [cljs.core.async.macros :refer [go]])
  (:require
    [cljs.core.async :refer [<!]]
    ["crypto" :refer [createHash]]
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

(defn parse-date [s]
  (when s
    (let [d (js/Date. s)]
      (when-not (js/isNaN d)
        d))))
;

(defn make-id [item]
  (-> (createHash "md5")
    (.update (str (:title item) (:link item)))
    (.digest "hex")))
;

(defn entry [tags]
  (let [item  (reduce
                (fn [a b]
                  (condp = (:tag b)
                    :id    (assoc a :id (first (:content b)))
                    :title (assoc a :title (first (:content b)))
                    :link  (assoc a :link (-> b :attributes :href))
                    :published (assoc a :published (-> b :content first parse-date))
                    :updated   (assoc a :updated   (-> b :content first parse-date))
                    a))
                {}
                tags)]
    (if (:id item)
      item
      (assoc item :id (make-id item)))))
;

(defn entries [xml]
  (->>
    (:content xml)
    (filter #(= :entry (get % :tag)))
    (map #(entry (:content %)))))
;

;;.