(ns wmbb.server.socket
  (:require
   [clojure.core.async :refer [<!! chan put!]]
   [clojure.edn :as edn]
   [clojure.java.io :as io]
   [integrant.core :as ig])
  (:import
   (java.io PushbackReader)
   (java.net ServerSocket)))


(defmethod ig/init-key ::incoming-events-channel [_ _]
  (chan))


(defn- -handler [client incoming-events]
  (try
    (with-open [client client
                reader (PushbackReader. (io/reader (.getInputStream client)))]
      (let [event (edn/read reader)]
        (println "read event " event)
        (put! incoming-events event)))
    (catch Exception e
      (println "Error" (.getMessage e)))))


(defn start-server! [port events-chan]
  (let [stop (atom false)
        _loop (future
                (with-open [server (ServerSocket. port)]
                  (println "server opened at" port)
                  (loop [client (.accept server)]
                    (println "new connection")
                    (future (-handler client events-chan))
                    (when-not @stop (recur (.accept server))))))]
    {:stop #(do (reset! stop true) (.cancel _loop true))
     :loop _loop}))

(comment
  (def incoming-events (chan))
  (def -s (start-server! 5556 incoming-events))
  ((:stop -s))
  (<!! incoming-events)
  END)


(defmethod ig/init-key ::server [_ {:keys [port events-chan]}]
  (start-server! port events-chan))

(defmethod ig/halt-key! ::server [_ server]
  ((:stop server)))
