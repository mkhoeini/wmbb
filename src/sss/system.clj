(ns sss.system
  (:require
   [clojure.core.async :as async]
   [integrant.core :as ig]
   [sss.archetype :as arch]
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
   ::ev/events {:buf-fn #(async/sliding-buffer 1000)
                :cfg (ig/ref ::cfg/config)
                :db-conn (ig/ref ::db/conn)}
   ::sub/subscriptions {:buf-fn #(async/sliding-buffer 20)
                        :cfg (ig/ref ::cfg/config)
                        :db-conn (ig/ref ::db/conn)
                        :events (ig/ref ::ev/events)
                        :signals (ig/ref ::sig/signals)}
   ::arch/archetypes {:cfg (ig/ref ::cfg/config)
                      :db-conn (ig/ref ::db/conn)}
   ::ent/init {:archetypes (ig/ref ::arch/archetypes)
               :cfg (ig/ref ::cfg/config)
               :db-conn (ig/ref ::db/conn)}
   ::ent/event-consumer {:events (ig/ref ::ev/events)}})


(defn get-final-config [config]
  (let [schema (arch/to-schema (:archetypes config))]
    (assoc-in default-config [::db/conn :schema] schema)))


(defn create-system [config]
  (ig/init (get-final-config config)))


(defn halt-system! [system]
  (ig/halt! system))
