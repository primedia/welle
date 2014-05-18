(ns clojurewerkz.welle.test.search-test
  (:require [clojurewerkz.welle.core :as wc]
            [clojurewerkz.welle.kv   :as kv]
            [clojurewerkz.welle.buckets :as wb]
            [clojurewerkz.welle.solr    :as wsolr]
            [clojure.test :refer :all]
            [clojurewerkz.welle.testkit :refer [drain]])
  (:import  com.basho.riak.client.http.util.Constants))



(deftest ^{:search true} test-term-query-via-the-solr-api
  (let [conn        (wc/connect)
        bucket-name "clojurewerkz.welle.solr.tweets"
        bucket      (wb/update conn bucket-name {:last-write-wins true :enable-search true})
        doc         {:username  "clojurewerkz"
                     :text      "Elastisch beta3 is out, several more @elasticsearch features supported github.com/clojurewerkz/elastisch, improved docs http://clojureelasticsearch.info #clojure"
                     :timestamp "20120802T101232+0100"
                     :id        1}]
    (drain conn bucket-name)
    (kv/store conn bucket-name "a-key" doc {:content-type "application/json"})
    (let [result (wsolr/search conn bucket-name "username:clojurewerkz")
          hits   (wsolr/hits-from result)]
      (is (= "a-key" (-> hits first :id)))
      (is (> (count hits) 0)))
    (drain conn bucket-name)))
