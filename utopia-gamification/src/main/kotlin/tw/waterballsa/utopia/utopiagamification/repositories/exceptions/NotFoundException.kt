package tw.waterballsa.utopia.utopiagamification.repositories.exceptions

import kotlin.reflect.KClass

class NotFoundException(
    resourceType: KClass<*>,
    id: String,
    message: String
) : RuntimeException("Resource (${resourceType.simpleName}) not found: (id = $id) $message.") {

    companion object {

        fun notFound(resource: KClass<*>): Builder = Builder(resource, "", "")
    }

    class Builder(
        private val resourceType: KClass<*>,
        private var id: String,
        private var message: String
    ) {

        fun id(id: Any): Builder {
            this.id = id.toString()
            return this
        }

        fun message(message: String): Builder {
            this.message = message
            return this
        }

        fun build(): NotFoundException {
            return NotFoundException(resourceType, id, message)
        }
    }
}
