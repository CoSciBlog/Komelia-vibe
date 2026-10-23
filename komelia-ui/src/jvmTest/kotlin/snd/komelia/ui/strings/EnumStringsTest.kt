package snd.komelia.ui.strings

import snd.komelia.color.ColorChannel
import snd.komelia.ui.home.*
import snd.komelia.ui.home.edit.*
import snd.komf.api.*
import snd.komga.client.book.KomgaReadStatus
import snd.komga.client.book.KomgaMediaStatus
import snd.komga.client.book.MediaProfile
import snd.komga.client.series.KomgaSeriesStatus
import snd.komga.client.search.KomgaSearchCondition.PosterMatch
import snd.komga.client.common.KomgaSort
import kotlin.test.Test
import kotlin.test.assertNotNull

class EnumStringsTest {
    @Test
    fun everyDisplayedAppEnumHasATranslatableLabel() {
        val entries: List<Enum<*>> = ColorChannel.entries + EqualityOpState.Op.entries +
            EqualityNullableOpState.Op.entries + BooleanOpState.Op.entries + StringOpState.Op.entries +
            DateOpState.Op.entries + NumericOpState.Op.entries + NumericNullableOpState.Op.entries +
            MatchType.entries + BookSort.entries + SeriesSort.entries +
            BookMatchConditionState.BookConditionType.entries + SeriesMatchConditionState.SeriesConditionType.entries +
            BookFilterEditState.FilterType.entries + SeriesFilterEditState.FilterType.entries +
            FilterEditViewModel.FilterType.entries + KomgaReadStatus.entries + KomgaSeriesStatus.entries +
            KomgaMediaStatus.entries + MediaProfile.entries + PosterMatch.Type.entries + KomgaSort.Direction.entries +
            KomfAuthorRole.entries + KomfMediaType.entries + KomfNameMatchingMode.entries +
            KomfUpdateMode.entries + KomfReadingDirection.entries + MangaBakaMode.entries
        for (entry in entries) assertNotNull(enumLabelResource(entry), "${entry::class.simpleName}.${entry.name}")
    }
}
