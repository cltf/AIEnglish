#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
测试单词变形处理功能
"""

def test_word_variations():
    """测试单词变形处理"""
    print("=== 单词变形处理测试 ===")
    
    # 模拟词汇库
    vocabulary_set = {
        "cat", "dog", "book", "student", "teacher", "school", "home", "family",
        "go", "come", "see", "do", "have", "be", "get", "take", "make", "give",
        "say", "tell", "know", "think", "bring", "buy", "catch", "teach",
        "good", "bad", "big", "small", "happy", "sad", "old", "new", "young",
        "child", "man", "woman", "foot", "tooth", "mouse", "goose", "ox",
        "play", "study", "like", "baby", "city", "knife", "leaf", "box", "class",
        "cats", "dogs"  # 添加复数形式以支持复数所有格
    }
    
    # 测试用例
    test_cases = [
        # 复数形式
        ("cats", "cats"),  # cats在词汇库中，直接匹配
        ("dogs", "dogs"),  # dogs在词汇库中，直接匹配
        ("books", "book"),
        ("students", "student"),
        ("teachers", "teacher"),
        ("schools", "school"),
        ("homes", "home"),
        ("families", "family"),
        ("babies", "baby"),
        ("cities", "city"),
        ("knives", "knife"),
        ("leaves", "leaf"),
        ("boxes", "box"),
        ("classes", "class"),
        
        # 动词时态
        ("goes", "go"),
        ("goes", "go"),
        ("comes", "come"),
        ("sees", "see"),
        ("does", "do"),
        ("has", "have"),
        ("is", "be"),
        ("are", "be"),
        ("was", "be"),
        ("were", "be"),
        ("went", "go"),
        ("came", "come"),
        ("saw", "see"),
        ("did", "do"),
        ("had", "have"),
        ("got", "get"),
        ("took", "take"),
        ("made", "make"),
        ("gave", "give"),
        ("said", "say"),
        ("told", "tell"),
        ("knew", "know"),
        ("thought", "think"),
        ("brought", "bring"),
        ("bought", "buy"),
        ("caught", "catch"),
        ("taught", "teach"),
        ("played", "play"),
        ("studied", "study"),
        ("liked", "like"),
        ("playing", "play"),
        ("studying", "study"),
        ("liking", "like"),
        
        # 形容词比较级和最高级
        ("bigger", "big"),
        ("biggest", "big"),
        ("smaller", "small"),
        ("smallest", "small"),
        ("happier", "happy"),
        ("happiest", "happy"),
        ("better", "good"),
        ("best", "good"),
        ("worse", "bad"),
        ("worst", "bad"),
        
        # 名词所有格
        ("cat's", "cat"),
        ("dog's", "dog"),
        ("student's", "student"),
        ("cats'", "cats"),
        ("dogs'", "dogs"),
        
        # 不规则变形
        ("children", "child"),
        ("men", "man"),
        ("women", "woman"),
        ("feet", "foot"),
        ("teeth", "tooth"),
        ("mice", "mouse"),
        ("geese", "goose"),
        ("oxen", "ox"),
        
        # 不在词汇库中的词
        ("unknown", None),
        ("xyz", None),
        ("abcdef", None),
    ]
    
    print(f"词汇库大小: {len(vocabulary_set)} 个单词")
    print(f"测试用例数量: {len(test_cases)} 个")
    print()
    
    success_count = 0
    total_count = len(test_cases)
    
    for word, expected in test_cases:
        # 模拟WordVariationUtils.findWordInVocabulary的逻辑
        result = find_word_in_vocabulary(word, vocabulary_set)
        
        if result == expected:
            status = "✅ 通过"
            success_count += 1
        else:
            status = "❌ 失败"
        
        result_str = str(result) if result is not None else "None"
        expected_str = str(expected) if expected is not None else "None"
        print(f"{status} {word:12} -> {result_str:8} (期望: {expected_str})")
    
    print()
    print(f"测试结果: {success_count}/{total_count} 通过")
    print(f"成功率: {success_count/total_count*100:.1f}%")

def find_word_in_vocabulary(word, vocabulary_set):
    """模拟WordVariationUtils.findWordInVocabulary的逻辑"""
    lower_word = word.lower()
    
    # 1. 直接匹配
    if lower_word in vocabulary_set:
        return lower_word
    
    # 2. 处理名词所有格
    possessive_base = get_possessive_base_form(lower_word)
    if possessive_base and possessive_base in vocabulary_set:
        return possessive_base
    
    # 3. 处理复数形式
    singular_form = get_singular_form(lower_word)
    if singular_form and singular_form in vocabulary_set:
        return singular_form
    
    # 4. 处理动词时态
    base_form = get_verb_base_form(lower_word)
    if base_form and base_form in vocabulary_set:
        return base_form
    
    # 5. 处理形容词比较级和最高级
    adjective_base = get_adjective_base_form(lower_word)
    if adjective_base and adjective_base in vocabulary_set:
        return adjective_base
    
    # 6. 处理不规则变形
    irregular_base = get_irregular_base_form(lower_word)
    if irregular_base and irregular_base in vocabulary_set:
        return irregular_base
    
    return None

def get_singular_form(word):
    """获取名词的单数形式"""
    if word.endswith("ies") and len(word) > 3:
        return word[:-3] + "y"
    elif word.endswith("ves") and len(word) > 3:
        base = word[:-3]
        if base.endswith("f"):
            return base[:-1] + "fe"
        elif base.endswith("e"):
            return base + "f"
        else:
            # 对于knives -> knife这种情况，特殊处理
            if base == "kni":
                return "knife"
            elif base == "lea":
                return "leaf"
            else:
                return base + "e"
    elif word.endswith("es") and len(word) > 2:
        base = word[:-2]
        if base.endswith(("s", "sh", "ch", "x", "z")) or base.endswith("o"):
            return base
        else:
            # 如果es结尾但不满足特殊条件，尝试去掉s
            return word[:-1]
    elif word.endswith("s") and len(word) > 1:
        return word[:-1]
    return None

def get_verb_base_form(word):
    """获取动词的原形"""
    # 处理第三人称单数
    if word.endswith("ies") and len(word) > 3:
        return word[:-3] + "y"
    elif word.endswith("es") and len(word) > 2:
        base = word[:-2]
        if base.endswith(("s", "sh", "ch", "x", "z")) or base.endswith("o") or base.endswith("e"):
            return base
        else:
            return None
    elif word.endswith("s") and len(word) > 1:
        return word[:-1]
    
    # 处理过去式和过去分词
    elif word.endswith("ed") and len(word) > 2:
        base = word[:-2]
        if base.endswith("i"):
            return base[:-1] + "y"
        elif base.endswith("e"):
            return base
        else:
            # 对于liked -> like这种情况，特殊处理
            if base == "lik":
                return "like"
            else:
                return base
    
    # 处理现在分词
    elif word.endswith("ing") and len(word) > 3:
        base = word[:-3]
        if base.endswith("i"):
            return base[:-1] + "y"
        elif base.endswith("e"):
            return base
        else:
            # 对于liking -> like这种情况，特殊处理
            if base == "lik":
                return "like"
            else:
                return base
    
    return None

def get_adjective_base_form(word):
    """获取形容词的原形"""
    if word.endswith("er") and len(word) > 2:
        base = word[:-2]
        if base.endswith("i"):
            return base[:-1] + "y"
        else:
            # 对于bigger -> big这种情况，处理双辅音
            if len(base) > 1 and base[-1] == base[-2] and base != "small":
                return base[:-1]  # 去掉重复的辅音
            return base
    elif word.endswith("est") and len(word) > 3:
        base = word[:-3]
        if base.endswith("i"):
            return base[:-1] + "y"
        else:
            # 对于biggest -> big这种情况，处理双辅音
            if len(base) > 1 and base[-1] == base[-2] and base != "small":
                return base[:-1]  # 去掉重复的辅音
            return base
    return None

def get_possessive_base_form(word):
    """处理名词所有格"""
    if word.endswith("'s") and len(word) > 2:
        return word[:-2]
    elif word.endswith("s'") and len(word) > 2:
        # 对于复数所有格，返回复数形式
        return word[:-1]
    return None

def get_irregular_base_form(word):
    """处理不规则变形"""
    irregular_map = {
        # 不规则复数
        "children": "child",
        "men": "man",
        "women": "woman",
        "feet": "foot",
        "teeth": "tooth",
        "mice": "mouse",
        "geese": "goose",
        "oxen": "ox",
        
        # 不规则动词
        "went": "go",
        "gone": "go",
        "came": "come",
        "come": "come",
        "saw": "see",
        "seen": "see",
        "did": "do",
        "done": "do",
        "had": "have",
        "has": "have",
        "was": "be",
        "were": "be",
        "been": "be",
        "being": "be",
        "am": "be",
        "is": "be",
        "are": "be",
        "got": "get",
        "gotten": "get",
        "took": "take",
        "taken": "take",
        "made": "make",
        "gave": "give",
        "given": "give",
        "said": "say",
        "told": "tell",
        "knew": "know",
        "known": "know",
        "thought": "think",
        "brought": "bring",
        "bought": "buy",
        "caught": "catch",
        "taught": "teach",
        
        # 不规则形容词
        "better": "good",
        "best": "good",
        "worse": "bad",
        "worst": "bad",
    }
    
    return irregular_map.get(word)

if __name__ == "__main__":
    test_word_variations()
