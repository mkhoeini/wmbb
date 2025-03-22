(ns wmbb.server.data
  (:require
   [clojure.core.async
    :as async
    :refer [<! alts! go-loop promise-chan put!]]
   [integrant.core :as ig]
   [wmbb.yabai :refer [yabai-query]]))

(defmethod ig/init-key ::displays [_ _] (atom nil))
(defmethod ig/init-key ::spaces [_ _] (atom nil))
(defmethod ig/init-key ::windows [_ _] (atom nil))


(defn update-info! [displays spaces windows]
  (reset! displays (yabai-query :displays))
  (reset! spaces (yabai-query :spaces))
  (reset! windows (yabai-query :windows)))


(defmethod ig/init-key ::last-event [_ _] (atom nil))


(defn start-loop! [{:keys [last-event displays spaces windows incoming-events]}]
  (let [stop (promise-chan)
        -loop (go-loop [[event ch] (alts! [stop (<! incoming-events)])]
                (when-not (= ch stop)
                  (println "processing event:")
                  (prn event)
                  (reset! last-event event)
                  (update-info! displays spaces windows)
                  (when-not (= ch stop)(recur (alts! [stop (<! incoming-events)])))))]
    {:stop #(put! stop true)
     :loop -loop}))


(defmethod ig/init-key ::update-loop [_ conf]
  (start-loop! conf))

(defmethod ig/halt-key! ::update-loop [_ {stop :stop}]
  (stop))


(defn get-display-ids [displays]
  (->> @displays
       (map :id)
       (into #{})))


(defn get-space-ids [spaces]
  (->> @spaces
       (map :id)
       (into #{})))


(defn get-window-ids [windows]
  (->> @windows
       (map :id)
       (into #{})))


(comment
  (get-display-ids)
  (get-space-ids)
  (get-window-ids)
  END)


(defn get-active-display [displays]
  (->> @displays (some #(when (:has-focus %) %))))


(defn get-active-space [spaces]
  (->> @spaces (some #(when (:has-focus %) %))))


(defn get-active-window [windows]
  (->> @windows (some #(when (:has-focus %) %))))


(comment
  (get-active-display)
  (get-active-space)
  (get-active-window)
  END)


(defn get-active-space-windows [spaces windows]
  (let [active-space (get-active-space spaces)
        window-ids (set (:windows active-space))]
    (filter #(window-ids (:id %)) @windows)))


(comment
  (get-active-space-windows)
  END)
