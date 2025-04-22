(ns sss.entity
  (:require
    [integrant.core :as ig]
    [sss.archetype :as arch]
    [sss.db :as db]
    [sss.event :as ev]
    [datascript.core :as d]
    [clojure.core.async :as async]))



(defmethod ig/init-key ::init [_ {:keys [archetypes db-conn] {:keys [entities]} :cfg}]
  (let [tx (for [[arch ents] entities
                 ent ents]
             (assoc ent ::archetype (get-in archetypes [arch :db/id])))
        tx-res (apply db/transact! db-conn tx)]
    (into {} (for [[arch ents] entities]
               [arch (into [] (for [[_ i] (map vector ents (range))]
                                (d/entity @db-conn (get-in tx-res [:tempids (str arch i)]))))]))))


(defmethod ig/init-key ::event-consumer [_ {:keys [events]}]
  (async/thread
    (loop []
      (when-let [{::ev/keys [name target data]} (async/<!! (ev/get-event-chan events))]
        (tap> ["got event" name target data])
        (recur)))))
