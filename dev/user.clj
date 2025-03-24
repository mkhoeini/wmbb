(ns user
  (:require
   [dev.nu.morse :as morse]
   [malli.dev :as mdev]
   [malli.dev.pretty :as mpret]
   [mount.core :as mount]))

(defn start-malli! []
  (mdev/start! {:report (mpret/reporter)}))

(defn stop-malli! []
  (mdev/stop!))

(defn start-system! []
  (mount/start))

(defn stop-system! []
  (mount/stop))

(defn start-morse! []
  (morse/launch-in-proc))
