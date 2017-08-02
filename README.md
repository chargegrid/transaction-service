# transaction-service

Basic transaction service that accepts messages from RabbitMQ in JSON format, stores them in a database, and exposes 
an API for retrieving them.

## Prerequisites

- Install [Leiningen][] 2.0.0 or above

[leiningen]: https://github.com/technomancy/leiningen

## Running

To start a web server for the application, run:

    lein run
    
## Put messages on the queue through web interface

- Go to the RabbitMQ admin panel and check the right exchange: http://docker-ip:15672/#/exchanges/%2F/transactions
- Fill in the following:
    - Properties: `content_type -> application/json`
    - Payload: Valid json with the following schema:
```
{
  "evse_id": "790838973-00002",
  "started_at": "2017-05-03T14:21:06Z",
  "ended_at": "2017-05-04T14:21:06Z",
  "volume": 17.453634968809,
  "user_id": "John",
  "price": 3.5
}
```
- Click _Publish message_

## Put messages from a Clojure app

Make sure to add the following to your Leiningen project definition: `[com.novemberain/langohr "3.5.0"]`

The following code illustrates how to publish messages to the `transactions` exchange:

```
(ns myns
  (:require
    [langohr.core      :as rmq]
    [langohr.channel   :as lch]
    [langohr.basic :as lb]))

;; Don't change this
(def exchange "transactions")

;; Connect to RabbitMQ and create a channel
(defn connect-and-create-channel []
  (let [conn (rmq/connect {:uri "your-amqp-queue"})]
    (lch/open conn)))

;; Publish a message (routing-key can be left empty for fanout exchange
(defn publish-message [ch msg]
  (lb/publish ch exchange "" msg {:content-type "application/json"}))
  
;; Shut the whole thing down
(defn shutdown [ch conn]
  (rmq/close ch)
  (rmq/close conn))
```

## TODO

Many things:

- Store more transaction details
- Permissions
- ...
