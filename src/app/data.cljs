
(ns app.data
  (:require
    [cljs.reader :refer [read-string]]
    ["fs" :refer [readFileSync writeFileSync]]))
    
;

(defn load [fname]
  (prn "load: fname" fname)
  (-> fname readFileSync str read-string vec))
;


(defn save [fname data]
  (prn "save:" fname (count data))
  (->> data pr-str (writeFileSync fname)))
;

;;.
