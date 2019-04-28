
(ns app.main
  (:require-macros 
    [cljs.core.async.macros :refer [go go-loop]])
  (:require
    [cljs.core.async :refer [<! take!]]
    [cljs.reader :refer [read-string]]
    ["fs" :refer [readFileSync]]
    ;
    [mlib.conf :refer [config-merge config-subst get-env]]
    ;
    [app.config :refer [config]]
    [app.data :refer [load save]]
    [app.rss :refer [get-xml entries]]
    [app.tlg :refer [esc send-message]]))
;

;; NOTE: duplicated in package.json
(def VERSION "rss2tlg 0.4")

(def DEFAULT_CONFIG_FILE "./rss2tlg.edn")


(defn send [cfg item]
  (let [token   (:apikey cfg)
        channel (:channel cfg)
        text    (str "<b>" (esc (:title item)) "</b>\n" 
                           (esc (:link item)))]
    (prn "send item:" item)
    (send-message token channel text)))
;

(defn notify [cfg data items]
  (let [data-max (int (:data-max cfg))]
    (go-loop [[f & r] items 
               d      data]
      (when f
        (let [new-data (->> (conj d f) (take data-max) vec)]
          (save (:data-file cfg) new-data)  
          (let [rc (<! (send cfg f))]
            (prn "send.rc:" rc))
          (when r
            (recur r new-data)))))))
;

(defn data-idx [data]
  (prn "data-loaded:" (count data))
  (reduce
    (fn [a b]
      (assoc a (:id b) b))
    {}
    data))
;

(defn process-feed [cfg]
  ;; TODO: make spec for config
  (prn "cfg:" cfg)
  (go
    (let [data  (load (:data-file cfg))
          idx   (data-idx data)
          xml   (<! (get-xml (:rss-url cfg)))
          items (->> xml
                  (entries)
                  (take (int (:rss-max cfg)))
                  (sort-by :published)
                  (remove #(get idx (:id %)))
                  (take (int (:rss-num cfg))))]
      (<! (notify cfg data items)))))
;

(defn get-config []
  (let [env (get-env)]
    (config-subst
      (if-let [cfile (or (get env "CONFIG_EDN") DEFAULT_CONFIG_FILE)]
        (config-merge [config (-> cfile readFileSync str read-string)])
        config)
      env)))
;

(defn main! []
  (js/console.log VERSION "-" (js/Date.))
  (take! 
    (process-feed (get-config))
    #(js/process.exit 0)))
;

;;.
