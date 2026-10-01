import React, { useState } from 'react';
import {
  Settings as SettingsIcon,
  User,
  Shield,
  Globe,
  Download,
  Upload,
  RotateCcw,
  Lock,
  CheckCircle2,
  DollarSign,
  AlertTriangle,
} from 'lucide-react';
import { useTraderLife } from '../context/TraderLifeContext';
import { Language } from '../types';

export const Settings: React.FC = () => {
  const { settings, updateSettings, exportData, importData, resetAllData } = useTraderLife();

  const [name, setName] = useState(settings.traderName);
  const [currency, setCurrency] = useState(settings.currency);
  const [monthlyTarget, setMonthlyTarget] = useState(settings.currentMonthTarget.toString());
  const [riskLimit, setRiskLimit] = useState(settings.riskPerTradePct.toString());
  const [dailyLoss, setDailyLoss] = useState(settings.maxDailyLoss.toString());
  const [maxTrades, setMaxTrades] = useState(settings.maxTradesPerDay.toString());
  const [language, setLang] = useState<Language>(settings.language);
  const [pinLockEnabled, setPinLockEnabled] = useState(settings.pinLockEnabled);
  const [pinCode, setPinCode] = useState(settings.pinCode);

  const [showResetConfirm, setShowResetConfirm] = useState(false);

  const handleSave = (e: React.FormEvent) => {
    e.preventDefault();
    updateSettings({
      traderName: name.trim() || 'Disciplined Trader',
      currency,
      currentMonthTarget: parseFloat(monthlyTarget) || 2.0,
      riskPerTradePct: parseFloat(riskLimit) || 1.0,
      maxDailyLoss: parseFloat(dailyLoss) || 200,
      maxTradesPerDay: parseInt(maxTrades) || 3,
      language,
      pinLockEnabled,
      pinCode: pinCode.trim() || '1234',
    });
  };

  const handleDownloadBackup = () => {
    const jsonStr = exportData();
    const blob = new Blob([jsonStr], { type: 'application/json' });
    const url = URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = `my_trader_life_backup_${new Date().toISOString().split('T')[0]}.json`;
    link.click();
    URL.revokeObjectURL(url);
  };

  const handleImportFile = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (file) {
      const reader = new FileReader();
      reader.onload = (event) => {
        const text = event.target?.result as string;
        if (text) {
          importData(text);
        }
      };
      reader.readAsText(file);
    }
  };

  return (
    <div className="space-y-6 pb-20 lg:pb-8">
      {/* Title */}
      <div>
        <h1 className="text-xl sm:text-2xl font-black text-slate-100">Application Settings</h1>
        <p className="text-xs text-slate-400">Configure trader profile, risk rules, currency, PIN lock & data backup</p>
      </div>

      <form onSubmit={handleSave} className="space-y-6 text-xs">
        {/* Profile Card */}
        <div className="p-6 rounded-3xl glow-card border-[#1E2534] space-y-4">
          <div className="flex items-center gap-2.5">
            <User className="w-5 h-5 text-amber-400" />
            <h2 className="text-base font-bold text-slate-100">Trader Profile & Display</h2>
          </div>

          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <div>
              <label className="text-slate-400 block mb-1 font-medium">Trader Name / Handle</label>
              <input
                type="text"
                required
                value={name}
                onChange={(e) => setName(e.target.value)}
                className="w-full p-3 rounded-xl bg-[#10141C] border border-[#1E2534] text-slate-100 font-bold focus:border-amber-500 focus:outline-none"
              />
            </div>

            <div>
              <label className="text-slate-400 block mb-1 font-medium">Account Currency Symbol</label>
              <div className="flex gap-2">
                {['$', '৳', '€', '£', '₹'].map((curr) => (
                  <button
                    key={curr}
                    type="button"
                    onClick={() => setCurrency(curr)}
                    className={`flex-1 py-2.5 rounded-xl font-black text-sm transition-all ${
                      currency === curr
                        ? 'bg-amber-500 text-black shadow-md shadow-amber-500/20'
                        : 'bg-[#10141C] text-slate-400 border border-[#1E2534] hover:text-white'
                    }`}
                  >
                    {curr}
                  </button>
                ))}
              </div>
            </div>
          </div>
        </div>

        {/* Risk & Target Defaults */}
        <div className="p-6 rounded-3xl glow-card border-[#1E2534] space-y-4">
          <div className="flex items-center gap-2.5">
            <Shield className="w-5 h-5 text-emerald-400" />
            <h2 className="text-base font-bold text-slate-100">Trading Rules & Limits</h2>
          </div>

          <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
            <div>
              <label className="text-slate-400 block mb-1 font-medium">Current Month Target ({currency}/day)</label>
              <input
                type="number"
                step="any"
                value={monthlyTarget}
                onChange={(e) => setMonthlyTarget(e.target.value)}
                className="w-full p-3 rounded-xl bg-[#10141C] border border-[#1E2534] text-slate-100 font-bold focus:border-amber-500 focus:outline-none"
              />
            </div>

            <div>
              <label className="text-slate-400 block mb-1 font-medium">Risk Limit Per Trade (%)</label>
              <input
                type="number"
                step="any"
                value={riskLimit}
                onChange={(e) => setRiskLimit(e.target.value)}
                className="w-full p-3 rounded-xl bg-[#10141C] border border-[#1E2534] text-slate-100 font-bold focus:border-amber-500 focus:outline-none"
              />
            </div>

            <div>
              <label className="text-slate-400 block mb-1 font-medium">Max Daily Loss Limit ({currency})</label>
              <input
                type="number"
                step="any"
                value={dailyLoss}
                onChange={(e) => setDailyLoss(e.target.value)}
                className="w-full p-3 rounded-xl bg-[#10141C] border border-[#1E2534] text-slate-100 font-bold focus:border-amber-500 focus:outline-none"
              />
            </div>
          </div>
        </div>

        {/* Language & UI Preferences */}
        <div className="p-6 rounded-3xl glow-card border-[#1E2534] space-y-4">
          <div className="flex items-center gap-2.5">
            <Globe className="w-5 h-5 text-indigo-400" />
            <h2 className="text-base font-bold text-slate-100">Language / ভাষা নির্বাচন</h2>
          </div>

          <div className="grid grid-cols-3 gap-3">
            {[
              { id: 'both', label: 'Bilingual (উভয়)' },
              { id: 'en', label: 'English Only' },
              { id: 'bn', label: 'বাংলা (Bengali)' },
            ].map((l) => (
              <button
                key={l.id}
                type="button"
                onClick={() => setLang(l.id as Language)}
                className={`p-3 rounded-xl font-bold transition-all text-center ${
                  language === l.id
                    ? 'bg-amber-500 text-black'
                    : 'bg-[#10141C] text-slate-400 border border-[#1E2534] hover:text-white'
                }`}
              >
                {l.label}
              </button>
            ))}
          </div>
        </div>

        {/* PIN Security & Privacy */}
        <div className="p-6 rounded-3xl glow-card border-[#1E2534] space-y-4">
          <div className="flex items-center gap-2.5">
            <Lock className="w-5 h-5 text-amber-400" />
            <h2 className="text-base font-bold text-slate-100">Journal Privacy & Security PIN</h2>
          </div>

          <div className="flex items-center justify-between p-4 rounded-2xl bg-[#10141C] border border-[#1E2534]">
            <div>
              <span className="font-bold text-slate-200 block text-xs">Enable PIN Lock Screen</span>
              <p className="text-slate-400 text-[11px]">Requires PIN code whenever the web application opens</p>
            </div>
            <input
              type="checkbox"
              checked={pinLockEnabled}
              onChange={(e) => setPinLockEnabled(e.target.checked)}
              className="w-5 h-5 rounded accent-amber-500 cursor-pointer"
            />
          </div>

          {pinLockEnabled && (
            <div>
              <label className="text-slate-400 block mb-1 font-medium">4 to 6 Digit Security PIN</label>
              <input
                type="password"
                maxLength={6}
                value={pinCode}
                onChange={(e) => setPinCode(e.target.value)}
                className="w-full max-w-xs p-3 rounded-xl bg-[#10141C] border border-[#1E2534] text-slate-100 font-bold tracking-widest text-base focus:border-amber-500 focus:outline-none"
              />
            </div>
          )}
        </div>

        <button
          type="submit"
          className="w-full py-4 rounded-2xl bg-amber-500 hover:bg-amber-400 text-black font-extrabold text-sm shadow-lg shadow-amber-500/20 active:scale-95 transition-all"
        >
          Save All Settings
        </button>
      </form>

      {/* Backup, Export & Reset Section */}
      <div className="p-6 rounded-3xl glow-card border-[#1E2534] space-y-4">
        <h2 className="text-base font-bold text-slate-100">Backup, Data Export & Restore</h2>
        <p className="text-xs text-slate-400">Export your journal database to a JSON file to keep 100% offline control of your data.</p>

        <div className="grid grid-cols-1 sm:grid-cols-2 gap-4 pt-2">
          <button
            onClick={handleDownloadBackup}
            className="flex items-center justify-center gap-2 p-3.5 rounded-2xl bg-[#10141C] hover:bg-[#1E2534] border border-[#1E2534] text-slate-200 font-bold text-xs transition-all"
          >
            <Download className="w-4 h-4 text-emerald-400" /> Export Backup (JSON)
          </button>

          <label className="flex items-center justify-center gap-2 p-3.5 rounded-2xl bg-[#10141C] hover:bg-[#1E2534] border border-[#1E2534] text-slate-200 font-bold text-xs cursor-pointer transition-all">
            <Upload className="w-4 h-4 text-indigo-400" /> Restore From Backup (JSON)
            <input type="file" accept=".json" onChange={handleImportFile} className="hidden" />
          </label>
        </div>

        <div className="pt-4 border-t border-[#1E2534]">
          <button
            onClick={() => setShowResetConfirm(true)}
            className="flex items-center gap-2 text-xs font-semibold text-rose-400 hover:text-rose-300"
          >
            <RotateCcw className="w-3.5 h-3.5" /> Reset all demo records and database to defaults
          </button>
        </div>
      </div>

      {/* Reset Confirmation Modal */}
      {showResetConfirm && (
        <div className="fixed inset-0 z-50 bg-black/80 backdrop-blur-sm flex items-center justify-center p-4">
          <div className="w-full max-w-sm bg-[#0B0E14] border border-[#1E2534] rounded-3xl p-6 shadow-2xl text-center space-y-4">
            <div className="w-12 h-12 mx-auto rounded-2xl bg-rose-500/15 border border-rose-500/30 flex items-center justify-center text-rose-400">
              <AlertTriangle className="w-6 h-6" />
            </div>
            <h3 className="text-base font-bold text-slate-100">Reset All Data?</h3>
            <p className="text-xs text-slate-400">
              This will wipe all custom trade records, habits, and reviews from local storage. Are you sure?
            </p>
            <div className="flex gap-3 pt-2">
              <button
                onClick={() => setShowResetConfirm(false)}
                className="flex-1 py-2.5 rounded-xl bg-[#161B26] hover:bg-[#1E2534] text-slate-300 font-semibold text-xs"
              >
                Cancel
              </button>
              <button
                onClick={() => {
                  resetAllData();
                  setShowResetConfirm(false);
                }}
                className="flex-1 py-2.5 rounded-xl bg-rose-500 hover:bg-rose-600 text-white font-bold text-xs"
              >
                Reset All
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};
