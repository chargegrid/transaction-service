(ns transaction-service.queue.handling
  [:require [clojure.data.json :as json]
            [transaction-service.persistence.queries :as q]
            [clojure.tools.logging :as log]
            [transaction-service.queue.schema :as schema]])

(defn handle-message [ch meta ^bytes payload]
  (let [raw-json (String. payload "UTF-8")
        data (-> raw-json
                 (json/read-str :key-fn keyword)
                 schema/coerce-transaction
                 (update :price bigdec))]
    (if-let [error (:error data)]
      (log/error "Could not coerce msg from queue: " raw-json error)
      (do (log/info "Received msg from queue:" raw-json)
          (q/create-transaction data)))))
