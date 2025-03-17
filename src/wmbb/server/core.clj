(ns wmbb.server.core
  (:require [clojure.java.process :refer [exec]]
            [cheshire.core :refer [parse-string]]
            [clojure.core.async :as async]
            [clojure.java.io :as jio]))


(comment
    (add-deps '{:deps {metosin/malli {:mvn/version "0.17.0"}}})
    (require '[malli.dev :as mdev])
    (require '[malli.dev.pretty :as mpret])

    (mdev/start! {:report (mpret/reporter)})

    (mdev/stop!)

    END)

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

(defn yabai-get
  {:malli/schema [:->
                  [:enum :displays :spaces :windows]
                  [:or [:vector display-schema] [:vector space-schema] [:vector window-schema]]]}
  [what]
  (let [cmd-what (str "--" (name what))
        res-str (exec {:err :stdout} "yabai" "-m" "query" cmd-what)
        res (parse-string res-str true)]
    (into [] res)))

(comment
    (yabai-get :displays)
    (yabai-get :spaces)
    (yabai-get :windows)
    END)

#_(def displays (atom (yabai-get :displays)))
#_(def spaces (atom (yabai-get :spaces)))
#_(def windows (atom (yabai-get :windows)))
(def last-event (atom nil))

(defonce -loop-closed (atom false))
#_(def -update-yabai-info
    (async/go
        (with-open [rdr (jio/reader "/run/wmbb-events")]
          (doseq [ev (line-seq rdr) :while (not @-loop-closed)]
            (reset! last-event ev)
            (reset! displays (yabai-get :displays))
            (reset! spaces (yabai-get :spaces))
            (reset! windows (yabai-get :windows))))
        (reset! -loop-closed true)))

(comment
  (swap! -loop-closed not)
  END)

#_(defn get-display-uuids []
    (->> @displays
         (map :uuid)
         (into #{})))

#_(defn get-space-uuids []
    (->> @spaces
         (map :uuid)
         (into #{})))

#_(defn get-window-ids []
    (->> @windows
         (map :id)
         (into #{})))
