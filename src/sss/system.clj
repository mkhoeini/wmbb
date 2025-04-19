(ns sss.system
  (:require
   [clojure.core.async :as async]
   [integrant.core :as ig]
   [sss.behavior :as bh]
   [sss.db :as db]
   [sss.entity :as ent]
   [sss.event :as ev]
   [sss.signal :as sig]
   [sss.subscription :as sub]))



(def default-config
  {::ev/events-chan {:buf-fn #(async/sliding-buffer 1000)}
   ::db/db {:schema {}}
   ::sig/signals {:buf-fn #(async/sliding-buffer 100)}
   ::sub/subscriptions {:buf-fn #(async/sliding-buffer 20)}})


(defn get-config [schema]
  (assoc-in default-config [::db/db :schema] schema))


(defn create-system [config]
  (-> (ent/to-schema (:entities config))
      get-config
      ig/init
      (assoc ::config config)))


(defn halt-system! [system]
  (ig/halt! system))
