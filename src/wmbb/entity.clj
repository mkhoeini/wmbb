(ns wmbb.entity
 (:require
  [sss.core :as sss]
  [wmbb.yabai :as yabai]))



(def entities
  [(sss/make-entity-archetype ::display
                              #:wmbb.display{:id [:id]
                                             :index [:id]
                                             :uuid []
                                             :label []
                                             :x []
                                             :y []
                                             :w []
                                             :h []
                                             :spaces [:ref :*]
                                             :has-focus []})

   (sss/make-entity-archetype ::space
                              #:wmbb.space{:id [:id]
                                           :index [:id]
                                           :uuid []
                                           :type []
                                           :label []
                                           :windows [:ref :*]
                                           :display [:ref]
                                           :first-window []
                                           :last-window []
                                           :is-native-fullscreen []
                                           :is-visible []
                                           :has-focus []})

   (sss/make-entity-archetype ::window
                              #:wmbb.window{:id [:id]
                                            :title []
                                            :pid []
                                            :app []
                                            :x []
                                            :y []
                                            :w []
                                            :h []
                                            :opacity []
                                            :stack-index []
                                            :level []
                                            :sub-level []
                                            :layer []
                                            :sub-layer []
                                            :role []
                                            :subrole []
                                            :split-type []
                                            :split-child []
                                            :scratchpad []
                                            :display [:ref]
                                            :space [:ref]
                                            :root-window []
                                            :is-minimized []
                                            :is-native-fullscreen []
                                            :is-sticky []
                                            :is-floating []
                                            :is-grabbed []
                                            :is-hidden []
                                            :is-visible []
                                            :has-ax-reference []
                                            :has-shadow []
                                            :has-parent-zoom []
                                            :has-focus []
                                            :has-fullscreen-zoom []
                                            :can-resize []
                                            :can-move []})

   (sss/make-entity-archetype ::manager-window
                              #:wmbb.manager.window{:window-id [:id]
                                                    :ref [:ref]
                                                    :space [:ref]
                                                    :next [:ref]
                                                    :prev [:ref]})])


(defn- disp->seed [disp]
  (assoc disp
         :db/id (str "display-" (:wmbb.display/index disp))
         :wmbb.display/spaces (map #(str "space-" (second %)) (:wmbb.display/spaces disp))))


(defn- spc->seed [spc]
  (assoc spc
         :db/id (str "space-" (:wmbb.space/index spc))
         :wmbb.space/display (str "display-" (-> spc :wmbb.space/display second))
         :wmbb.space/windows (map #(str "window-" (second %)) (:wmbb.space/windows spc))))


(defn- win->seed [win]
  (assoc win
         :db/id (str "window-" (:wmbb.window/id win))
         :wmbb.window/space (str "space-" (-> win :wmbb.window/space second))
         :wmbb.window/display (str "display-" (-> win :wmbb.window/display second))))


(defn get-initial-entity-values []
  {::display (map disp->seed (yabai/get-displays))
   ::space (map spc->seed (yabai/get-spaces))
   ::window (map win->seed (yabai/get-windows))})
