(ns transaction-service.routes
  (:require [ring.util.response :refer [response]]
            [ring.middleware.cors :refer [wrap-cors]]
            [ring.middleware.logger :refer [wrap-with-logger]]
            [ring.middleware.reload :refer [wrap-reload]]
            [transaction-service.persistence.queries :as q]
            [compojure.api.sweet :refer :all]
            [schema.core :as s]
            [clojure-csv.core :refer [write-csv]]
            [clj-time.core :as t])
  (:import (org.joda.time DateTime)))

(def min-date (t/date-time 0000 1 1 0 0 0))
(def max-date (t/date-time 9999 1 1 0 0 0))

(defn tx-to-csv [tx-data]
  (let [columns [:id :evse_id :user_id :started_at :ended_at :price  :volume]
        headers (map name columns)
        rows (mapv #(mapv % columns) tx-data)
        rows-str (mapv #(mapv str %) rows)
        rows-with-header (into [headers] rows-str)]
    (write-csv rows-with-header)))


(def api-routes
  (api
    (context "" []
      :header-params [x-tenant-id :- s/Uuid]
      (GET "/transactions" []
        :query-params [{from :- DateTime min-date} {to :- DateTime max-date}]
        (response (q/get-transactions x-tenant-id from to)))
      (GET "/transactions.csv" []
        :query-params [{from :- DateTime min-date} {to :- DateTime max-date}]
        {:status  200
         :headers {"Content-Type" "text/csv"}
         :body    (tx-to-csv (q/get-transactions x-tenant-id from to))})
      (GET "/transactions/stats" []
        :query-params [{from :- DateTime min-date} {to :- DateTime max-date} step :- s/Str {tz-offset :- s/Str "00:00"}]
        (response (q/get-stats x-tenant-id from to step tz-offset)))
      (GET "/transactions/:id" []
        :path-params [id :- s/Uuid]
        (response (q/get-transaction x-tenant-id id)))
      (GET "/evses/:id/transactions" [id]
        :query-params [{from :- DateTime min-date} {to :- DateTime max-date}]
        (response (q/get-evse-transactions x-tenant-id id from to))))))

(def app (-> #'api-routes
             wrap-with-logger
             wrap-reload))
