package com.lambdarat.quadmist.common.domain

import com.lambdarat.quadmist.common.domain.ModelGens._

import cats.syntax.option._

class ArrowSpec extends ModelSpec {
  val zeroByte: Byte = 0x00
  val maxByte: Byte  = 0xff.toByte

  "A packed arrows" when {
    "zero" should {
      "return empty list of arrows" in {
        Arrow.extract(zeroByte) shouldBe Nil
      }
    }

    "all bits set to one" should {
      "return list of arrows of size MAX_ARROWS" in {
        Arrow.extract(maxByte).size shouldBe Arrow.MAX_ARROWS
      }

      "return list of arrows composed of all arrows" in {
        Arrow.extract(maxByte).toSet shouldBe Arrow.values.toSet
      }
    }

    "random bits are set to one" should {
      "compressed arrows should give the same packed byte" in {
        forAll { (packed: Byte) =>
          Arrow.compress(Arrow.extract(packed)) shouldBe packed.some
        }
      }
    }
  }

  "A list of arrows" when {
    "empty" should {
      "return a zero compressed byte" in {
        Arrow.compress(Nil) shouldBe zeroByte.some
      }
    }

    "arrows are repeated and/or size is greater than MAX_ARROWS" should {
      "return no byte" in {
        forAll(invalidArrowsGenerator) { arrows: List[Arrow] =>
          whenever(arrows.distinct.size != arrows.size || arrows.size > Arrow.MAX_ARROWS) {
            Arrow.compress(arrows) shouldBe empty
          }
        }
      }
    }

    "valid random arrows are selected" should {
      "compress and extract the same list" in {
        forAll { arrows: List[Arrow] =>
          Arrow.compress(arrows).map(Arrow.extract(_).toSet) shouldBe arrows.toSet.some
        }
      }
    }

  }

}
