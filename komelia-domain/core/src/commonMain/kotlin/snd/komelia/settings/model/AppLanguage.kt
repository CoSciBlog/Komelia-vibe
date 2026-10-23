package snd.komelia.settings.model

/** Persist BCP 47 tags rather than enum names; an empty tag follows the system. */
enum class AppLanguage(val tag: String, val nativeName: String) {
    SYSTEM("", ""),
    ENGLISH("en", "English"),
    GERMAN("de", "Deutsch"),
    SPANISH("es", "Español"),
    FRENCH("fr", "Français"),
    JAPANESE("ja", "日本語"),
    RUSSIAN("ru", "Русский"),
    CHINESE_SIMPLIFIED("zh-CN", "简体中文");

    companion object {
        fun fromTag(tag: String): AppLanguage = entries.firstOrNull { it.tag == tag } ?: SYSTEM

        /** Match regional device locales to an available translation, with English fallback. */
        fun forDeviceTag(tag: String): AppLanguage {
            val language = tag.replace('_', '-').substringBefore('-').lowercase()
            return entries.firstOrNull { it != SYSTEM && it.tag.substringBefore('-') == language } ?: ENGLISH
        }
    }
}
