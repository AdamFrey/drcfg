(defproject com.roomkey/drcfg :lein-v
  :description "Dynamic Runtime Configuration Utility based on Zookeeper"
  :url "https://github.com/g1nn13/drcfg"
  :plugins [[com.roomkey/lein-v "4.1.0"]
            [lein-maven-s3-wagon "0.2.4"]]
  :license {:name "Copyright Hotel JV Services LLC"
            :distribution :manual
            :comments "All rights reserved"}
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/tools.logging "0.3.1"]
                 [org.apache.curator/curator-framework "2.7.1" :exclusions [org.slf4j/slf4j-log4j12 org.slf4j/slf4j-api log4j]]
                 [org.apache.curator/curator-recipes "2.7.1" :exclusions [org.slf4j/slf4j-log4j12]]]
  :repositories [["rk-public" {:url "http://rk-maven-public.s3-website-us-east-1.amazonaws.com/releases/"}] ["releases" {:url "s3://rk-maven/releases/"}]]
  :profiles {:dev {:resource-paths ["test-resources"]
                   :dependencies [[midje "1.6.3"]
                                  [org.slf4j/slf4j-api "1.7.12"]
                                  [org.slf4j/jcl-over-slf4j "1.7.12"]
                                  [org.slf4j/slf4j-log4j12 "1.7.12"]
                                  [log4j/log4j "1.2.17"]]}})
