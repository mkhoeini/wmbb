(ns sss.signal
  (:require
    [integrant.core :as ig]
    [clojure.core.async :as async]
    [sss.db :as db]))



(defprotocol SignalsState
  (-get-signals [this])
  (-get-signal-chan [this signal])
  (-get-signal-mult [this signal])
  (-add-signal! [this signal])
  (-send-signal! [this signal data])
  (-subscribe! [this signal chan]))


(defmethod ig/init-key ::signals [_ {:keys [buf-fn cfg db-conn]}]
  (let [chans (atom {})
        mults (atom {})
        signals (:signals cfg)
        state (reify SignalsState
                (-get-signals [_] signals)
                (-get-signal-chan [_ sig] (get @chans sig))
                (-get-signal-mult [_ sig] (get @mults sig))
                (-add-signal! [_ signal]
                  (let [ch (async/chan (buf-fn))
                        m (async/mult ch)]
                    (swap! chans assoc signal ch)
                    (swap! mults assoc signal m)))
                (-send-signal! [_ signal data]
                  (let [ch (get @chans signal)]
                    (async/put! ch data)))
                (-subscribe! [_ signal ch]
                  (async/tap (get @mults signal) ch)))]
    (doseq [s signals] (-add-signal! state s))
    (apply db/transact! db-conn
           (for [s signals] {::name s}))
    state))


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
