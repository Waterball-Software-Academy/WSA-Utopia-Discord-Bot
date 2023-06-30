package tw.waterballsa.utopia.mongo.gatweay.adapter

data class MappingMongoDocumentInformation<T, ID>(
        val collectionName: String,
        val documentClassType: Class<T>,
        val idType: Class<ID>,
        val idFieldName: String
)
