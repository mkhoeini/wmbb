(ns sss.reconciler
  (:require
    [integrant.core :as ig]
    [sss.db :as db]
    [datascript.core :as d]))



(defmethod ig/init-key ::reconcilers [_ {:keys [db-conn] {:keys [reconcilers]} :cfg}]
  (let [tx (for [[r-name {:keys [archetype dirty? to-cmds]}] reconcilers]
             {:db/id (str r-name)
              ::name r-name
              ::archetype archetype
              ::dirty? dirty?
              ::to-cmds to-cmds})
        tx-res (apply db/transact! db-conn tx)]
    (into {} (for [r-name (keys reconcilers)]
               [r-name (d/entity @db-conn (get-in tx-res [:tempids (str r-name)]))]))))
