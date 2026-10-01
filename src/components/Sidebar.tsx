import React from 'react';
import {
  LayoutDashboard,
  BookOpen,
  ShieldAlert,
  BrainCircuit,
  CheckSquare,
  MoonStar,
  Target,
  BarChart3,
  FileCheck2,
  Settings as SettingsIcon,
  X,
  CandlestickChart,
  ShieldCheck,
} from 'lucide-react';
import { useTraderLife } from '../context/TraderLifeContext';
import { Screen } from '../types';

interface SidebarProps {
  isOpen: boolean;
  onClose: () => void;
}

export const Sidebar: React.FC<SidebarProps> = ({ isOpen, onClose }) => {
  const { currentScreen, setCurrentScreen, settings, language } = useTraderLife();

  const navItems: { id: Screen; labelEn: string; labelBn: string; icon: React.ElementType }[] = [
    { id: 'dashboard', labelEn: 'Dashboard', labelBn: 'ড্যাশবোর্ড', icon: LayoutDashboard },
    { id: 'journal', labelEn: 'Trading Journal', labelBn: 'ট্রেডিং জার্নাল', icon: BookOpen },
    { id: 'risk-manager', labelEn: 'Risk Manager', labelBn: 'রিস্ক হিসাব', icon: ShieldAlert },
    { id: 'psychology', labelEn: 'Psychology', labelBn: 'সাইকোলজি ও আবেগ', icon: BrainCircuit },
    { id: 'routine', labelEn: 'Daily Routine', labelBn: 'দৈনিক রুটিন ও অভ্যাস', icon: CheckSquare },
    { id: 'salah', labelEn: 'Salah Tracker', labelBn: 'সালাত ট্র্যাকার', icon: MoonStar },
    { id: 'goals', labelEn: 'Goals Roadmap', labelBn: 'মাসিক রোডম্যাপ', icon: Target },
    { id: 'analytics', labelEn: 'Analytics', labelBn: 'অ্যানালিটিক্স', icon: BarChart3 },
    { id: 'daily-review', labelEn: 'Daily Review', labelBn: 'দৈনিক পর্যালোচনা', icon: FileCheck2 },
    { id: 'settings', labelEn: 'Settings', labelBn: 'সেটিংস', icon: SettingsIcon },
  ];

  const handleSelect = (screen: Screen) => {
    setCurrentScreen(screen);
    onClose();
  };

  return (
    <>
      {/* Mobile Backdrop */}
      {isOpen && (
        <div
          className="fixed inset-0 bg-black/70 backdrop-blur-sm z-40 lg:hidden transition-opacity"
          onClick={onClose}
        />
      )}

      {/* Sidebar Container */}
      <aside
        className={`fixed top-0 left-0 bottom-0 z-50 w-72 bg-[#0B0E14] border-r border-[#1E2534] flex flex-col transition-transform duration-300 ease-in-out lg:translate-x-0 ${
          isOpen ? 'translate-x-0' : '-translate-x-full'
        }`}
      >
        {/* Brand Header */}
        <div className="p-5 flex items-center justify-between border-b border-[#1E2534]">
          <div className="flex items-center gap-3">
            <div className="w-10 h-10 rounded-xl bg-gradient-to-br from-amber-500/20 to-amber-600/10 border border-amber-500/30 flex items-center justify-center text-amber-400 shadow-lg shadow-amber-500/10">
              <CandlestickChart className="w-5 h-5 text-amber-400" />
            </div>
            <div>
              <h1 className="font-extrabold text-sm tracking-wider text-slate-100 uppercase">
                My Trader Life
              </h1>
              <p className="text-xs text-amber-400 font-medium">Discipline & Journal</p>
            </div>
          </div>
          <button
            onClick={onClose}
            className="p-1.5 rounded-lg text-slate-400 hover:text-slate-100 hover:bg-[#1E2534] lg:hidden"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        {/* Trader Profile Card */}
        <div className="mx-4 my-3 p-3 rounded-xl bg-[#10141C] border border-[#1E2534] flex items-center justify-between">
          <div className="flex items-center gap-2.5">
            <div className="w-8 h-8 rounded-full bg-emerald-500/15 border border-emerald-500/30 flex items-center justify-center text-emerald-400 text-xs font-bold">
              {settings.traderName.charAt(0).toUpperCase()}
            </div>
            <div>
              <p className="text-xs font-semibold text-slate-200 truncate max-w-[130px]">
                {settings.traderName}
              </p>
              <p className="text-[10px] text-slate-400">Demo Trader</p>
            </div>
          </div>
          <span className="px-2 py-0.5 rounded text-[10px] font-semibold bg-amber-500/10 text-amber-400 border border-amber-500/20">
            DEMO
          </span>
        </div>

        {/* Nav Links */}
        <nav className="flex-1 px-3 py-2 space-y-1 overflow-y-auto">
          {navItems.map((item) => {
            const Icon = item.icon;
            const active = currentScreen === item.id;
            const label =
              language === 'bn'
                ? item.labelBn
                : language === 'en'
                ? item.labelEn
                : `${item.labelEn} (${item.labelBn})`;

            return (
              <button
                key={item.id}
                onClick={() => handleSelect(item.id)}
                className={`w-full flex items-center gap-3 px-3.5 py-2.5 rounded-xl text-xs font-medium transition-all ${
                  active
                    ? 'bg-amber-500/10 text-amber-400 border border-amber-500/30 font-semibold shadow-sm'
                    : 'text-slate-400 hover:text-slate-200 hover:bg-[#161B26]'
                }`}
              >
                <Icon
                  className={`w-4 h-4 shrink-0 ${
                    active ? 'text-amber-400' : 'text-slate-400'
                  }`}
                />
                <span className="truncate">{label}</span>
              </button>
            );
          })}
        </nav>

        {/* Disclaimer Footer */}
        <div className="p-4 border-t border-[#1E2534] bg-[#0B0E14]/80 text-[11px] text-slate-500 space-y-1.5">
          <div className="flex items-center gap-1.5 text-slate-400 font-medium">
            <ShieldCheck className="w-3.5 h-3.5 text-emerald-400 shrink-0" />
            <span>Discipline Tracker Only</span>
          </div>
          <p className="text-[10px] leading-tight text-slate-500">
            Not financial advice or live broker. Track rules, demo p/l and habits safely.
          </p>
        </div>
      </aside>
    </>
  );
};
