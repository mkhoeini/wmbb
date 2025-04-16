(ns wmbb.event)


(def events
  [::window-created
   ::window-destroyed
   ::window-focused
   ::window-moved
   ::window-resized
   ::window-minimized
   ::window-deminimized
   ::window-title-changed

   ::space-created
   ::space-destroyed
   ::space-changed

   ::display-added
   ::display-removed
   ::display-moved
   ::display-resized
   ::display-changed])
