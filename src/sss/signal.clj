(ns sss.signal
  (:require
    [integrant.core :as ig]
    [clojure.core.async :as async]
    [sss.db :as db]))



(def ^:private chan-to-mult (atom {}))


(defmethod ig/init-key ::signals-chans [_ {:keys [signals buf-fn]}]
  (let [chs (->> signals
                 (map (fn [s] [s (async/chan (buf-fn))]))
                 (into {}))]
    (doseq [ch (vals chs)]
      (swap! chan-to-mult assoc ch (async/mult ch)))
    chs))


(defmethod ig/halt-key! ::signals-chans [_ chs]
  (doseq [ch (vals chs)]
    (async/untap-all (@chan-to-mult ch))
    (swap! chan-to-mult dissoc ch)
    (async/close! ch)))


(defn send-signal [system sig data]
  (let [ch (get-in system [::signals-chans sig])
        data-with-meta (vary-meta data assoc :sss/system system)]
    (async/>!! ch data-with-meta)))


(defn subscribe! [system signal ch]
  (let [sig-chan (get-in system [::signals-chans signal])
        sig-mult (get @chan-to-mult sig-chan)]
    (async/tap sig-mult ch)))


(defn init! [system signals]
  (->> signals
       (map #(hash-map ::name %))
       (apply db/transact! system)))


(defn get-signals [system]
  (db/find* system ['?e ::name]))

(comment
  (get-signals @user/system)
  #_end)
