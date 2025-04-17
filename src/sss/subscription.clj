(ns sss.subscription
  (:require
   [clojure.core.async :as async]
   [integrant.core :as ig]
   [sss.db :as db]
   [sss.event :as ev]
   [sss.signal :as sig]
   [sss.sys-ref :refer [*system*]]))



(defmethod ig/init-key ::subscriptions-chans [_ {:keys [subscriptions buf-fn]}]
  (->> subscriptions
       (map (fn [[name {:keys [interesting? to-event]}]]
              [name (async/chan (buf-fn) (comp (filter #(binding [*system* (-> % meta :sss/system)] (interesting? %)))
                                               (map #(binding [*system* (-> % meta :sss/system)] (to-event %)))))]))
       (into {})))


(defmethod ig/halt-key! ::subscriptions-chans [_ chs]
  (doseq [ch (vals chs)]
    (async/close! ch)))


(defn init! [system subs]
  (doseq [[sub-name ch] (::subscriptions-chans system)]
    (ev/add-sub-chan system ch)
    (sig/subscribe! system (get-in subs [sub-name :signal]) ch))
  (let [txs (for [[sub-name {signal :signal}] subs]
              {::name sub-name
               ::status :enable
               ::signal [::sig/name signal]})]
    (apply db/transact! system txs)))

(comment
  (def ch1 (async/chan 1000000))
  (sig/subscribe! @user/system :wmbb.yabai/events ch1)
  (async/<!! ch1)
  #_end)


(defn get-subscriptions [system]
  (db/find* system ['?e ::name]))

(comment
  (->> (get-subscriptions @user/system)
       (map datascript.core/touch)
       #_(map #(update % ::signal datascript.core/touch)))
  #_end)
