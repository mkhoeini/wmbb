(ns sss.entity
  (:require
    [sss.db :as db]
    [sss.event :as ev]
    [clojure.core.async :as async]))



(defn- attrs [a]
  (->> a
       (map #(case %
               :id [:db/unique :db.unique/identity]
               :ref [:db/valueType :db.type/ref]
               :* [:db/cardinality :db.cardinality/many]))
       (into {})))


(defn make-entity-archetype [name fields]
  (let [schema (->> fields
                    (map (fn [[attr-name attr-val]]
                           [attr-name (attrs attr-val)]))
                    (into {}))]
    {::name name
     ::schema schema}))


(defn to-schema [entities]
  (->> entities (map ::schema) (apply merge)))


(defn make-event-loop! [system]
  (async/thread
    (loop []
      (when-let [{::ev/keys [name target data]} (async/<! (::ev/events-chan system))]
        (recur)))))


(defn init! [system entities init]
  (let [a-tx (for [{::keys [name schema]} entities]
               {:db/id (str name)
                :sss.entity-archetype/name name
                :sss.entity-archetype/schema schema})
        i-tx (for [[name instances] init
                   instance instances]
               (assoc instance ::archetype (str name)))]
    (apply db/transact! system (concat a-tx i-tx)))
  (make-event-loop! system))
