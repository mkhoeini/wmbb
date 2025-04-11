(ns wmbb.controller
  (:require
    [wmbb.yabai :as yabai]))



(defn apply-layout [manager-windows]
  (doseq [w manager-windows
          :let [{:wmbb.manager.window/keys [ref target-x target-y target-w target-h]} w
                {window-id :wmbb.window/id} ref]]
    (yabai/window-move window-id target-x target-y)
    (yabai/window-resize window-id target-w target-h)))

(comment
  (require '[wmbb.manager :as m])
  (apply-layout (m/get-active-manager-windows))
  #_end)
