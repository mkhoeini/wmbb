(ns sss.behavior
  (:require
    [sss.db :as db]
    [sss.event :as ev]))



(def behavior-fn (atom {}))


(defn init! [system behaviors]
  (let [tx (for [[bname bdef] behaviors]
             {::name bname
              ::events (for [ev (:events bdef)] [::ev/name ev])})]
    (apply db/transact! system tx))
  (doseq [[bname {bfn :fn}] behaviors]
    (swap! behavior-fn assoc-in [system bname] bfn)))
