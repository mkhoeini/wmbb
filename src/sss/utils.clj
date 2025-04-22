(ns sss.utils)



(defmacro spy [title x]
  `(let [x# ~x]
     (tap> [~title x#])
     x#))
