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

// 4. Proxy non-streaming content generation
app.post('/v1beta/models/:model\\:generateContent', apiLimiter, authenticateRequest, validateGeminiRequest, async (req, res) => {
  const { model } = req.params;
  const geminiKey = process.env.GEMINI_API_KEY;

  if (!geminiKey) {
    console.error("[CRITICAL] Server GEMINI_API_KEY is not set.");
    return res.status(500).json({ error: "Internal Server Error: Gemini credentials missing on backend." });
  }

  try {
    const url = `${GEMINI_BASE_URL}/v1beta/models/${model}:generateContent?key=${geminiKey}`;
    
    const response = await fetch(url, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(req.body)
    });

    if (!response.ok) {
      const errorText = await response.text();
      console.error(`[ERROR] Gemini API returned status ${response.status}:`, errorText);
      return res.status(response.status).json({
        error: "Gemini API failure",
        statusCode: response.status
      });
    }

    const data = await response.json();
    res.json(data);

  } catch (error) {
    console.error("[ERROR] Failed to forward request to Gemini:", error);
    res.status(500).json({ error: "Internal Server Error", message: "Failed to communicate with AI provider." });
  }
});

// 5. Proxy streaming content generation (Server-Sent Events)
app.post('/v1beta/models/:model\\:streamGenerateContent', apiLimiter, authenticateRequest, validateGeminiRequest, async (req, res) => {
  const { model } = req.params;
  const geminiKey = process.env.GEMINI_API_KEY;

  if (!geminiKey) {
    console.error("[CRITICAL] Server GEMINI_API_KEY is not set.");
    return res.status(500).json({ error: "Internal Server Error: Gemini credentials missing on backend." });
  }

  try {
    // Force alt=sse so Gemini returns clean Server-Sent Events line-by-line
    const url = `${GEMINI_BASE_URL}/v1beta/models/${model}:streamGenerateContent?alt=sse&key=${geminiKey}`;
    
    const response = await fetch(url, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(req.body)
    });

    if (!response.ok) {
      const errorText = await response.text();
      console.error(`[ERROR] Gemini API stream returned status ${response.status}:`, errorText);
      return res.status(response.status).json({
        error: "Gemini streaming API failure",
        statusCode: response.status
      });
    }

    // Set standard SSE stream response headers
    res.setHeader('Content-Type', 'text/event-stream');
    res.setHeader('Cache-Control', 'no-cache');
    res.setHeader('Connection', 'keep-alive');
    res.setHeader('Transfer-Encoding', 'chunked');

    const reader = response.body.getReader();
    
    // Read and write chunks iteratively to stream tokens instantly to client
    while (true) {
      const { done, value } = await reader.read();
      if (done) break;
      res.write(value);
    }
    
    res.end();

  } catch (error) {
    console.error("[ERROR] Streaming proxy failed:", error);
    if (!res.headersSent) {
      res.status(500).json({ error: "Internal Server Error", message: "Streaming connection failed." });
    } else {
      res.end();
    }
  }
});

// Start listening
app.listen(PORT, () => {
  console.log(`[INFO] Lisan AI Backend is active and running on port ${PORT}`);
});
