import {
  initVocabulary,
  initSupplementalWordData,
  getVocabularySet,
  getResolvedWordRecord,
  resolveInVocabulary,
  listWordsForHome,
  getWordCount,
} from "./vocabulary.js";

const STORAGE_NOTEBOOK = "aienglish_notebook";
const STORAGE_ESSAY_SUBJECT = "aienglish_essay_subject";
const STORAGE_ENGLISH_SUB = "aienglish_english_sub";
const STORAGE_DAOF_SUB = "aienglish_daofa_sub";
const STORAGE_CHINESE_SUB = "aienglish_chinese_sub";
const STORAGE_MATH_SUB = "aienglish_math_sub";
const STORAGE_READING_SUBJECT = "aienglish_reading_subject";
const STORAGE_FONT = "aienglish_font";
const STORAGE_AI_MODEL = "aienglish_ai_model";
const STORAGE_AI_OCR_MODEL = "aienglish_ai_ocr_model";

/** 本地 Go 代理（见 server/），避免浏览器直连 xiwang CORS；令牌仅在服务端配置 */
const EMBEDDED_AI_BASE = "http://127.0.0.1:8787/openai-compatible/v1";

const DEFAULT_AI_MODEL = "gemini-3-pro";
const DEFAULT_AI_OCR_MODEL = "gemini-2.5-flash-image";
const DEFAULT_ESSAY_TTS_VOICE = "Kore";

const VOCAB_PAGE_SIZE = 20;

/** @type {string} */
let filterType = "ALL";
/** @type {number} */
let vocabPage = 1;
/** @type {string} */
let recognizedText = "";
/** @type {string[]} */
let unknownWordsList = [];
/** @type {MediaStream | null} */
let cameraStream = null;
/** @type {Map<string, {audio?: HTMLAudioElement, url?: string, loading?: boolean, duration?: number, current?: number, playing?: boolean}>} */
const essayTtsState = new Map();

/**
 * @typedef {{
 *   mode: "heuristic" | "ai";
 *   paragraphGists: { index: number; gistZh: string; keySentenceEn: string }[];
 *   coreViewpointZh: string;
 *   examPoints: string[];
 *   logicRelations: string[];
 *   howToSolveZh: string;
 * }} ReadingAnalysis
 */

/** @type {ReadingAnalysis | null} */
let lastReadingAnalysis = null;

const STATIC_HOW_TO_SOLVE = `1. 先读题干与选项，圈出题干关键词（人名、地名、数字、大写、段落号）。
2. 细节题用关键词回原文定位，同义改写多为正确选项。
3. 主旨/标题题重点读首段、尾段与各段首句，排除片面细节项。
4. 词义猜测题看该词前后句的同义复述、举例或对比线索。
5. 推断题只选「能从原文合理推出」的选项，忌主观臆断与过度引申。
6. 注意路标词：转折、因果、递进、举例，常对应考点与正确项。`;

const LOGIC_MARKERS = [
  { re: /\b(however|but|yet|instead|while)\b/gi, zh: "转折对比" },
  { re: /\b(because|since|as|so that)\b/gi, zh: "原因目的" },
  { re: /\b(therefore|thus|so|as a result|consequently)\b/gi, zh: "结果结论" },
  { re: /\b(although|though|even if)\b/gi, zh: "让步" },
  { re: /\b(for example|for instance|such as)\b/gi, zh: "举例说明" },
  { re: /\b(first|second|finally|in conclusion|in short)\b/gi, zh: "顺序与总结" },
  { re: /\b(if|unless|when)\b/gi, zh: "条件时间" },
  { re: /\b(and|also|besides)\b/gi, zh: "并列递进" },
];

const EXAM_HINT_RULES = [
  {
    test: (t) => /\b(according to the passage|the passage says|paragraph\s*\d)\b/i.test(t),
    msg: "信息定位题：用题干关键词回原文找依据，排除未提及或偷换概念的选项。",
  },
  {
    test: (t) => /\b(main(ly)?\s+idea|best title|purpose of|mainly about)\b/i.test(t),
    msg: "主旨/标题类：串联各段中心句，勿把某一例子或细节当作全文中心。",
  },
  {
    test: (t) => /\b(infer|imply|suggest|it can be learned)\b/i.test(t),
    msg: "推断题：选项必须是原文可合理推出的结论，忌绝对化与无中生有。",
  },
  {
    test: (t) => /\b(the word|refers to|closest in meaning)\b/i.test(t),
    msg: "词义/指代题：看前后句同义复述、举例或反义对比；指代向前找最近名词。",
  },
  {
    test: (t) => /\b(order|sequence|correct order)\b/i.test(t),
    msg: "排序题：抓住时间词与 first/then/finally 等路标，先定位再排顺序。",
  },
];

function splitIntoParagraphs(text) {
  const t = text.trim();
  if (!t) return [];
  let parts = t
    .split(/\n\s*\n/)
    .map((s) => s.trim())
    .filter(Boolean);
  if (parts.length <= 1 && t.length > 380) {
    const sentences = t.match(/[^.!?]+[.!?]+/g) || [t];
    const chunks = [];
    for (let i = 0; i < sentences.length; i += 3) {
      chunks.push(sentences.slice(i, i + 3).join(" ").trim());
    }
    if (chunks.length > 1) parts = chunks;
  }
  return parts.length ? parts : [t];
}

function firstSentence(p) {
  const s = p.trim();
  const m = s.match(/^[\s\S]{1,400}?[.!?](?=\s|$)/);
  if (m) return m[0].trim();
  const line = s.split("\n")[0];
  return line.trim().slice(0, 220);
}

function detectLogicRelations(text) {
  const found = new Set();
  for (const { re, zh } of LOGIC_MARKERS) {
    const rx = new RegExp(re.source, re.flags);
    if (rx.test(text)) found.add(zh);
  }
  return [...found].map(
    (zh) => `文中可体现「${zh}」类逻辑衔接，做题时注意选项是否与该逻辑一致。`
  );
}

function detectExamHints(text) {
  const out = [];
  for (const { test, msg } of EXAM_HINT_RULES) {
    if (test(text)) out.push(msg);
  }
  const extra = [
    "长难句：先找主谓宾，再看从句与插入语，避免被生词干扰整体理解。",
    "对比题：注意 while、whereas、unlike 等引出的对照关系。",
  ];
  for (const e of extra) {
    if (out.length >= 6) break;
    out.push(e);
  }
  return [...new Set(out)].slice(0, 8);
}

function analyzeReadingHeuristic(text) {
  const paras = splitIntoParagraphs(text);
  const paragraphGists = paras.map((p, i) => ({
    index: i + 1,
    gistZh:
      "（启发式）以下为首句/关键句参考，完整段意请结合全段核对；印刷或 OCR 可能有误差。",
    keySentenceEn: firstSentence(p),
  }));
  const logic = detectLogicRelations(text);
  return {
    mode: "heuristic",
    paragraphGists,
    coreViewpointZh:
      "（未使用 AI）请通读各段首句、末段与反复出现的主题词，用一句话概括「作者最想传递的信息」；说明文常为「对象 + 特征/步骤」，议论文关注论点与论据。",
    examPoints: detectExamHints(text),
    logicRelations:
      logic.length > 0
        ? logic
        : [
            "（启发式）未检出明显路标词时，仍留意 and / but / because 等基础逻辑连接词。",
          ],
    howToSolveZh: STATIC_HOW_TO_SOLVE,
  };
}

function parseJsonFromLlm(content) {
  let s = content.trim();
  const fence = s.match(/```(?:json)?\s*([\s\S]*?)```/);
  if (fence) s = fence[1].trim();
  return JSON.parse(s);
}

function getAiBaseUrl() {
  return EMBEDDED_AI_BASE.replace(/\/$/, "");
}

function getAiReadingModel() {
  let v = (localStorage.getItem(STORAGE_AI_MODEL) || "").trim();
  if (!v) {
    try {
      v = (document.getElementById("ai-model")?.value || "").trim();
    } catch {
      v = "";
    }
  }
  return v || DEFAULT_AI_MODEL;
}

function getAiOcrModel() {
  let v = (localStorage.getItem(STORAGE_AI_OCR_MODEL) || "").trim();
  if (!v) {
    try {
      v = (document.getElementById("ai-ocr-model")?.value || "").trim();
    } catch {
      v = "";
    }
  }
  return v || DEFAULT_AI_OCR_MODEL;
}

function getAiHeaders() {
  /** 鉴权由本地 Go 代理附加，浏览器不再带 Bearer */
  return { "Content-Type": "application/json" };
}

/**
 * @param {Blob} blob
 * @returns {Promise<string>}
 */
function blobToDataUrl(blob) {
  return new Promise((resolve, reject) => {
    const r = new FileReader();
    r.onload = () => resolve(String(r.result));
    r.onerror = () => reject(new Error("read failed"));
    r.readAsDataURL(blob);
  });
}

/**
 * @param {unknown} content
 * @returns {string}
 */
function normalizeChatContent(content) {
  if (content == null) return "";
  if (typeof content === "string") return content;
  if (Array.isArray(content)) {
    return content
      .map((p) => {
        if (typeof p === "string") return p;
        if (p && typeof p === "object" && "text" in p) return String(/** @type {{ text?: string }} */ (p).text);
        return "";
      })
      .join("");
  }
  return String(content);
}

/**
 * @param {Blob | File} imageBlobOrFile
 * @param {{ setStatus?: (s: string) => void }} [opts]
 */
async function transcribeImageWithLlm(imageBlobOrFile, opts) {
  const base = getAiBaseUrl();
  const model = getAiOcrModel();
  const setStatus = opts?.setStatus;
  setStatus?.("正在读取图片…");
  const dataUrl = await blobToDataUrl(imageBlobOrFile);
  setStatus?.("正在请求大模型识图（可能需要几秒）…");
  const url = `${base}/chat/completions`;
  const body = {
    model,
    messages: [
      {
        role: "system",
        content:
          "You transcribe English text from photos of textbooks and exam papers. Output only the English text from the image, preserving line breaks where natural. Do not translate or explain.",
      },
      {
        role: "user",
        content: [
          {
            type: "text",
            text: "Extract all English text from this image. If there is no English, output an empty string.",
          },
          { type: "image_url", image_url: { url: dataUrl } },
        ],
      },
    ],
    temperature: 0.1,
    max_tokens: 8192,
  };
  const r = await fetch(url, {
    method: "POST",
    headers: getAiHeaders(),
    body: JSON.stringify(body),
  });
  if (!r.ok) {
    const errText = await r.text();
    throw new Error(`OCR API ${r.status}: ${errText.slice(0, 280)}`);
  }
  const data = await r.json();
  const raw = data.choices?.[0]?.message?.content;
  return normalizeChatContent(raw).trim();
}

async function analyzeReadingWithAi(text) {
  const base = getAiBaseUrl();
  const model = getAiReadingModel();
  const sample = text.length > 12000 ? `${text.slice(0, 12000)}\n\n…（已截断）` : text;
  const userPrompt = `以下英文可能来自 OCR。请用中文完成分析，并只输出一个 JSON 对象（不要使用 markdown 代码围栏），键名必须完全一致：
{
  "paragraphGists": [{"index":1,"gistZh":"该段主旨的中文概括","keySentenceEn":"从原文摘一句最能代表该段的英文原句"}],
  "coreViewpointZh":"全文核心观点（中文）",
  "examPoints": ["中考阅读高频考点提示1","提示2"],
  "logicRelations": ["如：因果/转折/例证 与答题注意"],
  "howToSolveZh":"针对此类文章与常见设问，学生应如何审题与作答（分条，用\\\\n换行）"
}
英文文本：
---
${sample}
---`;

  const url = `${base}/chat/completions`;
  const body = {
    model,
    messages: [
      {
        role: "system",
        content: "你是中考英语阅读教研助手，只输出合法 JSON，不要任何解释文字。",
      },
      { role: "user", content: userPrompt },
    ],
    temperature: 0.35,
  };
  const r = await fetch(url, {
    method: "POST",
    headers: getAiHeaders(),
    body: JSON.stringify(body),
  });
  if (!r.ok) {
    const errText = await r.text();
    throw new Error(`API ${r.status}: ${errText.slice(0, 240)}`);
  }
  const data = await r.json();
  const rawMsg = data.choices?.[0]?.message?.content;
  const content = normalizeChatContent(rawMsg);
  if (!content.trim()) throw new Error("无效响应");
  let parsed;
  try {
    parsed = parseJsonFromLlm(content);
  } catch {
    throw new Error("无法解析 AI 返回的 JSON");
  }
  return {
    mode: "ai",
    paragraphGists: Array.isArray(parsed.paragraphGists) ? parsed.paragraphGists : [],
    coreViewpointZh: String(parsed.coreViewpointZh || ""),
    examPoints: Array.isArray(parsed.examPoints) ? parsed.examPoints.map(String) : [],
    logicRelations: Array.isArray(parsed.logicRelations) ? parsed.logicRelations.map(String) : [],
    howToSolveZh: String(parsed.howToSolveZh || ""),
  };
}

function readingAnalysisToHtml(data) {
  const gists = data.paragraphGists
    .map(
      (g) =>
        `<div class="para-block"><strong>第 ${g.index} 段</strong> · ${escapeHtml(
          g.gistZh
        )}<div class="para-en">${escapeHtml(g.keySentenceEn)}</div></div>`
    )
    .join("");
  const exams = data.examPoints.map((x) => `<li>${escapeHtml(x)}</li>`).join("");
  const logic = data.logicRelations.map((x) => `<li>${escapeHtml(x)}</li>`).join("");
  const how = escapeHtml(data.howToSolveZh).replace(/\n/g, "<br/>");
  return `
    ${gists ? `<h3>各段主旨与关键句</h3>${gists}` : ""}
    <h3>文章核心观点</h3>
    <p>${escapeHtml(data.coreViewpointZh)}</p>
    <h3>高频考点与应对</h3>
    <ul>${exams || "<li>（无）</li>"}</ul>
    <h3>逻辑关系</h3>
    <ul>${logic || "<li>（无）</li>"}</ul>
    <h3>题目怎么去做</h3>
    <p>${how}</p>
  `;
}

function renderReadingAnalysisPanels(data) {
  const wrapScan = $("reading-analysis-wrap");
  const wrapResult = $("result-reading-wrap");
  const statusEl = $("reading-analysis-status");
  if (!data) {
    wrapScan.classList.add("hidden");
    wrapResult.classList.add("hidden");
    statusEl.hidden = true;
    return;
  }
  const modeLine =
    data.mode === "ai"
      ? "当前为 AI 深度分析（接口返回）。"
      : "当前为本地启发式分析；在「我的」配置 API 后可点击「AI 深度分析」获取更准解读。";
  $("reading-analysis-mode").textContent = modeLine;
  $("result-reading-mode").textContent = modeLine;
  const html = readingAnalysisToHtml(data);
  $("reading-analysis-body").innerHTML = html;
  $("result-reading-body").innerHTML = html;
  wrapScan.classList.remove("hidden");
  wrapResult.classList.remove("hidden");
  statusEl.hidden = true;
}

function applyReadingHeuristicFromText(text) {
  const t = text.trim();
  if (t.length < 15) {
    lastReadingAnalysis = null;
    renderReadingAnalysisPanels(null);
    return;
  }
  lastReadingAnalysis = analyzeReadingHeuristic(t);
  renderReadingAnalysisPanels(lastReadingAnalysis);
}

async function runAiReadingAnalysis() {
  const text = $("scan-text").value.trim();
  if (text.length < 15) {
    $("ocr-status").textContent = "请先输入或识别足够长的英文段落（至少一两句）。";
    return;
  }
  const st = $("reading-analysis-status");
  st.hidden = false;
  st.textContent = "正在请求 AI 分析…";
  try {
    const data = await analyzeReadingWithAi(text);
    lastReadingAnalysis = data;
    renderReadingAnalysisPanels(data);
    st.textContent = "AI 分析完成。";
  } catch (e) {
    console.error(e);
    st.textContent = `AI 分析失败：${e && e.message ? e.message : "请检查网络与接口"}`;
  }
}

function loadAiSettings() {
  try {
    $("ai-model").value = localStorage.getItem(STORAGE_AI_MODEL) ?? DEFAULT_AI_MODEL;
    $("ai-ocr-model").value = localStorage.getItem(STORAGE_AI_OCR_MODEL) ?? DEFAULT_AI_OCR_MODEL;
  } catch {
    /* ignore */
  }
}

function $(id) {
  const el = document.getElementById(id);
  if (!el) throw new Error(`Missing #${id}`);
  return el;
}

function migrateNotebookIfNeeded() {
  try {
    const raw = localStorage.getItem(STORAGE_NOTEBOOK);
    if (!raw) return;
    const parsed = JSON.parse(raw);
    if (!Array.isArray(parsed)) return;
    const m = {};
    for (const x of parsed) {
      if (typeof x === "string") m[x.toLowerCase()] = 1;
    }
    localStorage.setItem(STORAGE_NOTEBOOK, JSON.stringify(m));
  } catch {
    /* ignore */
  }
}

/** @returns {Record<string, number>} */
function loadNotebookMap() {
  migrateNotebookIfNeeded();
  try {
    const raw = localStorage.getItem(STORAGE_NOTEBOOK);
    if (!raw) return {};
    const o = JSON.parse(raw);
    if (!o || typeof o !== "object" || Array.isArray(o)) return {};
    /** @type {Record<string, number>} */
    const m = {};
    for (const [k, v] of Object.entries(o)) {
      if (typeof k === "string" && typeof v === "number" && v > 0) m[k.toLowerCase()] = v;
    }
    return m;
  } catch {
    return {};
  }
}

/** @param {Record<string, number>} m */
function saveNotebookMap(m) {
  localStorage.setItem(STORAGE_NOTEBOOK, JSON.stringify(m));
}

/** @returns {Array<[string, number]>} sorted by word */
function loadNotebookSortedEntries() {
  const m = loadNotebookMap();
  return Object.entries(m).sort((a, b) => a[0].localeCompare(b[0], "en"));
}

/** @param {string} word */
function notebookCount(word) {
  const w = word.toLowerCase();
  return loadNotebookMap()[w] ?? 0;
}

/** @type {ReturnType<typeof setTimeout> | null} */
let notebookToastTimer = null;

function showNotebookToast(message = "加入生词本成功") {
  let el = document.getElementById("notebook-toast");
  if (!el) {
    el = document.createElement("div");
    el.id = "notebook-toast";
    el.className = "notebook-toast";
    el.setAttribute("role", "status");
    document.body.appendChild(el);
  }
  el.textContent = message;
  el.classList.add("visible");
  if (notebookToastTimer != null) clearTimeout(notebookToastTimer);
  notebookToastTimer = setTimeout(() => {
    el.classList.remove("visible");
    notebookToastTimer = null;
  }, 2000);
}

function addToNotebook(word) {
  const w = word.toLowerCase();
  if (!w) return;
  const m = loadNotebookMap();
  m[w] = (m[w] ?? 0) + 1;
  saveNotebookMap(m);
  showNotebookToast();
}

function removeFromNotebook(word) {
  const w = word.toLowerCase();
  const m = loadNotebookMap();
  delete m[w];
  saveNotebookMap(m);
}

function applyFontClass() {
  const v = localStorage.getItem(STORAGE_FONT) || "standard";
  const app = $("app");
  app.classList.remove("font-small", "font-standard", "font-large", "font-xlarge");
  const map = { small: "font-small", standard: "font-standard", large: "font-large", xlarge: "font-xlarge" };
  app.classList.add(map[v] || "font-standard");
}

const TAB_HEADER_TITLES = {
  chinese: "语文",
  math: "数学",
  english: "英语",
  physics: "物理",
  daofa: "道法",
  profile: "我的",
  "pe-sports": "中考体育",
};

function getEnglishSub() {
  try {
    const v = localStorage.getItem(STORAGE_ENGLISH_SUB);
    if (
      v === "structure" ||
      v === "vocab" ||
      v === "scan" ||
      v === "essay" ||
      v === "readinghf" ||
      v === "readingskills" ||
      v === "mc688"
    ) {
      return v;
    }
    return "vocab";
  } catch (_) {
    return "vocab";
  }
}

function setEnglishSub(v) {
  try {
    localStorage.setItem(STORAGE_ENGLISH_SUB, v);
  } catch (_) {}
}

let readingHfLoaded = false;
/** @type {{ rank: number, word: string, phonetic: string, meaning: string, frequency: number }[]} */
let readingHfEntries = [];

function loadReadingHighFreq() {
  const wrap = $("reading-hf-wrap");
  const noteEl = $("reading-hf-note");
  if (!wrap) return;
  if (readingHfLoaded) {
    renderReadingHighFreqTable();
    return;
  }
  wrap.innerHTML = "<p class=\"muted\">加载中…</p>";
  fetch("data/reading_high_freq.json")
    .then((r) => {
      if (!r.ok) throw new Error(String(r.status));
      return r.json();
    })
    .then((data) => {
      readingHfEntries = data.entries || [];
      readingHfLoaded = true;
      if (noteEl && data.note) {
        noteEl.textContent = data.note;
        noteEl.hidden = false;
        noteEl.classList.remove("hidden");
      }
      renderReadingHighFreqTable();
    })
    .catch(() => {
      wrap.innerHTML = "<p class=\"muted\">加载失败，请确认存在 <code>data/reading_high_freq.json</code>。</p>";
    });
}

function renderReadingHighFreqTable() {
  const wrap = $("reading-hf-wrap");
  if (!wrap) return;
  if (!readingHfEntries.length) {
    wrap.innerHTML = "<p class=\"muted\">暂无数据。</p>";
    return;
  }
  const rows = readingHfEntries
    .map(
      (e) =>
        `<tr>` +
        `<td>${e.rank}</td>` +
        `<td><span class="reading-hf-word-cell"><button type="button" class="linklike reading-hf-word" data-word="${escapeAttr(e.word)}">${escapeHtml(e.word)}</button>` +
        `<button type="button" class="btn-nb-plus reading-hf-add-nb" data-word="${escapeAttr(e.word)}" aria-label="加入生词本">+</button></span></td>` +
        `<td class="reading-hf-ipa">${escapeHtml(e.phonetic)}</td>` +
        `<td>${escapeHtml(e.meaning)}</td>` +
        `<td class="reading-hf-freq">${e.frequency}</td>` +
        `<td><button type="button" class="btn-secondary reading-hf-speak" data-word="${escapeAttr(e.word)}" aria-label="朗读">🔊</button></td>` +
        `</tr>`
    )
    .join("");
  wrap.innerHTML =
    `<div class="reading-hf-scroll">` +
    `<table class="reading-hf-table" aria-label="阅读高频词汇">` +
    `<thead><tr><th>序号</th><th>单词</th><th>音标</th><th>含义</th><th>词频</th><th></th></tr></thead>` +
    `<tbody>${rows}</tbody></table></div>`;
  wrap.querySelectorAll(".reading-hf-speak, .reading-hf-word").forEach((btn) => {
    btn.addEventListener("click", () => speakEnglishWord((btn.dataset.word || "").trim()));
  });
  wrap.querySelectorAll(".reading-hf-add-nb").forEach((btn) => {
    btn.addEventListener("click", (e) => {
      e.stopPropagation();
      const w = (btn.dataset.word || "").trim();
      if (w) {
        addToNotebook(w);
        renderNotebook();
      }
    });
  });
}

function speakEnglishWord(word) {
  if (!word) return;
  if (typeof window.speechSynthesis === "undefined") {
    alert("当前浏览器不支持语音朗读。");
    return;
  }
  const synth = window.speechSynthesis;
  synth.cancel();
  const doSpeak = () => {
    const u = new SpeechSynthesisUtterance(word);
    u.lang = "en-US";
    const voices = synth.getVoices();
    const en =
      voices.find((v) => /^en(-|$)/i.test(v.lang.replace("_", "-"))) ||
      voices.find((v) => v.lang.toLowerCase().startsWith("en"));
    if (en) u.voice = en;
    synth.speak(u);
  };
  if (synth.getVoices().length > 0) {
    doSpeak();
    return;
  }
  let spoken = false;
  const once = () => {
    if (spoken) return;
    spoken = true;
    synth.removeEventListener("voiceschanged", once);
    doSpeak();
  };
  synth.addEventListener("voiceschanged", once);
  setTimeout(once, 400);
}

/** @param {"structure"|"vocab"|"scan"|"readinghf"|"readingskills"|"mc688"|"essay"} sub */
function showEnglishSub(sub) {
  let s = "vocab";
  if (sub === "structure") s = "structure";
  else if (sub === "scan") s = "scan";
  else if (sub === "essay") s = "essay";
  else if (sub === "readinghf") s = "readinghf";
  else if (sub === "readingskills") s = "readingskills";
  else if (sub === "mc688") s = "mc688";
  else if (sub === "vocab") s = "vocab";
  setEnglishSub(s);
  document.querySelectorAll("#panel-english [data-english-pane]").forEach((pane) => {
    pane.classList.toggle("active", pane.dataset.englishPane === s);
  });
  document.querySelectorAll("#panel-english [data-english-sub]").forEach((btn) => {
    btn.classList.toggle("active", btn.dataset.englishSub === s);
  });
  if (s === "structure") {
    loadEnglishStructureTab();
  }
  if (s === "essay") {
    setEssaySubject("english");
    renderEssayList("en");
  }
  if (s === "scan") {
    renderReadingMaterials();
  }
  if (s === "readinghf") {
    loadReadingHighFreq();
  }
  if (s === "readingskills") {
    loadReadingSkills();
  }
  if (s === "mc688") {
    loadMc688();
  }
}

let readingSkillsLoaded = false;

function loadReadingSkills() {
  const root = $("reading-skills-root");
  if (!root) return;
  if (readingSkillsLoaded) return;
  root.innerHTML = "<p class=\"muted\">加载中…</p>";
  fetch("data/reading_skills_zhongkao.json")
    .then((r) => {
      if (!r.ok) throw new Error(String(r.status));
      return r.json();
    })
    .then((data) => {
      readingSkillsLoaded = true;
      root.innerHTML = renderReadingSkillsHtml(data);
    })
    .catch(() => {
      root.innerHTML = "<p class=\"muted\">加载失败，请确认存在 <code>data/reading_skills_zhongkao.json</code>。</p>";
    });
}

/** @param {{ label?: string, intro?: string, topics: { id: string, title: string, summary?: string, sections: { subtitle?: string, paragraph?: string, bullets?: string[] }[] }[] }} data */
function renderReadingSkillsHtml(data) {
  const intro = data.intro ? `<p class="muted reading-skills-intro">${escapeHtml(data.intro)}</p>` : "";
  const topics = (data.topics || [])
    .map((t) => {
      const sum = t.summary ? `<p class="muted small reading-skills-sum">${escapeHtml(t.summary)}</p>` : "";
      const secs = (t.sections || [])
        .map((s) => {
          const sub = s.subtitle ? `<h4 class="reading-skills-sub">${escapeHtml(s.subtitle)}</h4>` : "";
          const para = s.paragraph
            ? `<p class="reading-skills-p">${escapeHtml(s.paragraph).replace(/\n/g, "<br/>")}</p>`
            : "";
          const ul =
            s.bullets && s.bullets.length
              ? `<ul class="reading-skills-ul">${s.bullets.map((b) => `<li>${escapeHtml(b)}</li>`).join("")}</ul>`
              : "";
          return `<div class="reading-skills-sec">${sub}${para}${ul}</div>`;
        })
        .join("");
      return `<article class="reading-skills-topic card settings-block"><h3 class="reading-skills-h3">${escapeHtml(t.title)}</h3>${sum}${secs}</article>`;
    })
    .join("");
  return `<div class="reading-skills-inner">${intro}${topics}</div>`;
}

let mc688Loaded = false;
/** @type {{ rank: number, day: number, word: string, meaning: string }[]} */
let mc688Entries = [];
let mc688SelectedDay = 1;
/** @type {{ words: { rank: number, word: string, meaning: string }[]; idx: number } | null} */
let mc688Session = null;
let mc688Answer = "";
let mc688Feedback = "";

function loadMc688() {
  const root = $("mc688-root");
  if (!root) return;
  if (mc688Loaded) {
    renderMc688();
    return;
  }
  root.innerHTML = "<p class=\"muted\">加载中…</p>";
  fetch("data/mc688_21day.json")
    .then((r) => {
      if (!r.ok) throw new Error(String(r.status));
      return r.json();
    })
    .then((data) => {
      mc688Entries = data.entries || [];
      mc688Loaded = true;
      renderMc688();
    })
    .catch(() => {
      root.innerHTML = "<p class=\"muted\">加载失败，请确认存在 <code>data/mc688_21day.json</code>。</p>";
    });
}

function shuffleArray(a) {
  const x = a.slice();
  for (let i = x.length - 1; i > 0; i--) {
    const j = Math.floor(Math.random() * (i + 1));
    [x[i], x[j]] = [x[j], x[i]];
  }
  return x;
}

function renderMc688() {
  const root = $("mc688-root");
  if (!root || !mc688Entries.length) {
    if (root && !mc688Entries.length) root.innerHTML = "<p class=\"muted\">暂无数据。</p>";
    return;
  }
  const pool = mc688Entries.filter((e) => e.day === mc688SelectedDay);
  const maxN = Math.min(33, pool.length) || 1;
  const dayBtns = Array.from({ length: 21 }, (_, i) => i + 1)
    .map(
      (d) =>
        `<button type="button" class="btn-secondary mc688-day-btn ${d === mc688SelectedDay ? "active" : ""}" data-mc688-day="${d}">第${d}天</button>`
    )
    .join("");
  const sess = mc688Session;
  const cur = sess && sess.words[sess.idx];
  const dictationBlock =
    sess && cur
      ? `<div class="mc688-dict card settings-block mt-8">
      <p class="small muted">第 ${sess.idx + 1} / ${sess.words.length} 词 · 序号 ${cur.rank}</p>
      <div class="row-actions wrap mt-8">
        <input type="text" id="mc688-answer" class="input-search" style="flex:1;min-width:140px" placeholder="输入英文单词" autocomplete="off" value="${escapeAttr(mc688Answer)}" />
        <button type="button" class="btn-primary" id="mc688-submit">提交</button>
        <button type="button" class="btn-secondary" id="mc688-next">${sess.idx >= sess.words.length - 1 ? "结束" : "下一词"}</button>
      </div>
      <p class="mc688-feedback muted small mt-8">${escapeHtml(mc688Feedback)}</p>
    </div>`
      : "";

  root.innerHTML =
    `<p class="small muted mb-8">${escapeHtml(mc688Entries.length ? "共 688 词" : "")}</p>` +
    `<div class="row-actions wrap mc688-day-row" role="group" aria-label="选择天数">${dayBtns}</div>` +
    `<h3 class="section-title mt-16">听写</h3>` +
    `<p class="muted small">从当天词汇中抽取若干词朗读，请输入英文。</p>` +
    `<div class="row-actions wrap mt-8 align-center">` +
    `<label class="small">词数 <input type="number" id="mc688-count" min="1" max="${maxN}" value="${Math.min(10, maxN)}" class="input-search" style="width:72px" /></label>` +
    `<label class="small"><input type="checkbox" id="mc688-shuffle" /> 随机顺序</label>` +
    `<button type="button" class="btn-primary" id="mc688-start">开始听写</button>` +
    `<button type="button" class="btn-secondary" id="mc688-repeat"${sess && cur ? "" : " disabled"}>再听一遍</button>` +
    `</div>` +
    dictationBlock +
    `<h3 class="section-title mt-16">本日词表</h3>` +
    `<div class="reading-hf-scroll"><table class="reading-hf-table"><thead><tr><th>序号</th><th>单词</th><th>释义</th><th></th></tr></thead><tbody>` +
    pool
      .map(
        (e) =>
          `<tr><td>${e.rank}</td><td><span class="mc688-word-cell"><button type="button" class="linklike mc688-word" data-word="${escapeAttr(e.word)}">${escapeHtml(e.word)}</button>` +
          `<button type="button" class="btn-nb-plus mc688-add-nb" data-word="${escapeAttr(e.word)}" aria-label="加入生词本">+</button></span></td><td>${escapeHtml(e.meaning)}</td>` +
          `<td><button type="button" class="btn-secondary mc688-speak" data-word="${escapeAttr(e.word)}" aria-label="朗读">🔊</button></td></tr>`
      )
      .join("") +
    `</tbody></table></div>`;

  root.querySelectorAll(".mc688-day-btn").forEach((btn) => {
    btn.addEventListener("click", () => {
      mc688SelectedDay = parseInt(/** @type {HTMLElement} */ (btn).dataset.mc688Day || "1", 10);
      mc688Session = null;
      mc688Feedback = "";
      mc688Answer = "";
      renderMc688();
    });
  });
  const startBtn = $("mc688-start");
  if (startBtn) {
    startBtn.addEventListener("click", () => {
      const n = parseInt(($("mc688-count") && $("mc688-count").value) || "10", 10);
      const shuf = $("mc688-shuffle") && $("mc688-shuffle").checked;
      const p = mc688Entries.filter((e) => e.day === mc688SelectedDay);
      if (!p.length) return;
      const lim = Math.min(Math.max(1, n), Math.min(33, p.length));
      let pick = p.slice(0, lim);
      if (shuf) pick = shuffleArray(p).slice(0, lim);
      mc688Session = { words: pick, idx: 0 };
      mc688Answer = "";
      mc688Feedback = "";
      renderMc688();
      const w = pick[0].word;
      setTimeout(() => speakEnglishWord(w), 100);
    });
  }
  const rep = $("mc688-repeat");
  if (rep && sess && cur) {
    rep.addEventListener("click", () => speakEnglishWord(cur.word));
  }
  root.querySelectorAll(".mc688-speak, .mc688-word").forEach((btn) => {
    btn.addEventListener("click", () => speakEnglishWord((btn.dataset.word || "").trim()));
  });
  root.querySelectorAll(".mc688-add-nb").forEach((btn) => {
    btn.addEventListener("click", (e) => {
      e.stopPropagation();
      const w = (btn.dataset.word || "").trim();
      if (w) {
        addToNotebook(w);
        renderNotebook();
      }
    });
  });
  const ansEl = $("mc688-answer");
  if (ansEl && sess && cur) {
    ansEl.addEventListener("input", () => {
      mc688Answer = /** @type {HTMLInputElement} */ (ansEl).value;
    });
    $("mc688-submit")?.addEventListener("click", () => {
      const a = /** @type {HTMLInputElement} */ (ansEl).value.trim();
      const ok = a.toLowerCase() === cur.word.toLowerCase();
      mc688Feedback = ok ? "正确" : `正确写法：${cur.word}`;
      renderMc688();
    });
    $("mc688-next")?.addEventListener("click", () => {
      if (!sess) return;
      if (sess.idx >= sess.words.length - 1) {
        mc688Session = null;
        mc688Feedback = "本轮完成";
        mc688Answer = "";
        renderMc688();
        return;
      }
      sess.idx += 1;
      mc688Answer = "";
      mc688Feedback = "";
      renderMc688();
      const nw = sess.words[sess.idx].word;
      setTimeout(() => speakEnglishWord(nw), 100);
    });
  }
}

function getDaofaSub() {
  try {
    const v = localStorage.getItem(STORAGE_DAOF_SUB);
    if (v === "subjective" || v === "past") return v;
    return "structure";
  } catch (_) {
    return "structure";
  }
}

function setDaofaSub(v) {
  try {
    localStorage.setItem(STORAGE_DAOF_SUB, v);
  } catch (_) {}
}

/** @param {"structure"|"subjective"|"past"} sub */
function showDaofaSub(sub) {
  const s =
    sub === "subjective" ? "subjective" : sub === "past" ? "past" : "structure";
  setDaofaSub(s);
  closeDaofaDetail();
  closeDaofaPastDetail();
  document.querySelectorAll("#panel-daofa [data-daofa-pane]").forEach((pane) => {
    pane.classList.toggle("active", pane.dataset.daofaPane === s);
  });
  document.querySelectorAll("#panel-daofa [data-daofa-sub]").forEach((btn) => {
    btn.classList.toggle("active", btn.dataset.daofaSub === s);
  });
  if (s === "subjective") {
    renderDaofaList();
  }
  if (s === "past") {
    renderDaofaPastList();
  }
  if (s === "structure") {
    loadDaofaStructureTab();
  }
}

/** @returns {{ title: string, body: string }[]} */
function parseDaofaSections(text) {
  const lines = text.split("\n");
  const sections = [];
  let i = 0;
  while (i < lines.length) {
    const t = lines[i].trim();
    if (t.startsWith("📚") || t.startsWith("📊")) {
      const title = t;
      const bodyLines = [];
      i++;
      while (i < lines.length) {
        const lt = lines[i].trim();
        if (lt.startsWith("📚") || lt.startsWith("📊")) break;
        bodyLines.push(lines[i]);
        i++;
      }
      sections.push({ title, body: bodyLines.join("\n").trim() });
    } else {
      i++;
    }
  }
  return sections;
}

let daofaDataReady = false;
/** @type {{ title: string, body: string }[]} */
let daofaSectionsCache = [];
/** @type {{ title: string, body: string }[]} */
let daofaPastItemsCache = [];
/** @type {string} */
let daofaPastLabel = "";

function renderPhysicsBlock(block) {
  const wrap = document.createElement("div");
  wrap.className = "physics-block";
  const t = block.type;
  if (t === "table") {
    wrap.appendChild(renderPhysicsTable(block.headers || [], block.rows || []));
    return wrap;
  }
  if (t === "subheading") {
    wrap.classList.add("physics-subheading");
    wrap.textContent = block.text || "";
    return wrap;
  }
  if (t === "label") {
    wrap.classList.add("physics-label");
    wrap.textContent = block.text || "";
    return wrap;
  }
  if (t === "bullets") {
    wrap.classList.add("physics-bullets");
    const ul = document.createElement("ul");
    for (const line of block.items || []) {
      const li = document.createElement("li");
      li.textContent = line;
      ul.appendChild(li);
    }
    wrap.appendChild(ul);
    return wrap;
  }
  if (t === "keyValues") {
    wrap.classList.add("physics-kv");
    for (const pair of block.pairs || []) {
      if (!pair || pair.length < 2) continue;
      const row = document.createElement("div");
      row.className = "physics-kv-row";
      const k = document.createElement("span");
      k.className = "physics-kv-key";
      k.textContent = pair[0];
      const v = document.createElement("span");
      v.className = "physics-kv-val";
      v.textContent = pair[1];
      row.append(k, v);
      wrap.appendChild(row);
    }
    return wrap;
  }
  if (t === "subsection") {
    wrap.classList.add("physics-subsection");
    const h = document.createElement("h4");
    h.textContent = block.title || "";
    wrap.appendChild(h);
    return wrap;
  }
  if (t === "callout") {
    wrap.classList.add("physics-callout");
    const ul = document.createElement("ul");
    for (const line of block.items || []) {
      const li = document.createElement("li");
      li.textContent = line;
      ul.appendChild(li);
    }
    wrap.appendChild(ul);
    return wrap;
  }
  if (block.text) {
    wrap.classList.add("physics-plain");
    wrap.textContent = block.text;
  }
  return wrap;
}

function renderPhysicsTable(headers, rows) {
  const table = document.createElement("table");
  table.className = "physics-table";
  const thead = document.createElement("thead");
  const trh = document.createElement("tr");
  for (const h of headers) {
    const th = document.createElement("th");
    th.textContent = h;
    trh.appendChild(th);
  }
  thead.appendChild(trh);
  table.appendChild(thead);
  const tbody = document.createElement("tbody");
  const n = headers.length;
  for (const row of rows) {
    const tr = document.createElement("tr");
    for (let i = 0; i < n; i++) {
      const td = document.createElement("td");
      td.textContent = row[i] != null ? String(row[i]) : "";
      tr.appendChild(td);
    }
    tbody.appendChild(tr);
  }
  table.appendChild(tbody);
  return table;
}

function renderExamStructure(data, root) {
  if (!root) return;
  root.innerHTML = "";
  const hero = document.createElement("div");
  hero.className = "physics-hero card settings-block";
  let html = `<h2 class="physics-title">${escapeHtml(data.title || "")}</h2>`;
  if (data.subtitle) html += `<p class="physics-subtitle">${escapeHtml(data.subtitle)}</p>`;
  if (data.badge) html += `<p class="physics-badge">${escapeHtml(data.badge)}</p>`;
  hero.innerHTML = html;
  root.appendChild(hero);
  for (const sec of data.sections || []) {
    const card = document.createElement("section");
    card.className = "physics-section-card card settings-block";
    const h3 = document.createElement("h3");
    h3.className = "physics-section-title";
    h3.textContent = sec.title || "";
    card.appendChild(h3);
    const body = document.createElement("div");
    body.className = "physics-section-body";
    for (const block of sec.blocks || []) {
      body.appendChild(renderPhysicsBlock(block));
    }
    card.appendChild(body);
    root.appendChild(card);
  }
}

function renderPhysicsStructure(data) {
  renderExamStructure(data, $("physics-root"));
}

function loadPhysicsTab() {
  const root = $("physics-root");
  if (!root || root.dataset.loaded === "1") return;
  const status = $("physics-structure-status");
  fetch("data/physics_beijing_structure.json")
    .then((r) => {
      if (!r.ok) throw new Error(String(r.status));
      return r.json();
    })
    .then((data) => {
      if (status) status.remove();
      renderExamStructure(data, root);
      root.dataset.loaded = "1";
    })
    .catch((e) => {
      if (status) status.textContent = `加载失败：${e.message || e}`;
    });
}

function loadMathTab() {
  const root = $("math-root");
  if (!root || root.dataset.loaded === "1") return;
  const status = $("math-structure-status");
  fetch("data/math_beijing_structure.json")
    .then((r) => {
      if (!r.ok) throw new Error(String(r.status));
      return r.json();
    })
    .then((data) => {
      if (status) status.remove();
      renderExamStructure(data, root);
      root.dataset.loaded = "1";
    })
    .catch((e) => {
      if (status) status.textContent = `加载失败：${e.message || e}`;
    });
}

function loadChineseStructureTab() {
  const root = $("chinese-structure-root");
  if (!root || root.dataset.loaded === "1") return;
  const status = $("chinese-structure-status");
  fetch("data/chinese_beijing_structure.json")
    .then((r) => {
      if (!r.ok) throw new Error(String(r.status));
      return r.json();
    })
    .then((data) => {
      if (status) status.remove();
      renderExamStructure(data, root);
      root.dataset.loaded = "1";
    })
    .catch((e) => {
      if (status) status.textContent = `加载失败：${e.message || e}`;
    });
}

function getMathSub() {
  try {
    const v = localStorage.getItem(STORAGE_MATH_SUB);
    if (v === "past") return "past";
    if (v === "extra") return "structure";
    return "structure";
  } catch (_) {
    return "structure";
  }
}

function setMathSub(v) {
  try {
    const s = v === "past" ? "past" : "structure";
    localStorage.setItem(STORAGE_MATH_SUB, s);
  } catch (_) {}
}

/** @param {"structure"|"past"} sub */
function showMathSub(sub) {
  const s = sub === "past" ? "past" : "structure";
  setMathSub(s);
  closeMathZhongkaoDetail();
  document.querySelectorAll("#panel-math [data-math-pane]").forEach((pane) => {
    pane.classList.toggle("active", pane.dataset.mathPane === s);
  });
  document.querySelectorAll("#panel-math [data-math-sub]").forEach((btn) => {
    btn.classList.toggle("active", btn.dataset.mathSub === s);
  });
  if (s === "structure") {
    loadMathTab();
  }
  if (s === "past") {
    renderMathZhongkaoList();
  }
}

function loadMathTabNav() {
  showMathSub(getMathSub());
  if (mathZkReady) return;
  fetch("data/math_zhongkao.json")
    .then((r) => (r.ok ? r.json() : Promise.resolve({ items: [] })))
    .catch(() => ({ items: [] }))
    .then((data) => {
      mathZkReady = true;
      const items = data && Array.isArray(data.items) ? data.items : [];
      mathZkItemsCache = items.map((it) => ({
        title: it.title || "",
        body: it.body || "",
        images: Array.isArray(it.images) ? it.images.filter((x) => typeof x === "string") : [],
      }));
      mathZkLabel = (data && data.label) || "";
      renderMathZhongkaoList();
    });
}

function renderMathZhongkaoList() {
  const listEl = $("math-zk-stack-list");
  if (!listEl) return;
  const sections = mathZkItemsCache;
  if (!sections.length) {
    listEl.innerHTML =
      "<p class=\"muted\">" +
      (mathZkReady ? "暂无历年真题数据。" : "加载中…") +
      "</p>";
    return;
  }
  const hint =
    mathZkLabel && String(mathZkLabel).trim()
      ? `<p class="hint muted">${escapeHtml(String(mathZkLabel).trim())}</p>`
      : `<p class="hint muted">按条目浏览，点击查看全文。</p>`;
  const parts = [];
  for (let idx = 0; idx < sections.length; idx++) {
    const sec = sections[idx];
    parts.push(
      `<div class="essay-row-card">` +
        `<button type="button" class="essay-row-btn math-zk-open-btn" data-math-zk-idx="${String(idx)}">` +
          `<span class="essay-row-title">${escapeHtml(sec.title)}</span><span class="muted essay-row-chevron" aria-hidden="true">›</span>` +
        `</button>` +
      `</div>`
    );
  }
  listEl.innerHTML = hint + parts.join("");
  listEl.querySelectorAll(".math-zk-open-btn").forEach((btn) => {
    btn.addEventListener("click", () => {
      const idx = parseInt(btn.dataset.mathZkIdx || "0", 10);
      openMathZhongkaoDetail(idx);
    });
  });
}

function mathZkDataUrl(assetPath) {
  const p = String(assetPath || "").trim();
  if (!p) return "";
  if (p.startsWith("data/") || p.startsWith("/") || /^https?:\/\//i.test(p)) return p;
  return "data/" + p.replace(/^\/+/, "");
}

function openMathZhongkaoDetail(idx) {
  const sec = mathZkItemsCache[idx];
  if (!sec) return;
  const listEl = $("math-zk-stack-list");
  const detailEl = $("math-zk-stack-detail");
  if (!listEl || !detailEl) return;
  listEl.classList.add("hidden");
  detailEl.classList.remove("hidden");
  const bodyHtml = escapeHtml(sec.body).replace(/\n/g, "<br />");
  const imgs = Array.isArray(sec.images) ? sec.images : [];
  const imgsHtml =
    imgs.length > 0
      ? `<div class="math-exam-images">` +
        imgs
          .map((p) => {
            const src = mathZkDataUrl(p);
            if (!src) return "";
            return `<figure class="math-exam-figure"><img src="${escapeAttr(src)}" alt="" loading="lazy" decoding="async" /></figure>`;
          })
          .join("") +
        `</div>`
      : "";
  const hasBody = sec.body && String(sec.body).trim();
  const textBlock = hasBody
    ? `<h3 class="essay-block-label">正文</h3><p class="essay-body-text">${bodyHtml}</p>`
    : "";
  const imgBlock =
    imgs.length > 0
      ? `<h3 class="essay-block-label">试卷图（点击放大）</h3>${imgsHtml}`
      : "";
  detailEl.innerHTML =
    `<div class="essay-detail-panel">` +
    `<button type="button" class="btn-secondary essay-detail-back">← 返回</button>` +
    `<h2 class="section-title essay-detail-sample-title">${escapeHtml(sec.title)}</h2>` +
    `<div class="card settings-block mt-8">` +
    textBlock +
    imgBlock +
    `</div></div>`;
  detailEl.querySelector(".essay-detail-back")?.addEventListener("click", closeMathZhongkaoDetail);
  detailEl.querySelectorAll(".math-exam-images img").forEach((img) => {
    img.addEventListener("click", () => openMathExamLightbox(img.getAttribute("src") || ""));
  });
}

function openMathExamLightbox(src) {
  const s = String(src || "").trim();
  if (!s) return;
  const box = $("math-exam-lightbox");
  const el = $("math-exam-lightbox-img");
  if (!box || !el) return;
  el.src = s;
  el.style.transform = "";
  box.classList.remove("hidden");
  try {
    document.body.style.overflow = "hidden";
  } catch (_) {}
}

function closeMathExamLightbox() {
  const box = $("math-exam-lightbox");
  const el = $("math-exam-lightbox-img");
  if (box) box.classList.add("hidden");
  if (el) el.removeAttribute("src");
  try {
    document.body.style.overflow = "";
  } catch (_) {}
}

function initMathExamLightbox() {
  const box = $("math-exam-lightbox");
  const btn = box?.querySelector(".math-exam-lightbox-close");
  const img = $("math-exam-lightbox-img");
  if (!box || !img) return;
  btn?.addEventListener("click", (e) => {
    e.stopPropagation();
    closeMathExamLightbox();
  });
  box.addEventListener("click", (e) => {
    if (e.target === box) closeMathExamLightbox();
  });
  img.addEventListener("click", (e) => e.stopPropagation());
  document.addEventListener("keydown", (e) => {
    if (e.key === "Escape" && box && !box.classList.contains("hidden")) {
      closeMathExamLightbox();
    }
  });
}

function closeMathZhongkaoDetail() {
  closeMathExamLightbox();
  const listEl = $("math-zk-stack-list");
  const detailEl = $("math-zk-stack-detail");
  if (!listEl || !detailEl) return;
  listEl.classList.remove("hidden");
  detailEl.classList.add("hidden");
  detailEl.innerHTML = "";
}

function loadPeSportsTab() {
  const root = $("pe-sports-root");
  if (!root || root.dataset.loaded === "1") return;
  const status = $("pe-sports-status");
  fetch("data/pe_beijing_structure.json")
    .then((r) => {
      if (!r.ok) throw new Error(String(r.status));
      return r.json();
    })
    .then((data) => {
      if (status) status.remove();
      renderExamStructure(data, root);
      root.dataset.loaded = "1";
    })
    .catch((e) => {
      if (status) status.textContent = `加载失败：${e.message || e}`;
    });
}

function loadEnglishStructureTab() {
  const root = $("english-structure-root");
  if (!root || root.dataset.loaded === "1") return;
  const status = $("english-structure-status");
  fetch("data/english_beijing_structure.json")
    .then((r) => {
      if (!r.ok) throw new Error(String(r.status));
      return r.json();
    })
    .then((data) => {
      if (status) status.remove();
      renderExamStructure(data, root);
      root.dataset.loaded = "1";
    })
    .catch((e) => {
      if (status) status.textContent = `加载失败：${e.message || e}`;
    });
}

function loadDaofaStructureTab() {
  const root = $("daofa-structure-root");
  if (!root || root.dataset.loaded === "1") return;
  const status = $("daofa-structure-status");
  fetch("data/daofa_beijing_structure.json")
    .then((r) => {
      if (!r.ok) throw new Error(String(r.status));
      return r.json();
    })
    .then((data) => {
      if (status) status.remove();
      renderExamStructure(data, root);
      root.dataset.loaded = "1";
    })
    .catch((e) => {
      if (status) status.textContent = `加载失败：${e.message || e}`;
    });
}

function loadDaofaTab() {
  if (daofaDataReady) {
    showDaofaSub(getDaofaSub());
    return;
  }
  loadDaofaStructureTab();
  Promise.all([
    fetch("data/daofa_reference.txt").then((r) => {
      if (!r.ok) throw new Error(String(r.status));
      return r.text();
    }),
    fetch("data/daofa_past_exams.json")
      .then((r) => (r.ok ? r.json() : Promise.resolve({ items: [] })))
      .catch(() => ({ items: [] })),
  ])
    .then(([refT, pastJson]) => {
      daofaDataReady = true;
      daofaSectionsCache = parseDaofaSections(refT);
      const items = pastJson && Array.isArray(pastJson.items) ? pastJson.items : [];
      daofaPastItemsCache = items.map((it) => ({
        title: it.title || "",
        body: it.body || "",
      }));
      daofaPastLabel = (pastJson && pastJson.label) || "";
      renderDaofaList();
      renderDaofaPastList();
      showDaofaSub(getDaofaSub());
    })
    .catch(() => {
      daofaDataReady = true;
      daofaSectionsCache = [];
      daofaPastItemsCache = [];
      daofaPastLabel = "";
      renderDaofaList();
      renderDaofaPastList();
      showDaofaSub(getDaofaSub());
    });
}

function renderDaofaList() {
  const listEl = $("daofa-stack-list");
  if (!listEl) return;
  const sections = daofaSectionsCache;
  if (!sections.length) {
    listEl.innerHTML = "<p class=\"muted\">暂无主观题条目。</p>";
    return;
  }
  const parts = [];
  for (let idx = 0; idx < sections.length; idx++) {
    const sec = sections[idx];
    parts.push(
      `<div class="essay-row-card">` +
        `<button type="button" class="essay-row-btn daofa-open-btn" data-daofa-idx="${String(idx)}">` +
          `<span class="essay-row-title">${escapeHtml(sec.title)}</span><span class="muted essay-row-chevron" aria-hidden="true">›</span>` +
        `</button>` +
      `</div>`
    );
  }
  listEl.innerHTML = parts.join("");
  listEl.querySelectorAll(".daofa-open-btn").forEach((btn) => {
    btn.addEventListener("click", () => {
      const idx = parseInt(btn.dataset.daofaIdx || "0", 10);
      openDaofaDetail(idx);
    });
  });
}

function openDaofaDetail(idx) {
  const sec = daofaSectionsCache[idx];
  if (!sec) return;
  const listEl = $("daofa-stack-list");
  const detailEl = $("daofa-stack-detail");
  if (!listEl || !detailEl) return;
  listEl.classList.add("hidden");
  detailEl.classList.remove("hidden");
  const bodyHtml = escapeHtml(sec.body).replace(/\n/g, "<br />");
  detailEl.innerHTML =
    `<div class="essay-detail-panel">` +
    `<button type="button" class="btn-secondary essay-detail-back">← 返回</button>` +
    `<h2 class="section-title essay-detail-sample-title">${escapeHtml(sec.title)}</h2>` +
    `<div class="card settings-block mt-8">` +
    `<h3 class="essay-block-label">正文</h3>` +
    `<p class="essay-body-text">${bodyHtml}</p>` +
    `</div></div>`;
  detailEl.querySelector(".essay-detail-back")?.addEventListener("click", closeDaofaDetail);
}

function closeDaofaDetail() {
  const listEl = $("daofa-stack-list");
  const detailEl = $("daofa-stack-detail");
  if (!listEl || !detailEl) return;
  listEl.classList.remove("hidden");
  detailEl.classList.add("hidden");
  detailEl.innerHTML = "";
}

function renderDaofaPastList() {
  const listEl = $("daofa-past-stack-list");
  if (!listEl) return;
  const sections = daofaPastItemsCache;
  if (!sections.length) {
    listEl.innerHTML = "<p class=\"muted\">暂无历年中考题数据。</p>";
    return;
  }
  const hint =
    daofaPastLabel && String(daofaPastLabel).trim()
      ? `<p class="hint muted">${escapeHtml(String(daofaPastLabel).trim())}</p>`
      : `<p class="hint muted">按条目浏览历年真题概览，点击查看全文（与主观题相同版式）。</p>`;
  const parts = [];
  for (let idx = 0; idx < sections.length; idx++) {
    const sec = sections[idx];
    parts.push(
      `<div class="essay-row-card">` +
        `<button type="button" class="essay-row-btn daofa-past-open-btn" data-daofa-past-idx="${String(idx)}">` +
          `<span class="essay-row-title">${escapeHtml(sec.title)}</span><span class="muted essay-row-chevron" aria-hidden="true">›</span>` +
        `</button>` +
      `</div>`
    );
  }
  listEl.innerHTML = hint + parts.join("");
  listEl.querySelectorAll(".daofa-past-open-btn").forEach((btn) => {
    btn.addEventListener("click", () => {
      const idx = parseInt(btn.dataset.daofaPastIdx || "0", 10);
      openDaofaPastDetail(idx);
    });
  });
}

function openDaofaPastDetail(idx) {
  const sec = daofaPastItemsCache[idx];
  if (!sec) return;
  const listEl = $("daofa-past-stack-list");
  const detailEl = $("daofa-past-stack-detail");
  if (!listEl || !detailEl) return;
  listEl.classList.add("hidden");
  detailEl.classList.remove("hidden");
  const bodyHtml = escapeHtml(sec.body).replace(/\n/g, "<br />");
  detailEl.innerHTML =
    `<div class="essay-detail-panel">` +
    `<button type="button" class="btn-secondary essay-detail-back">← 返回</button>` +
    `<h2 class="section-title essay-detail-sample-title">${escapeHtml(sec.title)}</h2>` +
    `<div class="card settings-block mt-8">` +
    `<h3 class="essay-block-label">正文</h3>` +
    `<p class="essay-body-text">${bodyHtml}</p>` +
    `</div></div>`;
  detailEl.querySelector(".essay-detail-back")?.addEventListener("click", closeDaofaPastDetail);
}

function closeDaofaPastDetail() {
  const listEl = $("daofa-past-stack-list");
  const detailEl = $("daofa-past-stack-detail");
  if (!listEl || !detailEl) return;
  listEl.classList.remove("hidden");
  detailEl.classList.add("hidden");
  detailEl.innerHTML = "";
}

let chineseZkReady = false;
/** @type {{ title: string, body: string, images: string[] }[]} */
let chineseZkItemsCache = [];
/** @type {string} */
let chineseZkLabel = "";

let mathZkReady = false;
/** @type {{ title: string, body: string, images: string[] }[]} */
let mathZkItemsCache = [];
/** @type {string} */
let mathZkLabel = "";

function chineseZkDataUrl(assetPath) {
  const p = String(assetPath || "").replace(/^\/+/, "").trim();
  if (!p) return "";
  if (/^(https?:)?\/\//i.test(p) || p.startsWith("data:")) return p;
  return `data/${p}`;
}

function getChineseSub() {
  try {
    const v = localStorage.getItem(STORAGE_CHINESE_SUB);
    if (v === "zhongkao") return "zhongkao";
    if (v === "structure") return "structure";
    return "essay";
  } catch (_) {
    return "essay";
  }
}

function setChineseSub(v) {
  try {
    localStorage.setItem(STORAGE_CHINESE_SUB, v);
  } catch (_) {}
}

/** @param {"essay"|"zhongkao"|"structure"} sub */
function showChineseSub(sub) {
  let s = "essay";
  if (sub === "zhongkao") s = "zhongkao";
  else if (sub === "structure") s = "structure";
  setChineseSub(s);
  closeChineseZhongkaoDetail();
  document.querySelectorAll("#panel-chinese [data-chinese-pane]").forEach((pane) => {
    pane.classList.toggle("active", pane.dataset.chinesePane === s);
  });
  document.querySelectorAll("#panel-chinese [data-chinese-sub]").forEach((btn) => {
    btn.classList.toggle("active", btn.dataset.chineseSub === s);
  });
  if (s === "essay") {
    renderEssayList("cn");
  }
  if (s === "zhongkao") {
    renderChineseZhongkaoList();
  }
  if (s === "structure") {
    loadChineseStructureTab();
  }
}

function loadChineseTab() {
  showChineseSub(getChineseSub());
  if (chineseZkReady) return;
  fetch("data/chinese_zhongkao.json")
    .then((r) => (r.ok ? r.json() : Promise.resolve({ items: [] })))
    .catch(() => ({ items: [] }))
    .then((data) => {
      chineseZkReady = true;
      const items = data && Array.isArray(data.items) ? data.items : [];
      chineseZkItemsCache = items.map((it) => ({
        title: it.title || "",
        body: it.body || "",
        images: Array.isArray(it.images) ? it.images.filter(Boolean) : [],
      }));
      chineseZkLabel = (data && data.label) || "";
      renderChineseZhongkaoList();
    });
}

function renderChineseZhongkaoList() {
  const listEl = $("chinese-zk-stack-list");
  if (!listEl) return;
  const sections = chineseZkItemsCache;
  if (!sections.length) {
    listEl.innerHTML =
      "<p class=\"muted\">" +
      (chineseZkReady ? "暂无中考真题数据。" : "加载中…") +
      "</p>";
    return;
  }
  const hint =
    chineseZkLabel && String(chineseZkLabel).trim()
      ? `<p class="hint muted">${escapeHtml(String(chineseZkLabel).trim())}</p>`
      : `<p class="hint muted">按条目浏览 2024 年中考语文试题与答案整合，点击查看全文。</p>`;
  const parts = [];
  for (let idx = 0; idx < sections.length; idx++) {
    const sec = sections[idx];
    parts.push(
      `<div class="essay-row-card">` +
        `<button type="button" class="essay-row-btn chinese-zk-open-btn" data-chinese-zk-idx="${String(idx)}">` +
          `<span class="essay-row-title">${escapeHtml(sec.title)}</span><span class="muted essay-row-chevron" aria-hidden="true">›</span>` +
        `</button>` +
      `</div>`
    );
  }
  listEl.innerHTML = hint + parts.join("");
  listEl.querySelectorAll(".chinese-zk-open-btn").forEach((btn) => {
    btn.addEventListener("click", () => {
      const idx = parseInt(btn.dataset.chineseZkIdx || "0", 10);
      openChineseZhongkaoDetail(idx);
    });
  });
}

function openChineseZhongkaoDetail(idx) {
  const sec = chineseZkItemsCache[idx];
  if (!sec) return;
  const listEl = $("chinese-zk-stack-list");
  const detailEl = $("chinese-zk-stack-detail");
  if (!listEl || !detailEl) return;
  listEl.classList.add("hidden");
  detailEl.classList.remove("hidden");
  const bodyHtml = escapeHtml(sec.body).replace(/\n/g, "<br />");
  const imgs = Array.isArray(sec.images) ? sec.images : [];
  const imgsHtml =
    imgs.length > 0
      ? `<div class="math-exam-images">` +
        imgs
          .map((p) => {
            const src = chineseZkDataUrl(p);
            if (!src) return "";
            return `<figure class="math-exam-figure"><img src="${escapeAttr(src)}" alt="" loading="lazy" decoding="async" /></figure>`;
          })
          .join("") +
        `</div>`
      : "";
  const hasBody = sec.body && String(sec.body).trim();
  const textBlock = hasBody
    ? `<h3 class="essay-block-label">正文</h3><p class="essay-body-text">${bodyHtml}</p>`
    : "";
  const imgBlock =
    imgs.length > 0
      ? `<h3 class="essay-block-label">试卷图（点击放大）</h3>${imgsHtml}`
      : "";
  detailEl.innerHTML =
    `<div class="essay-detail-panel">` +
    `<button type="button" class="btn-secondary essay-detail-back">← 返回</button>` +
    `<h2 class="section-title essay-detail-sample-title">${escapeHtml(sec.title)}</h2>` +
    `<div class="card settings-block mt-8">` +
    textBlock +
    imgBlock +
    `</div></div>`;
  detailEl.querySelector(".essay-detail-back")?.addEventListener("click", closeChineseZhongkaoDetail);
  detailEl.querySelectorAll(".math-exam-images img").forEach((img) => {
    img.addEventListener("click", () => openChineseExamLightbox(img.getAttribute("src") || ""));
  });
}

function openChineseExamLightbox(src) {
  const s = String(src || "").trim();
  if (!s) return;
  const box = $("chinese-exam-lightbox");
  const el = $("chinese-exam-lightbox-img");
  if (!box || !el) return;
  el.src = s;
  el.style.transform = "";
  box.classList.remove("hidden");
  try {
    document.body.style.overflow = "hidden";
  } catch (_) {}
}

function closeChineseExamLightbox() {
  const box = $("chinese-exam-lightbox");
  const el = $("chinese-exam-lightbox-img");
  if (box) box.classList.add("hidden");
  if (el) el.removeAttribute("src");
  try {
    document.body.style.overflow = "";
  } catch (_) {}
}

function initChineseExamLightbox() {
  const box = $("chinese-exam-lightbox");
  const btn = box?.querySelector(".math-exam-lightbox-close");
  const img = $("chinese-exam-lightbox-img");
  if (!box || !img) return;
  btn?.addEventListener("click", (e) => {
    e.stopPropagation();
    closeChineseExamLightbox();
  });
  box.addEventListener("click", (e) => {
    if (e.target === box) closeChineseExamLightbox();
  });
  img.addEventListener("click", (e) => e.stopPropagation());
  document.addEventListener("keydown", (e) => {
    if (e.key === "Escape" && box && !box.classList.contains("hidden")) {
      closeChineseExamLightbox();
    }
  });
}

function closeChineseZhongkaoDetail() {
  closeChineseExamLightbox();
  const listEl = $("chinese-zk-stack-list");
  const detailEl = $("chinese-zk-stack-detail");
  if (!listEl || !detailEl) return;
  listEl.classList.remove("hidden");
  detailEl.classList.add("hidden");
  detailEl.innerHTML = "";
}

function showTab(name) {
  if (name !== "english") {
    closeCameraModal();
  }
  if (name !== "chinese" && name !== "english") {
    closeEssayDetail();
  }
  if (name !== "chinese") {
    closeChineseZhongkaoDetail();
  }
  if (name !== "math") {
    closeMathZhongkaoDetail();
  }
  if (name !== "daofa") {
    closeDaofaDetail();
    closeDaofaPastDetail();
  }
  document.querySelectorAll(".panel").forEach((p) => p.classList.remove("active"));
  const panel = $(`panel-${name}`);
  if (panel) panel.classList.add("active");
  document.querySelectorAll(".nav-item").forEach((n) => {
    n.classList.toggle("active", n.dataset.tab === name);
  });
  $("bottom-nav").classList.toggle("hidden", name === "result" || name === "pe-sports");
  $("btn-result-back").classList.toggle("hidden", name !== "result");
  if (name === "result") {
    $("app-title").textContent = "识别结果";
  } else {
    $("app-title").textContent = TAB_HEADER_TITLES[name] || "中考应试";
  }
  if (name === "chinese") {
    closeEssayDetail("en");
    setEssaySubject("chinese");
    loadChineseTab();
  }
  if (name === "english") {
    closeEssayDetail("cn");
    showEnglishSub(getEnglishSub());
  }
  if (name === "math") {
    loadMathTabNav();
  }
  if (name === "physics") {
    loadPhysicsTab();
  }
  if (name === "daofa") {
    loadDaofaTab();
  }
  if (name === "pe-sports") {
    loadPeSportsTab();
  }
}

function processText(text) {
  const vocabularySet = getVocabularySet();
  const unknown = [];
  const re = /\b[a-zA-Z]+\b/g;
  let m;
  while ((m = re.exec(text)) !== null) {
    const raw = m[0];
    const lower = raw.toLowerCase();
    const base = resolveInVocabulary(lower);
    if (base == null && lower.length > 2) unknown.push(lower);
  }
  return [...new Set(unknown)];
}

function buildResultHtml(text, unknownSet) {
  const parts = [];
  const re = /(\b[a-zA-Z]+\b|[^a-zA-Z]+)/g;
  let m;
  while ((m = re.exec(text)) !== null) {
    const token = m[0];
    if (/^[a-zA-Z]+$/.test(token)) {
      const lower = token.toLowerCase();
      if (unknownSet.has(lower) && lower.length > 2) {
        parts.push(
          `<span class="unknown" data-word="${escapeAttr(lower)}">${escapeHtml(token)}</span>`
        );
      } else {
        parts.push(escapeHtml(token));
      }
    } else {
      parts.push(escapeHtml(token));
    }
  }
  return parts.join("");
}

function escapeHtml(s) {
  return s
    .replace(/&/g, "&amp;")
    .replace(/</g, "&lt;")
    .replace(/>/g, "&gt;")
    .replace(/"/g, "&quot;");
}

function escapeAttr(s) {
  return escapeHtml(s).replace(/'/g, "&#39;");
}

/**
 * @param {{ definitions?: { partOfSpeech?: string, meaning?: string }[] }} rec
 */
function formatWordMeaningsCn(rec) {
  if (!rec.definitions?.length) return "";
  return rec.definitions
    .map((d) => {
      const pos = (d.partOfSpeech || "").trim();
      const m = (d.meaning || "").trim();
      if (!m) return "";
      return pos ? `${pos} ${m}` : m;
    })
    .filter(Boolean)
    .join(" · ");
}

const EXAMPLE_CACHE_PREFIX = "dictex:";
const TRANSLATE_CACHE_PREFIX = "trm:";

function hashExampleKey(s) {
  let h = 0;
  for (let i = 0; i < s.length; i += 1) {
    h = (Math.imul(31, h) + s.charCodeAt(i)) | 0;
  }
  return String(h);
}

/**
 * @param {string} en
 * @returns {Promise<string | null>}
 */
async function translateEnToZh(en) {
  const t = en.trim();
  if (!t) return null;
  const cacheKey = TRANSLATE_CACHE_PREFIX + hashExampleKey(t);
  try {
    const hit = sessionStorage.getItem(cacheKey);
    if (hit) return hit;
  } catch {
    /* ignore */
  }
  try {
    const url = `https://api.mymemory.translated.net/get?q=${encodeURIComponent(t)}&langpair=en|zh-CN`;
    const r = await fetch(url);
    const data = await r.json();
    const zh = data.responseData?.translatedText;
    if (typeof zh === "string" && zh.trim()) {
      const out = zh.trim();
      try {
        sessionStorage.setItem(cacheKey, out);
      } catch {
        /* ignore */
      }
      return out;
    }
  } catch {
    /* ignore */
  }
  return null;
}

/**
 * @param {unknown} data
 * @param {string} word
 * @returns {string | null}
 */
function extractExampleFromApiPayload(data, word) {
  if (!Array.isArray(data)) return null;
  const candidates = [];
  for (const entry of data) {
    for (const m of entry.meanings || []) {
      for (const d of m.definitions || []) {
        const ex = d.example;
        if (typeof ex === "string" && ex.trim()) candidates.push(ex.trim());
      }
    }
  }
  if (candidates.length === 0) return null;
  const esc = word.replace(/[.*+?^${}()|[\]\\]/g, "\\$&");
  const boundary = new RegExp(`\\b${esc}\\b`, "i");
  for (const ex of candidates) {
    if (boundary.test(ex)) return ex;
  }
  return candidates[0];
}

/**
 * @param {string} word
 * @returns {Promise<string | null>}
 */
async function fetchExampleForWord(word) {
  const w = word.toLowerCase().trim();
  const key = EXAMPLE_CACHE_PREFIX + w;
  try {
    const cached = sessionStorage.getItem(key);
    if (cached === "__none__") return null;
    if (cached) return cached;
  } catch {
    /* ignore */
  }

  const slugs = [w];
  if (w.includes(" ")) slugs.push(w.split(/\s+/)[0]);

  for (const slug of slugs) {
    try {
      const url = `https://api.dictionaryapi.dev/api/v2/entries/en/${encodeURIComponent(slug)}`;
      const r = await fetch(url);
      if (!r.ok) continue;
      const data = await r.json();
      const ex = extractExampleFromApiPayload(data, slug);
      if (ex) {
        const out = /[.!?]$/.test(ex) ? ex : `${ex}.`;
        try {
          sessionStorage.setItem(key, out);
        } catch {
          /* ignore */
        }
        return out;
      }
    } catch {
      /* try next slug */
    }
  }
  try {
    sessionStorage.setItem(key, "__none__");
  } catch {
    /* ignore */
  }
  return null;
}

function calculateAccuracy(text, unknownCount) {
  const words = text.trim().split(/\s+/).filter(Boolean);
  const total = words.length;
  if (total === 0) return 100;
  const known = total - unknownCount;
  return Math.max(85, Math.round((known * 100) / total));
}

function renderVocabList() {
  const q = $("vocab-search").value;
  const list = listWordsForHome(filterType, q);
  const total = list.length;
  const totalInDb = getWordCount();
  const totalPages = Math.max(1, Math.ceil(total / VOCAB_PAGE_SIZE));
  if (vocabPage > totalPages) vocabPage = totalPages;
  if (vocabPage < 1) vocabPage = 1;
  const page = vocabPage;
  const start = (page - 1) * VOCAB_PAGE_SIZE;
  const pageItems = list.slice(start, start + VOCAB_PAGE_SIZE);

  $("vocab-count").textContent = `当前筛选共 ${total} 个单词 · 词库一共 ${totalInDb} 个 · 每页 ${VOCAB_PAGE_SIZE} 个 · 第 ${page} / ${totalPages} 页`;
  const ul = $("vocab-list");
  ul.innerHTML = "";
  for (const rec of pageItems) {
    const li = document.createElement("li");
    const typeLabel = rec.type === "ADVANCED" ? "超纲" : "中考";
    const cn = formatWordMeaningsCn(rec);
    const cnBlock = cn
      ? `<div class="word-meaning-cn">${escapeHtml(cn)}</div>`
      : `<div class="word-meaning-cn muted">暂无释义</div>`;
    const ph = (rec.phonetic || "").trim();
    const metaLine = ph ? `${typeLabel} · ${escapeHtml(ph)}` : typeLabel;
    li.innerHTML = `<div class="vocab-row-head"><span class="word-line">${escapeHtml(rec.word)}</span><button type="button" class="btn-nb-plus" aria-label="加入生词本">+</button></div>${cnBlock}<div class="word-meta">${metaLine}</div>`;
    li.addEventListener("click", (e) => {
      if (e.target.closest(".btn-nb-plus")) return;
      openWordModal(rec.word, "list");
    });
    li.querySelector(".btn-nb-plus")?.addEventListener("click", (e) => {
      e.stopPropagation();
      addToNotebook(rec.word);
      renderNotebook();
    });
    ul.appendChild(li);
  }

  const pag = $("vocab-pagination");
  const btnPrev = $("vocab-page-prev");
  const btnNext = $("vocab-page-next");
  const info = $("vocab-page-info");
  if (totalPages <= 1) {
    pag.hidden = true;
  } else {
    pag.hidden = false;
    info.textContent = `${page} / ${totalPages}`;
    btnPrev.disabled = page <= 1;
    btnNext.disabled = page >= totalPages;
  }
}

function syncModalNotebookUi(w) {
  const c = notebookCount(w);
  $("modal-note").textContent = c > 0 ? `已记 ${c} 次` : "";
  $("modal-add-notebook").textContent = c === 0 ? "加入生词本" : `再记一次（已 ${c} 次）`;
  $("modal-add-notebook").disabled = false;
}

function renderNotebook() {
  const ul = $("notebook-list");
  const entries = loadNotebookSortedEntries();
  const totalAdds = entries.reduce((s, [, c]) => s + c, 0);
  const nh = $("notebook-heading");
  if (nh) nh.textContent = `生词本（${entries.length} 个词 · 累计 ${totalAdds} 次）`;
  ul.innerHTML = "";
  if (entries.length === 0) {
    ul.innerHTML = '<li class="muted">生词本为空</li>';
    return;
  }
  for (const [w, c] of entries) {
    const li = document.createElement("li");
    li.innerHTML = `<span class="nb-main"><span>${escapeHtml(w)}</span><span class="nb-count">${c}次</span></span><button type="button" data-remove="${escapeAttr(w)}">删除</button>`;
    li.querySelector("button")?.addEventListener("click", (e) => {
      e.stopPropagation();
      removeFromNotebook(w);
      renderNotebook();
    });
    li.addEventListener("click", () => openWordModal(w, "notebook"));
    ul.appendChild(li);
  }
}

function openWordModal(word, context) {
  const w = word.toLowerCase();
  $("modal-word-title").textContent = w;
  const rec = getResolvedWordRecord(w);
  const ph = (rec?.phonetic || "").trim();
  const phEl = $("modal-phonetic");
  if (phEl) {
    phEl.textContent = ph || "—";
    phEl.classList.toggle("phonetic-empty", !ph);
  }
  const defBox = $("modal-definitions");
  defBox.innerHTML = "";
  if (rec && rec.definitions?.length) {
    for (const d of rec.definitions) {
      const pos = (d.partOfSpeech || "").trim();
      const row = document.createElement("div");
      row.className = "def-row";
      row.innerHTML = pos
        ? `<span class="def-pos">${escapeHtml(pos)}</span><span class="def-meaning">${escapeHtml(d.meaning)}</span>`
        : `<span class="def-meaning">${escapeHtml(d.meaning)}</span>`;
      defBox.appendChild(row);
    }
  } else {
    const row = document.createElement("div");
    row.className = "def-row";
    row.innerHTML = `<span class="def-meaning">当前内置词库中暂无该词详细释义，可加入生词本以便复习。</span>`;
    defBox.appendChild(row);
  }

  const exEn = $("modal-example-en");
  const exZh = $("modal-example-zh");

  function modalWordMatches() {
    return $("modal-word-title").textContent.trim().toLowerCase() === w;
  }

  function showExampleWithTranslation(enText, presetZh) {
    if (!modalWordMatches()) return;
    const en = enText.trim();
    if (!en) {
      exEn.textContent = "暂无例句（可检查网络后重试）";
      exZh.textContent = "";
      exEn.classList.add("muted-empty");
      exZh.classList.remove("loading");
      return;
    }
    exEn.textContent = en;
    exEn.classList.remove("muted-empty");
    const fixedZh = presetZh?.trim();
    if (fixedZh) {
      exZh.textContent = `译文：${fixedZh}`;
      exZh.classList.remove("loading");
      return;
    }
    exZh.textContent = "译文加载中…";
    exZh.classList.add("loading");
    translateEnToZh(en).then((zh) => {
      if (!modalWordMatches()) return;
      exZh.classList.remove("loading");
      exZh.textContent = zh ? `译文：${zh}` : "（译文暂不可用，请稍后重试）";
    });
  }

  const presetEx = rec?.example?.trim() || "";
  const presetZh = rec?.exampleZh?.trim() || "";
  if (presetEx) {
    showExampleWithTranslation(presetEx, presetZh || undefined);
  } else {
    exEn.textContent = "正在加载例句…";
    exZh.textContent = "";
    exEn.classList.add("muted-empty");
    exZh.classList.remove("loading");
    fetchExampleForWord(w).then((ex) => {
      showExampleWithTranslation(ex || "", undefined);
    });
  }

  syncModalNotebookUi(w);

  const showNav = context === "result" && unknownWordsList.length > 0;
  const idx = unknownWordsList.indexOf(w);
  $("modal-prev").classList.toggle("hidden", !showNav || idx <= 0);
  $("modal-next").classList.toggle(
    "hidden",
    !showNav || idx < 0 || idx >= unknownWordsList.length - 1
  );

  $("modal-overlay").classList.remove("hidden");
}

function closeWordModal() {
  $("modal-overlay").classList.add("hidden");
}

function closeCameraModal() {
  const video = $("camera-video");
  if (cameraStream) {
    cameraStream.getTracks().forEach((t) => t.stop());
    cameraStream = null;
  }
  video.srcObject = null;
  $("camera-overlay").classList.add("hidden");
}

async function openCameraModal() {
  if (!navigator.mediaDevices?.getUserMedia) {
    $("ocr-status").textContent =
      "当前浏览器无法使用摄像头（请使用 HTTPS、localhost，或换用现代浏览器）。";
    return;
  }
  closeCameraModal();
  const video = $("camera-video");
  const overlay = $("camera-overlay");
  try {
    cameraStream = await navigator.mediaDevices.getUserMedia({
      video: {
        facingMode: { ideal: "environment" },
        width: { ideal: 1920 },
        height: { ideal: 1080 },
      },
      audio: false,
    });
    video.srcObject = cameraStream;
    overlay.classList.remove("hidden");
  } catch (e) {
    console.error(e);
    let msg = "无法打开摄像头，请检查权限或换用「选择图片」。";
    if (e instanceof DOMException) {
      if (e.name === "NotAllowedError" || e.name === "SecurityError") {
        msg = "已拒绝摄像头权限，请在地址栏或系统设置中允许访问摄像头。";
      } else if (e.name === "NotFoundError") {
        msg = "未检测到摄像头设备。";
      }
    }
    $("ocr-status").textContent = msg;
  }
}

function captureFromCamera() {
  const video = $("camera-video");
  const canvas = $("camera-canvas");
  const w = video.videoWidth;
  const h = video.videoHeight;
  if (!w || !h) {
    $("ocr-status").textContent = "画面未就绪，请稍候再点拍照。";
    return;
  }
  canvas.width = w;
  canvas.height = h;
  const ctx = canvas.getContext("2d");
  if (!ctx) {
    $("ocr-status").textContent = "无法捕获画面，请重试。";
    return;
  }
  ctx.drawImage(video, 0, 0, w, h);
  canvas.toBlob(
    (blob) => {
      if (!blob) {
        $("ocr-status").textContent = "无法生成图片，请重试。";
        return;
      }
      closeCameraModal();
      showTab("english");
      showEnglishSub("scan");
      runOcr(blob);
    },
    "image/jpeg",
    0.92
  );
}

/**
 * @param {Blob | File} imageBlobOrFile
 */
async function runOcr(imageBlobOrFile) {
  const status = $("ocr-status");
  status.hidden = false;
  status.textContent = "准备识别…";
  try {
    const t = await transcribeImageWithLlm(imageBlobOrFile, {
      setStatus: (s) => {
        status.textContent = s;
      },
    });
    $("scan-text").value = $("scan-text").value ? `${$("scan-text").value.trim()}\n\n${t}` : t;
    status.textContent = t ? "识别完成。" : "未识别到英文文本，可换图或手动粘贴。";
    if (t) applyReadingHeuristicFromText($("scan-text").value);
    status.scrollIntoView({ block: "nearest", behavior: "smooth" });
  } catch (e) {
    console.error(e);
    if (e instanceof TypeError && String(e.message).includes("fetch")) {
      status.textContent =
        "识图请求失败（网络或跨域）：请确认已用 http://localhost 或 http://127.0.0.1 打开页面；若仍失败，多为接口未允许浏览器跨域，需网关配置 CORS 或由后端代理。";
    } else {
      status.textContent = `识图失败：${e && e.message ? e.message : "请检查网络、鉴权与模型是否支持图片输入"}`;
    }
    status.scrollIntoView({ block: "nearest", behavior: "smooth" });
  }
}

function wireEvents() {
  $("vocab-search").addEventListener("input", () => {
    vocabPage = 1;
    renderVocabList();
  });
  $("btn-filter").addEventListener("click", () => $("dialog-filter").classList.remove("hidden"));
  $("btn-filter-ok").addEventListener("click", () => {
    const sel = document.querySelector('input[name="filter-type"]:checked');
    filterType = sel ? sel.value : "ALL";
    $("dialog-filter").classList.add("hidden");
    vocabPage = 1;
    renderVocabList();
  });

  $("vocab-page-prev").addEventListener("click", () => {
    if (vocabPage > 1) {
      vocabPage -= 1;
      renderVocabList();
      document.querySelector(".main")?.scrollTo({ top: 0, behavior: "smooth" });
    }
  });
  $("vocab-page-next").addEventListener("click", () => {
    const q = $("vocab-search").value;
    const list = listWordsForHome(filterType, q);
    const totalPages = Math.max(1, Math.ceil(list.length / VOCAB_PAGE_SIZE));
    if (vocabPage < totalPages) {
      vocabPage += 1;
      renderVocabList();
      document.querySelector(".main")?.scrollTo({ top: 0, behavior: "smooth" });
    }
  });

  document.querySelectorAll(".nav-item").forEach((btn) => {
    btn.addEventListener("click", () => showTab(btn.dataset.tab || "chinese"));
  });

  $("btn-open-pe-sports")?.addEventListener("click", () => showTab("pe-sports"));
  $("btn-pe-sports-back")?.addEventListener("click", () => showTab("profile"));

  document.querySelectorAll("#panel-english [data-english-sub]").forEach((btn) => {
    btn.addEventListener("click", () => {
      showEnglishSub(/** @type {HTMLElement} */ (btn).dataset.englishSub || "vocab");
    });
  });

  document.querySelectorAll("#panel-daofa [data-daofa-sub]").forEach((btn) => {
    btn.addEventListener("click", () => {
      showDaofaSub(/** @type {HTMLElement} */ (btn).dataset.daofaSub || "structure");
    });
  });

  document.querySelectorAll("#panel-chinese [data-chinese-sub]").forEach((btn) => {
    btn.addEventListener("click", () => {
      showChineseSub(/** @type {HTMLElement} */ (btn).dataset.chineseSub || "essay");
    });
  });

  document.querySelectorAll("#panel-math [data-math-sub]").forEach((btn) => {
    btn.addEventListener("click", () => {
      showMathSub(/** @type {HTMLElement} */ (btn).dataset.mathSub || "structure");
    });
  });

  $("scan-file").addEventListener("change", (e) => {
    const input = e.target;
    const f = input.files?.[0];
    if (f) {
      showTab("english");
      showEnglishSub("scan");
      const st = $("ocr-status");
      st.hidden = false;
      st.textContent = `已选择「${f.name}」，开始识别…`;
      runOcr(f);
    }
    input.value = "";
  });

  $("btn-camera-open").addEventListener("click", () => {
    openCameraModal();
  });
  $("camera-close").addEventListener("click", closeCameraModal);
  $("camera-cancel").addEventListener("click", closeCameraModal);
  $("camera-capture").addEventListener("click", () => captureFromCamera());
  $("camera-overlay").addEventListener("click", (e) => {
    if (e.target === $("camera-overlay")) closeCameraModal();
  });

  $("btn-reading-ai").addEventListener("click", () => {
    runAiReadingAnalysis();
  });

  $("btn-ai-save").addEventListener("click", () => {
    try {
      localStorage.setItem(STORAGE_AI_MODEL, $("ai-model").value.trim());
      localStorage.setItem(STORAGE_AI_OCR_MODEL, $("ai-ocr-model").value.trim());
      alert("已保存模型设置");
    } catch (e) {
      console.error(e);
      alert("保存失败");
    }
  });

  $("btn-analyze").addEventListener("click", () => {
    const text = $("scan-text").value.trim();
    if (!text) {
      $("ocr-status").textContent = "请先输入或识别英文文本。";
      return;
    }
    applyReadingHeuristicFromText(text);
    recognizedText = text;
    unknownWordsList = processText(text);
    const unknownSet = new Set(unknownWordsList);
    $("result-text").innerHTML = buildResultHtml(text, unknownSet);
    $("stat-unknown").textContent = `生词数量：${unknownWordsList.length} 个`;
    $("stat-accuracy").textContent = `识别准确率：${calculateAccuracy(text, unknownWordsList.length)}%`;
    $("result-text").onclick = (ev) => {
      const t = ev.target;
      if (t instanceof HTMLElement && t.classList.contains("unknown")) {
        const w = t.dataset.word;
        if (w) openWordModal(w, "result");
      }
    };
    showTab("result");
  });

  $("btn-result-back").addEventListener("click", () => {
    showTab("english");
    showEnglishSub("scan");
  });
  $("btn-rescan").addEventListener("click", () => {
    showTab("english");
    showEnglishSub("scan");
  });
  $("btn-query-all").addEventListener("click", () => {
    if (unknownWordsList.length === 0) {
      alert("没有发现生词");
      return;
    }
    openWordModal(unknownWordsList[0], "result");
  });

  $("modal-close").addEventListener("click", closeWordModal);
  $("modal-overlay").addEventListener("click", (e) => {
    if (e.target === $("modal-overlay")) closeWordModal();
  });

  $("modal-speak")?.addEventListener("click", () => {
    const w = $("modal-word-title")?.textContent?.trim();
    if (!w || typeof speechSynthesis === "undefined") return;
    speechSynthesis.cancel();
    const u = new SpeechSynthesisUtterance(w);
    u.lang = "en-US";
    speechSynthesis.speak(u);
  });

  $("modal-add-notebook").addEventListener("click", () => {
    const w = $("modal-word-title").textContent.trim().toLowerCase();
    if (!w) return;
    addToNotebook(w);
    syncModalNotebookUi(w);
    renderNotebook();
  });

  $("modal-prev").addEventListener("click", () => {
    const w = $("modal-word-title").textContent.trim().toLowerCase();
    const i = unknownWordsList.indexOf(w);
    if (i > 0) openWordModal(unknownWordsList[i - 1], "result");
  });

  $("modal-next").addEventListener("click", () => {
    const w = $("modal-word-title").textContent.trim().toLowerCase();
    const i = unknownWordsList.indexOf(w);
    if (i >= 0 && i < unknownWordsList.length - 1) openWordModal(unknownWordsList[i + 1], "result");
  });

  $("btn-notebook-review").addEventListener("click", () => {
    const keys = Object.keys(loadNotebookMap());
    if (keys.length === 0) {
      alert("生词本为空");
      return;
    }
    const w = keys[Math.floor(Math.random() * keys.length)];
    openWordModal(w, "notebook");
  });

  $("btn-notebook-clear").addEventListener("click", () => {
    if (confirm("确定清空生词本？")) {
      saveNotebookMap({});
      renderNotebook();
    }
  });

  $("btn-clear-cache").addEventListener("click", () => {
    if (confirm("确定清理缓存？（不影响生词本与 AI 设置）")) {
      try {
        const nb = localStorage.getItem(STORAGE_NOTEBOOK);
        const font = localStorage.getItem(STORAGE_FONT);
        const aiModel = localStorage.getItem(STORAGE_AI_MODEL);
        const aiOcrModel = localStorage.getItem(STORAGE_AI_OCR_MODEL);
        localStorage.clear();
        if (nb) localStorage.setItem(STORAGE_NOTEBOOK, nb);
        if (font) localStorage.setItem(STORAGE_FONT, font);
        if (aiModel) localStorage.setItem(STORAGE_AI_MODEL, aiModel);
        if (aiOcrModel) localStorage.setItem(STORAGE_AI_OCR_MODEL, aiOcrModel);
      } catch (_) {}
      alert("清理完成");
    }
  });

  initMathExamLightbox();
  initChineseExamLightbox();
}

function getEssaySubject() {
  try {
    return localStorage.getItem(STORAGE_ESSAY_SUBJECT) || "english";
  } catch (_) {
    return "english";
  }
}

function setEssaySubject(v) {
  try {
    localStorage.setItem(STORAGE_ESSAY_SUBJECT, v);
  } catch (_) {}
}

function getReadingSubject() {
  try {
    return localStorage.getItem(STORAGE_READING_SUBJECT) || "english";
  } catch (_) {
    return "english";
  }
}

function setReadingSubject(v) {
  try {
    localStorage.setItem(STORAGE_READING_SUBJECT, v);
  } catch (_) {}
}

function renderReadingMaterials() {
  const root = $("reading-materials-root");
  if (!root) return;
  const data = window.READING_DATA;
  if (!data?.subjects?.length) {
    root.innerHTML =
      '<p class="muted">暂无阅读材料。请确认已加载 <code>data/reading-data.js</code>（与 iOS/Android 中 <code>reading_content.json</code> 同源）。</p>';
    return;
  }
  const subjects = data.subjects;
  let subjVal = getReadingSubject();
  if (!subjects.some((s) => s.id === subjVal)) {
    subjVal = subjects[0].id;
  }
  const current = subjects.find((s) => s.id === subjVal) || subjects[0];
  const parts = [];
  parts.push('<h2 class="section-title">阅读材料</h2>');
  parts.push('<p class="muted small">分学科收录真题与节选</p>');
  parts.push(
    '<div class="essay-subject-bar reading-subject-bar"><label class="essay-subject-label" for="reading-subject">学科</label>' +
      '<select id="reading-subject" class="essay-subject-select" aria-label="选择学科">'
  );
  for (const s of subjects) {
    parts.push(
      `<option value="${escapeAttr(s.id)}"${s.id === subjVal ? " selected" : ""}>${escapeHtml(s.label)}</option>`
    );
  }
  parts.push("</select></div>");

  if (!current.packs?.length) {
    parts.push('<p class="muted">该学科阅读材料将陆续补充。</p>');
  } else {
    for (const pack of current.packs) {
      parts.push('<div class="reading-pack">');
      parts.push(`<div class="reading-pack-title">📚 ${escapeHtml(pack.title)}</div>`);
      for (const sec of pack.sections || []) {
        parts.push('<div class="reading-section-block">');
        parts.push(`<div class="reading-section-headline">${escapeHtml(sec.headline)}</div>`);
        parts.push(
          `<div class="reading-section-body">${escapeHtml(sec.body || "").replace(/\n/g, "<br />")}</div>`
        );
        parts.push("</div>");
      }
      if (pack.footer) {
        parts.push(
          `<div class="reading-footer-text muted small">${escapeHtml(pack.footer).replace(/\n/g, "<br />")}</div>`
        );
      }
      parts.push("</div>");
    }
  }
  root.innerHTML = parts.join("");
  const sel = $("reading-subject");
  if (sel) {
    sel.addEventListener("change", () => {
      setReadingSubject(sel.value);
      renderReadingMaterials();
    });
  }
}

function getEssaySample(examId, sampleId) {
  const data = window.ESSAYS_DATA;
  const exam = data?.exams?.find((e) => e.id === examId);
  const sample = exam?.samples?.find((s) => s.id === sampleId);
  return { exam, sample };
}

/** English-only text for TTS when body includes 【中文翻译】 / 【写作要点】 blocks. */
function essayTtsSourceText(body) {
  const s = String(body || "").trim();
  const marker = "【中文翻译】";
  let t = s.includes(marker) ? s.split(marker)[0].trim() : s;
  const enLabel = "【英文范文】";
  if (t.startsWith(enLabel)) {
    t = t.slice(enLabel.length).trim();
  }
  t = t.replace(/\n*（全文约\d+字）\s*$/u, "").trim();
  return t;
}

function formatAudioTime(sec) {
  const v = Number.isFinite(sec) ? Math.max(0, Math.floor(sec)) : 0;
  const m = Math.floor(v / 60);
  const s = v % 60;
  return `${m}:${String(s).padStart(2, "0")}`;
}

async function requestEssayTtsAudio(text) {
  const res = await fetch(`${aiBase()}/essay-tts`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({
      model: "gemini-2.5-flash-tts",
      voiceName: DEFAULT_ESSAY_TTS_VOICE,
      text,
    }),
  });
  if (!res.ok) {
    const msg = await res.text();
    throw new Error(msg || `HTTP ${res.status}`);
  }
  return await res.blob();
}

function getEssayDetailPlaybackRate(detailRoot) {
  const el =
    detailRoot && typeof detailRoot.querySelector === "function"
      ? detailRoot.querySelector(".essay-tts-speed")
      : null;
  const fallback = document.querySelector(".essay-stack:not(.hidden) .essay-tts-speed");
  const node = el || fallback;
  if (!node) return 1;
  const v = parseFloat(node.value, 10);
  return Number.isFinite(v) ? v : 1;
}

function updateEssayTtsUi(sampleId) {
  const root = document.querySelector(`[data-tts-for="${CSS.escape(sampleId)}"]`);
  if (!root) return;
  const st = essayTtsState.get(sampleId) || {};
  const btn = root.querySelector(".essay-tts-btn");
  const pg = root.querySelector(".essay-tts-progress");
  const tm = root.querySelector(".essay-tts-time");
  if (btn) {
    btn.disabled = !!st.loading;
    const label = st.loading ? "生成中…" : st.playing ? "暂停" : st.url ? "继续播放" : "播放";
    btn.textContent = label;
    btn.setAttribute("aria-label", label);
    btn.classList.toggle("essay-tts-playing", !!st.playing && !st.loading);
  }
  if (pg) {
    if (st.seeking) {
      /* 拖动中由 input 事件更新，避免与 timeupdate 争抢 */
    } else {
      const ratio = st.duration > 0 ? (st.current || 0) / st.duration : 0;
      pg.value = String(Math.max(0, Math.min(100, ratio * 100)));
    }
    pg.disabled = !st.audio || !!st.loading;
  }
  if (tm) {
    tm.textContent = `${formatAudioTime(st.current || 0)} / ${formatAudioTime(st.duration || 0)}`;
  }
}

function stopOtherEssayAudio(currentId) {
  for (const [id, st] of essayTtsState.entries()) {
    if (id === currentId || !st.audio) continue;
    st.audio.pause();
    st.playing = false;
    updateEssayTtsUi(id);
  }
}

async function toggleEssayTts(examId, sampleId, detailRoot) {
  const { sample } = getEssaySample(examId, sampleId);
  if (!sample) return;
  const st = essayTtsState.get(sampleId) || {};
  if (st.loading) return;
  if (st.audio) {
    if (st.audio.paused) {
      stopOtherEssayAudio(sampleId);
      st.audio.playbackRate = getEssayDetailPlaybackRate(detailRoot);
      await st.audio.play();
      st.playing = true;
    } else {
      st.audio.pause();
      st.playing = false;
    }
    essayTtsState.set(sampleId, st);
    updateEssayTtsUi(sampleId);
    return;
  }

  st.loading = true;
  essayTtsState.set(sampleId, st);
  updateEssayTtsUi(sampleId);
  try {
    const blob = await requestEssayTtsAudio(essayTtsSourceText(sample.body));
    const url = URL.createObjectURL(blob);
    const audio = new Audio(url);
    audio.playbackRate = getEssayDetailPlaybackRate(detailRoot);
    st.url = url;
    st.audio = audio;
    st.loading = false;
    st.current = 0;
    st.duration = 0;
    st.playing = false;
    st.seeking = false;
    audio.addEventListener("loadedmetadata", () => {
      st.duration = Number.isFinite(audio.duration) ? audio.duration : 0;
      updateEssayTtsUi(sampleId);
    });
    audio.addEventListener("timeupdate", () => {
      const cur = essayTtsState.get(sampleId);
      if (!cur || cur.seeking) return;
      cur.current = audio.currentTime || 0;
      essayTtsState.set(sampleId, cur);
      updateEssayTtsUi(sampleId);
    });
    audio.addEventListener("ended", () => {
      st.playing = false;
      st.current = st.duration || audio.duration || 0;
      updateEssayTtsUi(sampleId);
    });
    stopOtherEssayAudio(sampleId);
    await audio.play();
    st.playing = true;
    essayTtsState.set(sampleId, st);
    updateEssayTtsUi(sampleId);
  } catch (e) {
    st.loading = false;
    essayTtsState.set(sampleId, st);
    updateEssayTtsUi(sampleId);
    alert(`语音生成失败：${e && e.message ? e.message : "请检查本地代理与网络"}`);
  }
}

/** @param {"cn"|"en"} suffix */
function renderEssayList(suffix) {
  const data = window.ESSAYS_DATA;
  const listEl = $(`essay-stack-list-${suffix}`);
  if (!listEl) return;
  const subj = suffix === "cn" ? "chinese" : "english";
  if (!data?.exams?.length) {
    listEl.innerHTML =
      "<p class=\"muted\">暂无作文数据。请确认已加载 <code>data/essays-data.js</code>（与 iOS 应用包内 <code>essays.json</code> 同源）。</p>";
    return;
  }
  const exams = data.exams.filter((e) => (e.subject || "english") === subj);
  const parts = [];
  if (exams.length === 0) {
    listEl.innerHTML = "<p class=\"muted\">该科目暂无范文，请切换科目或稍后再试。</p>";
    return;
  }
  for (const exam of exams) {
    parts.push(`<div class="essay-year-badge">📚 ${escapeHtml(exam.title)}</div>`);
    for (const s of exam.samples) {
      parts.push(
        `<div class="essay-row-card">` +
          `<button type="button" class="essay-row-btn essay-open-btn" data-exam-id="${escapeAttr(exam.id)}" data-sample-id="${escapeAttr(s.id)}">` +
            `<span class="essay-row-title">${escapeHtml(s.title)}</span><span class="muted essay-row-chevron" aria-hidden="true">›</span>` +
          `</button>` +
        `</div>`
      );
    }
  }
  listEl.innerHTML = parts.join("");
  listEl.querySelectorAll(".essay-open-btn").forEach((btn) => {
    btn.addEventListener("click", () =>
      openEssayDetail(btn.dataset.examId, btn.dataset.sampleId, suffix)
    );
  });
}

/** @param {"cn"|"en"} suffix */
function openEssayDetail(examId, sampleId, suffix) {
  const data = window.ESSAYS_DATA;
  const exam = data?.exams?.find((e) => e.id === examId);
  const sample = exam?.samples?.find((s) => s.id === sampleId);
  if (!exam || !sample) return;
  const listEl = $(`essay-stack-list-${suffix}`);
  const detailEl = $(`essay-stack-detail-${suffix}`);
  if (!listEl || !detailEl) return;
  listEl.classList.add("hidden");
  detailEl.classList.remove("hidden");
  const topicsHtml = escapeHtml(exam.topics).replace(/\n/g, "<br />");
  const bodyHtml = escapeHtml(sample.body.trim()).replace(/\n/g, "<br />");
  detailEl.innerHTML =
    `<div class="essay-detail-panel">` +
    `<button type="button" class="btn-secondary essay-detail-back">← 返回</button>` +
    `<h2 class="section-title essay-detail-sample-title">${escapeHtml(sample.title)}</h2>` +
    `<div class="card settings-block mt-8">` +
    `<h3 class="essay-block-label essay-block-label-topic">本年题目</h3>` +
    `<p class="essay-topics-text">${topicsHtml}</p>` +
    `</div>` +
    `<div class="essay-tts-row mt-8" data-tts-for="${escapeAttr(sample.id)}">` +
      `<button type="button" class="essay-tts-play-btn essay-tts-btn essay-detail-tts" data-exam-id="${escapeAttr(exam.id)}" data-sample-id="${escapeAttr(sample.id)}">播放</button>` +
      `<input type="range" class="essay-tts-progress" min="0" max="100" value="0" step="0.1" disabled aria-label="播放进度" />` +
      `<span class="essay-tts-time muted small">0:00 / 0:00</span>` +
      `<select class="essay-tts-speed" aria-label="播放倍速">` +
      `<option value="0.75">0.75×</option>` +
      `<option value="1" selected>1×</option>` +
      `<option value="1.25">1.25×</option>` +
      `<option value="1.5">1.5×</option>` +
      `<option value="2">2×</option>` +
      `</select>` +
    `</div>` +
    `<div class="card settings-block mt-8">` +
    `<h3 class="essay-block-label">正文</h3>` +
    `<p class="essay-body-text">${bodyHtml}</p>` +
    `</div></div>`;
  detailEl.querySelector(".essay-detail-back")?.addEventListener("click", () => closeEssayDetail(suffix));
  detailEl.querySelector(".essay-detail-tts")?.addEventListener("click", async () => {
    await toggleEssayTts(exam.id, sample.id, detailEl);
  });
  const speedEl = detailEl.querySelector(".essay-tts-speed");
  if (speedEl) {
    speedEl.addEventListener("change", () => {
      const rate = getEssayDetailPlaybackRate(detailEl);
      const st = essayTtsState.get(sample.id);
      if (st && st.audio) {
        st.audio.playbackRate = rate;
      }
    });
  }
  wireEssayTtsScrubber(sample.id, detailEl);
  updateEssayTtsUi(sample.id);
}

function wireEssayTtsScrubber(sampleId, detailRoot) {
  const range = detailRoot.querySelector(".essay-tts-progress");
  if (!range || range.tagName !== "INPUT") return;
  const setSeeking = (v) => {
    const st = essayTtsState.get(sampleId);
    if (st) {
      st.seeking = v;
      essayTtsState.set(sampleId, st);
    }
  };
  range.addEventListener("pointerdown", () => setSeeking(true));
  range.addEventListener("pointerup", () => setSeeking(false));
  range.addEventListener("pointercancel", () => setSeeking(false));
  range.addEventListener("change", () => setSeeking(false));
  range.addEventListener("input", (e) => {
    const st = essayTtsState.get(sampleId);
    if (!st || !st.audio || !(st.duration > 0)) return;
    const t = (Number(e.target.value) / 100) * st.duration;
    st.audio.currentTime = t;
    st.current = t;
    updateEssayTtsUi(sampleId);
  });
}

/** @param {"cn"|"en"|undefined} suffix — 不传则关闭中英文两处详情 */
function closeEssayDetail(suffix) {
  const sufs = suffix ? [suffix] : ["cn", "en"];
  for (const suf of sufs) {
    const listEl = $(`essay-stack-list-${suf}`);
    const detailEl = $(`essay-stack-detail-${suf}`);
    if (!listEl || !detailEl) continue;
    listEl.classList.remove("hidden");
    detailEl.classList.add("hidden");
    detailEl.innerHTML = "";
  }
}

async function boot() {
  const raw = window.VOCABULARY_DATA;
  if (!raw) {
    console.error("VOCABULARY_DATA missing");
  }
  initVocabulary(raw || { words: [] });
  try {
    const [hfRes, mcRes] = await Promise.all([
      fetch("data/reading_high_freq.json"),
      fetch("data/mc688_21day.json"),
    ]);
    const hfJson = hfRes.ok ? await hfRes.json() : { entries: [] };
    const mcJson = mcRes.ok ? await mcRes.json() : { entries: [] };
    initSupplementalWordData(hfJson.entries || [], mcJson.entries || []);
  } catch (e) {
    console.warn("supplemental vocabulary", e);
    initSupplementalWordData([], []);
  }
  applyFontClass();
  loadAiSettings();
  wireEvents();
  renderVocabList();
  renderNotebook();
  renderEssayList("cn");
  renderEssayList("en");
  renderReadingMaterials();
  showTab("chinese");
}

void boot();
