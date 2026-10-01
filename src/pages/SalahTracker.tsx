import React from 'react';
import { MoonStar, Sparkles, CheckCircle2, Users, User, XCircle, Heart } from 'lucide-react';
import { useTraderLife } from '../context/TraderLifeContext';
import { SalahRecord, SalahStatus } from '../types';

export const SalahTracker: React.FC = () => {
  const { salahRecords, updateSalahStatus, selectedDate, language } = useTraderLife();

  const todayRecord: SalahRecord = salahRecords[selectedDate] || {
    date: selectedDate,
    fajr: 'NONE',
    dhuhr: 'NONE',
    asr: 'NONE',
    maghrib: 'NONE',
    isha: 'NONE',
  };

  const prayers: { key: keyof Omit<SalahRecord, 'date' | 'notes'>; nameEn: string; nameBn: string; timeDesc: string }[] = [
    { key: 'fajr', nameEn: 'Fajr', nameBn: 'ফজর', timeDesc: 'Dawn prayer before sunrise' },
    { key: 'dhuhr', nameEn: 'Dhuhr', nameBn: 'যোহর', timeDesc: 'Noon prayer after sun zenith' },
    { key: 'asr', nameEn: 'Asr', nameBn: 'আসর', timeDesc: 'Late afternoon prayer' },
    { key: 'maghrib', nameEn: 'Maghrib', nameBn: 'মাগরিব', timeDesc: 'Sunset prayer' },
    { key: 'isha', nameEn: 'Isha', nameBn: 'এশা', timeDesc: 'Night prayer' },
  ];

  const prayerStatuses = [todayRecord.fajr, todayRecord.dhuhr, todayRecord.asr, todayRecord.maghrib, todayRecord.isha];
  const completedCount = prayerStatuses.filter((s) => s === 'JAMAAT' || s === 'ALONE').length;
  const jamaatCount = prayerStatuses.filter((s) => s === 'JAMAAT').length;

  // Monthly stats calculation
  const allDays = Object.values(salahRecords);
  const totalDays = Math.max(allDays.length, 1);
  let totalJamaatAll = 0;
  let totalAloneAll = 0;
  let totalMissedAll = 0;

  allDays.forEach((rec) => {
    [rec.fajr, rec.dhuhr, rec.asr, rec.maghrib, rec.isha].forEach((status) => {
      if (status === 'JAMAAT') totalJamaatAll++;
      else if (status === 'ALONE') totalAloneAll++;
      else if (status === 'MISSED') totalMissedAll++;
    });
  });

  const totalPossible = totalDays * 5;
  const jamaatPct = Math.round((totalJamaatAll / totalPossible) * 100);
  const alonePct = Math.round((totalAloneAll / totalPossible) * 100);
  const missedPct = Math.round((totalMissedAll / totalPossible) * 100);

  return (
    <div className="space-y-6 pb-20 lg:pb-8">
      {/* Title */}
      <div>
        <h1 className="text-xl sm:text-2xl font-black text-slate-100">Salah & Spiritual Discipline</h1>
        <p className="text-xs text-slate-400">Track 5 daily prayers, Jamaat consistency and spiritual peace</p>
      </div>

      {/* Islamic Reminder Banner */}
      <div className="p-6 rounded-3xl bg-gradient-to-r from-indigo-950/40 via-[#10141C] to-indigo-950/40 border border-indigo-500/30 glow-card space-y-4">
        <div className="flex items-center justify-between">
          <div className="flex items-center gap-3">
            <div className="w-10 h-10 rounded-2xl bg-indigo-500/20 border border-indigo-500/40 flex items-center justify-center text-indigo-400">
              <MoonStar className="w-5 h-5" />
            </div>
            <div>
              <span className="text-xs font-bold text-indigo-400 uppercase tracking-wider block">
                {language === 'bn' ? 'সালাত ও অন্তরের প্রশান্তি' : 'Spiritual Anchor & Focus'}
              </span>
              <h2 className="text-lg sm:text-xl font-black text-slate-100">
                {completedCount}/5 Prayed ({jamaatCount} in Jamaat)
              </h2>
            </div>
          </div>
          <span
            className={`px-3 py-1 rounded-xl text-xs font-bold ${
              completedCount === 5
                ? 'bg-emerald-500/20 text-emerald-300 border border-emerald-500/40'
                : 'bg-indigo-500/20 text-indigo-300 border border-indigo-500/40'
            }`}
          >
            {completedCount === 5 ? 'PERFECT 5/5 ⭐' : `${completedCount}/5 COMPLETED`}
          </span>
        </div>

        <p className="text-xs text-slate-300 italic">
          “Indeed, prayer prohibits immorality and wrongdoing, and the remembrance of Allah is greater.” (Surah Al-Ankabut: 45)
        </p>
      </div>

      {/* 5 Daily Prayers Cards */}
      <div className="space-y-3">
        {prayers.map((p) => {
          const currentStatus = todayRecord[p.key];
          return (
            <div
              key={p.key}
              className={`p-5 rounded-3xl border transition-all ${
                currentStatus === 'JAMAAT'
                  ? 'bg-[#10141C] border-emerald-500/40'
                  : currentStatus === 'ALONE'
                  ? 'bg-[#10141C] border-amber-500/40'
                  : currentStatus === 'MISSED'
                  ? 'bg-[#10141C] border-rose-500/40'
                  : 'bg-[#0B0E14] border-[#1E2534]'
              }`}
            >
              <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
                <div>
                  <div className="flex items-center gap-2">
                    <h3 className="text-base font-bold text-slate-100">{p.nameEn}</h3>
                    {language !== 'en' && <span className="text-xs text-amber-400 font-semibold">({p.nameBn})</span>}
                  </div>
                  <p className="text-xs text-slate-400 mt-0.5">{p.timeDesc}</p>
                </div>

                {/* 3 Status Buttons */}
                <div className="grid grid-cols-3 gap-2 w-full sm:w-auto">
                  <button
                    onClick={() => updateSalahStatus(selectedDate, p.key, 'JAMAAT')}
                    className={`flex items-center justify-center gap-1.5 px-4 py-2 rounded-xl text-xs font-bold transition-all ${
                      currentStatus === 'JAMAAT'
                        ? 'bg-emerald-500 text-black shadow-md shadow-emerald-500/20'
                        : 'bg-[#161B26] text-slate-400 hover:text-emerald-300 border border-[#1E2534]'
                    }`}
                  >
                    <Users className="w-3.5 h-3.5" /> Jamaat
                  </button>

                  <button
                    onClick={() => updateSalahStatus(selectedDate, p.key, 'ALONE')}
                    className={`flex items-center justify-center gap-1.5 px-4 py-2 rounded-xl text-xs font-bold transition-all ${
                      currentStatus === 'ALONE'
                        ? 'bg-amber-500 text-black shadow-md shadow-amber-500/20'
                        : 'bg-[#161B26] text-slate-400 hover:text-amber-300 border border-[#1E2534]'
                    }`}
                  >
                    <User className="w-3.5 h-3.5" /> Alone
                  </button>

                  <button
                    onClick={() => updateSalahStatus(selectedDate, p.key, 'MISSED')}
                    className={`flex items-center justify-center gap-1.5 px-4 py-2 rounded-xl text-xs font-bold transition-all ${
                      currentStatus === 'MISSED'
                        ? 'bg-rose-500 text-white shadow-md shadow-rose-500/20'
                        : 'bg-[#161B26] text-slate-400 hover:text-rose-300 border border-[#1E2534]'
                    }`}
                  >
                    <XCircle className="w-3.5 h-3.5" /> Missed
                  </button>
                </div>
              </div>
            </div>
          );
        })}
      </div>

      {/* Monthly Statistics Card */}
      <div className="p-6 rounded-3xl glow-card border-[#1E2534] space-y-4">
        <h3 className="text-base font-bold text-slate-100">Monthly Salah Consistency Statistics</h3>
        <div className="grid grid-cols-3 gap-4 text-center">
          <div className="p-4 rounded-2xl bg-[#10141C] border border-[#1E2534]">
            <span className="text-2xl font-black text-emerald-400">{jamaatPct}%</span>
            <span className="text-[11px] text-slate-400 block mt-1 font-medium">In Jamaat (জামাত)</span>
          </div>
          <div className="p-4 rounded-2xl bg-[#10141C] border border-[#1E2534]">
            <span className="text-2xl font-black text-amber-400">{alonePct}%</span>
            <span className="text-[11px] text-slate-400 block mt-1 font-medium">Alone (একাকী)</span>
          </div>
          <div className="p-4 rounded-2xl bg-[#10141C] border border-[#1E2534]">
            <span className="text-2xl font-black text-rose-400">{missedPct}%</span>
            <span className="text-[11px] text-slate-400 block mt-1 font-medium">Missed (কাজা)</span>
          </div>
        </div>
      </div>
    </div>
  );
};
