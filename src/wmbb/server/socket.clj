(ns wmbb.server.socket
  (:require
   [clojure.core.async :refer [<!! chan put!]]
   [clojure.edn :as edn]
   [clojure.java.io :as io])
  (:import
   (java.io PushbackReader)
   (java.net ServerSocket)))

(def incoming-events (chan))

(defn- -handler [client]
  (try
    (with-open [client client
                reader (PushbackReader. (io/reader (.getInputStream client)))]
      (let [event (edn/read reader)]
        (println "read event " event)
        (put! incoming-events event)))
    (catch Exception e
      (println "Error" (.getMessage e)))))

(defn start-server! []
  (future
    (with-open [server (ServerSocket. 5556)]
      (println "server opened at 5556")
      (loop [client (.accept server)]
        (println "new connection")
        (future (-handler client))
        (recur (.accept server))))))

(comment
  (start-server!)
  (<!! incoming-events)
  END)
