(ns sss.archetype
  (:require
    [sss.db :as db]
    [integrant.core :as ig]
    [datascript.core :as d]))



(defn- attrs [xs]
  (into {} (for [attr xs]
             (case attr
               :id [:db/unique :db.unique/identity]
               :ref [:db/valueType :db.type/ref]
               :* [:db/cardinality :db.cardinality/many]))))


(defn make-archetype [name fields]
  (let [schema (into {} (for [[attr-name attr-val] fields]
                          [attr-name (attrs attr-val)]))]
    {:name name
     :schema schema}))


(defn to-schema [archetypes]
  (apply merge (map :schema archetypes)))


(defmethod ig/init-key ::archetypes [_ {:keys [db-conn] {:keys [archetypes]} :cfg}]
  (let [tx (for [{:keys [name schema]} archetypes]
             {:db/id (str name)
              ::name name
              ::schema schema})
        tx-res (apply db/transact! db-conn tx)]
    (into {} (for [arch (map :name archetypes)]
               [arch (d/entity db-conn (get-in tx-res [:tempids (str arch)]))]))))
