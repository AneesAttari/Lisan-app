import urllib.request
import json
import os

api_key = os.environ.get("GEMINI_API_KEY")
url = f"https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-pro:generateContent?key={api_key}"

payload = {
  "contents": [{
    "parts": [{"text": "Return a JSON object mapping the following words to a single representative emoji. Words: apple, dog, house, car, cat, sun. Only output JSON."}]
  }],
  "generationConfig": {
    "responseMimeType": "application/json"
  }
}

req = urllib.request.Request(url, data=json.dumps(payload).encode(), headers={'Content-Type': 'application/json'}, method='POST')
try:
    response = urllib.request.urlopen(req)
    res_data = json.loads(response.read().decode())
    print(res_data['candidates'][0]['content']['parts'][0]['text'])
except Exception as e:
    print(e)
    if hasattr(e, 'read'):
        print(e.read().decode())
