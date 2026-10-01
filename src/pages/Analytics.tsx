import React from 'react';
import {
  BarChart3,
  TrendingUp,
  TrendingDown,
  PieChart,
  ShieldAlert,
  Award,
  CheckCircle2,
  Activity,
} from 'lucide-react';
import { useTraderLife } from '../context/TraderLifeContext';

export const Analytics: React.FC = () => {
  const { trades, habits, habitLogs, salahRecords, settings } = useTraderLife();

  const totalTrades = trades.length;
  const winningTrades = trades.filter((t) => t.result === 'WIN');
  const losingTrades = trades.filter((t) => t.result === 'LOSS');
  const beTrades = trades.filter((t) => t.result === 'BREAKEVEN');

  const winsCount = winningTrades.length;
  const lossCount = losingTrades.length;
  const beCount = beTrades.length;
  const winRate = winsCount + lossCount > 0 ? Math.round((winsCount / (winsCount + lossCount)) * 100) : 0;

  const totalPnl = trades.reduce((acc, t) => acc + t.pnl, 0);
  const grossProfit = winningTrades.reduce((acc, t) => acc + t.pnl, 0);
  const grossLoss = Math.abs(losingTrades.reduce((acc, t) => acc + t.pnl, 0));

  const avgWin = winsCount > 0 ? grossProfit / winsCount : 0;
  const avgLoss = lossCount > 0 ? grossLoss / lossCount : 0;
  const profitFactor = grossLoss > 0 ? (grossProfit / grossLoss).toFixed(2) : grossProfit > 0 ? '9.99' : '0.00';

  const fomoCount = trades.filter((t) => t.fomo).length;
  const revengeCount = trades.filter((t) => t.revenge).length;
  const overtradeCount = trades.filter((t) => t.overtrade).length;
  const offPlanCount = trades.filter((t) => !t.accordingToPlan).length;

  // Habit consistency
  const allDaysCount = Math.max(Object.keys(salahRecords).length, 1);
  const totalHabitPossible = allDaysCount * Math.max(habits.length, 1);
  let totalHabitDone = 0;
  Object.values(habitLogs).forEach((list) => {
    totalHabitDone += list.length;
  });
  const habitConsistency = Math.min(Math.round((totalHabitDone / totalHabitPossible) * 100), 100);

  // Salah consistency
  let totalPrayersDone = 0;
  Object.values(salahRecords).forEach((rec) => {
    [rec.fajr, rec.dhuhr, rec.asr, rec.maghrib, rec.isha].forEach((status) => {
      if (status === 'JAMAAT' || status === 'ALONE') totalPrayersDone++;
    });
  });
  const salahConsistency = Math.min(Math.round((totalPrayersDone / (allDaysCount * 5)) * 100), 100);

  // Recent 10 trades for bar chart
  const recentTrades = [...trades].reverse().slice(-10);
  const maxBarPnl = Math.max(...recentTrades.map((t) => Math.abs(t.pnl)), 50);

  return (
    <div className="space-y-6 pb-20 lg:pb-8">
      {/* Title */}
      <div>
        <h1 className="text-xl sm:text-2xl font-black text-slate-100">Analytics & Performance Reports</h1>
        <p className="text-xs text-slate-400">Statistical patterns, discipline metrics and correlation analysis</p>
      </div>

      {/* Main KPI Banner */}
      <div
        className={`p-6 sm:p-8 rounded-3xl glow-card border space-y-4 ${
          totalPnl >= 0 ? 'border-emerald-500/40' : 'border-rose-500/40'
        }`}
      >
        <span className="text-xs font-bold text-slate-400 uppercase tracking-wider block">
          All-Time Demo Trading Summary
        </span>

        <div className="grid grid-cols-2 sm:grid-cols-4 gap-6">
          <div>
            <span className="text-slate-400 text-xs block">Net P/L</span>
            <span
              className={`text-2xl sm:text-3xl font-black ${
                totalPnl >= 0 ? 'text-emerald-400' : 'text-rose-400'
              }`}
            >
              {totalPnl >= 0 ? '+' : ''}
              {settings.currency}
              {totalPnl.toFixed(2)}
            </span>
          </div>

          <div>
            <span className="text-slate-400 text-xs block">Profit Factor</span>
            <span className="text-2xl sm:text-3xl font-black text-amber-400">{profitFactor}</span>
          </div>

          <div>
            <span className="text-slate-400 text-xs block">Win Rate</span>
            <span className="text-2xl sm:text-3xl font-black text-emerald-400">{winRate}%</span>
          </div>

          <div>
            <span className="text-slate-400 text-xs block">Total Trades</span>
            <span className="text-2xl sm:text-3xl font-black text-slate-100">{totalTrades}</span>
          </div>
        </div>
      </div>

      {/* P/L Bar Chart (SVG Based) */}
      <div className="p-6 rounded-3xl glow-card border-[#1E2534] space-y-4">
        <div className="flex items-center justify-between">
          <div className="flex items-center gap-2">
            <BarChart3 className="w-5 h-5 text-amber-400" />
            <h2 className="text-base font-bold text-slate-100">Recent Trades P/L ({settings.currency})</h2>
          </div>
          <span className="text-xs text-slate-400 font-semibold">Last {recentTrades.length} Trades</span>
        </div>

        {recentTrades.length === 0 ? (
          <p className="text-xs text-slate-500 py-8 text-center">No trades logged yet to display chart.</p>
        ) : (
          <div className="pt-6 pb-2">
            <div className="relative h-44 flex items-center justify-between gap-2 border-b border-[#1E2534] px-2">
              {/* Zero baseline */}
              <div className="absolute left-0 right-0 top-1/2 border-t border-[#2A3346] border-dashed" />

              {recentTrades.map((t, idx) => {
                const heightPct = Math.min((Math.abs(t.pnl) / maxBarPnl) * 45, 45);
                const isWin = t.pnl >= 0;
                return (
                  <div key={t.id || idx} className="flex-1 flex flex-col items-center h-full justify-center relative group">
                    {/* Tooltip on hover */}
                    <div className="absolute -top-7 opacity-0 group-hover:opacity-100 bg-[#10141C] text-slate-100 text-[10px] font-bold px-2 py-1 rounded-lg border border-[#1E2534] pointer-events-none transition-opacity whitespace-nowrap z-20">
                      {t.pair}: {isWin ? '+' : ''}
                      {settings.currency}
                      {t.pnl.toFixed(2)}
                    </div>

                    {isWin ? (
                      <div className="w-full flex flex-col items-center justify-end h-1/2">
                        <div
                          className="w-full max-w-[28px] bg-gradient-to-t from-emerald-600 to-emerald-400 rounded-t-lg transition-all"
                          style={{ height: `${Math.max(heightPct * 2, 8)}%` }}
                        />
                      </div>
                    ) : (
                      <div className="w-full h-1/2" />
                    )}

                    {!isWin ? (
                      <div className="w-full flex flex-col items-center justify-start h-1/2">
                        <div
                          className="w-full max-w-[28px] bg-gradient-to-b from-rose-600 to-rose-400 rounded-b-lg transition-all"
                          style={{ height: `${Math.max(heightPct * 2, 8)}%` }}
                        />
                      </div>
                    ) : (
                      <div className="w-full h-1/2" />
                    )}
                  </div>
                );
              })}
            </div>

            <div className="flex justify-between text-[10px] text-slate-500 pt-2 px-2">
              <span>Oldest</span>
              <span>Chronological Trades</span>
              <span>Newest</span>
            </div>
          </div>
        )}
      </div>

      {/* Grid: Win/Loss Donut & Key Ratio */}
      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        {/* Win/Loss Breakdown */}
        <div className="p-6 rounded-3xl glow-card border-[#1E2534] space-y-4">
          <div className="flex items-center gap-2">
            <PieChart className="w-5 h-5 text-indigo-400" />
            <h2 className="text-base font-bold text-slate-100">Win / Loss Breakdown</h2>
          </div>

          <div className="flex items-center justify-center gap-6 py-4">
            <div className="space-y-3 text-xs">
              <div className="flex items-center gap-2">
                <div className="w-3 h-3 rounded-full bg-emerald-500" />
                <span className="text-slate-300 font-semibold">{winsCount} Wins ({winRate}%)</span>
              </div>
              <div className="flex items-center gap-2">
                <div className="w-3 h-3 rounded-full bg-rose-500" />
                <span className="text-slate-300 font-semibold">{lossCount} Losses ({100 - winRate}%)</span>
              </div>
              <div className="flex items-center gap-2">
                <div className="w-3 h-3 rounded-full bg-amber-500" />
                <span className="text-slate-300 font-semibold">{beCount} Breakeven</span>
              </div>
            </div>
          </div>
        </div>

        {/* Avg Win vs Avg Loss */}
        <div className="p-6 rounded-3xl glow-card border-[#1E2534] space-y-4">
          <div className="flex items-center gap-2">
            <TrendingUp className="w-5 h-5 text-emerald-400" />
            <h2 className="text-base font-bold text-slate-100">Trade Size & Ratios</h2>
          </div>

          <div className="grid grid-cols-2 gap-4 py-2">
            <div className="p-4 rounded-2xl bg-[#10141C] border border-[#1E2534] space-y-1">
              <span className="text-[11px] text-slate-400 block">Average Win</span>
              <span className="text-xl font-black text-emerald-400">
                +{settings.currency}{avgWin.toFixed(2)}
              </span>
            </div>

            <div className="p-4 rounded-2xl bg-[#10141C] border border-[#1E2534] space-y-1">
              <span className="text-[11px] text-slate-400 block">Average Loss</span>
              <span className="text-xl font-black text-rose-400">
                -{settings.currency}{avgLoss.toFixed(2)}
              </span>
            </div>
          </div>
        </div>
      </div>

      {/* Psychological & Discipline Violations */}
      <div className="p-6 rounded-3xl glow-card border-rose-500/30 space-y-4">
        <div className="flex items-center gap-2">
          <ShieldAlert className="w-5 h-5 text-rose-400" />
          <h2 className="text-base font-bold text-slate-100">Psychological & Rule Violations</h2>
        </div>

        <div className="grid grid-cols-2 sm:grid-cols-4 gap-4 text-center">
          <div className="p-4 rounded-2xl bg-[#10141C] border border-[#1E2534]">
            <span className={`text-2xl font-black ${fomoCount > 0 ? 'text-rose-400' : 'text-emerald-400'}`}>
              {fomoCount}
            </span>
            <span className="text-[11px] text-slate-400 block mt-1 font-medium">FOMO Trades</span>
          </div>

          <div className="p-4 rounded-2xl bg-[#10141C] border border-[#1E2534]">
            <span className={`text-2xl font-black ${revengeCount > 0 ? 'text-rose-400' : 'text-emerald-400'}`}>
              {revengeCount}
            </span>
            <span className="text-[11px] text-slate-400 block mt-1 font-medium">Revenge Trades</span>
          </div>

          <div className="p-4 rounded-2xl bg-[#10141C] border border-[#1E2534]">
            <span className={`text-2xl font-black ${overtradeCount > 0 ? 'text-amber-400' : 'text-emerald-400'}`}>
              {overtradeCount}
            </span>
            <span className="text-[11px] text-slate-400 block mt-1 font-medium">Overtrades</span>
          </div>

          <div className="p-4 rounded-2xl bg-[#10141C] border border-[#1E2534]">
            <span className={`text-2xl font-black ${offPlanCount > 0 ? 'text-rose-400' : 'text-emerald-400'}`}>
              {offPlanCount}
            </span>
            <span className="text-[11px] text-slate-400 block mt-1 font-medium">Off-Plan Executions</span>
          </div>
        </div>
      </div>

      {/* Routine & Spiritual Habit Correlations */}
      <div className="p-6 rounded-3xl glow-card border-[#1E2534] space-y-4">
        <h2 className="text-base font-bold text-slate-100">Life Discipline Consistency</h2>
        <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
          <div className="p-4 rounded-2xl bg-[#10141C] border border-[#1E2534] flex items-center justify-between">
            <div>
              <span className="text-xs text-slate-400 block">Daily Habits Completion Rate</span>
              <span className="text-xl font-black text-emerald-400">{habitConsistency}%</span>
            </div>
            <CheckCircle2 className="w-8 h-8 text-emerald-500/20" />
          </div>

          <div className="p-4 rounded-2xl bg-[#10141C] border border-[#1E2534] flex items-center justify-between">
            <div>
              <span className="text-xs text-slate-400 block">Salah Consistency Rate</span>
              <span className="text-xl font-black text-amber-400">{salahConsistency}%</span>
            </div>
            <Award className="w-8 h-8 text-amber-500/20" />
          </div>
        </div>
      </div>
    </div>
  );
};
