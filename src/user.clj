(ns user
  (:require [malli.dev :as mdev]
            [malli.dev.pretty :as mpret]))

(defn start []
  (mdev/start! {:report (mpret/reporter)}))

(defn stop []
  (mdev/stop!))
