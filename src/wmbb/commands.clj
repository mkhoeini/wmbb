(ns wmbb.commands
  (:require
   [wmbb.data :as data]
   [wmbb.yabai :refer [yabai-query]]))



(defn update-data []
  (let [displays (yabai-query :displays)
        spaces (yabai-query :spaces)
        windows (yabai-query :windows)]
    (data/update-data displays spaces windows)))

(comment
  (update-data)
  #_end)
