(ns sss.archetype
  (:require
    [sss.db :as db]
    [sss.tag :as tag]
    [integrant.core :as ig]
    [datascript.core :as d]))



(defn- attrs [xs]
  (into {} (for [attr xs]
             (case attr
               :id [:db/unique :db.unique/identity]
               :ref [:db/valueType :db.type/ref]
               :* [:db/cardinality :db.cardinality/many]))))


(defn make-archetype [archetype tags id-fields state-fields desired-fields]
  (let [a-name (name archetype)
        id-ns (str "sss.entity." a-name)
        ids (for [[k v] id-fields] [(keyword id-ns (name k)) (attrs v)])
        state-ns (str id-ns ".state")
        states (for [[k v] state-fields] [(keyword state-ns (name k)) (attrs v)])
        desired-ns (str id-ns ".desired")
        desireds (for [[k v] desired-fields] [(keyword desired-ns (name k)) (attrs v)])
        schema (into {} (concat ids states desireds))]
    {:name archetype
     :tags (into [::tag/entity] tags)
     :schema schema}))


(defn to-schema [archetypes]
  (apply merge (map :schema archetypes)))


(defmethod ig/init-key ::archetypes [_ {:keys [db-conn] _dummy :tags {:keys [archetypes]} :cfg}]
  (let [tx (for [{:keys [name tags schema]} archetypes]
             {:db/id (str name)
              ::name name
              ::tags (for [t tags] [::tag/name t])
              ::schema schema})
        tx-res (apply db/transact! db-conn tx)]
    (into {} (for [arch (map :name archetypes)]
               [arch (d/entity @db-conn (get-in tx-res [:tempids (str arch)]))]))))
