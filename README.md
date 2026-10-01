# 📈 My Trader Life — Personal Discipline & Demo Trading Journal

> **Modern, self-contained personal life and paper/demo trading journal web application built with React 18, TypeScript, Tailwind CSS, and Lucide Icons.**

---

## 🚀 Quick Start (Local Setup)

### Prerequisites
- Node.js (version 18 or higher)
- npm (comes bundled with Node.js)

### Installation
```bash
# 1. Install dependencies
npm install

# 2. Start the local development server
npm run dev
```
Open [http://localhost:3000](http://localhost:3000) (or the port shown in your terminal) in your web browser.

---

## 📦 Production Build

To create an optimized, static production build ready for deployment:

```bash
npm run build
```

This compiles TypeScript and outputs production-ready HTML, CSS, and JavaScript bundles inside the `dist/` folder.

---

## 🌐 Deployment Guide (Free Hosting Services)

### 1. Cloudflare Pages (Recommended)
1. Log in to your [Cloudflare Dashboard](https://dash.cloudflare.com/) and navigate to **Workers & Pages**.
2. Click **Create Application** → **Pages** → **Connect to Git** (or direct upload).
3. Set your build configuration:
   - **Framework Preset**: `Vite`
   - **Build command**: `npm run build`
   - **Build output directory**: `dist`
4. Click **Save and Deploy**. Your web app is live on Cloudflare's global edge network with zero config!

### 2. Vercel
1. Install Vercel CLI or connect your GitHub repository to [Vercel](https://vercel.com).
2. Framework Preset: `Vite`
3. Root Directory: `./`
4. Build Command: `npm run build`
5. Output Directory: `dist`
6. Click **Deploy**.

### 3. Netlify
1. Go to [Netlify](https://www.netlify.com/) and choose **Add new site** → **Import an existing project**.
2. Set **Build command**: `npm run build`
3. Set **Publish directory**: `dist`
4. Click **Deploy Site**.

---

## ✨ Features & Modules

1. **Dashboard**
   - Today's demo trading P/L vs Daily target progress bar
   - Discipline score radial gauge (0–100%)
   - Win/Loss counts, win rate %, and overtrading detector
   - Daily Salah and Routine completion quick cards

2. **Trading Journal**
   - Comprehensive paper trade logging (Pair, Entry, Stop Loss, Take Profit, Risk %, Risk $, Result, P/L, Setup, Confluence reason, Pre & Post-trade emotions, FOMO/Revenge/Overtrade flags, Mistake analysis, Lesson learned, and Chart Screenshot upload).
   - Real-time search by pair, setup, mistake, or lesson.
   - Filter chips by Result (Win/Loss/BE), Direction (Buy/Sell), and Rule violations.

3. **Risk Manager & Position Size Calculator**
   - Interactive lot size, R:R ratio, dollar risk, and dollar reward calculator.
   - Configurable capital balance, risk per trade %, and max daily loss limits.
   - Real-time **Daily Risk Status** alert card (`SAFE`, `CAUTION`, `BREACHED`).

4. **Psychology & Emotions Tracker**
   - Pre-market mental readiness test (Sleep, Patience, Pre-accepted risk, Distraction-free).
   - 1–5 scale emotion sliders (Calmness, Confidence, Stress, Fear, Greed, FOMO, Revenge).
   - Mindset reflection journaling and historical emotional check-in logs.

5. **Daily Routine & Habit Tracker**
   - Pre-configured core trading discipline habits.
   - Add custom habits with English and Bengali labels.
   - Real-time checklist with daily progress percentage.

6. **Salah Tracker (5 Daily Prayers)**
   - Fajr, Dhuhr, Asr, Maghrib, and Isha.
   - One-tap status recording: **Jamaat (জামাত)**, **Alone (একাকী)**, or **Missed (কাজা)**.
   - Daily completion progress and monthly consistency statistics.

7. **Monthly Goals Roadmap**
   - 12-Month progression roadmap (Month 1 = $1/day, Month 2 = $2/day, Month 3 = $3/day, Month 4 = $4/day...).
   - Editable targets, actual daily average P/L, and month completion percentage.

8. **Analytics & Performance Reports**
   - All-time Net P/L, Profit Factor, Average Win vs Average Loss ratio.
   - Interactive P/L bar chart.
   - Rule violation breakdown (FOMO, Revenge, Overtrades, Off-Plan).
   - Habit & Salah consistency correlation.

9. **Daily Review (End-of-Day Reflection)**
   - 5 guided reflection questions (Did well, Biggest mistake, Mistake reason, Tomorrow's improvement, Lesson learned).
   - 1–10 discipline score rating and past reviews archive.

10. **Settings & Data Security**
    - Trader Profile customization & currency selector (`$`, `৳`, `€`, `£`, `₹`).
    - Bilingual English & Bengali (বাংলা) language toggles.
    - Security PIN Lock protection.
    - **100% Offline JSON Backup & Restore**: Export all journal data and restore at any time.

---

## 🔒 Privacy & Local Storage Guarantee
All journal entries, risk parameters, habits, prayers, and psychological notes are stored directly in your browser's encrypted local storage (`localStorage`). No third-party servers, tracking, or cloud brokers are involved.

---

## 📄 License
MIT License. Open-source and free for personal development and trading discipline tracking.
