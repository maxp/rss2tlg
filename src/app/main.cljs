(ns app.main
  (:require-macros 
    [cljs.core.async.macros :refer [go]])
  (:require
    [cljs.core.async :refer [<! take!]]
    ;
    [mlib.conf :refer [config-merge config-subst]]
    [app.rss :refer [get-xml entries]]
    [app.feed :refer [load save]]))
;
    
                
(def RSS "https://www.youtube.com/feeds/videos.xml?channel_id=UClTIrwj5npeOaBjH6_AkKyA")

; (defn reload! []
;   (prn "reload"))

(def DATA_FILE "./feed-data.edn")

(defn exit [c]
  (prn "exit:" c)
  (js/process.exit 0))
;

(defn main! []
  (prn "main!")
  (let [c
        (->
          [{:a :b} {:c "${C}"}]
          (config-merge)
          (config-subst {"C" "new C"}))]

   (prn "main:" c))


  (let [data (load DATA_FILE)])

  (let [r 
            (go
              (let [xml (<! (get-xml RSS))]
                (prn "entries:" (entries xml))))]
                ;; get first entry that is not in data_file
                ;; send notify
                ;; save data_file + entry - oldest
                ;; exit
    (take! r exit)))
;

(comment 
  (main!)
  (+ 1 2)
  .)

;.
