(ns sss.utils)



(defmacro spy [title x]
  `(try
     (let [x# ~x]
       (tap> [~title x#])
       x#)
     (catch Exception e#
       (tap> [~title "EXCEPTION" e#])
       (throw e#))))
