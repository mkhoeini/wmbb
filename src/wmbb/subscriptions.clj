(ns wmbb.subscriptions
  (:require
   [sss.core :as sss]
   [wmbb.event :as ev]
   [wmbb.yabai :as yabai]))



(def subscriptions
  {::window-created {::signal ::yabai/events
                     ::interesting? (fn filter-window-created [_ signal]
                                      (= :yabai.window/created (:event signal)))
                     ::to-event (fn window-created-to-event [system signal]
                                  (let [win-id (-> signal :data :yabai.window/id)
                                        win (sss/get-entity system ['?e :wmbb.window/id win-id])]
                                    #:sss.event{:name ::ev/window-created
                                                :target win
                                                :data (yabai/get-window win-id)}))}

   ::window-destroyed {::signal ::yabai/events
                       ::interesting? (fn [_ signal] (= :yabai.window/destroyed (:event signal)))
                       ::to-event (fn [_ _] {})}

   ::window-focused {::signal ::yabai/events
                     ::interesting? (fn [_ signal] (= :yabai.window/focused (:event signal)))
                     ::to-event (fn [_ _] {})}

   ::window-moved {::signal ::yabai/events
                   ::interesting? (fn [_ signal] (= :yabai.window/moved (:event signal)))
                   ::to-event (fn [_ _] {})}

   ::window-resized {::signal ::yabai/events
                     ::interesting? (fn [_ signal] (= :yabai.window/resized (:event signal)))
                     ::to-event (fn [_ _] {})}

   ::window-minimized {::signal ::yabai/events
                       ::interesting? (fn [_ signal] (= :yabai.window/minimized (:event signal)))
                       ::to-event (fn [_ _] {})}

   ::window-deminimized {::signal ::yabai/events
                         ::interesting? (fn [_ signal] (= :yabai.window/deminimized (:event signal)))
                         ::to-event (fn [_ _] {})}

   ::window-title-changed {::signal ::yabai/events
                           ::interesting? (fn [_ signal] (= :yabai.window/title_changed (:event signal)))
                           ::to-event (fn [_ _] {})}

   ::space-created {::signal ::yabai/events
                    ::interesting? (fn [_ signal] (= :yabai.space/created (:event signal)))
                    ::to-event (fn [_ _] {})}

   ::space-destroyed {::signal ::yabai/events
                      ::interesting? (fn [_ signal] (= :yabai.space/destroyed (:event signal)))
                      ::to-event (fn [_ _] {})}

   ::space-changed {::signal ::yabai/events
                    ::interesting? (fn [_ signal] (= :yabai.space/changed (:event signal)))
                    ::to-event (fn [_ _] {})}

   ::display-added {::signal ::yabai/events
                    ::interesting? (fn [_ signal] (= :yabai.display/added (:event signal)))
                    ::to-event (fn [_ _] {})}

   ::display-removed {::signal ::yabai/events
                      ::interesting? (fn [_ signal] (= :yabai.display/removed (:event signal)))
                      ::to-event (fn [_ _] {})}

   ::display-moved {::signal ::yabai/events
                    ::interesting? (fn [_ signal] (= :yabai.display/moved (:event signal)))
                    ::to-event (fn [_ _] {})}

   ::display-resized {::signal ::yabai/events
                      ::interesting? (fn [_ signal] (= :yabai.display/resized (:event signal)))
                      ::to-event (fn [_ _] {})}

   ::display-changed {::signal ::yabai/events
                      ::interesting? (fn [_ signal] (= :yabai.display/changed (:event signal)))
                      ::to-event (fn [_ _] {})}})
