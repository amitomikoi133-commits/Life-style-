import React from 'react';
import { Menu, Calendar, Globe, Lock, Shield } from 'lucide-react';
import { useTraderLife } from '../context/TraderLifeContext';
import { Language, Screen } from '../types';

interface NavbarProps {
  onOpenSidebar: () => void;
}

export const Navbar: React.FC<NavbarProps> = ({ onOpenSidebar }) => {
  const { currentScreen, selectedDate, setSelectedDate, language, setLanguage, settings, lockApp, trades } = useTraderLife();

  const getScreenTitle = (screen: Screen): string => {
    switch (screen) {
      case 'dashboard':
        return language === 'bn' ? 'ড্যাশবোর্ড' : 'Dashboard';
      case 'journal':
        return language === 'bn' ? 'ট্রেডিং জার্নাল' : 'Trading Journal';
      case 'risk-manager':
        return language === 'bn' ? 'রিস্ক ম্যানেজার' : 'Risk Manager';
      case 'psychology':
        return language === 'bn' ? 'সাইকোলজি ও আবেগ' : 'Psychology & Emotions';
      case 'routine':
        return language === 'bn' ? 'দৈনিক রুটিন' : 'Daily Routine & Habits';
      case 'salah':
        return language === 'bn' ? 'সালাত ট্র্যাকার' : 'Salah Tracker';
      case 'goals':
        return language === 'bn' ? 'মাসিক রোডম্যাপ' : 'Goals Roadmap';
      case 'analytics':
        return language === 'bn' ? 'অ্যানালিটিক্স ও রিপোর্ট' : 'Analytics & Reports';
      case 'daily-review':
        return language === 'bn' ? 'দৈনিক পর্যালোচনা' : 'Daily Review';
      case 'settings':
        return language === 'bn' ? 'সেটিংস' : 'Settings';
      default:
        return 'My Trader Life';
    }
  };

  const todayTrades = trades.filter((t) => t.date === selectedDate);
  const todayPnl = todayTrades.reduce((acc, t) => acc + t.pnl, 0);

  return (
    <header className="sticky top-0 z-30 h-16 bg-[#0B0E14]/90 backdrop-blur-md border-b border-[#1E2534] px-4 lg:px-8 flex items-center justify-between">
      {/* Left section: Hamburger & Screen Title */}
      <div className="flex items-center gap-3.5">
        <button
          onClick={onOpenSidebar}
          className="p-2 rounded-xl text-slate-300 hover:text-white hover:bg-[#161B26] border border-[#1E2534] lg:hidden"
        >
          <Menu className="w-5 h-5" />
        </button>
        <div>
          <h2 className="text-base sm:text-lg font-bold text-slate-100">{getScreenTitle(currentScreen)}</h2>
          <p className="text-[11px] text-slate-400 hidden sm:block">Demo Trading & Life Discipline Journal</p>
        </div>
      </div>

      {/* Right Controls: Quick Date, Today's Mini PnL, Language & Lock */}
      <div className="flex items-center gap-2 sm:gap-3">
        {/* Date Selector */}
        <div className="flex items-center gap-1.5 px-2.5 py-1.5 rounded-xl bg-[#10141C] border border-[#1E2534] text-xs text-slate-300">
          <Calendar className="w-3.5 h-3.5 text-amber-400 shrink-0" />
          <input
            type="date"
            value={selectedDate}
            onChange={(e) => setSelectedDate(e.target.value)}
            className="bg-transparent text-slate-200 text-xs focus:outline-none cursor-pointer"
          />
        </div>

        {/* Mini PnL Badge */}
        <div
          className={`hidden md:flex items-center gap-1.5 px-3 py-1.5 rounded-xl border text-xs font-semibold ${
            todayPnl >= 0
              ? 'bg-emerald-500/10 border-emerald-500/30 text-emerald-400'
              : 'bg-rose-500/10 border-rose-500/30 text-rose-400'
          }`}
        >
          <span className="text-[10px] text-slate-400">TODAY:</span>
          <span>
            {todayPnl >= 0 ? '+' : ''}
            {settings.currency}
            {todayPnl.toFixed(2)}
          </span>
        </div>

        {/* Language Switcher */}
        <div className="flex items-center rounded-xl bg-[#10141C] border border-[#1E2534] p-0.5 text-[11px]">
          <button
            onClick={() => setLanguage('en')}
            className={`px-2 py-1 rounded-lg transition-all ${
              language === 'en' ? 'bg-amber-500 text-black font-bold' : 'text-slate-400 hover:text-slate-200'
            }`}
          >
            EN
          </button>
          <button
            onClick={() => setLanguage('bn')}
            className={`px-2 py-1 rounded-lg transition-all ${
              language === 'bn' ? 'bg-amber-500 text-black font-bold' : 'text-slate-400 hover:text-slate-200'
            }`}
          >
            বাং
          </button>
          <button
            onClick={() => setLanguage('both')}
            className={`px-2 py-1 rounded-lg transition-all ${
              language === 'both' ? 'bg-amber-500 text-black font-bold' : 'text-slate-400 hover:text-slate-200'
            }`}
          >
            Dual
          </button>
        </div>

        {/* PIN Lock Trigger */}
        {settings.pinLockEnabled && (
          <button
            onClick={lockApp}
            title="Lock Journal"
            className="p-2 rounded-xl bg-[#10141C] border border-[#1E2534] text-slate-400 hover:text-amber-400 transition-colors"
          >
            <Lock className="w-4 h-4" />
          </button>
        )}
      </div>
    </header>
  );
};
