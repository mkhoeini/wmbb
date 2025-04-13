(ns sss.system
  (:require
    [integrant.core :as ig]
    [sss.event :as ev]
    [sss.db :as db]
    [sss.signal :as sig]
    [sss.subscription :as sub]
    [sss.entity :as ent]))



(def default-config
  {::ev/events {:buf 1000}
   ::ev/event-loop {}
   ::db/db {:schema {}
            :seed []}
   ::sig/signals {}
   ::sub/subscriptions []})


(defn- get-config [opts]
  (let [merge-fn (fn [a b]
                   (if (map? a)
                     (merge a b)
                     b))]
    (merge-with merge-fn default-config opts)))


(defn create-system [config]
  (let [{:keys [entities init signals subscriptions reconciler commands tags behaviors]} config]
    (ig/init (get-config {::db/db {:schema (ent/to-schema entities)
                                   :seed init}
                          ::sig/signals signals
                          ::sub/subscriptions subscriptions}))))


(defn halt-system! [system]
  (ig/halt! system))
