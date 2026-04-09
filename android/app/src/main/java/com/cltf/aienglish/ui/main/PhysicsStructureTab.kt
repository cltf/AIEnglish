package com.cltf.aienglish.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cltf.aienglish.data.PhysicsBlock
import com.cltf.aienglish.data.PhysicsSection
import com.cltf.aienglish.data.PhysicsStructureFile
import com.cltf.aienglish.ui.theme.AppColors
import com.google.gson.Gson
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.compose.ui.platform.LocalContext

@Composable
fun PhysicsTab() {
    val context = LocalContext.current
    var doc by remember { mutableStateOf<PhysicsStructureFile?>(null) }
    var loadErr by remember { mutableStateOf<String?>(null) }
    val gson = remember { Gson() }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            try {
                context.assets.open("physics_beijing_structure.json").use { ins ->
                    doc = gson.fromJson(InputStreamReader(ins, StandardCharsets.UTF_8), PhysicsStructureFile::class.java)
                }
            } catch (e: Exception) {
                loadErr = e.message ?: e.toString()
            }
        }
    }

    when {
        doc == null && loadErr == null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }
        loadErr != null && doc == null -> Box(
            Modifier.fillMaxSize().padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "物理内容加载失败：$loadErr",
                color = AppColors.TextSecondary,
                style = MaterialTheme.typography.bodyMedium
            )
        }
        doc != null -> PhysicsStructureContent(doc!!)
    }
}

@Composable
fun MathTab() {
    val context = LocalContext.current
    var doc by remember { mutableStateOf<PhysicsStructureFile?>(null) }
    var loadErr by remember { mutableStateOf<String?>(null) }
    val gson = remember { Gson() }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            try {
                context.assets.open("math_beijing_structure.json").use { ins ->
                    doc = gson.fromJson(InputStreamReader(ins, StandardCharsets.UTF_8), PhysicsStructureFile::class.java)
                }
            } catch (e: Exception) {
                loadErr = e.message ?: e.toString()
            }
        }
    }

    when {
        doc == null && loadErr == null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }
        loadErr != null && doc == null -> Box(
            Modifier.fillMaxSize().padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "数学内容加载失败：$loadErr",
                color = AppColors.TextSecondary,
                style = MaterialTheme.typography.bodyMedium
            )
        }
        doc != null -> PhysicsStructureContent(doc!!)
    }
}

/** 北京中考语文试卷结构（chinese_beijing_structure.json） */
@Composable
fun ChineseStructureTab() {
    val context = LocalContext.current
    var doc by remember { mutableStateOf<PhysicsStructureFile?>(null) }
    var loadErr by remember { mutableStateOf<String?>(null) }
    val gson = remember { Gson() }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            try {
                context.assets.open("chinese_beijing_structure.json").use { ins ->
                    doc = gson.fromJson(InputStreamReader(ins, StandardCharsets.UTF_8), PhysicsStructureFile::class.java)
                }
            } catch (e: Exception) {
                loadErr = e.message ?: e.toString()
            }
        }
    }

    when {
        doc == null && loadErr == null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }
        loadErr != null && doc == null -> Box(
            Modifier.fillMaxSize().padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "语文试卷结构加载失败：$loadErr",
                color = AppColors.TextSecondary,
                style = MaterialTheme.typography.bodyMedium
            )
        }
        doc != null -> PhysicsStructureContent(doc!!)
    }
}

/** 北京中考体育考试说明（pe_beijing_structure.json） */
@Composable
fun PeSportsStructureTab() {
    val context = LocalContext.current
    var doc by remember { mutableStateOf<PhysicsStructureFile?>(null) }
    var loadErr by remember { mutableStateOf<String?>(null) }
    val gson = remember { Gson() }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            try {
                context.assets.open("pe_beijing_structure.json").use { ins ->
                    doc = gson.fromJson(InputStreamReader(ins, StandardCharsets.UTF_8), PhysicsStructureFile::class.java)
                }
            } catch (e: Exception) {
                loadErr = e.message ?: e.toString()
            }
        }
    }

    when {
        doc == null && loadErr == null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }
        loadErr != null && doc == null -> Box(
            Modifier.fillMaxSize().padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "体育考试说明加载失败：$loadErr",
                color = AppColors.TextSecondary,
                style = MaterialTheme.typography.bodyMedium
            )
        }
        doc != null -> PhysicsStructureContent(doc!!)
    }
}

/** 北京中考英语试卷结构（english_beijing_structure.json） */
@Composable
fun EnglishStructureTab() {
    val context = LocalContext.current
    var doc by remember { mutableStateOf<PhysicsStructureFile?>(null) }
    var loadErr by remember { mutableStateOf<String?>(null) }
    val gson = remember { Gson() }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            try {
                context.assets.open("english_beijing_structure.json").use { ins ->
                    doc = gson.fromJson(InputStreamReader(ins, StandardCharsets.UTF_8), PhysicsStructureFile::class.java)
                }
            } catch (e: Exception) {
                loadErr = e.message ?: e.toString()
            }
        }
    }

    when {
        doc == null && loadErr == null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }
        loadErr != null && doc == null -> Box(
            Modifier.fillMaxSize().padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "英语试卷结构加载失败：$loadErr",
                color = AppColors.TextSecondary,
                style = MaterialTheme.typography.bodyMedium
            )
        }
        doc != null -> PhysicsStructureContent(doc!!)
    }
}

/** 北京中考道法试卷结构（daofa_beijing_structure.json） */
@Composable
fun DaofaStructureTab() {
    val context = LocalContext.current
    var doc by remember { mutableStateOf<PhysicsStructureFile?>(null) }
    var loadErr by remember { mutableStateOf<String?>(null) }
    val gson = remember { Gson() }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            try {
                context.assets.open("daofa_beijing_structure.json").use { ins ->
                    doc = gson.fromJson(InputStreamReader(ins, StandardCharsets.UTF_8), PhysicsStructureFile::class.java)
                }
            } catch (e: Exception) {
                loadErr = e.message ?: e.toString()
            }
        }
    }

    when {
        doc == null && loadErr == null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }
        loadErr != null && doc == null -> Box(
            Modifier.fillMaxSize().padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "道法试卷结构加载失败：$loadErr",
                color = AppColors.TextSecondary,
                style = MaterialTheme.typography.bodyMedium
            )
        }
        doc != null -> PhysicsStructureContent(doc!!)
    }
}

@Composable
private fun PhysicsStructureContent(file: PhysicsStructureFile) {
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Text(
            file.title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = AppColors.TextPrimary
        )
        file.subtitle?.takeIf { it.isNotBlank() }?.let {
            Spacer(Modifier.height(6.dp))
            Text(it, style = MaterialTheme.typography.titleMedium, color = AppColors.TextSecondary, fontWeight = FontWeight.Medium)
        }
        file.badge?.takeIf { it.isNotBlank() }?.let { b ->
            Spacer(Modifier.height(10.dp))
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f)
            ) {
                Text(
                    b,
                    Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    lineHeight = 20.sp
                )
            }
        }
        Spacer(Modifier.height(16.dp))
        file.sections.forEachIndexed { idx, sec ->
            PhysicsSectionCard(sec)
            if (idx < file.sections.lastIndex) Spacer(Modifier.height(14.dp))
        }
        Spacer(Modifier.height(28.dp))
    }
}

@Composable
private fun PhysicsSectionCard(section: PhysicsSection) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp,
        shadowElevation = 2.dp
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(
                section.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(12.dp))
            section.blocks.forEachIndexed { i, block ->
                PhysicsBlockView(block)
                if (i < section.blocks.lastIndex) Spacer(Modifier.height(10.dp))
            }
        }
    }
}

@Composable
private fun PhysicsBlockView(block: PhysicsBlock) {
    when (block.type) {
        "table" -> PhysicsTable(
            headers = block.headers ?: emptyList(),
            rows = block.rows ?: emptyList()
        )
        "subheading" -> Text(
            block.text ?: "",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            color = AppColors.TextPrimary,
            modifier = Modifier.padding(top = 4.dp)
        )
        "label" -> Text(
            block.text ?: "",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
            color = AppColors.AccentOrange
        )
        "bullets" -> Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            block.items?.forEach { line ->
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("•", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    Text(line, style = MaterialTheme.typography.bodyMedium, color = AppColors.TextSecondary, lineHeight = 22.sp, modifier = Modifier.weight(1f))
                }
            }
        }
        "keyValues" -> Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            block.pairs?.forEach { pair ->
                if (pair.size >= 2) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            pair[0],
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = AppColors.TextHint,
                            modifier = Modifier.widthIn(min = 72.dp, max = 120.dp)
                        )
                        Text(pair[1], style = MaterialTheme.typography.bodyMedium, color = AppColors.TextPrimary, lineHeight = 22.sp, modifier = Modifier.weight(1f))
                    }
                }
            }
        }
        "subsection" -> Text(
            block.title ?: "",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.padding(top = 6.dp)
        )
        "callout" -> Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.35f)
        ) {
            Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                block.items?.forEach { line ->
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("◆", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.tertiary, modifier = Modifier.padding(top = 2.dp))
                        Text(line, style = MaterialTheme.typography.bodyMedium, color = AppColors.TextPrimary, lineHeight = 22.sp, modifier = Modifier.weight(1f))
                    }
                }
            }
        }
        else -> block.text?.takeIf { it.isNotBlank() }?.let {
            Text(it, style = MaterialTheme.typography.bodyMedium, color = AppColors.TextSecondary)
        }
    }
}

@Composable
private fun PhysicsTable(headers: List<String>, rows: List<List<String>>) {
    if (headers.isEmpty() || rows.isEmpty()) return
    val colCount = headers.size
    Column(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, AppColors.Divider.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
    ) {
        Row(Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f))) {
            headers.forEach { h ->
                Text(
                    h,
                    Modifier.weight(1f).padding(10.dp),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = AppColors.TextPrimary
                )
            }
        }
        HorizontalDivider(color = AppColors.Divider.copy(alpha = 0.5f))
        rows.forEachIndexed { ri, row ->
            Row(Modifier.fillMaxWidth().background(if (ri % 2 == 0) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surfaceContainerLow.copy(alpha = 0.5f))) {
                for (i in 0 until colCount) {
                    Text(
                        row.getOrElse(i) { "" },
                        Modifier.weight(1f).padding(10.dp),
                        style = MaterialTheme.typography.bodySmall,
                        color = AppColors.TextSecondary,
                        lineHeight = 18.sp
                    )
                }
            }
            if (ri < rows.lastIndex) HorizontalDivider(color = AppColors.Divider.copy(alpha = 0.35f), thickness = 0.5.dp)
        }
    }
}
