(ns wmbb.system
  (:require
   [mount.core :as mount]
   [wmbb.yabai]
   [wmbb.db]
   [wmbb.events]
   [wmbb.socket]))


(defn start-system! []
  (mount/start))

(defn stop-system! []
  (mount/stop))
