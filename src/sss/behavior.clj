(ns sss.behavior
  (:require
    [sss.db :as db]
    [sss.event :as ev]
    [integrant.core :as ig]
    [datascript.core :as d]))



(defmethod ig/init-key ::behaviors [_ {:keys [db-conn] _dummy :events {:keys [behaviors]} :cfg}]
  (let [tx (for [[bname bdef] behaviors]
             {:db/id (str bname)
              ::name bname
              ::events (for [ev (:events bdef)] [::ev/name ev])
              ::reaction (:reaction bdef)})
        tx-res (apply db/transact! db-conn tx)]
    (into {} (for [bname (keys behaviors)]
               [bname (d/entity @db-conn (get-in tx-res [:tempids (str bname)]))]))))
