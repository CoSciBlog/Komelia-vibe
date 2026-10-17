package snd.komelia.ui.common

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.Res
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.book_mark_read
import io.github.snd_r.komelia.ui.komelia_ui.generated.resources.book_mark_unread
import org.jetbrains.compose.resources.stringResource
import snd.komelia.komga.api.model.KomeliaBook
import snd.komelia.ui.common.menus.BookMenuActions

@Composable
fun BookReadStatusButtons(book: KomeliaBook, actions: BookMenuActions) {
    if (book.readProgress?.completed != true) {
        ElevatedButton(onClick = { actions.markAsRead(book) }) {
            Icon(Icons.Default.Check, contentDescription = null)
            Spacer(Modifier.width(3.dp))
            Text(stringResource(Res.string.book_mark_read))
        }
    }
    if (book.readProgress != null) {
        ElevatedButton(onClick = { actions.markAsUnread(book) }) {
            Icon(Icons.AutoMirrored.Filled.Undo, contentDescription = null)
            Spacer(Modifier.width(3.dp))
            Text(stringResource(Res.string.book_mark_unread))
        }
    }
}
