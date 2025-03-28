(ns wmbb.socket
  (:require
   [clojure.core.server :as s]
   [mount.core :refer [defstate]]))


(def port 5556)

(defstate socket-server
  "prepl socket server"
  :start (s/start-server {:name ::socket-server :port port :accept `s/io-prepl :server-daemon true})
  :stop (s/stop-server ::socket-server))
