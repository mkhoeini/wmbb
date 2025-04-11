(ns sss.entity)



(defn- attrs [a]
  (->> a
       (map #(case %
               :id [:db/unique :db.unique/identity]
               :ref [:db/valueType :db.type/ref]
               :* [:db/cardinality :db.cardinality/many]))
       (into {})))


(defn create-entity-schema [name fields-map]
  (->>  fields-map
        (map (fn [[attr-name attr-val]] [attr-name (attrs attr-val)]))
        (into {::name name})))


(defmacro defentity [ent-name fields]
  `(def ~ent-name ~(create-entity-schema (keyword (str (ns-name *ns*)) (name ent-name)) fields)))


(defn to-schema [entities]
  (-> (apply merge entities)
      (dissoc ::name)))

(comment
  (to-schema [{::name 12 :a 1 :b 2} {::name 23 :c 3 :d 4}])
  #_end)
