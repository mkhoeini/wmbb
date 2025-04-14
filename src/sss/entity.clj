(ns sss.entity)



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
  (->> entities
       (map ::schema)
       (apply merge)))
