(ns sss.signal
  (:require
    [integrant.core :as ig]
    [clojure.core.async :as async]
    [sss.db :as db]
    [datascript.core :as d]))



(defmethod ig/init-key ::signals [_ {:keys [buf-fn db-conn] {:keys [signals]} :cfg}]
  (let [tx (for [s signals
                 :let [ch (async/chan (buf-fn))
                       m (async/mult ch)]]
             {:db/id (str s)
              ::name s
              ::chan ch
              ::mult m})
        tx-res (apply db/transact! db-conn tx)]
    (into {} (for [s signals]
               [s (d/entity @db-conn (get-in tx-res [:tempids (str s)]))]))))


(defn- -get-signals [events-state]
  (keys events-state))


(defn- -get-signal-chan [events-state signal]
  (get-in events-state [signal ::chan]))


(defn- -get-signal-mult [events-state signal]
  (get-in events-state [signal ::mult]))


(defn- -send-signal! [events-state signal data]
  #_(tap> ["sent signal" (-get-signal-chan events-state signal) events-state signal])
  (async/put! (-get-signal-chan events-state signal) data))


(defn subscribe! [events-state signal chan]
  (async/tap (-get-signal-mult events-state signal) chan))


(defmethod ig/halt-key! ::signals [_ sigs]
  (doseq [sig (-get-signals sigs) :let [m (-get-signal-mult sigs sig)
                                        ch (-get-signal-chan sigs sig)]]
    (async/untap-all m)
    (async/close! ch)))


(defn send-signal! [system signal data]
  #_(tap> ["got event" system signal data])
  (let [data-with-meta (vary-meta data assoc :sss/system system)]
    (-send-signal! (::signals system) signal data-with-meta)))
