package wu.seal.jsontokotlin.bean.jsonschema

class JsonSchema(
  title: String = "",
  description: String = "",
  type: String? = "",
  properties: Map<String, JsonSchema> = mapOf(),
  required: Array<String> = emptyArray()
)
  : ObjectPropertyDef(description, type, properties, required = required)
