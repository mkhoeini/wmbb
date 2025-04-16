(ns wmbb.subscriptions
  (:require
   [sss.core :as sss]
   [wmbb.event :as ev]
   [wmbb.yabai :as yabai]))



(defn window-signal-to-event [signal event-name]
  (let [win-id (:yabai.window/id signal)
        win (sss/get-entity ['?e :wmbb.window/id win-id])]
    #:sss.event{:name event-name
                :target win
                :data (yabai/get-window win-id)}))


(defn space-signal-to-event [signal event-name]
  (let [spc-index (:yabai.space/id signal)
        spc (sss/get-entity ['?e :wmbb.space/index spc-index])]
    #:sss.event{:name event-name
                :target spc
                :data (yabai/get-space spc-index)}))


(defn display-signal-to-event [signal event-name]
  (let [disp-index (:yabai.display/id signal)
        disp (sss/get-entity ['?e :wmbb.display/index disp-index])]
    #:sss.event{:name event-name
                :target disp
                :data (yabai/get-display disp-index)}))


(defn to-event [signal event-name]
  (case (namespace (:event signal))
    "yabai.window" (window-signal-to-event signal event-name)
    "yabai.space" (space-signal-to-event signal event-name)
    "yabai.display" (display-signal-to-event signal event-name)))


(defn make-sub [yabai-event-name event-name]
  {:signal ::yabai/events
   :interesting? (fn filter-yabai-events [signal] (= yabai-event-name (:event signal)))
   :to-event (fn [signal] (to-event signal event-name))})


(def subscriptions
  {::window-created (make-sub :yabai.window/created ::ev/window-created)
   ::window-destroyed (make-sub :yabai.window/destroyed ::ev/window-destroyed)
   ::window-focused (make-sub :yabai.window/focused ::ev/window-focused)
   ::window-moved (make-sub :yabai.window/moved ::ev/window-moved)
   ::window-resized (make-sub :yabai.window/resized ::ev/window-resized)
   ::window-minimized (make-sub :yabai.window/minimized ::ev/window-minimized)
   ::window-deminimized (make-sub :yabai.window/deminimized ::ev/window-deminimized)
   ::window-title-changed (make-sub :yabai.window/title_changed :ev/window-title-changed)

   ::space-created (make-sub :yabai.space/created ::ev/space-created)
   ::space-destroyed (make-sub :yabai.space/destroyed ::ev/space-destroyed)
   ::space-changed (make-sub :yabai.space/changed :ev/space-changed)

   ::display-added (make-sub :yabai.display/added ::ev/display-added)
   ::display-removed (make-sub :yabai.display/removed ::ev/display-removed)
   ::display-moved (make-sub :yabai.display/moved ::ev/display-moved)
   ::display-resized (make-sub :yabai.display/resized ::ev/display-resized)
   ::display-changed (make-sub :yabai.display/changed ::ev/display-changed)})
