(ns sss.core
  (:require
   [sss.event :as ev]
   [sss.db :as db]
   [sss.entity :as ent]
   [sss.log :as log]
   [sss.system :as sys]))



(defn- get-config [c]
  (-> c
      (assoc-in [:db :entities] (:entities c))
      (assoc-in [:db :init] (:init c))
      (dissoc :entities :init)))


(defn create-system [config]
  (let [{:keys [entities init signals subscriptions reconciler commands tags behaviors]} config]
    (sys/create-system {::db/db {:schema (ent/to-schema entities)
                                 :seed init}})))


(defn halt-system! [system]
  (sys/halt-system! system))


(defn fire-event [system target type data]
  (log/debug "fire event" {:system system
                           :target target
                           :type type
                           :data data})
  (ev/fire-event system target type data))
