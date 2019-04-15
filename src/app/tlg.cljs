(ns app.tlg
  (:require-macros 
    [cljs.core.async.macros :refer [go]])
  (:require
    [cljs.core.async :refer [<!]]
    [cljs-http.client :as http]))
    ; ["node-http-xhr" :as XMLHttpRequest]
;

;; NOTE: already set in rss.cljs
; (set! js/XMLHttpRequest XMLHttpRequest)

(defn api-url [token method]
  (str "https://api.telegram.org/bot" token "/" (name method)))
;

(defn api [token method body]
  (go
    (try
      (let [url (api-url token method)
            result (<! (http/post url {:json-params body}))]
        (:body result))
      (catch :default ex
        (js/console.error "tg.api error")))))
;

(defn esc [text]
  (-> (str text) 
    (.replace "&" "&amp;")
    (.replace "<" "&lt;")
    (.replace ">" "&gt;") 
    (.replace "\"" "&quot;")))
;

(defn send-message [cfg chat text]
  ;; <!
  (api cfg :sendMessage {:chat_id chat :text text :parse_mode "HTML"}))
;
  
