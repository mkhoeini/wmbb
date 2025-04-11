(ns sss.subscription
  (:require
    [sss.db :as db]))



(defmacro defsub [signal sub-name binding filter-expr map-expr]
  `(def ~sub-name
     {::name ~(keyword (str (ns-name *ns*)) (str sub-name))
      ::signal ~signal
      ::filter (fn ~sub-name ~binding ~filter-expr)
      ::map (fn ~sub-name ~binding ~map-expr)}))


(defn add-sub [system signal sub conf]
  (db/transact))
