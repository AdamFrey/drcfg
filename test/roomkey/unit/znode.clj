(ns roomkey.unit.znode
  (:require [roomkey.znode :refer :all]
            [clojure.core.async :as async]
            [midje.sweet :refer :all]
            [midje.checking.core :refer [extended-=]]))

(fact "Can create a root ZNode"
      (let [$root (create-root)]
        $root => (partial instance? roomkey.znode.ZNode)
        (path $root) => "/"
        (children $root) => empty?))

(fact "Can create a psuedo-root ZNode"
      (let [$root (create-root "/myroot")]
        $root => (partial instance? roomkey.znode.ZNode)
        (path $root) => "/myroot"
        (children $root) => empty?))

(fact "Can create and identify children of a node"
      (let [$root (create-root)]
        (add-descendant $root "/a0" "a0") => (partial instance? roomkey.znode.ZNode)
        (add-descendant $root "/a1" "a1") => (partial instance? roomkey.znode.ZNode)
        (children $root) => (two-of (partial instance? roomkey.znode.ZNode))))

(fact "Can create descendants of the root ZNode"
      (let [$root (create-root)]
        (add-descendant $root "/a0" "a0") => (partial instance? roomkey.znode.ZNode)
        (add-descendant $root "/a1" "a1") => (partial instance? roomkey.znode.ZNode)
        (add-descendant $root "/a1/b1" "b1") => (partial instance? roomkey.znode.ZNode)
        (add-descendant $root "/a2/b2/c1/d1/e1" "e1") => (partial instance? roomkey.znode.ZNode)))

(fact "Can create descendants of arbitrary znodes"
      (let [$root (create-root)
            $child (add-descendant $root "/a" "a")]
        (add-descendant $child "/b" "b") => (partial instance? roomkey.znode.ZNode)))

(fact "ZNodes know their path in the tree"
      (let [$root (create-root)
            $child (add-descendant $root "/a" "a")]
        (path $root) => "/"
        (path $child) => "/a"
        (path (add-descendant $root "/a/b/c/d" 4)) => "/a/b/c/d"
        (path (add-descendant $child "/b1/c/d" 4)) => "/a/b1/c/d"))

(fact "ZNodes can be accessed by path"
      (let [$root (create-root)
            $child (add-descendant $root "/a0" 0)
            $g4 (add-descendant $root "/a1/b/c/d" 1)]
        (get-in $root ["a0"]) => $child
        (get-in $root ["a1" "b" "c" "d"]) => $g4
        (get-in $root ["a9"]) => nil
        (get-in $root ["a9"] ::not-found) => ::not-found))
