(ns sss.db
  (:require
   [datascript.core :as d]
   [integrant.core :as ig]))



(def system-schema
  {:sss.signal/name {:db/unique :db.unique/identity}
   :sss.signal/chan {}
   :sss.signal/mult {}

   :sss.subscription/name {:db/unique :db.unique/identity}
   :sss.subscription/status {}
   :sss.subscription/signal {:db/valueType :db.type/ref}
   :sss.subscription/chan {}

   :sss.event/name {:db/unique :db.unique/identity}

   :sss.entity-archetype/name {:db/unique :db.unique/identity}
   :sss.entity-archetype/schema {}

   :sss.entity/archetype {:db/valueType :db.type/ref}

   :sss.behavior/name {:db/unique :db.unique/identity}
   :sss.behavior/events {:db/cardinality :db.cardinality/many
                         :db/valueType :db.type/ref}})


(defmethod ig/init-key ::conn [_ {:keys [schema]}]
  (d/create-conn (merge system-schema schema)))


(defn transact! [conn & tx]
  (let [tx-data (d/transact! conn tx)]
    #_(tap> ["transaction" tx tx-data])
    tx-data))


(defn find1 [conn & where]
  (->> @conn
       (d/q {:find '[?e .] :where where})
       (d/entity @conn)))

(defn find* [conn & where]
  (->> @conn
       (d/q {:find '[[?e ...]] :where where})
       (map #(d/entity @conn %))))
