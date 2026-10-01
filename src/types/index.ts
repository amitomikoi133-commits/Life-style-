export type Screen =
  | 'dashboard'
  | 'journal'
  | 'risk-manager'
  | 'psychology'
  | 'routine'
  | 'salah'
  | 'goals'
  | 'analytics'
  | 'daily-review'
  | 'settings';

export type Language = 'en' | 'bn' | 'both';

export type TradeResult = 'WIN' | 'LOSS' | 'BREAKEVEN' | 'OPEN';
export type TradeDirection = 'BUY' | 'SELL';

export interface Trade {
  id: string;
  date: string; // YYYY-MM-DD
  time: string; // HH:mm
  pair: string;
  direction: TradeDirection;
  entryPrice: number;
  stopLoss: number;
  takeProfit: number;
  riskPercent: number;
  riskAmount: number;
  result: TradeResult;
  pnl: number;
  setup: string;
  reason: string;
  emotionBefore: string;
  emotionDuring: string;
  accordingToPlan: boolean;
  fomo: boolean;
  revenge: boolean;
  overtrade: boolean;
  mistake: string;
  lesson: string;
  screenshot?: string;
  createdAt: number;
}

export type SalahStatus = 'JAMAAT' | 'ALONE' | 'MISSED' | 'NONE';

export interface SalahRecord {
  date: string; // YYYY-MM-DD
  fajr: SalahStatus;
  dhuhr: SalahStatus;
  asr: SalahStatus;
  maghrib: SalahStatus;
  isha: SalahStatus;
  notes?: string;
}

export interface Habit {
  id: string;
  titleEn: string;
  titleBn: string;
  icon: string;
  category: string;
  order: number;
}

export interface HabitLog {
  date: string; // YYYY-MM-DD
  completedHabitIds: string[];
}

export interface PsychologyLog {
  id: string;
  date: string;
  time: string;
  calm: number; // 1-5
  fear: number; // 1-5
  greed: number; // 1-5
  fomo: number; // 1-5
  revenge: number; // 1-5
  confidence: number; // 1-5
  stress: number; // 1-5
  note: string;
  sessionType: 'Pre-Market' | 'Active-Session' | 'Post-Market' | 'Trade-Reflection';
  createdAt: number;
}

export interface MonthlyGoal {
  month: number; // 1-12
  targetPerDay: number;
  title: string;
  notes: string;
}

export interface DailyReview {
  date: string; // YYYY-MM-DD
  didWell: string;
  biggestMistake: string;
  mistakeReason: string;
  improvement: string;
  lesson: string;
  disciplineScore: number; // 1-10
  updatedAt: number;
}

export interface UserSettings {
  traderName: string;
  currency: string;
  demoBalance: number;
  riskPerTradePct: number;
  maxDailyLoss: number;
  maxTradesPerDay: number;
  currentMonthTarget: number;
  language: Language;
  pinLockEnabled: boolean;
  pinCode: string;
  theme: 'dark-gold' | 'dark-emerald' | 'cyber-slate';
}
