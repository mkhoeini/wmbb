(ns sss.utils)



(defmacro spy [x]
  `(let [x# ~x]
     (tap> x#)
     x#))
