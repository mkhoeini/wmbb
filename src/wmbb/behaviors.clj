(ns wmbb.behaviors
  (:require
   [wmbb.event :as ev]
   [wmbb.tags :as tag]))



(def behaviors
  {::center-window {:events [::ev/window-focused]
                    :tags [::tag/window]
                    :reaction (fn center-window [event]
                                [{:db/id (get-in event [:target :db/id])
                                  :center true}])}})
