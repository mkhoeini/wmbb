(ns sss.signal
  (:require
    [integrant.core :as ig]
    [clojure.core.async :as async]))



(defmethod ig/init-key ::signals-chans [_ {:keys [signals buf-fn]}]
  (->> signals
       (map (fn [s] [s (async/chan (buf-fn))]))
       (into {})))


(defn send-signal [system sig data]
  (let [ch (get-in system [::signals-chans sig])]
    (async/>!! ch {:signal sig
                   :data data})))
