(ns sss.entity
  (:require
   [clojure.core.async :as async]
   [datascript.core :as d]
   [integrant.core :as ig]
   [sss.db :as db]
   [sss.event :as ev]
   [sss.sys-ref :refer [*system*]]
   [sss.utils :refer [spy]]))



(defn make-init-entity [archetype id-map state desired]
  (let [a-name (name archetype)
        id-ns (str "sss.entity." a-name)
        ids (for [[k v] id-map] [(keyword id-ns (name k)) v])
        state-ns (str id-ns ".state")
        states (for [[k v] state] [(keyword state-ns (name k)) v])
        desired-ns (str id-ns ".desired")
        desireds (for [[k v] desired] [(keyword desired-ns (name k)) v])]
    (into {::archetype [:sss.archetype/name archetype]} (concat ids states desireds))))


(defmethod ig/init-key ::init [_ {:keys [db-conn] _dummy :archetypes _dummy2 :ev-loop {:keys [entities]} :cfg}]
  (let [entities (map-indexed (fn [idx ent]
                                (if (some? (:db/id ent))
                                  ent
                                  (assoc ent :db/id (str "$$ent-" idx))))
                              entities)
        tx-res (apply db/transact! db-conn entities)]
    (->> entities
         (map :db/id)
         (map #(get-in tx-res [:tempids %]))
         (mapv #(d/entity @db-conn %)))))


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


(defmethod ig/init-key ::event-consumer [_ {:keys [db-conn events] _dummy :behaviors}]
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
