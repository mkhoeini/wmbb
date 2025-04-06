(ns sss.system
  (:require
    [integrant.core :as ig]
    [sss.event]))




(def default-config
  {:event-chan {:buf 1000}})


(defn create-system [opts]
  (let [config (merge-with #(if (map? %1) (merge %1 %2) %2) default-config opts)
        system (ig/init config)]
    system))

(defn halt-system! [system]
  (ig/halt! system))
