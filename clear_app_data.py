#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
清除应用数据脚本
用于解决数据库权限问题
"""

import subprocess
import sys

def clear_app_data():
    """清除应用数据"""
    print("=== 清除应用数据 ===")
    
    try:
        # 清除应用数据
        print("正在清除应用数据...")
        result = subprocess.run([
            "adb", "shell", "pm", "clear", "com.vocabulary.scanner"
        ], capture_output=True, text=True)
        
        if result.returncode == 0:
            print("✅ 应用数据清除成功")
        else:
            print(f"❌ 清除失败: {result.stderr}")
            return False
            
        # 卸载应用
        print("正在卸载应用...")
        result = subprocess.run([
            "adb", "uninstall", "com.vocabulary.scanner"
        ], capture_output=True, text=True)
        
        if result.returncode == 0:
            print("✅ 应用卸载成功")
        else:
            print(f"⚠️ 卸载失败或应用未安装: {result.stderr}")
            
        return True
        
    except FileNotFoundError:
        print("❌ ADB未找到，请确保Android SDK已安装并配置PATH")
        return False
    except Exception as e:
        print(f"❌ 执行失败: {e}")
        return False

def install_app():
    """安装应用"""
    print("\n=== 安装应用 ===")
    
    try:
        print("正在安装应用...")
        result = subprocess.run([
            "adb", "install", "-r", "app/build/outputs/apk/debug/app-debug.apk"
        ], capture_output=True, text=True)
        
        if result.returncode == 0:
            print("✅ 应用安装成功")
            return True
        else:
            print(f"❌ 安装失败: {result.stderr}")
            return False
            
    except FileNotFoundError:
        print("❌ ADB未找到，请确保Android SDK已安装并配置PATH")
        return False
    except Exception as e:
        print(f"❌ 执行失败: {e}")
        return False

def main():
    print("数据库权限问题修复工具")
    print("=" * 50)
    
    # 检查设备连接
    try:
        result = subprocess.run(["adb", "devices"], capture_output=True, text=True)
        if "device" not in result.stdout:
            print("❌ 未检测到Android设备，请确保设备已连接并启用USB调试")
            return
    except FileNotFoundError:
        print("❌ ADB未找到，请确保Android SDK已安装并配置PATH")
        return
    
    # 清除应用数据
    if clear_app_data():
        # 安装应用
        install_app()
    
    print("\n=== 修复完成 ===")
    print("现在可以重新启动应用，数据库升级应该能够正常工作")

if __name__ == "__main__":
    main()

