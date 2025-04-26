(ns wmbb.reconcilers
  (:require
   [wmbb.entity :as ent]))



(def reconcilers
  {::window-position {:archetype ::ent/window
                      :dirty? (fn [old new] false)
                      :to-cmds (fn [old new] [])}})
