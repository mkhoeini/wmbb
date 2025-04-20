(ns sss.event
  (:require
   [clojure.core.async :as async]
   [integrant.core :as ig]
   [sss.db :as db]))



(defprotocol EventState
  (-get-event-chan [this])
  (-get-event-mix [this]))


(defmethod ig/init-key ::events [_ {:keys [buf-fn cfg db-conn]}]
  (apply db/transact! db-conn
         (for [ev (:events cfg)] {::name ev}))
  (let [ch (async/chan (buf-fn))
        mix (async/mix ch)]
    (reify EventState
      (-get-event-chan [_] ch)
      (-get-event-mix [_] mix))))


(defmethod ig/halt-key! ::events [_ events]
  (async/unmix-all (-get-event-mix events))
  (async/close! (-get-event-chan events)))


(defn -add-sub-chan! [events ch]
  (async/admix (-get-event-mix events) ch))
