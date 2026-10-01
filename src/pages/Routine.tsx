import React, { useState } from 'react';
import {
  CheckSquare,
  Plus,
  Trash2,
  Sparkles,
  TrendingUp,
  Award,
  CheckCircle2,
  Calendar,
  X,
} from 'lucide-react';
import { useTraderLife } from '../context/TraderLifeContext';

export const Routine: React.FC = () => {
  const { habits, habitLogs, toggleHabit, addCustomHabit, deleteHabit, selectedDate, language } = useTraderLife();

  const [showAddModal, setShowAddModal] = useState(false);
  const [newTitleEn, setNewTitleEn] = useState('');
  const [newTitleBn, setNewTitleBn] = useState('');
  const [newCategory, setNewCategory] = useState('Trading');

  const completedIds = habitLogs[selectedDate] || [];
  const completedCount = completedIds.length;
  const totalCount = habits.length;
  const completionPct = totalCount > 0 ? Math.round((completedCount / totalCount) * 100) : 0;

  const handleAddHabit = (e: React.FormEvent) => {
    e.preventDefault();
    if (newTitleEn.trim()) {
      addCustomHabit(newTitleEn.trim(), newTitleBn.trim(), newCategory, 'CheckSquare');
      setNewTitleEn('');
      setNewTitleBn('');
      setShowAddModal(false);
    }
  };

  return (
    <div className="space-y-6 pb-20 lg:pb-8">
      {/* Title & Action */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-xl sm:text-2xl font-black text-slate-100">Daily Routine & Habit Tracker</h1>
          <p className="text-xs text-slate-400">Build unbreakable trading discipline and lifestyle consistency</p>
        </div>
        <button
          onClick={() => setShowAddModal(true)}
          className="flex items-center justify-center gap-2 px-5 py-3 rounded-2xl bg-amber-500 hover:bg-amber-400 text-black font-extrabold text-xs sm:text-sm shadow-lg shadow-amber-500/20 active:scale-95 transition-all"
        >
          <Plus className="w-4 h-4" /> Add Habit
        </button>
      </div>

      {/* Daily Progress Banner */}
      <div className="p-6 rounded-3xl glow-card border-emerald-500/30 space-y-4">
        <div className="flex items-center justify-between">
          <div>
            <span className="text-xs font-bold text-emerald-400 uppercase tracking-wider block">
              {language === 'bn' ? 'আজকের রুটিন অগ্রগতি' : "Today's Routine Progress"}
            </span>
            <h2 className="text-2xl font-black text-slate-100 mt-0.5">
              {completedCount} of {totalCount} Habits Done
            </h2>
          </div>
          <span
            className={`text-3xl font-black ${
              completionPct >= 80 ? 'text-emerald-400' : 'text-amber-400'
            }`}
          >
            {completionPct}%
          </span>
        </div>

        <div className="w-full h-3 rounded-full bg-[#161B26] overflow-hidden p-0.5 border border-[#1E2534]">
          <div
            className="h-full rounded-full bg-gradient-to-r from-emerald-500 to-teal-400 transition-all duration-500"
            style={{ width: `${completionPct}%` }}
          />
        </div>
      </div>

      {/* Habit Checklist */}
      <div className="space-y-3">
        {habits.map((habit) => {
          const isDone = completedIds.includes(habit.id);
          return (
            <div
              key={habit.id}
              onClick={() => toggleHabit(habit.id, selectedDate)}
              className={`p-4 sm:p-5 rounded-3xl border transition-all cursor-pointer flex items-center justify-between gap-4 ${
                isDone
                  ? 'bg-[#10141C] border-emerald-500/40 text-slate-100'
                  : 'bg-[#0B0E14] border-[#1E2534] text-slate-300 hover:border-[#2A3346]'
              }`}
            >
              <div className="flex items-center gap-3.5">
                <div
                  className={`w-7 h-7 rounded-xl flex items-center justify-center transition-all ${
                    isDone
                      ? 'bg-emerald-500 text-black shadow-md shadow-emerald-500/20'
                      : 'border-2 border-slate-600'
                  }`}
                >
                  {isDone && <CheckCircle2 className="w-5 h-5 text-black stroke-[3]" />}
                </div>

                <div>
                  <h3
                    className={`text-sm font-semibold transition-all ${
                      isDone ? 'line-through text-slate-400' : 'text-slate-100'
                    }`}
                  >
                    {habit.titleEn}
                  </h3>
                  {habit.titleBn && language !== 'en' && (
                    <p className="text-[11px] text-slate-500">{habit.titleBn}</p>
                  )}
                </div>
              </div>

              <div className="flex items-center gap-3">
                <span className="px-2.5 py-1 rounded-lg text-[10px] font-bold bg-[#161B26] text-slate-400 border border-[#1E2534]">
                  {habit.category}
                </span>
                <button
                  onClick={(e) => {
                    e.stopPropagation();
                    deleteHabit(habit.id);
                  }}
                  className="p-1.5 rounded-lg text-slate-600 hover:text-rose-400 hover:bg-rose-500/10 transition-colors"
                >
                  <Trash2 className="w-4 h-4" />
                </button>
              </div>
            </div>
          );
        })}
      </div>

      {/* Add Custom Habit Modal */}
      {showAddModal && (
        <div className="fixed inset-0 z-50 bg-black/80 backdrop-blur-sm flex items-center justify-center p-4">
          <div className="w-full max-w-md bg-[#0B0E14] border border-[#1E2534] rounded-3xl p-6 shadow-2xl space-y-4">
            <div className="flex items-center justify-between border-b border-[#1E2534] pb-3">
              <h2 className="text-base font-bold text-slate-100">Add Custom Habit</h2>
              <button onClick={() => setShowAddModal(false)} className="text-slate-400 hover:text-white">
                <X className="w-5 h-5" />
              </button>
            </div>

            <form onSubmit={handleAddHabit} className="space-y-4 text-xs">
              <div>
                <label className="text-slate-400 block mb-1 font-medium">Habit Title (English)</label>
                <input
                  type="text"
                  required
                  placeholder="e.g. Backtest 20 trades, Evening walk..."
                  value={newTitleEn}
                  onChange={(e) => setNewTitleEn(e.target.value)}
                  className="w-full p-3 rounded-xl bg-[#10141C] border border-[#1E2534] text-slate-100 focus:border-amber-500 focus:outline-none"
                />
              </div>

              <div>
                <label className="text-slate-400 block mb-1 font-medium">Habit Title (Bengali / বাংলা)</label>
                <input
                  type="text"
                  placeholder="e.g. ২০টি ট্রেড ব্যাকটেস্ট, সান্ধ্যকালীন হাঁটা..."
                  value={newTitleBn}
                  onChange={(e) => setNewTitleBn(e.target.value)}
                  className="w-full p-3 rounded-xl bg-[#10141C] border border-[#1E2534] text-slate-100 focus:border-amber-500 focus:outline-none"
                />
              </div>

              <div>
                <label className="text-slate-400 block mb-1 font-medium">Category</label>
                <select
                  value={newCategory}
                  onChange={(e) => setNewCategory(e.target.value)}
                  className="w-full p-3 rounded-xl bg-[#10141C] border border-[#1E2534] text-slate-100 focus:border-amber-500 focus:outline-none"
                >
                  <option value="Trading">Trading</option>
                  <option value="Routine">Routine</option>
                  <option value="Health">Health</option>
                  <option value="Mindset">Mindset</option>
                  <option value="Life">Life</option>
                </select>
              </div>

              <div className="flex justify-end gap-3 pt-2">
                <button
                  type="button"
                  onClick={() => setShowAddModal(false)}
                  className="px-4 py-2.5 rounded-xl bg-[#161B26] hover:bg-[#1E2534] text-slate-300 font-semibold"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="px-5 py-2.5 rounded-xl bg-amber-500 hover:bg-amber-400 text-black font-extrabold"
                >
                  Add Habit
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};
