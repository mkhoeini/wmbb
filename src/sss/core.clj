(ns sss.core
  (:require
   [sss.system :as sys]
   [sss.entity :as ent]))



(defn create-system
  "create and initialize a new instance of the system.
  config is a key-val pairs of these options:

  signals:
    set of signal names
  events:
    set of event names
  subscriptions:
    map from subscription name to a map of {:signal :kw :interesting? (fn [system sig] bool) :to-event (fn [system sig] event)}
  entities:
    set of entity archetypes
  init:
    initial value for entities. a map from entity name to a set of entity instances
  behaviors:
    map from behavior name to a map of {:events [:kw] :fn (fn [system event] tx)}
  tags:
    map from tag name to set of behavior names
  entity-tags:
    map of entity name to set of tag names
  commands:
    map of command name to a map of {:fn (fn [system cmd])}
  reconcilers:
    map from reconciler name to a map of {:entity :ent-name :fn (fn [system old-ent new-ent] [{:command}])}
  "
  [& {:as config}]
  (sys/create-system config))


(defn halt-system! [system]
  (sys/halt-system! system))


; TODO
(defn send-signal
  "send a signal to the system"
  [system sig data])

; TODO
(defn add-entity!
  "add an instance of an entity and return it's entity ref"
  [system archetype instance])

; TODO
(defn get-entity
  "get an entity instance ref by running a query"
  [system & where])


(defn make-entity-archetype [name fields]
  (ent/make-entity-archetype name fields))
