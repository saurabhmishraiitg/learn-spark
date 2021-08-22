package org.nexus

import org.scalatest.flatspec.AnyFlatSpec

class HelloSpec extends AnyFlatSpec {

  "An empty Set" should "have size 0" in {
    assert(Set.empty.isEmpty)
  }
}
