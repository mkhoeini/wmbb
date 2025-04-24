(ns sss.system
  (:require
   [clojure.core.async :as async]
   [integrant.core :as ig]
   [sss.archetype :as arch]
   [sss.behavior :as be]
   [sss.config :as cfg]
   [sss.db :as db]
   [sss.entity :as ent]
   [sss.event :as ev]
   [sss.signal :as sig]
   [sss.subscription :as sub]
   [sss.tag :as tag]
   [sss.utils :refer [spy]]))



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
   ::tag/tags {:cfg (ig/ref ::cfg/config)
               :db-conn (ig/ref ::db/conn)}
   ::arch/archetypes {:cfg (ig/ref ::cfg/config)
                      :db-conn (ig/ref ::db/conn)
                      :tags (ig/ref ::tag/tags)}
   ::ent/init {:archetypes (ig/ref ::arch/archetypes)
               :cfg (ig/ref ::cfg/config)
               :db-conn (ig/ref ::db/conn)}
   ::ent/event-consumer {:db-conn (ig/ref ::db/conn)
                         :events (ig/ref ::ev/events)}
   ::be/behaviors {:cfg (ig/ref ::cfg/config)
                   :db-conn (ig/ref ::db/conn)
                   :events (ig/ref ::ev/events)
                   :tags (ig/ref ::tag/tags)}})


(defn get-final-config [config]
  (let [schema (arch/to-schema (:archetypes config))]
    (-> default-config
        (assoc-in [::cfg/config] config)
        (assoc-in [::db/conn :schema] schema))))


(defn create-system [config]
  (spy "create-system" (ig/init (get-final-config config))))


(defn halt-system! [system]
  (ig/halt! system))
