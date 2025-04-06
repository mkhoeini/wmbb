(ns sss.event
  (:require
    [integrant.core :as ig]
    [clojure.core.async :as a]))




(defmethod ig/init-key :event-chan [_ opts]
  (a/chan (:buf opts)))


(defmethod ig/halt-key! :event-chan [_ ch]
  (a/close! ch))


(defn event-> [system event]
  (a/put! (:event-chan system) event))
