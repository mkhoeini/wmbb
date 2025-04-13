(ns sss.db
  (:require
   [datascript.core :as d]
   [integrant.core :as ig]
   [sss.log :as log]))



(defmethod ig/init-key ::db [_ {:keys [schema seed]}]
  (let [db (d/create-conn schema)]
    (d/transact! db seed)
    db))


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
