(ns wmbb.entity
  (:require
   [sss.core :as sss]
   [wmbb.tags :as tag]
   [wmbb.yabai :as yabai]))



(def archetypes
  [(sss/make-archetype ::display
                       [::tag/display]
                       {:id [:id]
                        :uuid []}
                       {:index [:id]
                        :x []
                        :y []
                        :w []
                        :h []
                        :spaces [:ref :*]
                        :has-focus []})

   (sss/make-archetype ::space
                       [::tag/space]
                       {:id [:id]
                        :uuid []
                        :type []}
                       {:index [:id]
                        :windows [:ref :*]
                        :display [:ref]
                        :is-native-fullscreen []
                        :is-visible []
                        :has-focus []})

   (sss/make-archetype ::window
                       [::tag/window]
                       {:id [:id]
                        :pid []
                        :app []
                        :role []
                        :subrole []}
                       {:title []
                        :x []
                        :y []
                        :w []
                        :h []
                        :display [:ref]
                        :space [:ref]
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
                        :can-move []})])


(defn- disp->seed [disp]
  (let [spaces (map #(str "space-" %) (:spaces disp))
        res (sss/make-init-entity
             ::display
             (select-keys disp [:id :uuid])
             (-> (select-keys disp [:index :has-focus :x :y :w :h])
                 (assoc :spaces spaces)))]
    (assoc res :db/id (str "display-" (:index disp)))))


(defn- spc->seed [spc]
  (let [display (str "display-" (:display spc))
        windows (map #(str "window-" %) (:windows spc))
        res (sss/make-init-entity
             ::space
             (select-keys spc [:id :uuid :type])
             (-> (select-keys spc [:index :is-native-fullscreen :is-visible :has-focus])
                 (assoc :display display
                        :windows windows)))]
    (assoc res :db/id (str "space-" (:index spc)))))


(defn- win->seed [win]
  (let [space (str "space-" (:space  win))
        display (str "display-" (:display win))
        res (sss/make-init-entity
             ::window
             (select-keys win [:id :pid :app :role :subrole])
             (-> (select-keys win [:title :x :y :w :h :is-minimized :is-native-fullscreen :is-sticky :is-floating :is-grabbed :is-hidden :is-visible
                                   :has-focus :has-fullscreen-zoom :has-ax-reference :has-shadow :has-parent-zoom :can-move :can-resize])
                 (assoc :space space
                        :display display)))]
    (assoc res :db/id (str "window-" (:id win)))))


(defn get-initial-entity-values []
  (concat (map disp->seed (yabai/get-displays))
          (map spc->seed (yabai/get-spaces))
          (map win->seed (yabai/get-windows))))
