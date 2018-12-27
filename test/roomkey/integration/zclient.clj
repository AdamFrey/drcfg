(ns roomkey.integration.zclient
  (:require [roomkey.zclient :refer :all :as z]
            [clojure.core.async :as async]
            [zookeeper :as zoo]
            [clojure.tools.logging :as log]
            [midje.sweet :refer :all]
            [midje.checking.core :refer [extended-=]])
  (:import [org.apache.curator.test TestingServer TestingCluster]
           [org.apache.zookeeper ZooKeeper]
           [roomkey.zclient ZClient]))

(def bogus-host "127.1.1.1:9999")
(def test-server (TestingServer. true))
(def $cstring0 (.getConnectString test-server))
(def $cstring1 "localhost:2181/drcfg")
(def sandbox "/sandbox")

(defchecker refers-to [expected]
  (checker [actual] (extended-= (deref actual) expected)))

(defchecker eventually-is-realized-as [timeout expected]
  (checker [actual]
           (loop [t timeout]
             (when (pos? t)
               (if-let [result (and (realized? actual)
                                    (extended-= (deref actual) expected))]
                 result
                 (do (Thread/sleep 200)
                     (recur (- t 200))))))))

(defchecker eventually-refers-to [timeout expected]
  (checker [actual]
           (loop [t timeout]
             (when (pos? t)
               (if-let [result (extended-= (deref actual) expected)]
                 result
                 (do (Thread/sleep 200)
                     (recur (- t 200))))))))

(fact "Can create a client, open it and then close it with proper notifications arriving on supplied channel"
      (let [c (async/chan 1)]
        (with-open [$c (open (create) $cstring0 5000)]
          $c => (partial instance? ZClient)
          (async/<!! (async/tap $c c)) => (just [:roomkey.zclient/connected (partial instance? ZooKeeper)])
          $c => (refers-to (partial instance? ZooKeeper)))))

(fact "Can open client to unavailable server"
      (with-open [$t0 (TestingServer. false)
                  $t1 (TestingServer. (.getPort $t0) false)]
        (let [$cstring (.getConnectString $t0)
              $c (async/chan 1)
              $zclient (create)]
          (async/tap $zclient $c)
          (with-open [$client (open $zclient $cstring 5000)]
            (async/alts!! [$c (async/timeout 2500)]) => (contains [(just [:roomkey.zclient/started (partial instance? ZooKeeper)])])
            (.start $t0)
            (async/alts!! [$c (async/timeout 2500)]) => (contains [(just [:roomkey.zclient/connected (partial instance? ZooKeeper)])])
            (.stop $t0)
            (async/alts!! [$c (async/timeout 2500)]) => (contains [(just [:roomkey.zclient/disconnected (partial instance? ZooKeeper)])])
            (.restart $t0)
            (async/alts!! [$c (async/timeout 2500)]) => (contains [(just [:roomkey.zclient/connected (partial instance? ZooKeeper)])])
            (.stop $t0)
            (async/alts!! [$c (async/timeout 2500)]) => (contains [(just [:roomkey.zclient/disconnected (partial instance? ZooKeeper)])])
            (log/info ">>>>>>>>>> About to start a new server -should trigger expiration of existing sessions <<<<<<")
            (.start $t1)
            (async/alts!! [$c (async/timeout 2500)]) => (contains [(just [:roomkey.zclient/expired (partial instance? ZooKeeper)])])))))

(fact "Client survives session expiration"
      (with-open [$t (TestingCluster. 3)]
        (let [$cstring (.getConnectString $t)
              $c (async/chan 1)
              $zclient (create)]
          (async/tap $zclient $c)
          (with-open [$client (open $zclient $cstring 500)]
            (async/alts!! [$c (async/timeout 2500)]) => (contains [(just [:roomkey.zclient/started (partial instance? ZooKeeper)])])
            (.start $t)
            (async/alts!! [$c (async/timeout 2500)]) => (contains [(just [:roomkey.zclient/connected (partial instance? ZooKeeper)])])
            (let [instance (.findConnectionInstance $t @$zclient)]
              (assert (.killServer $t instance) "Couldn't kill ZooKeeper server instance")
              (async/alts!! [$c (async/timeout 2500)]) => (contains [(just [:roomkey.zclient/disconnected (partial instance? ZooKeeper)])])
              (async/alts!! [$c (async/timeout 2500)]) => (contains [(just [:roomkey.zclient/connected (partial instance? ZooKeeper)])])))
          (async/alts!! [$c (async/timeout 2500)]) => (contains [(just [:roomkey.zclient/closed (partial instance? ZooKeeper)])]))))
