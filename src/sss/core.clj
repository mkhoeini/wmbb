(ns sss.core
  (:require
   [sss.archetype :as arch]
   [sss.db :as db]
   [sss.signal :as sig]
   [sss.sys-ref :refer [*system*]]
   [sss.system :as sys]
   [sss.event :as ev]
   [sss.entity :as ent]))



(defn create-system
  "Create a new instance of the system. You still need to initialize the system afterwards.
  config is a key-val pairs of these options:

  signals:
    set of signal names
  events:
    set of event names. built-in events are: [:sss.event/entity-created :sss.event/entity-deleted]
  subscriptions:
    map from subscription name to a map of {:signal :kw :interesting? (fn [signal] bool) :to-event (fn [signal] event)}
  tags:
    set of tag names. built-in tags are: [:sss.tag/entity]
  archetypes:
    set of archetype maps ({:name :kw :tags [:kw] :schema {}}) for entities
    all archetypes will get the :sss.tag/entity tag by default
  entities:
    initial value for entities. set of entity maps {:sss.entity/archetype :kw
                                                    :sss.entity.[name]/* any?
                                                    :sss.entity.[name].state/* any?
                                                    :sss.entity.[name].desired/* any?}
  behaviors:
    map from behavior name to a map of {:events [:kw] :tags [:kw] :reaction (fn [event] tx)}
  commands:
    map of command name to a map of {:retry (fn [cmd] (seq int)) :exec (fn [cmd] bool)}
  reconcilers:
    map from reconciler name to a map of {:archetype :kw
                                          :dirty? (fn [old-ent new-ent] bool)
                                          :to-cmds (fn [old-ent new-ent] [{:command :data}])}
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


(defn make-archetype
  ([name tags id-fields state-fields]
   (arch/make-archetype name tags id-fields state-fields {}))
  ([name tags id-fields state-fields desired-fields]
   (arch/make-archetype name tags id-fields state-fields desired-fields)))


(defn make-init-entity
  ([archetype id-map state]
   (ent/make-init-entity archetype id-map state {}))
  ([archetype id-map state desired]
   (ent/make-init-entity archetype id-map state desired)))


(defn make-event [name target data]
  (ev/make-event name target data))
