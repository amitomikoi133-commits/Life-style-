import React, { useState } from 'react';
import {
  Plus,
  Search,
  Filter,
  Trash2,
  Edit2,
  TrendingUp,
  TrendingDown,
  AlertTriangle,
  X,
  Image as ImageIcon,
  CheckCircle2,
  XCircle,
  ExternalLink,
} from 'lucide-react';
import { useTraderLife } from '../context/TraderLifeContext';
import { Trade, TradeDirection, TradeResult } from '../types';

export const Journal: React.FC = () => {
  const { trades, addTrade, updateTrade, deleteTrade, settings } = useTraderLife();

  const [searchQuery, setSearchQuery] = useState('');
  const [resultFilter, setResultFilter] = useState<string>('ALL');
  const [directionFilter, setDirectionFilter] = useState<string>('ALL');
  const [violationOnly, setViolationOnly] = useState(false);

  const [showModal, setShowModal] = useState(false);
  const [editingTrade, setEditingTrade] = useState<Trade | null>(null);
  const [viewingTrade, setViewingTrade] = useState<Trade | null>(null);
  const [deletingId, setDeletingId] = useState<string | null>(null);

  // Form State
  const [formDate, setFormDate] = useState(new Date().toISOString().split('T')[0]);
  const [formTime, setFormTime] = useState('09:30');
  const [formPair, setFormPair] = useState('EUR/USD');
  const [formDirection, setFormDirection] = useState<TradeDirection>('BUY');
  const [formEntry, setFormEntry] = useState('1.08500');
  const [formSL, setFormSL] = useState('1.08200');
  const [formTP, setFormTP] = useState('1.09100');
  const [formRiskPct, setFormRiskPct] = useState(settings.riskPerTradePct.toString());
  const [formRiskAmt, setFormRiskAmt] = useState((settings.demoBalance * (settings.riskPerTradePct / 100)).toString());
  const [formResult, setFormResult] = useState<TradeResult>('WIN');
  const [formPnl, setFormPnl] = useState('150');
  const [formSetup, setFormSetup] = useState('Breakout Retest');
  const [formReason, setFormReason] = useState('');
  const [formEmotionBefore, setFormEmotionBefore] = useState('Calm');
  const [formEmotionDuring, setFormEmotionDuring] = useState('Disciplined');
  const [formAccordingToPlan, setFormAccordingToPlan] = useState(true);
  const [formFomo, setFormFomo] = useState(false);
  const [formRevenge, setFormRevenge] = useState(false);
  const [formOvertrade, setFormOvertrade] = useState(false);
  const [formMistake, setFormMistake] = useState('None');
  const [formLesson, setFormLesson] = useState('');
  const [formScreenshot, setFormScreenshot] = useState<string>('');

  const openAddModal = () => {
    setEditingTrade(null);
    setFormDate(new Date().toISOString().split('T')[0]);
    setFormTime('09:30');
    setFormPair('EUR/USD');
    setFormDirection('BUY');
    setFormEntry('1.08500');
    setFormSL('1.08200');
    setFormTP('1.09100');
    setFormRiskPct(settings.riskPerTradePct.toString());
    setFormRiskAmt((settings.demoBalance * (settings.riskPerTradePct / 100)).toString());
    setFormResult('WIN');
    setFormPnl('150');
    setFormSetup('Breakout Retest');
    setFormReason('');
    setFormEmotionBefore('Calm');
    setFormEmotionDuring('Disciplined');
    setFormAccordingToPlan(true);
    setFormFomo(false);
    setFormRevenge(false);
    setFormOvertrade(false);
    setFormMistake('None');
    setFormLesson('');
    setFormScreenshot('');
    setShowModal(true);
  };

  const openEditModal = (t: Trade) => {
    setEditingTrade(t);
    setFormDate(t.date);
    setFormTime(t.time || '09:30');
    setFormPair(t.pair);
    setFormDirection(t.direction);
    setFormEntry(t.entryPrice.toString());
    setFormSL(t.stopLoss.toString());
    setFormTP(t.takeProfit.toString());
    setFormRiskPct(t.riskPercent.toString());
    setFormRiskAmt(t.riskAmount.toString());
    setFormResult(t.result);
    setFormPnl(t.pnl.toString());
    setFormSetup(t.setup);
    setFormReason(t.reason);
    setFormEmotionBefore(t.emotionBefore);
    setFormEmotionDuring(t.emotionDuring);
    setFormAccordingToPlan(t.accordingToPlan);
    setFormFomo(t.fomo);
    setFormRevenge(t.revenge);
    setFormOvertrade(t.overtrade);
    setFormMistake(t.mistake);
    setFormLesson(t.lesson);
    setFormScreenshot(t.screenshot || '');
    setShowModal(true);
  };

  const handleSave = (e: React.FormEvent) => {
    e.preventDefault();
    const tradeData = {
      date: formDate,
      time: formTime,
      pair: formPair.toUpperCase().trim(),
      direction: formDirection,
      entryPrice: parseFloat(formEntry) || 0,
      stopLoss: parseFloat(formSL) || 0,
      takeProfit: parseFloat(formTP) || 0,
      riskPercent: parseFloat(formRiskPct) || 1.0,
      riskAmount: parseFloat(formRiskAmt) || 100,
      result: formResult,
      pnl: parseFloat(formPnl) || 0,
      setup: formSetup.trim(),
      reason: formReason.trim(),
      emotionBefore: formEmotionBefore.trim(),
      emotionDuring: formEmotionDuring.trim(),
      accordingToPlan: formAccordingToPlan,
      fomo: formFomo,
      revenge: formRevenge,
      overtrade: formOvertrade,
      mistake: formMistake.trim() || 'None',
      lesson: formLesson.trim(),
      screenshot: formScreenshot,
    };

    if (editingTrade) {
      updateTrade({ ...tradeData, id: editingTrade.id, createdAt: editingTrade.createdAt });
    } else {
      addTrade(tradeData);
    }
    setShowModal(false);
  };

  const handleImageUpload = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (file) {
      const reader = new FileReader();
      reader.onloadend = () => {
        setFormScreenshot(reader.result as string);
      };
      reader.readAsDataURL(file);
    }
  };

  // Filtering
  const filteredTrades = trades.filter((t) => {
    const matchQuery =
      t.pair.toLowerCase().includes(searchQuery.toLowerCase()) ||
      t.setup.toLowerCase().includes(searchQuery.toLowerCase()) ||
      t.reason.toLowerCase().includes(searchQuery.toLowerCase()) ||
      t.lesson.toLowerCase().includes(searchQuery.toLowerCase()) ||
      t.mistake.toLowerCase().includes(searchQuery.toLowerCase());

    const matchResult = resultFilter === 'ALL' || t.result === resultFilter;
    const matchDir = directionFilter === 'ALL' || t.direction === directionFilter;
    const matchViolation =
      !violationOnly ||
      !t.accordingToPlan ||
      t.fomo ||
      t.revenge ||
      t.overtrade ||
      (t.mistake !== 'None' && t.mistake.length > 0);

    return matchQuery && matchResult && matchDir && matchViolation;
  });

  return (
    <div className="space-y-6 pb-20 lg:pb-8">
      {/* Top Header & Action */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-xl sm:text-2xl font-black text-slate-100">Trading Journal</h1>
          <p className="text-xs text-slate-400">Searchable & filterable record of demo executions & psychology</p>
        </div>
        <button
          onClick={openAddModal}
          className="flex items-center justify-center gap-2 px-5 py-3 rounded-2xl bg-amber-500 hover:bg-amber-400 text-black font-extrabold text-xs sm:text-sm shadow-lg shadow-amber-500/20 active:scale-95 transition-all"
        >
          <Plus className="w-4 h-4" /> Log New Trade
        </button>
      </div>

      {/* Search & Filter Bar */}
      <div className="p-4 rounded-3xl glow-card border-[#1E2534] space-y-3">
        <div className="flex items-center gap-3">
          <div className="flex-1 flex items-center gap-2 px-3 py-2 rounded-xl bg-[#10141C] border border-[#1E2534]">
            <Search className="w-4 h-4 text-slate-400 shrink-0" />
            <input
              type="text"
              placeholder="Search pair, setup, reason, lesson, mistake..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              className="bg-transparent text-xs text-slate-100 focus:outline-none w-full placeholder:text-slate-500"
            />
            {searchQuery && (
              <button onClick={() => setSearchQuery('')} className="text-slate-400 hover:text-white">
                <X className="w-3.5 h-3.5" />
              </button>
            )}
          </div>
        </div>

        {/* Filter Chips */}
        <div className="flex flex-wrap items-center gap-2 pt-1 text-xs">
          <span className="text-slate-500 text-[11px] font-medium flex items-center gap-1">
            <Filter className="w-3 h-3" /> Filters:
          </span>

          {['ALL', 'WIN', 'LOSS', 'BREAKEVEN'].map((res) => (
            <button
              key={res}
              onClick={() => setResultFilter(res)}
              className={`px-3 py-1 rounded-xl transition-all font-semibold ${
                resultFilter === res
                  ? res === 'WIN'
                    ? 'bg-emerald-500 text-black'
                    : res === 'LOSS'
                    ? 'bg-rose-500 text-white'
                    : 'bg-amber-500 text-black'
                  : 'bg-[#161B26] text-slate-400 hover:text-slate-200 border border-[#1E2534]'
              }`}
            >
              {res}
            </button>
          ))}

          {['BUY', 'SELL'].map((dir) => (
            <button
              key={dir}
              onClick={() => setDirectionFilter(directionFilter === dir ? 'ALL' : dir)}
              className={`px-3 py-1 rounded-xl transition-all font-semibold ${
                directionFilter === dir
                  ? dir === 'BUY'
                    ? 'bg-emerald-500 text-black'
                    : 'bg-rose-500 text-white'
                  : 'bg-[#161B26] text-slate-400 hover:text-slate-200 border border-[#1E2534]'
              }`}
            >
              {dir} Only
            </button>
          ))}

          <button
            onClick={() => setViolationOnly(!violationOnly)}
            className={`px-3 py-1 rounded-xl transition-all font-semibold flex items-center gap-1 ${
              violationOnly
                ? 'bg-rose-500/20 text-rose-300 border border-rose-500/40'
                : 'bg-[#161B26] text-slate-400 hover:text-slate-200 border border-[#1E2534]'
            }`}
          >
            <AlertTriangle className="w-3 h-3" /> Violations
          </button>
        </div>
      </div>

      {/* Trades List / Table */}
      {filteredTrades.length === 0 ? (
        <div className="p-12 text-center rounded-3xl glow-card border-[#1E2534] space-y-3">
          <div className="w-12 h-12 mx-auto rounded-2xl bg-[#161B26] border border-[#1E2534] flex items-center justify-center text-slate-400">
            <Search className="w-6 h-6" />
          </div>
          <h3 className="text-base font-bold text-slate-200">No Journal Entries Found</h3>
          <p className="text-xs text-slate-400 max-w-sm mx-auto">
            Log your demo trades to track discipline, win rate, and emotional patterns.
          </p>
          <button
            onClick={openAddModal}
            className="px-4 py-2 rounded-xl bg-amber-500 text-black font-bold text-xs"
          >
            Log First Trade
          </button>
        </div>
      ) : (
        <div className="space-y-3">
          {filteredTrades.map((trade) => {
            const isProfit = trade.pnl >= 0;
            return (
              <div
                key={trade.id}
                onClick={() => setViewingTrade(trade)}
                className="p-4 sm:p-5 rounded-3xl glow-card border-[#1E2534] hover:border-amber-500/30 cursor-pointer transition-all space-y-3"
              >
                <div className="flex items-center justify-between">
                  <div className="flex items-center gap-2.5">
                    <span
                      className={`px-2.5 py-1 rounded-lg text-xs font-black uppercase ${
                        trade.direction === 'BUY'
                          ? 'bg-emerald-500/15 text-emerald-400 border border-emerald-500/30'
                          : 'bg-rose-500/15 text-rose-400 border border-rose-500/30'
                      }`}
                    >
                      {trade.direction}
                    </span>
                    <span className="font-extrabold text-base text-slate-100">{trade.pair}</span>
                    <span className="text-xs text-slate-400 hidden sm:inline">• {trade.setup}</span>
                  </div>

                  <div className="flex items-center gap-3">
                    <span
                      className={`text-base sm:text-lg font-black ${
                        trade.result === 'WIN'
                          ? 'text-emerald-400'
                          : trade.result === 'LOSS'
                          ? 'text-rose-400'
                          : 'text-amber-400'
                      }`}
                    >
                      {isProfit && trade.pnl > 0 ? '+' : ''}
                      {settings.currency}
                      {trade.pnl.toFixed(2)}
                    </span>
                    <span
                      className={`px-2 py-0.5 rounded text-[10px] font-extrabold ${
                        trade.result === 'WIN'
                          ? 'bg-emerald-500/20 text-emerald-300'
                          : trade.result === 'LOSS'
                          ? 'bg-rose-500/20 text-rose-300'
                          : 'bg-amber-500/20 text-amber-300'
                      }`}
                    >
                      {trade.result}
                    </span>
                  </div>
                </div>

                <div className="grid grid-cols-2 sm:grid-cols-4 gap-2 text-xs text-slate-400 bg-[#0B0E14] p-3 rounded-2xl border border-[#1E2534]">
                  <div>
                    <span className="text-slate-500 text-[10px] block">Entry Price</span>
                    <span className="font-semibold text-slate-200">{trade.entryPrice}</span>
                  </div>
                  <div>
                    <span className="text-slate-500 text-[10px] block">Stop Loss</span>
                    <span className="font-semibold text-rose-400">{trade.stopLoss}</span>
                  </div>
                  <div>
                    <span className="text-slate-500 text-[10px] block">Take Profit</span>
                    <span className="font-semibold text-emerald-400">{trade.takeProfit}</span>
                  </div>
                  <div>
                    <span className="text-slate-500 text-[10px] block">Risk Amount</span>
                    <span className="font-semibold text-slate-200">
                      {settings.currency}
                      {trade.riskAmount} ({trade.riskPercent}%)
                    </span>
                  </div>
                </div>

                {/* Violation & Tags Row */}
                <div className="flex flex-wrap items-center justify-between gap-2 pt-1">
                  <div className="flex flex-wrap items-center gap-1.5">
                    {trade.fomo && (
                      <span className="px-2 py-0.5 rounded text-[10px] font-bold bg-rose-500/15 text-rose-300 border border-rose-500/30">
                        FOMO
                      </span>
                    )}
                    {trade.revenge && (
                      <span className="px-2 py-0.5 rounded text-[10px] font-bold bg-rose-500/15 text-rose-300 border border-rose-500/30">
                        REVENGE
                      </span>
                    )}
                    {trade.overtrade && (
                      <span className="px-2 py-0.5 rounded text-[10px] font-bold bg-amber-500/15 text-amber-300 border border-amber-500/30">
                        OVERTRADE
                      </span>
                    )}
                    {!trade.accordingToPlan && (
                      <span className="px-2 py-0.5 rounded text-[10px] font-bold bg-rose-500/15 text-rose-300 border border-rose-500/30">
                        OFF PLAN
                      </span>
                    )}
                    {trade.screenshot && (
                      <span className="px-2 py-0.5 rounded text-[10px] font-bold bg-indigo-500/15 text-indigo-300 border border-indigo-500/30 flex items-center gap-1">
                        <ImageIcon className="w-3 h-3" /> Chart
                      </span>
                    )}
                  </div>

                  <div className="text-[11px] text-slate-500">
                    {trade.date} {trade.time && `• ${trade.time}`}
                  </div>
                </div>
              </div>
            );
          })}
        </div>
      )}

      {/* Add / Edit Trade Modal */}
      {showModal && (
        <div className="fixed inset-0 z-50 bg-black/80 backdrop-blur-sm flex items-center justify-center p-4 overflow-y-auto">
          <div className="w-full max-w-2xl bg-[#0B0E14] border border-[#1E2534] rounded-3xl p-6 shadow-2xl my-8 space-y-5">
            <div className="flex items-center justify-between border-b border-[#1E2534] pb-4">
              <h2 className="text-lg font-bold text-slate-100">
                {editingTrade ? 'Edit Trade Journal' : 'Log Paper Trade'}
              </h2>
              <button
                onClick={() => setShowModal(false)}
                className="p-1 rounded-lg text-slate-400 hover:text-white"
              >
                <X className="w-5 h-5" />
              </button>
            </div>

            <form onSubmit={handleSave} className="space-y-4 text-xs">
              {/* Row 1: Date, Time, Pair, Direction */}
              <div className="grid grid-cols-2 sm:grid-cols-4 gap-3">
                <div>
                  <label className="text-slate-400 block mb-1">Date</label>
                  <input
                    type="date"
                    required
                    value={formDate}
                    onChange={(e) => setFormDate(e.target.value)}
                    className="w-full p-2.5 rounded-xl bg-[#10141C] border border-[#1E2534] text-slate-200 focus:border-amber-500 focus:outline-none"
                  />
                </div>
                <div>
                  <label className="text-slate-400 block mb-1">Time</label>
                  <input
                    type="time"
                    value={formTime}
                    onChange={(e) => setFormTime(e.target.value)}
                    className="w-full p-2.5 rounded-xl bg-[#10141C] border border-[#1E2534] text-slate-200 focus:border-amber-500 focus:outline-none"
                  />
                </div>
                <div>
                  <label className="text-slate-400 block mb-1">Pair / Asset</label>
                  <input
                    type="text"
                    required
                    placeholder="EUR/USD, XAU/USD..."
                    value={formPair}
                    onChange={(e) => setFormPair(e.target.value)}
                    className="w-full p-2.5 rounded-xl bg-[#10141C] border border-[#1E2534] text-slate-200 focus:border-amber-500 focus:outline-none uppercase font-bold"
                  />
                </div>
                <div>
                  <label className="text-slate-400 block mb-1">Direction</label>
                  <div className="grid grid-cols-2 gap-1 bg-[#10141C] p-1 rounded-xl border border-[#1E2534]">
                    <button
                      type="button"
                      onClick={() => setFormDirection('BUY')}
                      className={`py-1.5 rounded-lg font-bold ${
                        formDirection === 'BUY'
                          ? 'bg-emerald-500 text-black'
                          : 'text-slate-400 hover:text-white'
                      }`}
                    >
                      BUY
                    </button>
                    <button
                      type="button"
                      onClick={() => setFormDirection('SELL')}
                      className={`py-1.5 rounded-lg font-bold ${
                        formDirection === 'SELL'
                          ? 'bg-rose-500 text-white'
                          : 'text-slate-400 hover:text-white'
                      }`}
                    >
                      SELL
                    </button>
                  </div>
                </div>
              </div>

              {/* Row 2: Entry, SL, TP, PnL */}
              <div className="grid grid-cols-2 sm:grid-cols-4 gap-3">
                <div>
                  <label className="text-slate-400 block mb-1">Entry Price</label>
                  <input
                    type="number"
                    step="any"
                    required
                    value={formEntry}
                    onChange={(e) => setFormEntry(e.target.value)}
                    className="w-full p-2.5 rounded-xl bg-[#10141C] border border-[#1E2534] text-slate-200 focus:border-amber-500 focus:outline-none"
                  />
                </div>
                <div>
                  <label className="text-slate-400 block mb-1">Stop Loss</label>
                  <input
                    type="number"
                    step="any"
                    required
                    value={formSL}
                    onChange={(e) => setFormSL(e.target.value)}
                    className="w-full p-2.5 rounded-xl bg-[#10141C] border border-[#1E2534] text-slate-200 focus:border-amber-500 focus:outline-none"
                  />
                </div>
                <div>
                  <label className="text-slate-400 block mb-1">Take Profit</label>
                  <input
                    type="number"
                    step="any"
                    required
                    value={formTP}
                    onChange={(e) => setFormTP(e.target.value)}
                    className="w-full p-2.5 rounded-xl bg-[#10141C] border border-[#1E2534] text-slate-200 focus:border-amber-500 focus:outline-none"
                  />
                </div>
                <div>
                  <label className="text-slate-400 block mb-1">Result & P/L ({settings.currency})</label>
                  <div className="flex gap-2">
                    <select
                      value={formResult}
                      onChange={(e) => setFormResult(e.target.value as TradeResult)}
                      className="p-2.5 rounded-xl bg-[#10141C] border border-[#1E2534] text-slate-200 focus:border-amber-500 focus:outline-none font-semibold"
                    >
                      <option value="WIN">WIN</option>
                      <option value="LOSS">LOSS</option>
                      <option value="BREAKEVEN">BE</option>
                      <option value="OPEN">OPEN</option>
                    </select>
                    <input
                      type="number"
                      step="any"
                      required
                      placeholder="P/L $"
                      value={formPnl}
                      onChange={(e) => setFormPnl(e.target.value)}
                      className="w-full p-2.5 rounded-xl bg-[#10141C] border border-[#1E2534] text-slate-200 focus:border-amber-500 focus:outline-none font-bold"
                    />
                  </div>
                </div>
              </div>

              {/* Row 3: Setup & Confluence Reason */}
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
                <div>
                  <label className="text-slate-400 block mb-1">Trading Setup</label>
                  <input
                    type="text"
                    required
                    placeholder="Order Block, Liquidity Sweep, FVG, Breakout..."
                    value={formSetup}
                    onChange={(e) => setFormSetup(e.target.value)}
                    className="w-full p-2.5 rounded-xl bg-[#10141C] border border-[#1E2534] text-slate-200 focus:border-amber-500 focus:outline-none"
                  />
                </div>
                <div>
                  <label className="text-slate-400 block mb-1">Reason for Entry</label>
                  <input
                    type="text"
                    placeholder="Higher timeframe bias, news reaction, confluence..."
                    value={formReason}
                    onChange={(e) => setFormReason(e.target.value)}
                    className="w-full p-2.5 rounded-xl bg-[#10141C] border border-[#1E2534] text-slate-200 focus:border-amber-500 focus:outline-none"
                  />
                </div>
              </div>

              {/* Row 4: Emotions Before & During */}
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
                <div>
                  <label className="text-slate-400 block mb-1">Before-Trade Emotion</label>
                  <input
                    type="text"
                    placeholder="Calm, Confident, Anxious, Impatient..."
                    value={formEmotionBefore}
                    onChange={(e) => setFormEmotionBefore(e.target.value)}
                    className="w-full p-2.5 rounded-xl bg-[#10141C] border border-[#1E2534] text-slate-200 focus:border-amber-500 focus:outline-none"
                  />
                </div>
                <div>
                  <label className="text-slate-400 block mb-1">During-Trade Emotion</label>
                  <input
                    type="text"
                    placeholder="Disciplined, Panicked, Relaxed, Excited..."
                    value={formEmotionDuring}
                    onChange={(e) => setFormEmotionDuring(e.target.value)}
                    className="w-full p-2.5 rounded-xl bg-[#10141C] border border-[#1E2534] text-slate-200 focus:border-amber-500 focus:outline-none"
                  />
                </div>
              </div>

              {/* Psychology & Discipline Checkboxes */}
              <div className="p-3.5 rounded-2xl bg-[#10141C] border border-[#1E2534] space-y-3">
                <span className="font-bold text-amber-400 block">Discipline Check</span>
                <div className="grid grid-cols-2 sm:grid-cols-4 gap-3">
                  <label className="flex items-center gap-2 cursor-pointer">
                    <input
                      type="checkbox"
                      checked={formAccordingToPlan}
                      onChange={(e) => setFormAccordingToPlan(e.target.checked)}
                      className="rounded accent-emerald-500 w-4 h-4"
                    />
                    <span className="text-slate-200">According to plan?</span>
                  </label>
                  <label className="flex items-center gap-2 cursor-pointer">
                    <input
                      type="checkbox"
                      checked={formFomo}
                      onChange={(e) => setFormFomo(e.target.checked)}
                      className="rounded accent-rose-500 w-4 h-4"
                    />
                    <span className="text-slate-200">FOMO Trade?</span>
                  </label>
                  <label className="flex items-center gap-2 cursor-pointer">
                    <input
                      type="checkbox"
                      checked={formRevenge}
                      onChange={(e) => setFormRevenge(e.target.checked)}
                      className="rounded accent-rose-500 w-4 h-4"
                    />
                    <span className="text-slate-200">Revenge Trade?</span>
                  </label>
                  <label className="flex items-center gap-2 cursor-pointer">
                    <input
                      type="checkbox"
                      checked={formOvertrade}
                      onChange={(e) => setFormOvertrade(e.target.checked)}
                      className="rounded accent-amber-500 w-4 h-4"
                    />
                    <span className="text-slate-200">Overtrade?</span>
                  </label>
                </div>
              </div>

              {/* Mistake & Lesson */}
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
                <div>
                  <label className="text-slate-400 block mb-1">Mistake Made</label>
                  <input
                    type="text"
                    placeholder="None, Moved SL, Sized too high..."
                    value={formMistake}
                    onChange={(e) => setFormMistake(e.target.value)}
                    className="w-full p-2.5 rounded-xl bg-[#10141C] border border-[#1E2534] text-slate-200 focus:border-amber-500 focus:outline-none"
                  />
                </div>
                <div>
                  <label className="text-slate-400 block mb-1">Lesson Learned</label>
                  <input
                    type="text"
                    placeholder="What to do differently next time..."
                    value={formLesson}
                    onChange={(e) => setFormLesson(e.target.value)}
                    className="w-full p-2.5 rounded-xl bg-[#10141C] border border-[#1E2534] text-slate-200 focus:border-amber-500 focus:outline-none"
                  />
                </div>
              </div>

              {/* Screenshot Upload */}
              <div>
                <label className="text-slate-400 block mb-1">Chart Screenshot (Optional)</label>
                <div className="flex items-center gap-3">
                  <input
                    type="file"
                    accept="image/*"
                    onChange={handleImageUpload}
                    className="text-xs text-slate-400 file:mr-3 file:py-2 file:px-4 file:rounded-xl file:border-0 file:text-xs file:font-semibold file:bg-[#1E2534] file:text-slate-200 hover:file:bg-[#2A3346] cursor-pointer"
                  />
                  {formScreenshot && (
                    <span className="text-[11px] text-emerald-400 font-semibold flex items-center gap-1">
                      <CheckCircle2 className="w-3.5 h-3.5" /> Attached
                    </span>
                  )}
                </div>
              </div>

              <div className="flex justify-end gap-3 pt-4 border-t border-[#1E2534]">
                <button
                  type="button"
                  onClick={() => setShowModal(false)}
                  className="px-4 py-2.5 rounded-xl bg-[#161B26] hover:bg-[#1E2534] text-slate-300 font-semibold"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="px-6 py-2.5 rounded-xl bg-amber-500 hover:bg-amber-400 text-black font-extrabold shadow-md shadow-amber-500/20"
                >
                  Save Trade
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Trade Detail Modal */}
      {viewingTrade && (
        <div className="fixed inset-0 z-50 bg-black/80 backdrop-blur-sm flex items-center justify-center p-4 overflow-y-auto">
          <div className="w-full max-w-lg bg-[#0B0E14] border border-[#1E2534] rounded-3xl p-6 shadow-2xl space-y-4">
            <div className="flex items-center justify-between border-b border-[#1E2534] pb-3">
              <div className="flex items-center gap-2">
                <span
                  className={`px-2.5 py-1 rounded-lg text-xs font-black ${
                    viewingTrade.direction === 'BUY'
                      ? 'bg-emerald-500/15 text-emerald-400'
                      : 'bg-rose-500/15 text-rose-400'
                  }`}
                >
                  {viewingTrade.direction}
                </span>
                <h3 className="text-lg font-bold text-slate-100">{viewingTrade.pair}</h3>
              </div>
              <button onClick={() => setViewingTrade(null)} className="text-slate-400 hover:text-white">
                <X className="w-5 h-5" />
              </button>
            </div>

            <div
              className={`p-4 rounded-2xl border flex items-center justify-between ${
                viewingTrade.pnl >= 0
                  ? 'bg-emerald-500/10 border-emerald-500/30 text-emerald-400'
                  : 'bg-rose-500/10 border-rose-500/30 text-rose-400'
              }`}
            >
              <div>
                <span className="text-[11px] text-slate-400 block">Result & P/L</span>
                <span className="text-xl font-black">
                  {viewingTrade.result} ({viewingTrade.pnl >= 0 ? '+' : ''}
                  {settings.currency}
                  {viewingTrade.pnl.toFixed(2)})
                </span>
              </div>
              <div className="text-right">
                <span className="text-[11px] text-slate-400 block">Risk Amount</span>
                <span className="font-bold text-slate-200">
                  {settings.currency}
                  {viewingTrade.riskAmount} ({viewingTrade.riskPercent}%)
                </span>
              </div>
            </div>

            <div className="grid grid-cols-3 gap-2 text-xs bg-[#10141C] p-3 rounded-2xl border border-[#1E2534]">
              <div>
                <span className="text-slate-500 block text-[10px]">Entry</span>
                <span className="font-bold text-slate-100">{viewingTrade.entryPrice}</span>
              </div>
              <div>
                <span className="text-slate-500 block text-[10px]">Stop Loss</span>
                <span className="font-bold text-rose-400">{viewingTrade.stopLoss}</span>
              </div>
              <div>
                <span className="text-slate-500 block text-[10px]">Take Profit</span>
                <span className="font-bold text-emerald-400">{viewingTrade.takeProfit}</span>
              </div>
            </div>

            <div className="space-y-2 text-xs">
              <p className="text-slate-300">
                <strong className="text-amber-400">Setup:</strong> {viewingTrade.setup}
              </p>
              {viewingTrade.reason && (
                <p className="text-slate-300">
                  <strong className="text-slate-400">Entry Reason:</strong> {viewingTrade.reason}
                </p>
              )}
              <div className="p-3 rounded-xl bg-[#10141C] border border-[#1E2534] space-y-1 text-slate-300">
                <p>
                  <strong className="text-slate-400">Emotions:</strong> Before: {viewingTrade.emotionBefore} | During: {viewingTrade.emotionDuring}
                </p>
                <p>
                  <strong className="text-slate-400">Trading Plan:</strong>{' '}
                  {viewingTrade.accordingToPlan ? (
                    <span className="text-emerald-400 font-semibold">Followed Plan</span>
                  ) : (
                    <span className="text-rose-400 font-semibold">Broke Plan</span>
                  )}
                </p>
              </div>

              {viewingTrade.mistake !== 'None' && viewingTrade.mistake && (
                <div className="p-3 rounded-xl bg-rose-500/10 border border-rose-500/30 text-rose-200">
                  <strong>Mistake:</strong> {viewingTrade.mistake}
                </div>
              )}

              {viewingTrade.lesson && (
                <div className="p-3 rounded-xl bg-[#1E2534] border border-[#2A3346] text-slate-200">
                  <strong className="text-amber-400">Lesson:</strong> “{viewingTrade.lesson}”
                </div>
              )}

              {viewingTrade.screenshot && (
                <div className="pt-2">
                  <span className="text-slate-400 block mb-1 font-semibold">Attached Chart</span>
                  <img
                    src={viewingTrade.screenshot}
                    alt="Trade Analysis"
                    className="w-full max-h-56 object-contain rounded-xl border border-[#1E2534]"
                  />
                </div>
              )}
            </div>

            <div className="flex justify-between items-center pt-3 border-t border-[#1E2534]">
              <button
                onClick={() => {
                  setDeletingId(viewingTrade.id);
                  setViewingTrade(null);
                }}
                className="flex items-center gap-1.5 px-3 py-2 rounded-xl text-rose-400 hover:bg-rose-500/10 text-xs font-semibold"
              >
                <Trash2 className="w-4 h-4" /> Delete
              </button>
              <button
                onClick={() => {
                  openEditModal(viewingTrade);
                  setViewingTrade(null);
                }}
                className="flex items-center gap-1.5 px-4 py-2 rounded-xl bg-amber-500 text-black text-xs font-bold"
              >
                <Edit2 className="w-4 h-4" /> Edit Trade
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Delete Confirmation Modal */}
      {deletingId && (
        <div className="fixed inset-0 z-50 bg-black/80 backdrop-blur-sm flex items-center justify-center p-4">
          <div className="w-full max-w-sm bg-[#0B0E14] border border-[#1E2534] rounded-3xl p-6 shadow-2xl text-center space-y-4">
            <div className="w-12 h-12 mx-auto rounded-2xl bg-rose-500/15 border border-rose-500/30 flex items-center justify-center text-rose-400">
              <Trash2 className="w-6 h-6" />
            </div>
            <h3 className="text-base font-bold text-slate-100">Delete Journal Entry?</h3>
            <p className="text-xs text-slate-400">
              Are you sure you want to delete this trade from your journal? This action cannot be undone.
            </p>
            <div className="flex gap-3 pt-2">
              <button
                onClick={() => setDeletingId(null)}
                className="flex-1 py-2.5 rounded-xl bg-[#161B26] hover:bg-[#1E2534] text-slate-300 font-semibold text-xs"
              >
                Cancel
              </button>
              <button
                onClick={() => {
                  if (deletingId) deleteTrade(deletingId);
                  setDeletingId(null);
                }}
                className="flex-1 py-2.5 rounded-xl bg-rose-500 hover:bg-rose-600 text-white font-bold text-xs"
              >
                Delete
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};
