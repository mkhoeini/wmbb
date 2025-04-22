(ns sss.core
  (:require
   [sss.archetype :as arch]
   [sss.db :as db]
   [sss.signal :as sig]
   [sss.sys-ref :refer [*system*]]
   [sss.system :as sys]
   [sss.event :as ev]))



(defn create-system
  "Create a new instance of the system. You still need to initialize the system afterwards.
  config is a key-val pairs of these options:

  signals:
    set of signal names
  events:
    set of event names
  subscriptions:
    map from subscription name to a map of {:signal :kw :interesting? (fn [signal] bool) :to-event (fn [signal] event)}
  tags:
    set of tag names
  archetypes:
    set of archetype maps ({:name :kw :tags [:kw] :schema {}}) for entities
  entities:
    initial value for entities. a map from archetype name to a set of entity instances
  behaviors:
    map from behavior name to a map of {:events [:kw] :tags [:kw] :reaction (fn [event] tx)}
  commands:
    map of command name to a map of {:fn (fn [cmd])}
  reconcilers:
    map from reconciler name to a map of {:entity :ent-name :fn (fn [old-ent new-ent] [{:command}])}
  "
  [& {:as config}]
  (sys/create-system config))


(defn halt-system! [system]
  (sys/halt-system! system))


(defmacro with-system
  "Set a thread-local context with the suplied system to call the other functions"
  [system & forms]
  `(binding [*system* ~system] ~@forms))


(defn send-signal
  "send a signal to the system"
  [sig data]
  {:pre [(some? *system*)]}
  (sig/send-signal! *system* sig data))


(defn get-entity
  "get an entity instance ref by running a query"
  [& where]
  {:pre [(some? *system*)]}
  (apply db/find1 (::db/conn *system*) where))


(defn make-archetype [name tags fields]
  (arch/make-archetype name tags fields))


(defn make-event [name target data]
  (ev/make-event name target data))
