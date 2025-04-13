(ns wmbb.subscriptions
  (:require
   [sss.subscription :refer [defsub]]))



(defsub :yabai window-created [system signal conf]
  (= :yabai.window/created (:event signal))
  {})


(defsub :yabai window-destroyed [system signal conf]
  (= :yabai.window/destroyed (:event signal))
  {})


(defsub :yabai window-focused [system signal conf]
  (= :yabai.window/focused (:event signal))
  {})


(defsub :yabai window-moved [system signal conf]
  (= :yabai.window/moved (:event signal))
  {})


(defsub :yabai window-resized [system signal conf]
  (= :yabai.window/resized (:event signal))
  {})


(defsub :yabai window-minimized [system signal conf]
  (= :yabai.window/minimized (:event signal))
  {})


(defsub :yabai window-deminimized [system signal conf]
  (= :yabai.window/deminimized (:event signal))
  {})


(defsub :yabai window-title-changed [system signal conf]
  (= :yabai.window/title_changed (:event signal))
  {})


(defsub :yabai space-created [system signal conf]
  (= :yabai.space/created (:event signal))
  {})


(defsub :yabai space-destroyed [system signal conf]
  (= :yabai.space/destroyed (:event signal))
  {})


(defsub :yabai space-changed [system signal conf]
  (= :yabai.space/changed (:event signal))
  {})


(defsub :yabai display-added [system signal conf]
  (= :yabai.display/added (:event signal))
  {})


(defsub :yabai display-removed [system signal conf]
  (= :yabai.display/removed (:event signal))
  {})


(defsub :yabai display-moved [system signal conf]
  (= :yabai.display/moved (:event signal))
  {})


(defsub :yabai display-resized [system signal conf]
  (= :yabai.display/resized (:event signal))
  {})


(defsub :yabai display-changed [system signal conf]
  (= :yabai.display/changed (:event signal))
  {})


(def subscriptions [window-created
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
