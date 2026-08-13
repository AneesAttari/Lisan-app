# Lisan AI - Secure Backend Proxy

This is a lightweight, secure Node.js backend proxy designed to protect developer API keys for **Lisan AI** Android applications. By routing API requests from the Android app to this backend instead of calling Gemini directly, we completely eliminate the risk of reverse-engineering decompiled APKs to extract sensitive credentials.

## Production Flow

```
Android App (No Key) 
      ↓ HTTPS (Authorized with BACKEND_API_SECRET)
Secure Backend Proxy (Stores developer key safely)
      ↓ HTTPS (Authorized with GEMINI_API_KEY)
Google Gemini API (Generates replies)
```

---

## Production Deployment Guide

You can deploy this lightweight service to any hosting provider that supports Node.js. Recommended platforms for low latency and zero cold starts include:

### Option A: Google Cloud Run (Recommended for Google Cloud Ecosystem)
Since Lisan AI uses Google Gemini, running on Google Cloud Run offers premium security, high speed, and low latency.
1. Install [Google Cloud CLI](https://cloud.google.com/sdk/docs/install).
2. Authenticate: `gcloud auth login` and `gcloud config set project YOUR_PROJECT_ID`.
3. Set your secrets using Google Cloud Secret Manager or directly in Cloud Run:
   - Go to Google Cloud Console → Cloud Run.
   - Click **Create Service**.
   - Set Environment Variables:
     - `GEMINI_API_KEY` = `your_gemini_api_key`
     - `BACKEND_API_SECRET` = `your_secret_app_token_used_by_android`
4. Deploy the source:
   ```bash
   gcloud run deploy lisan-ai-backend --source . --platform managed --allow-unauthenticated
   ```
5. Note the returned Service URL (e.g. `https://lisan-ai-backend-xxxxxx.a.run.app`). Update `BACKEND_URL` in the Android `.env` file or code with this URL.

### Option B: Render or Railway (Fast & Simple)
1. Push this `/backend` directory to a private GitHub repository.
2. Sign in to [Render](https://render.com/) or [Railway](https://railway.app/).
3. Create a new **Web Service** and link it to your GitHub repository.
4. Set the build and start commands:
   - **Build Command:** `npm install`
   - **Start Command:** `npm start`
5. Under **Environment Variables**, configure:
   - `GEMINI_API_KEY`: Your confidential Google Gemini API key (from Google AI Studio).
   - `BACKEND_API_SECRET`: A secure, random token (e.g., `lisan_secure_tutor_token_2026`) used by your Android app to authorize requests.
6. Copy the provided domain URL (e.g. `https://lisan-ai-backend.onrender.com`).

---

## Environment Variables Configuration

Create a `.env` file in the root of `/backend` during local testing:

```properties
PORT=3000
GEMINI_API_KEY=AIzaSyYourConfidentialGeminiKeyHere
BACKEND_API_SECRET=lisan_secure_tutor_token_2026
```

- **`GEMINI_API_KEY` (Secret):** Highly confidential. Never commit this key or share it.
- **`BACKEND_API_SECRET` (Shared Secret):** Must match the `BACKEND_API_SECRET` configured in the Android app (`.env` or `BuildConfig`). It prevents unauthorized users from calling your backend proxy to drain your quota.
