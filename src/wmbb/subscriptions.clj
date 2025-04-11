(ns wmbb.subscriptions
  (:require
   [sss.subscription :refer [defsub]]))



(defsub :yabai window-created [system signal]
  (= :yabai.window/created (:event signal))
  {})


(defsub :yabai window-destroyed [system signal]
  (= :yabai.window/destroyed (:event signal))
  {})


(defsub :yabai window-focused [system signal]
  (= :yabai.window/focused (:event signal))
  {})


(defsub :yabai window-moved [system signal]
  (= :yabai.window/moved (:event signal))
  {})


(defsub :yabai window-resized [system signal]
  (= :yabai.window/resized (:event signal))
  {})


(defsub :yabai window-minimized [system signal]
  (= :yabai.window/minimized (:event signal))
  {})


(defsub :yabai window-deminimized [system signal]
  (= :yabai.window/deminimized (:event signal))
  {})


(defsub :yabai window-title-changed [system signal]
  (= :yabai.window/title_changed (:event signal))
  {})


(defsub :yabai space-created [system signal]
  (= :yabai.space/created (:event signal))
  {})


(defsub :yabai space-destroyed [system signal]
  (= :yabai.space/destroyed (:event signal))
  {})


(defsub :yabai space-changed [system signal]
  (= :yabai.space/changed (:event signal))
  {})


(defsub :yabai display-added [system signal]
  (= :yabai.display/added (:event signal))
  {})


(defsub :yabai display-removed [system signal]
  (= :yabai.display/removed (:event signal))
  {})


(defsub :yabai display-moved [system signal]
  (= :yabai.display/moved (:event signal))
  {})


(defsub :yabai display-resized [system signal]
  (= :yabai.display/resized (:event signal))
  {})


(defsub :yabai display-changed [system signal]
  (= :yabai.display/changed (:event signal))
  {})


(defn subscriptions [window-created
                     window-destroyed
                     window-focused
                     window-moved
                     window-resized
                     window-minimized
                     window-deminimized
                     window-title-changed
                     space-created
                     space-destroyed
                     space-changed
                     display-added
                     display-removed
                     display-moved
                     display-resized
                     display-changed])
