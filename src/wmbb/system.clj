(ns wmbb.system
  (:require
   [mount.core :as mount :refer [defstate]]
   [sss.core :as sss]
   [wmbb.entity :as entity]
   [wmbb.socket]
   [wmbb.yabai :as yabai]
   [wmbb.subscriptions :as subs]))



(defn disp->seed [disp]
  (assoc disp
         :db/id (str "display-" (:wmbb.display/index disp))
         :wmbb.display/spaces (map #(str "space-" (second %)) (:wmbb.display/spaces disp))))


(defn spc->seed [spc]
  (assoc spc
         :db/id (str "space-" (:wmbb.space/index spc))
         :wmbb.space/display (str "display-" (-> spc :wmbb.space/display second))
         :wmbb.space/windows (map #(str "window-" (second %)) (:wmbb.space/windows spc))))


(defn win->seed [win]
  (assoc win
         :db/id (str "window-" (:wmbb.window/id win))
         :wmbb.window/space (str "space-" (-> win :wmbb.window/space second))
         :wmbb.window/display (str "display-" (-> win :wmbb.window/display second))))


(defstate system-def
  :start (when (.isAlive yabai/yabai-process)
           {:entities [entity/display entity/space entity/window entity/manager-window]
            :init (concat
                   (map disp->seed (yabai/get-displays))
                   (map spc->seed (yabai/get-spaces))
                   (map win->seed (yabai/get-windows)))
            :signals {:yabai/events yabai/events}
            :subscriptions subs/subscriptions
            :reconciler (constantly nil)
            :commands {:resize-window 123}
            :tags {:abc [:some :behaviors]}
            :behaviors {:abc {:events [:ev1 :ev2]}}}))


(defstate system
  :start (sss/create-system system-def)
  :stop (sss/halt-system! system))

(comment
  (require '[sss.subscription :as s])
  (s/add-sub! system (:sss.subscription/name subs/window-moved) {})
  (s/update-sub-status! system {:db/id 22} :enable)
  (s/get-all-subs system)
  #_end)


(defn start-system! []
  (mount/start))


(defn stop-system! []
  (mount/stop))
