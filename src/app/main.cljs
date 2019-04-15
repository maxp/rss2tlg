
(ns app.main
  (:require-macros 
    [cljs.core.async.macros :refer [go]])
  (:require
    [cljs.core.async :refer [<! take!]]
    [cljs.reader :refer [read-string]]
    ["fs" :refer [readFileSync]]
    ;
    [mlib.conf :refer [config-merge config-subst]]
    ;
    [app.config :refer [config]]
    [app.data :refer [load save]]
    [app.rss :refer [get-xml entries]]))
;

(def DEFAULT_CONFIG_FILE "./rss2telegram.edn")

(def env (.-env js/process))

(defn send [cfg item]
  (prn ">>>>")
  (prn "apikey/channel:" (:apikey cfg) (:channel cfg))
  (prn "send:" item)
  (prn "<<<<"))
;

(defn notify [cfg data items]
  (let [data-max (int (:data-max cfg))]
    (loop [[f & r] items d data]
      (when f
        (prn "i:" f)
        (let [new-data (->> (conj d f) (take data-max) vec)]
          (save (:data-file cfg) new-data)  
          (send cfg f)
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
  ;; TODO: validate config
  (prn "cfg:" cfg)
  (go
    (let [data (load (:data-file cfg))
          idx (data-idx data)
          items
            (->> (<! (get-xml (:rss-url cfg)))
              (entries)
              (take (int (:rss-max cfg)))
              (sort-by :published)
              (remove #(get idx (:id %)))
              (take (int (:rss-num cfg)))
              (notify cfg data))])))
;

(defn get-config []
  (config-subst
    (if-let [cfile (or (get env "CONFIG_EDN") DEFAULT_CONFIG_FILE)]
      (config-merge [config (-> cfile readFileSync str read-string)])
      config)
    env))
;

(defn main! []
  (take! 
    (process-feed (get-config))
    #(js/process.exit 0)))
;

;;.
