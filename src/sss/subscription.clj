(ns sss.subscription
  (:require
   [clojure.core.async :as async]
   [datascript.core :as d]
   [integrant.core :as ig]
   [sss.db :as db]
   [sss.event :as ev]
   [sss.signal :as sig]
   [sss.sys-ref :refer [*system*]]
   [sss.utils :refer [spy]]))



(defn -get-subscriptions [subs-state]
  (keys subs-state))


(defn -get-subscription-chan [subs-state subscription]
  (get-in subs-state [subscription ::chan]))


(defmethod ig/init-key ::subscriptions [_ {:keys [buf-fn db-conn events signals] {:keys [subscriptions]} :cfg}]
  (let [tx (for [[sub-name {:keys [signal interesting? to-event]}]  subscriptions
                 :let [filt-fn #(binding [*system* (-> % meta :sss/system)] (interesting? %))
                       map-fn #(binding [*system* (-> % meta :sss/system)] (to-event %))]]
             {:db/id (str sub-name)
              ::name sub-name
              ::status :enable
              ::signal [::sig/name signal]
              ::chan (async/chan (buf-fn) (comp (filter filt-fn) (map map-fn) #_(map #(spy (str sub-name) %))))})
        tx-res (apply db/transact! db-conn tx)
        subs (into {} (for [s (keys subscriptions)]
                        [s (d/entity @db-conn (get-in tx-res [:tempids (str s)]))]))]
    (doseq [[_ ent] subs :let [signal (get-in ent [::signal ::sig/name])
                               ch (::chan ent)]]
      (ev/add-sub-chan! events ch)
      (sig/subscribe! signals signal ch))
    subs))


(defmethod ig/halt-key! ::subscriptions [_ subs]
  (doseq [s (-get-subscriptions subs) :let [ch (-get-subscription-chan subs s)]]
    (async/close! ch)))
