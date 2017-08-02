(ns transaction-service.settings
  (:require [cprop.core :refer [load-config]]))

(def config (load-config))