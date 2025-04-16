(ns sss.sys-ref)



(def ^:dynamic *system*
  "Thread-local system ref to use in context of with-system"
  nil)
