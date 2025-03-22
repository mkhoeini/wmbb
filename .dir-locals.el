;;; Directory Local Variables            -*- no-byte-compile: t -*-
;;; For more information see (info "(emacs) Directory Variables")

((clojure-mode . ((cider-clojure-cli-aliases . ":dev")
                  (cider-ns-refresh-before-fn . "user/stop-system!")
                  (cider-ns-refresh-after-fn . "user/start-system!"))))
