(ns sss.tag
  (:require
   [integrant.core :as ig]
   [sss.db :as db]
   [datascript.core :as d]))



(defmethod ig/init-key ::tags [_ {:keys [db-conn] {:keys [tags]} :cfg}]
  (let [tx (for [tag tags]
             {:db/id (str tag)
              ::name tag})
        tx-res (apply db/transact! db-conn tx)]
    (into [] (for [tag tags]
               (d/entity @db-conn (get-in tx-res [:tempids (str tag)]))))))
