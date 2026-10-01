import React, { useState } from 'react';
import {
  ShieldAlert,
  ShieldCheck,
  Calculator,
  AlertTriangle,
  Info,
  Scale,
  DollarSign,
  TrendingDown,
  Lock,
} from 'lucide-react';
import { useTraderLife } from '../context/TraderLifeContext';

export const RiskManager: React.FC = () => {
  const { settings, updateSettings, trades, selectedDate } = useTraderLife();

  const [balance, setBalance] = useState(settings.demoBalance.toString());
  const [riskPct, setRiskPct] = useState(settings.riskPerTradePct.toString());
  const [maxDailyLoss, setMaxDailyLoss] = useState(settings.maxDailyLoss.toString());
  const [maxTrades, setMaxTrades] = useState(settings.maxTradesPerDay.toString());

  // Calculator State
  const [calcPair, setCalcPair] = useState('EUR/USD');
  const [calcEntry, setCalcEntry] = useState('1.08500');
  const [calcSL, setCalcSL] = useState('1.08200');
  const [calcTP, setCalcTP] = useState('1.09100');

  const todayTrades = trades.filter((t) => t.date === selectedDate);
  const todayPnl = todayTrades.reduce((acc, t) => acc + t.pnl, 0);
  const todayRisked = todayTrades.reduce((acc, t) => acc + t.riskAmount, 0);

  const numBal = parseFloat(balance) || settings.demoBalance;
  const numRiskPct = parseFloat(riskPct) || settings.riskPerTradePct;
  const numMaxLoss = parseFloat(maxDailyLoss) || settings.maxDailyLoss;
  const numMaxTrades = parseInt(maxTrades) || settings.maxTradesPerDay;

  const autoRiskPerTrade = numBal * (numRiskPct / 100);
  const maxDailyRiskExposure = autoRiskPerTrade * numMaxTrades;

  // Calculator math
  const cEntry = parseFloat(calcEntry) || 1.085;
  const cSL = parseFloat(calcSL) || 1.082;
  const cTP = parseFloat(calcTP) || 1.091;
  const slDist = Math.abs(cEntry - cSL);
  const tpDist = Math.abs(cTP - cEntry);
  const rrRatio = slDist > 0.000001 ? tpDist / slDist : 0;
  const rewardAmount = autoRiskPerTrade * rrRatio;
  const pips = slDist * 10000;
  const estimatedLotSize = pips > 0 ? autoRiskPerTrade / (pips * 10) : 0;

  const isRiskExceeded = autoRiskPerTrade > numMaxLoss;

  const riskStatus =
    todayTrades.some((t) => t.pnl <= -numMaxLoss) || todayPnl <= -numMaxLoss
      ? 'BREACHED'
      : todayPnl <= -numMaxLoss * 0.7 || todayRisked > maxDailyRiskExposure
      ? 'CAUTION'
      : 'SAFE';

  const handleSaveLimits = (e: React.FormEvent) => {
    e.preventDefault();
    updateSettings({
      demoBalance: numBal,
      riskPerTradePct: numRiskPct,
      maxDailyLoss: numMaxLoss,
      maxTradesPerDay: numMaxTrades,
    });
  };

  return (
    <div className="space-y-6 pb-20 lg:pb-8">
      {/* Title */}
      <div>
        <h1 className="text-xl sm:text-2xl font-black text-slate-100">Risk Manager & Calculator</h1>
        <p className="text-xs text-slate-400">Strict capital protection rules & live pre-trade sizing calculation</p>
      </div>

      {/* Mandatory Disclaimer */}
      <div className="p-4 rounded-2xl bg-amber-500/10 border border-amber-500/30 flex items-start gap-3">
        <Info className="w-5 h-5 text-amber-400 shrink-0 mt-0.5" />
        <div className="text-xs text-amber-200/90 leading-relaxed">
          <strong className="text-amber-400 block font-semibold mb-0.5">Discipline & Tracking Notice</strong>
          This application is solely a personal habit tracking, risk analysis, and paper/demo trading journal. It does not execute live trades, broker transactions, or manage real money accounts.
        </div>
      </div>

      {/* Daily Risk Status Alert Card */}
      <div
        className={`p-6 rounded-3xl border flex flex-col md:flex-row md:items-center justify-between gap-6 transition-all ${
          riskStatus === 'SAFE'
            ? 'bg-emerald-950/20 border-emerald-500/30 text-emerald-300'
            : riskStatus === 'CAUTION'
            ? 'bg-amber-950/20 border-amber-500/30 text-amber-300'
            : 'bg-rose-950/20 border-rose-500/40 text-rose-300'
        }`}
      >
        <div className="flex items-center gap-4">
          <div
            className={`w-14 h-14 rounded-2xl flex items-center justify-center ${
              riskStatus === 'SAFE'
                ? 'bg-emerald-500/20 text-emerald-400 border border-emerald-500/30'
                : riskStatus === 'CAUTION'
                ? 'bg-amber-500/20 text-amber-400 border border-amber-500/30'
                : 'bg-rose-500/20 text-rose-400 border border-rose-500/30'
            }`}
          >
            {riskStatus === 'SAFE' ? (
              <ShieldCheck className="w-7 h-7" />
            ) : (
              <ShieldAlert className="w-7 h-7" />
            )}
          </div>
          <div>
            <div className="flex items-center gap-2">
              <span className="text-xs uppercase font-bold text-slate-400">Daily Risk Status:</span>
              <span className="text-lg font-black">{riskStatus}</span>
            </div>
            <p className="text-xs text-slate-300 mt-0.5">
              {riskStatus === 'SAFE'
                ? 'Within allowed daily risk parameters. Keep executing with discipline.'
                : riskStatus === 'CAUTION'
                ? 'Approaching max loss or risk limits. Slow down and review setups.'
                : 'MAX DAILY LOSS LIMIT BREACHED! Stop demo trading for today immediately.'}
            </p>
          </div>
        </div>

        <div className="flex items-center gap-6 border-t md:border-t-0 md:border-l border-slate-700/50 pt-3 md:pt-0 md:pl-6 text-xs">
          <div>
            <span className="text-slate-400 block text-[11px]">Today's P/L</span>
            <span
              className={`text-base font-black ${
                todayPnl >= 0 ? 'text-emerald-400' : 'text-rose-400'
              }`}
            >
              {todayPnl >= 0 ? '+' : ''}
              {settings.currency}
              {todayPnl.toFixed(2)}
            </span>
          </div>
          <div>
            <span className="text-slate-400 block text-[11px]">Max Daily Loss</span>
            <span className="text-base font-black text-slate-200">
              {settings.currency}
              {settings.maxDailyLoss}
            </span>
          </div>
          <div>
            <span className="text-slate-400 block text-[11px]">Trades Made</span>
            <span className="text-base font-black text-slate-200">
              {todayTrades.length} / {settings.maxTradesPerDay}
            </span>
          </div>
        </div>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Personal Risk Limit Configuration */}
        <div className="p-6 rounded-3xl glow-card border-[#1E2534] space-y-5">
          <div className="flex items-center gap-2.5">
            <Scale className="w-5 h-5 text-amber-400" />
            <h2 className="text-base font-bold text-slate-100">Personal Risk Parameters</h2>
          </div>

          <form onSubmit={handleSaveLimits} className="space-y-4 text-xs">
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
              <div>
                <label className="text-slate-400 block mb-1 font-medium">Demo Capital ({settings.currency})</label>
                <input
                  type="number"
                  step="any"
                  value={balance}
                  onChange={(e) => setBalance(e.target.value)}
                  className="w-full p-3 rounded-xl bg-[#10141C] border border-[#1E2534] text-slate-100 font-bold focus:border-amber-500 focus:outline-none"
                />
              </div>
              <div>
                <label className="text-slate-400 block mb-1 font-medium">Risk Per Trade (%)</label>
                <input
                  type="number"
                  step="any"
                  value={riskPct}
                  onChange={(e) => setRiskPct(e.target.value)}
                  className="w-full p-3 rounded-xl bg-[#10141C] border border-[#1E2534] text-slate-100 font-bold focus:border-amber-500 focus:outline-none"
                />
              </div>
            </div>

            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
              <div>
                <label className="text-slate-400 block mb-1 font-medium">Max Daily Loss Limit ({settings.currency})</label>
                <input
                  type="number"
                  step="any"
                  value={maxDailyLoss}
                  onChange={(e) => setMaxDailyLoss(e.target.value)}
                  className="w-full p-3 rounded-xl bg-[#10141C] border border-[#1E2534] text-slate-100 font-bold focus:border-amber-500 focus:outline-none"
                />
              </div>
              <div>
                <label className="text-slate-400 block mb-1 font-medium">Max Allowed Trades / Day</label>
                <input
                  type="number"
                  value={maxTrades}
                  onChange={(e) => setMaxTrades(e.target.value)}
                  className="w-full p-3 rounded-xl bg-[#10141C] border border-[#1E2534] text-slate-100 font-bold focus:border-amber-500 focus:outline-none"
                />
              </div>
            </div>

            {/* Calculated Risk Breakdown */}
            <div className="p-4 rounded-2xl bg-[#10141C] border border-[#1E2534] space-y-2 text-xs">
              <div className="flex justify-between">
                <span className="text-slate-400">1 Trade Calculated Risk:</span>
                <span className="font-bold text-amber-400">
                  {settings.currency}
                  {autoRiskPerTrade.toFixed(2)}
                </span>
              </div>
              <div className="flex justify-between">
                <span className="text-slate-400">Max Possible Daily Loss (All Loss):</span>
                <span className={`font-bold ${maxDailyRiskExposure > numMaxLoss ? 'text-rose-400' : 'text-slate-200'}`}>
                  {settings.currency}
                  {maxDailyRiskExposure.toFixed(2)}
                </span>
              </div>
            </div>

            {isRiskExceeded && (
              <p className="text-xs text-rose-400 font-semibold flex items-center gap-1.5">
                <AlertTriangle className="w-4 h-4 shrink-0" /> Warning: Single trade risk ({settings.currency}
                {autoRiskPerTrade.toFixed(2)}) exceeds Max Daily Loss ({settings.currency}
                {numMaxLoss})!
              </p>
            )}

            <button
              type="submit"
              className="w-full py-3 rounded-xl bg-amber-500 hover:bg-amber-400 text-black font-extrabold text-xs shadow-md shadow-amber-500/20 active:scale-95 transition-all"
            >
              Save Risk Parameters
            </button>
          </form>
        </div>

        {/* Live Position Size & R:R Calculator */}
        <div className="p-6 rounded-3xl glow-card border-[#1E2534] space-y-5">
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-2.5">
              <Calculator className="w-5 h-5 text-indigo-400" />
              <h2 className="text-base font-bold text-slate-100">Live Position & R:R Calculator</h2>
            </div>
            <span className="text-[10px] font-bold px-2 py-0.5 rounded bg-indigo-500/10 text-indigo-400 border border-indigo-500/20">
              Interactive
            </span>
          </div>

          <div className="space-y-4 text-xs">
            <div className="grid grid-cols-3 gap-3">
              <div>
                <label className="text-slate-400 block mb-1 font-medium">Entry Price</label>
                <input
                  type="number"
                  step="any"
                  value={calcEntry}
                  onChange={(e) => setCalcEntry(e.target.value)}
                  className="w-full p-2.5 rounded-xl bg-[#10141C] border border-[#1E2534] text-slate-100 font-semibold focus:border-indigo-500 focus:outline-none"
                />
              </div>
              <div>
                <label className="text-slate-400 block mb-1 font-medium">Stop Loss</label>
                <input
                  type="number"
                  step="any"
                  value={calcSL}
                  onChange={(e) => setCalcSL(e.target.value)}
                  className="w-full p-2.5 rounded-xl bg-[#10141C] border border-[#1E2534] text-rose-400 font-semibold focus:border-rose-500 focus:outline-none"
                />
              </div>
              <div>
                <label className="text-slate-400 block mb-1 font-medium">Take Profit</label>
                <input
                  type="number"
                  step="any"
                  value={calcTP}
                  onChange={(e) => setCalcTP(e.target.value)}
                  className="w-full p-2.5 rounded-xl bg-[#10141C] border border-[#1E2534] text-emerald-400 font-semibold focus:border-emerald-500 focus:outline-none"
                />
              </div>
            </div>

            {/* Calculated Output Card */}
            <div className="p-4 rounded-2xl bg-gradient-to-br from-[#10141C] to-[#161B26] border border-[#1E2534] space-y-3">
              <div className="flex justify-between items-center">
                <span className="text-slate-400">Planned Risk ({numRiskPct}%):</span>
                <span className="text-sm font-bold text-rose-400">
                  {settings.currency}
                  {autoRiskPerTrade.toFixed(2)}
                </span>
              </div>
              <div className="flex justify-between items-center">
                <span className="text-slate-400">Potential Reward:</span>
                <span className="text-sm font-bold text-emerald-400">
                  +{settings.currency}
                  {rewardAmount.toFixed(2)}
                </span>
              </div>
              <div className="flex justify-between items-center pt-2 border-t border-[#1E2534]">
                <span className="text-slate-300 font-semibold">Risk to Reward (R:R):</span>
                <span
                  className={`text-base font-black ${
                    rrRatio >= 2 ? 'text-emerald-400' : 'text-amber-400'
                  }`}
                >
                  1 : {rrRatio.toFixed(2)}
                </span>
              </div>
              <div className="flex justify-between items-center">
                <span className="text-slate-400">Estimated Lot Size (100k standard):</span>
                <span className="font-bold text-indigo-300">
                  {Math.max(estimatedLotSize, 0.01).toFixed(2)} Lots
                </span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};
