import os
import requests
import json

GEMINI_API_KEY = os.environ.get("GEMINI_API_KEY")
GITHUB_TOKEN = os.environ.get("GITHUB_TOKEN")
REPO = os.environ.get("GITHUB_REPOSITORY")
APP_IDEA = os.environ.get("APP_IDEA", "Minimalist Pomodoro and Task Tracker")

def ask_gemini(prompt):
    url = f"https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-pro:generateContent?key={GEMINI_API_KEY}"
    payload = {
        "contents": [{"parts": [{"text": prompt}]}]
    }
    res = requests.post(url, json=payload).json()
    return res['candidates'][0]['content']['parts'][0]['text']

# 1. Araştırma ve Mimari Tasarımı
prompt = f"""
Sen bir Kıdemli Mobil Yazılım Mimarı ve Araştırmacısısın.
Fikir: {APP_IDEA}

Aşağıdaki başlıklarda teknik mimariyi ve görev listesini hazırla:
1. Pazar ve Özellik Analizi
2. Veri Modelleri ve Katman Mimarisi (MVVM)
3. Jules Kodlama Ajanı için Görev Listesi (Task breakdown)

Çıktıyı temiz Markdown formatında ver.
"""

spec = ask_gemini(prompt)

# 2. GitHub Issue Aç ve 'jules' etiketi ekle (Jules'u tetikler)
issue_url = f"https://api.github.com/repos/{REPO}/issues"
headers = {
    "Authorization": f"Bearer {GITHUB_TOKEN}",
    "Accept": "application/vnd.github.v3+json"
}
issue_data = {
    "title": f"[Feature Spec]: {APP_IDEA}",
    "body": spec,
    "labels": ["jules"]
}
requests.post(issue_url, headers=headers, json=issue_data)
print("Issue açıldı ve Jules göreve çağrıldı.")
