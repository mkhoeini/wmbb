(ns sss.db
  (:require
   [datascript.core :as d]
   [integrant.core :as ig]
   [sss.log :as log]))



(def system-schema
  {:sss.entity/archetype {:db/valueType :db.type/ref}
   :sss.signal/name {:db/unique :db.unique/identity}})


(defmethod ig/init-key ::db [_ {:keys [schema]}]
  (d/create-conn (merge system-schema schema)))


(defn transact! [system & tx]
  (let [tx-data (d/transact! (::db system) tx)]
    (log/debug "last transaction" tx-data)
    tx-data))


(defn find1 [system & where]
  (->> @(::db system)
       (d/q {:find '[?e .] :where where})
       (d/entity @(::db system))))

(defn find* [system & where]
  (->> @(::db system)
       (d/q {:find '[[?e ...]] :where where})
       (map #(d/entity @(::db system) %))))
