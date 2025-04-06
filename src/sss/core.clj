(ns sss.core
  (:require
   [sss.event :as ev]
   [sss.log :as log]
   [sss.system :as sys]))

(defn create-system [& {:as config}]
  (sys/create-system config))


(defn event-> [system event]
  (log/debug event)
  (ev/event-> system event))
