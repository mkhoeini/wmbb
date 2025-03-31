(ns wmbb.db
  (:require
   [datascript.core :as d]
   [mount.core :refer [defstate]]
   [wmbb.schema :refer [schema]]))



(defstate db :start (d/create-conn schema))


(def last-transaction (atom nil))

(defn transact [& tx]
  (let [tx-data (d/transact! db tx)]
    (reset! last-transaction tx-data)
    tx-data))


(defn find1 [& where]
  (->> @db
       (d/q {:find '[?e .] :where where})
       (d/entity @db)))

(defn find* [& where]
  (->> @db
       (d/q {:find '[[?e ...]] :where where})
       (map #(d/entity @db %))))

(comment
  (find1 '[?e :wmbb.display/id])
  (find* '[?e :wmbb.window/id])
  (find* '[?e :wmbb.window/id] '[?e :wmbb.window/app])
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
