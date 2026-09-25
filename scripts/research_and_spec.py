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
    # En stabil ve güncel üretim motoru
    url = f"https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key={GEMINI_API_KEY}"
    headers = {"Content-Type": "application/json"}
    payload = {
        "contents": [{"parts": [{"text": prompt}]}]
    }

    # 530/503 yoğunluk hatalarına karşı süreleri uzatarak 5 kez deneme
    for attempt in range(5):
        print(f"Gemini-3.5-flash çağrılıyor (Deneme {attempt + 1}/5)...")
        try:
            response = requests.post(url, headers=headers, json=payload, timeout=60)
            res_data = response.json()

            if response.status_code == 200 and "candidates" in res_data:
                print("Yanıt başarıyla alındı.")
                return res_data['candidates'][0]['content']['parts'][0]['text']

            print(f"Sunucu meşgul (HTTP {response.status_code}): {res_data}")
        except Exception as e:
            print(f"Bağlantı hatası: {str(e)}")

        # Her başarısız denemede bekleme süresini artır (10sn, 20sn, 30sn...)
        wait_time = (attempt + 1) * 10
        print(f"Yoğunluk nedeniyle {wait_time} saniye bekleniyor...")
        time.sleep(wait_time)

    print("HATA: Sunucu yoğunluğu geçmedi. Lütfen 5 dakika sonra Actions sekmesinden tekrar 'Re-run jobs' deyin.")
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
