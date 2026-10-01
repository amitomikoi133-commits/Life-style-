import React, { useState } from 'react';
import {
  BrainCircuit,
  Heart,
  Smile,
  Frown,
  Zap,
  Flame,
  CheckCircle2,
  Clock,
  Sparkles,
} from 'lucide-react';
import { useTraderLife } from '../context/TraderLifeContext';

export const Psychology: React.FC = () => {
  const { psychologyLogs, addPsychologyLog } = useTraderLife();

  const [calm, setCalm] = useState(4);
  const [fear, setFear] = useState(1);
  const [greed, setGreed] = useState(1);
  const [fomo, setFomo] = useState(1);
  const [revenge, setRevenge] = useState(1);
  const [confidence, setConfidence] = useState(4);
  const [stress, setStress] = useState(2);
  const [note, setNote] = useState('');
  const [sessionType, setSessionType] = useState<
    'Pre-Market' | 'Active-Session' | 'Post-Market' | 'Trade-Reflection'
  >('Pre-Market');

  // Pre-Trading Mental Readiness Checklist
  const [checkSleep, setCheckSleep] = useState(true);
  const [checkPatience, setCheckPatience] = useState(true);
  const [checkRiskAccepted, setCheckRiskAccepted] = useState(true);
  const [checkNoDistraction, setCheckNoDistraction] = useState(true);

  const handleSave = (e: React.FormEvent) => {
    e.preventDefault();
    const now = new Date();
    addPsychologyLog({
      date: now.toISOString().split('T')[0],
      time: now.toTimeString().substring(0, 5),
      calm,
      fear,
      greed,
      fomo,
      revenge,
      confidence,
      stress,
      note: note.trim(),
      sessionType,
    });
    setNote('');
  };

  const isReadyToTrade = checkSleep && checkPatience && checkRiskAccepted && checkNoDistraction;

  return (
    <div className="space-y-6 pb-20 lg:pb-8">
      {/* Title */}
      <div>
        <h1 className="text-xl sm:text-2xl font-black text-slate-100">Trading Psychology & Mindset</h1>
        <p className="text-xs text-slate-400">Track emotions, conquer FOMO and maintain emotional detachment</p>
      </div>

      {/* Pre-Market Readiness Checklist */}
      <div className="p-6 rounded-3xl glow-card border-[#1E2534] space-y-4">
        <div className="flex items-center justify-between">
          <div className="flex items-center gap-2.5">
            <Sparkles className="w-5 h-5 text-amber-400" />
            <h2 className="text-base font-bold text-slate-100">Pre-Session Mental Readiness Test</h2>
          </div>
          <span
            className={`px-3 py-1 rounded-xl text-xs font-bold ${
              isReadyToTrade
                ? 'bg-emerald-500/15 border border-emerald-500/30 text-emerald-400'
                : 'bg-amber-500/15 border border-amber-500/30 text-amber-400'
            }`}
          >
            {isReadyToTrade ? 'MIND READY TO EXECUTE 🧠' : 'PAUSE: CHECK MINDSET'}
          </span>
        </div>

        <div className="grid grid-cols-1 sm:grid-cols-2 gap-3 text-xs text-slate-300">
          <label className="flex items-center gap-3 p-3 rounded-2xl bg-[#10141C] border border-[#1E2534] cursor-pointer hover:border-[#2A3346] transition-all">
            <input
              type="checkbox"
              checked={checkSleep}
              onChange={(e) => setCheckSleep(e.target.checked)}
              className="rounded accent-emerald-500 w-4 h-4 shrink-0"
            />
            <span>Slept well, energized, and prayed on time</span>
          </label>

          <label className="flex items-center gap-3 p-3 rounded-2xl bg-[#10141C] border border-[#1E2534] cursor-pointer hover:border-[#2A3346] transition-all">
            <input
              type="checkbox"
              checked={checkPatience}
              onChange={(e) => setCheckPatience(e.target.checked)}
              className="rounded accent-emerald-500 w-4 h-4 shrink-0"
            />
            <span>Detached from outcome, patient to wait for A+ setups</span>
          </label>

          <label className="flex items-center gap-3 p-3 rounded-2xl bg-[#10141C] border border-[#1E2534] cursor-pointer hover:border-[#2A3346] transition-all">
            <input
              type="checkbox"
              checked={checkRiskAccepted}
              onChange={(e) => setCheckRiskAccepted(e.target.checked)}
              className="rounded accent-emerald-500 w-4 h-4 shrink-0"
            />
            <span>100% accepted the dollar risk before entering</span>
          </label>

          <label className="flex items-center gap-3 p-3 rounded-2xl bg-[#10141C] border border-[#1E2534] cursor-pointer hover:border-[#2A3346] transition-all">
            <input
              type="checkbox"
              checked={checkNoDistraction}
              onChange={(e) => setCheckNoDistraction(e.target.checked)}
              className="rounded accent-emerald-500 w-4 h-4 shrink-0"
            />
            <span>Quiet environment, zero financial rush / anxiety</span>
          </label>
        </div>
      </div>

      {/* Real-time Emotion Logging Form */}
      <div className="p-6 rounded-3xl glow-card border-[#1E2534] space-y-6">
        <div className="flex items-center gap-2.5">
          <BrainCircuit className="w-5 h-5 text-indigo-400" />
          <h2 className="text-base font-bold text-slate-100">Log Emotional State</h2>
        </div>

        <form onSubmit={handleSave} className="space-y-5 text-xs">
          {/* Session Phase */}
          <div className="space-y-2">
            <label className="text-slate-400 font-semibold block">Session Phase</label>
            <div className="flex flex-wrap gap-2">
              {(
                ['Pre-Market', 'Active-Session', 'Post-Market', 'Trade-Reflection'] as const
              ).map((type) => (
                <button
                  type="button"
                  key={type}
                  onClick={() => setSessionType(type)}
                  className={`px-3.5 py-1.5 rounded-xl font-semibold transition-all ${
                    sessionType === type
                      ? 'bg-amber-500 text-black font-bold'
                      : 'bg-[#10141C] text-slate-400 hover:text-slate-200 border border-[#1E2534]'
                  }`}
                >
                  {type}
                </button>
              ))}
            </div>
          </div>

          {/* Sliders Grid */}
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            {/* Calm */}
            <div className="p-3.5 rounded-2xl bg-[#10141C] border border-[#1E2534] space-y-2">
              <div className="flex justify-between font-semibold">
                <span className="text-emerald-400">Calmness & Peace (শান্তভাব)</span>
                <span className="text-emerald-400 font-bold">{calm} / 5</span>
              </div>
              <input
                type="range"
                min="1"
                max="5"
                value={calm}
                onChange={(e) => setCalm(parseInt(e.target.value))}
                className="w-full accent-emerald-500 cursor-pointer"
              />
            </div>

            {/* Confidence */}
            <div className="p-3.5 rounded-2xl bg-[#10141C] border border-[#1E2534] space-y-2">
              <div className="flex justify-between font-semibold">
                <span className="text-indigo-400">Confidence & Clarity (আত্মবিশ্বাস)</span>
                <span className="text-indigo-400 font-bold">{confidence} / 5</span>
              </div>
              <input
                type="range"
                min="1"
                max="5"
                value={confidence}
                onChange={(e) => setConfidence(parseInt(e.target.value))}
                className="w-full accent-indigo-500 cursor-pointer"
              />
            </div>

            {/* Stress */}
            <div className="p-3.5 rounded-2xl bg-[#10141C] border border-[#1E2534] space-y-2">
              <div className="flex justify-between font-semibold">
                <span className="text-amber-400">Stress & Tension (চাপ)</span>
                <span className="text-amber-400 font-bold">{stress} / 5</span>
              </div>
              <input
                type="range"
                min="1"
                max="5"
                value={stress}
                onChange={(e) => setStress(parseInt(e.target.value))}
                className="w-full accent-amber-500 cursor-pointer"
              />
            </div>

            {/* Fear */}
            <div className="p-3.5 rounded-2xl bg-[#10141C] border border-[#1E2534] space-y-2">
              <div className="flex justify-between font-semibold">
                <span className="text-rose-400">Fear & Hesitation (ভয়)</span>
                <span className="text-rose-400 font-bold">{fear} / 5</span>
              </div>
              <input
                type="range"
                min="1"
                max="5"
                value={fear}
                onChange={(e) => setFear(parseInt(e.target.value))}
                className="w-full accent-rose-500 cursor-pointer"
              />
            </div>

            {/* Greed */}
            <div className="p-3.5 rounded-2xl bg-[#10141C] border border-[#1E2534] space-y-2">
              <div className="flex justify-between font-semibold">
                <span className="text-rose-400">Greed & Urge to Oversize (লোভ)</span>
                <span className="text-rose-400 font-bold">{greed} / 5</span>
              </div>
              <input
                type="range"
                min="1"
                max="5"
                value={greed}
                onChange={(e) => setGreed(parseInt(e.target.value))}
                className="w-full accent-rose-500 cursor-pointer"
              />
            </div>

            {/* FOMO */}
            <div className="p-3.5 rounded-2xl bg-[#10141C] border border-[#1E2534] space-y-2">
              <div className="flex justify-between font-semibold">
                <span className="text-rose-400">FOMO (সুযোগ হারানোর ভয়)</span>
                <span className="text-rose-400 font-bold">{fomo} / 5</span>
              </div>
              <input
                type="range"
                min="1"
                max="5"
                value={fomo}
                onChange={(e) => setFomo(parseInt(e.target.value))}
                className="w-full accent-rose-500 cursor-pointer"
              />
            </div>

            {/* Revenge */}
            <div className="p-3.5 rounded-2xl bg-[#10141C] border border-[#1E2534] space-y-2 md:col-span-2">
              <div className="flex justify-between font-semibold">
                <span className="text-rose-400">Revenge Urge (ক্ষতিপূরণের জেদ)</span>
                <span className="text-rose-400 font-bold">{revenge} / 5</span>
              </div>
              <input
                type="range"
                min="1"
                max="5"
                value={revenge}
                onChange={(e) => setRevenge(parseInt(e.target.value))}
                className="w-full accent-rose-500 cursor-pointer"
              />
            </div>
          </div>

          {/* Mindset Thought Input */}
          <div className="space-y-1.5">
            <label className="text-slate-400 font-semibold block">
              What was going through your mind during the trade or session?
            </label>
            <textarea
              rows={3}
              required
              placeholder="Write your candid stream of thought, doubts, temptations or calm observations..."
              value={note}
              onChange={(e) => setNote(e.target.value)}
              className="w-full p-3 rounded-2xl bg-[#10141C] border border-[#1E2534] text-slate-100 placeholder:text-slate-600 focus:border-amber-500 focus:outline-none leading-relaxed"
            />
          </div>

          <button
            type="submit"
            className="w-full py-3.5 rounded-2xl bg-amber-500 hover:bg-amber-400 text-black font-extrabold shadow-lg shadow-amber-500/20 active:scale-95 transition-all"
          >
            Save Psychological Check-in
          </button>
        </form>
      </div>

      {/* Mindset History Log */}
      <div className="space-y-4">
        <h2 className="text-base font-bold text-slate-100">Past Mindset & Emotion Logs</h2>

        {psychologyLogs.length === 0 ? (
          <p className="text-xs text-slate-500">No psychological check-ins logged yet.</p>
        ) : (
          <div className="space-y-3">
            {psychologyLogs.map((log) => (
              <div
                key={log.id}
                className="p-5 rounded-3xl glow-card border-[#1E2534] space-y-3"
              >
                <div className="flex items-center justify-between">
                  <div className="flex items-center gap-2">
                    <span className="px-2.5 py-0.5 rounded-lg text-[10px] font-bold bg-amber-500/15 text-amber-400 border border-amber-500/30">
                      {log.sessionType}
                    </span>
                    <span className="text-xs text-slate-400 flex items-center gap-1">
                      <Clock className="w-3 h-3 text-slate-500" /> {log.date} {log.time}
                    </span>
                  </div>
                </div>

                <div className="flex flex-wrap gap-3 text-xs bg-[#0B0E14] p-3 rounded-2xl border border-[#1E2534]">
                  <span className="text-emerald-400 font-semibold">Calm: {log.calm}/5</span>
                  <span className="text-indigo-400 font-semibold">Confidence: {log.confidence}/5</span>
                  <span className="text-amber-400 font-semibold">Stress: {log.stress}/5</span>
                  {log.fomo > 2 && <span className="text-rose-400 font-bold">FOMO: {log.fomo}/5</span>}
                  {log.revenge > 1 && <span className="text-rose-400 font-bold">Revenge: {log.revenge}/5</span>}
                </div>

                <p className="text-xs text-slate-200 italic leading-relaxed">“{log.note}”</p>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
};
