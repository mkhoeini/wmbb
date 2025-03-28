(ns wmbb.cli-main
  (:gen-class)
  (:require
   [malli.core :as m]
   [piotr-yuxuan.malli-cli :as malli-cli]
   [wmbb.system :refer [start-system!]]))



(def Config
  (m/schema
    [:map {:closed true, :decode/args-transformer malli-cli/args-transformer}
     [:window-manager {:optional true}
      [boolean? {:description "Run the window manager."
                 :arg-number 0}]]
     [:help {:optional true}
      [boolean? {:description "Display usage summary and exit."
                 :short-option "-h"
                 :arg-number 0}]]]))


(defn get-help []
  (malli-cli/summary Config))


(defn -main [& args]
  (let [config (m/decode Config args malli-cli/cli-transformer)]
    (cond (not (m/validate Config config))
          (do
            (println "Invalid configuration value")
            (println (m/explain Config config)))

          (:help config)
          (println (get-help))

          (:window-manager config)
          (do
           (start-system!)
           @(promise))

          :else
          (println config (get-help)))))

(comment
  (-main "--window-manager")
  #_END)
