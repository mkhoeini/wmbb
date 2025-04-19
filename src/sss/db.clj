(ns sss.db
  (:require
   [datascript.core :as d]
   [integrant.core :as ig]))



(def system-schema
  {:sss.signal/name {:db/unique :db.unique/identity}

   :sss.subscription/name {:db/unique :db.unique/identity}
   :sss.subscription/status {}
   :sss.subscription/signal {:db/valueType :db.type/ref}

   :sss.event/name {:db/unique :db.unique/identity}

   :sss.entity-archetype/name {:db/unique :db.unique/identity}
   :sss.entity-archetype/schema {}

   :sss.entity/archetype {:db/valueType :db.type/ref}

   :sss.behavior/name {:db/unique :db.unique/identity}
   :sss.behavior/events {:db/cardinality :db.cardinality/many
                         :db/valueType :db.type/ref}})


(defmethod ig/init-key ::db [_ {:keys [schema]}]
  (d/create-conn (merge system-schema schema)))


(defn transact! [system & tx]
  (let [tx-data (d/transact! (::db system) tx)]
    #_(tap> ["transaction" tx tx-data])
    tx-data))


(defn find1 [system & where]
  (->> @(::db system)
       (d/q {:find '[?e .] :where where})
       (d/entity @(::db system))))

(defn find* [system & where]
  (->> @(::db system)
       (d/q {:find '[[?e ...]] :where where})
       (map #(d/entity @(::db system) %))))
