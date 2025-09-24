#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
句子分割测试脚本
测试文本分割功能
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
    import re
    text = text.replace('\n', ' ').replace('\r', ' ').strip()
    # 处理常见的缩写（在句号前添加特殊标记，避免被分割）
    text = re.sub(r'\b(Mr|Mrs|Ms|Dr|Prof)\.\s+', r'\1<DOT> ', text)
    # 处理数字后的句号（如 "1. 2. 3."）
    text = re.sub(r'(\d+)\.\s+', r'\1<DOT> ', text)
    return text

def restore_dots(text):
    """恢复句号标记"""
    return text.replace('<DOT>', '.')

def test_sentence_splitting():
    """测试句子分割功能"""
    print("=== 句子分割测试 ===")
    
    test_cases = [
        "Hello world. How are you? I am fine!",
        "This is a test. Another sentence here.",
        "What is your name? My name is John.",
        "I like apples, bananas, and oranges.",
        "The weather is good today; it's sunny and warm.",
        "First paragraph.\n\nSecond paragraph here.",
        "Hello world. How are you? I am fine! This is a long text with multiple sentences.",
        "Mr. Smith is a teacher. He works at a school.",
        "I have 1. 2. 3. items on my list.",
        "She said \"Hello\" to me.",
        "The book (which I read) is very interesting."
    ]
    
    for i, text in enumerate(test_cases, 1):
        print(f"\n测试 {i}:")
        print(f"原文: {text}")
        
        sentences = split_into_sentences(text)
        print(f"分割结果 ({len(sentences)} 个句子):")
        for j, sentence in enumerate(sentences, 1):
            print(f"  {j}. {sentence}")
        
        print("-" * 50)

if __name__ == "__main__":
    test_sentence_splitting()
