import { findWordInVocabulary } from "./word-variation.js";

/** @typedef {{ partOfSpeech: string, meaning: string }} DefinitionDef */
/** @typedef {{ word: string, phonetic: string, type: string, definitions: DefinitionDef[], example?: string, exampleZh?: string }} WordRecord */

let wordByKey = new Map();
/** @type {Set<string>} */
let vocabularySet = new Set();

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
