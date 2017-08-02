(ns transaction-service.core
  (:require [transaction-service.routes :refer [app]]
            [transaction-service.settings :refer [config]]
            [org.httpkit.server :refer [run-server]]
            [transaction-service.queue.connection :as queue]
            [transaction-service.persistence.db :as db]
            [clojure.tools.logging :as log]
            [perseverance.core :as p])
  (:gen-class))

(defn -main [& args]
  (p/retry {} (db/attempt-migrate))
  (queue/setup)
  (let [port (or (:port config) 8082)]
    (run-server app {:port port})
    (log/info "Server running at port" port)))
