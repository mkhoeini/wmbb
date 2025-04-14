(ns sss.system
  (:require
    [integrant.core :as ig]
    [sss.event :as ev]
    [sss.db :as db]
    [sss.signal :as sig]
    [sss.subscription :as sub]
    [sss.entity :as ent]))



(defn- merge-deep [a b]
  (let [merge-fn (fn [a b]
                   (if (map? a)
                     (merge a b)
                     b))]
    (merge-with merge-fn a b)))


(def default-config
  {::ev/events-chan {:buf 1000}
   ::db/db {}})


(defn get-config [config]
  (let [{:keys [entities init signals subscriptions reconciler commands tags behaviors]} config
        opts {::db/db {:schema (ent/to-schema entities)
                       :seed init}}]
    (merge-deep default-config opts)))


(defn create-system [config]
  (ig/init (get-config config)))


(defn halt-system! [system]
  (ig/halt! system))
