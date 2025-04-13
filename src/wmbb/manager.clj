(ns wmbb.manager
  (:require
   [sss.db :as db]
   [wmbb.controller :as controller]
   [wmbb.data :as data]))

(defn get-active-manager-windows []
  (let [current-space (data/get-active-space)
        first-manager-window (db/find1 ['?e :wmbb.manager.window/space (:db/id current-space)]
                                       '(not [?e :wmbb.manager.window/prev]))]
    (->> first-manager-window
         (iterate :wmbb.manager.window/next)
         (take-while some?))))

(comment
  db/db
  (get-active-manager-windows)
  #_end)


(def top-gap 50)
(def bottom-gap 20)
(def window-gap 10)


(defn calc-window-position [ind _win-count focused-ind x y w h]
  (let [target-h (- h top-gap bottom-gap)
        target-w (/ h 1.414214)
        screen-middle-x (+ x (/ w 2))
        distance-between-windows-in-x (+ target-w window-gap)
        target-x (+ screen-middle-x
                    (- (/ target-w 2))
                    (* distance-between-windows-in-x (- ind focused-ind)))
        target-y (+ y top-gap)]
    [target-x target-y target-w target-h]))


(defn calc-layout []
  (let [manager-windows (get-active-manager-windows)
        wcount (count manager-windows)
        focused (first (keep-indexed
                        #(when (-> %2 :wmbb.manager.window/ref :wmbb.window/has-focus) %1)
                        manager-windows))
        {:wmbb.display/keys [x y w h]} (data/get-active-display)]
    (doseq [[ind win] (zipmap (range) manager-windows)
            :let [[x y w h] (calc-window-position ind wcount focused x y w h)]]
      (db/transact! #:wmbb.manager.window{:db/id (:db/id win)
                                          :target-x x
                                          :target-y y
                                          :target-w w
                                          :target-h h}))))


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


(defn insert-window [window]
  (when (window-is-manageable? window)
    (let [space-id (-> window :wmbb.window/space :db/id)
          last-manager-window (db/find1 ['?e :wmbb.manager.window/space space-id]
                                        '(not [?e :wmbbb.manager.window/next]))]
      (if last-manager-window
        (db/transact! #:wmbb.manager.window{:db/id "manager"
                                            :window-id (:wmbb.window/id window)
                                            :ref (:db/id window)
                                            :space space-id
                                            :prev (:db/id last-manager-window)}
                     [:db/add (:db/id last-manager-window) :wmbb.manager.window/next "manager"])
        (db/transact! #:wmbb.manager.window{:window-id (:wmbb.window/id window)
                                            :ref (:db/id window)
                                            :space space-id}))
      (calc-layout)
      (controller/apply-layout (get-active-manager-windows)))))


(defn delete-window [window]
  (calc-layout)
  (controller/apply-layout (get-active-manager-windows)))


(defn update-window [window]
  (calc-layout)
  (controller/apply-layout (get-active-manager-windows)))






;; (defn get-current-manageable-windows []
;;   (->> (data/get-active-space-windows)
;;        (filter window-is-manageable?)
;;        (sort-by (juxt #(-> % :frame :x) #(-> % :frame :y)))))

;; (comment
;;   (get-current-manageable-windows)
;;   #_end)


;; (def top-margin 50)
;; (def bottom-margin 10)
;; (def h-w-ratio 1.414214)


;; (defn get-ideal-size []
;;   (let [height (-> (data/get-active-display) :wmbb.display/h)
;;         available-height (- height top-margin bottom-margin)]
;;     {:w (/ available-height h-w-ratio)
;;      :h available-height}))


;; (defn resize-current-windows []
;;   (let [{:keys [w h]} (get-ideal-size)]
;;     (doseq [win (get-current-manageable-windows)]
;;       (yabai/window-resize (:wmbb.window/id win) w h)
;;       (Thread/sleep 500))))

;; (comment
;;   (resize-current-windows)
;;   #_end)


;; (def around-margin 5)


;; (defn get-x-distribution []
;;   (let [width (:wmbb.display/w (data/get-active-display))
;;         available-width (- width (* 2 around-margin))
;;         ideal-width (:w (get-ideal-size))
;;         workable-width (/ (- available-width ideal-width) 2)
;;         windows (get-current-manageable-windows)
;;         total-windows (count windows)
;;         left-windows (count (take-while #(not (:wmbb.window/has-focus %)) windows))
;;         right-windows (- total-windows left-windows 1)
;;         left-pace (/ workable-width (max 1 left-windows))
;;         right-pace (/ workable-width (max 1 right-windows))
;;         xs (concat (->> (range left-windows) (map #(* left-pace %)))
;;                    [workable-width]
;;                    (->> (range right-windows) (map #(* right-pace %))))]
;;     (map vector windows xs)))

;; (comment
;;   (get-x-distribution)
;;   #_end)


;; (defn move-current-windows []
;;   (doseq [[win x] (get-x-distribution)]
;;     (yabai/window-move (:wmbb.window/id win) x top-margin)
;;     (Thread/sleep 500)))


;; (defn resize-and-move []
;;   (let [windows (get-current-manageable-windows)]
;;     (resize-current-windows)
;;     (move-current-windows)
;;     (doseq [w (take-while #(not (:has-focus %)) (reverse windows))]
;;       (yabai/window-focus (:id w))
;;       (Thread/sleep 500))
;;     (doseq [w (take-while #(not (:has-focus %)) windows)]
;;       (yabai/window-focus (:id w))
;;       (Thread/sleep 500))
;;     (yabai/window-focus (:id (some #(when (:has-focus %) %) windows)))))

;; (comment
;;   (resize-and-move)
;;   #_end)
