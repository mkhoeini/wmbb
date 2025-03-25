(ns wmbb.manage
  (:require
   [wmbb.data :as data]
   [wmbb.yabai :as yabai]))

(def window-is-manageable?
  (every-pred :wmbb.window/has-ax-reference
          #(= "AXWindow" (:wmbb.window/role %))
          (complement :wmbb.window/is-minimized)
          :wmbb.window/can-move
          (complement :wmbb.window/is-floating)
          #(= "normal" (:wmbb.window/layer %))
          #(= "normal" (:wmbb.window/sub-layer %))
          #(= "AXStandardWindow" (:wmbb.window/subrole %))
          (complement :wmbb.window/is-grabbed)
          (complement :wmbb.window/is-hidden)
          :wmbb.window/is-visible))


(defn get-current-manageable-windows []
  (->> (data/get-active-space-windows)
       (filter window-is-manageable?)
       (sort-by (juxt #(-> % :frame :x) #(-> % :frame :y)))))

(comment
  (get-current-manageable-windows)
  #_end)


(def top-margin 50)
(def bottom-margin 10)
(def h-w-ratio 1.414214)


(defn get-ideal-size []
  (let [height (-> (data/get-active-display) :wmbb.display/h)
        available-height (- height top-margin bottom-margin)]
    {:w (/ available-height h-w-ratio)
     :h available-height}))


(defn resize-current-windows []
  (let [{:keys [w h]} (get-ideal-size)]
    (doseq [win (get-current-manageable-windows)]
      (yabai/window-resize (:wmbb.window/id win) w h)
      (Thread/sleep 500))))

(comment
  (resize-current-windows)
  #_end)


(def around-margin 5)


(defn get-x-distribution []
  (let [width (:wmbb.display/w (data/get-active-display))
        available-width (- width (* 2 around-margin))
        ideal-width (:w (get-ideal-size))
        workable-width (/ (- available-width ideal-width) 2)
        windows (get-current-manageable-windows)
        total-windows (count windows)
        left-windows (count (take-while #(not (:wmbb.window/has-focus %)) windows))
        right-windows (- total-windows left-windows 1)
        left-pace (/ workable-width (max 1 left-windows))
        right-pace (/ workable-width (max 1 right-windows))
        xs (concat (->> (range left-windows) (map #(* left-pace %)))
                   [workable-width]
                   (->> (range right-windows) (map #(* right-pace %))))]
    (map vector windows xs)))

(comment
  (get-x-distribution)
  #_end)


(defn move-current-windows []
  (doseq [[win x] (get-x-distribution)]
    (yabai/window-move (:wmbb.window/id win) x top-margin)
    (Thread/sleep 500)))


(defn resize-and-move []
  (let [windows (get-current-manageable-windows)]
    (resize-current-windows)
    (move-current-windows)
    (doseq [w (take-while #(not (:has-focus %)) (reverse windows))]
      (yabai/window-focus (:id w))
      (Thread/sleep 500))
    (doseq [w (take-while #(not (:has-focus %)) windows)]
      (yabai/window-focus (:id w))
      (Thread/sleep 500))
    (yabai/window-focus (:id (some #(when (:has-focus %) %) windows)))))

(comment
  (resize-and-move)
  #_end)
