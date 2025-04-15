(ns sss.signal
  (:require
    [integrant.core :as ig]
    [clojure.core.async :as async]
    [sss.db :as db]))



(defmethod ig/init-key ::signals-chans [_ {:keys [signals buf-fn]}]
  (->> signals
       (map (fn [s] [s (async/chan (buf-fn))]))
       (into {})))


(defn send-signal [system sig data]
  (let [ch (get-in system [::signals-chans sig])]
    (async/>!! ch {:signal sig
                   :data data})))


(defn init! [system signals]
  (->> signals
       (map-indexed #(hash-map ::name %2))
       (apply db/transact! system)))
