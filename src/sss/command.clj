(ns sss.command
  (:require
    [integrant.core :as ig]
    [sss.db :as db]
    [datascript.core :as d]))



(defmethod ig/init-key ::commands [_ {:keys [db-conn] {:keys [commands]} :cfg}]
  (let [tx (for [[cmd {:keys [retry exec]}] commands]
             {:db/id (str cmd)
              ::name cmd
              ::retry-fn retry
              ::exec-fn exec})
        tx-res (apply db/transact! db-conn tx)]
    (into {} (for [cmd (keys commands)]
               [cmd (d/entity @db-conn (get-in tx-res [:tempids (str cmd)]))]))))
