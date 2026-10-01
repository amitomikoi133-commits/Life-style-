import React, { useState } from 'react';
import { Target, Flag, Calendar, Award, Edit2, TrendingUp, Sparkles, X } from 'lucide-react';
import { useTraderLife } from '../context/TraderLifeContext';
import { MonthlyGoal } from '../types';

export const Goals: React.FC = () => {
  const { goals, updateGoal, trades, settings, language } = useTraderLife();

  const currentMonthNum = new Date().getMonth() + 1;
  const currentGoal = goals.find((g) => g.month === currentMonthNum) || {
    month: currentMonthNum,
    targetPerDay: settings.currentMonthTarget,
    title: `Month ${currentMonthNum} Target`,
    notes: 'Discipline and consistency focus',
  };

  // Days in month calculation
  const now = new Date();
  const daysInMonth = new Date(now.getFullYear(), now.getMonth() + 1, 0).getDate();
  const currentDay = now.getDate();
  const remainingDays = Math.max(daysInMonth - currentDay + 1, 1);

  const totalMonthTarget = currentGoal.targetPerDay * daysInMonth;
  const totalMonthPnl = trades.reduce((acc, t) => acc + t.pnl, 0);
  const actualDailyAvg = daysInMonth > 0 ? totalMonthPnl / daysInMonth : 0;
  const progressPct = totalMonthTarget > 0 ? Math.min(Math.max((totalMonthPnl / totalMonthTarget) * 100, 0), 200) : 0;

  const [editingGoal, setEditingGoal] = useState<MonthlyGoal | null>(null);
  const [editTarget, setEditTarget] = useState('');
  const [editNotes, setEditNotes] = useState('');

  const openEdit = (g: MonthlyGoal) => {
    setEditingGoal(g);
    setEditTarget(g.targetPerDay.toString());
    setEditNotes(g.notes);
  };

  const handleSave = (e: React.FormEvent) => {
    e.preventDefault();
    if (editingGoal) {
      updateGoal(editingGoal.month, parseFloat(editTarget) || editingGoal.targetPerDay, editNotes);
      setEditingGoal(null);
    }
  };

  return (
    <div className="space-y-6 pb-20 lg:pb-8">
      {/* Title */}
      <div>
        <h1 className="text-xl sm:text-2xl font-black text-slate-100">Monthly Progression Roadmap</h1>
        <p className="text-xs text-slate-400">Step-by-step capital growth & demo daily target milestones</p>
      </div>

      {/* Current Month Active Card */}
      <div className="p-6 sm:p-8 rounded-3xl bg-gradient-to-br from-[#10141C] via-[#161B26] to-[#10141C] border border-amber-500/40 glow-card space-y-6">
        <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
          <div>
            <div className="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-amber-500/10 border border-amber-500/30 text-amber-400 text-xs font-bold uppercase tracking-wider mb-2">
              <Flag className="w-3.5 h-3.5" /> Month {currentMonthNum} Active Goal
            </div>
            <h2 className="text-2xl sm:text-3xl font-black text-slate-100">
              Target: {settings.currency}{currentGoal.targetPerDay.toFixed(2)} / Day
            </h2>
            <p className="text-xs text-slate-400 mt-1">
              Monthly Total Target: {settings.currency}{totalMonthTarget.toFixed(2)} ({daysInMonth} trading days)
            </p>
          </div>

          <div className="flex sm:flex-col items-center sm:items-end gap-2 text-right">
            <span className="px-3 py-1 rounded-xl bg-[#10141C] border border-[#1E2534] text-xs font-semibold text-slate-300">
              {remainingDays} Days Left
            </span>
            <button
              onClick={() => openEdit(currentGoal)}
              className="flex items-center gap-1.5 text-xs text-amber-400 hover:underline font-semibold"
            >
              <Edit2 className="w-3.5 h-3.5" /> Edit Goal
            </button>
          </div>
        </div>

        {/* Progress Bar */}
        <div className="space-y-2">
          <div className="flex justify-between text-xs font-bold">
            <span className="text-slate-400">Monthly Roadmap Progress</span>
            <span className="text-amber-400">{Math.round(progressPct)}%</span>
          </div>
          <div className="w-full h-3 rounded-full bg-[#10141C] overflow-hidden p-0.5 border border-[#1E2534]">
            <div
              className={`h-full rounded-full transition-all duration-500 ${
                totalMonthPnl >= 0 ? 'bg-gradient-to-r from-emerald-500 to-teal-400' : 'bg-rose-500'
              }`}
              style={{ width: `${Math.min(progressPct, 100)}%` }}
            />
          </div>
        </div>

        {/* Key Milestone Stats */}
        <div className="grid grid-cols-2 sm:grid-cols-4 gap-3 pt-4 border-t border-[#1E2534] text-xs">
          <div className="p-3 rounded-2xl bg-[#0B0E14] border border-[#1E2534]">
            <span className="text-slate-500 text-[11px] block">Actual Total P/L</span>
            <span
              className={`text-base font-black ${
                totalMonthPnl >= 0 ? 'text-emerald-400' : 'text-rose-400'
              }`}
            >
              {totalMonthPnl >= 0 ? '+' : ''}
              {settings.currency}
              {totalMonthPnl.toFixed(2)}
            </span>
          </div>

          <div className="p-3 rounded-2xl bg-[#0B0E14] border border-[#1E2534]">
            <span className="text-slate-500 text-[11px] block">Actual Daily Avg</span>
            <span
              className={`text-base font-black ${
                actualDailyAvg >= 0 ? 'text-emerald-400' : 'text-rose-400'
              }`}
            >
              {actualDailyAvg >= 0 ? '+' : ''}
              {settings.currency}
              {actualDailyAvg.toFixed(2)}/day
            </span>
          </div>

          <div className="p-3 rounded-2xl bg-[#0B0E14] border border-[#1E2534]">
            <span className="text-slate-500 text-[11px] block">Target Per Day</span>
            <span className="text-base font-black text-amber-400">
              {settings.currency}
              {currentGoal.targetPerDay.toFixed(2)}/day
            </span>
          </div>

          <div className="p-3 rounded-2xl bg-[#0B0E14] border border-[#1E2534]">
            <span className="text-slate-500 text-[11px] block">Discipline Rule</span>
            <span className="text-xs font-bold text-slate-300">1% Max Risk / Trade</span>
          </div>
        </div>
      </div>

      {/* 12-Month Target Progression Roadmap List */}
      <div className="space-y-4">
        <h2 className="text-base font-bold text-slate-100">12-Month Progression Plan (Month 1 → Month 12)</h2>
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
          {goals.map((g) => {
            const isCurrent = g.month === currentMonthNum;
            return (
              <div
                key={g.month}
                className={`p-5 rounded-3xl border transition-all ${
                  isCurrent
                    ? 'bg-[#10141C] border-amber-500/50 shadow-lg shadow-amber-500/5'
                    : 'bg-[#0B0E14] border-[#1E2534] hover:border-[#2A3346]'
                }`}
              >
                <div className="flex items-center justify-between mb-3">
                  <span
                    className={`px-3 py-1 rounded-xl text-xs font-black uppercase ${
                      isCurrent
                        ? 'bg-amber-500 text-black'
                        : 'bg-[#161B26] text-slate-400 border border-[#1E2534]'
                    }`}
                  >
                    Month {g.month}
                  </span>
                  <button
                    onClick={() => openEdit(g)}
                    className="p-1 text-slate-500 hover:text-amber-400"
                  >
                    <Edit2 className="w-3.5 h-3.5" />
                  </button>
                </div>

                <div className="space-y-1">
                  <span className="text-2xl font-black text-slate-100">
                    {settings.currency}{g.targetPerDay.toFixed(2)} <span className="text-xs text-slate-400 font-normal">/ day</span>
                  </span>
                  <p className="text-xs text-slate-400 leading-relaxed">{g.notes}</p>
                </div>
              </div>
            );
          })}
        </div>
      </div>

      {/* Edit Goal Modal */}
      {editingGoal && (
        <div className="fixed inset-0 z-50 bg-black/80 backdrop-blur-sm flex items-center justify-center p-4">
          <div className="w-full max-w-md bg-[#0B0E14] border border-[#1E2534] rounded-3xl p-6 shadow-2xl space-y-4">
            <div className="flex items-center justify-between border-b border-[#1E2534] pb-3">
              <h2 className="text-base font-bold text-slate-100">
                Edit Month {editingGoal.month} Target
              </h2>
              <button onClick={() => setEditingGoal(null)} className="text-slate-400 hover:text-white">
                <X className="w-5 h-5" />
              </button>
            </div>

            <form onSubmit={handleSave} className="space-y-4 text-xs">
              <div>
                <label className="text-slate-400 block mb-1 font-medium">Daily Target ({settings.currency}/day)</label>
                <input
                  type="number"
                  step="any"
                  required
                  value={editTarget}
                  onChange={(e) => setEditTarget(e.target.value)}
                  className="w-full p-3 rounded-xl bg-[#10141C] border border-[#1E2534] text-slate-100 font-bold focus:border-amber-500 focus:outline-none"
                />
              </div>

              <div>
                <label className="text-slate-400 block mb-1 font-medium">Focus / Rule Notes</label>
                <textarea
                  rows={3}
                  value={editNotes}
                  onChange={(e) => setEditNotes(e.target.value)}
                  className="w-full p-3 rounded-xl bg-[#10141C] border border-[#1E2534] text-slate-100 focus:border-amber-500 focus:outline-none"
                />
              </div>

              <div className="flex justify-end gap-3 pt-2">
                <button
                  type="button"
                  onClick={() => setEditingGoal(null)}
                  className="px-4 py-2.5 rounded-xl bg-[#161B26] hover:bg-[#1E2534] text-slate-300 font-semibold"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="px-5 py-2.5 rounded-xl bg-amber-500 hover:bg-amber-400 text-black font-extrabold"
                >
                  Save Target
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};
