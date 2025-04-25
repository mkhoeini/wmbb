(ns sss.event
  (:require
   [clojure.core.async :as async]
   [integrant.core :as ig]
   [sss.db :as db]
   [datascript.core :as d]))



(defn get-event-chan [ev-state]
  (::chan ev-state))


(defn -get-event-mix [ev-state]
  (::mix ev-state))


(defmethod ig/init-key ::events [_ {:keys [buf-fn cfg db-conn]}]
  (let [ch (async/chan (buf-fn))
        mix (async/mix ch)
        tx (conj
            (for [ev (:events cfg)] {::name ev})
            {:db/id "chan-mix"
             ::chan ch
             ::mix mix})
        tx-res (apply db/transact! db-conn tx)]
    (d/entity @db-conn (get-in tx-res [:tempids "chan-mix"]))))


(defmethod ig/halt-key! ::events [_ events]
  (async/unmix-all (-get-event-mix events))
  (async/close! (get-event-chan events)))


(defn add-sub-chan! [events ch]
  (async/admix (-get-event-mix events) ch))


(defn make-event [name target data]
  {:name name
   :target target
   :data data})


(defn send-event! [events ev]
  (async/put! (get-event-chan events) ev))
