package wu.seal.jsontokotlin.regression

import com.winterbe.expekt.should
import org.junit.Test
import wu.seal.jsontokotlin.KotlinCodeMaker
import wu.seal.jsontokotlin.KotlinDataClassCodeMaker
import wu.seal.jsontokotlin.KotlinDataClassMaker
import wu.seal.jsontokotlin.test.TestConfig

/**
 * Created by kezhenxu94 at 2019/4/17 17:27.
 *
 * @author kezhenxu94 (kezhenxu94 at 163 dot com)
 */
class Issue108Test {

  @Test
  fun test() {
    TestConfig.setToTestInitState()
    val json = """{
  "${"$"}schema": "http://json-schema.org/draft-04/schema#",
  "title": "DeliveryDayTime",
  "type": "object",
  "additionalProperties": false,
  "properties": {
    "hourMin": {
      "type": "integer",
      "description": "Начиная с: час (от 0 до 23)",
      "format": "int32",
      "maximum": 23.0,
      "minimum": 0.0
    },
    "minuteMin": {
      "type": "integer",
      "description": "Начиная с: минуты (от 0 до 59)",
      "format": "int32",
      "maximum": 59.0,
      "minimum": 0.0
    },
    "hourMax": {
      "type": "integer",
      "description": "До: час (от 0 до 23)",
      "format": "int32",
      "maximum": 23.0,
      "minimum": 0.0
    },
    "minuteMax": {
      "type": "integer",
      "description": "До: минуты (от 0 до 59)",
      "format": "int32",
      "maximum": 59.0,
      "minimum": 0.0
    }
  },
  "definitions": {
    "DeliveryDay": {
      "type": "integer",
      "description": "",
      "x-enumNames": [
        "Today",
        "Tomorrow"
      ],
      "enum": [
        0,
        1,
      ]
    }
  }
}""".trimIndent()
    val expected = """data class TestData(
    val hourMin: Int, // Начиная с: час (от 0 до 23)
    val minuteMin: Int, // Начиная с: минуты (от 0 до 59)
    val hourMax: Int, // До: час (от 0 до 23)
    val minuteMax: Int // До: минуты (от 0 до 59)
)""".trimIndent()
    val result = KotlinDataClassMaker("TestData", json).makeKotlinDataClass().getCode()
    result.trim().should.be.equal(expected)
  }
}
