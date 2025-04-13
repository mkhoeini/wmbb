(ns sss.system
  (:require
    [integrant.core :as ig]
    [sss.event :as ev]
    [sss.db :as db]
    [sss.signal :as sig]
    [sss.subscription :as sub]))



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


(defn create-system [opts]
  (ig/init (get-config opts)))


(defn halt-system! [system]
  (ig/halt! system))
