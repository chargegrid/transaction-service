(ns transaction-service.queue.schema
  (:require [schema.core :as s]
            [schema.coerce :as coerce]
            [ring.swagger.coerce :refer [coercer]])
  (:import (org.joda.time DateTime)))

(s/defschema Transaction
  {:started_at DateTime
   :ended_at   DateTime
   :volume     s/Num
   :user_id    String
   :evse_id    String
   :tenant_id  s/Uuid
   :price      s/Num})

(def coerce-transaction
  (coerce/coercer Transaction (coercer :json)))
