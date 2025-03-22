(ns user
  (:require
   [dev.nu.morse :as morse]
   [integrant.core :as ig]
   [malli.dev :as mdev]
   [malli.dev.pretty :as mpret]
   [wmbb.system :refer [make-system]]))

(defn start-malli! []
  (mdev/start! {:report (mpret/reporter)}))

(defn stop-malli! []
  (mdev/stop!))

(def system nil)

(defn start-system! []
  (alter-var-root #'system (make-system)))

(defn stop-system! []
  (when system
    (ig/halt! system)
    (alter-var-root #'system nil)))

(defn start-morse! []
  (morse/launch-in-proc))
