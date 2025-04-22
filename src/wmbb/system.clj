(ns wmbb.system
  (:require
   [mount.core :as mount :refer [defstate]]
   [sss.core :as sss]
   [wmbb.behaviors :as be]
   [wmbb.entity :as entity]
   [wmbb.event :as ev]
   [wmbb.subscriptions :as subs]
   [wmbb.tags :as tag]
   [wmbb.yabai :as yabai]))



(defstate system-def
  :start (when (.isAlive yabai/yabai-process)
           {:signals [::yabai/events]
            :events ev/events
            :subscriptions subs/subscriptions
            :tags tag/tags
            :archetypes entity/archetypes
            :entities (entity/get-initial-entity-values)
            :behaviors be/behaviors
            :commands {}
            :reconcilers {}}))

(comment
  (sss/create-system system-def)
  (require '[sss.system :as s-sys])
  (s-sys/create-system system-def)
  (tap> (s-sys/get-final-config system-def))
  (tap> system-def)
  (tap> entity/archetypes)
  #_end)


(defstate system
  :start (sss/create-system system-def)
  :stop (sss/halt-system! system))


(defn start-system! []
  (mount/start))


(defn stop-system! []
  (mount/stop))
