package snd.komelia.ui.strings

import androidx.compose.runtime.Composable
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.ui_enum_value
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.ui_enum_book
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.ui_enum_downloaded_series
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.Res
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.ui_enum_any
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.ui_enum_all
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.ui_enum_any_of
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.ui_enum_all_of
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.ui_enum_age_rating
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.ui_enum_author
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.ui_enum_collection
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.ui_enum_complete
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.ui_enum_deleted
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.ui_enum_genre
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.ui_enum_language
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.ui_enum_library
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.ui_enum_oneshot
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.ui_enum_publisher
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.ui_enum_read_status
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.ui_enum_release_date
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.ui_enum_sharing_label
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.ui_enum_status
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.ui_enum_tag
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.ui_enum_title
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.ui_enum_title_sort
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.ui_enum_media_profile
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.ui_enum_media_status
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.ui_enum_number_sort
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.ui_enum_poster
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.ui_enum_read_list
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.ui_enum_series
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.ui_enum_custom
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.ui_enum_on_deck
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.ui_enum_recently_added
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.ui_enum_recently_updated
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.ui_enum_downloaded
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.ui_enum_created_date
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.ui_enum_last_modified_date
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.ui_enum_book_count
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.ui_enum_unsorted
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.ui_enum_series_title
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.ui_enum_pages_count
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.ui_enum_last_modified
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.ui_enum_number
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.ui_enum_read_date
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.ui_enum_equals
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.ui_enum_not_equals
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.ui_enum_is_null
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.ui_enum_is_not_null
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.ui_enum_true
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.ui_enum_false
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.ui_enum_contains
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.ui_enum_does_not_contain
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.ui_enum_begins_with
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.ui_enum_does_not_begin_with
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.ui_enum_ends_with
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.ui_enum_does_not_end_with
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.ui_enum_is_before
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.ui_enum_is_after
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.ui_enum_is_in_last
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.ui_enum_is_not_in_last
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.ui_enum_equal_to
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.ui_enum_not_equal_to
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.ui_enum_greater_than
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.ui_enum_less_than
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.ui_enum_unread
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.ui_enum_in_progress
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.ui_enum_read
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.ui_enum_ready
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.ui_enum_unknown
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.ui_enum_error
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.ui_enum_unsupported
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.ui_enum_outdated
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.ui_enum_divina
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.ui_enum_pdf
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.ui_enum_epub
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.ui_enum_ended
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.ui_enum_ongoing
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.ui_enum_abandoned
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.ui_enum_hiatus
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.ui_enum_generated
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.ui_enum_sidecar
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.ui_enum_user_uploaded
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.ui_enum_asc
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.ui_enum_desc
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.ui_enum_writer
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.ui_enum_penciller
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.ui_enum_inker
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.ui_enum_colorist
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.ui_enum_letterer
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.ui_enum_cover
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.ui_enum_editor
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.ui_enum_translator
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.ui_enum_manga
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.ui_enum_novel
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.ui_enum_comic
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.ui_enum_webtoon
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.ui_enum_exact
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.ui_enum_closest_match
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.ui_enum_api
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.ui_enum_comic_info
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.ui_enum_left_to_right
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.ui_enum_right_to_left
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.ui_enum_vertical
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.ui_enum_database
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.ui_enum_rgb
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.ui_enum_red
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.ui_enum_green
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.ui_enum_blue

// Translate display labels only; enum identities and serialized protocol values stay unchanged.
private val enumLabels: Map<String, StringResource> = mapOf(
    "DownloadedSeries" to Res.string.ui_enum_downloaded_series,
    "Book" to Res.string.ui_enum_book,
    "VALUE" to Res.string.ui_enum_value,
    "Any" to Res.string.ui_enum_any,
    "All" to Res.string.ui_enum_all,
    "AnyOf" to Res.string.ui_enum_any_of,
    "AllOf" to Res.string.ui_enum_all_of,
    "AgeRating" to Res.string.ui_enum_age_rating,
    "Author" to Res.string.ui_enum_author,
    "Collection" to Res.string.ui_enum_collection,
    "Complete" to Res.string.ui_enum_complete,
    "Deleted" to Res.string.ui_enum_deleted,
    "Genre" to Res.string.ui_enum_genre,
    "Language" to Res.string.ui_enum_language,
    "Library" to Res.string.ui_enum_library,
    "Oneshot" to Res.string.ui_enum_oneshot,
    "Publisher" to Res.string.ui_enum_publisher,
    "ReadStatus" to Res.string.ui_enum_read_status,
    "ReleaseDate" to Res.string.ui_enum_release_date,
    "SharingLabel" to Res.string.ui_enum_sharing_label,
    "Status" to Res.string.ui_enum_status,
    "Tag" to Res.string.ui_enum_tag,
    "Title" to Res.string.ui_enum_title,
    "TitleSort" to Res.string.ui_enum_title_sort,
    "MediaProfile" to Res.string.ui_enum_media_profile,
    "MediaStatus" to Res.string.ui_enum_media_status,
    "NumberSort" to Res.string.ui_enum_number_sort,
    "Poster" to Res.string.ui_enum_poster,
    "ReadList" to Res.string.ui_enum_read_list,
    "Series" to Res.string.ui_enum_series,
    "Custom" to Res.string.ui_enum_custom,
    "OnDeck" to Res.string.ui_enum_on_deck,
    "RecentlyAdded" to Res.string.ui_enum_recently_added,
    "RecentlyUpdated" to Res.string.ui_enum_recently_updated,
    "Downloaded" to Res.string.ui_enum_downloaded,
    "CreatedDate" to Res.string.ui_enum_created_date,
    "LastModifiedDate" to Res.string.ui_enum_last_modified_date,
    "BookCount" to Res.string.ui_enum_book_count,
    "Unsorted" to Res.string.ui_enum_unsorted,
    "SeriesTitle" to Res.string.ui_enum_series_title,
    "PagesCount" to Res.string.ui_enum_pages_count,
    "LastModified" to Res.string.ui_enum_last_modified,
    "Number" to Res.string.ui_enum_number,
    "ReadDate" to Res.string.ui_enum_read_date,
    "Equals" to Res.string.ui_enum_equals,
    "NotEquals" to Res.string.ui_enum_not_equals,
    "IsNull" to Res.string.ui_enum_is_null,
    "IsNotNull" to Res.string.ui_enum_is_not_null,
    "True" to Res.string.ui_enum_true,
    "False" to Res.string.ui_enum_false,
    "Contains" to Res.string.ui_enum_contains,
    "DoesNotContain" to Res.string.ui_enum_does_not_contain,
    "BeginsWith" to Res.string.ui_enum_begins_with,
    "DoesNotBeginWith" to Res.string.ui_enum_does_not_begin_with,
    "EndsWith" to Res.string.ui_enum_ends_with,
    "DoesNotEndWith" to Res.string.ui_enum_does_not_end_with,
    "IsBefore" to Res.string.ui_enum_is_before,
    "IsAfter" to Res.string.ui_enum_is_after,
    "IsInLast" to Res.string.ui_enum_is_in_last,
    "IsNotInLast" to Res.string.ui_enum_is_not_in_last,
    "EqualTo" to Res.string.ui_enum_equal_to,
    "NotEqualTo" to Res.string.ui_enum_not_equal_to,
    "GreaterThan" to Res.string.ui_enum_greater_than,
    "LessThan" to Res.string.ui_enum_less_than,
    "UNREAD" to Res.string.ui_enum_unread,
    "IN_PROGRESS" to Res.string.ui_enum_in_progress,
    "READ" to Res.string.ui_enum_read,
    "READY" to Res.string.ui_enum_ready,
    "UNKNOWN" to Res.string.ui_enum_unknown,
    "ERROR" to Res.string.ui_enum_error,
    "UNSUPPORTED" to Res.string.ui_enum_unsupported,
    "OUTDATED" to Res.string.ui_enum_outdated,
    "DIVINA" to Res.string.ui_enum_divina,
    "PDF" to Res.string.ui_enum_pdf,
    "EPUB" to Res.string.ui_enum_epub,
    "ENDED" to Res.string.ui_enum_ended,
    "ONGOING" to Res.string.ui_enum_ongoing,
    "ABANDONED" to Res.string.ui_enum_abandoned,
    "HIATUS" to Res.string.ui_enum_hiatus,
    "GENERATED" to Res.string.ui_enum_generated,
    "SIDECAR" to Res.string.ui_enum_sidecar,
    "USER_UPLOADED" to Res.string.ui_enum_user_uploaded,
    "ASC" to Res.string.ui_enum_asc,
    "DESC" to Res.string.ui_enum_desc,
    "WRITER" to Res.string.ui_enum_writer,
    "PENCILLER" to Res.string.ui_enum_penciller,
    "INKER" to Res.string.ui_enum_inker,
    "COLORIST" to Res.string.ui_enum_colorist,
    "LETTERER" to Res.string.ui_enum_letterer,
    "COVER" to Res.string.ui_enum_cover,
    "EDITOR" to Res.string.ui_enum_editor,
    "TRANSLATOR" to Res.string.ui_enum_translator,
    "MANGA" to Res.string.ui_enum_manga,
    "NOVEL" to Res.string.ui_enum_novel,
    "COMIC" to Res.string.ui_enum_comic,
    "WEBTOON" to Res.string.ui_enum_webtoon,
    "EXACT" to Res.string.ui_enum_exact,
    "CLOSEST_MATCH" to Res.string.ui_enum_closest_match,
    "API" to Res.string.ui_enum_api,
    "COMIC_INFO" to Res.string.ui_enum_comic_info,
    "LEFT_TO_RIGHT" to Res.string.ui_enum_left_to_right,
    "RIGHT_TO_LEFT" to Res.string.ui_enum_right_to_left,
    "VERTICAL" to Res.string.ui_enum_vertical,
    "DATABASE" to Res.string.ui_enum_database,
    "RGB" to Res.string.ui_enum_rgb,
    "RED" to Res.string.ui_enum_red,
    "GREEN" to Res.string.ui_enum_green,
    "BLUE" to Res.string.ui_enum_blue,
 )

fun enumLabelResource(value: Enum<*>): StringResource? = enumLabels[value.name]

@Composable
fun localizedEnumLabel(value: Enum<*>): String =
    enumLabelResource(value)?.let { stringResource(it) } ?: value.name
