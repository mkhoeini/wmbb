(ns sss.tag
  (:require
   [sss.behavior :as be]
   [integrant.core :as ig]
   [sss.db :as db]
   [datascript.core :as d]))



(defmethod ig/init-key ::tags [_ {:keys [db-conn] _dummy :behaviors {:keys [tags]} :cfg}]
  (let [tx (for [[tag bs] tags]
             {:db/id (str tag)
              ::name tag
              ::behaviors (for [b bs] [::be/name b])})
        tx-res (apply db/transact! db-conn tx)]
    (into {} (for [tag (keys tags)]
               [tag (d/entity @db-conn (get-in tx-res [:tempids (str tag)]))]))))
