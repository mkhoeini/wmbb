(ns wmbb.entity
 (:require
  [sss.entity :refer [defentity]]))



(defentity display
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


(defentity space
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


(defentity window
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


(defentity manager-window
  #:wmbb.manager.window{:window-id [:id]
                        :ref [:ref]
                        :space [:ref]
                        :next [:ref]
                        :prev [:ref]})
