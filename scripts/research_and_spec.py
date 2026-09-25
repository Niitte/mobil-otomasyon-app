import os
import requests
import json
import sys
import time

GEMINI_API_KEY = os.environ.get("GEMINI_API_KEY")
GITHUB_TOKEN = os.environ.get("GITHUB_TOKEN")
REPO = os.environ.get("GITHUB_REPOSITORY")
APP_IDEA = os.environ.get("APP_IDEA", "Minimalist Pomodoro and Task Tracker")

if not GEMINI_API_KEY:
    print("HATA: GEMINI_API_KEY bulunamadı.")
    sys.exit(1)

if not GITHUB_TOKEN:
    print("HATA: GITHUB_TOKEN bulunamadı.")
    sys.exit(1)

def ask_gemini(prompt):
    # En güncel ve kararlı iş motoru
    url = f"https://generativelanguage.googleapis.com/v1beta/models/gemini-3.7-flash:generateContent?key={GEMINI_API_KEY}"
    headers = {"Content-Type": "application/json"}
    payload = {
        "contents": [{"parts": [{"text": prompt}]}],
        "generationConfig": {
            "thinkingConfig": {
                "thinkingBudget": 0  # Hızlı yanıt için düşünme bütçesini optimize eder
            }
        }
    }

    for attempt in range(4):
        print(f"Gemini-3.7-flash çağrılıyor (Deneme {attempt + 1}/4)...")
        response = requests.post(url, headers=headers, json=payload)
        res_data = response.json()

        if response.status_code == 200 and "candidates" in res_data:
            return res_data['candidates'][0]['content']['parts'][0]['text']

        print(f"Sunucu yanıtı (Kod: {response.status_code}): {res_data}")
        time.sleep(4)

    print("HATA: İstek tamamlanamadı.")
    sys.exit(1)

prompt = f"""
Sen bir Kıdemli Mobil Yazılım Mimarı ve Araştırmacısısın.
Fikir: {APP_IDEA}

Aşağıdaki başlıklarda teknik mimariyi ve görev listesini hazırla:
1. Pazar ve Özellik Analizi
2. Veri Modelleri ve Katman Mimarisi (MVVM)
3. Google Jules Kodlama Ajanı için Görev Listesi (Task breakdown)

Çıktıyı temiz Markdown formatında ver.
"""

print("Gemini araştırmayı yürütüyor...")
spec = ask_gemini(prompt)

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

issue_res = requests.post(issue_url, headers=headers, json=issue_data)
if issue_res.status_code == 201:
    print("Issue başarıyla açıldı ve Jules göreve çağrıldı.")
else:
    print(f"Issue açılırken hata oluştu ({issue_res.status_code}): {issue_res.text}")
    sys.exit(1)
