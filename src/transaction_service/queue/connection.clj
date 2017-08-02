(ns transaction-service.queue.connection
  (:require [transaction-service.queue.handling :refer [handle-message]]
            [langohr.core :as rmq]
            [langohr.channel :as channel]
            [langohr.consumers :as lc]
            [transaction-service.settings :refer [config]]))

(defn- declare-subscribe-queue [ch queue]
  (lc/subscribe ch queue handle-message {:auto-ack true}))

(defn- shutdown [ch conn]
  (rmq/close ch)
  (rmq/close conn))

(defn setup []
  (let [conf (:amqp config)
        queue (:tx-queue-name conf)
        uri (:url conf)
        conn (rmq/connect {:uri uri})
        ch (channel/open conn)]
    (declare-subscribe-queue ch queue)
    (.addShutdownHook (Runtime/getRuntime)
                      (Thread. #(shutdown ch conn)))))
