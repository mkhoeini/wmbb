(ns sss.log)



(def log-levels (into {} (map vector [:debug :info :warn :error] (range))))

(def log-level (atom :debug))

(def log-fn (atom tap>))


(defn log [level loc msg extra]
  (when (>= (log-levels level) (log-levels @log-level))
    (@log-fn {:level level
              :location loc
              :timestamp (System/currentTimeMillis)
              :message msg
              :extra extra})))


(defmacro debug [msg & extra]
  (let [loc (assoc (meta &form)
                   :file *file*
                   :form &form)]
    `(log :debug '~loc ~msg ~extra)))

(comment
  (debug "hi")
  #_end)


(defmacro info [msg & extra]
  (let [loc (assoc (meta &form)
                   :file *file*
                   :form &form)]
    `(log :info ~loc ~msg ~extra)))


(defmacro warn [msg & extra]
  (let [loc (assoc (meta &form)
                   :file *file*
                   :form &form)]
    `(log :warn ~loc ~msg ~extra)))


(defmacro error [msg & extra]
  (let [loc (assoc (meta &form)
                   :file *file*
                   :form &form)]
    `(log :error ~loc ~msg ~extra)))
