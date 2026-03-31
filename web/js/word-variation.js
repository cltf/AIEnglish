/**
 * 与 Android WordVariationUtils.kt 对齐的单词变形匹配
 */

const IRREGULAR_MAP = {
  children: "child",
  men: "man",
  women: "woman",
  feet: "foot",
  teeth: "tooth",
  mice: "mouse",
  geese: "goose",
  oxen: "ox",
  went: "go",
  gone: "go",
  came: "come",
  saw: "see",
  seen: "see",
  did: "do",
  done: "do",
  had: "have",
  has: "have",
  was: "be",
  were: "be",
  been: "be",
  being: "be",
  am: "be",
  is: "be",
  are: "be",
  got: "get",
  gotten: "get",
  took: "take",
  taken: "take",
  made: "make",
  gave: "give",
  given: "give",
  said: "say",
  told: "tell",
  knew: "know",
  known: "know",
  thought: "think",
  brought: "bring",
  bought: "buy",
  caught: "catch",
  taught: "teach",
  fought: "fight",
  sought: "seek",
  found: "find",
  built: "build",
  sent: "send",
  spent: "spend",
  bent: "bend",
  lent: "lend",
  meant: "mean",
  kept: "keep",
  slept: "sleep",
  swept: "sweep",
  wept: "weep",
  crept: "creep",
  leapt: "leap",
  felt: "feel",
  dealt: "deal",
  left: "leave",
  lost: "lose",
  won: "win",
  began: "begin",
  begun: "begin",
  drank: "drink",
  drunk: "drink",
  rang: "ring",
  rung: "ring",
  sang: "sing",
  sung: "sing",
  sank: "sink",
  sunk: "sink",
  swam: "swim",
  swum: "swim",
  ran: "run",
  shook: "shake",
  shaken: "shake",
  stole: "steal",
  stolen: "steal",
  broke: "break",
  broken: "break",
  chose: "choose",
  chosen: "choose",
  spoke: "speak",
  spoken: "speak",
  woke: "wake",
  woken: "wake",
  wore: "wear",
  worn: "wear",
  tore: "tear",
  torn: "tear",
  bore: "bear",
  born: "bear",
  swore: "swear",
  sworn: "swear",
  froze: "freeze",
  frozen: "freeze",
  drove: "drive",
  driven: "drive",
  wrote: "write",
  written: "write",
  rode: "ride",
  ridden: "ride",
  rose: "rise",
  risen: "rise",
  fell: "fall",
  fallen: "fall",
  held: "hold",
  stood: "stand",
  understood: "understand",
  misunderstood: "misunderstand",
  withstood: "withstand",
  withheld: "withhold",
  overcame: "overcome",
  became: "become",
  better: "good",
  best: "good",
  worse: "bad",
  worst: "bad",
  more: "much",
  most: "much",
  less: "little",
  least: "little",
  farther: "far",
  farthest: "far",
  further: "far",
  furthest: "far",
  older: "old",
  oldest: "old",
  elder: "old",
  eldest: "old",
};

function getPossessiveBaseForm(word) {
  if (word.endsWith("'s") && word.length > 2) return word.slice(0, -2);
  if (word.endsWith("s'") && word.length > 2) return word.slice(0, -1);
  return null;
}

function getSingularForm(word) {
  if (word.endsWith("ies") && word.length > 3) return word.slice(0, -3) + "y";
  if (word.endsWith("ves") && word.length > 3) {
    const base = word.slice(0, -3);
    if (base.endsWith("f")) return base.slice(0, -1) + "fe";
    if (base.endsWith("e")) return base + "f";
    if (base === "kni") return "knife";
    if (base === "lea") return "leaf";
    return base + "e";
  }
  if (word.endsWith("es") && word.length > 2) {
    const base = word.slice(0, -2);
    if (/[sxz]$/.test(base) || /(sh|ch)$/.test(base)) return base;
    if (base.endsWith("o")) return base;
    return word.slice(0, -1);
  }
  if (word.endsWith("s") && word.length > 1) return word.slice(0, -1);
  return null;
}

function getVerbBaseForm(word) {
  if (word.endsWith("ies") && word.length > 3) return word.slice(0, -3) + "y";
  if (word.endsWith("es") && word.length > 2) {
    const base = word.slice(0, -2);
    if (/[sxz]$/.test(base) || /(sh|ch)$/.test(base)) return base;
    if (base.endsWith("o")) return base;
    if (base.endsWith("e")) return base;
    return null;
  }
  if (word.endsWith("s") && word.length > 1) return word.slice(0, -1);
  if (word.endsWith("ed") && word.length > 2) {
    const base = word.slice(0, -2);
    if (base.endsWith("i")) return base.slice(0, -1) + "y";
    if (base.endsWith("e")) return base;
    if (base === "lik") return "like";
    return base;
  }
  if (word.endsWith("ing") && word.length > 3) {
    const base = word.slice(0, -3);
    if (base.endsWith("i")) return base.slice(0, -1) + "y";
    if (base.endsWith("e")) return base;
    if (base === "lik") return "like";
    return base;
  }
  return null;
}

function getAdjectiveBaseForm(word) {
  if (word.endsWith("er") && word.length > 2) {
    const base = word.slice(0, -2);
    if (base.endsWith("i")) return base.slice(0, -1) + "y";
    if (base.length > 1 && base.at(-1) === base.at(-2) && base !== "small") return base.slice(0, -1);
    return base;
  }
  if (word.endsWith("est") && word.length > 3) {
    const base = word.slice(0, -3);
    if (base.endsWith("i")) return base.slice(0, -1) + "y";
    if (base.length > 1 && base.at(-1) === base.at(-2) && base !== "small") return base.slice(0, -1);
    return base;
  }
  return null;
}

function getIrregularBaseForm(word) {
  return IRREGULAR_MAP[word] ?? null;
}

/**
 * @param {string} word
 * @param {Set<string>} vocabularySet 小写词形集合
 * @returns {string|null} 词库中的原形或 null
 */
export function findWordInVocabulary(word, vocabularySet) {
  const lowerWord = word.toLowerCase();

  if (vocabularySet.has(lowerWord)) return lowerWord;

  const possessiveBase = getPossessiveBaseForm(lowerWord);
  if (possessiveBase != null && vocabularySet.has(possessiveBase)) return possessiveBase;

  const singularForm = getSingularForm(lowerWord);
  if (singularForm != null && vocabularySet.has(singularForm)) return singularForm;

  const verbBase = getVerbBaseForm(lowerWord);
  if (verbBase != null && vocabularySet.has(verbBase)) return verbBase;

  const adjBase = getAdjectiveBaseForm(lowerWord);
  if (adjBase != null && vocabularySet.has(adjBase)) return adjBase;

  const irr = getIrregularBaseForm(lowerWord);
  if (irr != null && vocabularySet.has(irr)) return irr;

  return null;
}
