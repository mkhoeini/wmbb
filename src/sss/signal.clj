(ns sss.signal
  (:require
    [integrant.core :as ig]
    [clojure.core.async :as async]
    [sss.db :as db]))



(defprotocol SignalsState
  (get-chans [this])
  (get-mults [this])
  (-add-signal! [this signal])
  (-send-signal! [this signal data])
  (-subscribe! [this signal chan]))


(defmethod ig/init-key ::signals [_ {:keys [buf-fn]}]
  (let [chans (atom {})
        mults (atom {})]
    (reify SignalsState
      (get-chans [_] @chans)
      (get-mults [_] @mults)
      (-add-signal! [_ signal]
        (let [ch (async/chan (buf-fn))
              m (async/mult ch)]
          (swap! chans assoc signal ch)
          (swap! mults assoc signal m)))
      (-send-signal! [_ signal data]
        (let [ch (get @chans signal)]
          (async/put! ch data)))
      (-subscribe! [_ signal ch]
        (async/tap (get @mults signal) ch)))))


(defmethod ig/halt-key! ::signals [_ sigs]
  (doseq [m (get-mults sigs)] (async/untap-all m))
  (doseq [ch (get-chans sigs)] (async/close! ch)))


(defn send-signal! [system signal data]
  (let [data-with-meta (vary-meta data assoc :sss/system system)]
    (-send-signal! (::signals system) signal data-with-meta)))


(defn subscribe! [system signal chan]
  (-subscribe! (::signals system) signal chan))


(defn init! [system signals]
  (doseq [s signals]
    (-add-signal! (::signals system) s))
  (apply db/transact! system
         (for [s signals] {::name s})))
