(ns sss.system
  (:require
    [integrant.core :as ig]
    [sss.event :as ev]
    [sss.db :as db]))



(def default-config
  {::ev/event-chan {:buf 1000}
   ::db/db {:schema {}
            :seed []}})


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
