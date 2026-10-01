import React, { useState, useEffect } from 'react';
import { FileCheck2, Award, Calendar, CheckCircle2, History, Star, Save } from 'lucide-react';
import { useTraderLife } from '../context/TraderLifeContext';
import { DailyReview as IDailyReview } from '../types';

export const DailyReview: React.FC = () => {
  const { dailyReviews, saveDailyReview, selectedDate, settings, language } = useTraderLife();

  const currentReview = dailyReviews[selectedDate] || {
    date: selectedDate,
    didWell: '',
    biggestMistake: '',
    mistakeReason: '',
    improvement: '',
    lesson: '',
    disciplineScore: 9,
    updatedAt: Date.now(),
  };

  const [didWell, setDidWell] = useState(currentReview.didWell);
  const [biggestMistake, setBiggestMistake] = useState(currentReview.biggestMistake);
  const [mistakeReason, setMistakeReason] = useState(currentReview.mistakeReason);
  const [improvement, setImprovement] = useState(currentReview.improvement);
  const [lesson, setLesson] = useState(currentReview.lesson);
  const [disciplineScore, setDisciplineScore] = useState(currentReview.disciplineScore);

  useEffect(() => {
    const rev = dailyReviews[selectedDate];
    if (rev) {
      setDidWell(rev.didWell);
      setBiggestMistake(rev.biggestMistake);
      setMistakeReason(rev.mistakeReason);
      setImprovement(rev.improvement);
      setLesson(rev.lesson);
      setDisciplineScore(rev.disciplineScore);
    } else {
      setDidWell('');
      setBiggestMistake('');
      setMistakeReason('');
      setImprovement('');
      setLesson('');
      setDisciplineScore(9);
    }
  }, [selectedDate, dailyReviews]);

  const handleSave = (e: React.FormEvent) => {
    e.preventDefault();
    saveDailyReview({
      date: selectedDate,
      didWell: didWell.trim(),
      biggestMistake: biggestMistake.trim(),
      mistakeReason: mistakeReason.trim(),
      improvement: improvement.trim(),
      lesson: lesson.trim(),
      disciplineScore,
      updatedAt: Date.now(),
    });
  };

  const pastReviews = Object.values(dailyReviews).filter((r) => r.date !== selectedDate);

  return (
    <div className="space-y-6 pb-20 lg:pb-8">
      {/* Title */}
      <div>
        <h1 className="text-xl sm:text-2xl font-black text-slate-100">End-of-Day Review & Reflection</h1>
        <p className="text-xs text-slate-400">Structured daily debrief to convert errors into compound wisdom</p>
      </div>

      {/* Review Form Card */}
      <div className="p-6 sm:p-8 rounded-3xl glow-card border-amber-500/40 space-y-6">
        <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 border-b border-[#1E2534] pb-4">
          <div className="flex items-center gap-3">
            <div className="w-10 h-10 rounded-2xl bg-amber-500/15 border border-amber-500/30 flex items-center justify-center text-amber-400">
              <FileCheck2 className="w-5 h-5" />
            </div>
            <div>
              <h2 className="text-lg font-bold text-slate-100">Daily Journal & Debrief</h2>
              <p className="text-xs text-amber-400 font-semibold">{selectedDate}</p>
            </div>
          </div>

          {/* Discipline rating slider badge */}
          <div className="flex items-center gap-3 bg-[#10141C] px-4 py-2 rounded-2xl border border-[#1E2534]">
            <span className="text-xs text-slate-400 font-medium">Discipline Rating:</span>
            <span className="text-lg font-black text-amber-400">{disciplineScore} / 10</span>
          </div>
        </div>

        <form onSubmit={handleSave} className="space-y-5 text-xs">
          {/* Discipline Score Slider */}
          <div className="space-y-2 p-4 rounded-2xl bg-[#10141C] border border-[#1E2534]">
            <div className="flex justify-between font-semibold text-slate-300">
              <span>Overall Day Discipline Rating (1-10)</span>
              <span className="text-amber-400 font-bold">{disciplineScore} / 10</span>
            </div>
            <input
              type="range"
              min="1"
              max="10"
              value={disciplineScore}
              onChange={(e) => setDisciplineScore(parseInt(e.target.value))}
              className="w-full accent-amber-500 cursor-pointer"
            />
          </div>

          {/* 5 Guided Questions */}
          <div className="space-y-4">
            <div>
              <label className="text-slate-300 font-bold block mb-1.5 flex items-center gap-2">
                <span className="w-5 h-5 rounded-full bg-emerald-500/20 text-emerald-400 flex items-center justify-center text-[10px]">1</span>
                What did I do well today? (আজ কি ভালো করেছি?)
              </label>
              <textarea
                rows={2}
                placeholder="e.g. Stuck strictly to 1% risk rule, waited for 15m order block confluence, maintained calm mind..."
                value={didWell}
                onChange={(e) => setDidWell(e.target.value)}
                className="w-full p-3 rounded-2xl bg-[#10141C] border border-[#1E2534] text-slate-100 placeholder:text-slate-600 focus:border-amber-500 focus:outline-none"
              />
            </div>

            <div>
              <label className="text-slate-300 font-bold block mb-1.5 flex items-center gap-2">
                <span className="w-5 h-5 rounded-full bg-rose-500/20 text-rose-400 flex items-center justify-center text-[10px]">2</span>
                Biggest mistake today (আজকের সবচেয়ে বড় ভুল)
              </label>
              <textarea
                rows={2}
                placeholder="e.g. Sized up on second trade after London open, entered without waiting for candle close..."
                value={biggestMistake}
                onChange={(e) => setBiggestMistake(e.target.value)}
                className="w-full p-3 rounded-2xl bg-[#10141C] border border-[#1E2534] text-slate-100 placeholder:text-slate-600 focus:border-amber-500 focus:outline-none"
              />
            </div>

            <div>
              <label className="text-slate-300 font-bold block mb-1.5 flex items-center gap-2">
                <span className="w-5 h-5 rounded-full bg-amber-500/20 text-amber-400 flex items-center justify-center text-[10px]">3</span>
                Why did I make that mistake? (কেন ভুলটি হলো?)
              </label>
              <textarea
                rows={2}
                placeholder="e.g. Felt impatient after waiting 2 hours, gave in to quick dopamine urge..."
                value={mistakeReason}
                onChange={(e) => setMistakeReason(e.target.value)}
                className="w-full p-3 rounded-2xl bg-[#10141C] border border-[#1E2534] text-slate-100 placeholder:text-slate-600 focus:border-amber-500 focus:outline-none"
              />
            </div>

            <div>
              <label className="text-slate-300 font-bold block mb-1.5 flex items-center gap-2">
                <span className="w-5 h-5 rounded-full bg-indigo-500/20 text-indigo-400 flex items-center justify-center text-[10px]">4</span>
                What will I improve tomorrow? (আগামীকাল কি উন্নত করবো?)
              </label>
              <textarea
                rows={2}
                placeholder="e.g. Set price alerts and walk away from chart until alert sounds, take max 2 setups..."
                value={improvement}
                onChange={(e) => setImprovement(e.target.value)}
                className="w-full p-3 rounded-2xl bg-[#10141C] border border-[#1E2534] text-slate-100 placeholder:text-slate-600 focus:border-amber-500 focus:outline-none"
              />
            </div>

            <div>
              <label className="text-slate-300 font-bold block mb-1.5 flex items-center gap-2">
                <span className="w-5 h-5 rounded-full bg-teal-500/20 text-teal-400 flex items-center justify-center text-[10px]">5</span>
                Today's Key Lesson (আজকের শিক্ষা)
              </label>
              <textarea
                rows={2}
                placeholder="e.g. Trading is 80% waiting and 20% execution. Preserving capital comes before making profit..."
                value={lesson}
                onChange={(e) => setLesson(e.target.value)}
                className="w-full p-3 rounded-2xl bg-[#10141C] border border-[#1E2534] text-slate-100 placeholder:text-slate-600 focus:border-amber-500 focus:outline-none font-semibold text-amber-200"
              />
            </div>
          </div>

          <button
            type="submit"
            className="w-full py-3.5 rounded-2xl bg-amber-500 hover:bg-amber-400 text-black font-extrabold shadow-lg shadow-amber-500/20 active:scale-95 transition-all flex items-center justify-center gap-2 text-sm"
          >
            <Save className="w-4 h-4" /> Save Daily Review
          </button>
        </form>
      </div>

      {/* Past Reviews Archive */}
      <div className="space-y-4">
        <div className="flex items-center gap-2">
          <History className="w-5 h-5 text-slate-400" />
          <h2 className="text-base font-bold text-slate-100">Past Daily Reviews Timeline</h2>
        </div>

        {pastReviews.length === 0 ? (
          <p className="text-xs text-slate-500">No past reviews archived yet.</p>
        ) : (
          <div className="space-y-4">
            {pastReviews.map((rev) => (
              <div
                key={rev.date}
                className="p-5 rounded-3xl glow-card border-[#1E2534] space-y-3"
              >
                <div className="flex items-center justify-between">
                  <span className="text-sm font-bold text-amber-400">{rev.date}</span>
                  <span className="px-3 py-1 rounded-xl bg-[#10141C] border border-[#1E2534] text-xs font-bold text-slate-300">
                    Discipline: {rev.disciplineScore}/10
                  </span>
                </div>

                {rev.didWell && (
                  <p className="text-xs text-slate-300">
                    <strong className="text-emerald-400">Did Well:</strong> {rev.didWell}
                  </p>
                )}
                {rev.biggestMistake && (
                  <p className="text-xs text-slate-300">
                    <strong className="text-rose-400">Mistake:</strong> {rev.biggestMistake}
                  </p>
                )}
                {rev.lesson && (
                  <p className="text-xs text-amber-200 italic bg-[#10141C] p-3 rounded-2xl border border-[#1E2534]">
                    <strong>Lesson:</strong> “{rev.lesson}”
                  </p>
                )}
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
};
