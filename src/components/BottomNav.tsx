import React from 'react';
import { LayoutDashboard, BookOpen, ShieldAlert, CheckSquare, MoonStar } from 'lucide-react';
import { useTraderLife } from '../context/TraderLifeContext';
import { Screen } from '../types';

export const BottomNav: React.FC = () => {
  const { currentScreen, setCurrentScreen } = useTraderLife();

  const items: { id: Screen; label: string; icon: React.ElementType }[] = [
    { id: 'dashboard', label: 'Home', icon: LayoutDashboard },
    { id: 'journal', label: 'Journal', icon: BookOpen },
    { id: 'risk-manager', label: 'Risk', icon: ShieldAlert },
    { id: 'routine', label: 'Routine', icon: CheckSquare },
    { id: 'salah', label: 'Salah', icon: MoonStar },
  ];

  return (
    <div className="fixed bottom-0 left-0 right-0 z-30 lg:hidden bg-[#0B0E14]/95 backdrop-blur-md border-t border-[#1E2534] px-2 py-1.5 flex items-center justify-around">
      {items.map((item) => {
        const Icon = item.icon;
        const active = currentScreen === item.id;
        return (
          <button
            key={item.id}
            onClick={() => setCurrentScreen(item.id)}
            className={`flex flex-col items-center gap-1 py-1 px-3 rounded-xl transition-all ${
              active ? 'text-amber-400 font-bold' : 'text-slate-400 hover:text-slate-200'
            }`}
          >
            <Icon className={`w-5 h-5 ${active ? 'text-amber-400' : 'text-slate-400'}`} />
            <span className="text-[10px]">{item.label}</span>
          </button>
        );
      })}
    </div>
  );
};
