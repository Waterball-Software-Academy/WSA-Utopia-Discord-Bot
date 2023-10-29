package tw.waterballsa.utopia.utopiagamification

import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.extension.ParameterContext
import org.junit.jupiter.api.extension.ParameterResolver

class GenericTypedParameterResolver<T>(
    private val data: T
) : ParameterResolver {

    override fun supportsParameter(
        parameterContext: ParameterContext?,
        extensionContext: ExtensionContext?
    ): Boolean =
        parameterContext!!.parameter.type.isInstance(data)

    override fun resolveParameter(
        parameterContext: ParameterContext?,
        extensionContext: ExtensionContext?
    ): T = data
}
