(ns wmbb.server.core
  (:require
   [clojure.core.async :as async]
   [wmbb.server.fifo :refer [get-fifo-lines-chan]]
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
(def -loop (atom nil))

(defn start-loop! []
  (when-not @-loop
    (reset! -loop
            (async/go-loop [ev (async/<! (get-fifo-lines-chan))]
              (println ev)
              (reset! last-event ev)
              (update-info!)
              (when @-loop (recur (async/<! (get-fifo-lines-chan))))))))

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
