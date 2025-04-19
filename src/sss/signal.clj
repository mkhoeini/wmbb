(ns sss.signal
  (:require
    [integrant.core :as ig]
    [clojure.core.async :as async]
    [sss.db :as db]))



(defprotocol SignalsState
  (-get-signals [this])
  (-get-signal-chan [this signal])
  (-get-signal-mult [this signal])
  (-send-signal! [this signal data])
  (-subscribe! [this signal chan]))


(defmethod ig/init-key ::signals [_ {:keys [buf-fn db-conn] {:keys [signals]} :cfg}]
  (apply db/transact! db-conn
         (for [s signals] {::name s}))
  (let [chans (into {} (for [s signals] [s (async/chan (buf-fn))]))
        mults (into {} (for [s signals :let [ch (chans s)]] [s (async/mult ch)]))]
    (reify SignalsState
      (-get-signals [_] signals)
      (-get-signal-chan [_ sig] (chans sig))
      (-get-signal-mult [_ sig] (mults sig))
      (-send-signal! [_ signal data]
        (let [ch (chans signal)]
          (async/put! ch data)))
      (-subscribe! [_ signal ch]
        (async/tap (mults signal) ch)))))


(defmethod ig/halt-key! ::signals [_ sigs]
  (doseq [sig (-get-signals sigs) :let [m (-get-signal-mult sigs sig)
                                        ch (-get-signal-chan sigs sig)]]
    (async/untap-all m)
    (async/close! ch)))


(defn send-signal! [system signal data]
  (let [data-with-meta (vary-meta data assoc :sss/system system)]
    (-send-signal! (::signals system) signal data-with-meta)))


(defn subscribe! [system signal chan]
  (-subscribe! (::signals system) signal chan))
