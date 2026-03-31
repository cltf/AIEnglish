@file:OptIn(ExperimentalLayoutApi::class)

package com.cltf.aienglish.ui.main

import android.graphics.BitmapFactory
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.outlined.NetworkCheck
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.PhotoLibrary
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Tag
import androidx.compose.material.icons.outlined.TextFields
import androidx.compose.material.icons.outlined.TextFormat
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.cltf.aienglish.AppViewModel
import com.cltf.aienglish.data.WordRecord
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
import java.io.File

enum class WordSheetSource { VOCABULARY, SCAN, NOTEBOOK }

data class WordSheetRequest(val word: String, val source: WordSheetSource)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(vm: AppViewModel) {
    var tab by remember { mutableIntStateOf(0) }
    var sheetRequest by remember { mutableStateOf<WordSheetRequest?>(null) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)

    val topTitles = listOf("中考词汇", "扫描识图", "我的")
    val bottomLabels = listOf("词库", "扫描", "我的")
    val tabIcons = listOf(
        Icons.AutoMirrored.Outlined.MenuBook,
        Icons.Outlined.Search,
        Icons.Outlined.Person
    )

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(topTitles[tab], fontWeight = FontWeight.SemiBold) },
                colors = androidx.compose.material3.TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = AppColors.TextPrimary
                )
            )
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
                0 -> VocabularyTab(vm) { sheetRequest = WordSheetRequest(it, WordSheetSource.VOCABULARY) }
                1 -> ScanTab(vm) { sheetRequest = WordSheetRequest(it, WordSheetSource.SCAN) }
                2 -> ProfileTab(vm) { sheetRequest = WordSheetRequest(it, WordSheetSource.NOTEBOOK) }
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
                    Column(
                        Modifier
                            .fillMaxWidth()
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
private fun ProfileTab(vm: AppViewModel, onWordClick: (String) -> Unit) {
    var confirmClear by remember { mutableStateOf(false) }
    val font = fontScaleSp(vm.fontScaleKey)
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        AppSectionCard(elevated = false) {
            Column(Modifier.padding(16.dp)) {
                SectionHeader(Icons.Outlined.TextFields, "显示", "全局字号")
                Spacer(Modifier.height(12.dp))
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("small" to "小", "standard" to "标准", "large" to "大", "xlarge" to "特大").forEach { (k, label) ->
                        FilterChip(
                            selected = vm.fontScaleKey == k,
                            onClick = { vm.setFontScale(k) },
                            label = { Text(label) }
                        )
                    }
                }
            }
        }
        AppSectionCard(elevated = true) {
            Column(Modifier.padding(16.dp)) {
                SectionHeader(Icons.Outlined.Book, "生词本", "${vm.notebookWords.size} 个词 · 数据保存在本机")
                Spacer(Modifier.height(12.dp))
                if (vm.notebookWords.isEmpty()) {
                    Text("生词本为空", color = AppColors.TextSecondary, style = MaterialTheme.typography.bodyMedium)
                } else {
                    vm.notebookWords.forEach { w ->
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .clickable { onWordClick(w) }
                                .padding(vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(w, fontSize = font, fontWeight = FontWeight.Medium, color = AppColors.TextPrimary)
                            TextButton(onClick = { vm.removeNotebook(w) }) { Text("删除", color = AppColors.UnknownWord) }
                        }
                        HorizontalDivider(color = AppColors.Divider.copy(alpha = 0.5f))
                    }
                }
                Spacer(Modifier.height(8.dp))
                OutlinedButton(
                    onClick = { confirmClear = true },
                    enabled = vm.notebookWords.isNotEmpty(),
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
    val rec = vm.vocabularyRepository.recordFor(word)
    var exampleEn by remember { mutableStateOf("") }
    var exampleZh by remember { mutableStateOf("") }
    var loadingEx by remember { mutableStateOf(false) }

    val sourceLabel = when (source) {
        WordSheetSource.VOCABULARY -> "词库"
        WordSheetSource.SCAN -> "扫描"
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
                rec?.phonetic?.trim()?.takeIf { it.isNotEmpty() }?.let { ph ->
                    Spacer(Modifier.height(12.dp))
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Outlined.TextFormat, null, Modifier.size(18.dp), tint = AppColors.TextHint)
                        Text(ph, style = MaterialTheme.typography.bodyLarge, color = AppColors.TextSecondary)
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

        val saved = vm.notebookRepository.contains(word)
        Button(
            onClick = { vm.addNotebook(word) },
            enabled = !saved,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(14.dp),
            colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                containerColor = if (saved) MaterialTheme.colorScheme.surfaceContainerHigh else MaterialTheme.colorScheme.primary,
                disabledContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                contentColor = if (saved) AppColors.TextSecondary else androidx.compose.ui.graphics.Color.White,
                disabledContentColor = AppColors.TextSecondary
            )
        ) {
            Icon(if (saved) Icons.Filled.CheckCircle else Icons.Filled.Add, null, Modifier.size(22.dp))
            Spacer(Modifier.size(8.dp))
            Text(if (saved) "已在生词本" else "加入生词本", fontWeight = FontWeight.SemiBold)
        }
        Spacer(Modifier.height(28.dp))
    }
}
