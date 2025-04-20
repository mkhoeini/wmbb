(ns sss.subscription
  (:require
   [clojure.core.async :as async]
   [integrant.core :as ig]
   [sss.db :as db]
   [sss.event :as ev]
   [sss.signal :as sig]
   [sss.sys-ref :refer [*system*]]))



(defprotocol SubsState
  (-get-subscriptions [this])
  (-get-subscription-chan [this subscription]))


(defmethod ig/init-key ::subscriptions [_ {:keys [buf-fn db-conn events signals] {:keys [subscriptions]} :cfg}]
  (apply db/transact! db-conn
         (for [[sub-name {signal :signal}] subscriptions]
            {::name sub-name
             ::status :enable
             ::signal [::sig/name signal]}))
  (let [chans (into {} (for [[s d] subscriptions
                             :let [{:keys [interesting? to-event]} d
                                   filt-fn #(binding [*system* (-> % meta :sss/system)] (interesting? %))
                                   map-fn #(binding [*system* (-> % meta :sss/system)] (to-event %))]]
                         [s (async/chan (buf-fn) (comp (filter filt-fn) (map map-fn)))]))]
    (doseq [[sub-name ch] chans :let [signal (get-in subscriptions [sub-name :signal])]]
      (ev/-add-sub-chan! events ch)
      (sig/-subscribe! signals signal ch))
    (reify SubsState
      (-get-subscriptions [_] (keys subscriptions))
      (-get-subscription-chan [_ sub] (chans sub)))))


(defmethod ig/halt-key! ::subscriptions [_ subs]
  (doseq [s (-get-subscriptions subs) :let [ch (-get-subscription-chan subs s)]]
    (async/close! ch)))
