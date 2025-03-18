(ns wmbb.yabai
  (:require
   [cheshire.core :refer [parse-string]]
   [clojure.java.process :refer [exec]]))


(def display-schema
    [:map
     [:id :int]
     [:uuid :string]
     [:index :int]
     [:label :string]
     [:frame [:map [:x :double] [:y :double] [:w :double] [:h :double]]]
     [:spaces [:vector :int]]
     [:has-focus :boolean]])

(def space-schema
    [:map
     [:windows [:vector :int]]
     [:index :int]
     [:is-native-fullscreen :boolean]
     [:type :string]
     [:label :string]
     [:id :int]
     [:is-visible :boolean]
     [:has-focus :boolean]
     [:display :int]
     [:last-window :int]
     [:uuid :string]
     [:first-window :int]])

(def window-schema
    [:map
     [:role :string]
     [:has-ax-reference :boolean]
     [:has-shadow :boolean]
     [:space :int]
     [:is-minimized :boolean]
     [:frame [:map [:x :double] [:y :double] [:w :double] [:h :double]]]
     [:is-native-fullscreen :boolean]
     [:is-sticky :boolean]
     [:has-parent-zoom :boolean]
     [:stack-index :int]
     [:title :string]
     [:level :int]
     [:sub-level :int]
     [:can-move :boolean]
     [:pid :int]
     [:is-floating :boolean]
     [:layer :string]
     [:split-type :string]
     [:subrole :string]
     [:is-grabbed :boolean]
     [:opacity :double]
     [:id :int]
     [:sub-layer :string]
     [:is-hidden :boolean]
     [:app :string]
     [:is-visible :boolean]
     [:has-focus :boolean]
     [:display :int]
     [:has-fullscreen-zoom :boolean]
     [:root-window :boolean]
     [:split-child :string]
     [:scratchpad :string]
     [:can-resize :boolean]])

(defn yabai [mod & rest]
  (apply exec {:err :stdout} "yabai" "-m" (name mod) rest))

(defn yabai-query
  {:malli/schema [:->
                  [:enum :displays :spaces :windows]
                  [:or [:vector display-schema] [:vector space-schema] [:vector window-schema]]]}
  [what]
  (let [cmd-what (str "--" (name what))
        res-str (yabai :query cmd-what)
        res (parse-string res-str true)]
    (into [] res)))

(comment
  (yabai-query :displays)
  (yabai-query :spaces)
  (yabai-query :windows)
  END)

(defn yabai-window-resize [window-id width height]
  (yabai :window (str window-id) "--resize" (str "abs:" width ":" height)))

(defn yabai-window-move [window-id width height]
  (yabai :window (str window-id) "--move" (str "abs:" width ":" height)))
