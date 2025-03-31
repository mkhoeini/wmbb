(ns wmbb.schema)



(def display-schema
  #:wmbb.display{:id {:db/valueType :db.type/long
                      :db/unique :db.unique/identity}
                 :uuid {:db/valueType :db.type/uuid}
                 :index {:db/valueType :db.type/long}
                 :label {:db/valueType :db.type/string}
                 :x {:db/valueType :db.type/double}
                 :y {:db/valueType :db.type/double}
                 :w {:db/valueType :db.type/double}
                 :h {:db/valueType :db.type/double}
                 :spaces {:db/cardinality :db.cardinality/many
                          :db/valueType :db.type/ref}
                 :has-focus {:db/valueType :db.type/boolean}})


(def space-schema
  #:wmbb.space{:id {:db/valueType :db.type/long
                    :db/unique :db.unique/identity}
               :uuid {:db/valueType :db.type/uuid}
               :type {:db/valueType :db.type/string}
               :label {:db/valueType :db.type/string}
               :windows {:db/valueType :db.type/ref
                         :db/cardinality :db.cardinality/many}
               :display {:db/valueType :db.type/ref}
               :first-window {:db/valueType :db.type/ref}
               :last-window {:db/valueType :db.type/ref}
               :index {:db/valueType :db.type/long}
               :is-native-fullscreen {:db/valueType :db.type/boolean}
               :is-visible {:db/valueType :db.type/boolean}
               :has-focus {:db/valueType :db.type/boolean}})


(def window-schema
  #:wmbb.window{:id {:db/valueType :db.type/long
                     :db/unique :db.unique/identity}
                :title {:db/valueType :db.type/string}
                :pid {:db/valueType :db.type/long}
                :app {:db/valueType :db.type/string}
                :x {:db/valueType :db.type/double}
                :y {:db/valueType :db.type/double}
                :w {:db/valueType :db.type/double}
                :h {:db/valueType :db.type/double}
                :opacity {:db/valueType :db.type/double}
                :stack-index {:db/valueType :db.type/long}
                :level {:db/valueType :db.type/long}
                :sub-level {:db/valueType :db.type/long}
                :layer {:db/valueType :db.type/string}
                :sub-layer {:db/valueType :db.type/string}
                :role {:db/valueType :db.type/string}
                :subrole {:db/valueType :db.type/string}
                :split-type {:db/valueType :db.type/string}
                :split-child {:db/valueType :db.type/string}
                :scratchpad {:db/valueType :db.type/string}
                :display {:db/valueType :db.type/ref}
                :space {:db/valueType :db.type/ref}
                :root-window {:db/valueType :db.type/boolean}
                :is-minimized {:db/valueType :db.type/boolean}
                :is-native-fullscreen {:db/valueType :db.type/boolean}
                :is-sticky {:db/valueType :db.type/boolean}
                :is-floating {:db/valueType :db.type/boolean}
                :is-grabbed {:db/valueType :db.type/boolean}
                :is-hidden {:db/valueType :db.type/boolean}
                :is-visible {:db/valueType :db.type/boolean}
                :has-ax-reference {:db/valueType :db.type/boolean}
                :has-shadow {:db/valueType :db.type/boolean}
                :has-parent-zoom {:db/valueType :db.type/boolean}
                :has-focus {:db/valueType :db.type/boolean}
                :has-fullscreen-zoom {:db/valueType :db.type/boolean}
                :can-resize {:db/valueType :db.type/boolean}
                :can-move {:db/valueType :db.type/boolean}})


(def manager-display-schema
  #:wmbb.manager.display{:display-id {:db/valueType :db.type/long
                                      :db/unique :db.unique/identity}
                         :ref {:db/valueType :db.type/ref}
                         :next {:db/valueType :db.type/ref}
                         :prev {:db/valueType :db.type/ref}})


(def schema
  (into {} (concat display-schema space-schema window-schema manager-display-schema)))
