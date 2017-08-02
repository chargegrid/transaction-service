(ns transaction-service.persistence.queries
  (:require [transaction-service.persistence.db]
            [korma.core :refer [select select* insert where where* values defentity fields sqlfn aggregate order exec-raw]]
            [clj-time.jdbc]
            [clojure.tools.logging :as log]
            [clj-time.core :as t])
  (:import (java.util UUID)))

;; Entities
(declare transactions)

(defentity transactions)

;; Queries

(defn transactions-for [tenant-id]
  (-> (select* transactions)
      (where {:tenant_id tenant-id})))

(defn range-in [query from to]
  (where query (and (>= :ended_at from)
                    (<= :ended_at to))))

(defn get-transactions [tenant-id from to]
  (-> (transactions-for tenant-id)
      (range-in from to)
      (select)))

(defn get-transaction [tenant-id id]
  (first (select (transactions-for tenant-id)
                 (where {:id id}))))

(defn get-evse-transactions [tenant-id evse-id from to]
  (-> (transactions-for tenant-id)
      (range-in from to)
      (where {:evse_id evse-id})
      (select)))

(defn create-transaction [transaction]
  (let [tx (assoc transaction :id (UUID/randomUUID))]
    (insert transactions
            (values tx))))

(defn get-stats [tenant-id start end step tz-offset]
  (let [step-interval (str "1 " step)]
    (exec-raw ["SELECT dt as bucket, coalesce(total_tx, 0) as total_tx, coalesce(total_revenue, 0) as total_revenue
             FROM generate_series(date_trunc(?, ?::timestamptz AT TIME ZONE ?::INTERVAL), date_trunc(?, ?::timestamptz AT TIME ZONE ?::INTERVAL), ?::interval) AS dt
             LEFT JOIN (SELECT date_trunc(?, ended_at AT TIME ZONE ?::interval) AS bucket, count(*) AS total_tx, sum(price) AS total_revenue
             FROM transactions WHERE tenant_id = ? AND ended_at < ? AND ended_at >= ?
             GROUP BY bucket
             ORDER BY bucket) results ON results.bucket = dt;"
               [step start tz-offset step end tz-offset step-interval step tz-offset tenant-id end start]] :results)))

