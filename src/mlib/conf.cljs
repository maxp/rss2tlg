;;
;;  mlib: configuration methods
;;

(ns mlib.conf
  (:require
    [clojure.walk :refer [postwalk]]))
;

;; ;; ;; substitute vars ;; ;; ;;

(defn- subst-dollar-vars 
  "substitute string parameters like \"${VAR}\" or \"${VAR:default}\" by os environment variables,
   only the whole parameter value could be replaced with the value of environment variable
  "
  [parameter subst-map]
  (if (string? parameter)
    (let [  [_ var _ default] 
            (re-matches #"^\S\{([^:]+)(\:(.*))?\}" parameter)]
      (if var
        (or        
          (get subst-map var) default "")
        parameter))
    parameter))
;

;; ;; ;; deep merge ;; ;; ;;

(defn- deep-merge* [& maps]
  (let [f (fn [old new]
            (if (and (map? old) (map? new))
                (merge-with deep-merge* old new)
                new))]
    (if (every? map? maps)
      (apply merge-with f maps)
      (last maps))))
;

(defn- deep-merge [& maps]
  (let [maps (filter identity maps)]
    (assert (every? map? maps))
    (apply merge-with deep-merge* maps)))
;

;; ;; ;;  config methods  ;; ;; ;;

(defn config-merge 
  "deep merge config maps"
  [cfgs]
  (apply deep-merge cfgs))
;

(defn config-subst 
  "substitute ${VAR} parameters in config tree using env values"
  [cfg env]
  (postwalk 
    #(subst-dollar-vars %1 env)
    cfg))
;


(comment
  (def subst 
    { "VAR1" "qwe123" 
      "VAR2" nil
      "EMPTY" ""})

  ($subst "${VAR1}" subst)          ;; "qwe123"
  ($subst "${VAR2}" subst)          ;; ""
  ($subst "${VAR3}" subst)          ;; ""
  ($subst "${VAR3:default3}" subst) ;; default3
  ($subst "${EMPTY}" subst)         ;; ""

  ($subst 123 subst)   ;; 123 
  ($subst :a subst)    ;; :a

  .)
;

;;.
