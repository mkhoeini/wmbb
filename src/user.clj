(ns user
  (:require
   [clojure.tools.namespace.repl :as tn]
   [dev.nu.morse :as morse]
   [malli.dev :as mdev]
   [malli.dev.pretty :as mpret]
   [mount.core :as mount]
   [wmbb.system :as sys]
   [wmbb.yabai :as yabai]
   [sss.core :as sss]))



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


(def system
  "make wmbb system available to user namespace"
  #'sys/system)


(defn ev>
  "used by yabai signals to call us"
  [ev]
  (sss/with-system @system
    (#'yabai/put-event! ev)))
