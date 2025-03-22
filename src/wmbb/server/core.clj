(ns wmbb.server.core
  (:require
   [wmbb.server.data :refer [get-active-display get-active-space-windows]]
   [wmbb.yabai
    :refer
    [yabai-window-focus yabai-window-move yabai-window-resize]]))

(defn get-current-manageable-windows []
  (let [is-manageable? (every-pred :has-ax-reference
                                   #(= "AXWindow" (:role %))
                                   (complement :is-minimized)
                                   :can-move
                                   (complement :is-floating)
                                   #(= "normal" (:layer %))
                                   #(= "normal" (:sub-layer %))
                                   #(= "AXStandardWindow" (:subrole %))
                                   (complement :is-grabbed)
                                   (complement :is-hidden)
                                   :is-visible)]
    (->> (get-active-space-windows)
         (filter is-manageable?)
         (sort-by (juxt #(-> % :frame :x) #(-> % :frame :y))))))


(def top-margin 50)
(def bottom-margin 10)
(def h-w-ratio 1.414214)

(defn get-ideal-size []
  (let [height (-> (get-active-display) :frame :h)
        available-height (- height top-margin bottom-margin)]
    {:w (/ available-height h-w-ratio)
     :h available-height}))

(defn resize-current-windows []
  (let [{:keys [w h]} (get-ideal-size)]
    (doseq [win (get-current-manageable-windows)]
      (yabai-window-resize (:id win) w h))))

(def around-margin 5)

(defn get-x-distribution []
  (let [width (-> (get-active-display) :frame :w)
        available-width (- width (* 2 around-margin))
        ideal-width (:w (get-ideal-size))
        workable-width (/ (- available-width ideal-width) 2)
        windows (get-current-manageable-windows)
        total-windows (count windows)
        left-windows (count (take-while #(not (:has-focus %)) windows))
        right-windows (- total-windows left-windows 1)
        left-pace (/ workable-width (max 1 left-windows))
        right-pace (/ workable-width (max 1 right-windows))
        xs (concat (->> (range left-windows) (map #(* left-pace %)))
                   [workable-width]
                   (->> (range right-windows) (map #(* right-pace %))))]
    (map vector windows xs)))

(defn move-current-windows []
  (doseq [[win x] (get-x-distribution)]
    (yabai-window-move (:id win) x top-margin)))

(defn resize-and-move []
  (let [windows (get-current-manageable-windows)]
    (resize-current-windows)
    (move-current-windows)
    (doseq [w (take-while #(not (:has-focus %)) (reverse windows))]
      (yabai-window-focus (:id w)))
    (doseq [w (take-while #(not (:has-focus %)) windows)]
      (yabai-window-focus (:id w)))
    (yabai-window-focus (:id (some #(when (:has-focus %) %) windows)))))

(comment
  (resize-and-move)
  END)
