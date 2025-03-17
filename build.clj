(ns build
  (:require [clojure.tools.build.api :as b]))

(def class-dir "target/classes")
(def basis (delay (b/create-basis {:project "deps.edn"})))
(def basis-for-native-compile (delay (b/create-basis {:project "deps.edn"
                                                      :extra '{com.github.clj-easy/graal-build-time {:mvn/version "1.0.5"}}})))
(def jar-file "target/echo.jar")

(defn clean [_]
  (b/delete {:path "target"}))

(defn- -uberjar [basis]
  (clean nil)
  (b/copy-dir {:src-dirs ["src"]
               :target-dir class-dir})
  (b/compile-clj {:basis basis
                  :src-dirs ["src"]
                  :class-dir class-dir})
  (b/uber {:class-dir class-dir
           :uber-file jar-file
           :basis basis
           :main 'wmbb.cli.core}))

(defn uberjar [_]
  (-uberjar @basis))

(defn uber-native [_]
  (-uberjar @basis-for-native-compile))
