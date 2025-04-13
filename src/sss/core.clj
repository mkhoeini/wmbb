(ns sss.core
  (:require
   [sss.system :as sys]))



(defn create-system [config]
  (sys/create-system config))


(defn halt-system! [system]
  (sys/halt-system! system))
