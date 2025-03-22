(ns wmbb.system
  (:require
   [integrant.core :as ig]
   [wmbb.server.socket :as server-socket]
   [wmbb.server.data :as server-data]))


(def config
  {::server-socket/incoming-events-channel {}
   ::server-socket/server {:port 5556 :events-chan (ig/ref ::server-socket/incoming-events-channel)}

   ::server-data/displays {}
   ::server-data/spaces {}
   ::server-data/windows {}})


(defn make-system []
  (-> config
      (ig/deprofile [:dev])
      ig/init))
