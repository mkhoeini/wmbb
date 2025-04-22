(ns wmbb.behaviors
  (:require
   [wmbb.event :as ev]))



(def behaviors
  {::center-window {:events [::ev/window-focused]
                    :reaction (fn center-window [event]
                                (tap> ["behavior center window" event]))}})
