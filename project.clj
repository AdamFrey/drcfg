(defproject com.roomkey/drcfg :lein-v
  :description "Dynamic Runtime Configuration Utility based on Zookeeper"
  :url "https://github.com/g1nn13/drcfg"
  :plugins [[com.roomkey/lein-v "4.0.0"]
            [s3-wagon-private "1.1.2"]]
  :license {:name "Copyright Hotel JV Services LLC"
            :distribution :manual
            :comments "All rights reserved"}
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/tools.logging "0.2.6"]
                 [com.netflix.curator/curator-framework "1.3.3"]
                 [com.netflix.curator/curator-recipes "1.3.3"]]
  :exclusions [log4j]
  :repositories [["releases" {:url "s3p://rk-maven/releases/"}]]
  :profiles {:dev {:resource-paths ["test-resources"]
                   :dependencies [[midje "1.4.0"]
                                  [org.slf4j/slf4j-api "1.7.5"]
                                  [org.slf4j/jcl-over-slf4j "1.7.5"]
                                  [org.slf4j/slf4j-log4j12 "1.7.5"]
                                  [log4j/log4j "1.2.17"]]}})
