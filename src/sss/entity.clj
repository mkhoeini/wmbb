(ns sss.entity
  (:require
   [clojure.core.async :as async]
   [datascript.core :as d]
   [integrant.core :as ig]
   [sss.db :as db]
   [sss.event :as ev]
   [sss.sys-ref :refer [*system*]]))



(defmethod ig/init-key ::init [_ {:keys [archetypes db-conn] {:keys [entities]} :cfg}]
  (let [tx (for [[arch ents] entities
                 ent ents]
             (assoc ent ::archetype (get-in archetypes [arch :db/id])))
        tx-res (apply db/transact! db-conn tx)]
    (into {} (for [[arch ents] entities]
               [arch (into [] (for [ent ents]
                                (d/entity @db-conn (get-in tx-res [:tempids (:db/id ent)]))))]))))


(defn- get-events-of-behavior [behavior]
  (->> behavior
       :sss.behavior/events
       (map :sss.event/name)
       set))


(defn- get-behaviors-for-event [entity event]
  (let [tags (get-in entity [::archetype :sss.archetype/tags])
        behaviors (mapcat #(:sss.behavior/_tags %) tags)
        behaviors (filter #(contains? (get-events-of-behavior %) event) behaviors)]
    (set behaviors)))


(defmethod ig/init-key ::event-consumer [_ {:keys [db-conn events]}]
  (async/thread
    (loop []
      (when-let [event (async/<!! (ev/get-event-chan events))]
        #_(tap> ["got event" event])
        (let [behaviors (get-behaviors-for-event (:target event) (:name event))
              system (:sss/system (meta event))]
          (doseq [behavior behaviors
                  :let [bname (:sss.behavior/name behavior)
                        reaction (:sss.behavior/reaction behavior)
                        tx (try
                             (binding [*system* system]
                               (reaction event))
                             (catch Exception e
                               (.printStackTrace e)
                               nil))]]
            #_(tap> ["run behavior:" bname "event:" event "result:" tx])
            (apply db/transact! db-conn tx)))
        (recur)))))
