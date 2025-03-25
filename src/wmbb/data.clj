(ns wmbb.data
  (:require
   [wmbb.db :as db]))



(defn get-display-ids []
  (->> (db/get-displays)
       (map :wmbb.display/id)))


(defn get-space-ids []
  (->> (db/get-spaces)
       (map :wmbb.space/id)))


(defn get-window-ids []
  (->> (db/get-windows)
       (map :wmbb.window/id)))


(comment
  (get-display-ids)
  (get-space-ids)
  (get-window-ids)
  #_END)


(defn get-active-display []
  (db/find1 '[?e :wmbb.display/has-focus true]))


(defn get-active-space []
  (db/find1 '[?e :wmbb.space/has-focus true]))


(defn get-active-window []
  (db/find1 '[?e :wmbb.window/has-focus true]))


(comment
  (get-active-display)
  (get-active-space)
  (get-active-window)
  #_END)


(defn get-active-space-windows []
  (db/find* '([?s :wmbb.space/has-focus true]
              [?s :wmbb.space/windows ?w]
              [?e :wmbb.window/id ?w])))

(comment
  (get-active-space-windows)
  #_END)


(defn- display->tx [{:keys [id uuid index label frame spaces has-focus]}]
  #:wmbb.display{:id id
                 :uuid uuid
                 :index index
                 :label label
                 :has-focus has-focus
                 :spaces spaces
                 :x (:x frame)
                 :y (:y frame)
                 :w (:w frame)
                 :h (:h frame)})


(defn- space->tx [{:keys [windows index is-native-fullscreen type label id is-visible has-focus display last-window uuid first-window]}]
  #:wmbb.space{:id id
               :label label
               :type type
               :is-native-fullscreen is-native-fullscreen
               :index index
               :windows windows
               :is-visible is-visible
               :has-focus has-focus
               :display display
               :last-window last-window
               :uuid uuid
               :first-window first-window})


(defn- window->tx [window]
  #:wmbb.window{:role (:role window)
                :has-ax-reference (:has-ax-reference window)
                :has-shadow (:has-shadow window)
                :space (:space window)
                :is-minimized (:is-minimized window)
                :x (-> window :frame :x)
                :y (-> window :frame :y)
                :w (-> window :frame :w)
                :h (-> window :frame :h)
                :is-native-fullscreen (:is-native-fullscreen window)
                :is-sticky (:is-sticky window)
                :has-parent-zoom (:has-parent-zoom window)
                :stack-index (:stack-index window)
                :title (:title window)
                :level (:level window)
                :sub-level (:sub-level window)
                :can-move (:can-move window)
                :pid (:pid window)
                :is-floating (:is-floating window)
                :layer (:layer window)
                :split-type (:split-type window)
                :subrole (:subrole window)
                :is-grabbed (:is-grabbed window)
                :opacity (:opacity window)
                :id (:id window)
                :sub-layer (:sub-layer window)
                :is-hidden (:is-hidden window)
                :app (:app window)
                :is-visible (:is-visible window)
                :has-focus (:has-focus window)
                :display (:display window)
                :has-fullscreen-zoom (:has-fullscreen-zoom window)
                :root-window (:root-window window)
                :split-child (:split-child window)
                :scratchpad (:scratchpad window)
                :can-resize (:can-resize window)})


(defn- get-update-displays-transations [displays]
  (let [old-ids (get-display-ids)
        new-ids (set (map :id displays))
        ids-to-del (filter #(not (new-ids %)) old-ids)
        del-tx (map #(do [:db/retractEntity [:wmbb.display/id %]]) ids-to-del)
        add-tx (map display->tx displays)]
    (concat del-tx add-tx)))


(defn- get-update-spaces-transations [spaces]
  (let [old-ids (get-space-ids)
        new-ids (set (map :id spaces))
        ids-to-del (filter #(not (new-ids %)) old-ids)
        del-tx (map #(do [:db/retractEntity [:wmbb.space/id %]]) ids-to-del)
        add-tx (map space->tx spaces)]
    (concat del-tx add-tx)))


(defn- get-update-windows-transations [windows]
  (let [old-ids (get-window-ids)
        new-ids (set (map :id windows))
        ids-to-del (filter #(not (new-ids %)) old-ids)
        del-tx (map #(do [:db/retractEntity [:wmbb.window/id %]]) ids-to-del)
        add-tx (map window->tx windows)]
    (concat del-tx add-tx)))


(defn update-data [displays spaces windows]
  (let [displays-tx (get-update-displays-transations displays)
        spaces-tx (get-update-spaces-transations spaces)
        windows-tx (get-update-windows-transations windows)]
    (db/transact (concat displays-tx spaces-tx windows-tx))))
