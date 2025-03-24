(ns wmbb.events
  (:require
   [wmbb.commands :refer [update-data]]))



(defmulti add-event-multi :event)


(derive :yabai.window/created ::window)
(derive :yabai.window/destroyed ::window)
(derive :yabai.window/focused ::window)
(derive :yabai.window/moved ::window)
(derive :yabai.window/resized ::window)
(derive :yabai.window/minimized ::window)
(derive :yabai.window/deminimized ::window)
(derive :yabai.window/title-changed ::window)

(derive :yabai.space/created ::space)
(derive :yabai.space/destroyed ::space)
(derive :yabai.space/changed ::space)

(derive :yabai.display/added ::display)
(derive :yabai.display/removed ::display)
(derive :yabai.display/moved ::display)
(derive :yabai.display/resized ::display)
(derive :yabai.display/changed ::display)

(derive ::display ::info)
(derive ::space ::info)
(derive ::window ::info)


(defmethod add-event-multi ::info [event]
  (update-data))


(defmethod add-event-multi :default [event]
  (tap> (ex-info "Unsupported event" {:event event})))


(def last-event (atom nil))


(defn add-event [event]
  (reset! last-event event)
  (add-event-multi event))
