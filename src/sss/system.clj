(ns sss.system
  (:require
   [clojure.core.async :as async]
   [integrant.core :as ig]
   [sss.db :as db]
   [sss.entity :as ent]
   [sss.event :as ev]
   [sss.signal :as sig]
   [sss.subscription :as sub]))



(defn- merge-deep [a b]
  (let [merge-fn (fn [a b]
                   (if (map? a)
                     (merge a b)
                     b))]
    (merge-with merge-fn a b)))


(def default-config
  {::ev/events-chan {:buf-fn #(async/sliding-buffer 1000)}
   ::db/db {:schema {}}
   ::sig/signals-chans {:signals []
                        :buf-fn #(async/sliding-buffer 100)}
   ::sub/subscriptions-chans {:subscriptions {}
                              :buf-fn #(async/sliding-buffer 20)}})


(defn get-config [schema signals subscriptions]
  (let [opts {::db/db {:schema schema}
              ::sig/signals-chans {:signals signals}
              ::sub/subscriptions-chans {:subscriptions subscriptions}}]
    (merge-deep default-config opts)))


(defn create-system [config]
  (let [{:keys [entities init signals subscriptions reconciler commands tags behaviors]} config
        schema (ent/to-schema entities)
        system (ig/init (get-config schema signals subscriptions))]
    (sig/init! system signals)
    (sub/init! system subscriptions)
    system))


(defn halt-system! [system]
  (ig/halt! system))
