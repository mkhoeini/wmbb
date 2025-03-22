(ns wmbb.server.data
  (:require
   [clojure.core.async :as async :refer [<! go-loop]]
   [wmbb.server.socket :refer [incoming-events]]
   [wmbb.yabai :refer [yabai-query]]))

(def displays (atom nil))
(def spaces (atom nil))
(def windows (atom nil))

(defn update-info! []
  (reset! displays (yabai-query :displays))
  (reset! spaces (yabai-query :spaces))
  (reset! windows (yabai-query :windows)))

(comment
  (update-info!)
  END)

(def last-event (atom nil))

(defn start-loop! []
  (go-loop [event (<! incoming-events)]
    (println "processing " event)
    (reset! last-event event)
    (update-info!)
    (recur (<! incoming-events))))

(comment
  (start-loop!)
  (reset! -loop nil)
  END)

(defn get-display-ids []
  (->> @displays
       (map :id)
       (into #{})))

(defn get-space-ids []
  (->> @spaces
       (map :id)
       (into #{})))

(defn get-window-ids []
  (->> @windows
       (map :id)
       (into #{})))

(comment
  (get-display-ids)
  (get-space-ids)
  (get-window-ids)
  END)

(defn get-active-display []
  (->> @displays (some #(when (:has-focus %) %))))

(defn get-active-space []
  (->> @spaces (some #(when (:has-focus %) %))))

(defn get-active-window []
  (->> @windows (some #(when (:has-focus %) %))))

(comment
  (get-active-display)
  (get-active-space)
  (get-active-window)
  END)

(defn get-active-space-windows []
  (let [active-space (get-active-space)
        window-ids (set (:windows active-space))]
    (filter #(window-ids (:id %)) @windows)))

(comment
  (get-active-space-windows)
  END)
