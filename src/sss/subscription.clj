(ns sss.subscription
  (:require
    [sss.db :as db]
    [datascript.core :as d]
    [integrant.core :as ig]
    [clojure.core.async :as async]
    [sss.log :as log]
    [sss.event :as ev]))



(defmacro defsub [signal sub-name binding filter-expr map-expr]
  `(def ~sub-name
     {::name ~(keyword (str (ns-name *ns*)) (str sub-name))
      ::signal ~signal
      ::filter (fn ~sub-name ~binding ~filter-expr)
      ::map (fn ~sub-name ~binding ~map-expr)}))


(defmethod ig/init-key ::subscriptions [_ val]
  (->> val
       (map #(vector (::name %) %))
       (into {})))


(defn add-sub! [system sub conf]
  (let [sub-def (get-in system [::subscriptions sub])
        ch (async/chan
            (async/dropping-buffer 50)
            (comp (filter #((::filter sub-def) system % conf))
                  (map #(let [res ((::map sub-def) system % conf)]
                          (log/debug "processed sig to ev" {:sig % :ev res :sub sub-def :conf conf})
                          res))))
        tx-data (db/transact! system {:db/id "sub"
                                      ::status :enable
                                      ::signal (::signal sub-def)
                                      ::subscription sub
                                      ::config conf
                                      ::out-chan ch})
        sub-id (get-in tx-data [:tempids "sub"])]
    (ev/add-sub-chan system ch)
    (d/entity @(::db/db system) sub-id)))


(defn update-sub-status! [system sub status]
  (db/transact! system {:db/id (:db/id sub)
                        ::status status})
  (if (= :enable status)
    (ev/unmute-sub-chan system (::out-chan sub))
    (ev/mute-sub-chan system (::out-chan sub))))


(defn get-all-subs [system]
  (->> (db/find* system '[?e ::status _])
       (map :db/id)
       (d/pull-many @(::db/db system) [:*])))


(defmethod ig/init-key ::subscription-loop [_ {:keys [db subs signals]}]
  (async/thread
    (loop []
      (let [enabled (db/find* db '[?e ::status :enable])
            by-sig (group-by #(-> % ::signal signals) enabled)]
        (async/untap)
        (when false (recur))))))
