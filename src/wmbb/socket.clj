(ns wmbb.socket
  (:require
   [clojure.core.server :as s]
   [integrant.core :as ig]))


(defn start-server! [port events-chan]
  (s/start-server {:name ::socket-server :port port :accept s/io-prepl :server-daemon false})
  #(s/stop-server ::socket-server))

(comment
  (def incoming-events (chan))
  (def stop (start-server! 5556 incoming-events))
  (stop)
  END)


(defmethod ig/init-key ::server [_ {:keys [port events-chan]}]
  (start-server! port events-chan))

(defmethod ig/halt-key! ::server [_ server-stop-fn]
  (server-stop-fn))
