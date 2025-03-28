;;; Directory Local Variables            -*- no-byte-compile: t -*-
;;; For more information see (info "(emacs) Directory Variables")

((clojure-mode . ((cider-ns-refresh-before-fn . "wmbb.system/stop-system!")
                  (cider-ns-refresh-after-fn . "wmbb.system/start-system!"))))
