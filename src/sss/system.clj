(ns sss.system
  (:require
   [clojure.core.async :as async]
   [integrant.core :as ig]
   [sss.config :as cfg]
   [sss.db :as db]
   [sss.entity :as ent]
   [sss.event :as ev]
   [sss.signal :as sig]
   [sss.subscription :as sub]))



(def default-config
  {::cfg/config {}
   ::db/conn {:schema {}}
   ::sig/signals {:buf-fn #(async/sliding-buffer 100)
                  :cfg (ig/ref ::cfg/config)
                  :db-conn (ig/ref ::db/conn)}
   ::sub/subscriptions {:buf-fn #(async/sliding-buffer 20)}
   ::ev/events-chan {:buf-fn #(async/sliding-buffer 1000)}})


(defn get-final-config [config]
  (let [schema (ent/to-schema (:entities config))]
    (assoc-in default-config [::db/db :schema] schema)))


(defn create-system [config]
  (ig/init (get-final-config config)))


(defn halt-system! [system]
  (ig/halt! system))
