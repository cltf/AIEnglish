import { findWordInVocabulary } from "./word-variation.js";

/** @typedef {{ partOfSpeech: string, meaning: string }} DefinitionDef */
/** @typedef {{ word: string, phonetic: string, type: string, definitions: DefinitionDef[], example?: string, exampleZh?: string }} WordRecord */

let wordByKey = new Map();
/** @type {Set<string>} */
let vocabularySet = new Set();

/** @type {Map<string, { word: string, phonetic?: string, meaning: string }>} */
let readingHfByKey = new Map();
/** @type {Map<string, { word: string, meaning: string }>} */
let mc688ByKey = new Map();

/**
 * @param {unknown} raw
 */
export function initVocabulary(raw) {
  const data = raw && typeof raw === "object" && "words" in raw ? raw : { words: [] };
  const words = /** @type {WordRecord[]} */ (data.words || []);
  wordByKey = new Map();
  vocabularySet = new Set();
  for (const w of words) {
    const key = w.word.toLowerCase();
    wordByKey.set(key, w);
    vocabularySet.add(key);
  }
}

export function getVocabularySet() {
  return vocabularySet;
}

/**
 * @param {string} word
 * @returns {WordRecord | null}
 */
export function getWordRecord(word) {
  return wordByKey.get(word.toLowerCase()) ?? null;
}

/**
 * @param {{ word: string, phonetic?: string, meaning: string }[]} readingEntries
 * @param {{ word: string, meaning: string }[]} mcEntries
 */
export function initSupplementalWordData(readingEntries, mcEntries) {
  readingHfByKey = new Map();
  for (const e of readingEntries || []) {
    if (e && e.word) readingHfByKey.set(e.word.toLowerCase(), e);
  }
  mc688ByKey = new Map();
  for (const e of mcEntries || []) {
    if (e && e.word) mc688ByKey.set(e.word.toLowerCase(), e);
  }
}

/**
 * 合并主词库、阅读高频、688，用于详情弹层（生词本等）。
 * @param {string} word
 * @returns {WordRecord | null}
 */
export function getResolvedWordRecord(word) {
  const w = word.toLowerCase();
  const base = getWordRecord(word);
  const hf = readingHfByKey.get(w);
  const mc = mc688ByKey.get(w);
  if (base) {
    const phonetic = (base.phonetic || "").trim() || (hf?.phonetic || "").trim() || "";
    let definitions = base.definitions?.length ? base.definitions : [];
    if (!definitions.length) {
      if (hf?.meaning) definitions = [{ partOfSpeech: "", meaning: hf.meaning }];
      else if (mc?.meaning) definitions = [{ partOfSpeech: "", meaning: mc.meaning }];
    }
    return {
      ...base,
      phonetic,
      definitions,
    };
  }
  if (hf) {
    return {
      word: hf.word,
      phonetic: (hf.phonetic || "").trim(),
      type: "",
      definitions: [{ partOfSpeech: "", meaning: hf.meaning }],
      example: undefined,
      exampleZh: undefined,
    };
  }
  if (mc) {
    return {
      word: mc.word,
      phonetic: "",
      type: "",
      definitions: [{ partOfSpeech: "", meaning: mc.meaning }],
      example: undefined,
      exampleZh: undefined,
    };
  }
  return null;
}

/**
 * @param {string} token
 * @returns {string | null}
 */
export function resolveInVocabulary(token) {
  return findWordInVocabulary(token, vocabularySet);
}

/**
 * @param {string} typeFilter ALL | MIDDLE_SCHOOL | ADVANCED
 * @param {string} searchQuery
 */
export function listWordsForHome(typeFilter, searchQuery) {
  const q = searchQuery.trim().toLowerCase();
  const out = [];
  for (const [, rec] of wordByKey) {
    if (typeFilter !== "ALL" && rec.type !== typeFilter) continue;
    if (q && !rec.word.toLowerCase().includes(q)) continue;
    out.push(rec);
  }
  out.sort((a, b) => a.word.localeCompare(b.word, "en"));
  return out;
}

export function getWordCount() {
  return wordByKey.size;
}
