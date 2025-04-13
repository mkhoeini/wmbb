(ns sss.signal
  (:require
    [integrant.core :as ig]
    [clojure.core.async :as async]))


(defmethod ig/init-key ::signals [_ signals]
  (->> signals
       (map (fn [[k ch]] [k (async/mult ch)]))
       (into {})))
