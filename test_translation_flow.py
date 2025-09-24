#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
翻译流程测试脚本
测试完整的翻译流程：文本分割 -> 翻译 -> 显示
"""

import re

def split_into_sentences(text):
    """模拟Kotlin的句子分割逻辑"""
    if not text.strip():
        return []
    
    cleaned_text = preprocess_text(text)
    
    # 首先按段落分割（双换行符）
    paragraphs = re.split(r'\n\s*\n', cleaned_text)
    paragraphs = [p.strip() for p in paragraphs if p.strip()]
    
    sentences = []
    
    for paragraph in paragraphs:
        # 对每个段落按句子分割
        paragraph_sentences = split_paragraph_into_sentences(paragraph)
        sentences.extend(paragraph_sentences)
    
    return sentences

def split_paragraph_into_sentences(paragraph):
    """将段落分割成句子"""
    if not paragraph.strip():
        return []
    
    # 按句号、问号、感叹号分割，但排除<DOT>标记
    sentences = re.split(r'[.!?]+(?![^<]*<DOT>)', paragraph)
    sentences = [s.strip() for s in sentences if s.strip()]
    
    # 如果只有一个句子，检查是否包含分号
    if len(sentences) == 1:
        semicolon_sentences = re.split(r'[;]+', sentences[0])
        semicolon_sentences = [s.strip() for s in semicolon_sentences if s.strip()]
        
        if len(semicolon_sentences) > 1:
            return [restore_dots(s) for s in semicolon_sentences]
    
    return [restore_dots(s) for s in sentences]

def preprocess_text(text):
    """预处理文本"""
    text = text.replace('\n', ' ').replace('\r', ' ').strip()
    # 处理常见的缩写（在句号前添加特殊标记，避免被分割）
    text = re.sub(r'\b(Mr|Mrs|Ms|Dr|Prof)\.\s+', r'\1<DOT> ', text)
    # 处理数字后的句号（如 "1. 2. 3."）
    text = re.sub(r'(\d+)\.\s+', r'\1<DOT> ', text)
    return text

def restore_dots(text):
    """恢复句号标记"""
    return text.replace('<DOT>', '.')

def translate_with_local_dictionary(text, dictionary):
    """使用本地词典翻译"""
    words = text.lower().split()
    translated_words = []
    
    for word in words:
        # 清理单词（移除标点符号）
        clean_word = word.replace(',', '').replace('.', '').replace('!', '').replace('?', '')
        translation = dictionary.get(clean_word, word)
        translated_words.append(translation)
    
    return ' '.join(translated_words)

def test_translation_flow():
    """测试完整的翻译流程"""
    print("=== 翻译流程测试 ===")
    
    # 模拟本地翻译词典
    local_translations = {
        "hello": "你好",
        "world": "世界",
        "how": "如何",
        "are": "是",
        "you": "你",
        "i": "我",
        "am": "是",
        "fine": "好的",
        "this": "这",
        "is": "是",
        "a": "一个",
        "test": "测试",
        "what": "什么",
        "your": "你的",
        "name": "名字",
        "my": "我的",
        "john": "约翰",
        "like": "喜欢",
        "apples": "苹果",
        "bananas": "香蕉",
        "and": "和",
        "oranges": "橙子",
        "the": "这个",
        "weather": "天气",
        "good": "好的",
        "today": "今天",
        "it": "它",
        "sunny": "晴朗的",
        "warm": "温暖的",
        "first": "第一",
        "paragraph": "段落",
        "second": "第二",
        "here": "这里",
        "long": "长的",
        "text": "文本",
        "with": "带有",
        "multiple": "多个",
        "sentences": "句子",
        "mr": "先生",
        "smith": "史密斯",
        "teacher": "老师",
        "he": "他",
        "works": "工作",
        "at": "在",
        "school": "学校",
        "have": "有",
        "items": "项目",
        "on": "在",
        "list": "列表",
        "she": "她",
        "said": "说",
        "to": "给",
        "me": "我",
        "book": "书",
        "which": "哪个",
        "read": "读",
        "very": "非常",
        "interesting": "有趣的"
    }
    
    test_texts = [
        "Hello world. How are you? I am fine!",
        "This is a test. Another sentence here.",
        "What is your name? My name is John.",
        "I like apples, bananas, and oranges.",
        "The weather is good today; it's sunny and warm.",
        "First paragraph.\n\nSecond paragraph here.",
        "Mr. Smith is a teacher. He works at a school.",
        "Hello world. How are you? I am fine! This is a long text with multiple sentences."
    ]
    
    for i, text in enumerate(test_texts, 1):
        print(f"\n=== 测试 {i} ===")
        print(f"原文: {text}")
        
        # 步骤1：分割句子
        sentences = split_into_sentences(text)
        print(f"\n分割结果 ({len(sentences)} 个句子):")
        for j, sentence in enumerate(sentences, 1):
            print(f"  {j}. {sentence}")
        
        # 步骤2：翻译每个句子
        print(f"\n翻译结果:")
        for j, sentence in enumerate(sentences, 1):
            translated = translate_with_local_dictionary(sentence, local_translations)
            print(f"  {j}. {sentence}")
            print(f"     -> {translated}")
        
        print("-" * 60)

if __name__ == "__main__":
    test_translation_flow()
