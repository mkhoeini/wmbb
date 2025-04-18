(ns wmbb.system
  (:require
   [mount.core :as mount :refer [defstate]]
   [sss.core :as sss]
   [wmbb.entity :as entity]
   [wmbb.yabai :as yabai]
   [wmbb.subscriptions :as subs]
   [wmbb.event :as ev]))



(defstate system-def
  :start (when (.isAlive yabai/yabai-process)
           {:signals [::yabai/events]
            :events ev/events
            :subscriptions subs/subscriptions
            :entities entity/entities
            :init (entity/get-initial-entity-values)
            :behaviors {}
            :tags {}
            :entity-tags {}
            :commands {}
            :reconcilers {}}))

(comment
  (sss/create-system system-def)
  (sss/init-system! (sss/create-system system-def))
  (require '[sss.system :as s-sys])
  (s-sys/create-system system-def)
  (tap> (s-sys/get-config system-def))
  (tap> system-def)
  (tap> entity/entities)
  #_end)


(defstate system
  :start (-> system-def sss/create-system sss/init-system!)
  :stop (sss/halt-system! system))


(defn start-system! []
  (mount/start))


(defn stop-system! []
  (mount/stop))
