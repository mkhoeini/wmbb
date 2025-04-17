(ns sss.event
  (:require
   [clojure.core.async :as async]
   [integrant.core :as ig]
   [sss.db :as db]))



(def ^:private mix-for-chan (atom {}))


(defmethod ig/init-key ::events-chan [_ {:keys [buf-fn]}]
  (let [ch (async/chan (buf-fn))
        mix (async/mix ch)]
    (swap! mix-for-chan assoc ch mix)
    ch))


(defmethod ig/halt-key! ::events-chan [_ ch]
  (async/unmix-all (@mix-for-chan ch))
  (swap! mix-for-chan dissoc ch)
  (async/close! ch))


(defn add-sub-chan! [system ch]
  (let [ev-ch (::events-chan system)
        ev-mix (@mix-for-chan ev-ch)]
    (async/admix ev-mix ch)))


(defn init! [system events]
  (let [tx (for [ev events]
             {::name ev})]
    (apply db/transact! system tx)))
