package tw.waterballsa.utopia.jda.extensions

import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.OptionMapping

fun GenericCommandInteractionEvent.getOptionAsZeroOrPositiveInt(name: String): Int? {
    return getOptionAsIntWithValidation(name, "a zero or positive integer") {
        it >= 0
    }
}

fun GenericCommandInteractionEvent.getOptionAsPositiveInt(name: String): Int? {
    return getOptionAsIntWithValidation(name, "a positive integer") {
        it > 0
    }
}

fun GenericCommandInteractionEvent.getOptionAsLongInRange(name: String, range: LongRange): Long? {
    return getOptionAsLongWithValidation(name, "a long that within ${range.first} ~ ${range.last}") {
        range.contains(it)
    }
}

fun GenericCommandInteractionEvent.getOptionAsIntInRange(name: String, range: IntRange): Int? {
    return getOptionAsIntWithValidation(name, "a integer that within ${range.first} ~ ${range.last}") {
        range.contains(it)
    }
}

fun GenericCommandInteractionEvent.getOptionAsStringWithLimitedLength(name: String, lengthRange: IntRange): String? {
    return getOptionAsStringWithValidation(name, "a string which contains ${lengthRange.first} ~ ${lengthRange.last} characters.") {
        lengthRange.contains(it.length)
    }
}

fun GenericCommandInteractionEvent.getOptionAsStringWithValidation(name: String,
                                                                   optionTypeName: String,
                                                                   validation: (String) -> Boolean): String? {
    return getOptionWithValidation(name, optionTypeName, validation) { it?.asString }
}

fun GenericCommandInteractionEvent.getOptionAsLongWithValidation(name: String,
                                                                 optionTypeName: String,
                                                                 validation: (Long) -> Boolean): Long? {
    return getOptionWithValidation(name, optionTypeName, validation) { it?.asLong }
}

fun GenericCommandInteractionEvent.getOptionAsIntWithValidation(name: String,
                                                                optionTypeName: String,
                                                                validation: (Int) -> Boolean): Int? {
    return getOptionWithValidation(name, optionTypeName, validation) { it?.asInt }
}


fun <T> GenericCommandInteractionEvent.getOptionWithValidation(name: String,
                                                               optionTypeName: String,
                                                               validation: (T) -> Boolean,
                                                               optionTypeMapping: (OptionMapping?) -> T?): T? {
    val option = optionTypeMapping(getOption(name))
    if (option != null) {
        @Suppress("UNCHECKED_CAST")
        if (validation(option as T)) {
            return option
        } else {
            reply("The argument '$name' must be $optionTypeName.")
                    .setEphemeral(true).queue()
        }
    }
    return null
}
