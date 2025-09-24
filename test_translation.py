#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
翻译功能测试脚本
测试本地词典翻译功能
"""

def test_local_translation():
    """测试本地词典翻译功能"""
    print("=== 翻译功能测试 ===")
    
    # 模拟本地翻译词典
    local_translations = {
        # 基础词汇
        "hello": "你好",
        "world": "世界",
        "good": "好的",
        "morning": "早上",
        "afternoon": "下午",
        "evening": "晚上",
        "night": "夜晚",
        "thank": "谢谢",
        "please": "请",
        "sorry": "对不起",
        "yes": "是的",
        "no": "不",
        
        # 疑问词
        "how": "如何",
        "what": "什么",
        "where": "哪里",
        "when": "什么时候",
        "why": "为什么",
        "who": "谁",
        "which": "哪一个",
        
        # 学校相关
        "school": "学校",
        "student": "学生",
        "teacher": "老师",
        "book": "书",
        "pen": "笔",
        "pencil": "铅笔",
        "desk": "桌子",
        "chair": "椅子",
        "classroom": "教室",
        "homework": "作业",
        "exam": "考试",
        "test": "测试",
        
        # 家庭相关
        "family": "家庭",
        "father": "父亲",
        "mother": "母亲",
        "brother": "兄弟",
        "sister": "姐妹",
        "parent": "父母",
        "child": "孩子",
        "baby": "婴儿",
        
        # 时间相关
        "time": "时间",
        "hour": "小时",
        "minute": "分钟",
        "second": "秒",
        "day": "天",
        "week": "周",
        "month": "月",
        "year": "年",
        "today": "今天",
        "tomorrow": "明天",
        "yesterday": "昨天",
        
        # 颜色
        "red": "红色",
        "blue": "蓝色",
        "green": "绿色",
        "yellow": "黄色",
        "black": "黑色",
        "white": "白色",
        "pink": "粉色",
        "purple": "紫色",
        "orange": "橙色",
        "brown": "棕色",
        
        # 数字
        "one": "一",
        "two": "二",
        "three": "三",
        "four": "四",
        "five": "五",
        "six": "六",
        "seven": "七",
        "eight": "八",
        "nine": "九",
        "ten": "十",
        
        # 动词
        "go": "去",
        "come": "来",
        "see": "看",
        "look": "看",
        "listen": "听",
        "speak": "说",
        "talk": "谈话",
        "read": "读",
        "write": "写",
        "study": "学习",
        "learn": "学习",
        "teach": "教",
        "work": "工作",
        "play": "玩",
        "eat": "吃",
        "drink": "喝",
        "sleep": "睡觉",
        "walk": "走",
        "run": "跑",
        "jump": "跳",
        "swim": "游泳",
        "fly": "飞",
        "drive": "驾驶",
        "buy": "买",
        "sell": "卖",
        "give": "给",
        "take": "拿",
        "make": "制作",
        "do": "做",
        "get": "得到",
        "have": "有",
        "be": "是",
        
        # 情态动词
        "will": "将",
        "can": "能",
        "may": "可能",
        "must": "必须",
        "should": "应该",
        "would": "会",
        "could": "能够",
        "might": "可能",
        "shall": "将",
        
        # 形容词
        "good": "好的",
        "bad": "坏的",
        "big": "大的",
        "small": "小的",
        "long": "长的",
        "short": "短的",
        "tall": "高的",
        "old": "老的",
        "new": "新的",
        "young": "年轻的",
        "beautiful": "美丽的",
        "ugly": "丑陋的",
        "happy": "快乐的",
        "sad": "悲伤的",
        "angry": "生气的",
        "tired": "累的",
        "hungry": "饿的",
        "thirsty": "渴的",
        "hot": "热的",
        "cold": "冷的",
        "warm": "温暖的",
        "cool": "凉爽的",
        "fast": "快的",
        "slow": "慢的",
        "easy": "容易的",
        "difficult": "困难的",
        "important": "重要的",
        "interesting": "有趣的",
        "boring": "无聊的",
        "expensive": "昂贵的",
        "cheap": "便宜的",
        "free": "免费的",
        "busy": "忙碌的",
        "clean": "干净的",
        "dirty": "脏的",
        "full": "满的",
        "empty": "空的",
        "open": "开的",
        "closed": "关的",
        "right": "对的",
        "wrong": "错的",
        "true": "真的",
        "false": "假的"
    }
    
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
    
    # 测试用例
    test_cases = [
        "Hello world",
        "Good morning",
        "Thank you",
        "How are you",
        "What is your name",
        "Where are you from",
        "I am a student",
        "This is a book",
        "I like to read",
        "The weather is good today",
        "My family is happy",
        "I can speak English",
        "The teacher is very good",
        "I want to learn Chinese",
        "This is a beautiful day",
        "I have a red pen",
        "The cat is sleeping",
        "I will go to school tomorrow",
        "Can you help me",
        "I am very tired today"
    ]
    
    print(f"本地词典大小: {len(local_translations)} 个单词")
    print(f"测试用例数量: {len(test_cases)} 个")
    print()
    
    success_count = 0
    total_count = len(test_cases)
    
    for english_text in test_cases:
        translated_text = translate_with_local_dictionary(english_text, local_translations)
        
        # 简单的成功判断：如果翻译结果包含中文字符，认为翻译成功
        has_chinese = any('\u4e00' <= char <= '\u9fff' for char in translated_text)
        
        if has_chinese:
            status = "✅ 成功"
            success_count += 1
        else:
            status = "❌ 失败"
        
        print(f"{status} {english_text:30} -> {translated_text}")
    
    print()
    print(f"测试结果: {success_count}/{total_count} 成功")
    print(f"成功率: {success_count/total_count*100:.1f}%")
    
    # 测试句子分割功能
    print("\n=== 句子分割测试 ===")
    test_texts = [
        "Hello world. How are you? I am fine!",
        "This is a test. Another sentence here.",
        "What is your name? My name is John.",
        "I like apples, bananas, and oranges.",
        "The weather is good today; it's sunny and warm."
    ]
    
    for text in test_texts:
        sentences = re.split(r'[.!?]+', text)
        sentences = [s.strip() for s in sentences if s.strip()]
        print(f"原文: {text}")
        print(f"分割: {sentences}")
        print()

def test_text_parsing():
    """测试文本解析功能"""
    print("=== 文本解析测试 ===")
    
    test_texts = [
        "Hello world. How are you? I am fine!",
        "This is a test. Another sentence here.",
        "What is your name? My name is John.",
        "I like apples, bananas, and oranges.",
        "The weather is good today; it's sunny and warm.",
        "Mr. Smith is a teacher. He works at a school.",
        "I have 1. 2. 3. items on my list.",
        "She said \"Hello\" to me.",
        "The book (which I read) is very interesting."
    ]
    
    for text in test_texts:
        print(f"原文: {text}")
        
        # 模拟句子分割
        sentences = re.split(r'[.!?]+', text)
        sentences = [s.strip() for s in sentences if s.strip()]
        print(f"句子: {sentences}")
        
        # 模拟单词分割
        words = text.split()
        print(f"单词: {words}")
        
        # 模拟语言检测
        chinese_count = len(re.findall(r'[\u4e00-\u9fff]', text))
        english_count = len(re.findall(r'[a-zA-Z]', text))
        language = "zh" if chinese_count > english_count else "en" if english_count > chinese_count else "unknown"
        print(f"语言: {language}")
        
        print()

if __name__ == "__main__":
    import re
    
    test_local_translation()
    test_text_parsing()
