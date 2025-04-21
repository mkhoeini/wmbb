(ns sss.signal
  (:require
    [integrant.core :as ig]
    [clojure.core.async :as async]
    [sss.db :as db]))



(defmethod ig/init-key ::signals [_ {:keys [buf-fn db-conn] {:keys [signals]} :cfg}]
  (apply db/transact! db-conn
         (for [s signals] {::name s}))
  (let [chans (into {} (for [s signals] [s (async/chan (buf-fn))]))
        mults (into {} (for [s signals :let [ch (chans s)]] [s (async/mult ch)]))]
    {:signals signals
     :chans chans
     :mults mults}))


(defn -get-signals [events-state]
  (:signals events-state))


(defn -get-signal-chan [events-state signal]
  (get-in events-state [:chans signal]))


(defn -get-signal-mult [events-state signal]
  (get-in events-state [:mults signal]))


(defn -send-signal! [events-state signal data]
  (async/put! (-get-signal-chan events-state signal) data))


(defn -subscribe! [events-state signal chan]
  (async/tap (-get-signal-mult events-state signal) chan))


(defmethod ig/halt-key! ::signals [_ sigs]
  (doseq [sig (-get-signals sigs) :let [m (-get-signal-mult sigs sig)
                                        ch (-get-signal-chan sigs sig)]]
    (async/untap-all m)
    (async/close! ch)))


(defn send-signal! [system signal data]
  (let [data-with-meta (vary-meta data assoc :sss/system system)]
    (-send-signal! (::signals system) signal data-with-meta)))
