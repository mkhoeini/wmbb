(ns wmbb.data
  (:require
   [sss.db :as db]))

(defn get-displays []
  (db/find* '[?e :wmbb.display/id]))


(defn get-spaces []
  (db/find* '[?e :wmbb.space/id]))


(defn get-windows []
  (db/find* '[?e :wmbb.window/id]))


(comment
  (get-displays)
  (get-spaces)
  (get-windows)
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
  (db/find* '[?s :wmbb.space/has-focus true]
            '[?s :wmbb.space/windows ?w]
            '[?e :wmbb.window/id ?w]))

(comment
  (get-active-space-windows)
  #_END)


(defn- split-keys
  "split a seq of maps to two seqs of maps splitting the set of keys.
  s is the seq of maps
  both-k is a key that should exist on both resulting seqs
  ks is the keys that should be absent on the first and present on the second result seq"
  [s both-k & ks]
  (let [res (for [x s]
              [(apply dissoc x ks)
               (select-keys x (conj ks both-k))])]
    [(map first res)
     (map second res)]))

(defn insert-info [displays spaces windows]
  (let [[disp-ins disp-ref] (split-keys displays :wmbb.display/id :wmbb.display/spaces)
        [spc-ins spc-ref] (split-keys spaces :wmbb.space/id :wmbb.space/display :wmbb.space/windows)
        [win-ins win-ref] (split-keys windows :wmbb.window/id :wmbb.window/display :wmbb.window/space)]
    (apply db/transact! (concat disp-ins spc-ins win-ins))
    (apply db/transact! (concat disp-ref spc-ref win-ref))))


(defn update-info [displays spaces windows]
  (apply db/transact! (concat displays spaces windows)))


(defn delete-info [displays spaces windows]
  (let [disp-txs (for [d displays] [:db/retractEntity (:db/id d)])
        spc-txs (for [s spaces] [:db/retractEntity (:db/id s)])
        win-txs (for [w windows] [:db/retractEntity (:db/id w)])]
    (apply db/transact! (concat disp-txs spc-txs win-txs))))


(defn- diff-by [existing given attr]
  (let [existing-ids (set (map attr existing))
        given-ids (set (map attr given))]
    {:deleted (filter #(not (given-ids (attr %))) existing)
     :updated (filter #(existing-ids (attr %)) given)
     :inserted (filter #(not (existing-ids (attr %))) given)}))

(defn diff-displays [displays]
  (diff-by (get-displays) displays :wmbb.display/id))

(defn diff-spaces [spaces]
  (diff-by (get-spaces) spaces :wmbb.space/id))

(defn diff-windows [windows]
  (diff-by (get-windows) windows :wmbb.window/id))


(defn update-data [displays spaces windows]
  (let [displays-diff (diff-displays displays)
        spaces-diff (diff-spaces spaces)
        windows-diff (diff-windows windows)]
    (insert-info (:inserted displays-diff) (:inserted spaces-diff) (:inserted windows-diff))
    (update-info (:updated displays-diff) (:updated spaces-diff) (:updated windows-diff))
    (delete-info (:deleted displays-diff) (:deleted spaces-diff) (:deleted windows-diff))
    {:inserted (concat
                (for [d (:inserted displays-diff)] (db/find1 ['?e :wmbb.display/id (:wmbb.display/id d)]))
                (for [s (:inserted spaces-diff)] (db/find1 ['?e :wmbb.space/id (:wmbb.space/id s)]))
                (for [w (:inserted windows-diff)] (db/find1 ['?e :wmbb.window/id (:wmbb.window/id w)])))
     :updated (concat
               (for [d (:updated displays-diff)] (db/find1 ['?e :wmbb.display/id (:wmbb.display/id d)]))
               (for [s (:updated spaces-diff)] (db/find1 ['?e :wmbb.space/id (:wmbb.space/id s)]))
               (for [w (:updated windows-diff)] (db/find1 ['?e :wmbb.window/id (:wmbb.window/id w)])))
     :deleted (concat
               (for [d (:deleted displays-diff)] (db/find1 ['?e :wmbb.display/id (:wmbb.display/id d)]))
               (for [s (:deleted spaces-diff)] (db/find1 ['?e :wmbb.space/id (:wmbb.space/id s)]))
               (for [w (:deleted windows-diff)] (db/find1 ['?e :wmbb.window/id (:wmbb.window/id w)])))}))
