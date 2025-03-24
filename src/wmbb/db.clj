(ns wmbb.db
  (:require
    [datascript.core :as d]))


(def schema {:wmbb.display/id {:db/unique :db.unique/identity}
             :wmbb.space/id {:db/unique :db.unique/identity}
             :wmbb.window/id {:db/unique :db.unique/identity}

             :wmbb.display/spaces {:db/cardinality :db.cardinality/many}
             :wmbb.space/windows {:db/cardinality :db.cardinality/many}})


(def db (d/create-conn schema))


(defn transact [tx] (d/transact! db tx))


(defn find1 [where]
  (->> @db
       (d/q `[:find ~'?e . :where ~where])
       (d/entity @db)))

(defn find* [where]
  (->> @db
       (d/q `[:find [~'?e ...] :where ~@(if (list? where) where [where])])
       (map #(d/entity @db %))))

(comment
  (find1 '[?e :wmbb.display/id])
  (find* '[?e :wmbb.window/id])
  (find* '([?e :wmbb.window/id]))
  #_end)


(defn get-displays []
  (find* '[?e :wmbb.display/id]))

(defn get-spaces []
  (find* '[?e :wmbb.space/id]))

(defn get-windows []
  (find* '[?e :wmbb.window/id]))

(comment
  (get-displays)
  (get-spaces)
  (get-windows)
  #_end)
