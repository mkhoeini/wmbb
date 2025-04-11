(ns sss.event
  (:require
    [integrant.core :as ig]
    [clojure.core.async :as a]))




(defmethod ig/init-key ::event-chan [_ opts]
  (a/chan (:buf opts)))


(defmethod ig/halt-key! ::event-chan [_ ch]
  (a/close! ch))


(defn fire-event [system target ev-type ev-data]
  (a/put! (::event-chan system)
          {::type ev-type
           ::target target
           ::data ev-data}))
