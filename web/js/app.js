import {
  initVocabulary,
  getVocabularySet,
  getWordRecord,
  resolveInVocabulary,
  listWordsForHome,
  getWordCount,
} from "./vocabulary.js";

const STORAGE_NOTEBOOK = "aienglish_notebook";
const STORAGE_FONT = "aienglish_font";
const STORAGE_AI_MODEL = "aienglish_ai_model";
const STORAGE_AI_OCR_MODEL = "aienglish_ai_ocr_model";

/** 本地 Go 代理（见 server/），避免浏览器直连 xiwang CORS；令牌仅在服务端配置 */
const EMBEDDED_AI_BASE = "http://127.0.0.1:8787/openai-compatible/v1";

const DEFAULT_AI_MODEL = "gemini-3-pro";
const DEFAULT_AI_OCR_MODEL = "gemini-2.5-flash-image";

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

function loadNotebook() {
  try {
    const raw = localStorage.getItem(STORAGE_NOTEBOOK);
    if (!raw) return [];
    const arr = JSON.parse(raw);
    return Array.isArray(arr) ? arr.filter((x) => typeof x === "string") : [];
  } catch {
    return [];
  }
}

function saveNotebook(words) {
  localStorage.setItem(STORAGE_NOTEBOOK, JSON.stringify([...new Set(words.map((w) => w.toLowerCase()))]));
}

function addToNotebook(word) {
  const w = word.toLowerCase();
  const list = loadNotebook();
  if (!list.includes(w)) {
    list.push(w);
    saveNotebook(list);
  }
}

function removeFromNotebook(word) {
  const w = word.toLowerCase();
  saveNotebook(loadNotebook().filter((x) => x !== w));
}

function applyFontClass() {
  const v = localStorage.getItem(STORAGE_FONT) || "standard";
  const app = $("app");
  app.classList.remove("font-small", "font-standard", "font-large", "font-xlarge");
  const map = { small: "font-small", standard: "font-standard", large: "font-large", xlarge: "font-xlarge" };
  app.classList.add(map[v] || "font-standard");
  document.querySelectorAll("#font-size-seg button").forEach((b) => {
    b.classList.toggle("active", b.dataset.size === v);
  });
}

function showTab(name) {
  if (name !== "scan") {
    closeCameraModal();
  }
  document.querySelectorAll(".panel").forEach((p) => p.classList.remove("active"));
  const panel = $(`panel-${name}`);
  panel.classList.add("active");
  document.querySelectorAll(".nav-item").forEach((n) => {
    n.classList.toggle("active", n.dataset.tab === name);
  });
  $("bottom-nav").classList.toggle("hidden", name === "result");
  $("btn-result-back").classList.toggle("hidden", name !== "result");
  $("app-title").textContent = name === "result" ? "识别结果" : "中考词汇扫描助手";
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
    li.innerHTML = `<div class="word-line">${escapeHtml(rec.word)}</div>${cnBlock}<div class="word-meta">${metaLine}</div>`;
    li.addEventListener("click", () => openWordModal(rec.word, "list"));
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

function renderNotebook() {
  const ul = $("notebook-list");
  const words = loadNotebook().sort((a, b) => a.localeCompare(b, "en"));
  ul.innerHTML = "";
  if (words.length === 0) {
    ul.innerHTML = '<li class="muted">暂无收藏生词</li>';
    return;
  }
  for (const w of words) {
    const li = document.createElement("li");
    li.innerHTML = `<span>${escapeHtml(w)}</span><button type="button" data-remove="${escapeAttr(w)}">删除</button>`;
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
  const rec = getWordRecord(w);
  const ph = (rec?.phonetic || "").trim();
  const phEl = $("modal-phonetic");
  if (ph) {
    phEl.textContent = ph;
    phEl.classList.remove("hidden");
  } else {
    phEl.textContent = "";
    phEl.classList.add("hidden");
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

  const inNb = loadNotebook().includes(w);
  $("modal-note").textContent = inNb ? "已在生词本中" : "";
  $("modal-add-notebook").textContent = inNb ? "已在生词本" : "加入生词本";
  $("modal-add-notebook").disabled = inNb;

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
    btn.addEventListener("click", () => showTab(btn.dataset.tab || "vocab"));
  });

  $("scan-file").addEventListener("change", (e) => {
    const input = e.target;
    const f = input.files?.[0];
    if (f) {
      showTab("scan");
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

  $("btn-result-back").addEventListener("click", () => showTab("scan"));
  $("btn-rescan").addEventListener("click", () => showTab("scan"));
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

  $("modal-add-notebook").addEventListener("click", () => {
    const w = $("modal-word-title").textContent.trim();
    if (!w) return;
    addToNotebook(w);
    $("modal-note").textContent = "已加入生词本";
    $("modal-add-notebook").textContent = "已在生词本";
    $("modal-add-notebook").disabled = true;
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
    const nb = loadNotebook();
    if (nb.length === 0) {
      alert("生词本为空");
      return;
    }
    const w = nb[Math.floor(Math.random() * nb.length)];
    openWordModal(w, "notebook");
  });

  $("btn-notebook-clear").addEventListener("click", () => {
    if (confirm("确定清空生词本？")) {
      saveNotebook([]);
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

  document.querySelectorAll("#font-size-seg button").forEach((b) => {
    b.addEventListener("click", () => {
      const size = b.dataset.size || "standard";
      localStorage.setItem(STORAGE_FONT, size);
      applyFontClass();
    });
  });
}

function boot() {
  const raw = window.VOCABULARY_DATA;
  if (!raw) {
    console.error("VOCABULARY_DATA missing");
  }
  initVocabulary(raw || { words: [] });
  applyFontClass();
  loadAiSettings();
  wireEvents();
  renderVocabList();
  renderNotebook();
  showTab("vocab");
}

boot();
