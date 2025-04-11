(ns user
  (:require
   [dev.nu.morse :as morse]
   [malli.dev :as mdev]
   [malli.dev.pretty :as mpret]
   [mount.core :as mount]
   [wmbb.yabai :as yabai]
   [clojure.tools.namespace.repl :as tn]))

;; used by yabai signals to call us
(def ev> yabai/put-event)


(defn start-malli! []
  (mdev/start! {:report (mpret/reporter)}))

(defn stop-malli! []
  (mdev/stop!))


(defn start-morse! []
  (morse/launch-in-proc))


(defn go []
  (mount/start)
  :ready)


(defn reset []
  (mount/stop)
  (tn/refresh :after `go))
