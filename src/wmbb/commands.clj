(ns wmbb.commands
  (:require
   [wmbb.data :as data]
   [wmbb.manager :as manager]
   [wmbb.yabai :as yabai]))



(defn update-data []
  (let [displays (yabai/get-displays)
        spaces (yabai/get-spaces)
        windows (yabai/get-windows)
        {:keys [inserted updated deleted]} (data/update-data displays spaces windows)]
    (doseq [w (filter :wmbb.window/id inserted)]
      (manager/insert-window w))
    (doseq [w (filter :wmbb.window/id updated)]
      (manager/update-window w))
    (doseq [w (filter :wmbb.window/id deleted)]
      (manager/delete-window w))))


(comment
  (require '[wmbb.db :refer [db]])
  db
  (update-data)
  (let [displays (yabai/get-displays)
        spaces (yabai/get-spaces)
        windows (yabai/get-windows)]
    (data/update-data displays spaces windows))
  #_end)
