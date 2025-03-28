(ns wmbb.commands
  (:require
   [wmbb.data :as data]
   [wmbb.manager :as manager]
   [wmbb.yabai :as yabai]))



(defn update-data []
  (let [displays (yabai/query :displays)
        displays-diff (data/get-displays-diff displays)
        spaces (yabai/query :spaces)
        spaces-diff (data/get-spaces-diff spaces)
        windows (yabai/query :windows)
        windows-diff (data/get-windows-diff windows)]
    (data/update-data displays-diff spaces-diff windows-diff)
    (doseq [display (:inserted displays-diff)]
      (manager/insert-display display))
    (doseq [display (:deleted displays-diff)]
      (manager/delete-display display))
    (doseq [display (:updated displays-diff)]
      (manager/update-display display))
    (doseq [space (:inserted spaces-diff)]
      (manager/insert-space space))
    (doseq [space (:deleted spaces-diff)]
      (manager/delete-space space))
    (doseq [space (:updated spaces-diff)]
      (manager/update-space space))
    (doseq [window (:inserted windows-diff)]
      (manager/insert-window window))
    (doseq [window (:deleted windows-diff)]
      (manager/delete-window window))
    (doseq [window (:updated windows-diff)]
      (manager/update-window window))))


(comment
  (update-data)
  #_end)
