@file:OptIn(ExperimentalLayoutApi::class)

package com.cltf.aienglish.ui.main

import android.graphics.BitmapFactory
import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.PlaybackParams
import android.os.Build
import android.speech.tts.TextToSpeech
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.automirrored.outlined.Article
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.outlined.Article
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.Calculate
import androidx.compose.material.icons.outlined.Functions
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.EditNote
import androidx.compose.material.icons.outlined.NetworkCheck
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.PhotoLibrary
import androidx.compose.material.icons.outlined.Tag
import androidx.compose.material.icons.outlined.TextFields
import androidx.compose.material.icons.outlined.TextFormat
import androidx.compose.material.icons.automirrored.outlined.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Checkbox
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.core.content.FileProvider
import com.cltf.aienglish.AppViewModel
import com.cltf.aienglish.data.EssayExam
import com.cltf.aienglish.data.EssaySample
import com.cltf.aienglish.data.Mc688Entry
import com.cltf.aienglish.data.Mc688File
import com.cltf.aienglish.data.Mc688Repository
import com.cltf.aienglish.data.ReadingSkillSection
import com.cltf.aienglish.data.ReadingSkillsFile
import com.cltf.aienglish.data.ReadingSkillsRepository
import com.cltf.aienglish.data.ReadingHighFreqEntry
import com.cltf.aienglish.data.ReadingHighFreqFile
import com.cltf.aienglish.data.ReadingHighFreqRepository
import com.cltf.aienglish.data.ReadingPack
import com.cltf.aienglish.data.ReadingSubject
import com.cltf.aienglish.data.WordRecord
import com.cltf.aienglish.data.DaofaPastExamItem
import com.cltf.aienglish.data.DaofaPastExamsFile
import com.cltf.aienglish.data.DaofaPastExamsRepository
import com.cltf.aienglish.data.ChineseZhongkaoRepository
import com.cltf.aienglish.data.MathZhongkaoRepository
import com.cltf.aienglish.domain.ParagraphGist
import com.cltf.aienglish.domain.ReadingAnalysis
import com.cltf.aienglish.ui.components.AppGroupedBackground
import com.cltf.aienglish.ui.components.AppSectionCard
import com.cltf.aienglish.ui.components.ErrorBanner
import com.cltf.aienglish.ui.components.HintPill
import com.cltf.aienglish.ui.components.OrangeWordChip
import com.cltf.aienglish.ui.components.PrimaryFullWidthButton
import com.cltf.aienglish.ui.components.SectionHeader
import com.cltf.aienglish.ui.components.TealReadabilityCard
import com.cltf.aienglish.ui.theme.AppColors
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.abs
import kotlin.math.min
import java.io.File
import java.io.FileInputStream
import java.nio.charset.StandardCharsets
import java.util.Locale

enum class WordSheetSource { VOCABULARY, SCAN, NOTEBOOK }

data class WordSheetRequest(val word: String, val source: WordSheetSource)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(vm: AppViewModel) {
    var tab by remember { mutableIntStateOf(0) }
    var sheetRequest by remember { mutableStateOf<WordSheetRequest?>(null) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    val hideTopBar = LocalConfiguration.current.smallestScreenWidthDp >= 600

    val topTitles = listOf("语文", "数学", "英语", "物理", "道法", "我的")
    val bottomLabels = listOf("语文", "数学", "英语", "物理", "道法", "我的")
    val tabIcons = listOf(
        Icons.Outlined.Article,
        Icons.Outlined.Functions,
        Icons.Outlined.Book,
        Icons.Outlined.Calculate,
        Icons.Outlined.Description,
        Icons.Outlined.Person
    )

    Box(Modifier.fillMaxSize()) {
        Scaffold(
        containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
        topBar = {
            if (!hideTopBar) {
                CenterAlignedTopAppBar(
                    title = { Text(topTitles[tab], fontWeight = FontWeight.SemiBold) },
                    colors = androidx.compose.material3.TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        titleContentColor = AppColors.TextPrimary
                    )
                )
            }
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 3.dp
            ) {
                bottomLabels.forEachIndexed { index, label ->
                    NavigationBarItem(
                        selected = tab == index,
                        onClick = { tab = index },
                        icon = { Icon(tabIcons[index], contentDescription = null) },
                        label = { Text(label) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f)
                        )
                    )
                }
            }
        }
    ) { padding ->
        AppGroupedBackground(
            Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (tab) {
                0 -> ChineseHubTab(vm)
                1 -> MathHubTab()
                2 -> EnglishHubTab(vm) { w, src -> sheetRequest = WordSheetRequest(w, src) }
                3 -> PhysicsHubTab()
                4 -> DaofaTab()
                5 -> ProfileTab(vm) { sheetRequest = WordSheetRequest(it, WordSheetSource.NOTEBOOK) }
            }
        }
    }

    sheetRequest?.let { req ->
        ModalBottomSheet(
            onDismissRequest = { sheetRequest = null },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
            dragHandle = null
        ) {
            WordDetailSheetContent(
                word = req.word,
                source = req.source,
                vm = vm,
                onDismiss = { sheetRequest = null }
            )
        }
    }

        vm.notebookToast?.let { msg ->
            Popup(
                alignment = Alignment.BottomCenter,
                properties = PopupProperties(focusable = false, dismissOnBackPress = false, dismissOnClickOutside = false)
            ) {
                Surface(
                    modifier = Modifier.padding(bottom = 96.dp, start = 24.dp, end = 24.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.inverseSurface.copy(alpha = 0.92f),
                    shadowElevation = 8.dp
                ) {
                    Text(
                        msg,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
                        color = MaterialTheme.colorScheme.inverseOnSurface,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
private fun fontScaleSp(key: String): androidx.compose.ui.unit.TextUnit {
    val base = when (key) {
        "small" -> 14f
        "large" -> 18f
        "xlarge" -> 20f
        else -> 16f
    }
    return base.sp
}

@Composable
private fun VocabularyTab(vm: AppViewModel, onWordClick: (String) -> Unit) {
    val font = fontScaleSp(vm.fontScaleKey)
    if (!vm.vocabLoaded) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }
        return
    }
    Column(
        Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        AppSectionCard(elevated = false) {
            Row(
                Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = vm.vocabSearch,
                    onValueChange = {
                        vm.vocabSearch = it
                        vm.vocabPage = 1
                    },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("搜索单词…") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = AppColors.Divider
                    )
                )
                var showFilter by remember { mutableStateOf(false) }
                OutlinedButton(onClick = { showFilter = true }, shape = RoundedCornerShape(12.dp)) {
                    Text("筛选")
                }
                if (showFilter) {
                    AlertDialog(
                        onDismissRequest = { showFilter = false },
                        title = { Text("筛选词汇类型") },
                        text = {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                listOf("ALL" to "所有词汇", "MIDDLE_SCHOOL" to "中考词汇", "ADVANCED" to "超纲词汇").forEach { (v, label) ->
                                    FilterChip(
                                        selected = vm.vocabFilter == v,
                                        onClick = {
                                            vm.vocabFilter = v
                                            vm.vocabPage = 1
                                        },
                                        label = { Text(label) }
                                    )
                                }
                            }
                        },
                        confirmButton = {
                            TextButton(onClick = { showFilter = false }) { Text("确定") }
                        }
                    )
                }
            }
        }
        Text(
            "当前筛选共 ${vm.filteredVocabulary.size} 个 · 词库 ${vm.vocabularyRepository.totalCount} 个 · 每页 20 个",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 10.dp)
        )
        LazyColumn(
            Modifier.weight(1f),
            contentPadding = PaddingValues(bottom = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(vm.vocabPageItems, key = { it.word }) { rec ->
                AppSectionCard(elevated = true) {
                    Row(
                        Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            Modifier
                                .weight(1f)
                                .clickable { onWordClick(rec.word) }
                                .padding(16.dp)
                        ) {
                            Text(
                                rec.word,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = AppColors.TextPrimary,
                                fontSize = (font.value + 1).sp
                            )
                            Spacer(Modifier.height(6.dp))
                            Text(meaningLine(rec), style = MaterialTheme.typography.bodyMedium, fontSize = font, color = AppColors.TextPrimary)
                            Spacer(Modifier.height(4.dp))
                            Text(metaLine(rec), style = MaterialTheme.typography.bodySmall, fontSize = (font.value * 0.85f).sp, color = AppColors.TextSecondary)
                        }
                        IconButton(
                            onClick = { vm.addNotebook(rec.word) },
                            modifier = Modifier.padding(end = 4.dp)
                        ) {
                            Text(
                                "+",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
        if (vm.vocabTotalPages > 1) {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 1.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = { vm.vocabPage = (vm.vocabPage - 1).coerceAtLeast(1) },
                        enabled = vm.vocabPage > 1
                    ) { Text("上一页") }
                    Text("${vm.vocabPage} / ${vm.vocabTotalPages}", style = MaterialTheme.typography.labelLarge)
                    TextButton(
                        onClick = { vm.vocabPage = (vm.vocabPage + 1).coerceAtMost(vm.vocabTotalPages) },
                        enabled = vm.vocabPage < vm.vocabTotalPages
                    ) { Text("下一页") }
                }
            }
        }
    }
}

private fun meaningLine(rec: WordRecord): String {
    val parts = rec.definitions.map { d ->
        val p = d.partOfSpeech.trim()
        val m = d.meaning.trim()
        if (p.isEmpty()) m else "$p $m"
    }.filter { it.isNotEmpty() }
    return if (parts.isEmpty()) "暂无释义" else parts.joinToString(" · ")
}

private fun metaLine(rec: WordRecord): String {
    val t = if (rec.type == "ADVANCED") "超纲" else "中考"
    val ph = rec.phonetic.trim()
    return if (ph.isEmpty()) t else "$t · $ph"
}

private fun MediaPlayer.configureEssayTtsAudio() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                .build()
        )
    } else {
        @Suppress("DEPRECATION")
        setAudioStreamType(AudioManager.STREAM_MUSIC)
    }
    setVolume(1f, 1f)
}

/** Call from a background thread; [MediaPlayer.prepare] blocks. */
private fun applyEssayPlaybackSpeed(mp: MediaPlayer?, speed: Float) {
    if (mp == null || Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return
    try {
        val s = speed.coerceIn(0.5f, 2f)
        mp.playbackParams = PlaybackParams().setSpeed(s)
    } catch (_: Exception) {
    }
}

private fun formatEssaySpeedLabel(s: Float): String {
    return if (abs(s - 1f) < 0.001f) "1×" else String.format(Locale.US, "%.2g×", s)
}

private val essayPlaybackSpeedOptions = listOf(0.75f, 1f, 1.25f, 1.5f, 2f)

private fun prepareEssayTtsMediaPlayer(file: File): MediaPlayer {
    val mp = MediaPlayer()
    mp.configureEssayTtsAudio()
    FileInputStream(file).use { fis ->
        mp.setDataSource(fis.fd)
        mp.prepare()
    }
    return mp
}

private fun readBundledEssayAudio(context: Context, sampleId: String): ByteArray? {
    return try {
        context.assets.open("audio/essays/$sampleId.wav").use { it.readBytes() }
    } catch (_: Exception) {
        null
    }
}

@Composable
private fun ReadingMaterialsBlock(vm: AppViewModel) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var subjectKey by remember { mutableStateOf("english") }
    var subjectMenuExpanded by remember { mutableStateOf(false) }
    val font = fontScaleSp(vm.fontScaleKey)
    var ttsLoadingId by remember { mutableStateOf<String?>(null) }
    var ttsActiveId by remember { mutableStateOf<String?>(null) }
    var ttsPlaying by remember { mutableStateOf(false) }
    var ttsProgress by remember { mutableFloatStateOf(0f) }
    var ttsCurrentMs by remember { mutableIntStateOf(0) }
    var ttsDurationMs by remember { mutableIntStateOf(0) }
    var ttsPlayer by remember { mutableStateOf<MediaPlayer?>(null) }
    var ttsAudioFile by remember { mutableStateOf<File?>(null) }
    var ttsError by remember { mutableStateOf<String?>(null) }

    fun stopReadingTts(resetProgress: Boolean) {
        ttsPlayer?.setOnCompletionListener(null)
        ttsPlayer?.release()
        ttsPlayer = null
        ttsAudioFile?.delete()
        ttsAudioFile = null
        ttsPlaying = false
        if (resetProgress) {
            ttsActiveId = null
            ttsProgress = 0f
            ttsCurrentMs = 0
            ttsDurationMs = 0
        }
    }

    fun toggleSectionTts(sectionId: String, text: String) {
        if (ttsLoadingId != null) return
        val current = ttsPlayer
        if (ttsActiveId == sectionId && current != null) {
            if (current.isPlaying) {
                current.pause()
                ttsPlaying = false
            } else {
                current.start()
                ttsPlaying = true
            }
            return
        }

        stopReadingTts(resetProgress = true)
        ttsError = null
        ttsLoadingId = sectionId
        scope.launch {
            try {
                val bytes = vm.aiRepository.synthesizeEssayTts(text.trim())
                val safeId = sectionId.replace(Regex("[^a-zA-Z0-9._-]"), "_").take(80)
                val f = File(context.cacheDir, "reading_tts_$safeId.wav")
                val mp = withContext(Dispatchers.IO) {
                    f.writeBytes(bytes)
                    prepareEssayTtsMediaPlayer(f)
                }
                mp.setOnErrorListener { _, what, extra ->
                    ttsError = "阅读材料播放失败（错误码 $what/$extra）"
                    true
                }
                mp.setOnCompletionListener {
                    ttsPlaying = false
                    ttsCurrentMs = ttsDurationMs
                    ttsProgress = 1f
                }
                ttsAudioFile = f
                ttsPlayer = mp
                ttsActiveId = sectionId
                ttsDurationMs = mp.duration.coerceAtLeast(0)
                ttsCurrentMs = 0
                ttsProgress = 0f
                mp.start()
                ttsPlaying = true
            } catch (e: Exception) {
                ttsError = "阅读材料语音生成失败：${e.message ?: e}"
                stopReadingTts(resetProgress = true)
            } finally {
                ttsLoadingId = null
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose { stopReadingTts(resetProgress = true) }
    }

    LaunchedEffect(ttsActiveId, ttsPlaying, ttsPlayer) {
        while (ttsActiveId != null && ttsPlaying && ttsPlayer != null) {
            val mp = ttsPlayer ?: break
            val d = mp.duration.coerceAtLeast(0)
            val c = mp.currentPosition.coerceAtLeast(0)
            ttsDurationMs = d
            ttsCurrentMs = c
            ttsProgress = if (d > 0) c.toFloat() / d.toFloat() else 0f
            delay(250)
        }
    }

    if (!vm.readingLoaded) {
        AppSectionCard(elevated = false) {
            Row(
                Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                CircularProgressIndicator(Modifier.size(22.dp), strokeWidth = 2.dp)
                Text("加载阅读材料…", color = AppColors.TextSecondary)
            }
        }
        return
    }

    vm.readingLoadError?.let { err ->
        AppSectionCard(elevated = false) {
            Text(err, color = AppColors.TextSecondary, modifier = Modifier.padding(16.dp))
        }
        return
    }

    val subjects = vm.readingSubjects
    if (subjects.isEmpty()) {
        AppSectionCard(elevated = false) {
            Text("暂无阅读材料配置。", color = AppColors.TextSecondary, modifier = Modifier.padding(16.dp))
        }
        return
    }

    LaunchedEffect(subjects, subjectKey) {
        if (subjects.none { it.id == subjectKey }) {
            subjectKey = subjects.first().id
        }
    }

    val currentSubject: ReadingSubject = subjects.find { it.id == subjectKey } ?: subjects.first()

    AppSectionCard(elevated = true) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            SectionHeader(Icons.AutoMirrored.Outlined.Article, "阅读材料", "分学科收录真题与范文节选")
            ttsError?.let { err -> ErrorBanner(err) }
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("学科", style = MaterialTheme.typography.labelLarge, color = AppColors.TextSecondary)
                Box {
                    OutlinedButton(
                        onClick = { subjectMenuExpanded = true },
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(currentSubject.label)
                        Spacer(Modifier.size(4.dp))
                        Icon(Icons.Filled.ArrowDropDown, contentDescription = null, Modifier.size(22.dp))
                    }
                    DropdownMenu(
                        expanded = subjectMenuExpanded,
                        onDismissRequest = { subjectMenuExpanded = false }
                    ) {
                        for (s in subjects) {
                            DropdownMenuItem(
                                text = { Text(s.label) },
                                onClick = {
                                    subjectKey = s.id
                                    subjectMenuExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            when {
                currentSubject.packs.isEmpty() -> {
                    Text(
                        "该学科阅读材料将陆续补充。",
                        style = MaterialTheme.typography.bodyMedium,
                        color = AppColors.TextSecondary
                    )
                }
                else -> {
                    for (pack in currentSubject.packs) {
                        ReadingPackSections(
                            pack = pack,
                            font = font,
                            activeSectionId = ttsActiveId,
                            loadingSectionId = ttsLoadingId,
                            isPlaying = ttsPlaying,
                            progress = ttsProgress,
                            timeText = "${formatDurationMs(ttsCurrentMs)} / ${formatDurationMs(ttsDurationMs)}",
                            onToggleTts = ::toggleSectionTts
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ReadingPackSections(
    pack: ReadingPack,
    font: androidx.compose.ui.unit.TextUnit,
    activeSectionId: String?,
    loadingSectionId: String?,
    isPlaying: Boolean,
    progress: Float,
    timeText: String,
    onToggleTts: (sectionId: String, text: String) -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.28f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            "📚 ${pack.title}",
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            fontWeight = FontWeight.SemiBold,
            color = AppColors.TextPrimary,
            style = MaterialTheme.typography.titleSmall
        )
    }
    Spacer(Modifier.height(10.dp))
    for (sec in pack.sections) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surfaceContainerHigh,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp)
        ) {
            Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(sec.headline, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.titleSmall)
                Row(
                    Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = { onToggleTts(sec.id, sec.body) },
                        enabled = loadingSectionId == null || loadingSectionId == sec.id,
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            when {
                                loadingSectionId == sec.id -> "生成中…"
                                activeSectionId == sec.id && isPlaying -> "暂停"
                                activeSectionId == sec.id -> "继续播放"
                                else -> "播放"
                            }
                        )
                    }
                    LinearProgressIndicator(
                        progress = {
                            if (activeSectionId == sec.id) progress else 0f
                        },
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        if (activeSectionId == sec.id) timeText else "0:00 / 0:00",
                        style = MaterialTheme.typography.labelSmall,
                        color = AppColors.TextSecondary
                    )
                }
                Text(
                    sec.body.trim(),
                    style = MaterialTheme.typography.bodyMedium,
                    fontSize = font,
                    lineHeight = (font.value * 1.42f).sp,
                    color = AppColors.TextPrimary
                )
            }
        }
    }
    pack.footer?.takeIf { it.isNotBlank() }?.let { foot ->
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surfaceContainerLow,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                foot.trim(),
                modifier = Modifier.padding(14.dp),
                style = MaterialTheme.typography.bodySmall,
                lineHeight = 20.sp,
                color = AppColors.TextSecondary
            )
        }
    }
}

@Composable
private fun ScanTab(vm: AppViewModel, onWordClick: (String) -> Unit) {
    val context = LocalContext.current
    val pick = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri ?: return@rememberLauncherForActivityResult
        context.contentResolver.openInputStream(uri)?.use { stream ->
            val bmp = BitmapFactory.decodeStream(stream)
            vm.scanBitmap = bmp
            vm.resetOcrResults()
        }
    }
    val photoFile = remember { File(context.cacheDir, "aienglish_capture.jpg") }
    val cameraUri = remember {
        FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            photoFile
        )
    }
    val camera = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { ok ->
        if (ok && photoFile.exists()) {
            val bmp = BitmapFactory.decodeFile(photoFile.absolutePath)
            vm.scanBitmap = bmp
            vm.resetOcrResults()
        }
    }

    val font = fontScaleSp(vm.fontScaleKey)

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        ReadingMaterialsBlock(vm)

        AppSectionCard(elevated = true) {
            Column(Modifier.padding(16.dp)) {
                SectionHeader(
                    Icons.Outlined.Description,
                    "图片",
                    "选择教材、试卷等含英文的照片"
                )
                Spacer(Modifier.height(12.dp))
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = MaterialTheme.colorScheme.surfaceContainerHigh,
                    border = androidx.compose.foundation.BorderStroke(0.5.dp, AppColors.Divider.copy(alpha = 0.35f))
                ) {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .height(220.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        vm.scanBitmap?.let { bmp ->
                            Image(
                                bitmap = bmp.asImageBitmap(),
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(10.dp)
                                    .clip(RoundedCornerShape(14.dp))
                            )
                        } ?: Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Icon(Icons.Outlined.PhotoLibrary, null, Modifier.size(44.dp), tint = AppColors.TextHint)
                            Text("点击下方选择相册或拍照", style = MaterialTheme.typography.bodyMedium, color = AppColors.TextSecondary)
                        }
                    }
                }
            }
        }

        AppSectionCard(elevated = false) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                SectionHeader(Icons.Outlined.AutoAwesome, "操作", null)
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(
                        onClick = {
                            pick.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Outlined.PhotoLibrary, null, Modifier.size(20.dp))
                        Spacer(Modifier.size(6.dp))
                        Text("相册", fontWeight = FontWeight.Medium)
                    }
                    OutlinedButton(
                        onClick = { camera.launch(cameraUri) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Outlined.CameraAlt, null, Modifier.size(20.dp))
                        Spacer(Modifier.size(6.dp))
                        Text("拍照", fontWeight = FontWeight.Medium)
                    }
                    OutlinedButton(
                        onClick = { vm.clearScan() },
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Outlined.Delete, contentDescription = "清除")
                    }
                }
                PrimaryFullWidthButton(
                    text = if (vm.ocrLoading) "正在识别英文…" else "识别图中英文",
                    enabled = vm.scanBitmap != null,
                    loading = vm.ocrLoading,
                    onClick = { vm.scanBitmap?.let { vm.runOcr(it) } },
                    leadingIcon = if (!vm.ocrLoading) {
                        { Icon(Icons.Outlined.TextFields, null, Modifier.size(22.dp), tint = androidx.compose.ui.graphics.Color.White) }
                    } else null
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.Top) {
                    Icon(Icons.Outlined.NetworkCheck, null, Modifier.size(16.dp), tint = AppColors.TextHint)
                    HintPill("OCR 与 AI 分析需本机 8787 端口代理（与网页版一致）。模拟器请用 10.0.2.2 访问电脑。")
                }
            }
        }

        vm.scanError?.let { err ->
            ErrorBanner(err)
        }

        if (vm.ocrText.isNotEmpty() || vm.ocrLoading) {
            AppSectionCard(elevated = false) {
                Column(Modifier.padding(16.dp)) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        SectionHeader(Icons.Outlined.Description, "识别结果", null)
                        if (vm.ocrLoading) CircularProgressIndicator(Modifier.size(22.dp), strokeWidth = 2.dp)
                    }
                    Spacer(Modifier.height(10.dp))
                    Surface(shape = RoundedCornerShape(12.dp), color = MaterialTheme.colorScheme.surfaceContainerHigh) {
                        Text(
                            vm.ocrText.ifEmpty { "正在从图片中提取英文…" },
                            modifier = Modifier.padding(14.dp),
                            style = MaterialTheme.typography.bodyLarge,
                            fontSize = font,
                            lineHeight = (font.value * 1.45f).sp
                        )
                    }
                }
            }
        }

        if (vm.ocrText.isNotBlank()) {
            val acc = vm.accuracyPercent()
            val unknown = vm.unknownWordsForOcr()

            AppSectionCard(elevated = false) {
                Column(Modifier.padding(16.dp)) {
                    SectionHeader(Icons.Outlined.BarChart, "词汇与难度", "相对词库的启发式估计")
                    Spacer(Modifier.height(12.dp))
                    TealReadabilityCard(percent = acc)
                    Spacer(Modifier.height(12.dp))
                    if (unknown.isEmpty()) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Filled.CheckCircle, null, tint = AppColors.AccentTeal, modifier = Modifier.size(20.dp))
                            Text("未发现明显超纲词", style = MaterialTheme.typography.bodyMedium, color = AppColors.TextSecondary)
                        }
                    } else {
                        Text(
                            "可能需关注（${unknown.size}）",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = AppColors.TextSecondary,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            unknown.forEach { w ->
                                OrangeWordChip(w) { onWordClick(w) }
                            }
                        }
                    }
                }
            }

            AppSectionCard(elevated = false) {
                Column(Modifier.padding(16.dp)) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        SectionHeader(Icons.AutoMirrored.Outlined.MenuBook, "阅读分析", null)
                        androidx.compose.material3.FilledTonalButton(
                            onClick = { vm.runAiAnalysis() },
                            enabled = !vm.aiLoading,
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            if (vm.aiLoading) {
                                CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp)
                                Spacer(Modifier.size(6.dp))
                            } else {
                                Icon(Icons.Outlined.AutoAwesome, null, Modifier.size(18.dp))
                                Spacer(Modifier.size(4.dp))
                            }
                            Text(if (vm.aiLoading) "分析中" else "AI 深度")
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    vm.readingAnalysis?.let { a ->
                        ReadingAnalysisBlock(a, font.value)
                    }
                }
            }
        }
    }
}

@Composable
private fun ReadingAnalysisBlock(a: ReadingAnalysis, baseFontSp: Float) {
    Column {
        Surface(
            shape = RoundedCornerShape(10.dp),
            color = MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.5f)
        ) {
            Text(
                if (a.mode == "ai") "AI 生成" else "本地启发式",
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = AppColors.TextSecondary
            )
        }
        Spacer(Modifier.height(10.dp))
    if (a.coreViewpointZh.isNotEmpty()) {
        AnalysisSubBlock(title = "核心观点", icon = Icons.Outlined.Description) {
            Text(a.coreViewpointZh, fontSize = (baseFontSp * 0.95f).sp, lineHeight = (baseFontSp * 1.4f).sp)
        }
    }
    for (g in a.paragraphGists) {
        ParagraphGistBlock(g, baseFontSp)
    }
    if (a.examPoints.isNotEmpty()) {
        AnalysisSubBlock(title = "考点提示", icon = Icons.Outlined.Book) {
            for (item in a.examPoints) {
                Text("· $item", fontSize = (baseFontSp * 0.9f).sp, lineHeight = 22.sp)
            }
        }
    }
    if (a.logicRelations.isNotEmpty()) {
        AnalysisSubBlock(title = "逻辑关系", icon = Icons.Outlined.Description) {
            for (item in a.logicRelations) {
                Text("→ $item", fontSize = (baseFontSp * 0.9f).sp, lineHeight = 22.sp)
            }
        }
    }
    if (a.howToSolveZh.isNotEmpty()) {
        AnalysisSubBlock(title = "解题策略", icon = Icons.AutoMirrored.Outlined.MenuBook) {
            Text(a.howToSolveZh, fontSize = (baseFontSp * 0.95f).sp, lineHeight = 22.sp)
        }
    }
    }
}

@Composable
private fun AnalysisSubBlock(title: String, icon: ImageVector, content: @Composable () -> Unit) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Icon(icon, null, Modifier.size(18.dp), tint = AppColors.TextSecondary)
            Text(title, fontWeight = FontWeight.SemiBold, color = AppColors.TextSecondary, fontSize = 14.sp)
        }
        Spacer(Modifier.height(6.dp))
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surfaceContainerHigh,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(Modifier.padding(12.dp)) {
                content()
            }
        }
    }
}

@Composable
private fun ParagraphGistBlock(g: ParagraphGist, baseFontSp: Float) {
    Column(Modifier.padding(top = 8.dp)) {
        Text("第 ${g.index} 段", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.height(4.dp))
        Text(g.gistZh, fontSize = (baseFontSp * 0.95f).sp, lineHeight = 22.sp)
        if (g.keySentenceEn.isNotEmpty()) {
            Spacer(Modifier.height(6.dp))
            Surface(shape = RoundedCornerShape(8.dp), color = MaterialTheme.colorScheme.surfaceContainerHigh) {
                Text(
                    text = g.keySentenceEn,
                    modifier = Modifier.padding(10.dp),
                    fontSize = (baseFontSp * 0.85f).sp,
                    fontStyle = FontStyle.Italic,
                    color = AppColors.TextSecondary
                )
            }
        }
    }
}

@Composable
private fun EnglishHubTab(vm: AppViewModel, onOpenWord: (String, WordSheetSource) -> Unit) {
    // 0=试卷结构 1=词库 … 默认词库
    var sub by remember { mutableIntStateOf(1) }
    Column(Modifier.fillMaxSize()) {
        FlowRow(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf(
                "试卷结构" to 0,
                "词库" to 1,
                "英语阅读" to 2,
                "阅读高频" to 3,
                "阅读技巧" to 4,
                "21天688" to 5,
                "英语作文" to 6
            ).forEach { (label, idx) ->
                FilterChip(
                    selected = sub == idx,
                    onClick = { sub = idx },
                    label = { Text(label) }
                )
            }
        }
        Box(Modifier.weight(1f)) {
            when (sub) {
                0 -> EnglishStructureTab()
                1 -> VocabularyTab(vm) { onOpenWord(it, WordSheetSource.VOCABULARY) }
                2 -> ScanTab(vm) { onOpenWord(it, WordSheetSource.SCAN) }
                3 -> ReadingHighFreqTab(vm)
                4 -> ReadingSkillsTab()
                5 -> Mc688Tab(vm)
                6 -> EssayTab(vm, fixedSubject = "english")
            }
        }
    }
}

@Composable
private fun ReadingSkillsTab() {
    val context = LocalContext.current
    var file by remember { mutableStateOf<ReadingSkillsFile?>(null) }
    var loadError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        loadError = null
        try {
            file = withContext(Dispatchers.IO) {
                ReadingSkillsRepository.load(context)
            }
        } catch (e: Exception) {
            loadError = e.message ?: e.toString()
        }
    }

    val data = file
    val err = loadError
    when {
        err != null && data == null -> Box(
            Modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(err, color = AppColors.TextSecondary)
        }
        data == null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }
        else -> {
            LazyColumn(
                Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(
                        data.label,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = AppColors.TextPrimary
                    )
                    data.intro?.takeIf { it.isNotBlank() }?.let { intro ->
                        Text(
                            intro,
                            style = MaterialTheme.typography.bodyMedium,
                            color = AppColors.TextSecondary,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
                items(data.topics, key = { it.id }) { topic ->
                    AppSectionCard(elevated = false, modifier = Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(12.dp)) {
                            Text(
                                topic.title,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = AppColors.PrimaryBlue
                            )
                            topic.summary?.takeIf { it.isNotBlank() }?.let { s ->
                                Text(
                                    s,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = AppColors.TextSecondary,
                                    modifier = Modifier.padding(top = 6.dp),
                                    lineHeight = 18.sp
                                )
                            }
                            topic.sections.forEach { sec ->
                                ReadingSkillSectionBlock(sec)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ReadingSkillSectionBlock(sec: ReadingSkillSection) {
    Column(Modifier.padding(top = 10.dp)) {
        sec.subtitle?.takeIf { it.isNotBlank() }?.let { st ->
            Text(
                st,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Medium,
                color = AppColors.TextPrimary,
                modifier = Modifier.padding(bottom = 4.dp)
            )
        }
        sec.paragraph?.takeIf { it.isNotBlank() }?.let { p ->
            Text(
                p,
                style = MaterialTheme.typography.bodyMedium,
                color = AppColors.TextPrimary,
                lineHeight = 20.sp
            )
        }
        sec.bullets?.takeIf { it.isNotEmpty() }?.let { list ->
            list.forEach { line ->
                Row(
                    Modifier.padding(top = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text("•", color = AppColors.TextSecondary, style = MaterialTheme.typography.bodyMedium)
                    Text(
                        line,
                        style = MaterialTheme.typography.bodyMedium,
                        color = AppColors.TextPrimary,
                        modifier = Modifier.weight(1f),
                        lineHeight = 20.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun Mc688Tab(vm: AppViewModel) {
    val context = LocalContext.current
    var tts by remember { mutableStateOf<TextToSpeech?>(null) }
    var ttsReady by remember { mutableStateOf(false) }
    var file by remember { mutableStateOf<Mc688File?>(null) }
    var loadError by remember { mutableStateOf<String?>(null) }
    var selectedDay by remember { mutableIntStateOf(1) }
    var count by remember { mutableIntStateOf(10) }
    var shuffle by remember { mutableStateOf(false) }
    var session by remember { mutableStateOf<List<Mc688Entry>?>(null) }
    var idx by remember { mutableIntStateOf(0) }
    var answer by remember { mutableStateOf("") }
    var feedback by remember { mutableStateOf<String?>(null) }

    DisposableEffect(context) {
        val appContext = context.applicationContext
        lateinit var engine: TextToSpeech
        engine = TextToSpeech(appContext) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val lr = engine.setLanguage(Locale.US)
                if (lr == TextToSpeech.LANG_MISSING_DATA || lr == TextToSpeech.LANG_NOT_SUPPORTED) {
                    engine.setLanguage(Locale.ENGLISH)
                }
                ttsReady = true
            }
        }
        tts = engine
        onDispose {
            ttsReady = false
            engine.stop()
            engine.shutdown()
            tts = null
        }
    }

    LaunchedEffect(Unit) {
        loadError = null
        try {
            file = withContext(Dispatchers.IO) {
                Mc688Repository.load(context)
            }
        } catch (e: Exception) {
            loadError = e.message ?: e.toString()
        }
    }

    fun speakWord(w: String) {
        val engine = tts ?: return
        if (!ttsReady) return
        engine.speak(w, TextToSpeech.QUEUE_FLUSH, null, "mc688_${w}_${System.nanoTime()}")
    }

    val data = file
    val err = loadError
    when {
        err != null && data == null -> Box(
            Modifier.fillMaxSize().padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(err, color = AppColors.TextSecondary)
        }
        data == null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }
        else -> {
            val pool = data.entries.filter { it.day == selectedDay }
            val maxN = min(33, pool.size).coerceAtLeast(1)
            Column(
                Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    data.subtitle ?: "",
                    style = MaterialTheme.typography.bodySmall,
                    color = AppColors.TextSecondary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text("选择天数", style = MaterialTheme.typography.labelMedium, color = AppColors.TextSecondary)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(vertical = 8.dp)) {
                    items((1..21).toList()) { d ->
                        FilterChip(
                            selected = selectedDay == d,
                            onClick = {
                                selectedDay = d
                                session = null
                                feedback = null
                            },
                            label = { Text("第${d}天") }
                        )
                    }
                }
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                Text("听写（系统英语朗读）", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                Text(
                    "从当天词汇中抽取若干词，按顺序朗读，请输入并提交。",
                    style = MaterialTheme.typography.bodySmall,
                    color = AppColors.TextSecondary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Text("词数", style = MaterialTheme.typography.bodyMedium)
                    OutlinedTextField(
                        value = count.toString(),
                        onValueChange = { v ->
                            val digits = v.filter { it.isDigit() }
                            if (digits.isNotEmpty()) {
                                digits.toIntOrNull()?.let { count = it.coerceIn(1, maxN) }
                            }
                        },
                        modifier = Modifier.width(88.dp),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    Text("/ $maxN", style = MaterialTheme.typography.bodySmall, color = AppColors.TextSecondary)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = shuffle, onCheckedChange = { shuffle = it })
                    Text("随机顺序", style = MaterialTheme.typography.bodyMedium)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(vertical = 8.dp)) {
                    Button(onClick = {
                        if (pool.isEmpty()) return@Button
                        val n = min(count.coerceAtLeast(1), pool.size)
                        val pick = if (shuffle) pool.shuffled().take(n) else pool.take(n)
                        session = pick
                        idx = 0
                        answer = ""
                        feedback = null
                        speakWord(pick.first().word)
                    }) { Text("开始听写") }
                    OutlinedButton(onClick = {
                        val s = session ?: return@OutlinedButton
                        s.getOrNull(idx)?.let { speakWord(it.word) }
                    }, enabled = session != null) { Text("再听一遍") }
                }
                session?.let { s ->
                    val cur = s.getOrNull(idx)
                    if (cur != null) {
                        Text(
                            "第 ${idx + 1} / ${s.size} 词 · 序号 ${cur.rank}",
                            style = MaterialTheme.typography.labelLarge,
                            color = AppColors.PrimaryBlue,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        OutlinedTextField(
                            value = answer,
                            onValueChange = { answer = it },
                            label = { Text("输入英文单词") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                capitalization = KeyboardCapitalization.None,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(onDone = { })
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 8.dp)) {
                            Button(onClick = {
                                val ok = answer.trim().equals(cur.word, ignoreCase = true)
                                feedback = if (ok) "正确" else "正确写法：${cur.word}"
                            }) { Text("提交") }
                            Button(onClick = {
                                if (idx >= s.size - 1) {
                                    session = null
                                    feedback = "本轮完成"
                                    return@Button
                                }
                                idx++
                                answer = ""
                                feedback = null
                                speakWord(s[idx].word)
                            }) { Text(if (idx >= s.size - 1) "结束" else "下一词") }
                        }
                    }
                }
                feedback?.let {
                    Text(it, color = if (it.startsWith("正确") && it.length <= 3) AppColors.PrimaryBlue else AppColors.TextSecondary, modifier = Modifier.padding(top = 8.dp))
                }
                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                Text("本日词表", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                pool.forEach { e ->
                    AppSectionCard(elevated = false, modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("${e.rank}", modifier = Modifier.width(40.dp), color = AppColors.TextSecondary, style = MaterialTheme.typography.labelSmall)
                            Column(Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text(
                                        e.word,
                                        fontWeight = FontWeight.SemiBold,
                                        modifier = Modifier.clickable { speakWord(e.word) }
                                    )
                                    IconButton(onClick = { speakWord(e.word) }, modifier = Modifier.size(32.dp)) {
                                        Icon(Icons.AutoMirrored.Outlined.VolumeUp, contentDescription = "朗读")
                                    }
                                }
                                Text(e.meaning, style = MaterialTheme.typography.bodySmall, color = AppColors.TextPrimary)
                            }
                            IconButton(onClick = { vm.addNotebook(e.word) }) {
                                Text(
                                    "+",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ReadingHighFreqTab(vm: AppViewModel) {
    val context = LocalContext.current
    var tts by remember { mutableStateOf<TextToSpeech?>(null) }
    var ttsReady by remember { mutableStateOf(false) }
    var file by remember { mutableStateOf<ReadingHighFreqFile?>(null) }
    var loadError by remember { mutableStateOf<String?>(null) }

    DisposableEffect(context) {
        val appContext = context.applicationContext
        lateinit var engine: TextToSpeech
        engine = TextToSpeech(appContext) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val lr = engine.setLanguage(Locale.US)
                if (lr == TextToSpeech.LANG_MISSING_DATA || lr == TextToSpeech.LANG_NOT_SUPPORTED) {
                    engine.setLanguage(Locale.ENGLISH)
                }
                ttsReady = true
            }
        }
        tts = engine
        onDispose {
            ttsReady = false
            engine.stop()
            engine.shutdown()
            tts = null
        }
    }

    LaunchedEffect(Unit) {
        loadError = null
        try {
            file = withContext(Dispatchers.IO) {
                ReadingHighFreqRepository.load(context)
            }
        } catch (e: Exception) {
            loadError = e.message ?: e.toString()
        }
    }

    fun speakWord(w: String) {
        val engine = tts ?: return
        if (!ttsReady) return
        val uttId = "hf_${w}_${System.nanoTime()}"
        engine.speak(w, TextToSpeech.QUEUE_FLUSH, null, uttId)
    }

    val data = file
    val err = loadError
    when {
        err != null && data == null -> Box(
            Modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(err, color = AppColors.TextSecondary, style = MaterialTheme.typography.bodyMedium)
        }
        data == null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }
        else -> {
            val note = data.note?.takeIf { it.isNotBlank() }
            LazyColumn(
                Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    Text(
                        "点击单词或 🔊 使用系统英语朗读（离线可用）。",
                        color = AppColors.TextSecondary,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }
                if (note != null) {
                    item {
                        Text(note, color = AppColors.TextSecondary, style = MaterialTheme.typography.labelSmall)
                    }
                }
                items(data.entries, key = { it.rank }) { e ->
                    ReadingHighFreqRow(e, onSpeak = { speakWord(it) }, onAddNotebook = { vm.addNotebook(it) })
                }
            }
        }
    }
}

@Composable
private fun ReadingHighFreqRow(
    entry: ReadingHighFreqEntry,
    onSpeak: (String) -> Unit,
    onAddNotebook: (String) -> Unit
) {
    AppSectionCard(elevated = false, modifier = Modifier.fillMaxWidth()) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                "${entry.rank}",
                modifier = Modifier.width(36.dp),
                style = MaterialTheme.typography.labelLarge,
                color = AppColors.TextSecondary
            )
            Column(Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        entry.word,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = AppColors.TextPrimary,
                        modifier = Modifier.clickable { onSpeak(entry.word) }
                    )
                    IconButton(
                        onClick = { onSpeak(entry.word) },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(Icons.AutoMirrored.Outlined.VolumeUp, contentDescription = "朗读 ${entry.word}")
                    }
                }
                Text(
                    entry.phonetic,
                    style = MaterialTheme.typography.bodySmall,
                    color = AppColors.TextSecondary,
                    modifier = Modifier.padding(top = 2.dp)
                )
                Text(
                    entry.meaning,
                    style = MaterialTheme.typography.bodyMedium,
                    color = AppColors.TextPrimary,
                    modifier = Modifier.padding(top = 4.dp),
                    lineHeight = 20.sp
                )
            }
            IconButton(onClick = { onAddNotebook(entry.word) }) {
                Text(
                    "+",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Text(
                "${entry.frequency}",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Medium,
                color = AppColors.PrimaryBlue,
                modifier = Modifier.width(40.dp)
            )
        }
    }
}

private data class DaofaSection(
    val title: String,
    val body: String,
    /** Android assets 相对路径，如 math2025/page01.png */
    val imageAssetPaths: List<String> = emptyList()
)

/** 按「📚 / 📊」标题拆成多块（主观题年份与命题总结等）。 */
private fun parseDaofaReference(text: String): List<DaofaSection> {
    val lines = text.lines()
    val out = mutableListOf<DaofaSection>()
    var i = 0
    while (i < lines.size) {
        val t = lines[i].trim()
        if (t.startsWith("📚") || t.startsWith("📊")) {
            val title = t
            val buf = StringBuilder()
            i++
            while (i < lines.size) {
                val lt = lines[i].trim()
                if (lt.startsWith("📚") || lt.startsWith("📊")) break
                buf.appendLine(lines[i])
                i++
            }
            out.add(DaofaSection(title, buf.toString().trim()))
        } else {
            i++
        }
    }
    return out
}

@Composable
private fun DaofaTab() {
    val context = LocalContext.current
    var referenceText by remember { mutableStateOf<String?>(null) }
    var referenceErr by remember { mutableStateOf<String?>(null) }
    var pastFile by remember { mutableStateOf<DaofaPastExamsFile?>(null) }
    var pastErr by remember { mutableStateOf<String?>(null) }
    var sub by remember { mutableIntStateOf(0) }
    var detail by remember { mutableStateOf<DaofaSection?>(null) }
    var pastDetail by remember { mutableStateOf<DaofaPastExamItem?>(null) }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            try {
                referenceText = context.assets.open("daofa_reference.txt").use { ins ->
                    String(ins.readBytes(), StandardCharsets.UTF_8)
                }
            } catch (e: Exception) {
                referenceErr = e.message ?: e.toString()
            }
            try {
                pastFile = DaofaPastExamsRepository.load(context)
            } catch (e: Exception) {
                pastErr = e.message ?: e.toString()
            }
        }
    }

    val sections = remember(referenceText) {
        referenceText?.let { parseDaofaReference(it) } ?: emptyList()
    }

    if (referenceText == null && referenceErr == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }
        return
    }

    Column(Modifier.fillMaxSize()) {
        FlowRow(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("试卷结构" to 0, "主观题" to 1, "历年中考题" to 2).forEach { (label, idx) ->
                FilterChip(
                    selected = sub == idx,
                    onClick = {
                        sub = idx
                        detail = null
                        pastDetail = null
                    },
                    label = { Text(label) }
                )
            }
        }
        Box(Modifier.weight(1f)) {
            when (sub) {
                0 -> DaofaStructureTab()
                1 -> when (val d = detail) {
                    null -> {
                        if (referenceErr != null && referenceText == null) {
                            val refErr = referenceErr
                            Box(
                                Modifier
                                    .fillMaxSize()
                                    .padding(24.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(refErr ?: "", color = AppColors.TextSecondary, style = MaterialTheme.typography.bodyMedium)
                            }
                        } else if (sections.isEmpty()) {
                            Box(
                                Modifier
                                    .fillMaxSize()
                                    .padding(24.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "暂无主观题条目。请确认 daofa_reference.txt 格式正确。",
                                    color = AppColors.TextSecondary,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        } else {
                            LazyColumn(
                                Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                item {
                                    Text(
                                        "按板块浏览，点击条目查看全文（与英语作文列表相同交互）。",
                                        color = AppColors.TextSecondary,
                                        style = MaterialTheme.typography.bodyMedium,
                                        modifier = Modifier.padding(bottom = 4.dp)
                                    )
                                }
                                items(sections, key = { it.title }) { sec ->
                                    AppSectionCard(elevated = true, modifier = Modifier.fillMaxWidth()) {
                                        Row(
                                            Modifier
                                                .fillMaxWidth()
                                                .clickable { detail = sec }
                                                .padding(16.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                sec.title,
                                                modifier = Modifier.weight(1f),
                                                fontWeight = FontWeight.Medium,
                                                color = AppColors.TextPrimary,
                                                style = MaterialTheme.typography.bodyLarge
                                            )
                                            Text("›", color = AppColors.TextHint, fontSize = 20.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                    else -> DaofaDetailPane(section = d, onBack = { detail = null })
                }
                else -> {
                    val pastItems = pastFile?.items ?: emptyList()
                    when (val pd = pastDetail) {
                        null -> {
                            when {
                                pastErr != null && pastFile == null -> Box(
                                    Modifier
                                        .fillMaxSize()
                                        .padding(24.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        "历年中考题：$pastErr",
                                        color = AppColors.TextSecondary,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                                pastFile == null && pastErr == null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                                }
                                pastItems.isEmpty() -> Box(
                                    Modifier
                                        .fillMaxSize()
                                        .padding(24.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        "暂无历年中考题数据。",
                                        color = AppColors.TextSecondary,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                                else -> LazyColumn(
                                    Modifier
                                        .fillMaxSize()
                                        .padding(horizontal = 16.dp, vertical = 8.dp),
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    item {
                                        Text(
                                            pastFile?.label?.takeIf { it.isNotBlank() }
                                                ?: "按条目浏览，点击查看全文（与主观题列表相同交互）。",
                                            color = AppColors.TextSecondary,
                                            style = MaterialTheme.typography.bodyMedium,
                                            modifier = Modifier.padding(bottom = 4.dp)
                                        )
                                    }
                                    items(pastItems, key = { it.id.ifEmpty { it.title } }) { item ->
                                        AppSectionCard(elevated = true, modifier = Modifier.fillMaxWidth()) {
                                            Row(
                                                Modifier
                                                    .fillMaxWidth()
                                                    .clickable { pastDetail = item }
                                                    .padding(16.dp),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    item.title,
                                                    modifier = Modifier.weight(1f),
                                                    fontWeight = FontWeight.Medium,
                                                    color = AppColors.TextPrimary,
                                                    style = MaterialTheme.typography.bodyLarge
                                                )
                                                Text("›", color = AppColors.TextHint, fontSize = 20.sp)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        else -> DaofaDetailPane(
                            section = DaofaSection(pd.title, pd.body),
                            barTitle = "历年中考题",
                            onBack = { pastDetail = null }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DaofaDetailPane(section: DaofaSection, onBack: () -> Unit, barTitle: String = "道法主观题") {
    Column(Modifier.fillMaxSize()) {
        Row(
            Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 4.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "返回")
            }
            Column(Modifier.weight(1f)) {
                Text(barTitle, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.titleMedium)
                Text(
                    section.title,
                    maxLines = 2,
                    style = MaterialTheme.typography.labelSmall,
                    color = AppColors.TextSecondary
                )
            }
        }
        HorizontalDivider()
        Column(
            Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                section.title,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium,
                color = AppColors.TextPrimary
            )
            AppSectionCard(elevated = true) {
                Column(Modifier.padding(14.dp)) {
                    if (section.body.isNotBlank()) {
                        Text("正文", fontWeight = FontWeight.SemiBold, color = AppColors.TextSecondary, style = MaterialTheme.typography.labelMedium)
                        Spacer(Modifier.height(8.dp))
                        Text(
                            section.body.trim(),
                            style = MaterialTheme.typography.bodyLarge,
                            color = AppColors.TextPrimary,
                            lineHeight = 24.sp
                        )
                    }
                    if (section.imageAssetPaths.isNotEmpty()) {
                        if (section.body.isNotBlank()) {
                            Spacer(Modifier.height(12.dp))
                        }
                        Text(
                            "试卷图（点击放大，双指可缩放）",
                            fontWeight = FontWeight.SemiBold,
                            color = AppColors.TextSecondary,
                            style = MaterialTheme.typography.labelMedium
                        )
                        Spacer(Modifier.height(8.dp))
                        ExamImagesFromAssets(section.imageAssetPaths)
                    }
                }
            }
        }
    }
}

@Composable
private fun ChineseHubTab(vm: AppViewModel) {
    // 0=试卷结构 1=作文 2=中考真题；默认作文
    var sub by remember { mutableIntStateOf(1) }
    Column(Modifier.fillMaxSize()) {
        FlowRow(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("试卷结构" to 0, "作文" to 1, "中考真题" to 2).forEach { (label, idx) ->
                FilterChip(
                    selected = sub == idx,
                    onClick = { sub = idx },
                    label = { Text(label) }
                )
            }
        }
        Box(Modifier.weight(1f)) {
            when (sub) {
                0 -> ChineseStructureTab()
                1 -> EssayTab(vm, fixedSubject = "chinese")
                2 -> ChineseZhongkaoTab()
            }
        }
    }
}

@Composable
private fun MathHubTab() {
    var sub by remember { mutableIntStateOf(0) }
    Column(Modifier.fillMaxSize()) {
        FlowRow(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("试卷结构" to 0, "历年真题" to 1, "备考要点" to 2).forEach { (label, idx) ->
                FilterChip(
                    selected = sub == idx,
                    onClick = { sub = idx },
                    label = { Text(label) }
                )
            }
        }
        Box(Modifier.weight(1f)) {
            when (sub) {
                0 -> MathTab()
                1 -> MathZhongkaoTab()
                2 -> Box(
                    Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "更多数学内容敬请期待。",
                        color = AppColors.TextSecondary,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Composable
private fun PhysicsHubTab() {
    var sub by remember { mutableIntStateOf(0) }
    Column(Modifier.fillMaxSize()) {
        FlowRow(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("试卷结构" to 0, "备考要点" to 1).forEach { (label, idx) ->
                FilterChip(
                    selected = sub == idx,
                    onClick = { sub = idx },
                    label = { Text(label) }
                )
            }
        }
        Box(Modifier.weight(1f)) {
            when (sub) {
                0 -> PhysicsTab()
                1 -> Box(
                    Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "更多物理内容敬请期待。",
                        color = AppColors.TextSecondary,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Composable
private fun MathZhongkaoTab() {
    val context = LocalContext.current
    var file by remember { mutableStateOf<DaofaPastExamsFile?>(null) }
    var err by remember { mutableStateOf<String?>(null) }
    var detail by remember { mutableStateOf<DaofaPastExamItem?>(null) }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            try {
                file = MathZhongkaoRepository.load(context)
            } catch (e: Exception) {
                err = e.message ?: e.toString()
            }
        }
    }

    val items = file?.items ?: emptyList()
    when (val d = detail) {
        null -> {
            when {
                err != null && file == null -> Box(
                    Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "历年真题：$err",
                        color = AppColors.TextSecondary,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                file == null && err == null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
                items.isEmpty() -> Box(
                    Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "暂无历年真题数据。",
                        color = AppColors.TextSecondary,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                else -> LazyColumn(
                    Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Text(
                            file?.label?.takeIf { it.isNotBlank() }
                                ?: "按条目浏览，点击查看全文。",
                            color = AppColors.TextSecondary,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }
                    items(items, key = { it.id.ifEmpty { it.title } }) { item ->
                        AppSectionCard(elevated = true, modifier = Modifier.fillMaxWidth()) {
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .clickable { detail = item }
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    item.title,
                                    modifier = Modifier.weight(1f),
                                    fontWeight = FontWeight.Medium,
                                    color = AppColors.TextPrimary,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Text("›", color = AppColors.TextHint, fontSize = 20.sp)
                            }
                        }
                    }
                }
            }
        }
        else -> DaofaDetailPane(
            section = DaofaSection(
                d.title,
                d.body,
                d.images.orEmpty()
            ),
            barTitle = "历年真题",
            onBack = { detail = null }
        )
    }
}

@Composable
private fun ExamImagesFromAssets(
    paths: List<String>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        paths.forEach { rel ->
            key(rel) {
                SingleExamAssetImage(rel = rel, modifier = Modifier.fillMaxWidth())
            }
        }
    }
}

@Composable
private fun MathExamZoomDialog(bitmap: android.graphics.Bitmap, onDismiss: () -> Unit) {
    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            Modifier
                .fillMaxSize()
                .background(Color(0xEE000000))
        ) {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(12.dp)
            ) {
                Text("关闭", color = Color.White)
            }
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp, vertical = 48.dp)
                    .graphicsLayer(
                        scaleX = scale,
                        scaleY = scale,
                        translationX = offset.x,
                        translationY = offset.y
                    )
                    .pointerInput(Unit) {
                        detectTransformGestures { _, pan, zoom, _ ->
                            scale = (scale * zoom).coerceIn(1f, 5f)
                            offset += pan
                        }
                    },
                contentScale = ContentScale.Fit
            )
        }
    }
}

@Composable
private fun SingleExamAssetImage(rel: String, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var bitmap by remember(rel) { mutableStateOf<android.graphics.Bitmap?>(null) }
    LaunchedEffect(rel) {
        bitmap = withContext(Dispatchers.IO) {
            try {
                context.assets.open(rel).use { BitmapFactory.decodeStream(it) }
            } catch (_: Exception) {
                null
            }
        }
    }
    var zoomBitmap by remember(rel) { mutableStateOf<android.graphics.Bitmap?>(null) }
    val b = bitmap
    if (b != null) {
        Image(
            bitmap = b.asImageBitmap(),
            contentDescription = null,
            modifier = modifier
                .fillMaxWidth()
                .clickable { zoomBitmap = b },
            contentScale = ContentScale.FillWidth
        )
        Spacer(Modifier.height(12.dp))
    }
    zoomBitmap?.let { zb ->
        MathExamZoomDialog(bitmap = zb, onDismiss = { zoomBitmap = null })
    }
}

@Composable
private fun ChineseZhongkaoTab() {
    val context = LocalContext.current
    var file by remember { mutableStateOf<DaofaPastExamsFile?>(null) }
    var err by remember { mutableStateOf<String?>(null) }
    var detail by remember { mutableStateOf<DaofaPastExamItem?>(null) }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            try {
                file = ChineseZhongkaoRepository.load(context)
            } catch (e: Exception) {
                err = e.message ?: e.toString()
            }
        }
    }

    val items = file?.items ?: emptyList()
    when (val d = detail) {
        null -> {
            when {
                err != null && file == null -> Box(
                    Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "中考真题：$err",
                        color = AppColors.TextSecondary,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                file == null && err == null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
                items.isEmpty() -> Box(
                    Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "暂无中考真题数据。",
                        color = AppColors.TextSecondary,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                else -> LazyColumn(
                    Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Text(
                            file?.label?.takeIf { it.isNotBlank() }
                                ?: "按条目浏览，点击查看全文。",
                            color = AppColors.TextSecondary,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }
                    items(items, key = { it.id.ifEmpty { it.title } }) { item ->
                        AppSectionCard(elevated = true, modifier = Modifier.fillMaxWidth()) {
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .clickable { detail = item }
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    item.title,
                                    modifier = Modifier.weight(1f),
                                    fontWeight = FontWeight.Medium,
                                    color = AppColors.TextPrimary,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Text("›", color = AppColors.TextHint, fontSize = 20.sp)
                            }
                        }
                    }
                }
            }
        }
        else -> DaofaDetailPane(
            section = DaofaSection(d.title, d.body),
            barTitle = "中考真题",
            onBack = { detail = null }
        )
    }
}

@Composable
private fun EssayTab(vm: AppViewModel, fixedSubject: String? = null) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var detail by remember { mutableStateOf<Pair<EssayExam, EssaySample>?>(null) }
    var subjectKey by remember { mutableStateOf(fixedSubject ?: "english") }
    var subjectMenuExpanded by remember { mutableStateOf(false) }
    var ttsLoadingId by remember { mutableStateOf<String?>(null) }
    var ttsActiveId by remember { mutableStateOf<String?>(null) }
    var ttsPlaying by remember { mutableStateOf(false) }
    var ttsProgress by remember { mutableFloatStateOf(0f) }
    var ttsCurrentMs by remember { mutableIntStateOf(0) }
    var ttsDurationMs by remember { mutableIntStateOf(0) }
    var player by remember { mutableStateOf<MediaPlayer?>(null) }
    var audioFile by remember { mutableStateOf<File?>(null) }
    var ttsError by remember { mutableStateOf<String?>(null) }
    var essayPlaybackSpeed by remember { mutableFloatStateOf(1f) }

    val filteredExams = remember(subjectKey, vm.essayExams) {
        vm.essayExams.filter { it.subject == subjectKey }
    }

    fun stopCurrentPlayback(resetProgress: Boolean) {
        player?.setOnCompletionListener(null)
        player?.release()
        player = null
        audioFile?.delete()
        audioFile = null
        ttsPlaying = false
        if (resetProgress) {
            ttsActiveId = null
            ttsProgress = 0f
            ttsCurrentMs = 0
            ttsDurationMs = 0
        }
    }

    fun toggleEssayTts(sample: EssaySample) {
        if (ttsLoadingId != null) return
        val current = player
        if (ttsActiveId == sample.id && current != null) {
            if (current.isPlaying) {
                current.pause()
                ttsPlaying = false
            } else {
                current.start()
                ttsPlaying = true
            }
            return
        }
        stopCurrentPlayback(resetProgress = true)
        ttsError = null
        ttsLoadingId = sample.id
        scope.launch {
            try {
                val bytes = readBundledEssayAudio(context, sample.id)
                    ?: throw IllegalStateException("未找到离线音频：audio/essays/${sample.id}.wav，请先批量生成并打包资源。")
                val safeId = sample.id.replace(Regex("[^a-zA-Z0-9._-]"), "_").take(80)
                val f = File(context.cacheDir, "essay_tts_$safeId.wav")
                val mp = withContext(Dispatchers.IO) {
                    f.writeBytes(bytes)
                    prepareEssayTtsMediaPlayer(f)
                }
                mp.setOnErrorListener { _, what, extra ->
                    ttsError = "作文播放失败（错误码 $what/$extra）"
                    true
                }
                mp.setOnCompletionListener {
                    ttsPlaying = false
                    ttsCurrentMs = ttsDurationMs
                    ttsProgress = 1f
                }
                audioFile = f
                player = mp
                ttsActiveId = sample.id
                ttsDurationMs = mp.duration.coerceAtLeast(0)
                ttsCurrentMs = 0
                ttsProgress = 0f
                applyEssayPlaybackSpeed(mp, essayPlaybackSpeed)
                mp.start()
                ttsPlaying = true
            } catch (e: Exception) {
                ttsError = "作文离线音频播放失败：${e.message ?: e}"
                stopCurrentPlayback(resetProgress = true)
            } finally {
                ttsLoadingId = null
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose { stopCurrentPlayback(resetProgress = true) }
    }

    LaunchedEffect(ttsActiveId, ttsPlaying, player) {
        while (ttsActiveId != null && ttsPlaying && player != null) {
            val mp = player ?: break
            val d = mp.duration.coerceAtLeast(0)
            val c = mp.currentPosition.coerceAtLeast(0)
            ttsDurationMs = d
            ttsCurrentMs = c
            ttsProgress = if (d > 0) c.toFloat() / d.toFloat() else 0f
            delay(250)
        }
    }

    LaunchedEffect(essayPlaybackSpeed, player) {
        applyEssayPlaybackSpeed(player, essayPlaybackSpeed)
    }

    LaunchedEffect(subjectKey, fixedSubject) {
        if (fixedSubject != null) {
            subjectKey = fixedSubject
        }
        detail = null
    }

    if (!vm.essaysLoaded) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                Text("加载中…", color = AppColors.TextSecondary, style = MaterialTheme.typography.bodyMedium)
            }
        }
        return
    }

    vm.essayLoadError?.let { err ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(err, color = AppColors.TextSecondary, style = MaterialTheme.typography.bodyMedium)
        }
        return
    }

    if (vm.essayExams.isEmpty()) {
        Box(
            Modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Icon(Icons.Outlined.Description, null, Modifier.size(40.dp), tint = AppColors.TextHint)
                Text("暂无作文数据", fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.titleMedium)
                Text("请确认 assets 中已包含 essays.json。", color = AppColors.TextSecondary, style = MaterialTheme.typography.bodyMedium)
            }
        }
        return
    }

    val pair = detail
    if (pair != null) {
        val (exam, sample) = pair
        EssayDetailPane(
            ttsError = ttsError,
            examTitle = exam.title,
            topics = exam.topics,
            sampleTitle = sample.title,
            body = sample.body,
            ttsButtonText = when {
                ttsLoadingId == sample.id -> "生成中…"
                ttsActiveId == sample.id && ttsPlaying -> "暂停"
                ttsActiveId == sample.id -> "继续播放"
                else -> "播放"
            },
            ttsIsPlaying = ttsActiveId == sample.id && ttsPlaying,
            ttsEnabled = ttsLoadingId == null || ttsLoadingId == sample.id,
            ttsProgress = if (ttsActiveId == sample.id) ttsProgress else 0f,
            ttsCanSeek = ttsActiveId == sample.id && player != null,
            ttsTime = if (ttsActiveId == sample.id) {
                "${formatDurationMs(ttsCurrentMs)} / ${formatDurationMs(ttsDurationMs)}"
            } else {
                "0:00 / 0:00"
            },
            playbackSpeed = essayPlaybackSpeed,
            onPlaybackSpeedChange = { essayPlaybackSpeed = it },
            onToggleTts = { toggleEssayTts(sample) },
            onSeekToFraction = seek@{ fraction ->
                val mp = player ?: return@seek
                val d = mp.duration.coerceAtLeast(0)
                if (d <= 0) return@seek
                val pos = (fraction * d).toInt().coerceIn(0, d)
                mp.seekTo(pos)
                ttsCurrentMs = pos
                ttsProgress = pos.toFloat() / d.toFloat()
            },
            onBack = { detail = null }
        )
        return
    }

    LazyColumn(
        Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (fixedSubject == null) {
            item {
                Row(
                    Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        "科目",
                        style = MaterialTheme.typography.labelLarge,
                        color = AppColors.TextSecondary
                    )
                    Box {
                        OutlinedButton(
                            onClick = { subjectMenuExpanded = true },
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(if (subjectKey == "chinese") "语文" else "英语")
                            Spacer(Modifier.size(4.dp))
                            Icon(Icons.Filled.ArrowDropDown, contentDescription = null, Modifier.size(22.dp))
                        }
                        DropdownMenu(
                            expanded = subjectMenuExpanded,
                            onDismissRequest = { subjectMenuExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("英语") },
                                onClick = {
                                    subjectKey = "english"
                                    subjectMenuExpanded = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("语文") },
                                onClick = {
                                    subjectKey = "chinese"
                                    subjectMenuExpanded = false
                                }
                            )
                        }
                    }
                }
            }
        }
        item {
            Text(
                if (fixedSubject == "chinese") {
                    "中考语文范文与题目，点击条目查看全文。"
                } else if (fixedSubject == "english") {
                    "英语中考真题与范文，点击条目查看全文。"
                } else {
                    "中考真题与范文，点击条目查看全文。"
                },
                color = AppColors.TextSecondary,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 4.dp)
            )
        }
        if (filteredExams.isEmpty()) {
            item {
                Text(
                    "该科目暂无范文，请切换科目或稍后再试。",
                    color = AppColors.TextSecondary,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
        filteredExams.forEach { exam ->
            item(key = "h-${exam.id}") {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        "📚 ${exam.title}",
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        fontWeight = FontWeight.SemiBold,
                        color = AppColors.TextPrimary,
                        style = MaterialTheme.typography.titleSmall
                    )
                }
            }
            items(exam.samples, key = { it.id }) { sample ->
                AppSectionCard(elevated = true, modifier = Modifier.fillMaxWidth()) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .clickable { detail = exam to sample }
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            sample.title,
                            modifier = Modifier.weight(1f),
                            fontWeight = FontWeight.Medium,
                            color = AppColors.TextPrimary,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text("›", color = AppColors.TextHint, fontSize = 20.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun EssayDetailPane(
    ttsError: String?,
    examTitle: String,
    topics: String,
    sampleTitle: String,
    body: String,
    ttsButtonText: String,
    ttsIsPlaying: Boolean,
    ttsEnabled: Boolean,
    ttsProgress: Float,
    ttsCanSeek: Boolean,
    ttsTime: String,
    playbackSpeed: Float,
    onPlaybackSpeedChange: (Float) -> Unit,
    onToggleTts: () -> Unit,
    onSeekToFraction: (Float) -> Unit,
    onBack: () -> Unit
) {
    var speedMenuExpanded by remember { mutableStateOf(false) }
    var scrubbing by remember { mutableStateOf(false) }
    var scrubValue by remember { mutableFloatStateOf(0f) }
    Column(Modifier.fillMaxSize()) {
        ttsError?.let { ErrorBanner(it) }
        Row(
            Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 4.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "返回")
            }
            Column(Modifier.weight(1f)) {
                Text("范文详情", fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.titleMedium)
                Text(examTitle, style = MaterialTheme.typography.labelSmall, color = AppColors.TextSecondary)
            }
        }
        HorizontalDivider()
        Column(
            Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(sampleTitle, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium, color = AppColors.TextPrimary)
            AppSectionCard(elevated = false) {
                Column(Modifier.padding(14.dp)) {
                    Text("本年题目", fontWeight = FontWeight.SemiBold, color = AppColors.PrimaryBlue, style = MaterialTheme.typography.labelLarge)
                    Spacer(Modifier.height(8.dp))
                    Text(topics, style = MaterialTheme.typography.bodyMedium, color = AppColors.TextPrimary, lineHeight = 22.sp)
                }
            }
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                FilledTonalButton(
                    onClick = onToggleTts,
                    enabled = ttsEnabled,
                    shape = RoundedCornerShape(24.dp),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Icon(
                        imageVector = if (ttsIsPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        ttsButtonText,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Slider(
                    value = if (scrubbing) scrubValue else ttsProgress,
                    onValueChange = {
                        if (ttsCanSeek) {
                            scrubbing = true
                            scrubValue = it
                        }
                    },
                    onValueChangeFinished = {
                        if (scrubbing && ttsCanSeek) {
                            onSeekToFraction(scrubValue)
                        }
                        scrubbing = false
                    },
                    enabled = ttsCanSeek,
                    valueRange = 0f..1f,
                    modifier = Modifier.weight(1f),
                    colors = SliderDefaults.colors(
                        thumbColor = MaterialTheme.colorScheme.primary,
                        activeTrackColor = MaterialTheme.colorScheme.primary,
                        inactiveTrackColor = MaterialTheme.colorScheme.surfaceContainerHighest
                    )
                )
                Text(
                    ttsTime,
                    style = MaterialTheme.typography.labelSmall,
                    color = AppColors.TextSecondary,
                    modifier = Modifier.width(92.dp)
                )
                Box {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceContainerHigh,
                        modifier = Modifier.clickable { speedMenuExpanded = true }
                    ) {
                        Row(
                            Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                formatEssaySpeedLabel(playbackSpeed),
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Icon(Icons.Filled.ArrowDropDown, contentDescription = "倍速", Modifier.size(22.dp))
                        }
                    }
                    DropdownMenu(
                        expanded = speedMenuExpanded,
                        onDismissRequest = { speedMenuExpanded = false }
                    ) {
                        essayPlaybackSpeedOptions.forEach { opt ->
                            DropdownMenuItem(
                                text = { Text(formatEssaySpeedLabel(opt)) },
                                onClick = {
                                    onPlaybackSpeedChange(opt)
                                    speedMenuExpanded = false
                                }
                            )
                        }
                    }
                }
            }
            AppSectionCard(elevated = true) {
                Column(Modifier.padding(14.dp)) {
                    Text("正文", fontWeight = FontWeight.SemiBold, color = AppColors.TextSecondary, style = MaterialTheme.typography.labelMedium)
                    Spacer(Modifier.height(8.dp))
                    Text(body.trim(), style = MaterialTheme.typography.bodyLarge, color = AppColors.TextPrimary, lineHeight = 24.sp)
                }
            }
        }
    }
}

private fun formatDurationMs(ms: Int): String {
    val total = (ms / 1000).coerceAtLeast(0)
    val m = total / 60
    val s = total % 60
    return "$m:${s.toString().padStart(2, '0')}"
}

@Composable
private fun ProfileTab(vm: AppViewModel, onWordClick: (String) -> Unit) {
    var confirmClear by remember { mutableStateOf(false) }
    var showPeSports by remember { mutableStateOf(false) }
    val font = fontScaleSp(vm.fontScaleKey)
    val nbTotalAdds = vm.notebookEntries.sumOf { it.second }
    if (showPeSports) {
        Column(Modifier.fillMaxSize()) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { showPeSports = false }) {
                    Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "返回")
                }
                Text("中考体育", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold, color = AppColors.TextPrimary)
            }
            HorizontalDivider(color = AppColors.Divider.copy(alpha = 0.5f))
            Box(Modifier.weight(1f)) {
                PeSportsStructureTab()
            }
        }
        return
    }
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        AppSectionCard(elevated = true) {
            Column(Modifier.padding(16.dp)) {
                SectionHeader(
                    Icons.Outlined.EmojiEvents,
                    "中考体育",
                    "2025 年北京中考体育考试内容与说明"
                )
                Spacer(Modifier.height(12.dp))
                PrimaryFullWidthButton(
                    text = "查看体育考试说明",
                    enabled = true,
                    loading = false,
                    onClick = { showPeSports = true }
                )
            }
        }
        AppSectionCard(elevated = true) {
            Column(Modifier.padding(16.dp)) {
                SectionHeader(
                    Icons.Outlined.Book,
                    "生词本",
                    "${vm.notebookEntries.size} 个词 · 累计 $nbTotalAdds 次 · 数据保存在本机"
                )
                Spacer(Modifier.height(12.dp))
                if (vm.notebookEntries.isEmpty()) {
                    Text("生词本为空", color = AppColors.TextSecondary, style = MaterialTheme.typography.bodyMedium)
                } else {
                    vm.notebookEntries.forEach { (w, count) ->
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .clickable { onWordClick(w) }
                                .padding(vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(w, fontSize = font, fontWeight = FontWeight.Medium, color = AppColors.TextPrimary)
                                Text(
                                    "${count}次",
                                    fontSize = (font.value * 0.9f).sp,
                                    color = AppColors.TextSecondary,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            TextButton(onClick = { vm.removeNotebook(w) }) { Text("删除", color = AppColors.UnknownWord) }
                        }
                        HorizontalDivider(color = AppColors.Divider.copy(alpha = 0.5f))
                    }
                }
                Spacer(Modifier.height(8.dp))
                OutlinedButton(
                    onClick = { confirmClear = true },
                    enabled = vm.notebookEntries.isNotEmpty(),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("清空生词本", color = AppColors.UnknownWord)
                }
            }
        }
    }
    if (confirmClear) {
        AlertDialog(
            onDismissRequest = { confirmClear = false },
            title = { Text("确定清空全部生词？") },
            confirmButton = {
                TextButton(onClick = {
                    vm.clearNotebook()
                    confirmClear = false
                }) { Text("清空") }
            },
            dismissButton = { TextButton(onClick = { confirmClear = false }) { Text("取消") } }
        )
    }
}

@Composable
private fun WordDetailSheetContent(word: String, source: WordSheetSource, vm: AppViewModel, onDismiss: () -> Unit) {
    val context = LocalContext.current
    var tts by remember { mutableStateOf<TextToSpeech?>(null) }
    var ttsReady by remember { mutableStateOf(false) }
    DisposableEffect(context) {
        val appContext = context.applicationContext
        lateinit var engine: TextToSpeech
        engine = TextToSpeech(appContext) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val lr = engine.setLanguage(Locale.US)
                if (lr == TextToSpeech.LANG_MISSING_DATA || lr == TextToSpeech.LANG_NOT_SUPPORTED) {
                    engine.setLanguage(Locale.ENGLISH)
                }
                ttsReady = true
            }
        }
        tts = engine
        onDispose {
            ttsReady = false
            engine.stop()
            engine.shutdown()
            tts = null
        }
    }
    fun speakWord() {
        val engine = tts ?: return
        if (!ttsReady) return
        val w = word.lowercase()
        val uttId = "detail_${w}_${System.nanoTime()}"
        engine.speak(w, TextToSpeech.QUEUE_FLUSH, null, uttId)
    }

    val rec = vm.resolvedWordRecord(word)
    var exampleEn by remember { mutableStateOf("") }
    var exampleZh by remember { mutableStateOf("") }
    var loadingEx by remember { mutableStateOf(false) }

    val sourceLabel = when (source) {
        WordSheetSource.VOCABULARY -> "词库"
        WordSheetSource.SCAN -> "阅读"
        WordSheetSource.NOTEBOOK -> "生词本"
    }

    LaunchedEffect(word) {
        loadingEx = true
        try {
            val w = word.lowercase()
            val ex = rec?.example?.trim().orEmpty()
            if (ex.isNotEmpty()) {
                exampleEn = ex
                exampleZh = rec?.exampleZh?.trim().orEmpty()
                if (exampleZh.isEmpty()) {
                    exampleZh = vm.dictionaryRepository.translateToZh(ex).orEmpty()
                }
            } else {
                exampleEn = vm.dictionaryRepository.fetchExample(w).orEmpty()
                if (exampleEn.isEmpty()) {
                    exampleEn = "暂无例句（可检查网络后重试）"
                } else {
                    exampleZh = vm.dictionaryRepository.translateToZh(exampleEn).orEmpty()
                }
            }
        } finally {
            loadingEx = false
        }
    }

    val font = fontScaleSp(vm.fontScaleKey)

    Column(
        Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 8.dp)
    ) {
        Box(Modifier.fillMaxWidth()) {
            Text(
                "单词详情",
                modifier = Modifier.align(Alignment.Center),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = AppColors.TextSecondary
            )
            IconButton(onClick = onDismiss, modifier = Modifier.align(Alignment.CenterEnd)) {
                Icon(Icons.Default.Close, contentDescription = "关闭", tint = AppColors.TextSecondary)
            }
        }

        AppSectionCard(elevated = true) {
            Column(Modifier.padding(20.dp)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        word.lowercase(),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.TextPrimary,
                        fontSize = (kotlin.math.min(36f, font.value + 20f)).sp,
                        modifier = Modifier.weight(1f)
                    )
                    Surface(shape = RoundedCornerShape(50), color = MaterialTheme.colorScheme.surfaceContainerHigh) {
                        Text(sourceLabel, Modifier.padding(horizontal = 10.dp, vertical = 5.dp), style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold, color = AppColors.TextSecondary)
                    }
                }
                Spacer(Modifier.height(12.dp))
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val ph = rec?.phonetic?.trim().orEmpty()
                    Row(
                        Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Outlined.TextFormat, contentDescription = null, modifier = Modifier.size(18.dp), tint = AppColors.TextHint)
                        Column(Modifier.weight(1f)) {
                            Text("音标", style = MaterialTheme.typography.labelSmall, color = AppColors.TextHint)
                            Text(
                                if (ph.isNotEmpty()) ph else "—",
                                style = MaterialTheme.typography.bodyLarge,
                                color = if (ph.isNotEmpty()) AppColors.TextSecondary else AppColors.TextHint
                            )
                        }
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("读音", style = MaterialTheme.typography.labelSmall, color = AppColors.TextSecondary)
                        IconButton(onClick = { speakWord() }, enabled = ttsReady) {
                            Icon(
                                Icons.AutoMirrored.Outlined.VolumeUp,
                                contentDescription = "朗读单词",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
                rec?.type?.let { t ->
                    val badge = if (t == "ADVANCED") "超纲" else "中考"
                    Spacer(Modifier.height(10.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(AppColors.OrangeChipBg)
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Outlined.Tag, null, Modifier.size(14.dp), tint = AppColors.AccentOrange)
                        Text(badge, style = MaterialTheme.typography.labelMedium, color = AppColors.AccentOrange, fontWeight = FontWeight.Medium)
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        AppSectionCard(elevated = false) {
            Column(Modifier.padding(18.dp)) {
                SectionHeader(Icons.AutoMirrored.Outlined.MenuBook, "释义", null)
                Spacer(Modifier.height(12.dp))
                if (rec != null && rec.definitions.isNotEmpty()) {
                    rec.definitions.forEachIndexed { idx, d ->
                        if (idx > 0) Spacer(Modifier.height(10.dp))
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            if (d.partOfSpeech.isNotBlank()) {
                                Surface(shape = RoundedCornerShape(6.dp), color = AppColors.OrangeChipBg) {
                                    Text(
                                        d.partOfSpeech.trim(),
                                        Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = AppColors.AccentOrange
                                    )
                                }
                            }
                            Text(d.meaning, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
                        }
                    }
                } else {
                    Text("当前内置词库中暂无该词详细释义，可加入生词本以便复习。", color = AppColors.TextSecondary, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        AppSectionCard(elevated = false) {
            Column(Modifier.padding(18.dp)) {
                SectionHeader(Icons.Outlined.Description, "例句", null)
                Spacer(Modifier.height(10.dp))
                if (loadingEx) {
                    CircularProgressIndicator(Modifier.padding(8.dp))
                } else {
                    Surface(shape = RoundedCornerShape(12.dp), color = MaterialTheme.colorScheme.surfaceContainerHigh) {
                        Column(Modifier.padding(14.dp)) {
                            Text(exampleEn, fontStyle = FontStyle.Italic, style = MaterialTheme.typography.bodyLarge, fontSize = (font.value + 1).sp)
                            if (exampleZh.isNotEmpty()) {
                                Spacer(Modifier.height(8.dp))
                                Text(exampleZh, color = AppColors.TextSecondary, style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        val nbCount = vm.notebookRepository.countFor(word)
        Button(
            onClick = { vm.addNotebook(word) },
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Icon(Icons.Filled.Add, null, Modifier.size(22.dp), tint = androidx.compose.ui.graphics.Color.White)
            Spacer(Modifier.size(8.dp))
            Text(
                if (nbCount == 0) "加入生词本" else "已记 ${nbCount} 次 · 再记一次",
                fontWeight = FontWeight.SemiBold,
                color = androidx.compose.ui.graphics.Color.White
            )
        }
        Spacer(Modifier.height(28.dp))
    }
}
