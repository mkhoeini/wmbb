(ns wmbb.commands
  (:require
    [wmbb.yabai :as yabai]))



(def commands
  {::move-window {:retry (constantly nil)
                  :exec (fn [{:keys [data]}]
                          (yabai/window-move (:window-id data) (:x data) (:y data))
                          true)}})
