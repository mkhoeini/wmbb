(ns sss.event
  (:require
   [clojure.core.async :as async]
   [integrant.core :as ig]
   [sss.behavior :as bh]))

(defmethod ig/init-key ::events [_ opts]
  (let [ch (async/chan (:buf opts))
        mix (async/mix ch)]
    {:chan ch :mix mix}))


(defmethod ig/halt-key! ::events [_ {ch :chan m :mix}]
  (async/unmix-all m)
  (async/close! ch))


(defn fire-event [system target ev-type ev-data]
  (async/put! (-> system ::events :chan)
          {::type ev-type
           ::target target
           ::data ev-data}))


(defn add-sub-chan [system ch]
  (async/admix (-> system ::events :mix) ch))


(defn mute-sub-chan [system ch]
  (async/toggle (-> system ::events :mix) {ch {:mute true}}))


(defn unmute-sub-chan [system ch]
  (async/toggle (-> system ::events :mix) {ch {:mute false}}))


(defmethod ig/init-key ::event-loop [_ {{ch :chan} :events :keys [db behaviors]}]
  (async/thread
    (loop []
      (let [ev (async/<! ch)
            bs (bh/get-behaviors-by-event db (::target ev) (::type ev))]
        (doseq [b bs]
          (bh/exec-behavior db b ev)))
      (when false (recur)))))
