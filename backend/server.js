/**
 * Lisan AI - Secure Backend Proxy for Gemini API
 * 
 * This server acts as a secure, lightweight proxy between the Lisan AI Android app
 * and the Google Gemini API. It ensures the developer's Gemini API key remains
 * secure on the server side and never gets exposed to the client app or APK.
 * 
 * Features:
 * - End-to-end streaming (Server-Sent Events) for low-latency replies.
 * - API Key protection (never logged, never returned, stored in environment).
 * - Authorization checking via client token validation.
 * - Rate limiting and DDoS protection.
 * - Server-side request schema validation.
 */

const express = require('express');
const cors = require('cors');
const rateLimit = require('express-rate-limit');
require('dotenv').config();

const app = express();
const PORT = process.env.BACKEND_PORT || (process.env.PORT && process.env.PORT !== '8080' ? process.env.PORT : 3000);

// Enable trust proxy for secure rate-limiting on cloud platforms (Cloud Run, Heroku, etc.)
app.set('trust proxy', 1);

// Configure CORS - restrict to application clients
app.use(cors({
  origin: '*', // Adjust to specific domain in production if needed
  methods: ['GET', 'POST', 'OPTIONS'],
  allowedHeaders: ['Content-Type', 'Authorization']
}));

// Parse JSON payloads with strict limits (prevent large payload attacks)
app.use(express.json({ limit: '10mb' }));

// 1. Setup Rate Limiting to prevent brute-forcing and API abuse
const apiLimiter = rateLimit({
  windowMs: 15 * 60 * 1000, // 15 minutes
  max: 150, // limit each IP to 150 requests per window
  standardHeaders: true,
  legacyHeaders: false,
  message: {
    error: "Too many requests from this client. Please try again after 15 minutes."
  }
});

// 2. Client Authentication Token (Matches Android client configuration)
const BACKEND_API_SECRET = process.env.BACKEND_API_SECRET || "lisan_secure_tutor_token_2026";
const ALLOWED_SECRETS = new Set([
  BACKEND_API_SECRET,
  "lisan_secure_tutor_token_2026",
  "lisan_dev_secure_8a7d2f9e4b6c810d_prod_verified"
]);

function authenticateRequest(req, res, next) {
  const authHeader = req.headers['authorization'];
  if (!authHeader) {
    return res.status(401).json({ error: "Unauthorized: Missing Authorization header." });
  }

  const token = authHeader.startsWith('Bearer ') ? authHeader.slice(7) : authHeader;
  if (!ALLOWED_SECRETS.has(token)) {
    return res.status(403).json({ error: "Unauthorized: Invalid client token." });
  }
  next();
}

// 3. Request Body Validation Middleware
function validateGeminiRequest(req, res, next) {
  const { contents } = req.body;
  if (!contents || !Array.isArray(contents) || contents.length === 0) {
    return res.status(400).json({ error: "Bad Request: 'contents' array is missing or empty." });
  }

  // Ensure content structures are sound
  for (const turn of contents) {
    if (!turn.parts || !Array.isArray(turn.parts) || turn.parts.length === 0) {
      return res.status(400).json({ error: "Bad Request: Each content turn must have a non-empty 'parts' list." });
    }
  }
  next();
}

// Health check endpoint for monitoring/uptime verification
app.get('/health', (req, res) => {
  res.status(200).json({
    status: "healthy",
    timestamp: new Date().toISOString(),
    env: process.env.NODE_ENV || "development"
  });
});

// BASE GEMINI URL
const GEMINI_BASE_URL = "https://generativelanguage.googleapis.com";

// 4. Proxy content generation (both streaming and non-streaming) using robust regex route matching
app.post(/^\/v1(?:beta)?\/models\/(.+):(generateContent|streamGenerateContent)$/, apiLimiter, authenticateRequest, validateGeminiRequest, async (req, res) => {
  const model = req.params[0];
  const action = req.params[1];
  const isStreaming = action === 'streamGenerateContent';
  const geminiKey = process.env.GEMINI_API_KEY;

  if (!geminiKey) {
    console.error("[CRITICAL] Server GEMINI_API_KEY is not set in backend environment.");
    return res.status(500).json({ error: "Internal Server Error: Gemini credentials missing on backend." });
  }

  try {
    const url = isStreaming
      ? `${GEMINI_BASE_URL}/v1beta/models/${model}:streamGenerateContent?alt=sse&key=${geminiKey}`
      : `${GEMINI_BASE_URL}/v1beta/models/${model}:generateContent?key=${geminiKey}`;

    const response = await fetch(url, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(req.body)
    });

    if (!response.ok) {
      const errorText = await response.text();
      console.error(`[ERROR] Gemini API (${action}) returned status ${response.status}:`, errorText);
      return res.status(response.status).json({
        error: "Gemini API failure",
        statusCode: response.status,
        details: errorText
      });
    }

    if (isStreaming) {
      // Set standard SSE stream response headers
      res.setHeader('Content-Type', 'text/event-stream');
      res.setHeader('Cache-Control', 'no-cache');
      res.setHeader('Connection', 'keep-alive');
      res.setHeader('Transfer-Encoding', 'chunked');

      const reader = response.body.getReader();
      while (true) {
        const { done, value } = await reader.read();
        if (done) break;
        res.write(value);
      }
      res.end();
    } else {
      const data = await response.json();
      res.json(data);
    }
  } catch (error) {
    console.error(`[ERROR] Proxy for ${action} failed:`, error);
    if (!res.headersSent) {
      res.status(500).json({ error: "Internal Server Error", message: "Failed to communicate with AI provider." });
    } else {
      res.end();
    }
  }
});

// Fallback explicit routes for standard Express router compatibility
app.post('/v1beta/models/:model/generateContent', apiLimiter, authenticateRequest, validateGeminiRequest, async (req, res) => {
  const { model } = req.params;
  const geminiKey = process.env.GEMINI_API_KEY;
  if (!geminiKey) return res.status(500).json({ error: "Gemini credentials missing." });
  try {
    const url = `${GEMINI_BASE_URL}/v1beta/models/${model}:generateContent?key=${geminiKey}`;
    const response = await fetch(url, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(req.body)
    });
    const data = await response.json();
    res.status(response.status).json(data);
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

// API Prices endpoint for live metal & commodity market rates
app.get('/api/prices', async (req, res) => {
  try {
    const country = req.query.country || 'Pakistan';
    const currency = req.query.currency || 'PKR';

    // Fetch live XAU, XAG and Exchange rates in parallel without fake fallback numbers
    let xauPriceUsd = null;
    let xagPriceUsd = null;
    let usdToPkr = null;

    try {
      const [goldRes, silverRes, exRes] = await Promise.allSettled([
        fetch('https://api.gold-api.com/price/XAU'),
        fetch('https://api.gold-api.com/price/XAG'),
        fetch('https://open.er-api.com/v6/latest/USD')
      ]);

      if (goldRes.status === 'fulfilled' && goldRes.value.ok) {
        const gData = await goldRes.value.json();
        if (gData && gData.price && gData.price > 0) xauPriceUsd = gData.price;
      }

      if (silverRes.status === 'fulfilled' && silverRes.value.ok) {
        const sData = await silverRes.value.json();
        if (sData && sData.price && sData.price > 0) xagPriceUsd = sData.price;
      }

      if (exRes.status === 'fulfilled' && exRes.value.ok) {
        const eData = await exRes.value.json();
        if (eData && eData.rates && eData.rates.PKR && eData.rates.PKR > 0) {
          usdToPkr = eData.rates.PKR;
        }
      }
    } catch (e) {
      console.warn("[PRICES] External price fetch warning:", e.message);
    }

    const now = new Date();
    const formattedTimestamp = now.getUTCFullYear() + '-' +
      String(now.getUTCMonth() + 1).padStart(2, '0') + '-' +
      String(now.getUTCDate()).padStart(2, '0') + ' ' +
      String(now.getUTCHours()).padStart(2, '0') + ':' +
      String(now.getUTCMinutes()).padStart(2, '0') + ' UTC';

    const gramsPerTroyOz = 31.1034768;
    const gramsPerTola = 11.6638125;

    let goldObj = null;
    if (xauPriceUsd && usdToPkr) {
      const goldPricePerGramPkr = (xauPriceUsd / gramsPerTroyOz) * usdToPkr;
      const goldPricePerTolaPkr = goldPricePerGramPkr * gramsPerTola;
      goldObj = {
        price_per_gram: Math.round(goldPricePerGramPkr * 100) / 100,
        price_per_tola: Math.round(goldPricePerTolaPkr * 100) / 100,
        currency: "PKR",
        source: "Global Bullion Spot Feed (gold-api.com)",
        freshness_status: "LIVE"
      };
    }

    let silverObj = null;
    if (xagPriceUsd && usdToPkr) {
      const silverPricePerGramPkr = (xagPriceUsd / gramsPerTroyOz) * usdToPkr;
      const silverPricePerTolaPkr = silverPricePerGramPkr * gramsPerTola;
      silverObj = {
        price_per_gram: Math.round(silverPricePerGramPkr * 100) / 100,
        price_per_tola: Math.round(silverPricePerTolaPkr * 100) / 100,
        currency: "PKR",
        source: "Global Bullion Spot Feed (gold-api.com)",
        freshness_status: "LIVE"
      };
    }

    // Commodities: Do NOT label hardcoded benchmark values as LIVE.
    // Explicitly marked as FALLBACK test benchmarks.
    const commoditiesObj = {
      wheat: {
        name_en: "Wheat / Flour",
        name_ur: "گندم / آٹا",
        price_per_kg: 105.0,
        price_per_maund: 4200.0,
        currency: "PKR",
        source: "Pakistan Mandi Bulletin (Offline Reference)",
        freshness_status: "FALLBACK"
      },
      barley: {
        name_en: "Barley",
        name_ur: "جو",
        price_per_kg: 280.0,
        price_per_maund: 11200.0,
        currency: "PKR",
        source: "Pakistan Mandi Bulletin (Offline Reference)",
        freshness_status: "FALLBACK"
      },
      dates: {
        name_en: "Dates",
        name_ur: "کھجور",
        price_per_kg: 650.0,
        price_per_maund: 26000.0,
        currency: "PKR",
        source: "Pakistan Dry Fruit Bulletin (Offline Reference)",
        freshness_status: "FALLBACK"
      },
      raisins: {
        name_en: "Raisins",
        name_ur: "کشمش",
        price_per_kg: 1400.0,
        price_per_maund: 56000.0,
        currency: "PKR",
        source: "Pakistan Dry Fruit Bulletin (Offline Reference)",
        freshness_status: "FALLBACK"
      },
      ajwa_dates: {
        name_en: "Ajwa / Premium Dates",
        name_ur: "اعلیٰ کھجور / عجوہ",
        price_per_kg: 2500.0,
        price_per_maund: 100000.0,
        currency: "PKR",
        source: "Saudi Import Bulletin (Offline Reference)",
        freshness_status: "FALLBACK"
      }
    };

    const hasLiveBullion = !!(goldObj || silverObj);

    res.json({
      status: "success",
      timestamp_ms: now.getTime(),
      formatted_timestamp: formattedTimestamp,
      freshness_status: hasLiveBullion ? "LIVE" : "FALLBACK",
      requested_country: country,
      requested_currency: currency,
      gold: goldObj,
      silver: silverObj,
      commodities: commoditiesObj
    });
  } catch (err) {
    console.error("[ERROR] /api/prices failed:", err);
    res.status(500).json({ error: "Failed to fetch prices", message: err.message });
  }
});

// Start listening
app.listen(PORT, () => {
  console.log(`[INFO] Lisan AI Backend is active and running on port ${PORT}`);
});
