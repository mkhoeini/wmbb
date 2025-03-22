(ns wmbb.system
  (:require
   [integrant.core :as ig]))

(def config
  {} #_{:adapter/jetty {:port (ig/profile {:dev 8080 :prod 80}), :handler (ig/ref :handler/greet)}
        :handler/greet {:name "Alice"}})

(defmethod ig/init-key :adapter/jetty [_ {:keys [handler] :as opts}]
  nil #_(jetty/run-jetty handler (-> opts (dissoc :handler) (assoc :join? false))))

(defmethod ig/init-key :handler/greet [_ {:keys [name]}]
  (fn [_] nil #_(resp/response (str "Hello " name))))

(defmethod ig/halt-key! :adapter/jetty [_ server]
  (.stop server))

(ig/load-namespaces config)

(defn make-system []
  (-> config
      (ig/deprofile [:dev])
      ig/init))
