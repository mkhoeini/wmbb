(ns wmbb.yabai
  (:require
   [cheshire.core :refer [parse-string]]
   [clojure.java.io :as io]
   [clojure.java.process :as process]
   [mount.core :refer [defstate]]
   [wmbb.socket :as s]
   [sss.core :as sss])
  (:import
   (java.io File)))



(defn put-event! [ev]
  #_(tap> ["got event" ev])
  (sss/send-signal ::events ev))


(defonce ^:private config-file
  (let [conf (io/file (io/resource "yabairc"))
        temp (File/createTempFile "yabairc" "")]
    (io/copy conf temp)
    (println "yabai config is written into" (.getAbsolutePath temp))
    temp))

(defstate ^:private yabai-output :start (File/createTempFile "yabai" "out"))

(defstate yabai-process
  :start (when s/socket-server
           (let [p (process/start
                    {:err :stdout :out (process/to-file yabai-output)}
                    "yabai" "--config" (.getAbsolutePath config-file))]
             ;; Give yabai process a second to properly start
             (Thread/sleep 1000)
             (println "Started yabai. Output is written into" (.getAbsolutePath yabai-output))
             p))
  :stop (.destroy yabai-process))


(defn yabai [mod & rest]
  (apply process/exec {:err :stdout} "yabai" "-m" (name mod) rest))


(defn- display->entity [{:keys [id uuid index label frame spaces has-focus]}]
  #:wmbb.display{:id id
                 :uuid uuid
                 :index index
                 :label label
                 :has-focus has-focus
                 :spaces (map #(do [:wmbb.space/index %]) spaces)
                 :x (:x frame)
                 :y (:y frame)
                 :w (:w frame)
                 :h (:h frame)})

(defn get-displays []
  (let [res (-> (yabai :query "--displays")
                (parse-string true))]
    (map display->entity res)))

(defn get-display [index]
  (let [res (-> (yabai :query "--displays" "--display" (str index))
                (parse-string true))]
    (display->entity res)))

(comment
  (get-displays)
  #_end)


(defn- space->entity [{:keys [windows index is-native-fullscreen type label id is-visible has-focus display last-window uuid first-window]}]
  #:wmbb.space{:id id
               :uuid uuid
               :label label
               :type type
               :index index
               :is-native-fullscreen is-native-fullscreen
               :is-visible is-visible
               :has-focus has-focus
               :windows (map #(do [:wmbb.window/id %]) windows)
               :display [:wmbb.display/index display]
               :first-window first-window
               :last-window last-window})

(defn get-spaces []
  (let [res (-> (yabai :query "--spaces")
                (parse-string true))]
    (map space->entity res)))

(defn get-space [index]
  (let [res (-> (yabai :query "--spaces" "--space" (str index))
                (parse-string true))]
    (space->entity res)))

(comment
  (get-spaces)
  #_end)


(defn- window->entity [window]
  #:wmbb.window{:id (:id window)
                :title (:title window)
                :pid (:pid window)
                :app (:app window)
                :role (:role window)
                :subrole (:subrole window)
                :x (-> window :frame :x)
                :y (-> window :frame :y)
                :w (-> window :frame :w)
                :h (-> window :frame :h)
                :is-minimized (:is-minimized window)
                :is-native-fullscreen (:is-native-fullscreen window)
                :is-sticky (:is-sticky window)
                :is-floating (:is-floating window)
                :is-grabbed (:is-grabbed window)
                :is-hidden (:is-hidden window)
                :is-visible (:is-visible window)
                :has-focus (:has-focus window)
                :has-fullscreen-zoom (:has-fullscreen-zoom window)
                :has-ax-reference (:has-ax-reference window)
                :has-shadow (:has-shadow window)
                :has-parent-zoom (:has-parent-zoom window)
                :can-move (:can-move window)
                :can-resize (:can-resize window)
                :stack-index (:stack-index window)
                :level (:level window)
                :sub-level (:sub-level window)
                :layer (:layer window)
                :sub-layer (:sub-layer window)
                :split-type (:split-type window)
                :opacity (:opacity window)
                :display [:wmbb.display/index (:display window)]
                :space [:wmbb.space/index (:space window)]
                :root-window (:root-window window)
                :split-child (:split-child window)
                :scratchpad (:scratchpad window)})

(defn get-windows []
  (let [res (-> (yabai :query "--windows")
                (parse-string true))]
    (map window->entity res)))

(defn get-window [id]
  (let [res (-> (yabai :query "--windows" "--window" (str id))
                (parse-string true))]
    (window->entity res)))

(comment
  (get-windows)
  (get-window 26567)
  #_end)


(defn window-resize [window-id width height]
  (yabai :window (str window-id) "--resize" (str "abs:" width ":" height)))

(defn window-move [window-id x y]
  (yabai :window (str window-id) "--move" (str "abs:" x ":" y)))

(defn window-focus [window-id]
  (yabai :window "--focus" (str window-id)))
