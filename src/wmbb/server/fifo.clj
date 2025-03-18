(ns wmbb.server.fifo
  (:require [clojure.java.shell :refer [sh]]
            [clojure.java.io :as io]
            [clojure.core.async :as async])
  (:import [java.nio.file Files Paths]))

(def fifo-path "/tmp/wmbb-events")

(defn- -fifo-exists? []
  (.exists (io/file fifo-path)))

(defn- -cleanup-fifo []
  (try
    (when (-fifo-exists?)
      (Files/delete (Paths/get fifo-path (into-array String []))))
    (catch Exception e
      (println "Failed to delete FIFO:" (.getMessage e)))))

(defn create-fifo []
  (when-not (-fifo-exists?)
    (let [{:keys [exit err]} (sh "mkfifo" fifo-path)]
      (if (zero? exit)
        (.addShutdownHook (Runtime/getRuntime)
                          (Thread. -cleanup-fifo))
        (println "Failed to create FIFO:" err))
      (zero? exit))))

(def -fifo-lines-chan (atom nil))

(defn get-fifo-lines-chan []
  (when-not @-fifo-lines-chan
    (create-fifo)
    (let [chan (async/chan)]
      (async/go
        (while @-fifo-lines-chan
          (with-open [rdr (io/reader fifo-path)]
            (doseq [ev (line-seq rdr) :while @-fifo-lines-chan]
              (async/>! chan ev)))))
      (reset! -fifo-lines-chan chan)))
  @-fifo-lines-chan)

(comment
  (async/<!! (get-fifo-lines-chan))
  (reset! -fifo-lines-chan nil)
  END)
