import React from 'react';
import {
  TrendingUp,
  TrendingDown,
  ShieldCheck,
  ShieldAlert,
  AlertTriangle,
  Award,
  CheckCircle2,
  MoonStar,
  CheckSquare,
  Target,
  ArrowUpRight,
  PlusCircle,
  Activity,
} from 'lucide-react';
import { useTraderLife } from '../context/TraderLifeContext';

export const Dashboard: React.FC = () => {
  const { trades, salahRecords, habits, habitLogs, settings, selectedDate, setCurrentScreen, language } = useTraderLife();

  const todayTrades = trades.filter((t) => t.date === selectedDate);
  const todayPnl = todayTrades.reduce((acc, t) => acc + t.pnl, 0);
  const wins = todayTrades.filter((t) => t.result === 'WIN').length;
  const losses = todayTrades.filter((t) => t.result === 'LOSS').length;
  const breakevens = todayTrades.filter((t) => t.result === 'BREAKEVEN').length;
  const winRate = wins + losses > 0 ? Math.round((wins / (wins + losses)) * 100) : 0;

  const target = settings.currentMonthTarget;
  const targetProgress = target > 0 ? Math.min(Math.max((todayPnl / target) * 100, 0), 200) : 0;

  const ruleViolations = todayTrades.filter(
    (t) => !t.accordingToPlan || t.fomo || t.revenge || t.overtrade
  ).length;

  const isOvertrading = todayTrades.length > settings.maxTradesPerDay;

  const todayRisked = todayTrades.reduce((acc, t) => acc + t.riskAmount, 0);
  const riskStatus =
    todayTrades.some((t) => t.pnl <= -settings.maxDailyLoss) || todayPnl <= -settings.maxDailyLoss
      ? 'BREACHED'
      : todayPnl <= -settings.maxDailyLoss * 0.7 || todayRisked > settings.demoBalance * (settings.riskPerTradePct / 100) * settings.maxTradesPerDay
      ? 'CAUTION'
      : 'SAFE';

  const todaySalah = salahRecords[selectedDate] || {
    date: selectedDate,
    fajr: 'NONE',
    dhuhr: 'NONE',
    asr: 'NONE',
    maghrib: 'NONE',
    isha: 'NONE',
  };
  const salahList = [todaySalah.fajr, todaySalah.dhuhr, todaySalah.asr, todaySalah.maghrib, todaySalah.isha];
  const salahDone = salahList.filter((s) => s === 'JAMAAT' || s === 'ALONE').length;
  const salahJamaat = salahList.filter((s) => s === 'JAMAAT').length;

  const todayHabitList = habitLogs[selectedDate] || [];
  const routineDone = todayHabitList.length;
  const routineTotal = habits.length;
  const routinePct = routineTotal > 0 ? Math.round((routineDone / routineTotal) * 100) : 0;

  // Discipline Score calculation
  let disciplineScore = 100;
  if (isOvertrading) disciplineScore -= 20;
  disciplineScore -= ruleViolations * 15;
  if (todayTrades.some((t) => t.fomo)) disciplineScore -= 15;
  if (todayTrades.some((t) => t.revenge)) disciplineScore -= 20;
  if (riskStatus === 'BREACHED') disciplineScore -= 25;
  if (routineTotal > 0 && routinePct < 50) disciplineScore -= 10;
  if (salahDone < 3) disciplineScore -= 10;
  disciplineScore = Math.max(0, Math.min(100, disciplineScore));

  const totalMonthPnl = trades.reduce((acc, t) => acc + t.pnl, 0);

  return (
    <div className="space-y-6 pb-20 lg:pb-8">
      {/* Hero Welcome Banner */}
      <div className="relative overflow-hidden rounded-3xl p-6 sm:p-8 bg-gradient-to-r from-[#10141C] via-[#161B26] to-[#10141C] border border-amber-500/30 shadow-2xl">
        <div className="relative z-10 flex flex-col md:flex-row md:items-center justify-between gap-6">
          <div className="space-y-2">
            <div className="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-amber-500/10 border border-amber-500/30 text-amber-400 text-xs font-bold uppercase tracking-wider">
              <ShieldCheck className="w-3.5 h-3.5" /> Demo Trading Journal & Discipline Tracker
            </div>
            <h1 className="text-2xl sm:text-3xl font-black text-slate-100">
              Welcome, <span className="text-amber-400">{settings.traderName}</span>
            </h1>
            <p className="text-xs sm:text-sm text-slate-300 max-w-xl italic">
              “Trading success is not about making quick money. It is about flawless discipline, risk control, and emotional peace.”
            </p>
          </div>

          <div className="flex flex-wrap items-center gap-3">
            <button
              onClick={() => setCurrentScreen('journal')}
              className="flex items-center gap-2 px-5 py-3 rounded-2xl bg-amber-500 hover:bg-amber-400 text-black font-extrabold text-xs sm:text-sm shadow-lg shadow-amber-500/20 active:scale-95 transition-all"
            >
              <PlusCircle className="w-4 h-4" /> Log Trade
            </button>
            <button
              onClick={() => setCurrentScreen('daily-review')}
              className="flex items-center gap-2 px-4 py-3 rounded-2xl bg-[#1E2534] hover:bg-[#2A3346] text-slate-200 font-semibold text-xs sm:text-sm border border-[#2A3346] transition-all"
            >
              Daily Review
            </button>
          </div>
        </div>

        {/* Ambient glow background */}
        <div className="absolute -right-10 -bottom-10 w-64 h-64 bg-amber-500/10 rounded-full blur-3xl pointer-events-none" />
        <div className="absolute -left-10 -top-10 w-64 h-64 bg-emerald-500/5 rounded-full blur-3xl pointer-events-none" />
      </div>

      {/* Main KPI Row: Today's Target vs Actual PnL & Discipline Gauge */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Today's Target vs Actual P/L */}
        <div className="lg:col-span-2 rounded-3xl p-6 glow-card border-[#1E2534] flex flex-col justify-between space-y-6">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-xs font-semibold text-slate-400 uppercase tracking-wider">
                {language === 'bn' ? 'আজকের ডেমো লাভ/ক্ষতি ও টার্গেট' : "Today's Demo P/L vs Daily Target"}
              </p>
              <div className="flex items-baseline gap-3 mt-1">
                <span
                  className={`text-3xl sm:text-4xl font-black ${
                    todayPnl >= 0 ? 'text-emerald-400' : 'text-rose-400'
                  }`}
                >
                  {todayPnl >= 0 ? '+' : ''}
                  {settings.currency}
                  {todayPnl.toFixed(2)}
                </span>
                <span className="text-xs sm:text-sm text-slate-400 font-medium">
                  / Target: {settings.currency}
                  {target.toFixed(2)}
                </span>
              </div>
            </div>

            <div
              className={`px-3 py-1.5 rounded-xl border text-xs font-bold ${
                todayPnl >= target
                  ? 'bg-emerald-500/15 border-emerald-500/40 text-emerald-400'
                  : 'bg-amber-500/15 border-amber-500/40 text-amber-400'
              }`}
            >
              {todayPnl >= target ? 'TARGET REACHED 🎯' : 'IN PROGRESS'}
            </div>
          </div>

          {/* Progress Bar */}
          <div className="space-y-2">
            <div className="flex justify-between text-xs font-semibold">
              <span className="text-slate-400">Daily Target Completion</span>
              <span className="text-amber-400 font-bold">{Math.round(targetProgress)}%</span>
            </div>
            <div className="w-full h-3 rounded-full bg-[#161B26] overflow-hidden p-0.5 border border-[#1E2534]">
              <div
                className={`h-full rounded-full transition-all duration-500 ${
                  todayPnl >= 0 ? 'bg-gradient-to-r from-emerald-500 to-teal-400' : 'bg-rose-500'
                }`}
                style={{ width: `${Math.min(targetProgress, 100)}%` }}
              />
            </div>
          </div>

          <div className="grid grid-cols-3 gap-3 pt-2 border-t border-[#1E2534] text-xs">
            <div>
              <span className="text-slate-500 block text-[11px]">Trades Today</span>
              <span className="font-bold text-slate-200">{todayTrades.length} Trades</span>
            </div>
            <div>
              <span className="text-slate-500 block text-[11px]">Win / Loss</span>
              <span className="font-bold text-slate-200">
                <span className="text-emerald-400">{wins}W</span> - <span className="text-rose-400">{losses}L</span>
              </span>
            </div>
            <div>
              <span className="text-slate-500 block text-[11px]">Current Month P/L</span>
              <span
                className={`font-bold ${
                  totalMonthPnl >= 0 ? 'text-emerald-400' : 'text-rose-400'
                }`}
              >
                {totalMonthPnl >= 0 ? '+' : ''}
                {settings.currency}
                {totalMonthPnl.toFixed(2)}
              </span>
            </div>
          </div>
        </div>

        {/* Discipline Score Gauge */}
        <div className="rounded-3xl p-6 glow-card border-[#1E2534] flex flex-col items-center justify-center text-center space-y-4">
          <div className="flex items-center gap-2">
            <Award className="w-4 h-4 text-amber-400" />
            <span className="text-xs font-bold text-slate-300 uppercase tracking-wider">
              Discipline Score
            </span>
          </div>

          {/* Radial score representation */}
          <div className="relative w-36 h-36 flex items-center justify-center">
            <svg className="w-full h-full -rotate-90" viewBox="0 0 100 100">
              <circle
                cx="50"
                cy="50"
                r="40"
                fill="none"
                stroke="#1E2534"
                strokeWidth="10"
              />
              <circle
                cx="50"
                cy="50"
                r="40"
                fill="none"
                stroke={disciplineScore >= 80 ? '#10B981' : disciplineScore >= 60 ? '#F59E0B' : '#EF4444'}
                strokeWidth="10"
                strokeDasharray="251.2"
                strokeDashoffset={251.2 - (251.2 * disciplineScore) / 100}
                strokeLinecap="round"
                className="transition-all duration-700"
              />
            </svg>
            <div className="absolute inset-0 flex flex-col items-center justify-center">
              <span className="text-3xl font-black text-slate-100">{disciplineScore}</span>
              <span className="text-[10px] text-slate-400 font-semibold uppercase">out of 100</span>
            </div>
          </div>

          <p className="text-xs text-slate-400">
            {disciplineScore >= 80
              ? '🌟 High discipline! Sticking to plan.'
              : disciplineScore >= 60
              ? '⚠️ Warning: Minor rule slippage.'
              : '🚨 Red Alert: Emotional / overtrading.'}
          </p>
        </div>
      </div>

      {/* 4 Quick Stat Tiles */}
      <div className="grid grid-cols-2 sm:grid-cols-4 gap-4">
        {/* Win Rate */}
        <div className="p-4 rounded-2xl glow-card border-[#1E2534] space-y-1">
          <div className="flex items-center justify-between text-xs text-slate-400">
            <span>Win Rate</span>
            <TrendingUp className="w-4 h-4 text-emerald-400" />
          </div>
          <p className={`text-2xl font-black ${winRate >= 50 ? 'text-emerald-400' : 'text-slate-100'}`}>
            {winRate}%
          </p>
          <p className="text-[11px] text-slate-500">{wins} Wins, {losses} Losses</p>
        </div>

        {/* Overtrading check */}
        <div className="p-4 rounded-2xl glow-card border-[#1E2534] space-y-1">
          <div className="flex items-center justify-between text-xs text-slate-400">
            <span>Trade Count</span>
            {isOvertrading ? <AlertTriangle className="w-4 h-4 text-rose-400" /> : <CheckCircle2 className="w-4 h-4 text-emerald-400" />}
          </div>
          <p className={`text-2xl font-black ${isOvertrading ? 'text-rose-400' : 'text-slate-100'}`}>
            {todayTrades.length} / {settings.maxTradesPerDay}
          </p>
          <p className="text-[11px] text-slate-500">{isOvertrading ? 'Limit Exceeded!' : 'Safe frequency'}</p>
        </div>

        {/* Risk Status */}
        <div className="p-4 rounded-2xl glow-card border-[#1E2534] space-y-1">
          <div className="flex items-center justify-between text-xs text-slate-400">
            <span>Risk Status</span>
            <ShieldAlert className="w-4 h-4 text-amber-400" />
          </div>
          <p
            className={`text-2xl font-black ${
              riskStatus === 'SAFE'
                ? 'text-emerald-400'
                : riskStatus === 'CAUTION'
                ? 'text-amber-400'
                : 'text-rose-400'
            }`}
          >
            {riskStatus}
          </p>
          <p className="text-[11px] text-slate-500">Max loss: {settings.currency}{settings.maxDailyLoss}</p>
        </div>

        {/* Rule Violations */}
        <div className="p-4 rounded-2xl glow-card border-[#1E2534] space-y-1">
          <div className="flex items-center justify-between text-xs text-slate-400">
            <span>Violations</span>
            <Activity className="w-4 h-4 text-rose-400" />
          </div>
          <p className={`text-2xl font-black ${ruleViolations > 0 ? 'text-rose-400' : 'text-emerald-400'}`}>
            {ruleViolations}
          </p>
          <p className="text-[11px] text-slate-500">{ruleViolations > 0 ? 'FOMO / Revenge' : 'Zero violations'}</p>
        </div>
      </div>

      {/* Routine & Salah Quick Summary */}
      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        {/* Salah Summary Card */}
        <div
          onClick={() => setCurrentScreen('salah')}
          className="p-5 rounded-3xl glow-card border-[#1E2534] cursor-pointer hover:border-amber-500/40 transition-all space-y-3"
        >
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-2">
              <div className="w-8 h-8 rounded-xl bg-indigo-500/15 border border-indigo-500/30 flex items-center justify-center text-indigo-400">
                <MoonStar className="w-4 h-4" />
              </div>
              <div>
                <h3 className="text-sm font-bold text-slate-100">Daily Salah Tracker</h3>
                <p className="text-[11px] text-slate-400">5 Daily Prayers & Jamaat</p>
              </div>
            </div>
            <span className="text-xs font-bold text-amber-400 flex items-center gap-1">
              {salahDone}/5 Done <ArrowUpRight className="w-3.5 h-3.5" />
            </span>
          </div>

          <div className="grid grid-cols-5 gap-2 pt-1 text-center">
            {[
              { name: 'Fajr', val: todaySalah.fajr },
              { name: 'Dhuhr', val: todaySalah.dhuhr },
              { name: 'Asr', val: todaySalah.asr },
              { name: 'Maghrib', val: todaySalah.maghrib },
              { name: 'Isha', val: todaySalah.isha },
            ].map((p) => (
              <div
                key={p.name}
                className={`py-1.5 rounded-xl border text-[11px] font-semibold ${
                  p.val === 'JAMAAT'
                    ? 'bg-emerald-500/20 border-emerald-500/40 text-emerald-300'
                    : p.val === 'ALONE'
                    ? 'bg-amber-500/20 border-amber-500/40 text-amber-300'
                    : p.val === 'MISSED'
                    ? 'bg-rose-500/20 border-rose-500/40 text-rose-300'
                    : 'bg-[#161B26] border-[#1E2534] text-slate-500'
                }`}
              >
                {p.name}
              </div>
            ))}
          </div>
        </div>

        {/* Routine Summary Card */}
        <div
          onClick={() => setCurrentScreen('routine')}
          className="p-5 rounded-3xl glow-card border-[#1E2534] cursor-pointer hover:border-emerald-500/40 transition-all space-y-3"
        >
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-2">
              <div className="w-8 h-8 rounded-xl bg-emerald-500/15 border border-emerald-500/30 flex items-center justify-center text-emerald-400">
                <CheckSquare className="w-4 h-4" />
              </div>
              <div>
                <h3 className="text-sm font-bold text-slate-100">Daily Habits & Routine</h3>
                <p className="text-[11px] text-slate-400">Consistency building</p>
              </div>
            </div>
            <span className="text-xs font-bold text-emerald-400 flex items-center gap-1">
              {routineDone}/{routineTotal} Habits ({routinePct}%) <ArrowUpRight className="w-3.5 h-3.5" />
            </span>
          </div>

          <div className="w-full h-2.5 rounded-full bg-[#161B26] overflow-hidden border border-[#1E2534]">
            <div
              className="h-full bg-emerald-400 rounded-full transition-all"
              style={{ width: `${routinePct}%` }}
            />
          </div>
        </div>
      </div>
    </div>
  );
};
