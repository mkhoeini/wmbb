(ns sss.behavior
  (:require
    [sss.db :as db]
    [sss.event :as ev]
    [sss.tag :as tag]
    [integrant.core :as ig]
    [datascript.core :as d]))



(defmethod ig/init-key ::behaviors [_ {:keys [db-conn]
                                       _dummy :events
                                       _dummy2 :tags
                                       {:keys [behaviors]} :cfg}]
  (let [tx (for [[bname {:keys [events tags reaction]}] behaviors]
             {:db/id (str bname)
              ::name bname
              ::events (for [ev events] [::ev/name ev])
              ::tags (for [t tags] [::tag/name t])
              ::reaction reaction})
        tx-res (apply db/transact! db-conn tx)]
    (into {} (for [bname (keys behaviors)]
               [bname (d/entity @db-conn (get-in tx-res [:tempids (str bname)]))]))))
