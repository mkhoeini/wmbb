(ns sss.config
  (:require
    [integrant.core :as ig]))



(defmethod ig/init-key ::config [_ cfg]
  cfg)
